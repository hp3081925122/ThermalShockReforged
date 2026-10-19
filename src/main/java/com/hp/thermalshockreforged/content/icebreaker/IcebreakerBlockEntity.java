package com.hp.thermalshockreforged.content.icebreaker;

import java.util.UUID;
import com.hp.thermalshockreforged.content.decor.geo.GeoDecorBlockEntity;
import com.hp.thermalshockreforged.content.decor.geo.GeoDecorKind;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.state.BlockState;

public final class IcebreakerBlockEntity extends GeoDecorBlockEntity {
    private UUID pulse;

    public IcebreakerBlockEntity(BlockPos pos, BlockState state) { super(GeoDecorKind.ICEBREAKER, pos, state); }

    boolean activated() { return pulse != null; }
    boolean owns(UUID id) { return id.equals(pulse); }
    void assign(UUID id) { pulse = id; setChanged(); }

    @Override protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        pulse = tag.hasUUID("IcebreakerPulse") ? tag.getUUID("IcebreakerPulse") : null;
    }

    @Override protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        if (pulse != null) tag.putUUID("IcebreakerPulse", pulse);
    }
}
