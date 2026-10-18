package com.hp.thermalshockreforged.content.decor.geo;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.state.BlockState;

public final class CacheBlockEntity extends GeoDecorBlockEntity {
    private boolean consumed;
    public CacheBlockEntity(GeoDecorKind kind, BlockPos pos, BlockState state) { super(kind, pos, state); }
    boolean claim() { if (consumed) return false; consumed = true; setChanged(); return true; }
    void release() { consumed = false; setChanged(); }
    @Override protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) { super.loadAdditional(tag, registries); consumed = tag.getBoolean("CacheConsumed") || tag.getBoolean("EnchantedCacheConsumed"); }
    @Override protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) { super.saveAdditional(tag, registries); tag.putBoolean("CacheConsumed", consumed); }
}
