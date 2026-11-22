package com.hp.thermalshockreforged.gametest;

import com.hp.thermalshockreforged.ThermalShockReforged;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.Codec;
import io.netty.buffer.Unpooled;
import java.util.List;
import java.util.Map;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;

public final class DataServerRegressionTests {
    private DataServerRegressionTests() { }

    private static Map<Identifier, net.minecraft.server.packs.resources.Resource> resources(GameTestHelper helper, String directory, String extension) {
        return helper.getLevel().getServer().getResourceManager().listResources(directory,
                id -> id.getNamespace().equals(ThermalShockReforged.MOD_ID) && id.getPath().endsWith(extension));
    }

    private static Identifier key(Identifier resource, String directory, String extension) {
        return Identifier.fromNamespaceAndPath(resource.getNamespace(), resource.getPath().substring(directory.length() + 1, resource.getPath().length() - extension.length()));
    }

    public static void everyRecipeLoadsAndRoundTrips(GameTestHelper helper) {
        var recipes = resources(helper, "recipe", ".json");
        helper.assertTrue(!recipes.isEmpty(), "No recipe resources were discovered");
        var ops = helper.getLevel().registryAccess().createSerializationContext(JsonOps.INSTANCE);
        for (var location : recipes.keySet()) {
            var id = key(location, "recipe", ".json");
            var holder = helper.getLevel().recipeAccess().byKey(ResourceKey.create(Registries.RECIPE, id));
            helper.assertTrue(holder.isPresent(), "Recipe resource was silently dropped during loading: " + id);
            var recipe = holder.orElseThrow().value();
            var encoded = Recipe.CODEC.encodeStart(ops, recipe).getOrThrow(message -> new IllegalStateException(id + ": " + message));
            var decoded = Recipe.CODEC.parse(ops, encoded).getOrThrow(message -> new IllegalStateException(id + ": " + message));
            helper.assertTrue(Recipe.CODEC.encodeStart(ops, decoded).getOrThrow().equals(encoded), "Recipe JSON round trip changed fields: " + id);
            var buffer = new RegistryFriendlyByteBuf(Unpooled.buffer(), helper.getLevel().registryAccess());
            try {
                Recipe.STREAM_CODEC.encode(buffer, recipe);
                var received = Recipe.STREAM_CODEC.decode(buffer);
                helper.assertTrue(buffer.readableBytes() == 0, "Recipe network codec left unread bytes: " + id);
                helper.assertTrue(received.getType() == recipe.getType() && received.getSerializer() == recipe.getSerializer(), "Recipe network round trip changed type: " + id);
                if (id.getNamespace().equals(BuiltInRegistries.RECIPE_SERIALIZER.getKey(recipe.getSerializer()).getNamespace())) {
                    helper.assertTrue(Recipe.CODEC.encodeStart(ops, received).getOrThrow().equals(encoded), "Custom recipe network round trip changed fields: " + id);
                } else {
                    var secondBuffer = new RegistryFriendlyByteBuf(Unpooled.buffer(), helper.getLevel().registryAccess());
                    try {
                        Recipe.STREAM_CODEC.encode(secondBuffer, received);
                        helper.assertTrue(buffer.writerIndex() == secondBuffer.writerIndex(), "Vanilla recipe network round trip changed payload length: " + id);
                        for (int index = 0; index < buffer.writerIndex(); index++) helper.assertTrue(buffer.getByte(index) == secondBuffer.getByte(index), "Vanilla recipe network round trip changed payload: " + id);
                    } finally {
                        secondBuffer.release();
                    }
                }
            } finally {
                buffer.release();
            }
        }
        ThermalShockReforged.LOGGER.info("Server regression recipes loaded and JSON/network round-tripped: {}", recipes.size());
        helper.succeed();
    }

