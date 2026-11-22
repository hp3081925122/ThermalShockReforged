package com.hp.thermalshockreforged.gametest;

import com.hp.thermalshockreforged.ThermalShockReforged;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.storage.TagValueInput;
import net.minecraft.world.level.storage.TagValueOutput;

public final class EntityServerRegressionTests {
    private static final Map<String, String> FIRST_TICK_CLEANUP = Map.ofEntries(
            Map.entry("maiden_volley", "MaidenVolleyEntity.tick discards unarmed tasks"),
            Map.entry("stratus_reactor_sequence", "ReactorSequenceEntity.tick rejects unarmed sequences"),
            Map.entry("zap_detonation", "ZapDetonationEntity.tick rejects unarmed detonations"),
            Map.entry("soldier_combo_task", "SoldierComboEntity.tick rejects tasks without a caster"),
            Map.entry("snobol_blast", "SnobolBlastEntity.tick rejects unattached blasts without a caster"),
            Map.entry("herald_death_effect", "BossDeathEffectEntity.tick rejects effects without remaining emissions"),
            Map.entry("bloomling_burst", "BloomlingBurstEntity.tick rejects unarmed bursts"),
            Map.entry("volcano_spark_task", "VolcanoSparkEntity.tick discards when its pending spark list is empty"),
            Map.entry("sunbeam_attack_task", "SunbeamAttackEntity.tick discards without its caster UUID"),
            Map.entry("delta_volley_task", "DeltaVolleyEntity.tick discards without its caster UUID"),
            Map.entry("epsilon_attack_task", "EpsilonAttackEntity.tick discards without its caster UUID"),
            Map.entry("err", "ErrFieldEntity.baseTick decrements the uninitialized zero timer and discards"),
            Map.entry("shock_attack_entity", "CavebloomerImpactEntity.tick discards before finalizeSpawn initialization"),
            Map.entry("poison_attack_entity", "CavebloomerImpactEntity.tick discards before finalizeSpawn initialization"),
            Map.entry("atlas_activation", "AtlasActivationEntity.tick discards before the block activation arms it"),
            Map.entry("beetle_attack", "BeetleAttackEntity.tick discards before BeetleCombatEntity arms it"),
            Map.entry("effigy_strike_task", "EffigyStrikeEntity.tick discards before EffigyEntity arms it"),
            Map.entry("huntress_task", "HuntressTaskEntity.tick discards before HuntressBossEntity arms it"),
            Map.entry("pylon_core_activation", "PylonCoreActivationEntity.tick discards before its pylon arms it"),
            Map.entry("fire_bomb_activation", "FireBombActivationEntity.tick discards before its pylon arms it"),
            Map.entry("pylon_activation_task", "PylonActivationEntity.tick discards before its pylon arms it"),
            Map.entry("gunner_volley_task", "GunnerVolleyEntity.tick discards without its caster UUID"),
            Map.entry("kilolyte_attack_task", "KilolyteAttackEntity.tick discards without its caster UUID"),
            Map.entry("ripple_cast_task", "RippleCastEntity.tick discards without its caster UUID"),
            Map.entry("supernova_blast_task", "SupernovaBlastEntity.tick discards before weapon commit arms it"),
            Map.entry("razoraptor_strike_task", "RazoraptorStrikeEntity.tick discards before RazoraptorEntity arms it"),
            Map.entry("disk_resonance_task", "DiskResonanceEntity.tick discards before a disk action arms it"),
            Map.entry("buzzsaw_sweep_task", "BuzzsawSweepEntity.tick discards before a weapon action arms it"),
            Map.entry("titan_swing", "TitanSwingEntity.tick discards before a titan attack arms it"),
            Map.entry("blasphemer_process_task", "BlasphemerProcessEntity.tick discards before the machine arms it"),
            Map.entry("blade_impact_task", "BladeImpactEntity.tick discards before a weapon action arms it"),
            Map.entry("wallbore_cycle", "WallboreCycleEntity.tick discards before its machine arms it"),
            Map.entry("tesla_pulse_task", "TeslaPulseEntity.tick discards before TeslaReedEntity arms it"),
            Map.entry("volcano_burst_task", "VolcanoBurstEntity.tick discards before a weapon action arms it"),
            Map.entry("thermoblaster_burst_task", "ThermoblasterBurstEntity.tick discards before a weapon action arms it"),
            Map.entry("speleothem_sound_task", "SpeleothemSoundEntity.tick discards before a weapon action arms it"),
            Map.entry("lantern_death_task", "LanternDeathEntity.tick discards before the death event arms it"),
            Map.entry("dissolver_cycle", "DissolverCycleEntity.tick discards before a gemlock arms it"),
            Map.entry("rifle_cycle_sound_task", "RifleCycleSoundEntity.tick discards before a weapon action arms it"),
            Map.entry("magnetic_cycle_sound_task", "MagneticCycleSoundEntity.tick discards before a weapon action arms it"),
            Map.entry("combat_item_task", "CombatItemTaskEntity.tick discards before a combat action arms it"),
            Map.entry("ribbonblade_action_task", "RibbonbladeActionEntity.tick discards before a weapon action arms it"),
            Map.entry("hi_volt_pulse_task", "HiVoltPulseEntity.tick discards before a weapon action arms it"),
            Map.entry("springslime_fade", "SpringFadeEntity.tick discards before SpringslimeEntity arms it"),
            Map.entry("flare_gun_cycle_task", "FlareGunCycleEntity.tick discards before a weapon action arms it"),
            Map.entry("blizzard_deployment_task", "DeploymentTaskEntity.tick discards before IcidrostriderEntity arms it"),
            Map.entry("monolith_task", "MonolithTaskEntity.tick discards before MonolithEntity arms it"),
            Map.entry("magnetic_detonation_task", "MagneticDetonationEntity.tick discards before a magnetic weapon arms it"),
            Map.entry("brimstone_cake_consumption", "BrimstoneCakeConsumption.tick discards before consumption arms it"),
            Map.entry("ferro_cake_consumption", "FerroCakeConsumption.tick discards before consumption arms it"),
            Map.entry("purifier_pulse", "PurifierPulseEntity.tick discards when no matching purifier owns it"),
            Map.entry("icebreaker_pulse", "IcebreakerPulseEntity.tick discards when no matching block entity owns it"),
            Map.entry("thermite_detonation", "ThermiteDetonationEntity.tick discards before detonation arms it"));
    private static final Map<String, String> AI_CLEANUP = Map.of(
            "err", "ErrFieldEntity is an expiring attack field rather than a creature",
            "shock_attack_entity", "CavebloomerImpactEntity is a short-lived attack carrier",
            "poison_attack_entity", "CavebloomerImpactEntity is a short-lived attack carrier",
            "zap_attack_entity", "ZapWarningEntity commits a 20-tick detonation task and removes itself",
            "blinding_lantern_flash_ring", "BlindingLanternFlashRingEntity expires after ten scheduled ticks");

