package com.hp.thermalshockreforged.gametest;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.hp.thermalshockreforged.ThermalShockReforged;
import com.mojang.blaze3d.audio.Channel;
import java.util.ArrayList;
import java.util.List;
import java.nio.file.Files;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.resources.Identifier;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.sound.PlaySoundSourceEvent;
import net.neoforged.neoforge.client.event.sound.PlayStreamingSourceEvent;

@EventBusSubscriber(modid = ThermalShockReforged.MOD_ID, value = Dist.CLIENT)
public final class ClientAudioProbe {
    private static final float TEST_GAIN = 0.01F;
    private static final List<SoundEvent> EVENTS = new ArrayList<>();
    private static final JsonArray RESULTS = new JsonArray();
    private static final JsonArray NATURAL = new JsonArray();
    private static int index;
    private static int waitingTicks;
    private static volatile SoundInstance active;
    private static volatile boolean callbackPending;
    private static volatile boolean running;
    private static volatile boolean complete;
    private static volatile String armedNatural;
    private static JsonObject finalResult;
    private static boolean started;
    private static String session;
    private static final List<String> NATURAL_SCENES = List.of("rain_drum", "angry_rain_drum");
    private static final JsonArray NATURAL_RESULTS = new JsonArray();
    private static int naturalIndex;
    private static int naturalState;
    private static long naturalDeadline;
    private static long nextCommandTick;
    private static boolean naturalFinished;
    private static JsonObject naturalCurrent;
    private static JsonObject naturalPrepared;

    private ClientAudioProbe() {
    }

    public static void begin() {
        requireEnabledSocket();
        if (running) throw new IllegalStateException("Audio probe is already running");
        started = true;
        session = ClientIntegrationData.read("client-results.json").get("session").getAsString();
        EVENTS.clear();
        RESULTS.asList().clear();
        NATURAL.asList().clear();
        BuiltInRegistries.SOUND_EVENT.entrySet().stream()
                .filter(entry -> ThermalShockReforged.MOD_ID.equals(entry.getKey().identifier().getNamespace()))
                .sorted(java.util.Comparator.comparing(entry -> entry.getKey().identifier().toString()))
                .forEach(entry -> EVENTS.add(entry.getValue()));
        if (EVENTS.isEmpty()) throw new IllegalStateException("No registered TSR sound events were found");
        index = 0;
        waitingTicks = 0;
        active = null;
        callbackPending = false;
        complete = false;
        finalResult = null;
        running = true;
        ThermalShockReforged.LOGGER.info("Audio resource probe started for {} registered TSR sound events", EVENTS.size());
    }

    public static void armNatural(String soundPath) {
        requireEnabledSocket();
        if (!soundPath.equals("rain_drum_random") && !soundPath.equals("angry_rain_drum_random")) throw new IllegalArgumentException("Unsupported natural sound observation: " + soundPath);
        armedNatural = soundPath;
        NATURAL.asList().clear();
    }

    public static void disarmNatural() {
        armedNatural = null;
    }

    public static void tick() {
        if (!enabledAndConnected() || !running || complete) return;
        if (active != null) {
            if (++waitingTicks > 200) finishFailure("No sound source callback for " + BuiltInRegistries.SOUND_EVENT.getKey(EVENTS.get(index)));
            return;
        }
        if (index >= EVENTS.size()) {
            finishSuccess();
            return;
        }
        SoundEvent event = EVENTS.get(index);
        active = SimpleSoundInstance.forUI(event, 1.0F, TEST_GAIN);
        waitingTicks = 0;
        Minecraft.getInstance().getSoundManager().play(active);
    }

