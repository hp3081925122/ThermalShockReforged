package com.hp.thermalshockreforged.content.terrain;

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
        this(bright ? MapColor.COLOR_YELLOW : MapColor.COLOR_ORANGE, 6, true);
    }

    public SnowCoveredLeaves(MapColor color, int opacity, boolean dense) {
        super(color, opacity, dense);
        registerDefaultState(defaultBlockState().setValue(BLOCKSTATE, 0));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(BLOCKSTATE);
    }

    @Override
    protected void neighborChanged(BlockState state, Level level, BlockPos pos, Block neighbor, net.minecraft.world.level.redstone.Orientation from, boolean moving) {
        super.neighborChanged(state, level, pos, neighbor, from, moving);
        if (!level.isClientSide()) {
            int snowy = level.getBlockState(pos.above()).is(Blocks.SNOW) ? 1 : 0;
            if (state.getValue(BLOCKSTATE) != snowy) level.setBlock(pos, state.setValue(BLOCKSTATE, snowy), 3);
        }
    }
}
