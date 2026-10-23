package com.hp.thermalshockreforged.content;
import net.minecraft.world.item.component.TooltipDisplay;
import java.util.function.Consumer;

import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.BlockAndLightGetter;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.WoodType;
import net.minecraft.world.level.material.FluidState;

public final class DecorativeBlocks {
    private DecorativeBlocks() {
    }

    public static Block fence(BlockBehaviour.Properties properties, int light, int flammability, boolean fluidOverlay, String... descriptions) {
        List<String> tooltip = List.of(descriptions);
        return BlockTooltips.keys(new FenceBlock(properties) {
            @Override
            protected int getLightDampening(BlockState state) {
                return light < 0 ? super.getLightDampening(state) : light;
            }

            @Override
            public int getFlammability(BlockState state, BlockGetter level, BlockPos position, Direction face) {
                return flammability < 0 ? super.getFlammability(state, level, position, face) : flammability;
            }

            @Override
            public boolean shouldDisplayFluidOverlay(BlockState state, BlockAndLightGetter level, BlockPos position, FluidState fluid) {
                return fluidOverlay || super.shouldDisplayFluidOverlay(state, level, position, fluid);
            }
        }, tooltip);
    }

    public static Block gate(BlockBehaviour.Properties properties, int light, int flammability, boolean fluidOverlay, String... descriptions) {
        List<String> tooltip = List.of(descriptions);
        return BlockTooltips.keys(new FenceGateBlock(WoodType.OAK, properties) {
            @Override
            protected int getLightDampening(BlockState state) {
                return light < 0 ? super.getLightDampening(state) : light;
            }

            @Override
            public int getFlammability(BlockState state, BlockGetter level, BlockPos position, Direction face) {
                return flammability < 0 ? super.getFlammability(state, level, position, face) : flammability;
            }

            @Override
            public boolean shouldDisplayFluidOverlay(BlockState state, BlockAndLightGetter level, BlockPos position, FluidState fluid) {
                return fluidOverlay || super.shouldDisplayFluidOverlay(state, level, position, fluid);
            }
        }, tooltip);
    }

    public static Block bars(BlockBehaviour.Properties properties, int light, int flammability, boolean fluidOverlay, String... descriptions) {
        List<String> tooltip = List.of(descriptions);
        return BlockTooltips.keys(new IronBarsBlock(properties) {
            @Override
            protected int getLightDampening(BlockState state) {
                return light < 0 ? super.getLightDampening(state) : light;
            }

            @Override
            public int getFlammability(BlockState state, BlockGetter level, BlockPos position, Direction face) {
                return flammability < 0 ? super.getFlammability(state, level, position, face) : flammability;
            }

            @Override
            public boolean shouldDisplayFluidOverlay(BlockState state, BlockAndLightGetter level, BlockPos position, FluidState fluid) {
                return fluidOverlay || super.shouldDisplayFluidOverlay(state, level, position, fluid);
            }
        }, tooltip);
    }

    public static Block slab(BlockBehaviour.Properties properties, int light, int flammability, boolean fluidOverlay, String... descriptions) {
        List<String> tooltip = List.of(descriptions);
        return BlockTooltips.keys(new SlabBlock(properties) {
            @Override
            protected int getLightDampening(BlockState state) {
                return light < 0 ? super.getLightDampening(state) : light;
            }

            @Override
            public int getFlammability(BlockState state, BlockGetter level, BlockPos position, Direction face) {
                return flammability < 0 ? super.getFlammability(state, level, position, face) : flammability;
            }

            @Override
            public boolean shouldDisplayFluidOverlay(BlockState state, BlockAndLightGetter level, BlockPos position, FluidState fluid) {
                return fluidOverlay || super.shouldDisplayFluidOverlay(state, level, position, fluid);
            }
        }, tooltip);
    }

    public static Block wall(BlockBehaviour.Properties properties, int light, int flammability, boolean fluidOverlay, String... descriptions) {
        List<String> tooltip = List.of(descriptions);
        return BlockTooltips.keys(new WallBlock(properties) {
            @Override
            protected int getLightDampening(BlockState state) {
                return light < 0 ? super.getLightDampening(state) : light;
            }

            @Override
            public int getFlammability(BlockState state, BlockGetter level, BlockPos position, Direction face) {
                return flammability < 0 ? super.getFlammability(state, level, position, face) : flammability;
            }

            @Override
            public boolean shouldDisplayFluidOverlay(BlockState state, BlockAndLightGetter level, BlockPos position, FluidState fluid) {
                return fluidOverlay || super.shouldDisplayFluidOverlay(state, level, position, fluid);
            }
        }, tooltip);
    }
}
