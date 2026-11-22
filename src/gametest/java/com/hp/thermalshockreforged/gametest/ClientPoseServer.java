package com.hp.thermalshockreforged.gametest;

import com.google.gson.JsonObject;
import com.hp.thermalshockreforged.ThermalShockReforged;
import com.mojang.brigadier.arguments.StringArgumentType;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.chunk.status.ChunkStatus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.RegisterCommandsEvent;

@EventBusSubscriber(modid = ThermalShockReforged.MOD_ID)
public final class ClientPoseServer {
    private static final List<String> HELD_ITEMS = List.of(
            "blinding_lantern", "bloodstone_halberd", "buzzsaw", "carbor_gl", "carborundum_halberd",
            "carborundum_spear", "cobalt_thermoblaster", "core_eject_flare_gun", "flintsteel_rifle",
            "hailfall", "hi_volt_lance", "jelly_umbrella", "legendary_jelly_umbrella", "liteprism_laser",
            "magnetic_crossbow", "net_launcher", "piston_gauntlet", "ruby_spear", "speleothem",
            "sylvia_rifle", "the_judge", "thistle_bow", "titanium_mace", "volcano", "zap_gauntlet",
            "zenith_crossbow", "zenith_thermoblaster");
    private static final List<String> ARMOR_ITEMS = List.of(
            "sentry_helmet", "sentry_chestplate", "dev_scarf_chestplate", "corrial_diving_helmet_helmet",
            "strangesilk_armour_helmet", "strangesilk_armour_chestplate", "rustsilk_armour_helmet",
            "rustsilk_armour_chestplate", "ruby_armor_helmet", "ruby_armor_chestplate", "ruby_armor_leggings",
            "ruby_armor_boots", "carborundum_armor_helmet", "carborundum_armor_chestplate",
            "carborundum_armor_leggings", "carborundum_armor_boots", "ruby_zenith_armor_helmet",
            "ruby_zenith_armor_chestplate", "ruby_zenith_armor_leggings", "ruby_zenith_armor_boots",
            "blood_zenith_armor_helmet", "blood_zenith_armor_chestplate", "blood_zenith_armor_leggings",
            "blood_zenith_armor_boots", "blood_armor_helmet", "blood_armor_chestplate", "blood_armor_leggings",
            "blood_armor_boots", "propulsion_boots_boots", "silenced_armor_helmet", "silenced_armor_chestplate");
    private static final List<String> HAND_SUFFIXES = List.of(
            "_main_idle_first", "_main_idle_third_front", "_main_using_first", "_main_using_third_front",
            "_off_idle_first", "_off_idle_third_front", "_off_using_first", "_off_using_third_front");
    private static final int FIXTURE_X = 2048;
    private static final int FIXTURE_Y = 120;
    private static final int FIXTURE_Z = 2048;
    private static List<String> SCENARIOS;
    private static String previousSession;
    private static long previousCommandTick = Long.MIN_VALUE;

    private ClientPoseServer() {
    }

    public static List<String> scenarios() {
        if (SCENARIOS == null) {
            var result = new ArrayList<String>();
            for (String item : HELD_ITEMS) for (String suffix : HAND_SUFFIXES) result.add("held_" + item + suffix);
            for (String item : ARMOR_ITEMS) result.add("armor_" + item + "_third_front");
            String filter = System.getProperty("tsr.clientIntegrationPoseFilter", "all");
            ClientIntegrationData.require(List.of("all", "held", "armor").contains(filter), "Unsupported pose filter: " + filter);
            if (!filter.equals("all")) result.removeIf(scene -> !scene.startsWith(filter + "_"));
            SCENARIOS = List.copyOf(result);
        }
        return SCENARIOS;
    }

    private static boolean enabled() {
        return ClientIntegrationData.enabled() && Boolean.getBoolean("tsr.clientIntegrationPose");
    }

    @SubscribeEvent
    public static void registerCommands(RegisterCommandsEvent event) {
        if (!enabled()) return;
        event.getDispatcher().register(Commands.literal("tsr_pose")
                .then(Commands.literal("prepare")
                        .then(Commands.argument("scene", StringArgumentType.word())
                                .executes(context -> prepare(context.getSource().getPlayerOrException(), StringArgumentType.getString(context, "scene"))))));
    }

