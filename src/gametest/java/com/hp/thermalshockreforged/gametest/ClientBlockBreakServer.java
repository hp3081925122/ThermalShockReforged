package com.hp.thermalshockreforged.gametest;

import com.google.gson.JsonObject;
import com.hp.thermalshockreforged.ThermalShockReforged;
import com.hp.thermalshockreforged.content.MaterialContent;
import com.mojang.brigadier.arguments.StringArgumentType;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.function.Supplier;
import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.AABB;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.RegisterCommandsEvent;

@EventBusSubscriber(modid = ThermalShockReforged.MOD_ID)
public final class ClientBlockBreakServer {
    private static final BlockPos ANCHOR = new BlockPos(4, 120, 4);
    private static final List<Scene> SCENES = List.of(
            new Scene("bismuth_ore", () -> MaterialContent.blocks().get("bismuth_ore").get(), "thermal_shock_reforged:bismuth", "thermal_shock_reforged:block/bismuth_ore_shale", "minecraft:netherite_pickaxe", "pickaxe", "data/thermal_shock_reforged/loot_table/blocks/bismuth_ore.json", "Ore particle model with an unconditional survival drop; its generation can be gated, but this controlled placed block has no world-progression gate."),
            new Scene("ruby_ore", () -> MaterialContent.blocks().get("ruby_ore").get(), "thermal_shock_reforged:rough_ruby", "thermal_shock_reforged:block/shale_ruby_ore_25", "minecraft:netherite_pickaxe", "pickaxe", "data/thermal_shock_reforged/loot_table/blocks/ruby_ore.json", "Ore particle model with the non-silk-touch rough ruby loot path."),
            new Scene("bloodstone_ore", () -> MaterialContent.blocks().get("bloodstone_ore").get(), "thermal_shock_reforged:rough_bloodstone", "thermal_shock_reforged:block/blood_ore_shale", "minecraft:netherite_pickaxe", "pickaxe", "data/thermal_shock_reforged/loot_table/blocks/bloodstone_ore.json", "Ore particle model with the non-silk-touch rough bloodstone loot path."),
            new Scene("shale", () -> MaterialContent.blocks().get("shale").get(), "thermal_shock_reforged:cobbled_shale", "thermal_shock_reforged:block/shale_25", "minecraft:netherite_pickaxe", "pickaxe", "data/thermal_shock_reforged/loot_table/blocks/shale.json", "Stone particle model with a deterministic cobbled shale drop when mined without silk touch."),
            new Scene("permafrost", () -> MaterialContent.blocks().get("permafrost").get(), "thermal_shock_reforged:permafrost", "thermal_shock_reforged:block/permafrost_25b", "minecraft:netherite_pickaxe", "pickaxe", "data/thermal_shock_reforged/loot_table/blocks/permafrost.json", "Stone particle model and self-drop loot table."),
            new Scene("dark_frostrock_block", () -> MaterialContent.blocks().get("dark_frostrock_block").get(), "thermal_shock_reforged:dark_frostrock_block", "thermal_shock_reforged:block/dark_frostrock_block", "minecraft:netherite_pickaxe", "pickaxe", "data/thermal_shock_reforged/loot_table/blocks/dark_frostrock_block.json", "Stone particle model and self-drop loot table.")
    );
    private static String previousSession;
    private static long previousCommandTick = Long.MIN_VALUE;
    private static String currentScene;
    private static UUID currentPlayer;
    private static String session;

    private ClientBlockBreakServer() {
    }

    public static List<String> scenarios() {
        return SCENES.stream().map(Scene::name).toList();
    }

    private static boolean enabled() {
        return ClientIntegrationData.enabled() && Boolean.getBoolean("tsr.clientIntegrationBlockBreak");
    }

    @SubscribeEvent
    public static void registerCommands(RegisterCommandsEvent event) {
        if (!enabled()) return;
        event.getDispatcher().register(Commands.literal("tsr_break")
                .then(Commands.literal("prepare").then(Commands.argument("scene", StringArgumentType.word()).executes(context -> prepare(context.getSource().getPlayerOrException(), StringArgumentType.getString(context, "scene")))))
                .then(Commands.literal("verify").then(Commands.argument("scene", StringArgumentType.word()).executes(context -> verify(context.getSource().getPlayerOrException(), StringArgumentType.getString(context, "scene"))))));
    }

