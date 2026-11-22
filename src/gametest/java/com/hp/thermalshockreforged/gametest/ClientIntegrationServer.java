package com.hp.thermalshockreforged.gametest;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.hp.thermalshockreforged.ThermalShockReforged;
import com.hp.thermalshockreforged.content.centrifuge.CentrifugeMenu;
import com.hp.thermalshockreforged.content.decor.geo.FabricatorMenu;
import com.hp.thermalshockreforged.content.fabrication.FabricationRecipe;
import com.hp.thermalshockreforged.content.fabrication.FabricatorPage;
import com.hp.thermalshockreforged.content.npc.laut.LautContent;
import com.hp.thermalshockreforged.content.npc.laut.LautEntity;
import com.hp.thermalshockreforged.content.npc.laut.LautPage;
import com.hp.thermalshockreforged.entity.kofoni.KofoniContent;
import com.hp.thermalshockreforged.entity.kofoni.KofoniPage;
import com.hp.thermalshockreforged.world.AltarProgressPayload;
import com.hp.thermalshockreforged.world.OreChoicePayload;
import com.hp.thermalshockreforged.world.WorldProgressData;
import com.mojang.brigadier.arguments.StringArgumentType;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.server.ServerStartedEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;

@EventBusSubscriber(modid = ThermalShockReforged.MOD_ID)
public final class ClientIntegrationServer {
    private static final BlockPos ANCHOR = new BlockPos(4, 120, 4);
    private static final List<Entity> ACTORS = new ArrayList<>();
    private static final String SESSION = UUID.randomUUID().toString();
    private static String current;
    private static FabricationRecipe fabrication;
    private static LautEntity laut;
    private static long serverTicks;
    private static long clockWriteFailures;

    private ClientIntegrationServer() {
    }

    @SubscribeEvent
    public static void tick(ServerTickEvent.Post event) {
        if (!ClientIntegrationData.enabled()) return;
        serverTicks++;
        if (serverTicks % 10 != 0) return;
        var clock = new JsonObject();
        clock.addProperty("session", SESSION);
        clock.addProperty("serverTick", serverTicks);
        clock.addProperty("writeFailures", clockWriteFailures);
        try {
            ClientIntegrationData.write("server-clock.json", clock);
        } catch (IllegalStateException failure) {
            clockWriteFailures++;
            if (clockWriteFailures < 3 || clockWriteFailures % 20 == 0) ThermalShockReforged.LOGGER.debug("Client integration clock publication deferred: serverTick={}, failures={}", serverTicks, clockWriteFailures, failure);
        }
    }

    @SubscribeEvent
    public static void started(ServerStartedEvent event) {
        if (!ClientIntegrationData.enabled()) return;
        var result = new JsonObject();
        result.addProperty("session", SESSION);
        result.addProperty("status", "ready");
        result.addProperty("transport", "TCP socket; vanilla login and NeoForge channel negotiation");
        ClientIntegrationData.write("server-ready.json", result);
    }

    @SubscribeEvent
    public static void commands(RegisterCommandsEvent event) {
        if (!ClientIntegrationData.enabled()) return;
        event.getDispatcher().register(Commands.literal("tsr_client")
                .then(Commands.literal("prepare").then(Commands.argument("scenario", StringArgumentType.word()).executes(context -> run(context.getSource().getPlayerOrException(), StringArgumentType.getString(context, "scenario"), "prepare"))))
                .then(Commands.literal("verify").then(Commands.argument("scenario", StringArgumentType.word()).executes(context -> run(context.getSource().getPlayerOrException(), StringArgumentType.getString(context, "scenario"), "verify"))))
                .then(Commands.literal("snapshot").then(Commands.argument("scenario", StringArgumentType.word()).executes(context -> run(context.getSource().getPlayerOrException(), StringArgumentType.getString(context, "scenario"), "snapshot"))))
                .then(Commands.literal("finish").executes(context -> {
                    context.getSource().getServer().halt(false);
                    return 1;
                })));
    }