    private EntityServerRegressionTests() {
    }

    public static void allRegisteredEntitiesConstructAndRoundTrip(GameTestHelper helper) {
        List<EntityType<?>> types = registeredTypes();
        helper.assertTrue(!types.isEmpty(), "No mod entity types were found in the runtime registry");
        constructNextEntity(helper, types, 0, new int[2]);
    }

    public static void allRegisteredEntitiesTickInWorld(GameTestHelper helper) {
        List<EntityType<?>> types = registeredTypes();
        helper.assertTrue(!types.isEmpty(), "No mod entity types were found in the runtime registry");
        tickNextEntity(helper, types, 0, new int[2]);
    }

    public static void allRegisteredMobsTickWithAiEnabled(GameTestHelper helper) {
        List<EntityType<?>> types = new ArrayList<>();
        for (EntityType<?> type : registeredTypes()) {
            Entity sample = type.create(helper.getLevel(), EntitySpawnReason.TRIGGERED);
            var id = BuiltInRegistries.ENTITY_TYPE.getKey(type);
            helper.assertTrue(sample != null, "Entity constructor returned null while identifying AI mobs: " + id);
            if (sample instanceof Mob) {
                types.add(type);
            }
            sample.discard();
        }
        ThermalShockReforged.LOGGER.info("Entity AI regression selected {} Mob types, including transient/owner-bound cases: {}", types.size(), AI_CLEANUP.keySet());
        helper.assertTrue(!types.isEmpty(), "No registered mod mobs were found");
        tickNextMobWithAi(helper, types, 0, new int[2]);
    }