    private static int prepare(ServerPlayer player, String scene) {
        JsonObject evidence = new JsonObject();
        evidence.addProperty("scene", scene);
        try {
            ClientIntegrationData.require(enabled(), "Pose integration probe is not enabled");
            ClientIntegrationData.require(scenarios().contains(scene), "Unsupported pose scene: " + scene);
            Session session = currentSession();
            if (session.id().equals(previousSession)) ClientIntegrationData.require(session.tick() - previousCommandTick >= 30L, "Pose server commands must be spaced at least 30 server ticks apart");
            ServerLevel level = player.level().getServer().getLevel(Level.OVERWORLD);
            ClientIntegrationData.require(level != null && player.level() == level, "Pose fixture requires the Overworld");
            ClientIntegrationData.require(player.connection != null && !player.connection.getConnection().isMemoryConnection(), "Pose probe requires the real TCP player");
            ClientIntegrationData.require(player.level().getServer().getPlayerList().getPlayer(player.getUUID()) == player, "Command source is not a connected server player");
            ClientIntegrationData.require(player.containerMenu == player.inventoryMenu, "Close the screen before preparing a pose scene");
            ClientIntegrationData.require(!player.isUsingItem(), "The server player must be idle before preparing a pose scene");

            preparePlatform(level);
            player.getInventory().clearContent();
            for (EquipmentSlot slot : List.of(EquipmentSlot.MAINHAND, EquipmentSlot.OFFHAND, EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET)) player.setItemSlot(slot, ItemStack.EMPTY);
            player.getInventory().setSelectedSlot(0);

            boolean armorScene = scene.startsWith("armor_");
            String itemPath = armorScene ? scene.substring("armor_".length(), scene.length() - "_third_front".length()) : heldItemPath(scene);
            Identifier itemId = Identifier.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, itemPath);
            var item = BuiltInRegistries.ITEM.getValue(itemId);
            ClientIntegrationData.require(item != null && !item.equals(net.minecraft.world.item.Items.AIR), "Pose item is not registered: " + itemId);
            ItemStack stack = item.getDefaultInstance();
            ClientIntegrationData.require(!stack.isEmpty(), "Pose item produced an empty default stack: " + itemId);
            String handName = "none";
            String useState = "none";
            String view = "third_person_front";
            String equipmentSlot = "none";
            if (armorScene) {
                EquipmentSlot slot = player.getEquipmentSlotForItem(stack);
                ClientIntegrationData.require(List.of(EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET).contains(slot), "Armor fixture item does not resolve to a humanoid equipment slot: " + itemId + "/" + slot);
                player.setItemSlot(slot, stack);
                equipmentSlot = slot.getName();
            } else {
                String suffix = HAND_SUFFIXES.stream().filter(scene::endsWith).findFirst().orElseThrow(() -> new IllegalArgumentException("Pose scene suffix is invalid: " + scene));
                String body = scene.substring("held_".length(), scene.length() - suffix.length());
                ClientIntegrationData.require(body.equals(itemPath), "Pose scene item path does not match parsed item: " + scene);
                handName = suffix.startsWith("_main") ? "main" : "off";
                useState = suffix.contains("_using_") ? "using" : "idle";
                view = suffix.endsWith("_first") ? "first_person" : "third_person_front";
                InteractionHand hand = handName.equals("main") ? InteractionHand.MAIN_HAND : InteractionHand.OFF_HAND;
                player.setItemInHand(hand, stack);
            }
            ClientIntegrationData.require(player.teleportTo(level, FIXTURE_X + 0.5D, FIXTURE_Y, FIXTURE_Z + 0.5D, java.util.Set.of(), 0.0F, 0.0F, true), "Cannot place the connected client on the pose fixture");
            player.setYRot(0.0F);
            player.setXRot(0.0F);
            previousSession = session.id();
            previousCommandTick = session.tick();

            evidence.addProperty("status", "passed");
            evidence.addProperty("session", session.id());
            evidence.addProperty("serverTick", session.tick());
            evidence.addProperty("dimension", level.dimension().identifier().toString());
            evidence.addProperty("itemId", itemId.toString());
            evidence.addProperty("sceneKind", armorScene ? "single_armor_piece_third_person_front" : "held_item_render_fixture");
            evidence.addProperty("hand", handName);
            evidence.addProperty("useState", useState);
            evidence.addProperty("view", view);
            evidence.addProperty("equipmentSlot", equipmentSlot);
            evidence.addProperty("x", FIXTURE_X);
            evidence.addProperty("y", FIXTURE_Y);
            evidence.addProperty("z", FIXTURE_Z);
            evidence.addProperty("playerYaw", 0.0F);
            evidence.addProperty("playerPitch", 0.0F);
            evidence.addProperty("fixture", "Dedicated TCP integration world platform with a cleared 13x13x17 air volume, 13x13 stone floor and overhead sea-lantern lighting");
            evidence.addProperty("clearedVolumeMinX", FIXTURE_X - 6);
            evidence.addProperty("clearedVolumeMinY", FIXTURE_Y);
            evidence.addProperty("clearedVolumeMinZ", FIXTURE_Z - 6);
            evidence.addProperty("clearedVolumeMaxX", FIXTURE_X + 6);
            evidence.addProperty("clearedVolumeMaxY", FIXTURE_Y + 16);
            evidence.addProperty("clearedVolumeMaxZ", FIXTURE_Z + 6);
            evidence.addProperty("usingStateConstruction", armorScene ? "not_applicable" : "Client LocalPlayer.startUsingItem or stopUsingItem after item synchronization; no OS input and no production use action is invoked");
            evidence.addProperty("usingStateLimit", armorScene ? "not_applicable" : "Forced local render state only; does not prove natural use authorization, ammunition, cooldown, firing, or release behavior");
            evidence.addProperty("geckoAnimationState", armorScene ? "default synchronized armor stack; no geckoAnim component injected" : "not_applicable");
            evidence.addProperty("geckoAnimationLimit", armorScene ? "Validates one-piece equipped default rendering only; no production writer was found for geckoAnim, so this is not evidence of natural special-clip triggering. Full animation resource catalogue remains a separate Render Preview result." : "not_applicable");
            evidence.addProperty("scenarioCount", scenarios().size());
        } catch (Throwable failure) {
            evidence.addProperty("status", "failed");
            evidence.addProperty("error", failure.toString());
            ThermalShockReforged.LOGGER.error("Client pose preparation failed: scene={}", scene, failure);
        }
        try {
            ClientIntegrationData.write("pose-" + scene + "-prepared.json", evidence);
        } catch (Throwable failure) {
            ThermalShockReforged.LOGGER.error("Client pose preparation evidence publication failed: scene={}", scene, failure);
            return 0;
        }
        return "passed".equals(evidence.get("status").getAsString()) ? 1 : 0;
    }

    private static String heldItemPath(String scene) {
        ClientIntegrationData.require(scene.startsWith("held_"), "Pose scene must start with held_: " + scene);
        String suffix = HAND_SUFFIXES.stream().filter(scene::endsWith).findFirst().orElseThrow(() -> new IllegalArgumentException("Pose scene suffix is invalid: " + scene));
        return scene.substring("held_".length(), scene.length() - suffix.length());
    }

    private static void preparePlatform(ServerLevel level) {
        int minX = FIXTURE_X - 6;
        int maxX = FIXTURE_X + 6;
        int minZ = FIXTURE_Z - 6;
        int maxZ = FIXTURE_Z + 6;
        for (int chunkX = minX >> 4; chunkX <= maxX >> 4; chunkX++) for (int chunkZ = minZ >> 4; chunkZ <= maxZ >> 4; chunkZ++) {
            level.getChunk(chunkX, chunkZ, ChunkStatus.FULL, true);
        }
        int clearTopY = FIXTURE_Y + 16;
        for (int x = minX; x <= maxX; x++) for (int z = minZ; z <= maxZ; z++) for (int y = FIXTURE_Y; y <= clearTopY; y++) {
            BlockPos pos = new BlockPos(x, y, z);
            if (!level.isEmptyBlock(pos)) level.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
        }
        for (int x = minX; x <= maxX; x++) for (int z = minZ; z <= maxZ; z++) {
            BlockPos pos = new BlockPos(x, FIXTURE_Y - 1, z);
            if (!level.getBlockState(pos).is(Blocks.SMOOTH_STONE)) level.setBlock(pos, Blocks.SMOOTH_STONE.defaultBlockState(), 3);
        }
        for (int x = minX; x <= maxX; x += 4) for (int z = minZ; z <= maxZ; z += 4) {
            BlockPos pos = new BlockPos(x, FIXTURE_Y + 7, z);
            if (!level.getBlockState(pos).is(Blocks.SEA_LANTERN)) level.setBlock(pos, Blocks.SEA_LANTERN.defaultBlockState(), 3);
        }
    }

    private static Session currentSession() {
        JsonObject ready = ClientIntegrationData.read("server-ready.json");
        JsonObject clock = ClientIntegrationData.read("server-clock.json");
        ClientIntegrationData.require("ready".equals(ready.get("status").getAsString()), "TCP integration server is not ready");
        String session = ready.get("session").getAsString();
        ClientIntegrationData.require(session.equals(clock.get("session").getAsString()), "Pose server clock belongs to another session");
        return new Session(session, clock.get("serverTick").getAsLong());
    }

    private record Session(String id, long tick) {
    }
}