    private static int run(ServerPlayer player, String scenario, String phase) {
        var result = new JsonObject();
        result.addProperty("session", SESSION);
        result.addProperty("scenario", scenario);
        result.addProperty("player", player.getUUID().toString());
        result.addProperty("remoteAddress", player.connection.getConnection().getRemoteAddress().toString());
        result.addProperty("serverTick", serverTicks);
        result.addProperty("clockWriteFailures", clockWriteFailures);
        try {
            ClientIntegrationData.require(ClientIntegrationData.scenarios().contains(scenario), "Unknown client integration scenario");
            switch (phase) {
                case "verify" -> verify(player, scenario, result);
                case "prepare" -> prepare(player, scenario, result);
                case "snapshot" -> ClientIntegrationData.require(scenario.equals(current), "Stale inventory snapshot request");
                default -> throw new IllegalArgumentException(phase);
            }
            boolean waitingForClose = phase.equals("snapshot") && player.containerMenu != player.inventoryMenu;
            result.addProperty("status", waitingForClose ? "pending" : "passed");
            if (phase.equals("snapshot")) result.addProperty("serverCloseObserved", !waitingForClose);
        } catch (Throwable failure) {
            result.addProperty("status", "failed");
            result.addProperty("error", failure.toString());
            ThermalShockReforged.LOGGER.error("Client integration server scenario failed: scenario={}, phase={}", scenario, phase, failure);
        }
        result.addProperty("menu", ClientIntegrationData.menu(player));
        result.addProperty("containerId", player.containerMenu.containerId);
        result.add("inventory", ClientIntegrationData.inventory(player));
        result.add("recipes", ClientIntegrationData.recipes(player.level()));
        var progress = WorldProgressData.get((ServerLevel) player.level());
        result.addProperty("altarUnlocked", progress.altarUnlocked());
        result.addProperty("oreTier1", progress.oreTier1());
        result.addProperty("oreTier2", progress.oreTier2());
        ClientIntegrationData.write(scenario + switch (phase) { case "verify" -> "-verified.json"; case "prepare" -> "-prepared.json"; default -> "-closed.json"; }, result);
        return result.get("status").getAsString().equals("passed") ? 1 : 0;
    }

    private static void prepare(ServerPlayer player, String scenario, JsonObject result) {
        current = scenario;
        player.closeContainer();
        for (Entity actor : ACTORS) actor.discard();
        ACTORS.clear();
        ClientMenuCatalogue.cleanup();
        player.getInventory().clearContent();
        player.setGameMode(GameType.CREATIVE);
        var level = (ServerLevel) player.level();
        for (int x = 0; x <= 9; x++) for (int z = 0; z <= 9; z++) level.setBlock(new BlockPos(x, 119, z), Blocks.STONE.defaultBlockState(), 3);
        level.setBlock(ANCHOR, Blocks.AIR.defaultBlockState(), 3);
        ClientIntegrationData.require(player.teleportTo(level, 4.5, 120, 7, Set.of(), 180, 10, true), "Cannot position connected client");
        var actions = new JsonArray();
        switch (scenario) {
            case "login_sync" -> { }
            case "unlock_sync" -> {
                var progress = WorldProgressData.get(level);
                progress.unlockAltars();
                progress.initializeOreChoices(level);
                AltarProgressPayload.broadcast(level);
                OreChoicePayload.broadcast(level);
            }
            case "centrifuge" -> {
                useBlock(player, "centrifuge");
                ClientIntegrationData.require(player.containerMenu instanceof CentrifugeMenu, "Production block did not open centrifuge");
                player.containerMenu.getSlot(0).set(new ItemStack(item("pyrogenic_energy")));
                player.containerMenu.getSlot(1).set(new ItemStack(item("pyrogenic_energy")));
                player.containerMenu.getSlot(2).set(new ItemStack(item("frost_pearl")));
                action(actions, 0, ClientIntegrationData.menu(player));
                action(actions, 0, ClientIntegrationData.menu(player));
            }
            case "fabricator" -> {
                useBlock(player, "fabricator");
                ClientIntegrationData.require(player.containerMenu instanceof FabricatorMenu, "Production block did not open fabricator");
                var holder = level.recipeAccess().byKey(ResourceKey.create(Registries.RECIPE, FabricatorPage.GHOSTGLASS_PICK.recipe)).orElseThrow();
                fabrication = (FabricationRecipe) holder.value();
                int slot = 0;
                for (var cost : fabrication.materials()) player.getInventory().setItem(slot++, new ItemStack(cost.item().value(), cost.count()));
                player.containerMenu.getSlot(1).set(new ItemStack(fabrication.energy().item().value(), fabrication.energyCount()));
                action(actions, FabricatorMenu.SELECT_BUTTON + FabricatorPage.GHOSTGLASS_PICK.ordinal(), ClientIntegrationData.menu(player));
                action(actions, 0, ClientIntegrationData.menu(player));
                action(actions, 0, ClientIntegrationData.menu(player));
            }
            case "desk_photo" -> useBlock(player, "desk_photo");
            case "laut_quest" -> {
                player.getInventory().setItem(0, new ItemStack(item("plenky"), 4));
                laut = LautContent.LAUT.get().create(level, EntitySpawnReason.COMMAND);
                spawn(player, laut);
                action(actions, 0, id(LautPage.CONFIRM.id).toString());
                action(actions, 0, id(LautPage.ACCEPT.id).toString());
            }
            case "kofoni_navigation" -> {
                var npc = KofoniContent.KOFONI.get().create(level, EntitySpawnReason.COMMAND);
                spawn(player, npc);
                action(actions, 0, id(KofoniPage.INFO.id).toString());
                action(actions, 0, id(KofoniPage.SELF.id).toString());
                action(actions, 0, id(KofoniPage.INFO.id).toString());
            }
            default -> {
                if (scenario.startsWith("menu_")) ClientMenuCatalogue.prepare(player, ANCHOR, scenario, result);
                else throw new IllegalArgumentException(scenario);
            }
        }
        player.containerMenu.broadcastChanges();
        player.inventoryMenu.broadcastChanges();
        result.add("actions", actions);
        if (!result.has("entry")) result.addProperty("entry", "Production block use or entity interaction; no client-local screen construction");
    }

