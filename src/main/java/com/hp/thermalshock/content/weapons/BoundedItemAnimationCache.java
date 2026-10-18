package com.hp.thermalshock.content.weapons;

import java.util.HashMap;
import java.util.Map;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.animatable.instance.SingletonAnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;

final class BoundedItemAnimationCache extends SingletonAnimatableInstanceCache {
    private final Map<Long, Long> lastAccess = new HashMap<>();
    private long lastPruned;

    BoundedItemAnimationCache(GeoAnimatable item) { super(item); }

    @Override
    public synchronized AnimatableManager<?> getManagerForId(long id) {
        lastAccess.put(id, System.nanoTime());
        return super.getManagerForId(id);
    }

    synchronized void prune() {
        long now = System.nanoTime();
        if (now - lastPruned < 1_000_000_000L) return;
        lastPruned = now;
        var entries = lastAccess.entrySet().iterator();
        while (entries.hasNext()) {
            var entry = entries.next();
            if (now - entry.getValue() > 30_000_000_000L) {
                managers.remove(entry.getKey().longValue());
                entries.remove();
            }
        }
    }

    synchronized void clear() { lastAccess.clear(); managers.clear(); }
}