    private static List<EntityType<?>> registeredTypes() {
        List<EntityType<?>> types = new ArrayList<>();
        for (var entry : BuiltInRegistries.ENTITY_TYPE.entrySet()) {
            if (ThermalShockReforged.MOD_ID.equals(BuiltInRegistries.ENTITY_TYPE.getKey(entry.getValue()).getNamespace())) {
                types.add(entry.getValue());
            }
        }
        return types;
    }

    private static void constructNextEntity(GameTestHelper helper, List<EntityType<?>> types, int index, int[] counts) {
        if (index >= types.size()) {
            ThermalShockReforged.LOGGER.info("Entity construct/save regression complete: constructed={}, save-load checked={}", counts[0], counts[1]);
            helper.succeed();
            return;
        }
        var type = types.get(index);
        var id = BuiltInRegistries.ENTITY_TYPE.getKey(type);
        Entity entity = type.create(helper.getLevel(), EntitySpawnReason.TRIGGERED);
        helper.assertTrue(entity != null, "Entity constructor returned null: " + id);
        counts[0]++;
        entity.setNoGravity(true);
        if (entity instanceof net.minecraft.world.entity.Mob mob) {
            mob.setNoAi(true);
        }
        entity.snapTo(helper.absolutePos(new BlockPos(3, 2, 3)).getCenter());

        if (entity.shouldBeSaved()) {
            TagValueOutput output = TagValueOutput.createWithContext(ProblemReporter.DISCARDING, helper.getLevel().registryAccess());
            entity.saveWithoutId(output);
            Entity restored = type.create(helper.getLevel(), EntitySpawnReason.LOAD);
            helper.assertTrue(restored != null, "Persistent entity could not be reconstructed: " + id);
            restored.load(TagValueInput.create(ProblemReporter.DISCARDING, helper.getLevel().registryAccess(), output.buildResult()));
            helper.assertTrue(restored.getType() == type, "Save/load changed entity type: " + id);
            helper.assertTrue(restored.position().distanceToSqr(entity.position()) < 0.0001, "Save/load changed entity position: " + id);
            if (entity instanceof LivingEntity living && restored instanceof LivingEntity restoredLiving) {
                helper.assertTrue(Math.abs(living.getHealth() - restoredLiving.getHealth()) < 0.001, "Save/load changed entity health: " + id);
            }
            restored.discard();
            counts[1]++;
        }

        ThermalShockReforged.LOGGER.info("Entity construct/save regression: id={}, constructed=true, saveLoadChecked={}", id, entity.shouldBeSaved());
        entity.discard();
        constructNextEntity(helper, types, index + 1, counts);
    }

