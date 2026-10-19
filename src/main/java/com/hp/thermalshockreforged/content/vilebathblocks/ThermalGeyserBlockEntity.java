package com.hp.thermalshockreforged.content.vilebathblocks;

import com.hp.thermalshockreforged.content.farming.FarmInventoryBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public final class ThermalGeyserBlockEntity extends FarmInventoryBlockEntity {
    public ThermalGeyserBlockEntity(BlockPos pos, BlockState state) { super(VilebathBlocksContent.THERMAL_GEYSER_ENTITY.get(), pos, state); }
}
