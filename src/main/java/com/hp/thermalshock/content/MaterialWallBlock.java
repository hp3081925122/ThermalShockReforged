package com.hp.thermalshock.content;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.WallBlock;
import net.minecraft.world.level.block.state.BlockState;

public final class MaterialWallBlock extends WallBlock {
    public MaterialWallBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected int getLightBlock(BlockState state, BlockGetter level, BlockPos position) {
        return 0;
    }
}
