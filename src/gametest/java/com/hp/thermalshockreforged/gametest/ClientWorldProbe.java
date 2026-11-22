package com.hp.thermalshockreforged.gametest;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.hp.thermalshockreforged.ThermalShockReforged;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import net.minecraft.client.CloudStatus;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Screenshot;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.state.level.LevelRenderState;
import net.minecraft.client.renderer.state.level.WeatherRenderState;
import net.minecraft.core.BlockPos;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import net.minecraft.util.ARGB;
import net.minecraft.world.clock.WorldClocks;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.ExtractLevelRenderStateEvent;
import net.neoforged.neoforge.client.event.RenderFrameEvent;

@EventBusSubscriber(modid = ThermalShockReforged.MOD_ID, value = Dist.CLIENT)
public final class ClientWorldProbe {
    private static JsonArray RESULTS = new JsonArray();
    private static boolean running;
    private static boolean finished;
    private static String resultStatus = "idle";
    private static boolean capturePending;
    private static boolean captureDone;
    private static int state;
    private static int sceneIndex;
    private static int stableFrames;
    private static long extractedSnapshotSequence;
    private static long consumedSnapshotSequence;
    private static long latestSnapshotSequence;
    private static long activeFrameSequence;
    private static long latestFrameSequence;
    private static int latestRenderTick;
    private static String autoStartedSession;
    private static int originalWeatherRadius;
    private static long nextCommandTick;
    private static long deadlineNanos;
    private static String session;
    private static JsonObject prepared;
    private static JsonObject current;
    private static JsonObject latest;
    private static Throwable failure;

    private ClientWorldProbe() {
    }

    private static boolean enabled() {
        return ClientIntegrationData.enabled() && Boolean.getBoolean("tsr.clientIntegrationWorld");
    }

    public static List<String> scenarios() {
        return ClientWorldServer.scenarios();
    }

    public static void begin() {
        Minecraft mc = Minecraft.getInstance();
        ClientIntegrationData.require(enabled(), "Client world probe is not enabled");
        ClientIntegrationData.require(!running, "Client world probe is already running");
        ClientIntegrationData.require(mc.player != null && mc.level != null && mc.getConnection() != null, "World probe requires a logged-in client");
        ClientIntegrationData.require(!mc.getConnection().getConnection().isMemoryConnection(), "World probe requires the real TCP client");
        ClientIntegrationData.require(mc.screen == null, "World probe requires the in-world view");
        JsonObject ready = ClientIntegrationData.read("server-ready.json");
        ClientIntegrationData.require("ready".equals(ready.get("status").getAsString()), "TCP integration server is not ready");
        session = ready.get("session").getAsString();
        originalWeatherRadius = mc.options.weatherRadius().get();
        sceneIndex = 0;
        stableFrames = 0;
        extractedSnapshotSequence = 0L;
        consumedSnapshotSequence = 0L;
        latestSnapshotSequence = 0L;
        activeFrameSequence = 0L;
        latestFrameSequence = -1L;
        latestRenderTick = -1;
        state = 0;
        current = null;
        prepared = null;
        latest = null;
        failure = null;
        capturePending = false;
        captureDone = false;
        finished = false;
        resultStatus = "running";
        running = true;
        nextCommandTick = clock() + 30L;
        deadlineNanos = System.nanoTime() + 900_000_000_000L;
        RESULTS = new JsonArray();
        writeResult("running", null);
        ThermalShockReforged.LOGGER.info("Client world probe armed: session={}, scenarios={}, transport=TCP", session, scenarios().size());
    }

