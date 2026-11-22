package com.hp.thermalshockreforged.gametest;

import com.mojang.authlib.GameProfile;
import com.hp.thermalshockreforged.content.decor.geo.GeoDecorBlockEntity;
import com.hp.thermalshockreforged.content.decor.geo.GeoDecorContent;
import com.hp.thermalshockreforged.content.decor.geo.GeoDecorKind;
import com.hp.thermalshockreforged.content.decor.geo.GrandBlasphemerBlock;
import com.hp.thermalshockreforged.content.decor.geo.GrandBlasphemerBlockEntity;
import com.hp.thermalshockreforged.content.machines.BlasphemerProcessEntity;
import com.hp.thermalshockreforged.content.machines.MachineContent;
import java.util.UUID;
import java.util.function.Consumer;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.level.storage.TagValueInput;
import net.minecraft.world.level.storage.TagValueOutput;
import net.minecraft.util.ProblemReporter;
public final class BlasphemerServerBehaviorTests {
    public static void register() {
        TsrGameTests.add("history_blasphemer_server_behavior_tests_activation_commits_once_awards_advancement_and_keeps_empty_machine", BlasphemerServerBehaviorTests::activationCommitsOnceAwardsAdvancementAndKeepsEmptyMachine, 20, "supernova_server");
        TsrGameTests.add("history_blasphemer_server_behavior_tests_activation_persistence_survives_block_entity_nbt_round_trip", BlasphemerServerBehaviorTests::activationPersistenceSurvivesBlockEntityNbtRoundTrip, 20, "supernova_server");
        TsrGameTests.add("history_blasphemer_server_behavior_tests_process_uses_the_original_forty_one_hundred_four_and_one_hundred_thirty_four_boundaries", BlasphemerServerBehaviorTests::processUsesTheOriginalFortyOneHundredFourAndOneHundredThirtyFourBoundaries, 190, "supernova_server");
        TsrGameTests.add("history_blasphemer_server_behavior_tests_saved_process_keeps_remaining_elapsed_and_does_not_restart", BlasphemerServerBehaviorTests::savedProcessKeepsRemainingElapsedAndDoesNotRestart, 200, "supernova_server");
        TsrGameTests.add("history_blasphemer_server_behavior_tests_removed_machine_keeps_committed_tail_but_replacement_is_protected", BlasphemerServerBehaviorTests::removedMachineKeepsCommittedTailButReplacementIsProtected, 190, "supernova_server");
        TsrGameTests.add("history_blasphemer_server_behavior_tests_mismatched_token_cannot_finish_machine", BlasphemerServerBehaviorTests::mismatchedTokenCannotFinishMachine, 10, "supernova_server");
        TsrGameTests.add("history_blasphemer_server_behavior_tests_cancelled_process_join_does_not_burn_activation", BlasphemerServerBehaviorTests::cancelledProcessJoinDoesNotBurnActivation, 20, "supernova_server");
        TsrGameTests.add("history_blasphemer_server_behavior_tests_cancelled_sample_join_is_recorded_without_asecond_hidden_reward", BlasphemerServerBehaviorTests::cancelledSampleJoinIsRecordedWithoutASecondHiddenReward, 190, "supernova_server");
    }
    public static void activationCommitsOnceAwardsAdvancementAndKeepsEmptyMachine(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        BlockPos pos = place(helper);
        GrandBlasphemerBlockEntity machine = machine(level, pos);
        ServerPlayer player = offlinePlayer(level);
        machine.activate(player);
        CompoundTag saved = machine.saveWithoutMetadata(level.registryAccess());
        helper.assertTrue(machine.getPersistentData().getBooleanOr("UsedHole", false) && machine.getPersistentData().contains("ProcessToken"), "Activation must commit the one-time claim and process token in memory");
        helper.assertTrue(level.getBlockState(pos).getValue(GrandBlasphemerBlock.PHASE) == 1, "Activation must enter procedure phase one");
        helper.assertTrue(taskCount(level, pos) == 1, "A successful activation must own exactly one process task");
        UUID token = machine.getPersistentData().read("ProcessToken", net.minecraft.core.UUIDUtil.CODEC).orElseThrow();
        var advancement = level.getServer().getAdvancements().get(MachineContent.id("blasphemy_2"));
        helper.assertTrue(advancement != null && player.getAdvancements().getOrStartProgress(advancement).isDone(), "Activation must award the complete blasphemy_2 advancement");
        machine.activate(player);
        helper.assertTrue(taskCount(level, pos) == 1 && machine.getPersistentData().read("ProcessToken", net.minecraft.core.UUIDUtil.CODEC).orElseThrow().equals(token), "Repeated activation must not create a second task or replace the token");
        helper.succeed();
    }
    public static void activationPersistenceSurvivesBlockEntityNbtRoundTrip(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        BlockPos pos = place(helper);
        GrandBlasphemerBlockEntity machine = machine(level, pos);
        machine.activate(offlinePlayer(level));
        CompoundTag saved = machine.saveWithoutMetadata(level.registryAccess());
        GrandBlasphemerBlockEntity restored = new GrandBlasphemerBlockEntity(pos, level.getBlockState(pos));
        restored.loadWithComponents(input(helper, saved));
        helper.assertTrue(restored.getPersistentData().getBooleanOr("UsedHole", false) && restored.getPersistentData().contains("ProcessToken"), "The one-time claim and process token must survive a block entity NBT round trip");
        helper.succeed();
    }
    public static void processUsesTheOriginalFortyOneHundredFourAndOneHundredThirtyFourBoundaries(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        BlockPos pos = place(helper);
        GrandBlasphemerBlockEntity machine = machine(level, pos);
        machine.activate(offlinePlayer(level));
        helper.runAfterDelay(39, () -> helper.assertTrue(level.getEntitiesOfClass(ItemEntity.class, outputBox(pos), e -> isSample(e)).isEmpty() && level.getBlockState(pos).getValue(GrandBlasphemerBlock.PHASE) == 1, "The sample must not exist before the 134 END stage and the machine must remain in phase one"));
        helper.runAfterDelay(105, () -> helper.assertTrue(level.getEntitiesOfClass(ItemEntity.class, outputBox(pos), e -> isSample(e)).isEmpty() && level.getBlockState(pos).getValue(GrandBlasphemerBlock.PHASE) == 1, "The 104 stage is audio only and must not create a sample"));
        helper.runAfterDelay(136, () -> helper.assertTrue(level.getEntitiesOfClass(ItemEntity.class, outputBox(pos), e -> isSample(e)).size() == 1, "The 134 stage must create exactly one infected core sample"));
        helper.runAfterDelay(185, () -> helper.assertTrue(level.getBlockState(pos).getValue(GrandBlasphemerBlock.PHASE) == 2 && taskCount(level, pos) == 0, "The 180 stage must finish phase two and consume the process task"));
        helper.runAfterDelay(188, helper::succeed);
    }
    public static void savedProcessKeepsRemainingElapsedAndDoesNotRestart(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        BlockPos pos = place(helper);
        GrandBlasphemerBlockEntity machine = machine(level, pos);
        machine.activate(offlinePlayer(level));
        helper.runAfterDelay(12, () -> {
            var old = level.getEntitiesOfClass(BlasphemerProcessEntity.class, outputBox(pos)).getFirst();
            CompoundTag saved = save(old);
            int elapsed = saved.getIntOr("Elapsed", 0);
            helper.assertTrue(elapsed >= 10 && elapsed <= 14 && saved.getBooleanOr("Armed", false), "A saved process must retain its elapsed END count");
            old.discard();
            saved.store("UUID", net.minecraft.core.UUIDUtil.CODEC, UUID.randomUUID());
            var restored = new BlasphemerProcessEntity(MachineContent.BLASPHEMER_PROCESS.get(), level);
            restored.load(input(helper, saved));
            restored.snapTo(pos.getX() + .5, pos.getY() + .5, pos.getZ() + .5);
            helper.assertTrue(level.addFreshEntity(restored), "The NBT-restored process must be accepted");
            helper.assertTrue(save(restored).getIntOr("Elapsed", 0) == elapsed, "Loading must not reset the elapsed process count");
        });
        helper.runAfterDelay(130, () -> helper.assertTrue(level.getEntitiesOfClass(ItemEntity.class, outputBox(pos), e -> isSample(e)).isEmpty(), "The reloaded process must not emit the sample before its original deadline"));
        helper.runAfterDelay(140, () -> helper.assertTrue(level.getEntitiesOfClass(ItemEntity.class, outputBox(pos), e -> isSample(e)).size() == 1, "The reloaded process must emit the sample at the original remaining deadline"));
        helper.runAfterDelay(188, () -> helper.assertTrue(level.getBlockState(pos).getValue(GrandBlasphemerBlock.PHASE) == 2 && taskCount(level, pos) == 0, "The reloaded process must finish exactly once"));
        helper.runAfterDelay(192, helper::succeed);
    }
    public static void removedMachineKeepsCommittedTailButReplacementIsProtected(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        BlockPos pos = place(helper);
        GrandBlasphemerBlockEntity old = machine(level, pos);
        old.activate(offlinePlayer(level));
        helper.runAfterDelay(20, () -> {
            level.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
            level.setBlock(pos, GeoDecorContent.block(GeoDecorKind.GRAND_BLASPHEMER).defaultBlockState(), 3);
            helper.assertTrue(level.getBlockState(pos).getValue(GrandBlasphemerBlock.PHASE) == 0, "A replacement machine must start unused in phase zero");
        });
        helper.runAfterDelay(138, () -> helper.assertTrue(level.getEntitiesOfClass(ItemEntity.class, outputBox(pos), e -> isSample(e)).size() == 1, "The old process must retain its committed sample tail after machine replacement"));
        helper.runAfterDelay(185, () -> helper.assertTrue(level.getBlockState(pos).getValue(GrandBlasphemerBlock.PHASE) == 0 && !machine(level, pos).saveWithoutMetadata(level.registryAccess()).getBooleanOr("UsedHole", false), "The old token must not finish or consume the replacement machine"));
        helper.runAfterDelay(188, helper::succeed);
    }
    public static void mismatchedTokenCannotFinishMachine(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        BlockPos pos = place(helper);
        GrandBlasphemerBlockEntity machine = machine(level, pos);
        machine.activate(offlinePlayer(level));
        machine.finish(UUID.randomUUID());
        helper.assertTrue(level.getBlockState(pos).getValue(GrandBlasphemerBlock.PHASE) == 1, "A mismatched process token must not change the machine phase");
        helper.succeed();
    }
    public static void cancelledProcessJoinDoesNotBurnActivation(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        BlockPos pos = place(helper);
        GrandBlasphemerBlockEntity machine = machine(level, pos);
        Consumer<EntityJoinLevelEvent> veto = event -> {
            if (event.getLevel() == level && event.getEntity() instanceof BlasphemerProcessEntity) event.setCanceled(true);
        };
        NeoForge.EVENT_BUS.addListener(veto);
        try {
            machine.activate(offlinePlayer(level));
            CompoundTag saved = machine.saveWithoutMetadata(level.registryAccess());
            helper.assertTrue(!machine.getPersistentData().getBooleanOr("UsedHole", false) && !machine.getPersistentData().contains("ProcessToken") && level.getBlockState(pos).getValue(GrandBlasphemerBlock.PHASE) == 0 && taskCount(level, pos) == 0, "A cancelled process join must leave the machine reusable");
        } finally {
            NeoForge.EVENT_BUS.unregister(veto);
        }
        machine.activate(offlinePlayer(level));
        helper.assertTrue(machine.getPersistentData().getBooleanOr("UsedHole", false) && taskCount(level, pos) == 1, "After the veto is removed the same machine must activate normally");
        helper.succeed();
    }
    public static void cancelledSampleJoinIsRecordedWithoutASecondHiddenReward(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        BlockPos pos = place(helper);
        GrandBlasphemerBlockEntity machine = machine(level, pos);
        machine.activate(offlinePlayer(level));
        Consumer<EntityJoinLevelEvent> veto = event -> {
            if (event.getLevel() == level && event.getEntity() instanceof ItemEntity item && isSample(item) && item.blockPosition().distSqr(pos) < 4) event.setCanceled(true);
        };
        NeoForge.EVENT_BUS.addListener(veto);
        helper.runAfterDelay(136, () -> helper.assertTrue(level.getEntitiesOfClass(ItemEntity.class, outputBox(pos), BlasphemerServerBehaviorTests::isSample).isEmpty(), "A cancelled sample join must produce no hidden duplicate item"));
        helper.runAfterDelay(185, () -> {
            helper.assertTrue(level.getBlockState(pos).getValue(GrandBlasphemerBlock.PHASE) == 2 && taskCount(level, pos) == 0, "The current process still completes after the sample join is cancelled");
            NeoForge.EVENT_BUS.unregister(veto);
        });
        helper.runAfterDelay(188, helper::succeed);
    }

