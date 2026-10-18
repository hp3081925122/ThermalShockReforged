package com.hp.thermalshock.content;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.state.BlockState;

public final class MaterialPillarBlock extends RotatedPillarBlock {
    private final int flammability;

    public MaterialPillarBlock(Properties properties, int flammability) {
        super(properties);
        this.flammability = flammability;
    }

    @Override
    protected int getLightBlock(BlockState state, BlockGetter level, BlockPos position) {
        return 15;
    }

    @Override
    public int getFlammability(BlockState state, BlockGetter level, BlockPos position, Direction face) {
        return flammability < 0 ? super.getFlammability(state, level, position, face) : flammability;
    }
}
