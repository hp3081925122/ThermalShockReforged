package com.hp.thermalshockreforged.content.decor.nativeblocks;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.FallingBlock;
import net.minecraft.world.level.block.state.BlockState;

final class SpiceFallingBlock extends FallingBlock {
    private static final MapCodec<SpiceFallingBlock> CODEC = simpleCodec(SpiceFallingBlock::new);

    SpiceFallingBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected MapCodec<? extends FallingBlock> codec() {
        return CODEC;
    }

    @Override
    protected int getLightDampening(BlockState state) {
        return 15;
    }

    @Override
    public int getDustColor(BlockState state, BlockGetter level, BlockPos pos) {
        return -16777216;
    }
}
