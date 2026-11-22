package com.hp.thermalshockreforged.gametest;

import com.hp.thermalshockreforged.ThermalShockReforged;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import net.minecraft.core.registries.Registries;
import net.minecraft.gametest.framework.FunctionGameTestInstance;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.gametest.framework.TestData;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.storage.TagValueInput;
import net.minecraft.world.level.storage.TagValueOutput;
import net.minecraft.world.level.storage.ValueInput;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.RegisterGameTestsEvent;
import net.neoforged.neoforge.registries.RegisterEvent;

@EventBusSubscriber(modid = ThermalShockReforged.MOD_ID)
public final class TsrGameTests {
    public static final String STRUCTURE = "migration_empty";
    private static final List<Entry> TESTS = new ArrayList<>();
    private static final Set<String> SIMULATION_KEEPER_TESTS = Set.of(
            "history_forecast_fixture_audit_server_tests_warning_filters_items_but_keeps_ordinary_entities",
            "history_huntress_combat_next_tests_slam_waits_for_final_stage_then_hits_and_releases_gravity",
            "history_maiden_combat_next_tests_wave_keeps_committed_target_identity_after_target_moves",
            "history_maiden_combat_next_tests_emitted_blood_arrow_uses_real_collision_and_damage_source",
            "history_maiden_combat_next_tests_moving_caster_changes_real_volley_launch_origin");

    private record Entry(String name, Consumer<GameTestHelper> function, int maxTicks, String structure, boolean required, boolean dedicatedOnly) {
    }

