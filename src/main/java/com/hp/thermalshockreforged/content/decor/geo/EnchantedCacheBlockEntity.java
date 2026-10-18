package com.hp.thermalshockreforged.content.decor.geo;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.state.BlockState;

public final class EnchantedCacheBlockEntity extends GeoDecorBlockEntity {
    private boolean consumed;
    public EnchantedCacheBlockEntity(BlockPos pos, BlockState state) { super(GeoDecorKind.ENCHANTED_CACHE, pos, state); }
    boolean claim() { if (consumed) return false; consumed = true; setChanged(); return true; }
    void release() { consumed = false; setChanged(); }
    @Override protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) { super.loadAdditional(tag, registries); consumed = tag.getBoolean("EnchantedCacheConsumed"); }
    @Override protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) { super.saveAdditional(tag, registries); tag.putBoolean("EnchantedCacheConsumed", consumed); }
}
