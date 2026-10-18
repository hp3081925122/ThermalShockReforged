package com.hp.thermalshockreforged.entity.mangler;

import java.util.*;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.*;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;

final class SummonLedger extends SavedData {
    private static final Factory<SummonLedger> FACTORY = new Factory<>(SummonLedger::new, SummonLedger::load);
    private final Map<UUID, Map<UUID, Long>> owners = new HashMap<>();
    private final TreeMap<Long, Set<Key>> expiry = new TreeMap<>();
    static long clock(ServerLevel level) { return level.getServer().overworld().getGameTime(); }
    static SummonLedger get(ServerLevel level) {
        var ledger = level.getServer().overworld().getDataStorage().computeIfAbsent(FACTORY, "thermal_shock_reforged_mangler_summons");
        ledger.prune(clock(level));
        return ledger;
    }
    private void prune(long now) {
        while (!expiry.isEmpty() && expiry.firstKey() <= now) {
            var bucket = expiry.pollFirstEntry();
            for (var key : bucket.getValue()) { var children = owners.get(key.owner); if (children != null && Objects.equals(children.get(key.child), bucket.getKey())) { children.remove(key.child); if (children.isEmpty()) owners.remove(key.owner); setDirty(); } }
        }
    }
    boolean reserve(UUID owner, UUID child, long until, int cap) {
        var children = owners.computeIfAbsent(owner, key -> new HashMap<>());
        if (children.size() >= cap) return false;
        children.put(child, until); expiry.computeIfAbsent(until, key -> new HashSet<>()).add(new Key(owner, child)); setDirty(); return true;
    }
    void replace(UUID owner, UUID reservation, UUID child, long until) { release(owner, reservation); owners.computeIfAbsent(owner, key -> new HashMap<>()).put(child, until); expiry.computeIfAbsent(until, key -> new HashSet<>()).add(new Key(owner, child)); setDirty(); }
    void release(UUID owner, UUID child) {
        var children = owners.get(owner); if (children == null) return;
        var until = children.remove(child); if (until == null) return;
        if (children.isEmpty()) owners.remove(owner);
        var bucket = expiry.get(until); if (bucket != null) { bucket.remove(new Key(owner, child)); if (bucket.isEmpty()) expiry.remove(until); }
        setDirty();
    }
    private static SummonLedger load(CompoundTag tag, HolderLookup.Provider registries) {
        var ledger = new SummonLedger();
        for (var value : tag.getList("Leases", Tag.TAG_COMPOUND)) {
            var lease = (CompoundTag) value;
            if (!lease.hasUUID("Owner") || !lease.hasUUID("Child")) continue;
            UUID owner = lease.getUUID("Owner"), child = lease.getUUID("Child"); long until = lease.getLong("Until");
            ledger.owners.computeIfAbsent(owner, key -> new HashMap<>()).put(child, until); ledger.expiry.computeIfAbsent(until, key -> new HashSet<>()).add(new Key(owner, child));
        }
        return ledger;
    }
    @Override
    public CompoundTag save(CompoundTag tag, HolderLookup.Provider registries) {
        var leases = new ListTag();
        owners.forEach((owner, children) -> children.forEach((child, until) -> { var value = new CompoundTag(); value.putUUID("Owner", owner); value.putUUID("Child", child); value.putLong("Until", until); leases.add(value); }));
        tag.put("Leases", leases); return tag;
    }
    private record Key(UUID owner, UUID child) { }
}
