package com.hp.thermalshockreforged.content.farming;

import net.minecraft.core.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.*;

public final class BarbubbleBlock extends DoublePlantBlock {
    public BarbubbleBlock() {
        super(Properties.of().mapColor(MapColor.PLANT).sound(SoundType.GRASS).strength(.1f, 0).noCollission().offsetType(OffsetType.XZ).pushReaction(PushReaction.DESTROY));
    }
    @Override public int getFlammability(BlockState state, BlockGetter level, BlockPos pos, Direction face) { return 100; }
    @Override public int getFireSpreadSpeed(BlockState state, BlockGetter level, BlockPos pos, Direction face) { return 60; }
}
