package com.hp.thermalshockreforged.gametest;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.hp.thermalshockreforged.ThermalShockReforged;
import com.hp.thermalshockreforged.content.atlas.AtlasArrivalState;
import com.hp.thermalshockreforged.content.atlas.AtlasEffectDisplay;
import com.hp.thermalshockreforged.content.atlas.AtlasMenu;
import com.hp.thermalshockreforged.content.fabrication.FabricationContent;
import com.hp.thermalshockreforged.content.fabrication.FabricatorPage;
import com.hp.thermalshockreforged.content.recipe.RecipeContent;
import com.hp.thermalshockreforged.compat.jei.MachineEntry;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Screenshot;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.tutorial.TutorialSteps;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.effect.MobEffects;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RenderFrameEvent;
import net.neoforged.neoforge.client.event.ScreenEvent;

@EventBusSubscriber(modid = ThermalShockReforged.MOD_ID, value = Dist.CLIENT)
public final class ClientFeatureClient {
    private static final JsonArray RESULTS = new JsonArray();
    private static int state;
    private static int sceneIndex;
    private static int jeiIndex;
    private static int stableTicks;
    private static int inventoryEffectEvents;
    private static int inventoryEffectEventsBefore;
    private static long deadline;
    private static long nextCommandTick;
    private static String session;
    private static JsonObject prepared;
    private static JsonObject current;
    private static JsonArray captures;
    private static String captureName;
    private static boolean captureRequested;
    private static boolean capturePending;
    private static boolean captureDone;
    private static boolean buttonSent;
    private static boolean finished;
    private static Throwable captureFailure;
    private static CompletableFuture<Void> resourceReload;

    private ClientFeatureClient() {
    }

    private static boolean enabled() {
        return ClientIntegrationData.enabled() && Boolean.getBoolean("tsr.clientIntegrationFeatures");
    }

    @SubscribeEvent
    public static void inventoryEffects(ScreenEvent.RenderInventoryMobEffects event) {
        if (enabled() && event.getScreen() instanceof InventoryScreen) inventoryEffectEvents++;
    }