    private static int prepare(ServerPlayer player, String name) {
        JsonObject evidence = base(name, player);
        try {
            Scene scene = scene(name);
            Session active = currentSession();
            if (active.id().equals(previousSession)) ClientIntegrationData.require(active.tick() - previousCommandTick >= 30L, "Block break commands must be spaced at least 30 server ticks apart");
            ClientIntegrationData.require(player.connection != null && !player.connection.getConnection().isMemoryConnection(), "Block break probe requires the real TCP player");
            ClientIntegrationData.require(player.level().dimension().equals(Level.OVERWORLD), "Block break fixture requires Overworld");
            ClientIntegrationData.require(player.containerMenu == player.inventoryMenu, "Close the screen before block break preparation");
            ServerLevel level = player.level().getServer().getLevel(Level.OVERWORLD);
            ClientIntegrationData.require(level != null, "Overworld is unavailable");
            preparePlatform(level);
            var removedEntities = level.getEntitiesOfClass(Entity.class, new AABB(ANCHOR).inflate(12), entity -> !(entity instanceof Player));
            removedEntities.forEach(Entity::discard);
            level.setBlock(ANCHOR, scene.block().defaultBlockState(), 3);
            ClientIntegrationData.require(level.getBlockState(ANCHOR).is(scene.block()), "Registered production block was not placed");
            ClientIntegrationData.require(level.getBlockState(ANCHOR).is(BlockTags.MINEABLE_WITH_PICKAXE), "Fixture block is not in the production pickaxe mining tag");
            float hardness = level.getBlockState(ANCHOR).getDestroySpeed(level, ANCHOR);
            ClientIntegrationData.require(hardness > 0.0F, "Fixture block is not normally mineable: hardness=" + hardness);
            player.getInventory().clearContent();
            for (EquipmentSlot slot : List.of(EquipmentSlot.OFFHAND, EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET)) player.setItemSlot(slot, ItemStack.EMPTY);
            player.getInventory().setSelectedSlot(0);
            player.setGameMode(GameType.SURVIVAL);
            player.setItemInHand(net.minecraft.world.InteractionHand.MAIN_HAND, new ItemStack(Items.NETHERITE_PICKAXE));
            ClientIntegrationData.require(player.teleportTo(level, 4.5D, 120.0D, 7.0D, Set.of(), 180.0F, 20.5F, true), "Cannot position connected player at block fixture");
            previousSession = active.id();
            previousCommandTick = active.tick();
            currentScene = name;
            currentPlayer = player.getUUID();
            session = active.id();
            evidence.addProperty("status", "passed");
            evidence.addProperty("session", active.id());
            evidence.addProperty("serverTick", active.tick());
            evidence.addProperty("dimension", level.dimension().identifier().toString());
            evidence.addProperty("x", ANCHOR.getX());
            evidence.addProperty("y", ANCHOR.getY());
            evidence.addProperty("z", ANCHOR.getZ());
            evidence.addProperty("blockId", BuiltInRegistries.BLOCK.getKey(scene.block()).toString());
            evidence.addProperty("expectedDropId", scene.dropId());
            evidence.addProperty("toolId", scene.toolId());
            evidence.addProperty("requiredToolTag", scene.toolTag());
            evidence.addProperty("blockHasPickaxeMiningTag", true);
            evidence.addProperty("hardness", hardness);
            evidence.addProperty("particleTexture", scene.particleTexture());
            evidence.addProperty("lootTablePath", scene.lootTablePath());
            evidence.addProperty("selectionReason", scene.selectionReason());
            evidence.addProperty("gameMode", "survival");
            evidence.addProperty("removedFixtureEntities", removedEntities.size());
            evidence.addProperty("fixture", "Isolated TCP scene with a stone platform, cleared target volume, one production block, and a real netherite pickaxe; no altar or advancement gate is involved.");
            evidence.addProperty("breakPath", "Client MultiPlayerGameMode.startDestroyBlock and continueDestroyBlock emit native player-action packets; server verification observes the resulting AIR state and ItemEntity drop.");
            evidence.addProperty("naturalWorldGeneration", false);
        } catch (Throwable failure) {
            evidence.addProperty("status", "failed");
            evidence.addProperty("error", failure.toString());
            ThermalShockReforged.LOGGER.error("Client block break fixture preparation failed: scene={}", name, failure);
        }
        ClientIntegrationData.write("blockbreak-" + name + "-prepared.json", evidence);
        return "passed".equals(evidence.get("status").getAsString()) ? 1 : 0;
    }