    private static void verify(ServerPlayer player, String scenario, JsonObject result) {
        ClientIntegrationData.require(scenario.equals(current), "Stale integration verification request");
        switch (scenario) {
            case "centrifuge" -> {
                ClientIntegrationData.require(player.containerMenu instanceof CentrifugeMenu, "Centrifuge closed before verification");
                ClientIntegrationData.require(count(player, item("cryonic_energy")) == 1, "Centrifuge output must be exactly one after two requests");
                for (int slot = 0; slot < 3; slot++) ClientIntegrationData.require(player.containerMenu.getSlot(slot).getItem().isEmpty(), "Centrifuge input not consumed");
            }
            case "fabricator" -> {
                ClientIntegrationData.require(player.containerMenu instanceof FabricatorMenu menu && menu.page() == FabricatorPage.GHOSTGLASS_PICK, "Fabricator selection was not received");
                var output = player.containerMenu.getSlot(0).getItem();
                ClientIntegrationData.require(ItemStack.isSameItemSameComponents(output, fabrication.result()) && output.getCount() == fabrication.result().getCount(), "Fabricator output changed or duplicate request crafted twice");
                ClientIntegrationData.require(player.containerMenu.getSlot(1).getItem().isEmpty(), "Fabricator energy not consumed");
                for (var cost : fabrication.materials()) ClientIntegrationData.require(count(player, cost.item().value()) == 0, "Fabricator materials not consumed exactly");
            }
            case "laut_quest" -> {
                ClientIntegrationData.require(laut.completed() && count(player, item("plenky")) == 1, "Laut quest must consume exactly three Plenky");
                int rewards = count(player, item("adamantine_spice"));
                for (var drop : player.level().getEntitiesOfClass(ItemEntity.class, new AABB(ANCHOR).inflate(10))) if (drop.getItem().is(item("adamantine_spice"))) rewards += drop.getItem().getCount();
                ClientIntegrationData.require(rewards == 10, "Laut quest reward must contain ten spice");
                result.addProperty("rewardCount", rewards);
            }
            case "kofoni_navigation" -> ClientIntegrationData.require(player.containerMenu.getType() == KofoniContent.menu(KofoniPage.INFO), "Kofoni button navigation ended on wrong page");
            default -> {
                if (scenario.startsWith("menu_")) ClientIntegrationData.require(ClientIntegrationData.menu(player).equals(id(scenario.substring(5)).toString()) && player.containerMenu.stillValid(player), "Catalogue menu changed or closed before actual client capture");
            }
        }
    }

    private static void action(JsonArray actions, int button, String menu) {
        var action = new JsonObject();
        action.addProperty("button", button);
        action.addProperty("menu", menu);
        actions.add(action);
    }

    private static void spawn(ServerPlayer player, Mob npc) {
        ClientIntegrationData.require(npc != null, "NPC creation failed");
        npc.setNoAi(true);
        npc.setPersistenceRequired();
        npc.snapTo(4.5, 120, 4.5, 0, 0);
        ClientIntegrationData.require(player.level().addFreshEntity(npc), "NPC join rejected");
        ACTORS.add(npc);
        player.interactOn(npc, InteractionHand.MAIN_HAND, npc.position());
        ClientIntegrationData.require(player.containerMenu != player.inventoryMenu, "Production NPC interaction did not open a menu");
    }

    private static void useBlock(ServerPlayer player, String block) {
        var level = (ServerLevel) player.level();
        level.setBlock(ANCHOR, BuiltInRegistries.BLOCK.getValue(id(block)).defaultBlockState(), 3);
        player.gameMode.useItemOn(player, level, ItemStack.EMPTY, InteractionHand.MAIN_HAND, new BlockHitResult(Vec3.atCenterOf(ANCHOR), Direction.UP, ANCHOR, false));
    }

    private static Item item(String path) {
        Item item = BuiltInRegistries.ITEM.getValue(id(path));
        ClientIntegrationData.require(item != Items.AIR, "Missing integration item: " + path);
        return item;
    }

    private static int count(ServerPlayer player, Item item) {
        return player.getInventory().getNonEquipmentItems().stream().filter(stack -> stack.is(item)).mapToInt(ItemStack::getCount).sum();
    }

    private static Identifier id(String path) {
        return Identifier.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, path);
    }
}