    static {
        add("all_pillars_retain_placement_rotation_light_and_fire", AxisMigrationTests::allPillarsRetainPlacementRotationLightAndFire, 100);
        add("buttons_retain_distinct_press_durations", InteractiveMigrationTests::buttonsRetainDistinctPressDurations, 65);
        add("only_original_wood_buttons_respond_to_arrows", InteractiveMigrationTests::onlyOriginalWoodButtonsRespondToArrows, 100);
        add("iron_sound_pressure_plate_still_ignores_items", InteractiveMigrationTests::ironSoundPressurePlateStillIgnoresItems, 100);
        add("doors_retain_hand_operation_and_paired_redstone_state", InteractiveMigrationTests::doorsRetainHandOperationAndPairedRedstoneState, 100);
        add("trapdoors_retain_water_and_interaction_categories", InteractiveMigrationTests::trapdoorsRetainWaterAndInteractionCategories, 100);
        add("scarlet_ray_keeps_distinct_attributes_size_and_loot", InteractiveMigrationTests::scarletRayKeepsDistinctAttributesSizeAndLoot, 100);
        add("modular_tool_does_not_require_vanilla_class", MigrationGameTests::modularToolDoesNotRequireVanillaClass, 100);
        add("migrated_recipe_and_wolf_food_load", MigrationGameTests::migratedRecipeAndWolfFoodLoad, 100);
        add("zap_uses_owner_damage_and_multiplier", MigrationGameTests::zapUsesOwnerDamageAndMultiplier, 60);
        add("removed_owner_does_not_cancel_committed_zap", MigrationGameTests::removedOwnerDoesNotCancelCommittedZap, 60);
        add("forecast_cooldown_does_not_use_potion_effects", MigrationGameTests::forecastCooldownDoesNotUsePotionEffects, 100);
        add("map_colors_and_mining_tiers_remain_defined", MigrationGameTests::mapColorsAndMiningTiersRemainDefined, 100);
        add("koi_retains_aquatic_semantics_and_spawn_rule", MigrationGameTests::koiRetainsAquaticSemanticsAndSpawnRule, 100);
        add("koi_drops_one_fish_and_preserves_saved_texture", MigrationGameTests::koiDropsOneFishAndPreservesSavedTexture, 100);
        add("decorative_blocks_keep_shape_fire_and_tooltip_behavior", MigrationGameTests::decorativeBlocksKeepShapeFireAndTooltipBehavior, 100);
        add("painting_dimensions_and_placeable_tags_load", PaintingMigrationTests::paintingDimensionsAndPlaceableTagsLoad, 100);
        add("large_painting_keeps_variant_and_physical_size_on_save", PaintingMigrationTests::largePaintingKeepsVariantAndPhysicalSizeOnSave, 100);
        add("owner_commands_and_feeding_do_not_interfere", ProboskerMigrationTests::ownerCommandsAndFeedingDoNotInterfere, 100);
        add("sitting_retains_damage_and_persists_color_owner_and_pose", ProboskerMigrationTests::sittingRetainsDamageAndPersistsColorOwnerAndPose, 30);
        add("taming_keeps_both_random_outcomes_and_food_set", ProboskerMigrationTests::tamingKeepsBothRandomOutcomesAndFoodSet, 100);
        add("dump_wiki_data", WikiDumpGameTests::dumpWikiData, 100);
        PlayerInteractionTests.register();
        add("network_payload_byte_codecs_preserve_all_fields", NetworkCodecServerTests::allPayloadsPreserveFields, 100);
        add("network_payload_byte_codecs_reject_malformed_data", NetworkCodecServerTests::malformedPayloadsAreRejected, 100);
        add("every_recipe_loads_and_round_trips", DataServerRegressionTests::everyRecipeLoadsAndRoundTrips, 200);
        add("every_structure_and_advancement_loads", DataServerRegressionTests::everyStructureAndAdvancementLoads, 200);
        add("every_loot_table_loads_and_rolls", DataServerRegressionTests::everyLootTableLoadsAndRolls, 200);
        add("every_item_and_block_state_serializes", DataServerRegressionTests::everyItemAndBlockStateSerializes, 200);
        add("every_block_entity_preserves_persistent_data_and_inventory", DataServerRegressionTests::everyBlockEntityPreservesPersistentDataAndInventory, 200);
        add("world_progress_codec_and_state_transitions_round_trip", WorldServerRegressionTests::worldProgressCodecAndStateTransitionsRoundTrip, 100);
        add("custom_dimension_definitions_and_worldgen_decode", WorldServerRegressionTests::customDimensionDefinitionsAndWorldgenDecode, 400);
        add("all_registered_entities_construct_and_round_trip", EntityServerRegressionTests::allRegisteredEntitiesConstructAndRoundTrip, 100);
        add("all_registered_entities_tick_in_world", EntityServerRegressionTests::allRegisteredEntitiesTickInWorld, 600);
        add("all_registered_mobs_tick_with_ai_enabled", EntityServerRegressionTests::allRegisteredMobsTickWithAiEnabled, 4000);
        add("skeletitan_random_variant_commits_frost_replacement", EntityServerRegressionTests::skeletitanRandomVariantCommitsFrostReplacement, 40);
        add("forecast_attack_cooldown_survives_reload", EntityServerRegressionTests::forecastAttackCooldownSurvivesReload, 40);
        add("monolith_creates_dormant_replacement_without_nearby_players", EntityServerRegressionTests::monolithCreatesDormantReplacementWithoutNearbyPlayers, 100);
        add("frostbite_blocks_healing_and_removal_restores_it", StatusServerRegressionTests::frostbiteBlocksHealingAndRemovalRestoresIt, 100);
        add("electrified_damage_chain_does_not_recurse", StatusServerRegressionTests::electrifiedDamageChainDoesNotRecurse, 100);
        add("infection_removal_schedules_and_resolves_burst", StatusServerRegressionTests::infectionRemovalSchedulesAndResolvesBurst, 60);
        add("purified_prevents_infection_removal_recurrence", StatusServerRegressionTests::purifiedPreventsInfectionRemovalRecurrence, 60);
        MachineServerRegressionTests.register();
        ExtractorIntegrationTests.register();
        WorldProgressIntegrationTests.register();
        com.hp.thermalshockreforged.content.atlas.PalaceSoftlockServerTests.register();
        BossServerRegressionTests.register();
        NativeBrewingServerBehaviorTests.register();
        GrowthMachineServerBehaviorTests.register();
        FluidToolFoodServerBehaviorTests.register();
        UtilityDecorServerBehaviorTests.register();
        ControlInfectionParryServerTests.register();
        StatusSafetyServerBehaviorTests.register();
        add("spears_keep_vanilla_melee_during_item_cooldown", MeleeCooldownServerTests::spearsKeepVanillaMeleeDuringItemCooldown, 100);
        com.hp.thermalshockreforged.content.atlas.AtlasBatch30ServerTests.register();
        com.hp.thermalshockreforged.content.atlas.AtlasBatch30TargetedTests.register();
        com.hp.thermalshockreforged.content.atlas.AtlasWaterPlacementFinalTest.register();
        BenzorServerBehaviorTests.register();
        com.hp.thermalshockreforged.entity.kofoni.KofoniServerBehaviorTests.register();
        BeetleServerBehaviorTests.register();
        DormantBeetleServerTests.register();
        BlasphemerServerBehaviorTests.register();
        EarlyContentPlacementServerTests.register();
        EarlyContentServerBehaviorTests.register();
        com.hp.thermalshockreforged.entity.titans.TitanServerBehaviorTests.register();
        com.hp.thermalshockreforged.entity.sanctum.SanctumCombatServerTests.register();
        SootleHuntersServerTests.register();
        AqeronMoonMigrationTests.register();
        RibbonbladeServerBehaviorTests.register();
        GlastrekkerUpgradeServerBehaviorTests.register();
        BunkerDoorServerBehaviorTests.register();
        EarlyContentRegressionServerTests.register();
        EarlyContentIsolationServerTests.register();
        RemainingCombatServerTests.register();
        ServerCoverageSmokeTests.register();
        com.hp.thermalshockreforged.entity.sanctum.SanctumCombatParryFocusedTests.register();
        com.hp.thermalshockreforged.entity.sanctum.SanctumRippleTimingFocusedTests.register();
        com.hp.thermalshockreforged.content.farming.SickleServerBehaviorTests.register();
        com.hp.thermalshockreforged.content.labworldgen.DissolverGemlockServerBehaviorTests.register();
        com.hp.thermalshockreforged.entity.weaponcore.PrismaticSupernovaServerBehaviorTests.register();
        com.hp.thermalshockreforged.entity.gaze.GazeServerBehaviorTests.register();
        com.hp.thermalshockreforged.entity.icesquad.StriderServerBehaviorTests.register();
        com.hp.thermalshockreforged.entity.icesquad.DeploymentServerBehaviorTests.register();
        ElectricFamilyServerBehaviorTests.register();
        HuntressServerBehaviorTests.register();
        MaidenServerBehaviorTests.register();
        SootleServerBehaviorTests.register();
        SootlingServerBehaviorTests.register();
        FabricatorServerBehaviorTests.register();
        com.hp.thermalshockreforged.entity.glastrekker.GlastrekkerDissolverWorldNpcServerTests.register();
        com.hp.thermalshockreforged.content.weapons.WeaponServerBehaviorTests.register();
        com.hp.thermalshockreforged.content.weapons.WeaponBoundaryServerTests.register();
        com.hp.thermalshockreforged.entity.projectile.WeaponCoreDiskServerTests.register();
        com.hp.thermalshockreforged.entity.projectile.WeaponTailServerTests.register();
        com.hp.thermalshockreforged.entity.ForecastServerBehaviorTests.register();
        com.hp.thermalshockreforged.entity.ForecastBoundaryServerTests.register();
        com.hp.thermalshockreforged.entity.icesquad.BastionServerBehaviorTests.register();
        com.hp.thermalshockreforged.entity.icesquad.CurrentBastionSkillServerTests.register();
        com.hp.thermalshockreforged.entity.icesquad.GruntProjectileServerBehaviorTests.register();
        com.hp.thermalshockreforged.entity.DeathPersistenceNextTests.register();
        com.hp.thermalshockreforged.entity.DimensionPlayerIdentityTests.register();
        com.hp.thermalshockreforged.entity.ForecastFixtureAuditServerTests.register();
        com.hp.thermalshockreforged.entity.gravers.GraverCombatNextTests.register();
        com.hp.thermalshockreforged.entity.huntress.CombatFailureReruns.register();
        com.hp.thermalshockreforged.entity.huntress.HuntressCombatNextTests.register();
        com.hp.thermalshockreforged.entity.huntress.HuntressTailContractTests.register();
        com.hp.thermalshockreforged.entity.maiden.MaidenCombatNextTests.register();
        com.hp.thermalshockreforged.entity.maiden.MaidenTailContractTests.register();
        RibbonbladeSb002CorrectedRegressionTests.register();
        RibbonbladeSb002RegressionTests.register();
    }