    private static net.minecraft.world.level.storage.ValueInput input(GameTestHelper helper, CompoundTag tag) { return TagValueInput.create(ProblemReporter.DISCARDING, helper.getLevel().registryAccess(), tag); }
    private static CompoundTag save(net.minecraft.world.entity.Entity entity) { var output = TagValueOutput.createWithContext(ProblemReporter.DISCARDING, entity.registryAccess()); entity.saveWithoutId(output); return output.buildResult(); }
    private static BlockPos place(GameTestHelper helper) {
        BlockPos pos = helper.absolutePos(new BlockPos(5, 2, 8));
        helper.getLevel().setBlock(pos, GeoDecorContent.block(GeoDecorKind.GRAND_BLASPHEMER).defaultBlockState(), 3);
        return pos;
    }

    private static GrandBlasphemerBlockEntity machine(ServerLevel level, BlockPos pos) {
        return (GrandBlasphemerBlockEntity) level.getBlockEntity(pos);
    }

    private static ServerPlayer offlinePlayer(ServerLevel level) {
        return new ServerPlayer(level.getServer(), level, new GameProfile(UUID.randomUUID(), "blasphemer-test"), net.minecraft.server.level.ClientInformation.createDefault());
    }

    private static int taskCount(ServerLevel level, BlockPos pos) {
        return level.getEntitiesOfClass(BlasphemerProcessEntity.class, outputBox(pos)).size();
    }

    private static AABB outputBox(BlockPos pos) {
        return new AABB(pos).inflate(2);
    }

    private static boolean isSample(ItemEntity entity) {
        return entity.getItem().is(com.hp.thermalshockreforged.content.MaterialContent.items().get("infected_core_sample").get());
    }

    private BlasphemerServerBehaviorTests() { }
}