    @SubscribeEvent
    public static void tick(ClientTickEvent.Post event) {
        if (!enabled() || finished) return;
        var mc = Minecraft.getInstance();
        try {
            if (state != 0 && System.nanoTime() > deadline) throw new IllegalStateException("Client feature timeout: state=" + state + ", scenario=" + scene());
            if (captureFailure != null) throw new IllegalStateException("Client feature screenshot failed", captureFailure);
            if (prepared != null && scene().startsWith("jade_") && (state == 6 || state == 10) && mc.player != null) {
                mc.player.lookAt(EntityAnchorArgument.Anchor.EYES, new Vec3(prepared.get("targetCenterX").getAsDouble(), prepared.get("targetCenterY").getAsDouble(), prepared.get("targetCenterZ").getAsDouble()));
            }
            switch (state) {
                case 0 -> {
                    Path baseline = ClientIntegrationData.directory().resolve("client-results.json");
                    if (!Files.exists(baseline)) return;
                    var result = ClientIntegrationData.read("client-results.json");
                    if (!result.get("status").getAsString().equals("passed")) return;
                    ClientIntegrationData.require(mc.player != null && mc.getConnection() != null && !mc.getConnection().getConnection().isMemoryConnection(), "Feature tests require the real TCP client");
                    session = result.get("session").getAsString();
                    nextCommandTick = clock() + 30;
                    mc.getTutorial().setStep(TutorialSteps.NONE);
                    move(1);
                }
                case 1 -> {
                    if (sceneIndex == ClientFeatureServer.scenarios().size()) {
                        writeResult("passed", null);
                        finished = true;
                        ThermalShockReforged.LOGGER.info("Client feature integration passed: scenarios={}", RESULTS.size());
                        return;
                    }
                    if (clock() < nextCommandTick) return;
                    if (mc.screen != null) mc.screen.onClose();
                    current = new JsonObject();
                    current.addProperty("scenario", scene());
                    captures = new JsonArray();
                    current.add("captures", captures);
                    buttonSent = false;
                    mc.player.connection.sendCommand("tsr_feature prepare " + scene());
                    nextCommandTick = clock() + 30;
                    move(2);
                }
                case 2 -> {
                    Path path = ClientIntegrationData.directory().resolve("feature-" + scene() + "-prepared.json");
                    if (!Files.exists(path)) return;
                    prepared = ClientIntegrationData.read(path.getFileName().toString());
                    ClientIntegrationData.require(prepared.get("session").getAsString().equals(session), "Feature evidence belongs to another session");
                    ClientIntegrationData.require(prepared.get("status").getAsString().equals("passed"), "Server feature preparation failed: " + prepared);
                    nextCommandTick = Math.max(nextCommandTick, prepared.get("serverTick").getAsLong() + 30);
                    current.add("serverPrepared", prepared);
                    if (scene().equals("jei_ready")) {
                        move(3);
                    } else if (scene().startsWith("jade_")) {
                        ClientJadeProbe.arm(prepared.get("x").getAsInt(), prepared.get("y").getAsInt(), prepared.get("z").getAsInt(), prepared.get("blockId").getAsString(), text("expectedTextKey"), text("forbiddenTextKey"), prepared.has("serverExpectedData") ? prepared.getAsJsonObject("serverExpectedData") : new JsonObject(), !prepared.has("requireServerData") || prepared.get("requireServerData").getAsBoolean());
                        move(6);
                    } else if (scene().startsWith("atlas_")) move(7);
                    else if (scene().equals("client_resource_reload")) {
                        resourceReload = mc.reloadResourcePacks();
                        move(11);
                    }
                    else throw new IllegalStateException("Unhandled feature scene: " + scene());
                }
                case 3 -> {
                    if (!ClientJeiProbe.ready()) return;
                    verifyJeiCounts(mc);
                    jeiIndex = 0;
                    ClientIntegrationData.require(ClientJeiProbe.showType(jeiIndex), "Could not open the first actual JEI category");
                    move(4);
                }
                case 4 -> {
                    if (!ClientJeiProbe.types().get(jeiIndex).equals(ClientJeiProbe.currentType())) { stableTicks = 0; return; }
                    if (++stableTicks < 10) return;
                    current.add("jei", ClientJeiProbe.result());
                    requestCapture("jei-" + jeiIndex, 5);
                }
                case 5 -> {
                    if (!captureDone) return;
                    jeiIndex++;
                    if (jeiIndex == ClientJeiProbe.types().size()) finishScene(mc);
                    else {
                        ClientIntegrationData.require(ClientJeiProbe.showType(jeiIndex), "Could not open actual JEI category " + jeiIndex);
                        move(4);
                    }
                }
                case 6 -> {
                    var observed = ClientJadeProbe.result();
                    if (observed == null || !observed.get("status").getAsString().equals("observed") || !observed.get("rendered").getAsBoolean() || !observed.get("targetMatches").getAsBoolean()) return;
                    if (!jadeMatches(observed)) return;
                    if (++stableTicks < 8) return;
                    current.add("jade", observed);
                    current.add("clientWorldAtObservation", worldObservation());
                    current.addProperty("cameraControl", "Entity.lookAt from the real client eye to the actual target shape center; normal game picking; no OS input or hitResult assignment");
                    requestCapture(scene(), 10);
                }
                case 7 -> {
                    if (prepared.has("clientButton") && !buttonSent) {
                        if (!(mc.player.containerMenu instanceof AtlasMenu) || mc.screen == null) return;
                        if (++stableTicks < 8) return;
                        mc.gameMode.handleInventoryButtonClick(mc.player.containerMenu.containerId, prepared.get("clientButton").getAsInt());
                        buttonSent = true;
                        stableTicks = 0;
                        return;
                    }
                    if (!mc.level.dimension().identifier().toString().equals(prepared.get("expectedDimension").getAsString()) || mc.screen != null) return;
                    boolean expected = prepared.get("expectedArrivalVisual").getAsBoolean();
                    var arrival = (AtlasArrivalState) mc.player;
                    if (arrival.hasArrivalProtectionVisual() != expected || (arrival.arrivalProtectionTicks() > 0) != expected) return;
                    if (++stableTicks < 5) return;
                    var effects = atlasState(mc);
                    ClientIntegrationData.require(effects.get("hasResistanceXDisplay").getAsBoolean() == expected, "Atlas virtual resistance display differs from synchronized arrival state");
                    current.add("hudState", effects);
                    requestCapture(scene() + "-hud", 8);
                }
                case 8 -> {
                    if (!captureDone) return;
                    inventoryEffectEventsBefore = inventoryEffectEvents;
                    mc.setScreen(new InventoryScreen(mc.player));
                    move(9);
                }
                case 9 -> {
                    if (!(mc.screen instanceof InventoryScreen)) return;
                    if (++stableTicks < 8) return;
                    boolean expected = prepared.get("expectedArrivalVisual").getAsBoolean();
                    var effects = atlasState(mc);
                    ClientIntegrationData.require(effects.get("hasResistanceXDisplay").getAsBoolean() == expected, "Atlas display expired or disagrees in the real inventory");
                    int events = inventoryEffectEvents - inventoryEffectEventsBefore;
                    if (expected) ClientIntegrationData.require(events > 0, "Actual inventory effect rendering hook was not observed");
                    effects.addProperty("inventoryEffectRenderEvents", events);
                    current.add("inventoryState", effects);
                    requestCapture(scene() + "-inventory", 10);
                }
                case 10 -> {
                    if (captureDone) finishScene(mc);
                }
                case 11 -> {
                    if (!resourceReload.isDone()) return;
                    resourceReload.join();
                    if (mc.getOverlay() != null || mc.screen != null || !ClientJeiProbe.ready()) return;
                    ClientIntegrationData.require(mc.level.dimension().identifier().toString().equals(prepared.get("expectedDimension").getAsString()), "Resource reload changed the connected dimension");
                    if (++stableTicks < 20) return;
                    verifyJeiCounts(mc);
                    current.addProperty("realResourceReloadCompleted", true);
                    current.add("jeiAfterReload", ClientJeiProbe.result());
                    requestCapture("client-resource-reload", 10);
                }
                default -> throw new IllegalStateException("Unknown client feature state " + state);
            }
        } catch (Throwable failure) {
            finished = true;
            ThermalShockReforged.LOGGER.error("Client feature integration failed: state={}, scene={}", state, sceneIndex, failure);
            writeResult("failed", failure);
        }
    }