    public static void tick() {
        if (!running || finished) return;
        Minecraft mc = Minecraft.getInstance();
        try {
            if (System.nanoTime() > deadlineNanos) throw new IllegalStateException("Client world probe timed out at scene " + scene());
            if (failure != null) throw new IllegalStateException("World render-state capture failed", failure);
            if (prepared != null && state >= 2 && state <= 3 && mc.player != null && mc.level != null && mc.level.dimension().identifier().toString().equals(prepared.get("dimension").getAsString())) {
                Vec3 direction = Vec3.directionFromRotation(prepared.get("cameraPitch").getAsFloat(), prepared.get("cameraYaw").getAsFloat());
                mc.player.lookAt(EntityAnchorArgument.Anchor.EYES, mc.player.getEyePosition().add(direction.scale(64.0)));
            }
            if (state == 0) {
                if (sceneIndex >= scenarios().size()) {
                    restoreWeatherRadius(mc);
                    finished = true;
                    running = false;
                    resultStatus = "passed";
                    writeResult("passed", null);
                    ThermalShockReforged.LOGGER.info("Client world probe passed: scenes={}, screenshots={}", RESULTS.size());
                    return;
                }
                if (clock() < nextCommandTick) return;
                String scene = scene();
                current = new JsonObject();
                current.addProperty("scene", scene);
                current.addProperty("session", session);
                current.addProperty("remoteAddress", mc.getConnection().getConnection().getRemoteAddress().toString());
                current.addProperty("captureView", scene.startsWith("aqeron_moon_") ? "upward sky view for moon phase" : "horizon view");
                if (scene.equals("weather_overworld_borean_snow_radius10") || scene.equals("weather_loq_borean_snow_radius4")) {
                    mc.options.weatherRadius().set(10);
                    current.addProperty("inputWeatherRadius", 10);
                    current.addProperty("weatherRadiusControl", "client weatherRadius input is 10 for both dimensions");
                    current.addProperty("productionWeatherRadiusRule", scene.equals("weather_loq_borean_snow_radius4") ? "LoqendiaWeatherMixin.reduceWeatherRadius: actual radius = input * 2 / 5" : "WeatherEffectRenderer uses the input option directly");
                }

                mc.player.connection.sendCommand("tsr_world prepare " + scene);
                stableFrames = 0;
                state = 1;
                deadlineNanos = System.nanoTime() + 120_000_000_000L;
                return;
            }
            if (state == 1) {
                Path preparedPath = ClientIntegrationData.directory().resolve("world-" + scene() + "-prepared.json");
                if (!Files.exists(preparedPath)) return;
                prepared = ClientIntegrationData.read(preparedPath.getFileName().toString());
                ClientIntegrationData.require("passed".equals(prepared.get("status").getAsString()), "Server did not prepare " + scene() + ": " + prepared);
                ClientIntegrationData.require(session.equals(prepared.get("session").getAsString()), "Prepared world fixture belongs to another session");
                current.add("serverPrepared", prepared.deepCopy());
                state = 2;
                stableFrames = 0;
                deadlineNanos = System.nanoTime() + 120_000_000_000L;
                return;
            }
            if (state == 2 && stableFrames >= 20 && latest != null) {
                validateLatest(latest);
                state = 3;
                requestScreenshot();
                deadlineNanos = System.nanoTime() + 120_000_000_000L;
                return;
            }
            if (state == 3 && captureDone) {
                current.addProperty("status", "passed");
                current.addProperty("observedServerTick", clock());
                ClientIntegrationData.write("world-" + scene() + "-verified.json", current.deepCopy());
                RESULTS.add(current.deepCopy());
                writeResult("running", null);
                sceneIndex++;
                current = null;
                prepared = null;
                latest = null;
                captureDone = false;
                stableFrames = 0;
                nextCommandTick = clock() + 30L;
                state = 0;
            }
        } catch (Throwable problem) {
            fail(mc, problem);
        }
    }

    public static boolean finished() {
        return finished;
    }

    public static JsonObject result() {
        return buildResult(resultStatus, failure).deepCopy();
    }

