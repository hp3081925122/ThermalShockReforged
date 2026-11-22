package com.hp.thermalshockreforged.gametest;

import com.google.gson.JsonParser;
import com.hp.thermalshockreforged.ThermalShockReforged;
import com.mojang.serialization.JsonOps;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.storage.TagValueInput;
import net.minecraft.world.level.storage.TagValueOutput;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;

public final class ServerCoverageSmokeTests {
    private static final String MOD_ID = ThermalShockReforged.MOD_ID;
    private static final java.util.Set<String> MAP_COLOR_EXEMPTIONS = java.util.Set.of("fabricator_constructor", "light_blocking_barrier", "large_jellercell_collision");
    private static final java.util.Set<String> INVISIBLE_COMPONENT_BLOCKS = java.util.Set.of("decor_part", "maw_door_part");

    public static void register() {
        TsrGameTests.add("history_server_coverage_smoke_tests_every_registered_block_and_item_has_a_valid_default", ServerCoverageSmokeTests::everyRegisteredBlockAndItemHasAValidDefault, 120, "supernova_server");
        TsrGameTests.add("history_server_coverage_smoke_tests_every_registered_entity_can_be_created_and_saved", ServerCoverageSmokeTests::everyRegisteredEntityCanBeCreatedAndSaved, 120, "supernova_server");
        TsrGameTests.add("history_server_coverage_smoke_tests_every_mod_entity_ticks_on_the_server", ServerCoverageSmokeTests::everyModEntityTicksOnTheServer, 40, "supernova_server");
        TsrGameTests.add("history_server_coverage_smoke_tests_every_mod_recipe_resource_is_decoded", ServerCoverageSmokeTests::everyModRecipeResourceIsDecoded, 120, "supernova_server");
        TsrGameTests.add("history_server_coverage_smoke_tests_every_mod_loot_table_is_registered", ServerCoverageSmokeTests::everyModLootTableIsRegistered, 120, "supernova_server");
        TsrGameTests.add("history_server_coverage_smoke_tests_every_mod_structure_template_is_readable", ServerCoverageSmokeTests::everyModStructureTemplateIsReadable, 120, "supernova_server");
        TsrGameTests.add("history_server_coverage_smoke_tests_every_mod_advancement_and_worldgen_resource_is_registered", ServerCoverageSmokeTests::everyModAdvancementAndWorldgenResourceIsRegistered, 120, "supernova_server");
    }

    public static void everyRegisteredBlockAndItemHasAValidDefault(GameTestHelper helper) {
        List<String> failures = new ArrayList<>();
        int blocks = 0;
        int items = 0;
        for (Block block : BuiltInRegistries.BLOCK) {
            Identifier id = BuiltInRegistries.BLOCK.getKey(block);
            if (!MOD_ID.equals(id.getNamespace())) continue;
            blocks++;
            try {
                var state = block.defaultBlockState();
                if (state == null || state.isAir()) failures.add("block:" + id);
                if (state != null) {
                    var pos = helper.absolutePos(net.minecraft.core.BlockPos.ZERO);
                    state.getShape(helper.getLevel(), pos);
                    if (INVISIBLE_COMPONENT_BLOCKS.contains(id.getPath())) {
                        if (state.getRenderShape() != RenderShape.INVISIBLE) failures.add("component-render-shape:" + id);
                    } else if (state.getMapColor(helper.getLevel(), pos).col == 0 && !MAP_COLOR_EXEMPTIONS.contains(id.getPath())) failures.add("map-color:" + id);
                }
            } catch (Throwable error) {
                failures.add("block:" + id + ":" + error.getClass().getSimpleName());
            }
        }
        for (var item : BuiltInRegistries.ITEM) {
            Identifier id = BuiltInRegistries.ITEM.getKey(item);
            if (!MOD_ID.equals(id.getNamespace())) continue;
            items++;
            try {
                var stack = item.getDefaultInstance();
                if (stack.isEmpty() || stack.getCount() <= 0 || stack.getMaxStackSize() <= 0) failures.add("item:" + id);
            } catch (Throwable error) {
                failures.add("item:" + id + ":" + error.getClass().getSimpleName());
            }
        }
        helper.assertTrue(blocks > 0 && items > 0, "Mod block/item registry must not be empty");
        helper.assertTrue(failures.isEmpty(), "Invalid registered blocks/items count=" + failures.size() + " sample=" + sample(failures));
        ThermalShockReforged.LOGGER.info("Server coverage smoke: validated {} mod blocks and {} mod items", blocks, items);
        helper.succeed();
    }