    public static void everyStructureAndAdvancementLoads(GameTestHelper helper) {
        var templates = resources(helper, "structure", ".nbt");
        helper.assertTrue(templates.size() > 100, "Structure resource inventory unexpectedly small: " + templates.size());
        for (var location : templates.keySet()) {
            var id = key(location, "structure", ".nbt");
            var template = helper.getLevel().getServer().getStructureManager().get(id);
            helper.assertTrue(template.isPresent(), "Structure template could not load: " + id);
            var size = template.orElseThrow().getSize();
            helper.assertTrue(size.getX() > 0 && size.getY() > 0 && size.getZ() > 0, "Structure has invalid dimensions: " + id);
        }
        var advancements = resources(helper, "advancement", ".json");
        helper.assertTrue(!advancements.isEmpty(), "No advancement resources were discovered");
        for (var location : advancements.keySet()) {
            var id = key(location, "advancement", ".json");
            helper.assertTrue(helper.getLevel().getServer().getAdvancements().get(id) != null, "Advancement resource did not load: " + id);
        }
        ThermalShockReforged.LOGGER.info("Server regression structures decoded: {}; advancements registered: {}", templates.size(), advancements.size());
        helper.succeed();
    }

    public static void everyLootTableLoadsAndRolls(GameTestHelper helper) {
        var resources = resources(helper, "loot_table", ".json");
        var registry = helper.getLevel().getServer().reloadableRegistries().lookup().lookupOrThrow(Registries.LOOT_TABLE);
        var cow = helper.spawnWithNoFreeWill(EntityType.COW, 3, 2, 3);
        var origin = cow.position();
        for (var location : resources.keySet()) {
            var id = key(location, "loot_table", ".json");
            var holder = registry.get(ResourceKey.create(Registries.LOOT_TABLE, id));
            helper.assertTrue(holder.isPresent(), "Loot table resource failed to load: " + id);
            var table = holder.orElseThrow().value();
            var params = new LootParams.Builder(helper.getLevel())
                    .withParameter(LootContextParams.ORIGIN, origin)
                    .withParameter(LootContextParams.THIS_ENTITY, cow)
                    .withParameter(LootContextParams.DAMAGE_SOURCE, helper.getLevel().damageSources().genericKill())
                    .withParameter(LootContextParams.ATTACKING_ENTITY, cow)
                    .withParameter(LootContextParams.DIRECT_ATTACKING_ENTITY, cow)
                    .withParameter(LootContextParams.BLOCK_STATE, Blocks.STONE.defaultBlockState())
                    .withParameter(LootContextParams.TOOL, Items.DIAMOND_PICKAXE.getDefaultInstance())
                    .withParameter(LootContextParams.EXPLOSION_RADIUS, 1F)
                    .create(new net.minecraft.util.context.ContextKeySet.Builder()
                            .required(LootContextParams.ORIGIN).required(LootContextParams.THIS_ENTITY)
                            .required(LootContextParams.DAMAGE_SOURCE).required(LootContextParams.ATTACKING_ENTITY)
                            .required(LootContextParams.DIRECT_ATTACKING_ENTITY).required(LootContextParams.BLOCK_STATE)
                            .required(LootContextParams.TOOL).required(LootContextParams.EXPLOSION_RADIUS).build());
            for (int seed = 1; seed <= 4; seed++) {
                for (var stack : table.getRandomItems(params, seed)) {
                    helper.assertTrue(stack.isEmpty() || stack.getCount() > 0 && stack.getCount() <= stack.getMaxStackSize(),
                            "Loot roll produced an invalid stack: " + id + " -> " + stack);
                }
            }
        }
        helper.assertTrue(!resources.isEmpty(), "No mod loot resources were discovered");
        ThermalShockReforged.LOGGER.info("Server regression loot tables loaded and rolled with four seeds: {}", resources.size());
        helper.succeed();
    }

