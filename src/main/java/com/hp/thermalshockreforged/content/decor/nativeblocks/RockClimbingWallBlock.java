package com.hp.thermalshockreforged.content.decor.nativeblocks;
import net.minecraft.world.InteractionResult;

import java.util.concurrent.ThreadLocalRandom;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.*;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.*;
import net.minecraft.world.level.block.state.properties.*;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.BlockHitResult;

final class RockClimbingWallBlock extends NativeDecorBlock {
    static final IntegerProperty VARIANT = IntegerProperty.create("blockstate", 0, 2);
    RockClimbingWallBlock() { this(MapColor.COLOR_GRAY); }
    RockClimbingWallBlock(MapColor color) {
        super(com.hp.thermalshockreforged.ModRegistration.block().mapColor(color).instrument(NoteBlockInstrument.BASEDRUM).sound(SoundType.NETHERRACK).strength(1, 5).requiresCorrectToolForDrops(), new Traits(15, false, false, false, 0, 0, false, false), null);
        registerDefaultState(defaultBlockState().setValue(VARIANT, 0));
    }
    @Override protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) { builder.add(VARIANT); }
    @Override protected void onPlace(BlockState state, Level level, BlockPos pos, BlockState previous, boolean moving) {
        super.onPlace(state, level, pos, previous, moving);
        if (!level.isClientSide() && !previous.is(this)) level.setBlock(pos, state.setValue(VARIANT, ThreadLocalRandom.current().nextInt(3)), 3);
    }
    private boolean climb(Level level, BlockPos pos, Player player, BlockHitResult hit) {
        if (player.isShiftKeyDown() || hit.getDirection().getAxis().isVertical()) return false;
        player.setDeltaMovement(0, .35, 0);
        if (level.isClientSide()) {
            level.playLocalSound(pos.getX(), pos.getY(), pos.getZ(), SoundEvents.BUNDLE_INSERT, SoundSource.NEUTRAL, 1, 1, false);
            level.playLocalSound(pos.getX(), pos.getY(), pos.getZ(), SoundEvents.RABBIT_JUMP, SoundSource.NEUTRAL, 1, 1, false);
        }
        return true;
    }
    @Override protected InteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) { return climb(level, pos, player, hit) ? InteractionResult.SUCCESS : InteractionResult.PASS; }
    @Override protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) { return climb(level, pos, player, hit) ? InteractionResult.SUCCESS : InteractionResult.PASS; }
}
