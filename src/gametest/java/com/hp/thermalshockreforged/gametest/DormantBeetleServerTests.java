package com.hp.thermalshockreforged.gametest;

import com.hp.thermalshockreforged.entity.beetles.BeetleContent;
import com.hp.thermalshockreforged.entity.beetles.DormantBeetleCrasherEntity;
import com.mojang.authlib.GameProfile;
import java.util.UUID;
import java.util.function.Consumer;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import com.hp.thermalshockreforged.gametest.PlayerInteractionTests;
import net.minecraft.world.level.GameType;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.common.util.FakePlayer;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
public final class DormantBeetleServerTests {
    public static void register() {
        TsrGameTests.add("history_dormant_beetle_server_tests_survival_player_converts_dormant_once_without_login_events", DormantBeetleServerTests::survivalPlayerConvertsDormantOnceWithoutLoginEvents, 100, "supernova_server");
        TsrGameTests.add("history_dormant_beetle_server_tests_creative_player_does_not_trigger_dormant_conversion", DormantBeetleServerTests::creativePlayerDoesNotTriggerDormantConversion, 100, "supernova_server");
        TsrGameTests.add("history_dormant_beetle_server_tests_closer_creative_player_retains_the_original_nearest_player_priority", DormantBeetleServerTests::closerCreativePlayerRetainsTheOriginalNearestPlayerPriority, 100, "supernova_server");
        TsrGameTests.add("history_dormant_beetle_server_tests_observation_only_player_does_not_cross_the_inner_trigger_boundary", DormantBeetleServerTests::observationOnlyPlayerDoesNotCrossTheInnerTriggerBoundary, 100, "supernova_server");
        TsrGameTests.add("history_dormant_beetle_server_tests_rejected_conversion_retains_source_and_waits_twenty_ticks_before_retry", DormantBeetleServerTests::rejectedConversionRetainsSourceAndWaitsTwentyTicksBeforeRetry, 100, "supernova_server");
    }
    public static void survivalPlayerConvertsDormantOnceWithoutLoginEvents(GameTestHelper helper) {
        var source = dormant(helper);
        var player = player(helper, GameType.SURVIVAL, 6, 3, 8);
        try {
            source.baseTick();
            var spawned = crashers(helper);
            helper.assertTrue(source.isRemoved() && spawned.size() == 1 && spawned.getFirst().getDeltaMovement().distanceTo(new Vec3(0, .3, 0)) < 1.0E-8, "A real survival-mode server player must trigger one complete dormant conversion");
            helper.assertTrue(save(spawned.getFirst()).getIntOr("BeetleCooldown", 0) == 60, "The new Crasher must retain its original sixty-tick emergence cooldown");
            source.baseTick();
            helper.assertTrue(crashers(helper).size() == 1, "Repeating the removed dormant callback must not duplicate the replacement");
            helper.succeed();
        } finally { PlayerInteractionTests.release(helper, player); }
    }
    public static void creativePlayerDoesNotTriggerDormantConversion(GameTestHelper helper) {
        var source = dormant(helper);
        var player = player(helper, GameType.CREATIVE, 6, 3, 8);
        try {
            source.baseTick();
            helper.assertTrue(!source.isRemoved() && crashers(helper).isEmpty(), "Creative mode must not activate a dormant beetle");
            helper.succeed();
        } finally { PlayerInteractionTests.release(helper, player); }
    }
    public static void closerCreativePlayerRetainsTheOriginalNearestPlayerPriority(GameTestHelper helper) {
        var source = dormant(helper);
        var creative = player(helper, GameType.CREATIVE, 6, 3, 8);
        var survival = player(helper, GameType.SURVIVAL, 8, 3, 8);
        try {
            source.baseTick();
            helper.assertTrue(!source.isRemoved() && crashers(helper).isEmpty(), "The nearest creative player must retain the original priority over a farther survival player");
            PlayerInteractionTests.release(helper, creative);
            source.baseTick();
            helper.assertTrue(source.isRemoved() && crashers(helper).size() == 1, "Removing the closer non-survival player must allow the remaining valid target");
            helper.succeed();
        } finally { if (!creative.isRemoved()) PlayerInteractionTests.release(helper, creative); PlayerInteractionTests.release(helper, survival); }
    }
    public static void observationOnlyPlayerDoesNotCrossTheInnerTriggerBoundary(GameTestHelper helper) {
        var source = dormant(helper);
        var player = player(helper, GameType.SURVIVAL, 11, 3, 8);
        try {
            source.baseTick();
            helper.assertTrue(!source.isRemoved() && crashers(helper).isEmpty(), "A player inside the observation box but outside the trigger box must not convert the dormant beetle");
            helper.succeed();
        } finally { PlayerInteractionTests.release(helper, player); }
    }
    public static void rejectedConversionRetainsSourceAndWaitsTwentyTicksBeforeRetry(GameTestHelper helper) {
        var source = dormant(helper);
        var player = player(helper, GameType.SURVIVAL, 6, 3, 8);
        Consumer<EntityJoinLevelEvent> reject = event -> {
            if (event.getEntity().getType() == BeetleContent.CRASHER.get() && helper.getBounds().inflate(2).contains(event.getEntity().position())) event.setCanceled(true);
        };
        try {
            NeoForge.EVENT_BUS.addListener(EntityJoinLevelEvent.class, reject);
            try { source.baseTick(); }
            finally { NeoForge.EVENT_BUS.unregister(reject); }
            helper.assertTrue(!source.isRemoved() && crashers(helper).isEmpty() && save(source).getIntOr("BeetleRetryDelay", 0) == 20, "Rejected insertion must preserve the dormant source and begin its finite retry delay");
            for (int tick = 0; tick < 20; tick++) source.baseTick();
            helper.assertTrue(!source.isRemoved() && crashers(helper).isEmpty() && save(source).getIntOr("BeetleRetryDelay", 0) == 0, "All twenty waiting callbacks must avoid creating a replacement");
            source.baseTick();
            helper.assertTrue(source.isRemoved() && crashers(helper).size() == 1, "The first callback after the delay must retry and commit exactly once");
            helper.succeed();
        } finally { PlayerInteractionTests.release(helper, player); }
    }

    private static DormantBeetleCrasherEntity dormant(GameTestHelper helper) {
        var source = BeetleContent.DORMANT.get().create(helper.getLevel(), EntitySpawnReason.COMMAND);
        source.snapTo(helper.absolutePos(new BlockPos(5, 3, 8)), 0, 0);
        source.setNoAi(true);
        source.setNoGravity(true);
        if (!helper.getLevel().addFreshEntity(source)) throw new IllegalStateException("Dormant test insertion failed");
        return source;
    }

    private static net.minecraft.server.level.ServerPlayer player(GameTestHelper helper, GameType mode, double x, double y, double z) { return PlayerInteractionTests.player(helper, mode, x, y, z); }
    private static CompoundTag save(Entity entity) { var output = net.minecraft.world.level.storage.TagValueOutput.createWithContext(net.minecraft.util.ProblemReporter.DISCARDING, entity.registryAccess()); entity.saveWithoutId(output); return output.buildResult(); }
    private static java.util.List<com.hp.thermalshockreforged.entity.beetles.BeetleCrasherEntity> crashers(GameTestHelper helper) { return helper.getLevel().getEntitiesOfClass(com.hp.thermalshockreforged.entity.beetles.BeetleCrasherEntity.class, helper.getBounds()); }
    private DormantBeetleServerTests() { }
}
