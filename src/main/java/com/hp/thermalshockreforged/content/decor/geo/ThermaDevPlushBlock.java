package com.hp.thermalshockreforged.content.decor.geo;
import net.minecraft.world.InteractionResult;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

public final class ThermaDevPlushBlock extends GeoDecorBlock.Directional {
    public ThermaDevPlushBlock() { super(GeoDecorKind.THERMA_DEV_PLUSH); }
    private void collect(BlockState state, Level level, BlockPos pos) {
        if (level instanceof ServerLevel server && server.getBlockState(pos).equals(state) && server.destroyBlock(pos, false)) Block.dropResources(state, server, pos, null);
    }
    @Override protected InteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) { collect(state, level, pos); return InteractionResult.SUCCESS; }
    @Override protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) { collect(state, level, pos); return InteractionResult.SUCCESS; }
}