    @SubscribeEvent
    public static void tick(ClientTickEvent.Post event) {
        if (!enabled() || finished) return;
        Minecraft mc = Minecraft.getInstance();
        try {
            if (!running && autoStartedSession == null) {
                Path baselinePath = ClientIntegrationData.directory().resolve("client-results.json");
                if (!Files.exists(baselinePath)) return;
                JsonObject baseline = ClientIntegrationData.read("client-results.json");
                JsonObject ready = ClientIntegrationData.read("server-ready.json");
                if (!"passed".equals(baseline.get("status").getAsString()) || !"ready".equals(ready.get("status").getAsString())) return;
                String baselineSession = baseline.get("session").getAsString();
                if (!baselineSession.equals(ready.get("session").getAsString())) return;
                if (mc.player == null || mc.level == null || mc.getConnection() == null || mc.getConnection().getConnection().isMemoryConnection() || mc.screen != null) return;
                autoStartedSession = baselineSession;
                begin();
                ThermalShockReforged.LOGGER.info("Client world probe auto-started after baseline passed: session={}", baselineSession);
            }
            tick();
        } catch (Throwable problem) {
            if (running) fail(mc, problem);
            else {
                failure = problem;
                resultStatus = "failed";
                finished = true;
                writeResult("failed", problem);
                ThermalShockReforged.LOGGER.error("Client world probe auto-start failed", problem);
            }
        }
    }

