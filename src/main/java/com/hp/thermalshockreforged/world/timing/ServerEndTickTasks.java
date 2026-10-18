package com.hp.thermalshockreforged.world.timing;

import java.util.*;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.server.ServerStoppedEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;

public final class ServerEndTickTasks {
    private static final Map<MinecraftServer, Map<ResourceKey<Level>, LinkedHashSet<UUID>>> PENDING = new WeakHashMap<>();
    private ServerEndTickTasks() { }
    public static void register() {
        NeoForge.EVENT_BUS.addListener(ServerEndTickTasks::endTick);
        NeoForge.EVENT_BUS.addListener(ServerEndTickTasks::stopped);
    }
    public static void schedule(ServerLevel level, UUID id) {
        synchronized (PENDING) { PENDING.computeIfAbsent(level.getServer(), key -> new LinkedHashMap<>()).computeIfAbsent(level.dimension(), key -> new LinkedHashSet<>()).add(id); }
    }
    private static void endTick(ServerTickEvent.Post event) {
        Map<ResourceKey<Level>, LinkedHashSet<UUID>> snapshot;
        synchronized (PENDING) { snapshot = PENDING.remove(event.getServer()); }
        if (snapshot == null) return;
        for (var entry : snapshot.entrySet()) {
            ServerLevel level = event.getServer().getLevel(entry.getKey());
            if (level == null) continue;
            for (UUID id : entry.getValue()) if (level.getEntity(id) instanceof ServerEndTickTask task) task.endTick(level);
        }
    }
    private static void stopped(ServerStoppedEvent event) { synchronized (PENDING) { PENDING.remove(event.getServer()); } }
}