    private static boolean jadeMatches(JsonObject observed) {
        boolean requireData = !prepared.has("requireServerData") || prepared.get("requireServerData").getAsBoolean();
        if (observed.get("screenOpen").getAsBoolean()) return false;
        if (!observed.get("expectedSubstringMatches").getAsBoolean() || !observed.get("forbiddenSubstringAbsent").getAsBoolean()) return false;
        if (requireData && (!observed.get("isServerConnected").getAsBoolean() || !observed.get("serverDataPositionMatches").getAsBoolean() || !observed.get("serverDataBlockIdMatches").getAsBoolean() || !observed.get("expectedServerDataMatches").getAsBoolean())) return false;
        if (prepared.has("additionalExpectedTextKeys")) for (var key : prepared.getAsJsonArray("additionalExpectedTextKeys")) {
            if (!observed.get("text").getAsString().contains(Component.translatable(key.getAsString()).getString())) return false;
        }
        var fields = observed.getAsJsonObject("serverDataFields");
        if (prepared.has("serverExpectedData")) for (var entry : prepared.getAsJsonObject("serverExpectedData").entrySet()) {
            if (entry.getKey().equals("absenceKeys")) {
                for (var key : entry.getValue().getAsJsonArray()) if (observed.getAsJsonArray("serverDataKeys").asList().stream().anyMatch(actual -> actual.getAsString().equals(key.getAsString()))) return false;
            } else if (!entry.getValue().equals(fields.get(entry.getKey()))) return false;
        }
        if (fields.has("SeedWaterings")) {
            String answer = fields.get("SeedThirsty").getAsBoolean() ? "yes" : "no";
            String thirsty = Component.translatable("jade.thermal_shock_reforged.seed.thirsty", Component.translatable("jade.thermal_shock_reforged.seed." + answer)).getString();
            String waterings = Component.translatable("jade.thermal_shock_reforged.seed.waterings", Math.min(fields.get("SeedWaterings").getAsInt(), 3), 3).getString();
            if (!observed.get("text").getAsString().contains(thirsty) || !observed.get("text").getAsString().contains(waterings)) return false;
        }
        return true;
    }

