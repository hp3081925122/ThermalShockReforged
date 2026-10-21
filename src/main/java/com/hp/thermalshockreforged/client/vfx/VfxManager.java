package com.hp.thermalshockreforged.client.vfx;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.hp.thermalshockreforged.ThermalShockReforged;
import java.io.Reader;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import net.neoforged.neoforge.client.event.RegisterClientReloadListenersEvent;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;

@EventBusSubscriber(modid = ThermalShockReforged.MOD_ID, value = Dist.CLIENT)
public final class VfxManager {
    private static final int MAX_EMITTERS = 1024;
    private static final float MAX_FRAMES_PER_UPDATE = 10;
    private static final double MAX_DISTANCE_SQUARED = 160 * 160;
    private static final Map<ResourceLocation, VfxDefinition> DEFINITIONS = new HashMap<>();
    private static final ArrayDeque<VfxEmitter> EMITTERS = new ArrayDeque<>();
    private static final Set<ResourceLocation> MISSING = new HashSet<>();
    private static final VfxBatch BATCH = new VfxBatch();
    private static Level level;
    private static long lastTime;

    private VfxManager() { }

    public static void play(Level world, ResourceLocation effect, double x, double y, double z) {
        if (!world.isClientSide()) return;
        VfxDefinition definition = DEFINITIONS.get(effect);
        if (definition == null) {
            if (MISSING.add(effect)) ThermalShockReforged.LOGGER.warn("Missing visual effect definition: {}", effect);
            return;
        }
        Camera camera = Minecraft.getInstance().gameRenderer.getMainCamera();
        if (camera.getPosition().distanceToSqr(x, y, z) > MAX_DISTANCE_SQUARED) return;
        if (world != level) {
            EMITTERS.clear();
            level = world;
        }
        while (EMITTERS.size() >= MAX_EMITTERS) EMITTERS.pollFirst();
        EMITTERS.addLast(new VfxEmitter(definition, x, y, z));
    }

    @SubscribeEvent
    public static void render(RenderLevelStageEvent event) {
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_WEATHER) return;
        Minecraft minecraft = Minecraft.getInstance();
        long now = System.nanoTime();
        float frames = 0;
        if (lastTime != 0 && !minecraft.isPaused()) frames = Math.min(MAX_FRAMES_PER_UPDATE, (now - lastTime) / 1.0E9F * 60);
        lastTime = now;
        if (EMITTERS.isEmpty()) return;
        if (minecraft.level != level) {
            EMITTERS.clear();
            return;
        }
        Iterator<VfxEmitter> iterator = EMITTERS.iterator();
        while (iterator.hasNext()) {
            if (!iterator.next().update(frames)) iterator.remove();
        }
        Camera camera = event.getCamera();
        Vec3 position = camera.getPosition();
        List<VfxEmitter> sorted = new ArrayList<>(EMITTERS);
        sorted.sort((first, second) -> Double.compare(distance(second, position), distance(first, position)));
        BATCH.begin(camera.getLookVector());
        for (VfxEmitter emitter : sorted) {
            BATCH.origin(emitter.x - position.x, emitter.y - position.y, emitter.z - position.z);
            emitter.render(BATCH);
        }
        BATCH.end();
    }

    private static double distance(VfxEmitter emitter, Vec3 position) { return position.distanceToSqr(emitter.x, emitter.y, emitter.z); }

    @SubscribeEvent
    public static void loggingOut(ClientPlayerNetworkEvent.LoggingOut event) {
        EMITTERS.clear();
        level = null;
    }

    @EventBusSubscriber(modid = ThermalShockReforged.MOD_ID, value = Dist.CLIENT, bus = EventBusSubscriber.Bus.MOD)
    public static final class Loader {
        private Loader() { }

        @SubscribeEvent
        public static void register(RegisterClientReloadListenersEvent event) {
            event.registerReloadListener((ResourceManagerReloadListener) VfxManager::reload);
        }
    }

    private static void reload(ResourceManager manager) {
        DEFINITIONS.clear();
        MISSING.clear();
        EMITTERS.clear();
        for (Map.Entry<ResourceLocation, Resource> entry : manager.listResources("vfx", location -> location.getPath().endsWith(".json")).entrySet()) {
            ResourceLocation file = entry.getKey();
            String path = file.getPath().substring("vfx/".length(), file.getPath().length() - ".json".length());
            try (Reader reader = entry.getValue().openAsReader()) {
                JsonObject json = JsonParser.parseReader(reader).getAsJsonObject();
                DEFINITIONS.put(ResourceLocation.fromNamespaceAndPath(file.getNamespace(), path), VfxDefinition.parse(json));
            } catch (Exception exception) {
                ThermalShockReforged.LOGGER.error("Failed to load visual effect {}", file, exception);
            }
        }
        ThermalShockReforged.LOGGER.debug("Loaded {} visual effect definitions", DEFINITIONS.size());
    }
}
