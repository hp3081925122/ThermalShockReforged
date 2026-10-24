package com.hp.thermalshockreforged.gametest;

import com.hp.thermalshockreforged.ThermalShockReforged;
import java.util.ArrayList;
import java.util.List;
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

    private record Entry(String name, Consumer<GameTestHelper> function, int maxTicks, String structure) {
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
    }

    private TsrGameTests() {
    }

    public static void add(String name, Consumer<GameTestHelper> function, int maxTicks) {
        add(name, function, maxTicks, STRUCTURE);
    }

    public static void add(String name, Consumer<GameTestHelper> function, int maxTicks, String structure) {
        TESTS.add(new Entry(name, function, maxTicks, structure));
    }

    public static Identifier id(String path) {
        return Identifier.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, path);
    }

    public static ValueInput roundTrip(GameTestHelper helper, Entity entity) {
        TagValueOutput output = TagValueOutput.createWithContext(ProblemReporter.DISCARDING, helper.getLevel().registryAccess());
        entity.saveWithoutId(output);
        return TagValueInput.create(ProblemReporter.DISCARDING, helper.getLevel().registryAccess(), output.buildResult());
    }

    @SubscribeEvent
    public static void registerFunctions(RegisterEvent event) {
        event.register(Registries.TEST_FUNCTION, registry -> TESTS.forEach(entry -> registry.register(id(entry.name()), entry.function())));
    }

    @SubscribeEvent
    public static void registerTests(RegisterGameTestsEvent event) {
        var environment = event.registerEnvironment(id("default"));
        for (Entry entry : TESTS) {
            var data = new TestData<>(environment, id(entry.structure()), entry.maxTicks(), 0, true, Rotation.NONE, false, 1, 1, false, 0);
            event.registerTest(id(entry.name()), new FunctionGameTestInstance(ResourceKey.create(Registries.TEST_FUNCTION, id(entry.name())), data));
        }
    }
}
