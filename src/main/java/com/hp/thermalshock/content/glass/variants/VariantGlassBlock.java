package com.hp.thermalshock.content.glass.variants;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

class VariantGlassBlock extends Block {
    private final int blockedLight;

    VariantGlassBlock(Properties properties, int blockedLight) {
        super(properties);
        this.blockedLight = blockedLight;
    }

    @Override
    public boolean shouldDisplayFluidOverlay(BlockState state, BlockAndTintGetter level, BlockPos pos, FluidState fluid) { return true; }

    @Override
    protected boolean skipRendering(BlockState state, BlockState adjacent, Direction side) { return adjacent.is(this) || super.skipRendering(state, adjacent, side); }

    @Override
    protected int getLightBlock(BlockState state, BlockGetter level, BlockPos pos) { return blockedLight; }

    @Override
    protected VoxelShape getVisualShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) { return Shapes.empty(); }
}
