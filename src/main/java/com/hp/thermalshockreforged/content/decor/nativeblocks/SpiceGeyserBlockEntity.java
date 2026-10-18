package com.hp.thermalshockreforged.content.decor.nativeblocks;

import com.hp.thermalshockreforged.content.ModBlockEntities;
import com.hp.thermalshockreforged.content.farming.FarmInventoryBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public final class SpiceGeyserBlockEntity extends FarmInventoryBlockEntity {
    public SpiceGeyserBlockEntity(BlockPos pos, BlockState state) { super(ModBlockEntities.SPICE_GEYSER.get(), pos, state); }
}
