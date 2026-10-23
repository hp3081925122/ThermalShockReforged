package com.hp.thermalshockreforged.content.gemlocks;
import net.minecraft.world.InteractionResult;

import com.hp.thermalshockreforged.ThermalShockReforged;
import net.minecraft.core.*;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.*;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.neoforge.items.ItemHandlerHelper;

public final class GemlockBlock extends Block {
    public static final IntegerProperty BLOCKSTATE = IntegerProperty.create("blockstate", 0, 1);
    public final GemlockKind kind;
    public GemlockBlock(GemlockKind kind) {
        super(Properties.of().mapColor(kind.mapColor).sound(SoundType.STONE).strength(2, 30).requiresCorrectToolForDrops().lightLevel(state -> state.getValue(BLOCKSTATE) == 1 ? 15 : 0));
        this.kind = kind;
        registerDefaultState(defaultBlockState().setValue(BLOCKSTATE, 0));
    }
    @Override protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) { builder.add(BLOCKSTATE); }
    @Override protected int getLightBlock(BlockState state, BlockGetter level, BlockPos pos) { return 15; }
    @Override protected boolean isSignalSource(BlockState state) { return true; }
    @Override protected int getSignal(BlockState state, BlockGetter level, BlockPos pos, Direction side) { return state.getValue(BLOCKSTATE) == 1 ? 15 : 0; }
    @Override public boolean canConnectRedstone(BlockState state, BlockGetter level, BlockPos pos, Direction side) { return true; }
    @Override protected InteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (level instanceof ServerLevel server) toggle(server, pos, player);
        return InteractionResult.SUCCESS;
    }
    @Override protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        if (level instanceof ServerLevel server) toggle(server, pos, player);
        return InteractionResult.SUCCESS;
    }
    private void toggle(ServerLevel level, BlockPos pos, Player player) {
        var current = level.getBlockState(pos);
        if (!current.is(this)) return;
        boolean activating = current.getValue(BLOCKSTATE) == 0;
        var held = player.getMainHandItem();
        if (activating && (held.isEmpty() || !held.is(kind.item()))) return;
        var payment = activating ? held.split(1) : ItemStack.EMPTY;
        if (!level.setBlock(pos, current.setValue(BLOCKSTATE, activating ? 1 : 0), 3)) {
            if (activating) ItemHandlerHelper.giveItemToPlayer(player, payment);
            ThermalShockReforged.LOGGER.debug("Gemlock state transition rejected: kind={}, position={}, player={}", kind, pos, player.getUUID());
            return;
        }
        if (!activating) ItemHandlerHelper.giveItemToPlayer(player, new ItemStack(kind.item()));
        player.getInventory().setChanged();
        level.playSound(null, pos, activating ? SoundEvents.EXPERIENCE_ORB_PICKUP : SoundEvents.GLASS_BREAK, SoundSource.NEUTRAL, 1, 1);
        ThermalShockReforged.LOGGER.debug("Gemlock state changed: kind={}, active={}, position={}, player={}", kind, activating, pos, player.getUUID());
    }
}