    @SubscribeEvent
    public static void tick(ClientTickEvent.Post event) {
        if (!enabledAndConnected()) return;
        try {
            if (!started) {
                if (!Files.exists(ClientIntegrationData.directory().resolve("client-results.json"))) return;
                var baseline = ClientIntegrationData.read("client-results.json");
                if (!baseline.get("status").getAsString().equals("passed")) return;
                var server = ClientIntegrationData.read("server-ready.json");
                if (!baseline.get("session").getAsString().equals(server.get("session").getAsString())) throw new IllegalStateException("Audio probe baseline belongs to another server session");
                begin();
            }
            tick();
            if (complete && finalResult != null && finalResult.get("status").getAsString().equals("passed") && !naturalFinished) tickNatural();
        } catch (Throwable failure) {
            if (!complete) finishFailure(failure.toString());
            else if (!naturalFinished) finishNatural(failure);
        }
    }

    private static void tickNatural() {
        var mc = Minecraft.getInstance();
        if (naturalState != 0 && System.nanoTime() > naturalDeadline) throw new IllegalStateException("Natural audio probe timed out: scene=" + NATURAL_SCENES.get(naturalIndex) + ", state=" + naturalState);
        if (naturalState == 0) {
            if (naturalIndex == NATURAL_SCENES.size()) {
                finishNatural(null);
                return;
            }
            if (mc.screen != null || serverTick() < nextCommandTick) return;
            String scene = NATURAL_SCENES.get(naturalIndex);
            naturalCurrent = new JsonObject();
            naturalCurrent.addProperty("scene", scene);
            naturalCurrent.addProperty("session", session);
            sendAudioCommand("prepare", scene);
            moveNatural(1);
            return;
        }
        String scene = NATURAL_SCENES.get(naturalIndex);
        if (naturalState == 1) {
            String name = "audio-" + scene + "-prepared.json";
            if (!Files.exists(ClientIntegrationData.directory().resolve(name))) return;
            naturalPrepared = ClientIntegrationData.read(name);
            requireNaturalEvidence(naturalPrepared, scene);
            naturalCurrent.add("serverPrepared", naturalPrepared.deepCopy());
            nextCommandTick = Math.max(nextCommandTick, naturalPrepared.get("serverTick").getAsLong() + 30);
            moveNatural(2);
            return;
        }
        if (naturalState == 2) {
            if (!mc.level.dimension().identifier().toString().equals(naturalPrepared.get("dimension").getAsString())) return;
            if (mc.level.getRainLevel(1.0F) < 0.99F || serverTick() < nextCommandTick || mc.screen != null) return;
            var drumPos = new net.minecraft.core.BlockPos(naturalPrepared.get("drumX").getAsInt(), naturalPrepared.get("drumY").getAsInt(), naturalPrepared.get("drumZ").getAsInt());
            var leverPos = new net.minecraft.core.BlockPos(naturalPrepared.get("leverX").getAsInt(), naturalPrepared.get("leverY").getAsInt(), naturalPrepared.get("leverZ").getAsInt());
            if (!BuiltInRegistries.BLOCK.getKey(mc.level.getBlockState(drumPos).getBlock()).toString().equals("thermal_shock_reforged:rain_drum")) return;
            if (!mc.level.getBlockState(leverPos).is(net.minecraft.world.level.block.Blocks.LEVER)) return;
            if (mc.level.getBlockState(leverPos).getValue(net.minecraft.world.level.block.LeverBlock.POWERED)) throw new IllegalStateException("Audio fixture lever was powered before the requested production trigger");
            Identifier sound = Identifier.parse(naturalPrepared.get("expectedSound").getAsString());
            armNatural(sound.getPath());
            naturalCurrent.addProperty("triggerRequestedServerTick", serverTick());
            sendAudioCommand("trigger", scene);
            moveNatural(3);
            return;
        }
        if (naturalState == 3) {
            String name = "audio-" + scene + "-triggered.json";
            if (!Files.exists(ClientIntegrationData.directory().resolve(name))) return;
            var triggered = ClientIntegrationData.read(name);
            requireNaturalEvidence(triggered, scene);
            ClientIntegrationData.require(triggered.get("leverPowered").getAsBoolean() && triggered.get("neighborSignal").getAsInt() > 0, "Production rain-drum trigger did not power the lever and drum neighbor signal");
            ClientIntegrationData.require(triggered.get("expectedSound").equals(naturalPrepared.get("expectedSound")), "Natural audio trigger changed the expected sound");
            if (NATURAL.isEmpty()) return;
            naturalCurrent.add("serverTriggered", triggered.deepCopy());
            naturalCurrent.add("soundSourceCallbacks", NATURAL.deepCopy());
            naturalCurrent.addProperty("status", "passed");
            naturalCurrent.addProperty("meaning", "Connected client observed the original rain-drum sound while the real server lever/redstone trigger succeeded. Native client animateTick may also trigger this sound; callbacks are not attributed to a unique packet.");
            NATURAL_RESULTS.add(naturalCurrent.deepCopy());
            disarmNatural();
            naturalIndex++;
            nextCommandTick = serverTick() + 30;
            naturalState = 0;
            writeNatural("running", null);
        }
    }

