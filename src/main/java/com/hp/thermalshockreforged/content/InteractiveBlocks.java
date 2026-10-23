package com.hp.thermalshockreforged.content;
import net.minecraft.world.item.component.TooltipDisplay;
import java.util.function.Consumer;

import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockSetType;

public final class InteractiveBlocks {
    private InteractiveBlocks() {
    }

    public static Block door(BlockBehaviour.Properties properties, BlockSetType type, int light, int flammability, String... descriptions) {
        List<String> tooltip = List.of(descriptions);
        return new DoorBlock(type, properties) {
            @Override
            protected int getLightBlock(BlockState state, BlockGetter level, BlockPos position) {
                return light < 0 ? super.getLightBlock(state, level, position) : light;
            }

            @Override
            public int getFlammability(BlockState state, BlockGetter level, BlockPos position, Direction face) {
                return flammability < 0 ? super.getFlammability(state, level, position, face) : flammability;
            }

            @Override
            public void appendHoverText(ItemStack stack, Item.TooltipContext context, TooltipDisplay display, Consumer<Component> tooltipAdder, TooltipFlag flag) {
                super.appendHoverText(stack, context, display, tooltipAdder, flag);
                tooltip.forEach(key -> tooltipAdder.accept(Component.translatable(key)));
            }
        };
    }

    public static Block trapdoor(BlockBehaviour.Properties properties, BlockSetType type, int light, int flammability, String... descriptions) {
        List<String> tooltip = List.of(descriptions);
        return new TrapDoorBlock(type, properties) {
            @Override
            protected int getLightBlock(BlockState state, BlockGetter level, BlockPos position) {
                return light < 0 ? super.getLightBlock(state, level, position) : light;
            }

            @Override
            public int getFlammability(BlockState state, BlockGetter level, BlockPos position, Direction face) {
                return flammability < 0 ? super.getFlammability(state, level, position, face) : flammability;
            }

            @Override
            public void appendHoverText(ItemStack stack, Item.TooltipContext context, TooltipDisplay display, Consumer<Component> tooltipAdder, TooltipFlag flag) {
                super.appendHoverText(stack, context, display, tooltipAdder, flag);
                tooltip.forEach(key -> tooltipAdder.accept(Component.translatable(key)));
            }
        };
    }

    public static Block button(BlockBehaviour.Properties properties, BlockSetType type, int pressTicks, int light, int flammability, String... descriptions) {
        List<String> tooltip = List.of(descriptions);
        return new ButtonBlock(type, pressTicks, properties) {
            @Override
            protected int getLightBlock(BlockState state, BlockGetter level, BlockPos position) {
                return light < 0 ? super.getLightBlock(state, level, position) : light;
            }

            @Override
            public int getFlammability(BlockState state, BlockGetter level, BlockPos position, Direction face) {
                return flammability < 0 ? super.getFlammability(state, level, position, face) : flammability;
            }

            @Override
            public void appendHoverText(ItemStack stack, Item.TooltipContext context, TooltipDisplay display, Consumer<Component> tooltipAdder, TooltipFlag flag) {
                super.appendHoverText(stack, context, display, tooltipAdder, flag);
                tooltip.forEach(key -> tooltipAdder.accept(Component.translatable(key)));
            }
        };
    }

    public static Block pressurePlate(BlockBehaviour.Properties properties, BlockSetType type, boolean mobsOnly, int light, int flammability, String... descriptions) {
        List<String> tooltip = List.of(descriptions);
        return new PressurePlateBlock(type, properties) {
            @Override
            protected int getLightBlock(BlockState state, BlockGetter level, BlockPos position) {
                return light < 0 ? super.getLightBlock(state, level, position) : light;
            }

            @Override
            public int getFlammability(BlockState state, BlockGetter level, BlockPos position, Direction face) {
                return flammability < 0 ? super.getFlammability(state, level, position, face) : flammability;
            }

            @Override
            public void appendHoverText(ItemStack stack, Item.TooltipContext context, TooltipDisplay display, Consumer<Component> tooltipAdder, TooltipFlag flag) {
                super.appendHoverText(stack, context, display, tooltipAdder, flag);
                tooltip.forEach(key -> tooltipAdder.accept(Component.translatable(key)));
            }

            @Override
            protected int getSignalStrength(Level level, BlockPos position) {
                return getEntityCount(level, TOUCH_AABB.move(position), mobsOnly ? LivingEntity.class : Entity.class) > 0 ? 15 : 0;
            }
        };
    }
}
