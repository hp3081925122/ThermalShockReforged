package com.hp.thermalshockreforged.content.decor.geo;
import net.minecraft.world.InteractionResult;

import com.hp.thermalshockreforged.content.dialogue.*;
import net.minecraft.core.*;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
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

public class DialogueBlock extends GeoDecorBlock.Directional implements SimpleWaterloggedBlock {
    public static final IntegerProperty VARIANT = IntegerProperty.create("blockstate", 0, 7);
    private static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    public DialogueBlock(GeoDecorKind kind) { super(kind); registerDefaultState(defaultBlockState().setValue(WATERLOGGED, false)); }
    @Override protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) { super.createBlockStateDefinition(builder); builder.add(WATERLOGGED); }
    @Override public BlockState getStateForPlacement(BlockPlaceContext context) { return super.getStateForPlacement(context).setValue(WATERLOGGED, context.getLevel().getFluidState(context.getClickedPos()).getType() == Fluids.WATER); }
    @Override protected FluidState getFluidState(BlockState state) { return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state); }
    @Override protected boolean propagatesSkylightDown(BlockState state) { return state.getFluidState().isEmpty(); }
    @Override protected BlockState updateShape(BlockState state, net.minecraft.world.level.LevelReader level, net.minecraft.world.level.ScheduledTickAccess ticks, BlockPos pos, Direction side, BlockPos neighborPos, BlockState neighbor, net.minecraft.util.RandomSource random) {
        if (state.getValue(WATERLOGGED)) ticks.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
        return super.updateShape(state, level, ticks, pos, side, neighborPos, neighbor, random);
    }
    private void interact(BlockState state, Level level, BlockPos pos, Player player) {
        if (!(player instanceof ServerPlayer serverPlayer)) return;
        if (kind == GeoDecorKind.CIVILIAN && player.getAbilities().instabuild) { level.setBlock(pos, state.cycle(VARIANT), 3); return; }
        var page = kind == GeoDecorKind.CIVILIAN ? DialoguePage.civilian(state.getValue(VARIANT)) : DialoguePage.RADIO;
        serverPlayer.openMenu(new SimpleMenuProvider((id, inventory, owner) -> new DialogueMenu(page, id, inventory, pos), Component.empty()), pos);
    }
    @Override protected InteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) { interact(state, level, pos, player); return InteractionResult.SUCCESS; }
    @Override protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) { interact(state, level, pos, player); return InteractionResult.SUCCESS; }
    public static final class Civilian extends DialogueBlock {
        public Civilian() { super(GeoDecorKind.CIVILIAN); registerDefaultState(defaultBlockState().setValue(VARIANT, 0)); }
        @Override protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) { super.createBlockStateDefinition(builder); builder.add(VARIANT); }
    }
}