    private static void verifyJeiCounts(Minecraft mc) {
        ClientIntegrationData.require(ClientJeiProbe.types().size() == 11, "JEI must expose all eleven production categories");
        var nativeCounts = ClientIntegrationData.recipes(mc.level).getAsJsonObject("types");
        var mappings = Map.of("transmutator", "transmutation", "extractor", "extraction", "centrifuge", "centrifuge", "lapidary", "lapidary", "assembler", "assembler", "altar", "altar_conversion");
        for (var value : ClientJeiProbe.counts()) {
            var row = value.getAsJsonObject();
            String id = row.get("type").getAsString();
            int count = row.get("includeHidden").getAsInt();
            ClientIntegrationData.require(count > 0 && row.get("visible").getAsInt() > 0, "JEI category has no actual recipes: " + id);
            String path = id.substring(id.indexOf(':') + 1);
            if (mappings.containsKey(path)) ClientIntegrationData.require(count == nativeCounts.get(ThermalShockReforged.MOD_ID + ":" + mappings.get(path)).getAsInt(), "JEI recipe count differs from the synchronized production recipe map: " + row);
        }
        verifyFabricatorEntries(mc);
    }

    private static void verifyFabricatorEntries(Minecraft mc) {
        var actual = new ArrayList<>(ClientJeiProbe.fabricatorEntries());
        var recipes = RecipeContent.recipes(mc.level).byType(FabricationContent.TYPE.get());
        var remainingIds = new HashSet<String>();
        for (var holder : recipes) remainingIds.add(holder.id().identifier().toString());
        var verified = new JsonArray();
        for (FabricatorPage page : FabricatorPage.values()) {
            if (page.isGuide()) continue;
            var holder = recipes.stream().filter(value -> value.id().identifier().equals(page.recipe)).findFirst().orElseThrow(() -> new IllegalStateException("Missing synchronized production fabrication page recipe " + page.recipe));
            var recipe = holder.value();
            int match = -1;
            for (int index = 0; index < actual.size(); index++) {
                MachineEntry entry = actual.get(index);
                if (entry.inputs().size() != 7 || entry.outputs().size() != 1 || !slotEquals(entry.outputs().getFirst(), recipe.result())) continue;
                boolean inputsMatch = true;
                for (int input = 0; input < 6; input++) {
                    ItemStack expected = input < recipe.materials().size() ? new ItemStack(recipe.materials().get(input).item(), recipe.materials().get(input).count()) : ItemStack.EMPTY;
                    inputsMatch &= slotEquals(entry.inputs().get(input), expected);
                }
                inputsMatch &= slotEquals(entry.inputs().get(6), new ItemStack(recipe.energy().item(), recipe.energy().count()));
                if (inputsMatch) { match = index; break; }
            }
            ClientIntegrationData.require(match >= 0, "JEI fabrication materials, energy or result differ from the production page recipe " + page.recipe);
            actual.remove(match);
            remainingIds.remove(page.recipe.toString());
            verified.add(page.recipe.toString());
        }
        ClientIntegrationData.require(actual.isEmpty(), "JEI fabrication category contains extra or duplicate page recipes");
        ClientIntegrationData.require(remainingIds.equals(Set.of(ThermalShockReforged.MOD_ID + ":fabrication/ender_tentacle")), "Unexpected non-page fabrication recipes: " + remainingIds);
        var membership = new JsonObject();
        membership.add("verifiedProductionPageRecipes", verified);
        membership.addProperty("verifiedCount", verified.size());
        membership.addProperty("synchronizedFabricationCount", recipes.size());
        membership.addProperty("legacyRecipeOutsideOrdinaryFabricator", ThermalShockReforged.MOD_ID + ":fabrication/ender_tentacle");
        membership.addProperty("baselineContract", "Both 1.21.1 and 26.1.2 JEI register ordinary fabrication from the same 33 non-guide FabricatorPage entries; ender_tentacle belongs to LegacyFabricatorMenu");
        current.add("fabricatorMembership", membership);
    }