    public static void everyRegisteredEntityCanBeCreatedAndSaved(GameTestHelper helper) {
        List<String> failures = new ArrayList<>();
        int entities = 0;
        for (var type : BuiltInRegistries.ENTITY_TYPE) {
            Identifier id = BuiltInRegistries.ENTITY_TYPE.getKey(type);
            if (!MOD_ID.equals(id.getNamespace())) continue;
            entities++;
            Entity first = null;
            Entity second = null;
            try {
                first = type.create(helper.getLevel(), EntitySpawnReason.COMMAND);
                if (first == null) {
                    failures.add(id + ":create-null");
                    continue;
                }
                TagValueOutput output = TagValueOutput.createWithContext(ProblemReporter.DISCARDING, helper.getLevel().registryAccess());
                first.saveWithoutId(output);
                second = type.create(helper.getLevel(), EntitySpawnReason.LOAD);
                if (second == null) {
                    failures.add(id + ":reload-create-null");
                    continue;
                }
                second.load(TagValueInput.create(ProblemReporter.DISCARDING, helper.getLevel().registryAccess(), output.buildResult()));
            } catch (Throwable error) {
                failures.add(id + ":" + error.getClass().getSimpleName());
            } finally {
                if (first != null) first.discard();
                if (second != null) second.discard();
            }
        }
        helper.assertTrue(entities > 0, "Mod entity registry must not be empty");
        helper.assertTrue(failures.isEmpty(), "Entity create/save/load failures count=" + failures.size() + " sample=" + sample(failures));
        ThermalShockReforged.LOGGER.info("Server coverage smoke: created and round-tripped {} mod entity types", entities);
        helper.succeed();
    }

    public static void everyModEntityTicksOnTheServer(GameTestHelper helper) {
        List<Entity> spawned = new ArrayList<>();
        int index = 0;
        for (var type : BuiltInRegistries.ENTITY_TYPE) {
            Identifier id = BuiltInRegistries.ENTITY_TYPE.getKey(type);
            if (!MOD_ID.equals(id.getNamespace())) continue;
            Entity entity = type.create(helper.getLevel(), EntitySpawnReason.COMMAND);
            if (entity == null) continue;
            entity.setNoGravity(true);
            if (entity instanceof net.minecraft.world.entity.Mob mob) mob.setNoAi(true);
            entity.snapTo(helper.absolutePos(new net.minecraft.core.BlockPos(2 + index % 12, 2, 2 + index / 12)), 0, 0);
            if (helper.getLevel().addFreshEntity(entity)) spawned.add(entity);
            index++;
        }
        helper.assertTrue(!spawned.isEmpty(), "No mod entities were accepted by the test server");
        helper.runAfterDelay(5, () -> {
            int present = 0;
            List<String> removed = new ArrayList<>();
            for (Entity entity : spawned) {
                if (entity.isRemoved()) removed.add(BuiltInRegistries.ENTITY_TYPE.getKey(entity.getType()).toString());
                else present++;
            }
            ThermalShockReforged.LOGGER.info("Server coverage smoke: ticked {} mod entities for 5 server ticks; {} remain; {} self-removed: {}", spawned.size(), present, removed.size(), sample(removed));
            helper.succeed();
        });
    }

    public static void everyModRecipeResourceIsDecoded(GameTestHelper helper) {
        RecipeManager recipes = helper.getLevel().recipeAccess();
        List<String> failures = new ArrayList<>();
        var resources = helper.getLevel().getServer().getResourceManager().listResources("recipe", id -> MOD_ID.equals(id.getNamespace()) && id.getPath().endsWith(".json"));
        for (Identifier resource : resources.keySet()) {
            String path = resource.getPath();
            String prefix = "recipe/";
            Identifier id = Identifier.fromNamespaceAndPath(resource.getNamespace(), path.substring(prefix.length(), path.length() - 5));
            if (recipes.byKey(ResourceKey.create(Registries.RECIPE, id)).isEmpty()) failures.add(id.toString());
        }
        helper.assertTrue(!resources.isEmpty(), "Mod recipe resources must not be empty");
        helper.assertTrue(failures.isEmpty(), "Recipe resources not decoded count=" + failures.size() + " sample=" + sample(failures));
        ThermalShockReforged.LOGGER.info("Server coverage smoke: verified {} mod recipe resources", resources.size());
        helper.succeed();
    }

    public static void everyModLootTableIsRegistered(GameTestHelper helper) {
        List<String> failures = new ArrayList<>();
        var resources = helper.getLevel().getServer().getResourceManager().listResources("loot_table", id -> MOD_ID.equals(id.getNamespace()) && id.getPath().endsWith(".json"));
        for (Identifier resource : resources.keySet()) {
            String path = resource.getPath();
            String prefix = "loot_table/";
            Identifier id = Identifier.fromNamespaceAndPath(resource.getNamespace(), path.substring(prefix.length(), path.length() - 5));
            var key = ResourceKey.create(Registries.LOOT_TABLE, id);
            LootTable table = helper.getLevel().getServer().reloadableRegistries().getLootTable(key);
            if (table == LootTable.EMPTY) failures.add(id.toString());
        }
        helper.assertTrue(!resources.isEmpty(), "Mod loot table resources must not be empty");
        helper.assertTrue(failures.isEmpty(), "Mod loot table resources not registered count=" + failures.size() + " sample=" + sample(failures));
        ThermalShockReforged.LOGGER.info("Server coverage smoke: verified {} mod loot tables", resources.size());
        helper.succeed();
    }

