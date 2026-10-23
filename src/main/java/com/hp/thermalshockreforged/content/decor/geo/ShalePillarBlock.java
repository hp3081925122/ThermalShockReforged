package com.hp.thermalshockreforged.content.decor.geo;
import net.minecraft.world.InteractionResult;

import com.hp.thermalshockreforged.ThermalShockReforged;
import com.hp.thermalshockreforged.content.MaterialContent;
import com.hp.thermalshockreforged.content.utility.UtilityContent;
import net.minecraft.core.*;
import net.minecraft.sounds.*;
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

public final class ShalePillarBlock extends GeoDecorBlock.Directional implements SimpleWaterloggedBlock {
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    private static final GeoDecorKind[] STYLES = {GeoDecorKind.SHALE_PILLAR, GeoDecorKind.SHALE_PILLAR_VAR_2, GeoDecorKind.SHALE_PILLAR_VAR_3, GeoDecorKind.SHALE_PILLAR_VAR_4, GeoDecorKind.SHALE_PILLAR_VAR_5, GeoDecorKind.SHALE_PILLAR_VAR_6, GeoDecorKind.SHALE_PILLAR_VAR_7, GeoDecorKind.SHALE_PILLAR_VAR_8};
    public ShalePillarBlock(GeoDecorKind kind) { super(kind); registerDefaultState(defaultBlockState().setValue(WATERLOGGED, false)); }
    @Override protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) { super.createBlockStateDefinition(builder); builder.add(WATERLOGGED); }
    @Override public BlockState getStateForPlacement(BlockPlaceContext context) { return super.getStateForPlacement(context).setValue(WATERLOGGED, context.getLevel().getFluidState(context.getClickedPos()).getType() == Fluids.WATER); }
    @Override protected FluidState getFluidState(BlockState state) { return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state); }
    @Override protected boolean propagatesSkylightDown(BlockState state) { return state.getFluidState().isEmpty(); }
    @Override protected BlockState updateShape(BlockState state, net.minecraft.world.level.LevelReader level, net.minecraft.world.level.ScheduledTickAccess ticks, BlockPos pos, Direction side, BlockPos neighborPos, BlockState neighbor, net.minecraft.util.RandomSource random) {
        if (state.getValue(WATERLOGGED)) ticks.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
        return super.updateShape(state, level, ticks, pos, side, neighborPos, neighbor, random);
    }
    private boolean chisel(BlockState state, Level level, BlockPos pos, Player player) {
        if (!player.getMainHandItem().is(UtilityContent.CHISEL.get())) return false;
        if (level.isClientSide()) return true;
        if (!level.getBlockState(pos).equals(state) || !(level.getBlockEntity(pos) instanceof GeoDecorBlockEntity previous)) return false;
        int index = java.util.Arrays.asList(STYLES).indexOf(kind);
        if (index < 0) return false;
        BlockState replacement = GeoDecorContent.block(STYLES[(index + 1) % STYLES.length]).withPropertiesOf(state);
        var saved = previous.saveWithoutMetadata(level.registryAccess());
        previous.setLootTable(null);
        previous.clearContent();
        boolean changed = level.setBlock(pos, replacement, 3);
        if (changed && level.getBlockEntity(pos) instanceof GeoDecorBlockEntity next) {
            next.loadWithComponents(saved, level.registryAccess());
            next.setChanged();
            level.sendBlockUpdated(pos, replacement, replacement, 3);
            level.levelEvent(2001, pos, Block.getId(MaterialContent.blocks().get("cobbled_shale").get().defaultBlockState()));
            level.playSound(null, pos, SoundEvents.END_PORTAL_FRAME_FILL, SoundSource.NEUTRAL, 1, 1);
            return true;
        }
        if (changed) level.setBlock(pos, state, 3);
        if (level.getBlockEntity(pos) instanceof GeoDecorBlockEntity restored) { restored.loadWithComponents(saved, level.registryAccess()); restored.setChanged(); }
        ThermalShockReforged.LOGGER.warn("Shale pillar style conversion failed at {} in {}", pos, level.dimension().identifier());
        return true;
    }
    @Override protected InteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) { return chisel(state, level, pos, player) ? InteractionResult.SUCCESS : InteractionResult.PASS; }
    @Override protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) { return chisel(state, level, pos, player) ? InteractionResult.SUCCESS : InteractionResult.PASS; }
}