    private static boolean slotEquals(MachineEntry.Slot slot, ItemStack expected) {
        if (expected.isEmpty()) return slot.stacks().isEmpty() || slot.stacks().stream().allMatch(ItemStack::isEmpty);
        return slot.stacks().size() == 1 && slot.stacks().getFirst().getCount() == expected.getCount() && ItemStack.isSameItemSameComponents(slot.stacks().getFirst(), expected);
    }

    private static JsonObject atlasState(Minecraft mc) {
        var result = new JsonObject();
        var arrival = (AtlasArrivalState) mc.player;
        result.addProperty("dimension", mc.level.dimension().identifier().toString());
        result.addProperty("arrivalTicks", arrival.arrivalProtectionTicks());
        result.addProperty("arrivalVisual", arrival.hasArrivalProtectionVisual());
        result.addProperty("realEffectCount", mc.player.getActiveEffects().size());
        var display = AtlasEffectDisplay.effects(mc.player);
        result.addProperty("displayEffectCount", display.size());
        result.addProperty("hasResistanceXDisplay", display.stream().anyMatch(effect -> effect.is(MobEffects.RESISTANCE) && effect.getAmplifier() >= 9));
        return result;
    }

    private static String text(String field) {
        if (!prepared.has(field) || prepared.get(field).isJsonNull() || prepared.get(field).getAsString().isEmpty()) return "";
        return Component.translatable(prepared.get(field).getAsString()).getString();
    }

    private static long clock() {
        var clock = ClientIntegrationData.read("server-clock.json");
        ClientIntegrationData.require(clock.get("session").getAsString().equals(session), "Feature clock session changed");
        return clock.get("serverTick").getAsLong();
    }

    private static void requestCapture(String name, int next) {
        Minecraft.getInstance().getToastManager().clear();
        captureName = name;
        captureDone = false;
        captureRequested = true;
        move(next);
    }

    @SubscribeEvent
    public static void frame(RenderFrameEvent.Post event) {
        if (!enabled() || finished || !captureRequested || capturePending) return;
        var mc = Minecraft.getInstance();
        if (scene().startsWith("jade_") && (!(mc.hitResult instanceof BlockHitResult hit) || !hit.getBlockPos().equals(new BlockPos(prepared.get("x").getAsInt(), prepared.get("y").getAsInt(), prepared.get("z").getAsInt())) || mc.screen != null)) return;
        captureRequested = false;
        capturePending = true;
        var metadata = new JsonObject();
        metadata.addProperty("scene", scene());
        metadata.addProperty("capture", captureName);
        metadata.addProperty("width", mc.getWindow().getWidth());
        metadata.addProperty("height", mc.getWindow().getHeight());
        metadata.addProperty("screen", mc.screen == null ? "none" : mc.screen.getClass().getName());
        metadata.addProperty("serverTickObserved", clock());
        metadata.addProperty("jeiType", ClientJeiProbe.currentType());
        var sky = mc.gameRenderer.getGameRenderState().levelRenderState.skyRenderState;
        metadata.addProperty("skybox", sky.skybox.name());
        metadata.addProperty("sunAngleRadians", sky.sunAngle);
        metadata.addProperty("moonAngleRadians", sky.moonAngle);
        metadata.addProperty("moonPhase", sky.moonPhase.name());
        metadata.addProperty("starBrightness", sky.starBrightness);
        Path path = ClientIntegrationData.directory().resolve("feature-" + captureName + ".png");
        metadata.addProperty("image", path.toString());
        Screenshot.takeScreenshot(mc.getMainRenderTarget(), image -> {
            try (image) {
                image.writeToFile(path);
                mc.execute(() -> {
                    captures.add(metadata);
                    capturePending = false;
                    captureDone = true;
                });
            } catch (Throwable failure) {
                mc.execute(() -> captureFailure = failure);
            }
        });
    }

