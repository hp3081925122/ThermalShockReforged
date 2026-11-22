package com.hp.thermalshockreforged.entity.glastrekker;

import com.hp.thermalshockreforged.content.MaterialContent;
import com.hp.thermalshockreforged.content.labworldgen.DissolverCycleEntity;
import com.hp.thermalshockreforged.content.labworldgen.DissolverGemlockBlock;
import com.hp.thermalshockreforged.content.labworldgen.LabWorldgenContent;
import com.hp.thermalshockreforged.entity.labmachinist.LabContent;
import com.hp.thermalshockreforged.entity.labmachinist.LabMachinistEntity;
import com.hp.thermalshockreforged.gametest.TsrGameTests;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.UUIDUtil;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.storage.TagValueInput;
import net.minecraft.world.level.storage.TagValueOutput;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.PlayLevelSoundEvent;

public final class GlastrekkerDissolverWorldNpcServerTests {
    private static final Map<Long, SoundCapture> SOUND_TRACE = new ConcurrentHashMap<>();

    public static void register() {
        TsrGameTests.add("history_glastrekker_dissolver_world_npc_server_tests_survival_bone_meal_failure_consumes_one_and_preserves_the_egg", GlastrekkerDissolverWorldNpcServerTests::survivalBoneMealFailureConsumesOneAndPreservesTheEgg, 25, "migration_empty");
        TsrGameTests.add("history_glastrekker_dissolver_world_npc_server_tests_one_nearby_machinist_schedules_two_observed_voice_sound_calls_at_elapsed33_and50", GlastrekkerDissolverWorldNpcServerTests::oneNearbyMachinistSchedulesTwoObservedVoiceSoundCallsAtElapsed33And50, 60, "migration_empty");
        TsrGameTests.add("history_glastrekker_dissolver_world_npc_server_tests_two_nearby_machinists_produce_two_actual_sound_calls_at_each_voice_stage", GlastrekkerDissolverWorldNpcServerTests::twoNearbyMachinistsProduceTwoActualSoundCallsAtEachVoiceStage, 60, "migration_empty");
        TsrGameTests.add("history_glastrekker_dissolver_world_npc_server_tests_notice_search_includes_intersecting_machinist_and_excludes_beyond_expanded_aabb", GlastrekkerDissolverWorldNpcServerTests::noticeSearchIncludesIntersectingMachinistAndExcludesBeyondExpandedAabb, 40, "migration_empty");
        TsrGameTests.add("history_glastrekker_dissolver_world_npc_server_tests_voice_schedule_survives_save_load_between_both_actual_sound_stages", GlastrekkerDissolverWorldNpcServerTests::voiceScheduleSurvivesSaveLoadBetweenBothActualSoundStages, 65, "supernova_server");
    }

    static {
        NeoForge.EVENT_BUS.addListener((PlayLevelSoundEvent event) -> {
            if (!(event instanceof PlayLevelSoundEvent.AtPosition positioned)) return;
            var key = BuiltInRegistries.SOUND_EVENT.getKey(event.getSound().value());
            if (key == null) return;
            String path = key.getPath();
            for (var entry : SOUND_TRACE.entrySet()) {
                SoundCapture capture = entry.getValue();
                if (event.getLevel() == capture.level() && Vec3.atLowerCornerOf(BlockPos.of(entry.getKey())).distanceToSqr(positioned.getPosition()) <= 16) capture.sounds().add(path);
            }
        });
    }

