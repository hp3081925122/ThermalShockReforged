package com.hp.thermalshockreforged.content;

import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.BlockAndTintGetter;
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
        return new FenceBlock(properties) {
            @Override
            protected int getLightBlock(BlockState state, BlockGetter level, BlockPos position) {
                return light < 0 ? super.getLightBlock(state, level, position) : light;
            }

            @Override
            public int getFlammability(BlockState state, BlockGetter level, BlockPos position, Direction face) {
                return flammability < 0 ? super.getFlammability(state, level, position, face) : flammability;
            }

            @Override
            public boolean shouldDisplayFluidOverlay(BlockState state, BlockAndTintGetter level, BlockPos position, FluidState fluid) {
                return fluidOverlay || super.shouldDisplayFluidOverlay(state, level, position, fluid);
            }

            @Override
            public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> lines, TooltipFlag flag) {
                super.appendHoverText(stack, context, lines, flag);
                tooltip.forEach(key -> lines.add(Component.translatable(key)));
            }
        };
    }

    public static Block gate(BlockBehaviour.Properties properties, int light, int flammability, boolean fluidOverlay, String... descriptions) {
        List<String> tooltip = List.of(descriptions);
        return new FenceGateBlock(WoodType.OAK, properties) {
            @Override
            protected int getLightBlock(BlockState state, BlockGetter level, BlockPos position) {
                return light < 0 ? super.getLightBlock(state, level, position) : light;
            }

            @Override
            public int getFlammability(BlockState state, BlockGetter level, BlockPos position, Direction face) {
                return flammability < 0 ? super.getFlammability(state, level, position, face) : flammability;
            }

            @Override
            public boolean shouldDisplayFluidOverlay(BlockState state, BlockAndTintGetter level, BlockPos position, FluidState fluid) {
                return fluidOverlay || super.shouldDisplayFluidOverlay(state, level, position, fluid);
            }

            @Override
            public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> lines, TooltipFlag flag) {
                super.appendHoverText(stack, context, lines, flag);
                tooltip.forEach(key -> lines.add(Component.translatable(key)));
            }
        };
    }

    public static Block bars(BlockBehaviour.Properties properties, int light, int flammability, boolean fluidOverlay, String... descriptions) {
        List<String> tooltip = List.of(descriptions);
        return new IronBarsBlock(properties) {
            @Override
            protected int getLightBlock(BlockState state, BlockGetter level, BlockPos position) {
                return light < 0 ? super.getLightBlock(state, level, position) : light;
            }

            @Override
            public int getFlammability(BlockState state, BlockGetter level, BlockPos position, Direction face) {
                return flammability < 0 ? super.getFlammability(state, level, position, face) : flammability;
            }

            @Override
            public boolean shouldDisplayFluidOverlay(BlockState state, BlockAndTintGetter level, BlockPos position, FluidState fluid) {
                return fluidOverlay || super.shouldDisplayFluidOverlay(state, level, position, fluid);
            }

            @Override
            public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> lines, TooltipFlag flag) {
                super.appendHoverText(stack, context, lines, flag);
                tooltip.forEach(key -> lines.add(Component.translatable(key)));
            }
        };
    }

    public static Block slab(BlockBehaviour.Properties properties, int light, int flammability, boolean fluidOverlay, String... descriptions) {
        List<String> tooltip = List.of(descriptions);
        return new SlabBlock(properties) {
            @Override
            protected int getLightBlock(BlockState state, BlockGetter level, BlockPos position) {
                return light < 0 ? super.getLightBlock(state, level, position) : light;
            }

            @Override
            public int getFlammability(BlockState state, BlockGetter level, BlockPos position, Direction face) {
                return flammability < 0 ? super.getFlammability(state, level, position, face) : flammability;
            }

            @Override
            public boolean shouldDisplayFluidOverlay(BlockState state, BlockAndTintGetter level, BlockPos position, FluidState fluid) {
                return fluidOverlay || super.shouldDisplayFluidOverlay(state, level, position, fluid);
            }

            @Override
            public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> lines, TooltipFlag flag) {
                super.appendHoverText(stack, context, lines, flag);
                tooltip.forEach(key -> lines.add(Component.translatable(key)));
            }
        };
    }

    public static Block wall(BlockBehaviour.Properties properties, int light, int flammability, boolean fluidOverlay, String... descriptions) {
        List<String> tooltip = List.of(descriptions);
        return new WallBlock(properties) {
            @Override
            protected int getLightBlock(BlockState state, BlockGetter level, BlockPos position) {
                return light < 0 ? super.getLightBlock(state, level, position) : light;
            }

            @Override
            public int getFlammability(BlockState state, BlockGetter level, BlockPos position, Direction face) {
                return flammability < 0 ? super.getFlammability(state, level, position, face) : flammability;
            }

            @Override
            public boolean shouldDisplayFluidOverlay(BlockState state, BlockAndTintGetter level, BlockPos position, FluidState fluid) {
                return fluidOverlay || super.shouldDisplayFluidOverlay(state, level, position, fluid);
            }

            @Override
            public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> lines, TooltipFlag flag) {
                super.appendHoverText(stack, context, lines, flag);
                tooltip.forEach(key -> lines.add(Component.translatable(key)));
            }
        };
    }
}
