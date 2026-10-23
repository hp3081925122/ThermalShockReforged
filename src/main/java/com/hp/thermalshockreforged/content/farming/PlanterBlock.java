package com.hp.thermalshockreforged.content.farming;

import net.minecraft.core.*;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.util.TriState;

public final class PlanterBlock extends Block {
    public PlanterBlock(boolean metal) {
        super(Properties.of().mapColor(metal ? MapColor.METAL : MapColor.WOOD).sound(metal ? SoundType.METAL : SoundType.WOOD).strength(1, 3.8f).requiresCorrectToolForDrops());
    }
    @Override protected int getLightBlock(BlockState state, BlockGetter level, BlockPos pos) { return 15; }
    @Override public TriState canSustainPlant(BlockState state, BlockGetter level, BlockPos pos, Direction face, BlockState plant) { return TriState.TRUE; }
}
