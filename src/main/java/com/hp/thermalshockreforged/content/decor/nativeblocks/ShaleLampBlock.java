package com.hp.thermalshockreforged.content.decor.nativeblocks;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.level.block.state.properties.EnumProperty;

import net.minecraft.core.*;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.*;
import net.minecraft.util.RandomSource;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.*;
import net.minecraft.world.level.block.state.properties.*;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.BlockHitResult;

class ShaleLampBlock extends NativeDecorBlock {
    static final IntegerProperty VARIANT = IntegerProperty.create("blockstate", 0, 1);
    ShaleLampBlock() {
        super(com.hp.thermalshockreforged.ModRegistration.block().mapColor(MapColor.COLOR_GRAY).instrument(NoteBlockInstrument.BASEDRUM).sound(SoundType.NETHERRACK).strength(1, 5)
                .lightLevel(state -> state.getValue(state.getBlock() instanceof Chromatic ? Chromatic.VARIANT : VARIANT) == 1 ? 0 : 15)
                .requiresCorrectToolForDrops().randomTicks(), new Traits(15, false, false, false, 0, 0, false, false), null);
        registerDefaultState(defaultBlockState().setValue(variant(), 0));
    }
    protected IntegerProperty variant() { return VARIANT; }
    @Override protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) { super.createBlockStateDefinition(builder); builder.add(variant()); }
    @Override protected void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        tick(state, level, pos, random);
        com.hp.thermalshockreforged.ThermalShockReforged.LOGGER.debug("Shale lamp random ambience completed: position={}, dimension={}", pos, level.dimension().identifier());
    }
    @Override protected void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        super.tick(state, level, pos, random); level.playSound(null, pos, SoundEvents.FIRE_AMBIENT, SoundSource.NEUTRAL, 1, 1);
    }
    private boolean toggle(BlockState state, Level level, BlockPos pos, Player player) {
        if (player.isShiftKeyDown()) return false;
        if (!level.isClientSide()) {
            level.setBlock(pos, state.cycle(variant()), 3);
            level.playSound(null, pos, state.getValue(variant()) == 0 ? SoundEvents.FIRE_EXTINGUISH : SoundEvents.FLINTANDSTEEL_USE, SoundSource.NEUTRAL, 1, 1);
        }
        return true;
    }
    @Override protected InteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        return toggle(state, level, pos, player) ? InteractionResult.SUCCESS : InteractionResult.PASS;
    }
    @Override protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        return toggle(state, level, pos, player) ? InteractionResult.SUCCESS : InteractionResult.PASS;
    }
    static final class Chromatic extends ShaleLampBlock {
        static final IntegerProperty VARIANT = IntegerProperty.create("blockstate", 0, 3);
        private static final EnumProperty<Direction> FACING = BlockStateProperties.HORIZONTAL_FACING;
        Chromatic() { registerDefaultState(defaultBlockState().setValue(FACING, Direction.NORTH)); }
        @Override protected IntegerProperty variant() { return VARIANT; }
        @Override protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) { super.createBlockStateDefinition(builder); builder.add(FACING); }
        @Override public BlockState getStateForPlacement(BlockPlaceContext context) { return defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite()); }
        @Override protected BlockState rotate(BlockState state, Rotation rotation) { return state.setValue(FACING, rotation.rotate(state.getValue(FACING))); }
        @Override protected BlockState mirror(BlockState state, Mirror mirror) { return state.rotate(mirror.getRotation(state.getValue(FACING))); }
    }
}
