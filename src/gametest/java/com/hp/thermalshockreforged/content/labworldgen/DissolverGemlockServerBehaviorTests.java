package com.hp.thermalshockreforged.content.labworldgen;

import com.hp.thermalshockreforged.content.MaterialContent;
import com.hp.thermalshockreforged.gametest.PlayerInteractionTests;
import com.hp.thermalshockreforged.gametest.TsrGameTests;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
public final class DissolverGemlockServerBehaviorTests {
    public static void register() {
        TsrGameTests.add("history_dissolver_gemlock_server_behavior_tests_phobotite_runs_three_bounded_spherical_dissolve_phases", DissolverGemlockServerBehaviorTests::phobotiteRunsThreeBoundedSphericalDissolvePhases, 55, "supernova_server");
    }

    public static void phobotiteRunsThreeBoundedSphericalDissolvePhases(GameTestHelper helper) {
        var level = helper.getLevel();
        BlockPos anchor = helper.absolutePos(new BlockPos(5, 5, 5));
        var gemlock = LabWorldgenContent.DISSOLVER_GEMLOCK.get();
        var dissolving = MaterialContent.blocks().get("dissolving_impervium").get();
        BlockPos radiusOne = anchor.east();
        BlockPos radiusTwo = anchor.above(2);
        BlockPos radiusFour = anchor.north(4);
        BlockPos radiusFive = anchor.north(5);
        helper.assertTrue(level.setBlock(anchor, gemlock.defaultBlockState(), 3), "Gemlock must be placed in the test world");
        helper.assertTrue(level.setBlock(radiusOne, dissolving.defaultBlockState(), 3), "Radius-one target must be placed");
        helper.assertTrue(level.setBlock(radiusTwo, dissolving.defaultBlockState(), 3), "Radius-two target must be placed");
        helper.assertTrue(level.setBlock(radiusFour, dissolving.defaultBlockState(), 3), "Radius-four target must be placed");
        helper.assertTrue(level.setBlock(radiusFive, dissolving.defaultBlockState(), 3), "Outside-radius target must be placed");

        var player = PlayerInteractionTests.player(helper, GameType.SURVIVAL, 5.5, 5, 7.5);
        BlockHitResult hit = new BlockHitResult(Vec3.atCenterOf(anchor), Direction.UP, anchor, false);
        try {
            player.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(Items.STONE));
            gemlock.useItemOn(player.getMainHandItem(), level.getBlockState(anchor), level, anchor, player, InteractionHand.MAIN_HAND, hit);
            helper.assertTrue(level.getBlockState(anchor).getValue(DissolverGemlockBlock.BLOCKSTATE) == 0, "A wrong item must not activate the gemlock");
            helper.assertTrue(player.getMainHandItem().getCount() == 1, "A wrong item must remain unconsumed");
            helper.assertTrue(taskCount(helper, anchor) == 0, "A wrong item must not schedule a dissolver task");

            player.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(MaterialContent.items().get("phobotite").get(), 2));
            gemlock.useItemOn(player.getMainHandItem(), level.getBlockState(anchor), level, anchor, player, InteractionHand.MAIN_HAND, hit);
            helper.assertTrue(level.getBlockState(anchor).getValue(DissolverGemlockBlock.BLOCKSTATE) == 1, "A valid Phobotite interaction must activate the gemlock");
            helper.assertTrue(player.getMainHandItem().getCount() == 1, "Activation must consume exactly one Phobotite");
            helper.assertTrue(taskCount(helper, anchor) == 1, "Activation must add exactly one dissolver task");
            gemlock.useItemOn(player.getMainHandItem(), level.getBlockState(anchor), level, anchor, player, InteractionHand.MAIN_HAND, hit);
            helper.assertTrue(player.getMainHandItem().getCount() == 1 && taskCount(helper, anchor) == 1, "Repeated interaction while active must not consume or schedule again");
            helper.assertTrue(isDissolving(level, radiusOne) && isDissolving(level, radiusTwo) && isDissolving(level, radiusFour) && isDissolving(level, radiusFive), "No sphere may dissolve blocks immediately on activation");
        } finally {
            PlayerInteractionTests.release(helper, player);
        }

        helper.runAfterDelay(19, () -> {
            helper.assertTrue(isDissolving(level, radiusOne) && isDissolving(level, radiusTwo), "The first phase must not run before its 20-tick delay");
            helper.assertTrue(isDissolving(level, radiusFour) && isDissolving(level, radiusFive), "Later-phase targets must remain before the first delay expires");
        });
        helper.runAfterDelay(22, () -> {
            helper.assertTrue(level.getBlockState(radiusOne).isAir(), "The first phase must dissolve its radius-one sphere");
            helper.assertTrue(isDissolving(level, radiusTwo) && isDissolving(level, radiusFour) && isDissolving(level, radiusFive), "The first phase must not exceed radius one");
        });
        helper.runAfterDelay(24, () -> {
            helper.assertTrue(isDissolving(level, radiusTwo) && isDissolving(level, radiusFour), "The second phase must wait for its additional five ticks");
        });
        helper.runAfterDelay(27, () -> {
            helper.assertTrue(level.getBlockState(radiusTwo).isAir(), "The second phase must dissolve its radius-two sphere");
            helper.assertTrue(isDissolving(level, radiusFour) && isDissolving(level, radiusFive), "The second phase must not exceed radius two");
        });
        helper.runAfterDelay(29, () -> helper.assertTrue(isDissolving(level, radiusFour), "The third phase must wait for its additional five ticks"));
        helper.runAfterDelay(32, () -> {
            helper.assertTrue(level.getBlockState(radiusFour).isAir(), "The third phase must dissolve its radius-four sphere");
            helper.assertTrue(isDissolving(level, radiusFive), "The third phase must preserve blocks outside radius four");
            helper.assertTrue(taskCount(helper, anchor) == 0, "The task must finish after the third phase when no LabMachinist is nearby");
            helper.assertTrue(level.getBlockState(anchor).getValue(DissolverGemlockBlock.BLOCKSTATE) == 1, "The completed gemlock must retain its activated state");
            helper.succeed();
        });
    }

    private static boolean isDissolving(net.minecraft.server.level.ServerLevel level, BlockPos pos) {
        return level.getBlockState(pos).is(MaterialContent.blocks().get("dissolving_impervium").get());
    }

    private static int taskCount(GameTestHelper helper, BlockPos anchor) {
        return helper.getLevel().getEntitiesOfClass(DissolverCycleEntity.class, new AABB(anchor).inflate(2)).size();
    }

    private DissolverGemlockServerBehaviorTests() { }
}