    public static void everyModStructureTemplateIsReadable(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        List<String> failures = new ArrayList<>();
        var resources = level.getServer().getResourceManager().listResources("structure", id -> MOD_ID.equals(id.getNamespace()) && id.getPath().endsWith(".nbt"));
        for (Identifier resource : resources.keySet()) {
            String path = resource.getPath();
            String prefix = "structure/";
            Identifier id = Identifier.fromNamespaceAndPath(resource.getNamespace(), path.substring(prefix.length(), path.length() - 4));
            StructureTemplate template = level.getStructureManager().get(id).orElse(null);
            if (template == null || template.getSize().getX() <= 0 || template.getSize().getY() <= 0 || template.getSize().getZ() <= 0) failures.add(id.toString());
        }
        helper.assertTrue(!resources.isEmpty(), "Mod structure templates must not be empty");
        helper.assertTrue(failures.isEmpty(), "Unreadable mod structure templates count=" + failures.size() + " sample=" + sample(failures));
        ThermalShockReforged.LOGGER.info("Server coverage smoke: loaded {} mod structure templates", resources.size());
        helper.succeed();
    }

    public static void everyModAdvancementAndWorldgenResourceIsRegistered(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        List<String> failures = new ArrayList<>();
        int advancements = checkResources(level, "advancement", level.getServer().getAdvancements()::get, failures);
        var registries = level.getServer().registryAccess();
        int dimensionTypes = checkResources(level, "dimension_type", id -> registries.lookupOrThrow(Registries.DIMENSION_TYPE).get(ResourceKey.create(Registries.DIMENSION_TYPE, id)).orElse(null), failures);
        int dimensions = checkDimensionResources(level, registries, failures);
        int biomes = checkResources(level, "worldgen/biome", id -> registries.lookupOrThrow(Registries.BIOME).get(ResourceKey.create(Registries.BIOME, id)).orElse(null), failures);
        int configured = checkResources(level, "worldgen/configured_feature", id -> registries.lookupOrThrow(Registries.CONFIGURED_FEATURE).get(ResourceKey.create(Registries.CONFIGURED_FEATURE, id)).orElse(null), failures);
        int placed = checkResources(level, "worldgen/placed_feature", id -> registries.lookupOrThrow(Registries.PLACED_FEATURE).get(ResourceKey.create(Registries.PLACED_FEATURE, id)).orElse(null), failures);
        int structures = checkResources(level, "worldgen/structure", id -> registries.lookupOrThrow(Registries.STRUCTURE).get(ResourceKey.create(Registries.STRUCTURE, id)).orElse(null), failures);
        int structureSets = checkResources(level, "worldgen/structure_set", id -> registries.lookupOrThrow(Registries.STRUCTURE_SET).get(ResourceKey.create(Registries.STRUCTURE_SET, id)).orElse(null), failures);
        helper.assertTrue(failures.isEmpty(), "Advancement/worldgen resources missing from server registries count=" + failures.size() + " sample=" + sample(failures));
        ThermalShockReforged.LOGGER.info("Server coverage smoke: registered {} advancements, {} dimension types, {} dimensions, {} biomes, {} configured/{} placed features, {} structures/{} structure sets", advancements, dimensionTypes, dimensions, biomes, configured, placed, structures, structureSets);
        helper.succeed();
    }

    private static int checkDimensionResources(ServerLevel level, net.minecraft.core.RegistryAccess registries, List<String> failures) {
        var resources = level.getServer().getResourceManager().listResources("dimension", id -> MOD_ID.equals(id.getNamespace()) && id.getPath().endsWith(".json"));
        var ops = RegistryOps.create(JsonOps.INSTANCE, registries);
        for (var entry : resources.entrySet()) {
            try (var reader = entry.getValue().openAsReader()) {
                LevelStem.CODEC.parse(ops, JsonParser.parseReader(reader)).getOrThrow();
            } catch (Throwable error) {
                failures.add("dimension:" + entry.getKey() + ":" + error.getClass().getSimpleName());
            }
        }
        return resources.size();
    }

    private static int checkResources(ServerLevel level, String directory, Registry<?> registry, List<String> failures) {
        return checkResources(level, directory, id -> registry.getOptional(id).orElse(null), failures);
    }

    private static int checkResources(ServerLevel level, String directory, java.util.function.Function<Identifier, ?> lookup, List<String> failures) {
        var resources = level.getServer().getResourceManager().listResources(directory, id -> MOD_ID.equals(id.getNamespace()) && id.getPath().endsWith(".json"));
        for (Identifier resource : resources.keySet()) {
            String path = resource.getPath();
            String prefix = directory + "/";
            Identifier id = Identifier.fromNamespaceAndPath(resource.getNamespace(), path.substring(prefix.length(), path.length() - 5));
            if (lookup.apply(id) == null) failures.add(directory + ":" + id);
        }
        return resources.size();
    }

    private static List<String> sample(List<String> values) { return values.stream().limit(10).toList(); }
    private ServerCoverageSmokeTests() { }
}
