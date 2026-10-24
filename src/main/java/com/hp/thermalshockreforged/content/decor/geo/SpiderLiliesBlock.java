package com.hp.thermalshockreforged.content.decor.geo;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;

public final class SpiderLiliesBlock extends GeoFlowerBlock {
    public static final IntegerProperty ANIMATION = IntegerProperty.create("animation", 0, 4);
    public static final EnumProperty<Direction> FACING = BlockStateProperties.HORIZONTAL_FACING;

    public SpiderLiliesBlock() {
        super(GeoDecorKind.SPIDER_LILIES);
        registerDefaultState(defaultBlockState().setValue(FACING, Direction.NORTH));
    }

    @Override protected IntegerProperty animationProperty() { return ANIMATION; }
    @Override protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) { super.createBlockStateDefinition(builder); builder.add(FACING); }
    @Override public BlockState getStateForPlacement(BlockPlaceContext context) { return defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite()); }
    @Override protected BlockState rotate(BlockState state, Rotation rotation) { return state.setValue(FACING, rotation.rotate(state.getValue(FACING))); }
    @Override protected BlockState mirror(BlockState state, Mirror mirror) { return state.rotate(mirror.getRotation(state.getValue(FACING))); }

    private boolean addPetal(BlockState state, Level level, BlockPos pos, Player player) {
        if (!player.getMainHandItem().is(asItem()) || !(level.getBlockEntity(pos) instanceof GeoDecorBlockEntity entity)) return false;
        double count = entity.getPersistentData().getDoubleOr("PetalCount", 0);
        if (!(count < 3)) return false;
        if (!level.isClientSide()) {
            entity.getPersistentData().putDouble("PetalCount", count + 1);
            entity.setChanged();
            level.sendBlockUpdated(pos, state, state, 3);
        }
        int animation = (int)entity.getPersistentData().getDoubleOr("PetalCount", 0);
        if (ANIMATION.getPossibleValues().contains(animation)) level.setBlock(pos, level.getBlockState(pos).setValue(ANIMATION, animation), 3);
        if (level.isClientSide()) level.playLocalSound(pos.getX(), pos.getY(), pos.getZ(), SoundEvents.GRASS_PLACE, SoundSource.BLOCKS, 1, 1, false);
        else level.playSound(null, pos, SoundEvents.GRASS_PLACE, SoundSource.BLOCKS, 1, 1);
        return true;
    }

    @Override protected InteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        return addPetal(state, level, pos, player) ? InteractionResult.SUCCESS : InteractionResult.TRY_WITH_EMPTY_HAND;
    }
    @Override protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        return addPetal(state, level, pos, player) ? InteractionResult.SUCCESS : InteractionResult.PASS;
    }
}
