package com.hp.thermalshockreforged.content;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.FenceBlock;
import net.minecraft.world.level.block.state.BlockState;

public final class MaterialFenceBlock extends FenceBlock {
    public MaterialFenceBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected int getLightDampening(BlockState state) {
        return 0;
    }
}
