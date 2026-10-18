package com.hp.thermalshock.content.terrain;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.MapColor;

public final class SnowCoveredLeaves extends UnderbrushLeaves {
    public static final IntegerProperty BLOCKSTATE = IntegerProperty.create("blockstate", 0, 1);

    public SnowCoveredLeaves(boolean bright) {
        super(bright ? MapColor.COLOR_YELLOW : MapColor.COLOR_ORANGE, 6, true);
        registerDefaultState(defaultBlockState().setValue(BLOCKSTATE, 0));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(BLOCKSTATE);
    }

    @Override
    protected void neighborChanged(BlockState state, Level level, BlockPos pos, Block neighbor, BlockPos from, boolean moving) {
        super.neighborChanged(state, level, pos, neighbor, from, moving);
        if (!level.isClientSide) {
            int snowy = level.getBlockState(pos.above()).is(Blocks.SNOW) ? 1 : 0;
            if (state.getValue(BLOCKSTATE) != snowy) level.setBlock(pos, state.setValue(BLOCKSTATE, snowy), 3);
        }
    }
}
