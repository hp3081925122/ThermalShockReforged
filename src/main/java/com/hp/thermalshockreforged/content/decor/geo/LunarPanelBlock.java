package com.hp.thermalshockreforged.content.decor.geo;

import net.minecraft.core.*;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.Capabilities;

public final class LunarPanelBlock extends GeoDecorBlock {
    public LunarPanelBlock() { super(GeoDecorKind.LUNAR_PANEL); }
    @Override protected void onPlace(BlockState state, Level level, BlockPos pos, BlockState previous, boolean moving) {
        super.onPlace(state, level, pos, previous, moving);
        level.scheduleTick(pos, this, 1);
    }
    @Override protected void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        BlockPos below = pos.below();
        if (level.hasChunkAt(below) && level.getBlockEntity(below) != null) {
            int amount = 150;
            if (level.isBrightOutside()) {
                var unsided = level.getCapability(Capabilities.EnergyStorage.BLOCK, below, null);
                if (unsided != null && unsided.canReceive()) amount = 5;
            }
            var receiver = level.getCapability(Capabilities.EnergyStorage.BLOCK, below, Direction.UP);
            if (receiver != null) receiver.receiveEnergy(amount, false);
        }
        level.scheduleTick(pos, this, 1);
    }
}
