package com.hp.thermalshockreforged.entity.summon;

import java.util.*;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.*;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;
import com.mojang.serialization.Codec;
import net.minecraft.resources.Identifier;

public final class SummonLedger extends SavedData {
    private static final SavedDataType<SummonLedger> FACTORY = new SavedDataType<>(Identifier.fromNamespaceAndPath("thermal_shock_reforged", "mangler_summons"), SummonLedger::new, Codec.unit(new SummonLedger()));
    private final Map<UUID, Map<UUID, Long>> owners = new HashMap<>();
    private final TreeMap<Long, Set<Key>> expiry = new TreeMap<>();
    public static long clock(ServerLevel level) { return level.getServer().overworld().getGameTime(); }
    public static SummonLedger get(ServerLevel level) {
        var ledger = level.getServer().overworld().getDataStorage().computeIfAbsent(FACTORY);
        ledger.prune(clock(level));
        return ledger;
    }
    private void prune(long now) {
        while (!expiry.isEmpty() && expiry.firstKey() <= now) {
            var bucket = expiry.pollFirstEntry();
            for (var key : bucket.getValue()) { var children = owners.get(key.owner); if (children != null && Objects.equals(children.get(key.child), bucket.getKey())) { children.remove(key.child); if (children.isEmpty()) owners.remove(key.owner); setDirty(); } }
        }
    }
    public boolean reserve(UUID owner, UUID child, long until, int cap) {
        var children = owners.computeIfAbsent(owner, key -> new HashMap<>());
        if (children.size() >= cap) return false;
        children.put(child, until); expiry.computeIfAbsent(until, key -> new HashSet<>()).add(new Key(owner, child)); setDirty(); return true;
    }
    public List<UUID> reserveGroup(UUID owner, int count, long until, int cap) {
        if (count <= 0 || count > cap || owners.getOrDefault(owner, Map.of()).size() > cap - count) return List.of();
        var result = new ArrayList<UUID>(count);
        for (int i = 0; i < count; i++) { var reservation = UUID.randomUUID(); reserve(owner, reservation, until, cap); result.add(reservation); }
        return result;
    }
    public void replace(UUID owner, UUID reservation, UUID child, long until) { release(owner, reservation); owners.computeIfAbsent(owner, key -> new HashMap<>()).put(child, until); expiry.computeIfAbsent(until, key -> new HashSet<>()).add(new Key(owner, child)); setDirty(); }
    public void release(UUID owner, UUID child) {
        var children = owners.get(owner); if (children == null) return;
        var until = children.remove(child); if (until == null) return;
        if (children.isEmpty()) owners.remove(owner);
        var bucket = expiry.get(until); if (bucket != null) { bucket.remove(new Key(owner, child)); if (bucket.isEmpty()) expiry.remove(until); }
        setDirty();
    }
    private static SummonLedger load(CompoundTag tag, HolderLookup.Provider registries) {
        var ledger = new SummonLedger();
        for (var value : tag.getListOrEmpty("Leases")) {
            var lease = (CompoundTag) value;
            if (!lease.read("Owner", net.minecraft.core.UUIDUtil.CODEC).isPresent() || !lease.read("Child", net.minecraft.core.UUIDUtil.CODEC).isPresent()) continue;
            UUID owner = lease.read("Owner", net.minecraft.core.UUIDUtil.CODEC).orElseThrow(), child = lease.read("Child", net.minecraft.core.UUIDUtil.CODEC).orElseThrow(); long until = lease.getLongOr("Until", 0L);
            ledger.owners.computeIfAbsent(owner, key -> new HashMap<>()).put(child, until); ledger.expiry.computeIfAbsent(until, key -> new HashSet<>()).add(new Key(owner, child));
        }
        return ledger;
    }
    @Override
    public CompoundTag save(CompoundTag tag, HolderLookup.Provider registries) {
        var leases = new ListTag();
        owners.forEach((owner, children) -> children.forEach((child, until) -> { var value = new CompoundTag(); value.store("Owner", net.minecraft.core.UUIDUtil.CODEC, owner); value.store("Child", net.minecraft.core.UUIDUtil.CODEC, child); value.putLong("Until", until); leases.add(value); }));
        tag.put("Leases", leases); return tag;
    }
    private record Key(UUID owner, UUID child) { }
}