    private static void tickNextEntity(GameTestHelper helper, List<EntityType<?>> types, int index, int[] counts) {
        if (index >= types.size()) {
            ThermalShockReforged.LOGGER.info("Entity first-world-tick regression complete: ticked={}, expected cleanup={}", counts[0], counts[1]);
            helper.succeed();
            return;
        }
        EntityType<?> type = types.get(index);
        var id = BuiltInRegistries.ENTITY_TYPE.getKey(type);
        Entity entity = type.create(helper.getLevel(), EntitySpawnReason.TRIGGERED);
        helper.assertTrue(entity != null, "Entity constructor returned null: " + id);
        entity.setNoGravity(true);
        if (entity instanceof Mob mob) {
            mob.setNoAi(true);
            mob.setPersistenceRequired();
        }
        entity.snapTo(helper.absolutePos(new BlockPos(3, 2, 3)).getCenter());
        String expectedCleanup = FIRST_TICK_CLEANUP.get(id.getPath());
        var keeper = "monolith".equals(id.getPath()) ? keepMonolithAwake(helper) : null;
        if (entity instanceof Mob mob && expectedCleanup == null) {
            if ("skeletitan".equals(id.getPath())) mob.getRandom().setSeed(0);
            mob.finalizeSpawn(helper.getLevel(), helper.getLevel().getCurrentDifficultyAt(entity.blockPosition()), EntitySpawnReason.TRIGGERED, null);
        }
        boolean persistent = entity.shouldBeSaved();
        helper.assertTrue(helper.getLevel().addFreshEntity(entity), "Entity was rejected when entering the test world: " + id);
        helper.runAfterDelay(1, () -> {
            try {
                helper.assertTrue(entity.tickCount > 0, "Entity did not advance through its first server world tick: " + id);
                if (expectedCleanup != null) {
                    helper.assertTrue(entity.isRemoved(), "Expected transient entity did not clean itself up: " + id + ", reason=" + expectedCleanup);
                    counts[1]++;
                } else {
                    helper.assertTrue(!entity.isRemoved(), "Entity unexpectedly removed itself during its first server world tick: " + id);
                }
                counts[0]++;
                ThermalShockReforged.LOGGER.info("Entity first-world-tick regression: id={}, tickCount={}, removed={}, expectedCleanup={}", id, entity.tickCount, entity.isRemoved(), expectedCleanup != null);
            } finally {
                entity.discard();
                if (keeper != null) PlayerInteractionTests.release(helper, keeper);
            }
            tickNextEntity(helper, types, index + 1, counts);
        });
    }

    private static void tickNextMobWithAi(GameTestHelper helper, List<EntityType<?>> types, int index, int[] counts) {
        if (index >= types.size()) {
            ThermalShockReforged.LOGGER.info("Entity AI regression complete: survived 20 AI-enabled ticks={}, expected transient cleanup={}", counts[0], counts[1]);
            helper.succeed();
            return;
        }
        EntityType<?> type = types.get(index);
        var id = BuiltInRegistries.ENTITY_TYPE.getKey(type);
        Entity entity = type.create(helper.getLevel(), EntitySpawnReason.TRIGGERED);
        helper.assertTrue(entity instanceof Mob, "Non-mob type entered the AI regression set: " + id);
        Mob mob = (Mob) entity;
        mob.setNoGravity(true);
        mob.setNoAi(false);
        mob.setPersistenceRequired();
        mob.snapTo(helper.absolutePos(new BlockPos(3, 2, 3)).getCenter());
        String expectedCleanup = AI_CLEANUP.get(id.getPath());
        var keeper = "monolith".equals(id.getPath()) ? keepMonolithAwake(helper) : null;
        if (expectedCleanup == null) {
            if ("skeletitan".equals(id.getPath())) mob.getRandom().setSeed(0);
            mob.finalizeSpawn(helper.getLevel(), helper.getLevel().getCurrentDifficultyAt(mob.blockPosition()), EntitySpawnReason.TRIGGERED, null);
        } else if ("blinding_lantern_flash_ring".equals(id.getPath())) {
            mob.finalizeSpawn(helper.getLevel(), helper.getLevel().getCurrentDifficultyAt(mob.blockPosition()), EntitySpawnReason.TRIGGERED, null);
        }
        boolean persistent = mob.shouldBeSaved();
        helper.assertTrue(helper.getLevel().addFreshEntity(mob), "Mob was rejected when entering the AI test world: " + id);
        int requiredTicks = "zap_attack_entity".equals(id.getPath()) ? 22 : "blinding_lantern_flash_ring".equals(id.getPath()) ? 20 : expectedCleanup == null ? 20 : 1;
        helper.runAfterDelay(requiredTicks, () -> {
            try {
                if (expectedCleanup != null) {
                    helper.assertTrue(mob.isRemoved() && mob.tickCount > 0,
                            "Expected transient/owner-bound mob did not clean itself up: " + id + ", reason=" + expectedCleanup + ", ticks=" + mob.tickCount);
                    counts[1]++;
                } else {
                    helper.assertTrue(mob.tickCount >= 20 && !mob.isRemoved(),
                            "Creature did not survive 20 AI-enabled server ticks: " + id + ", ticks=" + mob.tickCount + ", removed=" + mob.isRemoved() + ", persistent=" + persistent
                                    + ", removalReason=" + mob.getRemovalReason() + ", health=" + mob.getHealth() + ", position=" + mob.position()
                                    + ", lastDamage=" + (mob.getLastDamageSource() == null ? "none" : mob.getLastDamageSource().getMsgId()));
                    counts[0]++;
                }
                ThermalShockReforged.LOGGER.info("Entity AI regression: id={}, AI enabled, ticks={}, removed={}, expectedCleanup={}", id, mob.tickCount, mob.isRemoved(), expectedCleanup != null);
            } finally {
                mob.discard();
                if (keeper != null) PlayerInteractionTests.release(helper, keeper);
            }
            tickNextMobWithAi(helper, types, index + 1, counts);
        });
    }

