package com.hp.thermalshockreforged.gametest;
import net.minecraft.world.entity.EntitySpawnReason;

import com.hp.thermalshockreforged.entity.ribbonblade.RibbonbladeActionEntity;
import com.hp.thermalshockreforged.entity.ribbonblade.RibbonbladeContent;
import com.hp.thermalshockreforged.entity.ribbonblade.RibbonbladeGalvaniteEntity;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.cow.Cow;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.block.Blocks;

public final class RibbonbladeSb002CorrectedRegressionTests {
    public static void beginAttackUsesIsolatedSeventeenEndTickDirectionalSweep(GameTestHelper h) {
        h.getLevel().setBlock(h.absolutePos(new BlockPos(5, 1, 5)), Blocks.STONE.defaultBlockState(), 3);
        h.getLevel().setBlock(h.absolutePos(new BlockPos(5, 1, 7)), Blocks.STONE.defaultBlockState(), 3);
        var blade = h.spawnWithNoFreeWill(RibbonbladeContent.RIBBONBLADE.get(), 5, 2, 3);
        var target = cow(h, h.absolutePos(new BlockPos(5, 2, 5)).getBottomCenter());
        target.setYRot(0);
        target.setXRot(0);
        target.setDeltaMovement(Vec3.ZERO);
        float before = target.getHealth();
        Vec3 expectedDestination = target.position().add(0, 0, 2);
        blade.beginAttack(target);
        h.assertTrue(blade.position().distanceToSqr(expectedDestination) < 1.0E-6, "The real beginAttack entry must commit the two-block target-relative destination");
        CompoundTag attackState = snapshot(blade);
        h.assertTrue(attackState.getIntOr("RibbonCooldown", 0) == 20, "The committed real attack must start the configured cooldown");
        UUID taskId = uuid(attackState, "RibbonActiveStrike") != null ? uuid(attackState, "RibbonActiveStrike") : null;
        h.assertTrue(taskId != null && h.getLevel().getEntity(taskId) instanceof RibbonbladeActionEntity, "The actual attack entry must own one live action task identified by its UUID");
        h.runAfterDelay(16, () -> {
            h.assertTrue(Math.abs(before - target.getHealth()) < 1.0E-3, "The strike must remain pending through the first sixteen END ticks");
            h.assertTrue(h.getLevel().getEntity(taskId) instanceof RibbonbladeActionEntity, "The same local strike task must remain live through END tick sixteen");
            h.runAfterDelay(1, () -> {
                h.assertTrue(Math.abs(before - target.getHealth() - 8) < 1.0E-3, "The real beginAttack path must hit its chosen target for exactly eight damage on END tick seventeen");
                h.assertTrue(h.getLevel().getEntity(taskId) == null, "The local finite task must be removed after END tick seventeen");
                Vec3 towardTarget = new Vec3(target.getX() - blade.getX(), 0, target.getZ() - blade.getZ()).normalize();
                h.assertTrue(new Vec3(blade.getLookAngle().x, 0, blade.getLookAngle().z).normalize().dot(towardTarget) > .99, "The resolved sweep must face the selected target direction");
                h.succeed();
            });
        });
    }

    private static Cow cow(GameTestHelper h, Vec3 position) {
        Cow cow = EntityType.COW.create(h.getLevel(), net.minecraft.world.entity.EntitySpawnReason.COMMAND);
        h.assertTrue(cow != null, "Cow fixture must be created");
        cow.setNoAi(true);
        cow.setNoGravity(true);
        cow.getAttribute(net.minecraft.world.entity.ai.attributes.Attributes.MAX_HEALTH).setBaseValue(100);
        cow.setHealth(100);
        cow.setPos(position);
        h.assertTrue(h.getLevel().addFreshEntity(cow), "Cow fixture must join the server world");
        return cow;
    }

    private RibbonbladeSb002CorrectedRegressionTests() { }


    private static CompoundTag snapshot(Entity entity) { var output=net.minecraft.world.level.storage.TagValueOutput.createWithContext(net.minecraft.util.ProblemReporter.DISCARDING,entity.registryAccess()); entity.saveWithoutId(output); return output.buildResult(); }
    private static void restore(GameTestHelper helper, Entity entity, CompoundTag tag) { entity.load(net.minecraft.world.level.storage.TagValueInput.create(net.minecraft.util.ProblemReporter.DISCARDING,helper.getLevel().registryAccess(),tag)); }
    private static java.util.UUID uuid(CompoundTag tag, String key) { net.minecraft.nbt.Tag value=tag.get(key); return value==null?null:net.minecraft.core.UUIDUtil.LENIENT_CODEC.parse(net.minecraft.nbt.NbtOps.INSTANCE,value).result().orElse(null); }
    public static void register() {
        com.hp.thermalshockreforged.gametest.TsrGameTests.add("history_ribbonblade_sb002_corrected_regression_tests_begin_attack_uses_isolated_seventeen_end_tick_directional_sweep", RibbonbladeSb002CorrectedRegressionTests::beginAttackUsesIsolatedSeventeenEndTickDirectionalSweep, 150, "supernova_server");
    }
}
