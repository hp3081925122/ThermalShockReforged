package com.hp.thermalshockreforged.gametest;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.hp.thermalshockreforged.ThermalShockReforged;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Screenshot;
import net.minecraft.client.renderer.state.level.QuadParticleRenderState;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.GameType;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.ExtractLevelRenderStateEvent;
import net.neoforged.neoforge.client.event.RenderFrameEvent;

@EventBusSubscriber(modid = ThermalShockReforged.MOD_ID, value = Dist.CLIENT)
public final class ClientBlockBreakProbe {
    private static JsonArray RESULTS = new JsonArray();
    private static boolean running;
    private static boolean finished;
    private static boolean capturePending;
    private static boolean captureDone;
    private static boolean started;
    private static boolean particleScreenshotCaptured;
    private static boolean dropScreenshotCaptured;
    private static String status = "idle";
    private static String session;
    private static String autoStartedSession;
    private static String captureKind;
    private static String capturePath;
    private static int scenarioIndex;
    private static int state;
    private static int baselineParticles;
    private static int breakTickCount;
    private static int stableDropTicks;
    private static long nextCommandTick;
    private static long deadlineNanos;
    private static long breakObservedClientTick;
    private static long activeFrameSequence;
    private static long snapshotFrameSequence = -1L;
    private static long consumedFrameSequence = -1L;
    private static JsonObject renderedSnapshot;
    private static JsonObject prepared;
    private static JsonObject verified;
    private static JsonObject current;
    private static JsonObject latest;
    private static Throwable failure;

    private ClientBlockBreakProbe() {
    }

    private static boolean enabled() {
        return ClientIntegrationData.enabled() && Boolean.getBoolean("tsr.clientIntegrationBlockBreak");
    }

    public static List<String> scenarios() {
        return ClientBlockBreakServer.scenarios();
    }

    public static JsonObject result() {
        JsonObject result = new JsonObject();
        result.addProperty("status", finished ? status : running ? "running" : "idle");
        result.addProperty("session", session);
        result.addProperty("expectedScenarios", scenarios().size());
        result.addProperty("completedScenarios", RESULTS.size());
        result.add("scenarios", RESULTS.deepCopy());
        if (failure != null) result.addProperty("error", failure.toString());
        return result;
    }

    @SubscribeEvent
    public static void tick(ClientTickEvent.Post event) {
        if (!enabled() || finished) return;
        Minecraft mc = Minecraft.getInstance();
        try {
            if (failure != null) throw new IllegalStateException("Block break screenshot or client observation failed", failure);
            if (running && System.nanoTime() > deadlineNanos) throw new IllegalStateException("Block break probe timed out at scene " + scene());
            if (!running) {
                Path baselinePath = ClientIntegrationData.directory().resolve("client-results.json");
                if (!Files.exists(baselinePath)) return;
                JsonObject baseline = ClientIntegrationData.read("client-results.json");
                JsonObject ready = ClientIntegrationData.read("server-ready.json");
                if (!"passed".equals(baseline.get("status").getAsString()) || baseline.get("completedScenarios").getAsInt() != 7 || !"ready".equals(ready.get("status").getAsString())) return;
                String baselineSession = baseline.get("session").getAsString();
                if (!baselineSession.equals(ready.get("session").getAsString()) || baselineSession.equals(autoStartedSession)) return;
                if (mc.player == null || mc.level == null || mc.getConnection() == null || mc.getConnection().getConnection().isMemoryConnection() || mc.screen != null) return;
                autoStartedSession = baselineSession;
                begin();
            }
            advance(mc);
        } catch (Throwable problem) {
            fail(mc, problem);
        }
    }