    private static void finishScene(Minecraft mc) {
        current.addProperty("status", "passed");
        RESULTS.add(current);
        writeResult("running", null);
        sceneIndex++;
        if (mc.screen != null) mc.screen.onClose();
        move(1);
    }

    private static String scene() {
        return sceneIndex < ClientFeatureServer.scenarios().size() ? ClientFeatureServer.scenarios().get(sceneIndex) : "finished";
    }

    private static void move(int next) {
        state = next;
        stableTicks = 0;
        deadline = System.nanoTime() + 120_000_000_000L;
    }

    private static void writeResult(String status, Throwable failure) {
        var result = new JsonObject();
        result.addProperty("status", status);
        result.addProperty("session", session);
        result.addProperty("expectedScenarios", ClientFeatureServer.scenarios().size());
        result.addProperty("completedScenarios", RESULTS.size());
        result.addProperty("transport", "Actual TCP client, production JEI and Jade callbacks, actual HUD and inventory screenshots");
        result.addProperty("state", state);
        result.add("scenarios", RESULTS);
        if (failure != null) {
            result.addProperty("error", failure.toString());
            if (current != null) result.add("currentScenario", current.deepCopy());
            var jade = ClientJadeProbe.result();
            if (jade != null) result.add("lastJadeObservation", jade);
            result.add("lastJeiObservation", ClientJeiProbe.result());
            result.add("worldAtFailure", worldObservation());
        }
        ClientIntegrationData.write("feature-results.json", result);
    }

    private static JsonObject worldObservation() {
        var mc = Minecraft.getInstance();
        var result = new JsonObject();
        result.addProperty("screen", mc.screen == null ? "none" : mc.screen.getClass().getName());
        if (mc.player == null || mc.level == null) return result;
        result.addProperty("dimension", mc.level.dimension().identifier().toString());
        result.addProperty("playerX", mc.player.getX());
        result.addProperty("playerY", mc.player.getY());
        result.addProperty("playerZ", mc.player.getZ());
        result.addProperty("playerYaw", mc.player.getYRot());
        result.addProperty("playerPitch", mc.player.getXRot());
        result.addProperty("playerPose", mc.player.getPose().name());
        result.addProperty("playerHeadBlock", mc.level.getBlockState(mc.player.blockPosition().above()).toString());
        result.addProperty("eye", mc.player.getEyePosition().toString());
        result.addProperty("look", mc.player.getLookAngle().toString());
        result.addProperty("hitType", mc.hitResult == null ? "none" : mc.hitResult.getType().name());
        if (mc.hitResult != null) result.addProperty("hitLocation", mc.hitResult.getLocation().toString());
        if (mc.hitResult instanceof BlockHitResult hit) {
            result.addProperty("hitPosition", hit.getBlockPos().toShortString());
            result.addProperty("hitDirection", hit.getDirection().name());
            result.addProperty("hitBlock", BuiltInRegistries.BLOCK.getKey(mc.level.getBlockState(hit.getBlockPos()).getBlock()).toString());
        }
        if (prepared != null && prepared.has("x") && prepared.has("y") && prepared.has("z")) {
            var pos = new BlockPos(prepared.get("x").getAsInt(), prepared.get("y").getAsInt(), prepared.get("z").getAsInt());
            var block = mc.level.getBlockState(pos);
            var shape = block.getShape(mc.level, pos);
            result.addProperty("expectedBlock", BuiltInRegistries.BLOCK.getKey(block.getBlock()).toString());
            result.addProperty("expectedBlockState", block.toString());
            result.addProperty("expectedShape", shape.isEmpty() ? "empty" : shape.bounds().toString());
            var entity = mc.level.getBlockEntity(pos);
            result.addProperty("expectedBlockEntity", entity == null ? "none" : entity.getClass().getName());
        }
        ThermalShockReforged.LOGGER.debug("Client feature world diagnostic: {}", result);
        return result;
    }
}
