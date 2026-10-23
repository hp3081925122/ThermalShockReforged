package com.hp.thermalshockreforged.content.decor.geo;
import net.minecraft.world.InteractionResult;

import net.minecraft.core.*;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.*;
import net.minecraft.world.level.block.state.properties.*;
import net.minecraft.world.level.material.*;
import net.minecraft.world.phys.BlockHitResult;

public final class CapsuleBlock extends GeoDecorBlock.Directional implements SimpleWaterloggedBlock {
    public static final IntegerProperty VARIANT = IntegerProperty.create("blockstate", 0, 4);
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    public CapsuleBlock() { super(GeoDecorKind.CAPSULE); registerDefaultState(defaultBlockState().setValue(VARIANT, 0).setValue(WATERLOGGED, false)); }
    @Override protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) { super.createBlockStateDefinition(builder); builder.add(VARIANT, WATERLOGGED); }
    @Override public BlockState getStateForPlacement(BlockPlaceContext context) { return super.getStateForPlacement(context).setValue(WATERLOGGED, context.getLevel().getFluidState(context.getClickedPos()).getType() == Fluids.WATER); }
    @Override protected FluidState getFluidState(BlockState state) { return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state); }
    @Override protected boolean propagatesSkylightDown(BlockState state, BlockGetter level, BlockPos pos) { return state.getFluidState().isEmpty(); }
    @Override protected BlockState updateShape(BlockState state, Direction side, BlockState neighbor, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
        if (state.getValue(WATERLOGGED)) level.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
        return super.updateShape(state, side, neighbor, level, pos, neighborPos);
    }
    private void cycle(BlockState state, Level level, BlockPos pos, Player player) {
        if (!level.isClientSide() && player.getAbilities().instabuild && level.getBlockState(pos).equals(state)) level.setBlock(pos, state.cycle(VARIANT), 3);
    }
    @Override protected InteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) { cycle(state, level, pos, player); return InteractionResult.SUCCESS; }
    @Override protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) { cycle(state, level, pos, player); return InteractionResult.SUCCESS; }
}