    public static void begin() {
        Minecraft mc = Minecraft.getInstance();
        ClientIntegrationData.require(enabled(), "Block break probe is not enabled");
        ClientIntegrationData.require(!running, "Block break probe is already running");
        ClientIntegrationData.require(mc.player != null && mc.level != null && mc.getConnection() != null && !mc.getConnection().getConnection().isMemoryConnection(), "Block break probe requires the real TCP client");
        ClientIntegrationData.require(mc.screen == null, "Block break probe requires an unobstructed in-world view");
        JsonObject ready = ClientIntegrationData.read("server-ready.json");
        JsonObject baseline = ClientIntegrationData.read("client-results.json");
        ClientIntegrationData.require("ready".equals(ready.get("status").getAsString()) && "passed".equals(baseline.get("status").getAsString()), "Default TCP baseline is not ready and passed");
        session = ready.get("session").getAsString();
        ClientIntegrationData.require(session.equals(baseline.get("session").getAsString()) && baseline.get("completedScenarios").getAsInt() == 7, "Default seven result and server-ready sessions differ or are incomplete");
        scenarioIndex = 0;
        state = 0;
        status = "running";
        running = true;
        finished = false;
        capturePending = false;
        captureDone = false;
        started = false;
        particleScreenshotCaptured = false;
        dropScreenshotCaptured = false;
        failure = null;
        prepared = null;
        verified = null;
        current = null;
        latest = null;
        activeFrameSequence = 0L;
        snapshotFrameSequence = -1L;
        consumedFrameSequence = -1L;
        renderedSnapshot = null;
        RESULTS = new JsonArray();
        nextCommandTick = clock() + 30L;
        deadlineNanos = System.nanoTime() + 1_800_000_000_000L;
        writeResult("running");
        ThermalShockReforged.LOGGER.info("Client block break probe armed: session={}, scenarios={}, transport=TCP", session, scenarios().size());
    }

    private static void advance(Minecraft mc) throws java.io.IOException {
        if (state == 0) {
            if (scenarioIndex >= scenarios().size()) {
                finished = true;
                running = false;
                status = "passed";
                writeResult("passed");
                ThermalShockReforged.LOGGER.info("Client block break probe passed: scenes={}, pairedScreenshots={}", RESULTS.size(), RESULTS.size() * 2);
                return;
            }
            if (clock() < nextCommandTick) return;
            if (mc.screen != null) throw new IllegalStateException("Close the screen before block break capture");
            prepared = null;
            verified = null;
            latest = null;
            renderedSnapshot = null;
            snapshotFrameSequence = -1L;
            captureDone = false;
            capturePending = false;
            captureKind = null;
            capturePath = null;
            current = new JsonObject();
            current.addProperty("scene", scene());
            current.addProperty("session", session);
            current.addProperty("transport", "Real TCP player uses MultiPlayerGameMode.startDestroyBlock and continueDestroyBlock; no OS input, fabricated hitResult, or direct server destroy call.");
            current.addProperty("particleEngineClearedBeforeBreak", true);
            current.addProperty("screenshotTiming", "First rendered frame with native block-break particles, then a later frame with the synchronized real dropped ItemEntity.");
            Path evidence = evidencePath("prepared");
            Files.deleteIfExists(evidence);
            mc.player.connection.sendCommand("tsr_break prepare " + scene());
            state = 1;
            deadlineNanos = System.nanoTime() + 120_000_000_000L;
            return;
        }
        if (state == 1) {
            Path path = evidencePath("prepared");
            if (!Files.exists(path)) return;
            prepared = ClientIntegrationData.read(path.getFileName().toString());
            ClientIntegrationData.require("passed".equals(prepared.get("status").getAsString()), "Server fixture preparation failed: " + prepared);
            ClientIntegrationData.require(session.equals(prepared.get("session").getAsString()) && scene().equals(prepared.get("scenario").getAsString()), "Prepared scene/session does not match the active TCP session");
            if (clock() < prepared.get("serverTick").getAsLong() + 10L) return;
            if (mc.level == null || !mc.level.dimension().identifier().toString().equals(prepared.get("dimension").getAsString())) return;
            if (mc.player == null || !BuiltInRegistries.ITEM.getKey(mc.player.getMainHandItem().getItem()).toString().equals(prepared.get("toolId").getAsString())) return;
            if (mc.player.blockPosition().distSqr(target()) > 64.0D) return;
            if (!BuiltInRegistries.BLOCK.getKey(mc.level.getBlockState(target()).getBlock()).toString().equals(prepared.get("blockId").getAsString())) return;
            if (mc.gameMode == null || mc.gameMode.getPlayerMode() != GameType.SURVIVAL) return;
            if (!mc.level.getEntitiesOfClass(LivingEntity.class, new AABB(target()).inflate(4), entity -> entity != mc.player).isEmpty()) return;
            current.add("serverPrepared", prepared.deepCopy());
            current.addProperty("breakPath", "Actual client game mode startDestroyBlock/continueDestroyBlock and normal serverbound player action packets");
            current.addProperty("nativeParticleObservation", "ParticleEngine.clearParticles before the scene; ParticleEngine.countParticles and native destroy stage sampled during the real break; extraction and screenshot use the actual rendered frame");
            current.addProperty("ambientParticlesBeforeClear", particleCount(mc));
            mc.particleEngine.clearParticles();
            baselineParticles = particleCount(mc);
            ClientIntegrationData.require(baselineParticles == 0, "Particle engine did not clear to zero before break");
            nextCommandTick = prepared.get("serverTick").getAsLong() + 30L;
            started = false;
            breakTickCount = 0;
            stableDropTicks = 0;
            particleScreenshotCaptured = false;
            dropScreenshotCaptured = false;
            state = 2;
            deadlineNanos = System.nanoTime() + 240_000_000_000L;
        }
        if (state == 2) mine(mc);
        if (state == 3) {
            if (captureDone) {
                ClientIntegrationData.require(latest != null && latest.has("phase") && "first-native-break-debris".equals(latest.get("phase").getAsString())
                        && latest.has("image") && current.has("debrisScreenshot"), "Debris completion did not come from the current scene screenshot");
                Path debrisPath = Path.of(current.get("debrisScreenshot").getAsString());
                ClientIntegrationData.require(Files.isRegularFile(debrisPath) && Files.size(debrisPath) > 0L, "Current debris screenshot file is missing or empty");
                particleScreenshotCaptured = true;
                current.add("firstDebrisFrame", latest.deepCopy());
                captureDone = false;
            }
            if (particleScreenshotCaptured && clock() >= nextCommandTick) {
                Files.deleteIfExists(evidencePath("verified"));
                mc.player.connection.sendCommand("tsr_break verify " + scene());
                state = 4;
                deadlineNanos = System.nanoTime() + 120_000_000_000L;
            }
        }
        if (state == 4) {
            if (!Files.exists(evidencePath("verified"))) return;
            verified = ClientIntegrationData.read(evidencePath("verified").getFileName().toString());
            ClientIntegrationData.require("passed".equals(verified.get("status").getAsString()), "Server did not verify block removal and expected item drop: " + verified);
            ClientIntegrationData.require(session.equals(verified.get("session").getAsString()) && scene().equals(verified.get("scenario").getAsString()), "Server drop verification belongs to another scene/session");
            current.add("serverVerifiedDrop", verified.deepCopy());
            state = 5;
            deadlineNanos = System.nanoTime() + 120_000_000_000L;
        }
        if (state == 5) {
            if (mc.level == null || !mc.level.getBlockState(target()).isAir()) return;
            List<ItemEntity> drops = clientDrops(mc);
            if (drops.isEmpty()) {
                stableDropTicks = 0;
                return;
            }
            stableDropTicks++;
            if (stableDropTicks < 2) return;
            JsonArray rows = new JsonArray();
            for (ItemEntity entity : drops) {
                JsonObject row = new JsonObject();
                row.addProperty("itemId", BuiltInRegistries.ITEM.getKey(entity.getItem().getItem()).toString());
                row.addProperty("count", entity.getItem().getCount());
                row.addProperty("entityId", entity.getId());
                row.addProperty("x", entity.getX());
                row.addProperty("y", entity.getY());
                row.addProperty("z", entity.getZ());
                rows.add(row);
            }
            current.add("clientDropEntities", rows);
            current.addProperty("clientDropStableTicks", stableDropTicks);
            state = 6;
            captureKind = "drops";
            captureDone = false;
            capturePending = false;
            deadlineNanos = System.nanoTime() + 120_000_000_000L;
        }
        if (state == 6 && dropScreenshotCaptured) completeScene();
    }