    private static int verify(ServerPlayer player, String name) {
        JsonObject evidence = base(name, player);
        try {
            Scene scene = scene(name);
            Session active = currentSession();
            if (active.id().equals(previousSession)) ClientIntegrationData.require(active.tick() - previousCommandTick >= 30L, "Block break commands must be spaced at least 30 server ticks apart");
            ClientIntegrationData.require(name.equals(currentScene) && player.getUUID().equals(currentPlayer) && active.id().equals(session), "Block break verification belongs to a stale fixture or player");
            ServerLevel level = player.level().getServer().getLevel(Level.OVERWORLD);
            ClientIntegrationData.require(level != null && level.getBlockState(ANCHOR).isAir(), "Server has not observed the real block break");
            var drops = level.getEntitiesOfClass(ItemEntity.class, new AABB(ANCHOR).inflate(5));
            var matching = drops.stream().filter(item -> BuiltInRegistries.ITEM.getKey(item.getItem().getItem()).toString().equals(scene.dropId())).toList();
            ClientIntegrationData.require(!matching.isEmpty(), "Expected production loot item entity was not observed: " + scene.dropId());
            var dropRows = new com.google.gson.JsonArray();
            for (ItemEntity item : drops) {
                JsonObject row = new JsonObject();
                row.addProperty("itemId", BuiltInRegistries.ITEM.getKey(item.getItem().getItem()).toString());
                row.addProperty("count", item.getItem().getCount());
                row.addProperty("entityId", item.getId());
                row.addProperty("uuid", item.getUUID().toString());
                row.addProperty("x", item.getX());
                row.addProperty("y", item.getY());
                row.addProperty("z", item.getZ());
                dropRows.add(row);
            }
            evidence.addProperty("status", "passed");
            evidence.addProperty("session", active.id());
            evidence.addProperty("serverTick", active.tick());
            evidence.addProperty("dimension", level.dimension().identifier().toString());
            evidence.addProperty("blockId", BuiltInRegistries.BLOCK.getKey(scene.block()).toString());
            evidence.addProperty("blockIsAir", true);
            evidence.addProperty("expectedDropId", scene.dropId());
            evidence.addProperty("matchingExpectedDropEntities", matching.size());
            evidence.add("dropEntities", dropRows);
            evidence.addProperty("verification", "Actual server-side survival break result and spawned item entity; no direct server destroy call was used by the probe.");
            previousSession = active.id();
            previousCommandTick = active.tick();
        } catch (Throwable failure) {
            evidence.addProperty("status", "failed");
            evidence.addProperty("error", failure.toString());
            ThermalShockReforged.LOGGER.error("Client block break result verification failed: scene={}", name, failure);
        }
        ClientIntegrationData.write("blockbreak-" + name + "-verified.json", evidence);
        return "passed".equals(evidence.get("status").getAsString()) ? 1 : 0;
    }

    private static void preparePlatform(ServerLevel level) {
        for (int x = 0; x <= 8; x++) for (int z = 1; z <= 9; z++) {
            level.setBlock(new BlockPos(x, 119, z), Blocks.STONE.defaultBlockState(), 3);
            for (int y = 120; y <= 126; y++) level.setBlock(new BlockPos(x, y, z), Blocks.AIR.defaultBlockState(), 3);
        }
        for (BlockPos light : List.of(new BlockPos(1, 126, 2), new BlockPos(7, 126, 2), new BlockPos(1, 126, 8), new BlockPos(7, 126, 8))) level.setBlock(light, Blocks.SEA_LANTERN.defaultBlockState(), 3);
    }

    private static Scene scene(String name) {
        return SCENES.stream().filter(value -> value.name().equals(name)).findFirst().orElseThrow(() -> new IllegalArgumentException("Unknown block break scene: " + name));
    }

    private static JsonObject base(String name, ServerPlayer player) {
        JsonObject result = new JsonObject();
        result.addProperty("scenario", name);
        result.addProperty("player", player.getUUID().toString());
        return result;
    }

    private static Session currentSession() {
        JsonObject ready = ClientIntegrationData.read("server-ready.json");
        JsonObject baseline = ClientIntegrationData.read("client-results.json");
        JsonObject clock = ClientIntegrationData.read("server-clock.json");
        ClientIntegrationData.require("ready".equals(ready.get("status").getAsString()), "TCP integration server is not ready");
        ClientIntegrationData.require("passed".equals(baseline.get("status").getAsString()) && baseline.get("completedScenarios").getAsInt() == 7, "Default TCP baseline seven has not passed");
        String id = ready.get("session").getAsString();
        ClientIntegrationData.require(id.equals(baseline.get("session").getAsString()) && id.equals(clock.get("session").getAsString()), "Baseline, ready marker and server clock sessions differ");
        return new Session(id, clock.get("serverTick").getAsLong());
    }

    private record Scene(String name, Supplier<Block> blockSupplier, String dropId, String particleTexture, String toolId, String toolTag, String lootTablePath, String selectionReason) {
        private Block block() { return blockSupplier.get(); }
    }
    private record Session(String id, long tick) { }
}