    private static net.minecraft.server.level.ServerPlayer keepMonolithAwake(GameTestHelper helper) {
        var keeper = PlayerInteractionTests.player(helper, net.minecraft.world.level.GameType.SURVIVAL, 3, 2, 3);
        keeper.getAbilities().invulnerable = true;
        keeper.setInvulnerable(true);
        return keeper;
    }

    public static void monolithCreatesDormantReplacementWithoutNearbyPlayers(GameTestHelper helper) {
        var level = helper.getLevel();
        var pos = helper.absolutePos(new BlockPos(4096, 5, 4096));
        var bounds = net.minecraft.world.phys.AABB.ofSize(pos.getCenter(), 180, 180, 180);
        helper.assertTrue(level.players().stream().noneMatch(player -> !player.isRemoved() && player.getBoundingBox().intersects(bounds)), "Sleep fixture must be isolated from every test player");
        level.getChunkAt(pos);
        var monolith = com.hp.thermalshockreforged.entity.monolith.MonolithContent.ACTIVE.get().create(level, EntitySpawnReason.TRIGGERED);
        helper.assertTrue(monolith != null, "Monolith must be constructible for the sleep conversion");
        monolith.snapTo(pos.getCenter());
        helper.assertTrue(level.addFreshEntity(monolith), "Isolated monolith must enter the actual server world");
        var replacements = new ArrayList<com.hp.thermalshockreforged.entity.monolith.DormantMonolithEntity>();
        java.util.function.Consumer<net.neoforged.neoforge.event.entity.EntityJoinLevelEvent> listener = event -> {
            if (event.getLevel() == level && event.getEntity() instanceof com.hp.thermalshockreforged.entity.monolith.DormantMonolithEntity dormant
                    && dormant.blockPosition().equals(monolith.blockPosition())) replacements.add(dormant);
        };
        net.neoforged.neoforge.common.NeoForge.EVENT_BUS.addListener(listener);
        try {
            monolith.baseTick();
            helper.assertTrue(monolith.isRemoved(), "Monolith without nearby players must enter dormancy");
            helper.assertTrue(replacements.size() == 1, "Sleep conversion must create exactly one dormant replacement");
            var replacement = replacements.getFirst();
            helper.assertTrue(replacement.isAddedToLevel() && !replacement.isRemoved() && replacement.level() == level,
                    "Dormant replacement must actually enter the server level even before its distant chunk becomes queryable");
            helper.assertTrue(replacement.blockPosition().equals(monolith.blockPosition()) && replacement.getDeltaMovement().lengthSqr() == 0,
                    "Dormant replacement must retain the source position and zero velocity");
        } finally {
            net.neoforged.neoforge.common.NeoForge.EVENT_BUS.unregister(listener);
            replacements.forEach(Entity::discard);
            monolith.discard();
        }
        helper.succeed();
    }