    private static void mine(Minecraft mc) {
        if (mc.player == null || mc.level == null || mc.gameMode == null || mc.screen != null) return;
        BlockPos target = target();
        mc.player.lookAt(EntityAnchorArgument.Anchor.EYES, Vec3.atCenterOf(target));
        if (mc.level.getBlockState(target).isAir()) {
            if (state == 2) {
                breakObservedClientTick = mc.level.getGameTime();
                current.addProperty("clientPredictedAirTick", breakObservedClientTick);
                current.addProperty("particleCountAtBreak", particleCount(mc));
                state = 3;
                deadlineNanos = System.nanoTime() + 30_000_000_000L;
                return;
            }
        }
        if (!(mc.hitResult instanceof BlockHitResult hit) || !hit.getBlockPos().equals(target)) return;
        if (!started) {
            if (!mc.gameMode.startDestroyBlock(hit.getBlockPos(), hit.getDirection())) throw new IllegalStateException("Native startDestroyBlock rejected the real block hit");
            started = true;
        } else {
            mc.gameMode.continueDestroyBlock(hit.getBlockPos(), hit.getDirection());
        }
        breakTickCount++;
        JsonObject sample = new JsonObject();
        sample.addProperty("tick", mc.level.getGameTime());
        sample.addProperty("destroying", mc.gameMode.isDestroying());
        sample.addProperty("destroyStage", mc.gameMode.getDestroyStage());
        sample.addProperty("particleCount", particleCount(mc));
        sample.addProperty("actualHitFace", hit.getDirection().getName());
        sample.addProperty("actualHitLocation", hit.getLocation().toString());
        sample.addProperty("targetBlockId", BuiltInRegistries.BLOCK.getKey(mc.level.getBlockState(target).getBlock()).toString());
        if (latest == null) latest = new JsonObject();
        JsonArray samples = latest.has("samples") ? latest.getAsJsonArray("samples") : new JsonArray();
        samples.add(sample);
        latest.add("samples", samples);
        if (mc.level.getBlockState(target).isAir()) {
            breakObservedClientTick = mc.level.getGameTime();
            current.add("breakProgressSamples", samples.deepCopy());
            current.addProperty("clientPredictedAirTick", breakObservedClientTick);
            current.addProperty("particleCountAtBreak", particleCount(mc));
            state = 3;
            deadlineNanos = System.nanoTime() + 30_000_000_000L;
        }
    }

