package com.hp.thermalshockreforged.content.decor.nativeblocks;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

final class DeskPhotoBlock extends FacingDecorBlock {
    private static final VoxelShape SHAPE = box(0, 0, 0, 16, 1, 16);

    DeskPhotoBlock() {
        super(Properties.of().mapColor(MapColor.COLOR_BROWN).sound(SoundType.ANVIL).strength(-1, 3600000)
                .requiresCorrectToolForDrops().noCollission().noOcclusion().pushReaction(PushReaction.BLOCK)
                .isRedstoneConductor((state, level, pos) -> false),
                new Traits(0, true, true, false, 0, 0, false, false), Outline.FULL);
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    private void openPhoto(Level level, Player player) {
        if (!level.isClientSide) player.openMenu(new SimpleMenuProvider((id, inventory, owner) -> new DeskPhotoMenu(id, inventory), Component.literal("Desk Photo")));
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos,
                                               Player player, InteractionHand hand, BlockHitResult hit) {
        openPhoto(level, player);
        return ItemInteractionResult.sidedSuccess(level.isClientSide);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        openPhoto(level, player);
        return InteractionResult.sidedSuccess(level.isClientSide);
    }
}