    public static void survivalBoneMealFailureConsumesOneAndPreservesTheEgg(GameTestHelper helper) {
        var level = helper.getLevel();
        BlockPos eggPos = pos(helper);
        level.setBlock(eggPos, GlastrekkerContent.EGG.get().defaultBlockState(), 3);
        PlayerFixture fixture = player(helper, GameType.SURVIVAL);
        fixture.player.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(Items.BONE_MEAL, 3));
        seedForHatch(level, false);
        startCapture(level, eggPos);
        var result = level.getBlockState(eggPos).useItemOn(fixture.player.getMainHandItem(), level, fixture.player, InteractionHand.MAIN_HAND, hit(eggPos));
        helper.assertTrue(result == net.minecraft.world.InteractionResult.SUCCESS && fixture.player.getMainHandItem().getCount() == 2, "A survival bone-meal attempt must consume exactly one item");
        helper.assertTrue(level.getBlockState(eggPos).is(GlastrekkerContent.EGG.get()), "A non-hatching probability result must preserve the egg");
        helper.assertTrue(sounds(eggPos).equals(List.of("block.sniffer_egg.crack")), "The real server sound-play entry must observe one egg interaction sound; observed=" + sounds(eggPos));
        stopCapture(eggPos);
        helper.succeed();
    }

    public static void oneNearbyMachinistSchedulesTwoObservedVoiceSoundCallsAtElapsed33And50(GameTestHelper helper) {
        var level = helper.getLevel();
        BlockPos anchor = pos(helper);
        LabMachinistEntity npc = placeNpc(level, anchor.offset(2, 0, 0));
        helper.assertTrue(noticeCandidates(level, anchor) == 1, "The actual LabMachinist fixture must be inside the production notice AABB before activation");
        activate(helper, anchor);
        DissolverCycleEntity initialCycle = onlyCycle(helper, anchor);
        startCapture(level, anchor);
        DissolverCycleEntity[] cycleRef = new DissolverCycleEntity[]{initialCycle};
        helper.runAfterDelay(30, () -> {
            helper.assertTrue(npc.isAlive() && !npc.isRemoved() && noticeCandidates(level, anchor) == 1, "The previously admitted LabMachinist fixture must remain alive and inside the production notice AABB through the third phase");
            CompoundTag tag = save(helper, cycleRef[0]);
            helper.assertTrue(tag.getIntOr("DissolverPhase", 0) == 3 && tag.getIntOr("DissolverVoices", 0) == 1 && sounds(anchor).stream().noneMatch(s -> s.startsWith("qo_em")), "Third dissolve must discover one actual nearby NPC before either voice deadline; phase=" + tag.getIntOr("DissolverPhase", 0) + ", voices=" + tag.getIntOr("DissolverVoices", 0) + ", elapsed=" + tag.getIntOr("DissolverElapsed", 0) + ", liveCandidates=" + noticeCandidates(level, anchor) + ", npcPos=" + npc.position() + ", npcAlive=" + npc.isAlive());
        });
        helper.runAfterDelay(34, () -> {
            CompoundTag tag = save(helper, cycleRef[0]);
            helper.assertTrue(tag.getIntOr("DissolverElapsed", 0) >= 33 && tag.getIntOr("DissolverPhase", 0) >= 4 && sounds(anchor).stream().filter("qo_em1_speak"::equals).count() == 1, "The real ServerLevel sound-play entry must observe exactly one first NPC voice after elapsed 33");
        });
        helper.runAfterDelay(51, () -> {
            CompoundTag tag = save(helper, cycleRef[0]);
            helper.assertTrue(tag.getIntOr("DissolverElapsed", 0) == 50 && tag.getIntOr("DissolverPhase", 0) == 5 && sounds(anchor).stream().filter("qo_em2_speak"::equals).count() == 1, "The second NPC voice must reach the real sound-play entry exactly once by elapsed 50");
            helper.assertTrue(sounds(anchor).stream().filter("hailfire"::equals).count() == 3, "Each of the three dissolve phases must enter its real block-sound playback path once");
            stopCapture(anchor);
            helper.succeed();
        });
    }

    public static void twoNearbyMachinistsProduceTwoActualSoundCallsAtEachVoiceStage(GameTestHelper helper) {
        var level = helper.getLevel();
        BlockPos anchor = pos(helper);
        placeNpc(level, anchor.offset(2, 0, 0));
        placeNpc(level, anchor.offset(-2, 0, 0));
        helper.assertTrue(noticeCandidates(level, anchor) == 2, "Both actual LabMachinist fixtures must lie inside the production notice AABB");
        activate(helper, anchor);
        DissolverCycleEntity cycle = onlyCycle(helper, anchor);
        startCapture(level, anchor);
        helper.runAfterDelay(30, () -> {
            helper.assertTrue(noticeCandidates(level, anchor) == 2, "Both admitted LabMachinist fixtures must remain inside the production notice AABB through the third phase");
            helper.assertTrue(save(helper, cycle).getIntOr("DissolverVoices", 0) == 2, "The third phase must count both real LabMachinist entities in the notice search area");
        });
        helper.runAfterDelay(34, () -> helper.assertTrue(sounds(anchor).stream().filter("qo_em1_speak"::equals).count() == 2, "The first voice stage must submit one actual sound event for each discovered NPC"));
        helper.runAfterDelay(51, () -> {
            helper.assertTrue(sounds(anchor).stream().filter("qo_em2_speak"::equals).count() == 2, "The second voice stage must submit one actual sound event for each discovered NPC");
            stopCapture(anchor);
            helper.succeed();
        });
    }

    public static void noticeSearchIncludesIntersectingMachinistAndExcludesBeyondExpandedAabb(GameTestHelper helper) {
        var level = helper.getLevel();
        BlockPos anchor = pos(helper);
        placeNpc(level, anchor.offset(4, 0, 0));
        placeNpc(level, anchor.offset(7, 0, 0));
        helper.assertTrue(noticeCandidates(level, anchor) == 1, "The actual NPC fixtures must distinguish intersection with the 4.5-expanded AABB from the clearly external entity");
        activate(helper, anchor);
        DissolverCycleEntity cycle = onlyCycle(helper, anchor);
        helper.runAfterDelay(30, () -> {
            helper.assertTrue(noticeCandidates(level, anchor) == 1, "The admitted inside/outside LabMachinist fixture counts must remain stable through the third phase");
            CompoundTag tag = save(helper, cycle);
            helper.assertTrue(tag.getIntOr("DissolverVoices", 0) == 1, "A machinist intersecting the 4.5-expanded search AABB must be counted while the clearly external machinist is excluded");
            helper.succeed();
        });
    }

    public static void voiceScheduleSurvivesSaveLoadBetweenBothActualSoundStages(GameTestHelper helper) {
        var level = helper.getLevel();
        BlockPos anchor = pos(helper);
        LabMachinistEntity npc = placeNpc(level, anchor.offset(2, 0, 0));
        helper.assertTrue(noticeCandidates(level, anchor) == 1, "The actual LabMachinist fixture must be eligible before testing serialized voice timing");
        activate(helper, anchor);
        DissolverCycleEntity initialCycle = onlyCycle(helper, anchor);
        startCapture(level, anchor);
        helper.runAfterDelay(30, () -> {
            String liveState = "alive=" + npc.isAlive() + ", removed=" + npc.isRemoved() + ", health=" + npc.getHealth() + ", candidates=" + noticeCandidates(level, anchor) + ", nearby=" + level.getEntitiesOfClass(LabMachinistEntity.class, new AABB(anchor).inflate(16)).stream().map(entity -> entity.getUUID() + "@" + entity.position() + ":" + entity.getHealth() + ":" + entity.isRemoved()).toList();
            System.out.println("DISSOLVER_VOICE_SETUP " + liveState);
            helper.assertTrue(npc.isAlive() && !npc.isRemoved() && noticeCandidates(level, anchor) == 1, "The saved voice task must still have its live LabMachinist at the notice point: " + liveState);
            DissolverCycleEntity old = initialCycle;
            CompoundTag tag = save(helper, old);
            helper.assertTrue(tag.getIntOr("DissolverPhase", 0) == 3 && tag.getIntOr("DissolverVoices", 0) == 1, "The saved task must retain the committed notice count and first voice deadline; phase=" + tag.getIntOr("DissolverPhase", 0) + ", voices=" + tag.getIntOr("DissolverVoices", 0) + ", elapsed=" + tag.getIntOr("DissolverElapsed", 0) + ", liveCandidates=" + noticeCandidates(level, anchor) + ", npcPos=" + npc.position() + ", npcAlive=" + npc.isAlive());
            old.discard();
            putUuid(tag, UUID.randomUUID());
            DissolverCycleEntity restored = LabWorldgenContent.DISSOLVER_CYCLE.get().create(level, EntitySpawnReason.LOAD);
            restored.load(input(helper, tag));
            helper.assertTrue(level.addFreshEntity(restored), "Reloaded voice task must join the server level");
        });
        helper.runAfterDelay(34, () -> {
            helper.assertTrue(sounds(anchor).stream().filter("qo_em1_speak"::equals).count() == 1 && sounds(anchor).stream().noneMatch("qo_em2_speak"::equals), "Reloaded phase three must submit its first sound exactly once without skipping to the second stage");
            DissolverCycleEntity old = onlyCycle(helper, anchor);
            CompoundTag tag = save(helper, old);
            old.discard();
            putUuid(tag, UUID.randomUUID());
            DissolverCycleEntity restored = LabWorldgenContent.DISSOLVER_CYCLE.get().create(level, EntitySpawnReason.LOAD);
            restored.load(input(helper, tag));
            helper.assertTrue(level.addFreshEntity(restored), "The phase-four save must restore as a live server task");
        });
        helper.runAfterDelay(51, () -> {
            helper.assertTrue(sounds(anchor).stream().filter("qo_em1_speak"::equals).count() == 1 && sounds(anchor).stream().filter("qo_em2_speak"::equals).count() == 1, "Reloading after voice one must not replay it and must still submit voice two once");
            stopCapture(anchor);
            helper.succeed();
        });
    }

    private static BlockPos pos(GameTestHelper helper) {
        return helper.absolutePos(new BlockPos(10, 5, 10));
    }

    private static PlayerFixture player(GameTestHelper helper, GameType mode) {
        Player player = helper.makeMockPlayer(mode);
        player.getAbilities().instabuild = mode == GameType.CREATIVE;
        return new PlayerFixture(player);
    }

    private static BlockHitResult hit(BlockPos pos) {
        return new BlockHitResult(Vec3.atCenterOf(pos), Direction.UP, pos, false);
    }

    private static void seedForHatch(net.minecraft.server.level.ServerLevel level, boolean hatch) {
        for (long seed = 0; seed < 10000; seed++) {
            level.getRandom().setSeed(seed);
            if ((level.getRandom().nextDouble() < .1) == hatch) {
                level.getRandom().setSeed(seed);
                return;
            }
        }
        throw new IllegalStateException("No deterministic random seed found in the search interval");
    }

    private static void startCapture(net.minecraft.world.level.Level level, BlockPos pos) {
        SOUND_TRACE.put(pos.asLong(), new SoundCapture(level, new CopyOnWriteArrayList<>()));
    }

    private static List<String> sounds(BlockPos pos) {
        SoundCapture capture = SOUND_TRACE.get(pos.asLong());
        return capture == null ? List.of() : List.copyOf(capture.sounds());
    }

    private static void stopCapture(BlockPos pos) {
        SOUND_TRACE.remove(pos.asLong());
    }

    private static void activate(GameTestHelper helper, BlockPos anchor) {
        var level = helper.getLevel();
        var gemlock = LabWorldgenContent.DISSOLVER_GEMLOCK.get();
        helper.assertTrue(level.setBlock(anchor, gemlock.defaultBlockState(), 3), "Dissolver gemlock fixture must be placed");
        var player = helper.makeMockPlayer(GameType.SURVIVAL);
        player.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(MaterialContent.items().get("phobotite").get(), 1));
        var state = level.getBlockState(anchor);
        var result = state.useItemOn(player.getMainHandItem(), level, player, InteractionHand.MAIN_HAND, hit(anchor));
        helper.assertTrue(result.consumesAction() && level.getBlockState(anchor).getValue(DissolverGemlockBlock.BLOCKSTATE) == 1, "Real gemlock item interaction must commit the active cycle");
    }

    private static LabMachinistEntity placeNpc(net.minecraft.server.level.ServerLevel level, BlockPos pos) {
        var npc = LabContent.LAB_MACHINIST.get().create(level, EntitySpawnReason.LOAD);
        npc.setNoAi(true);
        npc.setNoGravity(true);
        npc.setPersistenceRequired();
        npc.setPos(Vec3.atCenterOf(pos));
        if (!level.addFreshEntity(npc)) throw new IllegalStateException("LabMachinist fixture was rejected by the test level");
        return npc;
    }

    private static int noticeCandidates(net.minecraft.server.level.ServerLevel level, BlockPos anchor) {
        Vec3 center = Vec3.atLowerCornerOf(anchor);
        return level.getEntitiesOfClass(Entity.class, new AABB(center, center).inflate(4.5), entity -> true).stream().filter(LabMachinistEntity.class::isInstance).toList().size();
    }

    private static DissolverCycleEntity onlyCycle(GameTestHelper helper, BlockPos anchor) {
        var cycles = cycles(helper, anchor);
        helper.assertTrue(cycles.size() == 1, "Exactly one real DissolverCycleEntity must remain at the anchor");
        return cycles.getFirst();
    }

    private static List<DissolverCycleEntity> cycles(GameTestHelper helper, BlockPos anchor) {
        return helper.getLevel().getEntitiesOfClass(DissolverCycleEntity.class, new AABB(anchor).inflate(2));
    }
    private static CompoundTag save(GameTestHelper helper, Entity entity) {
        TagValueOutput output = TagValueOutput.createWithContext(ProblemReporter.DISCARDING, helper.getLevel().registryAccess());
        entity.saveWithoutId(output);
        return output.buildResult();
    }

    private static net.minecraft.world.level.storage.ValueInput input(GameTestHelper helper, CompoundTag tag) {
        return TagValueInput.create(ProblemReporter.DISCARDING, helper.getLevel().registryAccess(), tag);
    }

    private static void putUuid(CompoundTag tag, UUID value) {
        tag.put("UUID", UUIDUtil.CODEC.encodeStart(NbtOps.INSTANCE, value).result().orElseThrow());
    }

    private record PlayerFixture(Player player) { }
    private record SoundCapture(net.minecraft.world.level.Level level, List<String> sounds) { }

    private GlastrekkerDissolverWorldNpcServerTests() { }
}
