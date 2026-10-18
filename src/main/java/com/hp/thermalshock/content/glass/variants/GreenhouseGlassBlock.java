package com.hp.thermalshock.content.glass.variants;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

final class GreenhouseGlassBlock extends VariantGlassBlock {
    private static final VoxelShape SHAPE = box(0, 0, 0, 16, 15.9, 16);

    GreenhouseGlassBlock(Properties properties) { super(properties, 0); }

    @Override
    protected boolean propagatesSkylightDown(BlockState state, BlockGetter level, BlockPos pos) { return true; }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) { return SHAPE; }
}
