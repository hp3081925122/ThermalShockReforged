package com.hp.thermalshockreforged.world.thermite;

import com.hp.thermalshockreforged.ThermalShockReforged;
import java.util.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.*;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.server.ServerStoppedEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
import net.neoforged.neoforge.registries.*;

public final class ThermiteContent {
    private static final DeferredRegister<EntityType<?>> TYPES = DeferredRegister.create(Registries.ENTITY_TYPE, ThermalShockReforged.MOD_ID);
    public static final DeferredHolder<EntityType<?>, EntityType<ThermiteDetonationEntity>> DETONATION = TYPES.register("thermite_detonation", () -> EntityType.Builder.of(ThermiteDetonationEntity::new, MobCategory.MISC).noSummon().fireImmune().sized(0.01F, 0.01F).clientTrackingRange(0).updateInterval(20).build(ThermalShockReforged.MOD_ID + ":thermite_detonation"));
    private static final Map<MinecraftServer, Map<ResourceKey<Level>, LinkedHashSet<UUID>>> PENDING = new WeakHashMap<>();
    private ThermiteContent() { }
    public static void register(IEventBus bus) {
        TYPES.register(bus);
        NeoForge.EVENT_BUS.addListener(ThermiteContent::endTick);
        NeoForge.EVENT_BUS.addListener(ThermiteContent::stopped);
    }
    static void schedule(ServerLevel level, UUID id) {
        synchronized (PENDING) { PENDING.computeIfAbsent(level.getServer(), key -> new LinkedHashMap<>()).computeIfAbsent(level.dimension(), key -> new LinkedHashSet<>()).add(id); }
    }
    private static void endTick(ServerTickEvent.Post event) {
        Map<ResourceKey<Level>, LinkedHashSet<UUID>> snapshot;
        synchronized (PENDING) { snapshot = PENDING.remove(event.getServer()); }
        if (snapshot == null) return;
        for (var entry : snapshot.entrySet()) {
            ServerLevel level = event.getServer().getLevel(entry.getKey());
            if (level == null) continue;
            for (UUID id : entry.getValue()) if (level.getEntity(id) instanceof ThermiteDetonationEntity task) task.endTick(level);
        }
    }
    private static void stopped(ServerStoppedEvent event) { synchronized (PENDING) { PENDING.remove(event.getServer()); } }
}
