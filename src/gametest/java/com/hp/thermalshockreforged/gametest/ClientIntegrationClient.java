package com.hp.thermalshockreforged.gametest;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.hp.thermalshockreforged.ThermalShockReforged;
import com.hp.thermalshockreforged.content.decor.geo.FabricatorMenu;
import com.hp.thermalshockreforged.world.AltarProgressPayload;
import com.hp.thermalshockreforged.world.OreChoicePayload;
import java.nio.file.Files;
import java.nio.file.Path;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Screenshot;
import net.minecraft.client.gui.screens.ConnectScreen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.multiplayer.resolver.ServerAddress;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RenderFrameEvent;
import net.neoforged.neoforge.client.event.ScreenEvent;

@EventBusSubscriber(modid = ThermalShockReforged.MOD_ID, value = Dist.CLIENT)
public final class ClientIntegrationClient {
    private static final JsonArray RESULTS = new JsonArray();
    private static int state;
    private static int scenarioIndex;
    private static int actionIndex = -1;
    private static int stableTicks;
    private static int initializedScreens;
    private static int snapshotRequests;
    private static long deadline;
    private static long nextCommandServerTick;
    private static String session;
    private static String expectedMenu;
    private static int expectedFabricatorPage = -1;
    private static JsonObject prepared;
    private static JsonObject current;
    private static JsonArray steps;
    private static boolean captureRequested;
    private static boolean capturePending;
    private static boolean captureDone;
    private static boolean finished;
    private static Throwable captureFailure;

    private ClientIntegrationClient() {
    }

    @SubscribeEvent
    public static void initialized(ScreenEvent.Init.Post event) {
        if (ClientIntegrationData.enabled()) {
            initializedScreens++;
            ThermalShockReforged.LOGGER.debug("Client integration screen initialized: {}", event.getScreen().getClass().getName());
        }
    }

