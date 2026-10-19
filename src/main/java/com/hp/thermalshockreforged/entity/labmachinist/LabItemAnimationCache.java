package com.hp.thermalshockreforged.entity.labmachinist;

import java.util.*;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.animatable.instance.SingletonAnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;

final class LabItemAnimationCache extends SingletonAnimatableInstanceCache {
    private final Map<Long, Long> accessed = new HashMap<>();
    private long lastPruned;
    LabItemAnimationCache(GeoAnimatable item) { super(item); }
    @Override public synchronized AnimatableManager<?> getManagerForId(long id) { accessed.put(id, System.nanoTime()); return super.getManagerForId(id); }
    synchronized void prune() {
        long now = System.nanoTime(); if (now - lastPruned < 1_000_000_000L) return; lastPruned = now;
        var entries = accessed.entrySet().iterator(); while (entries.hasNext()) { var entry = entries.next(); if (now - entry.getValue() > 30_000_000_000L) { managers.remove(entry.getKey().longValue()); entries.remove(); } }
    }
    synchronized void clear() { accessed.clear(); managers.clear(); }
}