    private static void requireNaturalEvidence(JsonObject evidence, String scene) {
        ClientIntegrationData.require(evidence.get("session").getAsString().equals(session), "Natural audio evidence belongs to another session");
        ClientIntegrationData.require(evidence.get("scene").getAsString().equals(scene), "Natural audio evidence belongs to another scene");
        ClientIntegrationData.require(evidence.get("status").getAsString().equals("passed"), "Natural audio server step failed: " + evidence);
    }

    private static long serverTick() {
        var clock = ClientIntegrationData.read("server-clock.json");
        ClientIntegrationData.require(clock.get("session").getAsString().equals(session), "Audio server clock belongs to another session");
        return clock.get("serverTick").getAsLong();
    }

    private static void sendAudioCommand(String phase, String scene) {
        long tick = serverTick();
        ClientIntegrationData.require(tick >= nextCommandTick, "Audio command must be throttled by observed server ticks");
        Minecraft.getInstance().player.connection.sendCommand("tsr_audio " + phase + " " + scene);
        nextCommandTick = tick + 30;
    }

    private static void moveNatural(int state) {
        naturalState = state;
        naturalDeadline = System.nanoTime() + 120_000_000_000L;
    }

    private static void finishNatural(Throwable failure) {
        naturalFinished = true;
        disarmNatural();
        writeNatural(failure == null ? "passed" : "failed", failure);
        if (failure == null) ThermalShockReforged.LOGGER.info("Natural audio probe passed: scenes={}", NATURAL_RESULTS.size());
        else ThermalShockReforged.LOGGER.error("Natural audio probe failed: sceneIndex={}, state={}", naturalIndex, naturalState, failure);
    }

    private static void writeNatural(String status, Throwable failure) {
        var result = new JsonObject();
        result.addProperty("status", status);
        result.addProperty("session", session);
        result.addProperty("expectedScenarios", NATURAL_SCENES.size());
        result.addProperty("completedScenarios", NATURAL_RESULTS.size());
        result.addProperty("state", naturalState);
        result.add("scenarios", NATURAL_RESULTS.deepCopy());
        if (naturalCurrent != null) result.add("currentScenario", naturalCurrent.deepCopy());
        if (failure != null) result.addProperty("error", failure.toString());
        ClientIntegrationData.write("audio-natural-results.json", result);
    }

    @SubscribeEvent
    public static void nonStreaming(PlaySoundSourceEvent event) {
        observed(event.getSound(), event.getChannel(), false);
    }

    @SubscribeEvent
    public static void streaming(PlayStreamingSourceEvent event) {
        observed(event.getSound(), event.getChannel(), true);
    }

    private static void observed(SoundInstance sound, Channel channel, boolean streaming) {
        if (!ClientIntegrationData.enabled() || !Boolean.getBoolean("tsr.clientIntegrationAudioProbe")) return;
        synchronized (ClientAudioProbe.class) {
            if (running && !complete && active == sound) {
                if (callbackPending) return;
                callbackPending = true;
            }
        }
        Minecraft.getInstance().execute(() -> {
            if (enabledAndConnected()) processObserved(sound, channel, streaming);
        });
    }