    private TsrGameTests() {
    }

    public static void add(String name, Consumer<GameTestHelper> function, int maxTicks) {
        add(name, function, maxTicks, STRUCTURE);
    }

    public static void add(String name, Consumer<GameTestHelper> function, int maxTicks, String structure) {
        TESTS.add(new Entry(name, function, maxTicks, structure, true, false));
    }

    public static void addOptional(String name, Consumer<GameTestHelper> function, int maxTicks, String structure) {
        TESTS.add(new Entry(name, function, maxTicks, structure, false, false));
    }

    public static void addDedicated(String name, Consumer<GameTestHelper> function, int maxTicks, String structure) {
        TESTS.add(new Entry(name, function, maxTicks, structure, true, true));
    }

    public static Identifier id(String path) {
        return Identifier.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, path);
    }

    public static ValueInput roundTrip(GameTestHelper helper, Entity entity) {
        TagValueOutput output = TagValueOutput.createWithContext(ProblemReporter.DISCARDING, helper.getLevel().registryAccess());
        entity.saveWithoutId(output);
        return TagValueInput.create(ProblemReporter.DISCARDING, helper.getLevel().registryAccess(), output.buildResult());
    }

    private static Consumer<GameTestHelper> withSimulationKeeper(Entry entry) {
        if (!entry.name().startsWith("history_forecast_server_behavior_tests_") && !entry.name().startsWith("history_forecast_boundary_server_tests_") && !entry.name().startsWith("history_bastion_current_") && !SIMULATION_KEEPER_TESTS.contains(entry.name())) return entry.function();
        return helper -> {
            var keeper = PlayerInteractionTests.player(helper, net.minecraft.world.level.GameType.CREATIVE, 4, 6, 4);
            keeper.setNoGravity(true);
            keeper.setInvulnerable(true);
            keeper.setDeltaMovement(net.minecraft.world.phys.Vec3.ZERO);
            helper.testInfo.addListener(new net.minecraft.gametest.framework.GameTestListener() {
                @Override public void testStructureLoaded(net.minecraft.gametest.framework.GameTestInfo test) { }
                @Override public void testPassed(net.minecraft.gametest.framework.GameTestInfo test, net.minecraft.gametest.framework.GameTestRunner runner) { PlayerInteractionTests.release(helper, keeper); }
                @Override public void testFailed(net.minecraft.gametest.framework.GameTestInfo test, net.minecraft.gametest.framework.GameTestRunner runner) { PlayerInteractionTests.release(helper, keeper); }
                @Override public void testAddedForRerun(net.minecraft.gametest.framework.GameTestInfo original, net.minecraft.gametest.framework.GameTestInfo copy, net.minecraft.gametest.framework.GameTestRunner runner) { }
            });
            entry.function().accept(helper);
        };
    }

    @SubscribeEvent
    public static void registerFunctions(RegisterEvent event) {
        event.register(Registries.TEST_FUNCTION, registry -> TESTS.forEach(entry -> registry.register(id(entry.name()), withSimulationKeeper(entry))));
    }

    @SubscribeEvent
    public static void registerTests(RegisterGameTestsEvent event) {
        var environment = event.registerEnvironment(id("default"));
        var lifecycleEnvironment = event.registerEnvironment(id("entity_lifecycle"));
        for (Entry entry : TESTS) {
            var selectedEnvironment = environment;
            if (entry.name().equals("all_registered_entities_tick_in_world") || entry.name().equals("all_registered_mobs_tick_with_ai_enabled")) selectedEnvironment = lifecycleEnvironment;
            else if (entry.name().startsWith("history_forecast_server_behavior_tests_") || entry.name().startsWith("history_forecast_boundary_server_tests_")) selectedEnvironment = event.registerEnvironment(id("isolated_" + entry.name()));
            else if (entry.name().startsWith("history_bastion_current_")) selectedEnvironment = event.registerEnvironment(id("isolated_" + entry.name()));
            else if (SIMULATION_KEEPER_TESTS.contains(entry.name())) selectedEnvironment = event.registerEnvironment(id("isolated_" + entry.name()));
            else if (entry.name().startsWith("world_progress_integration_") || entry.name().equals("extractor_seeded_random_outputs_cover_both_branches")) selectedEnvironment = event.registerEnvironment(id("isolated_" + entry.name()));
            else if (entry.name().equals("history_sootle_hunters_server_tests_razorwing_day_spawn_accepts_both_shales_and_rejects_stone") || entry.name().equals("history_sootle_hunters_server_tests_razorwing_natural_spawn_rejects_night_after_world_time_advances")) selectedEnvironment = event.registerEnvironment(id("isolated_" + entry.name()));
            var data = new TestData<>(selectedEnvironment, id(entry.structure()), entry.maxTicks(), 0, entry.required(), Rotation.NONE, entry.dedicatedOnly(), 1, 1, false, 0);
            event.registerTest(id(entry.name()), new FunctionGameTestInstance(ResourceKey.create(Registries.TEST_FUNCTION, id(entry.name())), data));
        }
    }
}
