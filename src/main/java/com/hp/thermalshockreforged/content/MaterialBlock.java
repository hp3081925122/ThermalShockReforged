package com.hp.thermalshockreforged.content;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public final class MaterialBlock extends Block {
    private final int blockedLight;

    public MaterialBlock(Properties properties, int blockedLight) {
        super(properties);
        this.blockedLight = blockedLight;
    }

    @Override
    protected int getLightDampening(BlockState state) {
        return blockedLight;
    }
}