    private static void processObserved(SoundInstance sound, Channel channel, boolean streaming) {
        if (!enabledAndConnected()) return;
        Identifier id = sound.getIdentifier();
        if (armedNatural != null && id.getNamespace().equals(ThermalShockReforged.MOD_ID) && id.getPath().equals(armedNatural)) {
            JsonObject evidence = new JsonObject();
            evidence.addProperty("event", id.toString());
            evidence.addProperty("streaming", streaming);
            evidence.addProperty("channel", Integer.toHexString(System.identityHashCode(channel)));
            NATURAL.add(evidence);
            ThermalShockReforged.LOGGER.info("Naturally triggered sound observed: {} streaming={}", id, streaming);
        }
        if (!running || complete || active == null || sound != active) return;
        Identifier expected = BuiltInRegistries.SOUND_EVENT.getKey(EVENTS.get(index));
        if (!expected.equals(id)) {
            finishFailure("Started sound identifier mismatch: expected " + expected + " but observed " + id);
            return;
        }
        JsonObject evidence = new JsonObject();
        evidence.addProperty("event", id.toString());
        evidence.addProperty("streaming", streaming);
        evidence.addProperty("resourceStarted", true);
        evidence.addProperty("channel", Integer.toHexString(System.identityHashCode(channel)));
        evidence.addProperty("gain", TEST_GAIN);
        RESULTS.add(evidence);
        Minecraft.getInstance().getSoundManager().stop(active);
        active = null;
        callbackPending = false;
        waitingTicks = 0;
        index++;
    }

    public static boolean finished() {
        return complete;
    }

    public static JsonObject result() {
        return finalResult == null ? null : finalResult.deepCopy();
    }

    public static JsonArray naturalObservations() {
        return NATURAL.deepCopy();
    }

    private static void finishSuccess() {
        if (RESULTS.size() != EVENTS.size()) {
            finishFailure("Observed " + RESULTS.size() + " started sound sources for " + EVENTS.size() + " registered sound events");
            return;
        }
        complete = true;
        running = false;
        JsonObject result = baseResult();
        result.addProperty("status", "passed");
        result.addProperty("gain", TEST_GAIN);
        result.addProperty("meaning", "Registered sound resources loaded and sound sources started at reduced test gain; this does not prove all natural triggers or complete-track listening at normal volume");
        result.add("events", RESULTS.deepCopy());
        result.add("naturalObservations", NATURAL.deepCopy());
        finalResult = result;
        ClientIntegrationData.write("audio-probe-result.json", result);
        ThermalShockReforged.LOGGER.info("Audio resource probe completed for {} registered sound events", EVENTS.size());
    }

    private static void finishFailure(String reason) {
        if (active != null) Minecraft.getInstance().getSoundManager().stop(active);
        active = null;
        callbackPending = false;
        complete = true;
        running = false;
        JsonObject result = baseResult();
        result.addProperty("status", "failed");
        result.addProperty("reason", reason);
        result.addProperty("completedCount", RESULTS.size());
        result.add("events", RESULTS.deepCopy());
        result.add("naturalObservations", NATURAL.deepCopy());
        finalResult = result;
        ClientIntegrationData.write("audio-probe-result.json", result);
        ThermalShockReforged.LOGGER.error("Audio resource probe failed: {}", reason);
    }

    private static JsonObject baseResult() {
        JsonObject result = new JsonObject();
        result.addProperty("session", session);
        result.addProperty("registeredCount", EVENTS.size());
        result.addProperty("observedCount", RESULTS.size());
        result.addProperty("testGain", TEST_GAIN);
        return result;
    }

    private static void requireEnabledSocket() {
        if (!enabledAndConnected()) throw new IllegalStateException("Audio probe requires enabled client integration and a real socket connection");
    }

    private static boolean enabledAndConnected() {
        Minecraft minecraft = Minecraft.getInstance();
        return ClientIntegrationData.enabled() && Boolean.getBoolean("tsr.clientIntegrationAudioProbe") && minecraft.getConnection() != null && !minecraft.getConnection().getConnection().isMemoryConnection();
    }
}
