package com.hp.thermalshockreforged.content.centrifuge;

import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.BlockHitResult;

public final class CentrifugeBlock extends Block implements EntityBlock {
    public CentrifugeBlock() { super(Properties.of().mapColor(MapColor.METAL).sound(SoundType.METAL).strength(2, 20).lightLevel(state -> 10)); }
    @Override public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> lines, TooltipFlag flag) {
        super.appendHoverText(stack, context, lines, flag);
        lines.add(Component.translatable("block.thermal_shock_reforged.centrifuge.description_0"));
    }
    @Override protected int getLightBlock(BlockState state, BlockGetter level, BlockPos pos) { return 15; }
    @Override protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (player instanceof ServerPlayer server) CentrifugeMenu.open(server, pos);
        return ItemInteractionResult.SUCCESS;
    }
    @Override protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        if (player instanceof ServerPlayer server) CentrifugeMenu.open(server, pos);
        return InteractionResult.SUCCESS;
    }
    @Override protected MenuProvider getMenuProvider(BlockState state, Level level, BlockPos pos) {
        return level.getBlockEntity(pos) instanceof CentrifugeBlockEntity entity ? entity : null;
    }
    @Override public BlockEntity newBlockEntity(BlockPos pos, BlockState state) { return new CentrifugeBlockEntity(pos, state); }
    @Override protected boolean triggerEvent(BlockState state, Level level, BlockPos pos, int id, int parameter) {
        super.triggerEvent(state, level, pos, id, parameter);
        var entity = level.getBlockEntity(pos);
        return entity != null && entity.triggerEvent(id, parameter);
    }
    @Override protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState next, boolean moving) {
        if (!state.is(next.getBlock())) {
            if (level.getBlockEntity(pos) instanceof CentrifugeBlockEntity entity) {
                Containers.dropContents(level, pos, entity);
                level.updateNeighbourForOutputSignal(pos, this);
            }
            super.onRemove(state, level, pos, next, moving);
        }
    }
}
