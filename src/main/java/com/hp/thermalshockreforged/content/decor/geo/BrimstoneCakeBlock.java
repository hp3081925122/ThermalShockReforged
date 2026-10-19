package com.hp.thermalshockreforged.content.decor.geo;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.*;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;

public final class BrimstoneCakeBlock extends GeoDecorBlock.Directional {
    public static final IntegerProperty CAKE_ANIMATION = IntegerProperty.create("animation", 0, 2);
    public BrimstoneCakeBlock() { super(GeoDecorKind.BRIMSTONE_CAKE); }
    @Override protected IntegerProperty animationProperty() { return CAKE_ANIMATION; }
    private void consume(Level level, BlockPos pos, Player player) {
        if (level instanceof ServerLevel server && player instanceof ServerPlayer owner && level.getBlockEntity(pos) instanceof GeoDecorBlockEntity cake && cake.kind == GeoDecorKind.BRIMSTONE_CAKE) BrimstoneCakeConsumption.begin(server, pos, cake, owner);
    }
    @Override protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) { consume(level, pos, player); return ItemInteractionResult.SUCCESS; }
    @Override protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) { consume(level, pos, player); return InteractionResult.SUCCESS; }
}
