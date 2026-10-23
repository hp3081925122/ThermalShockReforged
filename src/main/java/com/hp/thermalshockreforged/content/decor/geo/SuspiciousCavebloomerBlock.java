package com.hp.thermalshockreforged.content.decor.geo;
import net.minecraft.world.InteractionResult;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.*;
import net.minecraft.world.level.block.state.*;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;

public final class SuspiciousCavebloomerBlock extends GeoDecorBlock.Directional {
    public static final BooleanProperty SPAWNING = BooleanProperty.create("spawning");
    public SuspiciousCavebloomerBlock() { super(GeoDecorKind.SUSPICIOUS_CAVEBLOOMER); registerDefaultState(defaultBlockState().setValue(SPAWNING, false)); }
    @Override protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) { super.createBlockStateDefinition(builder); builder.add(SPAWNING); }
    @Override public BlockEntity newBlockEntity(BlockPos pos, BlockState state) { return new SuspiciousCavebloomerBlockEntity(pos, state); }
    private void interact(Level level, BlockPos pos) {
        if (level instanceof ServerLevel server && level.getBlockEntity(pos) instanceof SuspiciousCavebloomerBlockEntity entity) entity.interact(server);
    }
    @Override protected InteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        interact(level, pos); return InteractionResult.SUCCESS;
    }
    @Override protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        interact(level, pos); return InteractionResult.SUCCESS;
    }
    @Override public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        if (level.isClientSide() || !state.getValue(SPAWNING) || type != GeoDecorContent.entity(kind)) return null;
        return (world, pos, current, entity) -> { if (world instanceof ServerLevel server && entity instanceof SuspiciousCavebloomerBlockEntity summoner) summoner.tickSummons(server); };
    }
}