    @SubscribeEvent
    public static void extract(ExtractLevelRenderStateEvent event) {
        if (!running || finished || prepared == null || (state != 3 && state != 6)) return;
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null || mc.player == null) return;
        try {
            snapshotFrameSequence = activeFrameSequence + 1L;
            renderedSnapshot = observation(mc, state == 3 ? "first-native-break-debris" : "synchronized-item-drop");
            renderedSnapshot.addProperty("frameSequence", snapshotFrameSequence);
            renderedSnapshot.addProperty("renderTick", event.getRenderTick());
            renderedSnapshot.addProperty("visibleQuadParticleGroups", event.getRenderState().particlesRenderState.particles.stream()
                    .filter(group -> group instanceof QuadParticleRenderState quad && !quad.isEmpty()).count());
            renderedSnapshot.addProperty("expectedDropEntityCount", clientDrops(mc).size());
            renderedSnapshot.addProperty("snapshotSource", "Actual ExtractLevelRenderStateEvent after native ParticleEngine.extract; copied before the corresponding frame is rendered and reset");
        } catch (Throwable problem) {
            failure = problem;
        }
    }

    @SubscribeEvent
    public static void framePre(RenderFrameEvent.Pre event) {
        if (running && !finished) activeFrameSequence++;
    }

    @SubscribeEvent
    public static void frame(RenderFrameEvent.Post event) {
        if (!running || finished || capturePending || captureDone || mcScreenOpen()) return;
        Minecraft mc = Minecraft.getInstance();
        if (renderedSnapshot == null || snapshotFrameSequence != activeFrameSequence || consumedFrameSequence == activeFrameSequence) return;
        if (state == 3 && !particleScreenshotCaptured && "first-native-break-debris".equals(renderedSnapshot.get("phase").getAsString())
                && renderedSnapshot.get("nativeParticleEngineCount").getAsInt() > baselineParticles
                && renderedSnapshot.get("visibleQuadParticleGroups").getAsLong() > 0L
                && "minecraft:air".equals(renderedSnapshot.get("clientTargetBlock").getAsString())) {
            latest = renderedSnapshot.deepCopy();
            captureKind = "debris";
            capturePending = true;
            capturePath = ClientIntegrationData.directory().resolve("blockbreak-" + session.substring(0, 8) + "-" + scene() + "-debris.png").toString();
        } else if (state == 6 && !dropScreenshotCaptured && "synchronized-item-drop".equals(renderedSnapshot.get("phase").getAsString())
                && renderedSnapshot.get("expectedDropEntityCount").getAsInt() > 0) {
            latest = renderedSnapshot.deepCopy();
            captureKind = "drops";
            capturePending = true;
            capturePath = ClientIntegrationData.directory().resolve("blockbreak-" + session.substring(0, 8) + "-" + scene() + "-drops.png").toString();
        }
        if (!capturePending) return;
        consumedFrameSequence = activeFrameSequence;
        Screenshot.takeScreenshot(mc.getMainRenderTarget(), image -> {
            try (image) {
                image.writeToFile(Path.of(capturePath));
                mc.execute(() -> {
                    current.addProperty(captureKind + "Screenshot", capturePath);
                    latest.addProperty("captureKind", captureKind);
                    latest.addProperty("image", capturePath);
                    capturePending = false;
                    captureDone = true;
                    if ("drops".equals(captureKind)) dropScreenshotCaptured = true;
                });
            } catch (Throwable problem) {
                mc.execute(() -> failure = problem);
            }
        });
    }

    private static JsonObject observation(Minecraft mc, String phase) {
        JsonObject result = new JsonObject();
        result.addProperty("phase", phase);
        result.addProperty("scene", scene());
        result.addProperty("clientGameTick", mc.level == null ? -1L : mc.level.getGameTime());
        result.addProperty("nativeParticleEngineCount", particleCount(mc));
        result.addProperty("nativeDestroyStage", mc.gameMode != null && mc.gameMode.isDestroying() ? mc.gameMode.getDestroyStage() : -1);
        result.addProperty("clientTargetBlock", mc.level == null ? "none" : BuiltInRegistries.BLOCK.getKey(mc.level.getBlockState(target()).getBlock()).toString());
        result.addProperty("screen", mc.screen == null ? "none" : mc.screen.getClass().getName());
        return result;
    }

    private static List<ItemEntity> clientDrops(Minecraft mc) {
        if (mc.level == null || prepared == null) return List.of();
        String expected = prepared.get("expectedDropId").getAsString();
        return mc.level.getEntitiesOfClass(ItemEntity.class, new AABB(target()).inflate(5), entity -> BuiltInRegistries.ITEM.getKey(entity.getItem().getItem()).toString().equals(expected));
    }

    private static int particleCount(Minecraft mc) {
        if (mc.particleEngine == null) return 0;
        try {
            return Integer.parseInt(mc.particleEngine.countParticles());
        } catch (NumberFormatException failure) {
            throw new IllegalStateException("ParticleEngine.countParticles returned a non-integer", failure);
        }
    }

    private static void completeScene() throws java.io.IOException {
        ClientIntegrationData.require(particleScreenshotCaptured && dropScreenshotCaptured && current.has("debrisScreenshot") && current.has("dropsScreenshot"), "Both real screenshots are required before completing the scene");
        Path debrisPath = Path.of(current.get("debrisScreenshot").getAsString());
        Path dropsPath = Path.of(current.get("dropsScreenshot").getAsString());
        ClientIntegrationData.require(Files.isRegularFile(debrisPath) && Files.size(debrisPath) > 0L && Files.isRegularFile(dropsPath) && Files.size(dropsPath) > 0L, "Both scene screenshot files must exist and contain data");
        current.addProperty("status", "passed");
        current.addProperty("particleScreenshotCaptured", particleScreenshotCaptured);
        current.addProperty("dropScreenshotCaptured", dropScreenshotCaptured);
        current.addProperty("particleClearingLimit", "ParticleEngine.clearParticles clears ambient client particles for a clean native break capture; this is a controlled fixture, not an ambient particle frequency test.");
        RESULTS.add(current.deepCopy());
        writeResult("running");
        scenarioIndex++;
        state = 0;
        prepared = null;
        verified = null;
        current = null;
        latest = null;
        nextCommandTick = clock() + 30L;
    }

    private static void fail(Minecraft mc, Throwable problem) {
        failure = problem;
        status = "failed";
        running = false;
        finished = true;
        if (current != null) {
            current.addProperty("status", "failed");
            current.addProperty("error", problem.toString());
            if (latest != null) current.add("latestObservation", latest.deepCopy());
            RESULTS.add(current.deepCopy());
        }
        writeResult("failed");
        ThermalShockReforged.LOGGER.error("Client block break probe failed: scene={}, state={}, tick={}", scene(), state, mc.level == null ? -1L : mc.level.getGameTime(), problem);
    }

    private static void writeResult(String value) {
        status = value;
        try {
            ClientIntegrationData.write("blockbreak-client-results.json", result());
        } catch (Throwable failure) {
            ThermalShockReforged.LOGGER.error("Client block break result write failed", failure);
        }
    }

    private static BlockPos target() {
        return new BlockPos(prepared.get("x").getAsInt(), prepared.get("y").getAsInt(), prepared.get("z").getAsInt());
    }

    private static Path evidencePath(String phase) {
        return ClientIntegrationData.directory().resolve("blockbreak-" + scene() + "-" + phase + ".json");
    }

    private static String scene() {
        return scenarioIndex < scenarios().size() ? scenarios().get(scenarioIndex) : "finished";
    }

    private static long clock() {
        JsonObject value = ClientIntegrationData.read("server-clock.json");
        ClientIntegrationData.require(session == null || session.equals(value.get("session").getAsString()), "Block break clock belongs to another server session");
        return value.get("serverTick").getAsLong();
    }

    private static boolean mcScreenOpen() {
        return Minecraft.getInstance().screen != null;
    }
}
