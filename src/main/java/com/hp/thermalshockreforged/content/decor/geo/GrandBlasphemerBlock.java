package com.hp.thermalshockreforged.content.decor.geo;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;

public final class GrandBlasphemerBlock extends GeoDecorBlock.Directional {
    public static final IntegerProperty PHASE = IntegerProperty.create("animation", 0, 2);
    public GrandBlasphemerBlock() { super(GeoDecorKind.GRAND_BLASPHEMER); }
    @Override protected IntegerProperty animationProperty() { return PHASE; }
    @Override public net.minecraft.world.level.block.entity.BlockEntity newBlockEntity(BlockPos pos, BlockState state) { return new GrandBlasphemerBlockEntity(pos, state); }
    private static void activate(Level level, BlockPos pos, Player player) {
        if (player instanceof ServerPlayer owner && level.getBlockEntity(pos) instanceof GrandBlasphemerBlockEntity entity) entity.activate(owner);
    }
    @Override protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        activate(level, pos, player);
        return ItemInteractionResult.SUCCESS;
    }
    @Override protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        activate(level, pos, player);
        return InteractionResult.SUCCESS;
    }
}
