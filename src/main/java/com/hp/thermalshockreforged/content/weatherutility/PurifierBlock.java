package com.hp.thermalshockreforged.content.weatherutility;
import net.minecraft.world.item.component.TooltipDisplay;
import java.util.function.Consumer;

import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.MapColor;

public final class PurifierBlock extends Block implements EntityBlock, com.hp.thermalshockreforged.content.BlockTooltips.Provider {
    public static final EnumProperty<Direction> FACING = HorizontalDirectionalBlock.FACING;
    public PurifierBlock() {
        super(Properties.of().mapColor(MapColor.SNOW).sound(SoundType.GLASS).strength(7, 99).requiresCorrectToolForDrops().emissiveRendering((state, level, pos) -> true));
        registerDefaultState(defaultBlockState().setValue(FACING, Direction.NORTH));
    }
    @Override protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) { super.createBlockStateDefinition(builder); builder.add(FACING); }
    @Override public BlockState getStateForPlacement(BlockPlaceContext context) { return defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite()); }
    @Override protected BlockState rotate(BlockState state, Rotation rotation) { return state.setValue(FACING, rotation.rotate(state.getValue(FACING))); }
    @Override protected BlockState mirror(BlockState state, Mirror mirror) { return state.rotate(mirror.getRotation(state.getValue(FACING))); }
    @Override protected int getLightDampening(BlockState state) { return 15; }
    @Override public BlockEntity newBlockEntity(BlockPos pos, BlockState state) { return new PurifierBlockEntity(pos, state); }
    @Override public void appendHoverText(ItemStack stack, Item.TooltipContext context, TooltipDisplay display, Consumer<Component> tooltipAdder, TooltipFlag flag) {
        tooltipAdder.accept(Component.translatable(getDescriptionId() + ".description_0"));
        tooltipAdder.accept(Component.translatable(getDescriptionId() + ".description_1"));
    }
    @Override protected void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean moving) {
        super.onPlace(state, level, pos, oldState, moving);
        if (!oldState.is(this) && level instanceof ServerLevel server) activate(server, pos);
    }
    @Override protected void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) { activate(level, pos); }
    private void activate(ServerLevel level, BlockPos pos) {
        if (!level.getBlockState(pos).is(this) || !(level.getBlockEntity(pos) instanceof PurifierBlockEntity owner) || owner.activated()) return;
        if (!PurifierPulseEntity.submit(level, pos, owner)) { level.scheduleTick(pos, this, 20); return; }
        level.playSound(null, pos, SoundEvents.BEACON_ACTIVATE, SoundSource.NEUTRAL, 1, 5);
    }
    @Override public void setPlacedBy(Level level, BlockPos pos, BlockState state, LivingEntity entity, ItemStack stack) {
        super.setPlacedBy(level, pos, state, entity, stack);
        if (entity instanceof ServerPlayer player) WeatherUtilityContent.award(player, "purifier_get");
    }
}
