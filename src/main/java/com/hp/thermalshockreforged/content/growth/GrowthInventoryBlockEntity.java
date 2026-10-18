package com.hp.thermalshockreforged.content.growth;

import com.hp.thermalshockreforged.content.farming.FarmInventoryBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public final class GrowthInventoryBlockEntity extends FarmInventoryBlockEntity {
    public GrowthInventoryBlockEntity(BlockPos pos, BlockState state) { super(GrowthContent.inventoryType(state.getBlock()), pos, state); }
}
