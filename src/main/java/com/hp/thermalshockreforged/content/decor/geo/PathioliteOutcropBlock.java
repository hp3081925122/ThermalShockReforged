package com.hp.thermalshockreforged.content.decor.geo;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;

public final class PathioliteOutcropBlock extends GeoDecorBlock {
    public PathioliteOutcropBlock() { super(GeoDecorKind.PATHIOLITE_OUTCROP); }
    @Override public boolean onDestroyedByPlayer(BlockState state, Level level, BlockPos pos, Player player, boolean harvest, FluidState fluid) {
        boolean removed = super.onDestroyedByPlayer(state, level, pos, player, harvest, fluid);
        if (removed && !level.isClientSide() && !level.getBlockState(pos).is(this)) level.setBlock(pos, Blocks.LAVA.defaultBlockState(), 3);
        return removed;
    }
    @Override public void wasExploded(Level level, BlockPos pos, Explosion explosion) {
        super.wasExploded(level, pos, explosion);
        if (!level.isClientSide()) level.setBlock(pos, Blocks.LAVA.defaultBlockState(), 3);
    }
}
