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
    @Override protected void loadAdditional(net.minecraft.world.level.storage.ValueInput input) { super.loadAdditional(input); consumed = input.getBooleanOr("CacheConsumed", false) || input.getBooleanOr("EnchantedCacheConsumed", false); }
    @Override protected void saveAdditional(net.minecraft.world.level.storage.ValueOutput output) { super.saveAdditional(output); output.putBoolean("CacheConsumed", consumed); }
}
