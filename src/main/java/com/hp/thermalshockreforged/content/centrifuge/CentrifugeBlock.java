package com.hp.thermalshockreforged.content.centrifuge;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.component.TooltipDisplay;
import java.util.function.Consumer;
import net.minecraft.world.InteractionResult;

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

public final class CentrifugeBlock extends Block implements EntityBlock, com.hp.thermalshockreforged.content.BlockTooltips.Provider {
    public CentrifugeBlock() { super(com.hp.thermalshockreforged.ModRegistration.block().mapColor(MapColor.METAL).sound(SoundType.METAL).strength(2, 20).lightLevel(state -> 10)); }
    @Override public void appendHoverText(ItemStack stack, Item.TooltipContext context, TooltipDisplay display, Consumer<Component> tooltipAdder, TooltipFlag flag) {
        tooltipAdder.accept(Component.translatable("block.thermal_shock_reforged.centrifuge.description_0"));
    }
    @Override protected int getLightDampening(BlockState state) { return 15; }
    @Override protected InteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (player instanceof ServerPlayer server) CentrifugeMenu.open(server, pos);
        return InteractionResult.SUCCESS;
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
    @Override protected void affectNeighborsAfterRemoval(BlockState state, net.minecraft.server.level.ServerLevel level, BlockPos pos, boolean moving) { Containers.updateNeighboursAfterDestroy(state, level, pos); }
}