    @SubscribeEvent
    public static void tick(ClientTickEvent.Post event) {
        if (!ClientIntegrationData.enabled() || finished) return;
        Minecraft mc = Minecraft.getInstance();
        try {
            if (state != 0 && System.nanoTime() > deadline) throw new IllegalStateException("Client integration timed out: state=" + state + ", scenario=" + scenarioIndex + ", screen=" + (mc.screen == null ? "none" : mc.screen.getClass().getName()));
            if (captureFailure != null) throw new IllegalStateException("Main window screenshot failed", captureFailure);
            switch (state) {
                case 0 -> {
                    if (!(mc.screen instanceof TitleScreen) || mc.getOverlay() != null || !Files.exists(ClientIntegrationData.directory().resolve("server-ready.json"))) return;
                    session = ClientIntegrationData.read("server-ready.json").get("session").getAsString();
                    String address = System.getProperty("tsr.clientIntegrationAddress", "127.0.0.1:25735");
                    ConnectScreen.startConnecting(mc.screen, mc, ServerAddress.parseString(address), new ServerData("TSR integration", address, ServerData.Type.OTHER), false, null);
                    move(1);
                }
                case 1 -> {
                    if (mc.player == null || mc.level == null || mc.getConnection() == null || mc.screen != null) return;
                    ClientIntegrationData.require(!mc.getConnection().getConnection().isMemoryConnection(), "Client integration must use a real socket");
                    move(2);
                }
                case 2 -> {
                    if (scenarioIndex == ClientIntegrationData.scenarios().size()) {
                        writeResult("passed", null);
                        finished = true;
                        ThermalShockReforged.LOGGER.info("Client socket integration passed: scenarios={}, screenshots={}", RESULTS.size(), RESULTS.asList().stream().mapToInt(value -> value.getAsJsonObject().getAsJsonArray("steps").size()).sum());
                        return;
                    }
                    if (!commandReady()) return;
                    current = new JsonObject();
                    current.addProperty("scenario", scenario());
                    current.addProperty("remoteAddress", mc.getConnection().getConnection().getRemoteAddress().toString());
                    steps = new JsonArray();
                    current.add("steps", steps);
                    actionIndex = -1;
                    expectedFabricatorPage = -1;
                    sendCommand(mc, "prepare");
                    move(3);
                }
                case 3 -> {
                    Path file = ClientIntegrationData.directory().resolve(scenario() + "-prepared.json");
                    if (!Files.exists(file)) return;
                    prepared = ClientIntegrationData.read(file.getFileName().toString());
                    checkServerResult(prepared);
                    expectedMenu = prepared.get("menu").getAsString();
                    current.add("serverPrepared", prepared);
                    move(4);
                }
                case 4 -> {
                    if (!matches(mc)) { stableTicks = 0; return; }
                    if (++stableTicks < 16) return;
                    verifySynchronization(mc, prepared);
                    captureDone = false;
                    captureRequested = true;
                    move(5);
                }
                case 5 -> {
                    if (!captureDone) return;
                    if (actionIndex + 1 >= prepared.getAsJsonArray("actions").size() && !commandReady()) return;
                    actionIndex++;
                    var actions = prepared.getAsJsonArray("actions");
                    if (actionIndex >= actions.size()) {
                        sendCommand(mc, "verify");
                        move(7);
                        return;
                    }
                    var action = actions.get(actionIndex).getAsJsonObject();
                    expectedMenu = action.get("menu").getAsString();
                    int button = action.get("button").getAsInt();
                    if (mc.player.containerMenu instanceof FabricatorMenu && button >= FabricatorMenu.SELECT_BUTTON) expectedFabricatorPage = button - FabricatorMenu.SELECT_BUTTON;
                    mc.gameMode.handleInventoryButtonClick(mc.player.containerMenu.containerId, button);
                    move(4);
                }
                case 7 -> {
                    Path file = ClientIntegrationData.directory().resolve(scenario() + "-verified.json");
                    if (!Files.exists(file)) return;
                    var verified = ClientIntegrationData.read(file.getFileName().toString());
                    checkServerResult(verified);
                    if (!commandReady()) return;
                    current.add("serverVerified", verified);
                    current.add("inventoryBeforeMenuClose", ClientIntegrationData.inventory(mc.player));
                    if (mc.player.containerMenu != mc.player.inventoryMenu) mc.player.closeContainer();
                    snapshotRequests = 1;
                    sendCommand(mc, "snapshot");
                    move(8);
                }
                case 8 -> {
                    Path file = ClientIntegrationData.directory().resolve(scenario() + "-closed.json");
                    if (!Files.exists(file)) return;
                    var closed = ClientIntegrationData.read(file.getFileName().toString());
                    ClientIntegrationData.require(closed.get("session").getAsString().equals(session), "Stale inventory snapshot session");
                    if (closed.get("status").getAsString().equals("pending")) {
                        if (++stableTicks % 10 == 0 && commandReady()) {
                            snapshotRequests++;
                            sendCommand(mc, "snapshot");
                        }
                        return;
                    }
                    checkServerResult(closed);
                    ClientIntegrationData.require(closed.get("serverCloseObserved").getAsBoolean(), "Inventory snapshot must follow the real close packet");
                    if (mc.player.containerMenu != mc.player.inventoryMenu || !ClientIntegrationData.inventory(mc.player).equals(closed.getAsJsonObject("inventory"))) return;
                    current.add("serverAfterMenuClose", closed);
                    current.addProperty("inventorySnapshotRequests", snapshotRequests);
                    current.add("clientInventory", ClientIntegrationData.inventory(mc.player));
                    current.addProperty("status", "passed");
                    RESULTS.add(current);
                    writeResult("running", null);
                    scenarioIndex++;
                    move(2);
                }
                default -> throw new IllegalStateException("Unknown client integration state");
            }
        } catch (Throwable failure) {
            finished = true;
            ThermalShockReforged.LOGGER.error("Client socket integration failed: state={}, scenario={}", state, scenarioIndex, failure);
            writeResult("failed", failure);
        }
    }

    @SubscribeEvent
    public static void frame(RenderFrameEvent.Post event) {
        if (!ClientIntegrationData.enabled() || !captureRequested || capturePending || finished) return;
        Minecraft mc = Minecraft.getInstance();
        if (!matches(mc)) return;
        captureRequested = false;
        capturePending = true;
        var step = new JsonObject();
        step.addProperty("actionIndex", actionIndex);
        step.addProperty("screen", mc.screen == null ? "none" : mc.screen.getClass().getName());
        step.addProperty("menu", ClientIntegrationData.menu(mc.player));
        step.addProperty("containerId", mc.player.containerMenu.containerId);
        step.addProperty("screenInitializations", initializedScreens);
        step.addProperty("width", mc.getWindow().getWidth());
        step.addProperty("height", mc.getWindow().getHeight());
        if (mc.screen instanceof AbstractContainerScreen<?> screen) {
            step.addProperty("imageWidth", screen.getImageWidth());
            step.addProperty("imageHeight", screen.getImageHeight());
            step.addProperty("leftPos", screen.getLeftPos());
            step.addProperty("topPos", screen.getTopPos());
        }
        if (mc.player.containerMenu instanceof FabricatorMenu menu) step.addProperty("fabricatorPage", menu.page().name());
        Path path = ClientIntegrationData.directory().resolve(scenario() + "-step" + (actionIndex + 1) + ".png");
        step.addProperty("image", path.toString());
        Screenshot.takeScreenshot(mc.getMainRenderTarget(), image -> {
            try (image) {
                image.writeToFile(path);
                mc.execute(() -> {
                    steps.add(step);
                    capturePending = false;
                    captureDone = true;
                });
            } catch (Throwable failure) {
                mc.execute(() -> captureFailure = failure);
            }
        });
    }

