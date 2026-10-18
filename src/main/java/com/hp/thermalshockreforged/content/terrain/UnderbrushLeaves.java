package com.hp.thermalshockreforged.content.terrain;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.MapColor;

public class UnderbrushLeaves extends LeavesBlock {
    private final int opacity;

    public UnderbrushLeaves(MapColor color, int opacity, boolean dull) {
        super(Properties.of().mapColor(color).ignitedByLava().sound(SoundType.GRASS)
                .strength(dull ? 0.15F : 0.1F, dull ? 1.0F : 0.0F).noOcclusion());
        this.opacity = opacity;
    }

    @Override
    protected int getLightBlock(BlockState state, BlockGetter level, BlockPos pos) {
        return opacity;
    }

    public static final class Dull extends UnderbrushLeaves {
        public static final IntegerProperty BLOCKSTATE = IntegerProperty.create("blockstate", 0, 1);

        public Dull() {
            super(MapColor.TERRACOTTA_GREEN, 6, true);
            registerDefaultState(defaultBlockState().setValue(BLOCKSTATE, 0));
        }

        @Override
        protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
            super.createBlockStateDefinition(builder);
            builder.add(BLOCKSTATE);
        }
    }
}