    public static void everyItemAndBlockStateSerializes(GameTestHelper helper) {
        var ops = helper.getLevel().registryAccess().createSerializationContext(JsonOps.INSTANCE);
        int items = 0;
        int blocks = 0;
        int states = 0;
        for (var item : BuiltInRegistries.ITEM) {
            var id = BuiltInRegistries.ITEM.getKey(item);
            if (!id.getNamespace().equals(ThermalShockReforged.MOD_ID)) continue;
            var stack = item.getDefaultInstance();
            helper.assertTrue(!stack.isEmpty(), "Registered item produced an empty default stack: " + id);
            var encoded = ItemStack.CODEC.encodeStart(ops, stack).getOrThrow(message -> new IllegalStateException(id + ": " + message));
            var restored = ItemStack.CODEC.parse(ops, encoded).getOrThrow();
            helper.assertTrue(ItemStack.isSameItemSameComponents(stack, restored) && stack.getCount() == restored.getCount(), "Item components did not survive serialization: " + id);
            stack.getTooltipLines(net.minecraft.world.item.Item.TooltipContext.of(helper.getLevel()), null, net.minecraft.world.item.TooltipFlag.NORMAL);
            items++;
        }
        for (var block : BuiltInRegistries.BLOCK) {
            var id = BuiltInRegistries.BLOCK.getKey(block);
            if (!id.getNamespace().equals(ThermalShockReforged.MOD_ID)) continue;
            for (var state : block.getStateDefinition().getPossibleStates()) {
                var encoded = net.minecraft.world.level.block.state.BlockState.CODEC.encodeStart(ops, state).getOrThrow();
                helper.assertTrue(net.minecraft.world.level.block.state.BlockState.CODEC.parse(ops, encoded).getOrThrow() == state, "Block state did not survive serialization: " + id + " " + state);
                states++;
            }
            blocks++;
        }
        helper.assertTrue(items > 0 && blocks > 0, "Runtime item/block registry inventory is empty");
        ThermalShockReforged.LOGGER.info("Server regression serialized items: {}; blocks: {}; block states: {}", items, blocks, states);
        helper.succeed();
    }

    public static void everyBlockEntityPreservesPersistentDataAndInventory(GameTestHelper helper) {
        int count = 0;
        int containers = 0;
        var position = helper.absolutePos(new BlockPos(3, 2, 3));
        var registries = helper.getLevel().registryAccess();
        for (var block : BuiltInRegistries.BLOCK) {
            var id = BuiltInRegistries.BLOCK.getKey(block);
            if (!id.getNamespace().equals(ThermalShockReforged.MOD_ID) || !(block instanceof net.minecraft.world.level.block.EntityBlock factory)) continue;
            var state = block.defaultBlockState();
            var entity = factory.newBlockEntity(position, state);
            if (entity == null) continue;
            entity.setLevel(helper.getLevel());
            entity.getPersistentData().putString("tsr_test_marker", id.toString());
            entity.getPersistentData().putInt("tsr_test_counter", 37);
            if (entity instanceof net.minecraft.world.Container inventory) {
                for (int slot = 0; slot < inventory.getContainerSize(); slot++) {
                    inventory.setItem(slot, new ItemStack(slot % 2 == 0 ? Items.DIAMOND : Items.IRON_INGOT, Math.min(slot + 1, 64)));
                }
                if (inventory.getContainerSize() > 0) containers++;
            }
            var tag = entity.saveWithFullMetadata(registries);
            var restored = net.minecraft.world.level.block.entity.BlockEntity.loadStatic(position, state, tag, registries);
            helper.assertTrue(restored != null && restored.getType() == entity.getType(), "Block entity could not reconstruct from full save: " + id);
            helper.assertTrue(restored.getPersistentData().getStringOr("tsr_test_marker", "").equals(id.toString())
                    && restored.getPersistentData().getIntOr("tsr_test_counter", 0) == 37, "Block entity lost persistent data: " + id);
            if (entity instanceof net.minecraft.world.Container original) {
                helper.assertTrue(restored instanceof net.minecraft.world.Container, "Loaded container lost its inventory: " + id);
                var loaded = (net.minecraft.world.Container) restored;
                helper.assertTrue(loaded.getContainerSize() == original.getContainerSize(), "Container slot count changed: " + id);
                for (int slot = 0; slot < original.getContainerSize(); slot++) {
                    var first = original.getItem(slot);
                    var second = loaded.getItem(slot);
                    helper.assertTrue(ItemStack.isSameItemSameComponents(first, second) && first.getCount() == second.getCount(), "Container save/load changed slot " + slot + ": " + id);
                }
            }
            count++;
        }
        helper.assertTrue(count > 0 && containers > 0, "No block entities or containers were tested");
        ThermalShockReforged.LOGGER.info("Server regression saved/restored block entities: {}; containers with every slot filled: {}", count, containers);
        helper.succeed();
    }
}