    private static boolean matches(Minecraft mc) {
        if (mc.player == null || mc.level == null || expectedMenu == null || !ClientIntegrationData.menu(mc.player).equals(expectedMenu)) return false;
        if (expectedMenu.equals("none")) return mc.screen == null;
        if (!(mc.screen instanceof AbstractContainerScreen<?> screen) || screen.getMenu() != mc.player.containerMenu) return false;
        return expectedFabricatorPage < 0 || mc.player.containerMenu instanceof FabricatorMenu menu && menu.page().ordinal() == expectedFabricatorPage;
    }

    private static void verifySynchronization(Minecraft mc, JsonObject expected) {
        ClientIntegrationData.require(ClientIntegrationData.recipes(mc.level).equals(expected.getAsJsonObject("recipes")), "Client custom recipes differ from the server after real synchronization");
        ClientIntegrationData.require(AltarProgressPayload.unlocked(mc.level) == expected.get("altarUnlocked").getAsBoolean(), "Altar unlock payload was not received");
        ClientIntegrationData.require(OreChoicePayload.tier1(mc.level) == expected.get("oreTier1").getAsInt() && OreChoicePayload.tier2(mc.level) == expected.get("oreTier2").getAsInt(), "Ore choices were not synchronized");
    }

    private static void checkServerResult(JsonObject result) {
        ClientIntegrationData.require(result.get("session").getAsString().equals(session), "Stale server result from another session");
        ClientIntegrationData.require(result.get("status").getAsString().equals("passed"), "Server integration scenario failed: " + result);
        if (result.has("serverTick")) nextCommandServerTick = Math.max(nextCommandServerTick, result.get("serverTick").getAsLong() + 30);
    }

    private static boolean commandReady() {
        Path file = ClientIntegrationData.directory().resolve("server-clock.json");
        if (!Files.exists(file)) return false;
        var clock = ClientIntegrationData.read("server-clock.json");
        return clock.get("session").getAsString().equals(session) && clock.get("serverTick").getAsLong() >= nextCommandServerTick;
    }

    private static void sendCommand(Minecraft mc, String phase) {
        ClientIntegrationData.require(commandReady(), "Client integration command throttle must observe server ticks");
        var clock = ClientIntegrationData.read("server-clock.json");
        long observedTick = clock.get("serverTick").getAsLong();
        nextCommandServerTick = observedTick + 30;
        mc.player.connection.sendCommand("tsr_client " + phase + " " + scenario());
        ThermalShockReforged.LOGGER.debug("Client integration command sent: phase={}, serverTick={}, nextAllowedTick={}", phase, observedTick, nextCommandServerTick);
    }

    private static void move(int next) {
        state = next;
        stableTicks = 0;
        deadline = System.nanoTime() + 90_000_000_000L;
        ThermalShockReforged.LOGGER.debug("Client integration state changed: state={}, scenarioIndex={}", next, scenarioIndex);
    }

    private static String scenario() {
        return ClientIntegrationData.scenarios().get(scenarioIndex);
    }

    private static void writeResult(String status, Throwable failure) {
        var result = new JsonObject();
        result.addProperty("status", status);
        result.addProperty("session", session);
        result.addProperty("transport", "Actual TCP connection; vanilla login, NeoForge negotiation, recipe and menu packets");
        result.addProperty("expectedScenarios", ClientIntegrationData.scenarios().size());
        result.addProperty("completedScenarios", RESULTS.size());
        result.addProperty("state", state);
        result.addProperty("scenarioIndex", scenarioIndex);
        result.add("scenarios", RESULTS);
        if (failure != null) {
            result.addProperty("error", failure.toString());
            if (current != null) result.add("currentScenario", current.deepCopy());
            var mc = Minecraft.getInstance();
            if (mc.player != null) result.add("clientInventoryAtFailure", ClientIntegrationData.inventory(mc.player));
        }
        ClientIntegrationData.write("client-results.json", result);
    }
}