    @SubscribeEvent
    public static void extractRenderState(ExtractLevelRenderStateEvent event) {
        if (!running || finished || state < 2 || state > 3) return;
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.level == null) return;
        try {
            latestSnapshotSequence = ++extractedSnapshotSequence;
            latestFrameSequence = activeFrameSequence + 1L;
            latest = observe(mc, event.getRenderState(), event.getRenderTick(), latestSnapshotSequence, latestFrameSequence);
            latestRenderTick = event.getRenderTick();
        } catch (Throwable problem) {
            failure = problem;
        }
    }
    @SubscribeEvent
    public static void framePre(RenderFrameEvent.Pre event) {
        if (running && !finished && state >= 2 && state <= 3) activeFrameSequence++;
    }

    @SubscribeEvent
    public static void frame(RenderFrameEvent.Post event) {
        if (!running || finished || state < 2 || state > 3) return;
        Minecraft mc = Minecraft.getInstance();
        if (activeFrameSequence <= 3L) ThermalShockReforged.LOGGER.debug("World probe frame pairing: scene={}, upcomingSnapshotFrame={}, renderedFrame={}, snapshot={}, consumed={}", scene(), latestFrameSequence, activeFrameSequence, latestSnapshotSequence, consumedSnapshotSequence);
        if (mc.player == null || mc.level == null || latest == null || latestFrameSequence != activeFrameSequence || latestSnapshotSequence <= consumedSnapshotSequence) return;
        try {
            JsonObject observation = latest.deepCopy();
            consumedSnapshotSequence = latestSnapshotSequence;
            if (state == 2) {
                if (matchesPreparedFixture(mc, observation)) stableFrames++;
                else stableFrames = 0;
            } else if (state == 3 && !capturePending && !captureDone) {
                takeScreenshot(mc, observation);
            }
        } catch (Throwable problem) {
            failure = problem;
        }
    }
    private static boolean matchesPreparedFixture(Minecraft mc, JsonObject observation) {
        if (mc.screen != null) return false;
        if (!mc.level.dimension().identifier().toString().equals(prepared.get("dimension").getAsString())) return false;
        if (!observation.get("biome").getAsString().equals(prepared.get("biome").getAsString())) return false;
        if (Math.abs(Mth.wrapDegrees(observation.get("cameraYaw").getAsFloat() - prepared.get("cameraYaw").getAsFloat())) > 1.0F || Math.abs(observation.get("cameraPitch").getAsFloat() - prepared.get("cameraPitch").getAsFloat()) > 1.0F) return false;
        String kind = prepared.get("sceneKind").getAsString();
        float intensity = observation.get("weatherIntensity").getAsFloat();
        if ("clear".equals(prepared.get("weatherRequested").getAsString()) && intensity > 0.0F) return false;
        if (kind.equals("weather_snow_radius_10") || kind.equals("weather_snow_radius_4") || kind.equals("rain")) {
            if (intensity <= 0.0F) return false;
            if (kind.equals("weather_snow_radius_10") || kind.equals("weather_snow_radius_4")) {
                int expectedRadius = kind.endsWith("10") ? 10 : 4;
                return observation.get("weatherRadiusOption").getAsInt() == 10
                        && observation.get("weatherRadius").getAsInt() == expectedRadius
                        && observation.get("snowColumnCount").getAsInt() > 0
                        && observation.get("rainColumnCount").getAsInt() == 0;
            }
            return observation.get("rainColumnCount").getAsInt() > 0;
        }
        if (kind.equals("no_precipitation")) return intensity > 0.0F
                && observation.get("rainColumnCount").getAsInt() == 0
                && observation.get("snowColumnCount").getAsInt() == 0;
        if (kind.equals("aqeron_moon")) return prepared.get("expectedMoonPhase").getAsString().equals(observation.get("moonPhase").getAsString())
                && Math.abs(observation.get("sunAngleDegrees").getAsFloat() - 180.0F) < 0.05F
                && Math.abs(observation.get("starBrightness").getAsFloat() - 0.5F) < 0.001F
                && observation.get("cloudColorAlpha").getAsInt() == 0
                && !observation.get("cloudPassGateOpen").getAsBoolean();
        if (kind.equals("sunrise") || kind.equals("noon") || kind.equals("sunset") || kind.equals("night")) {
            long expected = prepared.get("expectedClockTicks").getAsLong();
            return Math.abs(observation.get("clientClockTicks").getAsLong() - expected) <= 200L;
        }
        return true;
    }

    private static JsonObject observe(Minecraft mc, LevelRenderState levelState, int renderTick, long snapshotSequence, long frameSequence) {
        var gameState = mc.gameRenderer.getGameRenderState();
        WeatherRenderState weather = levelState.weatherRenderState;
        CameraRenderState camera = levelState.cameraRenderState;
        var sky = levelState.skyRenderState;
        BlockPos cameraBlock = camera.blockPos;
        String biome = mc.level.getBiome(cameraBlock).unwrapKey().orElseThrow().identifier().toString();
        var result = new JsonObject();
        result.addProperty("dimension", mc.level.dimension().identifier().toString());
        result.addProperty("biome", biome);
        result.addProperty("seaLevel", mc.level.getSeaLevel());
        result.addProperty("biomePrecipitationAtCamera", mc.level.getBiome(cameraBlock).value().getPrecipitationAt(cameraBlock, mc.level.getSeaLevel()).toString());
        result.addProperty("screen", mc.screen == null ? "none" : mc.screen.getClass().getName());
        result.addProperty("clientClockTicks", mc.level.clockManager().getTotalTicks(mc.level.registryAccess().getOrThrow(WorldClocks.OVERWORLD)));
        result.addProperty("clientGameTime", levelState.gameTime);
        result.addProperty("cameraX", camera.pos.x);
        result.addProperty("cameraY", camera.pos.y);
        result.addProperty("cameraZ", camera.pos.z);
        result.addProperty("cameraBlock", cameraBlock.toShortString());
        result.addProperty("cameraYaw", camera.yRot);
        result.addProperty("cameraPitch", camera.xRot);
        result.addProperty("weatherRadius", weather.radius);
        result.addProperty("weatherIntensity", weather.intensity);
        result.addProperty("rainColumnCount", weather.rainColumns.size());
        result.addProperty("snowColumnCount", weather.snowColumns.size());
        if (!weather.rainColumns.isEmpty() && scene().startsWith("weather_")) {
            JsonArray samples = new JsonArray();
            for (var column : weather.rainColumns) {
                var sample = new JsonObject();
                var pos = new BlockPos(column.x(), cameraBlock.getY(), column.z());
                sample.addProperty("position", pos.toShortString());
                sample.addProperty("biome", mc.level.getBiome(pos).unwrapKey().orElseThrow().identifier().toString());
                samples.add(sample);
                if (samples.size() == 8) break;
            }
            result.add("unexpectedRainColumnBiomes", samples);
        }
        result.addProperty("skybox", sky.skybox.toString());
        result.addProperty("sunAngleRadians", sky.sunAngle);
        result.addProperty("sunAngleDegrees", Math.toDegrees(sky.sunAngle));
        result.addProperty("moonAngleRadians", sky.moonAngle);
        result.addProperty("moonPhase", sky.moonPhase.getSerializedName());
        result.addProperty("starBrightness", sky.starBrightness);
        result.addProperty("skyColor", sky.skyColor);
        result.addProperty("cloudColor", levelState.cloudColor);
        result.addProperty("cloudColorAlpha", ARGB.alpha(levelState.cloudColor));
        result.addProperty("cloudHeight", levelState.cloudHeight);
        result.addProperty("weatherRadiusOption", mc.options.weatherRadius().get());
        result.addProperty("cloudStatus", gameState.optionsRenderState.cloudStatus.getSerializedName());
        result.addProperty("cloudPassGateOpen", gameState.optionsRenderState.cloudStatus != CloudStatus.OFF && ARGB.alpha(levelState.cloudColor) > 0);
        result.addProperty("customCloudRendererPresent", levelState.customCloudsRenderer != null);
        result.addProperty("fogType", camera.fogType.toString());
        result.addProperty("fogColorR", camera.fogData.color.x());
        result.addProperty("fogColorG", camera.fogData.color.y());
        result.addProperty("fogColorB", camera.fogData.color.z());
        result.addProperty("fogColorA", camera.fogData.color.w());
        result.addProperty("fogEnvironmentalStart", camera.fogData.environmentalStart);
        result.addProperty("fogEnvironmentalEnd", camera.fogData.environmentalEnd);
        result.addProperty("renderFrameCaptured", true);
        result.addProperty("renderStateCaptureSource", "ExtractLevelRenderStateEvent snapshot copied before LevelRenderState.reset");
        result.addProperty("renderTick", renderTick);
        result.addProperty("snapshotSequence", snapshotSequence);
        result.addProperty("renderFrameSequence", frameSequence);
        return result;
    }

    private static void validateLatest(JsonObject observation) {
        String scene = scene();
        if (scene.startsWith("weather_")) {
            int expectedRadius = scene.endsWith("radius10") ? 10 : 4;
            ClientIntegrationData.require(observation.get("weatherRadiusOption").getAsInt() == 10, "Client weather-radius input is not 10");
            ClientIntegrationData.require(observation.get("weatherRadius").getAsInt() == expectedRadius, "Actual WeatherRenderState radius differs from the production dimension rule");
            current.addProperty("inputWeatherRadius", 10);
            current.addProperty("productionWeatherRadiusRule", scene.equals("weather_loq_borean_snow_radius4") ? "LoqendiaWeatherMixin.reduceWeatherRadius: actual radius = input * 2 / 5" : "WeatherEffectRenderer uses the input option directly");
            ClientIntegrationData.require(observation.get("snowColumnCount").getAsInt() > 0, "Snow fixture produced no actual WeatherRenderState snow columns");
            ClientIntegrationData.require(observation.get("rainColumnCount").getAsInt() == 0, "Snow fixture unexpectedly produced rain columns");
        } else if (scene.equals("luminous_sea_rain")) {
            ClientIntegrationData.require(observation.get("rainColumnCount").getAsInt() > 0, "Luminous Sea fixture produced no actual rain columns");
        } else if (scene.equals("lunar_tundra_clear")) {
            ClientIntegrationData.require(observation.get("weatherIntensity").getAsFloat() > 0.0F, "Rain was not active while testing the no-precipitation biome");
            ClientIntegrationData.require(observation.get("rainColumnCount").getAsInt() == 0 && observation.get("snowColumnCount").getAsInt() == 0, "No-precipitation biome produced weather columns");
        } else if (scene.startsWith("aqeron_moon_")) {
            String phase = prepared.get("expectedMoonPhase").getAsString();
            ClientIntegrationData.require(phase.equals(observation.get("moonPhase").getAsString()), "Aqeron moon phase does not match the synchronized render state");
            ClientIntegrationData.require(Math.abs(observation.get("sunAngleDegrees").getAsFloat() - 180.0F) < 0.05F, "Aqeron sun angle is not 180 degrees in the render state");
            ClientIntegrationData.require(Math.abs(observation.get("starBrightness").getAsFloat() - 0.5F) < 0.001F, "Aqeron star brightness is not 0.5 in the render state");
            ClientIntegrationData.require(observation.get("cloudColorAlpha").getAsInt() == 0, "Aqeron cloud color is not fully transparent");
            ClientIntegrationData.require(!observation.get("cloudPassGateOpen").getAsBoolean(), "Aqeron cloud render gate is open despite the no-cloud fixture");
        } else if (scene.startsWith("loq_")) {
            long expected = prepared.get("expectedClockTicks").getAsLong();
            long actual = observation.get("clientClockTicks").getAsLong();
            ClientIntegrationData.require(Math.abs(actual - expected) <= 200L, "Loq render state is not near the prepared day-time marker");
        }
        current.add("renderState", observation.deepCopy());
        current.addProperty("stableRenderedFrames", stableFrames);
        current.addProperty("interpretation", "Measured from the real client's extracted GameRenderState after a server-controlled biome, weather, dimension, or clock setup; not natural world generation evidence");
    }

    private static void requestScreenshot() {
        capturePending = false;
        captureDone = false;
    }

    private static void takeScreenshot(Minecraft mc, JsonObject observation) {
        capturePending = true;
        JsonObject captured = observation.deepCopy();
        String name = "world-" + scene() + ".png";
        Path path = ClientIntegrationData.directory().resolve(name);
        captured.addProperty("image", path.toString());
        current.add("renderState", captured);
        Screenshot.takeScreenshot(mc.getMainRenderTarget(), image -> {
            try (image) {
                image.writeToFile(path);
                mc.execute(() -> {
                    capturePending = false;
                    captureDone = true;
                });
            } catch (Throwable problem) {
                mc.execute(() -> failure = problem);
            }
        });
    }

    private static long clock() {
        JsonObject evidence = ClientIntegrationData.read("server-clock.json");
        ClientIntegrationData.require(session.equals(evidence.get("session").getAsString()), "Server clock session changed");
        return evidence.get("serverTick").getAsLong();
    }

    private static String scene() {
        return sceneIndex < scenarios().size() ? scenarios().get(sceneIndex) : "finished";
    }

    private static void restoreWeatherRadius(Minecraft mc) {
        mc.options.weatherRadius().set(originalWeatherRadius);
        mc.options.save();
    }

    private static void fail(Minecraft mc, Throwable problem) {
        failure = problem;
        running = false;
        finished = true;
        resultStatus = "failed";
        try {
            restoreWeatherRadius(mc);
        } catch (Throwable restoreFailure) {
            problem.addSuppressed(restoreFailure);
        }
        writeResult("failed", problem);
        ThermalShockReforged.LOGGER.error("Client world probe failed: scene={}, state={}", scene(), state, problem);
    }

    private static JsonObject buildResult(String status, Throwable problem) {
        JsonObject result = new JsonObject();
        result.addProperty("status", status);
        result.addProperty("session", session == null ? "" : session);
        result.addProperty("expectedScenarios", scenarios().size());
        result.addProperty("completedScenarios", RESULTS.size());
        result.addProperty("transport", "Actual TCP player and server-issued scene setup; render values copied from ExtractLevelRenderStateEvent and consumed once by the matching RenderFrameEvent.Post");
        result.addProperty("state", state);
        result.addProperty("stableRenderedFrames", stableFrames);
        result.addProperty("latestSnapshotFrameSequence", latestFrameSequence);
        result.addProperty("activeRenderedFrameSequence", activeFrameSequence);
        result.addProperty("consumedSnapshotSequence", consumedSnapshotSequence);
        result.add("scenes", RESULTS.deepCopy());
        if (current != null) result.add("currentScene", current.deepCopy());
        if (latest != null) result.add("latestRenderState", latest.deepCopy());
        if (problem != null) result.addProperty("error", problem.toString());
        return result;
    }

    private static void writeResult(String status, Throwable problem) {
        ClientIntegrationData.write("world-results.json", buildResult(status, problem));
    }
}















