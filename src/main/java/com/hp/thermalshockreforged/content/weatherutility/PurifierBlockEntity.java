package com.hp.thermalshockreforged.content.weatherutility;

import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public final class PurifierBlockEntity extends BlockEntity {
    private UUID pulse;
    public PurifierBlockEntity(BlockPos pos, BlockState state) { super(WeatherUtilityContent.PURIFIER_ENTITY.get(), pos, state); }
    boolean activated() { return pulse != null; }
    void assign(UUID id) { pulse = id; setChanged(); }
    boolean owns(UUID id) { return id.equals(pulse); }
    @Override protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        pulse = tag.hasUUID("PurifierPulse") ? tag.getUUID("PurifierPulse") : null;
    }
    @Override protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        if (pulse != null) tag.putUUID("PurifierPulse", pulse);
    }
}
