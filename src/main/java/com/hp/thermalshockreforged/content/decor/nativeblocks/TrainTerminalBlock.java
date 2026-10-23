package com.hp.thermalshockreforged.content.decor.nativeblocks;
import net.minecraft.world.InteractionResult;

import com.hp.thermalshockreforged.content.MaterialContent;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.*;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.*;

final class TrainTerminalBlock extends FacingDecorBlock {
    TrainTerminalBlock() { super(Properties.of().mapColor(MapColor.COLOR_GRAY).sound(SoundType.METAL).strength(-1, 3600000).noOcclusion().pushReaction(PushReaction.BLOCK).isRedstoneConductor((state, level, pos) -> false), new Traits(0, true, true, false, 0, 0, false, false), Outline.FULL); }
    @Override protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) { return Block.box(1, 0, 1, 15, 14, 15); }
    private void interact(Level level, Player player) {
        if (!level.isClientSide() && player.getMainHandItem().is(MaterialContent.items().get("train_pass").get())) player.sendOverlayMessage(Component.translatable("message.thermal_shock_reforged.train.insufficient_credit"));
    }
    @Override protected InteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) { interact(level, player); return InteractionResult.SUCCESS; }
    @Override protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) { interact(level, player); return InteractionResult.SUCCESS; }
}
