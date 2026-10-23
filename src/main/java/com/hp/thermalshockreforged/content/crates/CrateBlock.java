package com.hp.thermalshockreforged.content.crates;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.*;
import net.minecraft.world.level.block.state.properties.EnumProperty;

class CrateBlock extends FallingBlock {
    private static final MapCodec<CrateBlock> CODEC = simpleCodec(CrateBlock::new);
    CrateBlock(Properties properties) { super(properties); }
    @Override protected MapCodec<? extends FallingBlock> codec() { return CODEC; }
    @Override protected int getLightBlock(BlockState state, BlockGetter level, BlockPos pos) { return 15; }

    static final class Frigid extends CrateBlock {
        private static final MapCodec<Frigid> CODEC = simpleCodec(Frigid::new);
        private static final EnumProperty<Direction> FACING = HorizontalDirectionalBlock.FACING;
        Frigid(Properties properties) { super(properties); registerDefaultState(defaultBlockState().setValue(FACING, Direction.NORTH)); }
        @Override protected MapCodec<? extends FallingBlock> codec() { return CODEC; }
        @Override protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) { builder.add(FACING); }
        @Override public BlockState getStateForPlacement(BlockPlaceContext context) { return defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite()); }
        @Override protected BlockState rotate(BlockState state, Rotation rotation) { return state.setValue(FACING, rotation.rotate(state.getValue(FACING))); }
        @Override protected BlockState mirror(BlockState state, Mirror mirror) { return state.rotate(mirror.getRotation(state.getValue(FACING))); }
    }
}
