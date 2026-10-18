package com.hp.thermalshock.content.farming;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public final class SeedBlockEntity extends FarmInventoryBlockEntity {
    public SeedBlockEntity(CropKind kind, BlockPos pos, BlockState state) { super(FarmingContent.seedEntity(kind), pos, state); }
}
