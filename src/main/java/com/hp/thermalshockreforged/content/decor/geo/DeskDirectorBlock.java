package com.hp.thermalshockreforged.content.decor.geo;

import com.hp.thermalshockreforged.content.npc.sopel.*;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

final class DeskDirectorBlock extends GeoDecorBlock.Directional {
    DeskDirectorBlock() { super(GeoDecorKind.DESK_DIRECTOR); }
    private void open(Player player, BlockPos pos) { if (player instanceof ServerPlayer server) SopelContent.open(server, pos, SopelPage.INTRO_1); }
    @Override protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) { open(player, pos); return ItemInteractionResult.SUCCESS; }
    @Override protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) { open(player, pos); return InteractionResult.SUCCESS; }
}