    public static void skeletitanRandomVariantCommitsFrostReplacement(GameTestHelper helper) {
        var level = helper.getLevel();
        var pos = helper.absolutePos(new BlockPos(3, 2, 3));
        level.setBlockAndUpdate(pos.below(), net.minecraft.world.level.block.Blocks.STONE.defaultBlockState());
        var source = com.hp.thermalshockreforged.entity.titans.TitanContent.SKELETITAN.get().create(level, EntitySpawnReason.TRIGGERED);
        helper.assertTrue(source != null, "Skeletitan must be constructible for random variant initialization");
        source.snapTo(pos.getCenter());
        long seed = 0;
        while (seed < 100000 && net.minecraft.util.RandomSource.create(seed).nextDouble() >= .12) seed++;
        helper.assertTrue(seed < 100000 && net.minecraft.util.RandomSource.create(0).nextDouble() >= .12, "Variant fixtures must select both random branches deterministically");
        source.getRandom().setSeed(seed);
        var replacements = new ArrayList<com.hp.thermalshockreforged.entity.titans.SkeletalTitanEntity>();
        java.util.function.Consumer<net.neoforged.neoforge.event.entity.EntityJoinLevelEvent> listener = event -> {
            if (event.getLevel() == level && event.getEntity() instanceof com.hp.thermalshockreforged.entity.titans.SkeletalTitanEntity titan
                    && titan.kind() == com.hp.thermalshockreforged.entity.titans.SkeletalTitanEntity.Kind.FROST && titan.blockPosition().equals(pos)) replacements.add(titan);
        };
        net.neoforged.neoforge.common.NeoForge.EVENT_BUS.addListener(listener);
        try {
            source.finalizeSpawn(level, level.getCurrentDifficultyAt(pos), EntitySpawnReason.TRIGGERED, null);
        } finally {
            net.neoforged.neoforge.common.NeoForge.EVENT_BUS.unregister(listener);
        }
        helper.assertTrue(source.isRemoved() && replacements.size() == 1, "The selected random branch must consume the source and create exactly one Frost Titan");
        var replacement = replacements.getFirst();
        helper.assertTrue(replacement.isAddedToLevel() && !replacement.isRemoved() && level.getEntity(replacement.getUUID()) == replacement, "The variant must actually enter the server world");
        replacement.setNoAi(true);
        replacement.setNoGravity(true);
        replacement.setPersistenceRequired();
        helper.runAfterDelay(1, () -> {
            try {
                helper.assertTrue(replacement.tickCount > 0 && !replacement.isRemoved(), "The committed variant must survive its first real world tick");
                helper.succeed();
            } finally {
                replacement.discard();
            }
        });
    }

    public static void forecastAttackCooldownSurvivesReload(GameTestHelper helper) {
        var forecast = helper.spawnWithNoFreeWill(com.hp.thermalshockreforged.entity.ModEntities.FORECAST.get(), 1, 2, 1);
        var target = helper.spawnWithNoFreeWill(EntityType.COW, 4, 2, 4);
        target.setNoGravity(true);
        target.getAttribute(Attributes.MAX_HEALTH).setBaseValue(100);
        target.setHealth(100);
        forecast.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(7);
        helper.assertTrue(forecast.doHurtTarget(helper.getLevel(), target), "Forecast's server attack entry point must start a zap");
        helper.assertTrue(helper.getLevel().getEntitiesOfClass(com.hp.thermalshockreforged.entity.ZapWarningEntity.class, target.getBoundingBox().inflate(2)).size() == 1,
                "Forecast attack must create exactly one armed warning at its target");

        TagValueOutput output = TagValueOutput.createWithContext(ProblemReporter.DISCARDING, helper.getLevel().registryAccess());
        forecast.saveWithoutId(output);
        var restored = com.hp.thermalshockreforged.entity.ModEntities.FORECAST.get().create(helper.getLevel(), EntitySpawnReason.LOAD);
        helper.assertTrue(restored != null, "Forecast could not be reconstructed for cooldown reload check");
        restored.load(TagValueInput.create(ProblemReporter.DISCARDING, helper.getLevel().registryAccess(), output.buildResult()));
        restored.snapTo(1.5, 2, 1.5);
        helper.assertTrue(!restored.doHurtTarget(helper.getLevel(), target), "Saved Forecast cooldown must prevent a second zap after reload");
        helper.succeed();
    }
}
