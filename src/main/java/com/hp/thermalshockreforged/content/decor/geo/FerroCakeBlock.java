package com.hp.thermalshockreforged.content.decor.geo;
import net.minecraft.world.InteractionResult;

import com.hp.thermalshockreforged.content.food.FerroCakeConsumption;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.*;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;

public final class FerroCakeBlock extends GeoDecorBlock.Directional {
    public static final IntegerProperty CAKE_ANIMATION = IntegerProperty.create("animation", 0, 2);
    public FerroCakeBlock() { super(GeoDecorKind.FERRO_CAKE); }
    @Override protected IntegerProperty animationProperty() { return CAKE_ANIMATION; }
    private void consume(Level level, BlockPos pos, Player player) {
        if (level instanceof ServerLevel server && player instanceof ServerPlayer owner && level.getBlockEntity(pos) instanceof GeoDecorBlockEntity cake && cake.kind == GeoDecorKind.FERRO_CAKE) FerroCakeConsumption.begin(server, pos, cake, owner);
    }
    @Override protected InteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) { consume(level, pos, player); return InteractionResult.SUCCESS; }
    @Override protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) { consume(level, pos, player); return InteractionResult.SUCCESS; }
}
