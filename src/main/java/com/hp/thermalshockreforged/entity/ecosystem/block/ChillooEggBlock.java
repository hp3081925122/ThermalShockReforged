package com.hp.thermalshockreforged.entity.ecosystem.block;

import com.hp.thermalshockreforged.entity.ecosystem.EcosystemContent;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Containers;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public final class ChillooEggBlock extends Block implements SimpleWaterloggedBlock, EntityBlock {
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

    public ChillooEggBlock() {
        super(Properties.of().mapColor(net.minecraft.world.level.material.MapColor.ICE).sound(SoundType.POINTED_DRIPSTONE).strength(0.2F, 0).noOcclusion().isRedstoneConductor((state, level, pos) -> false));
        registerDefaultState(stateDefinition.any().setValue(WATERLOGGED, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(WATERLOGGED);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return defaultBlockState().setValue(WATERLOGGED, context.getLevel().getFluidState(context.getClickedPos()).getType() == Fluids.WATER);
    }

    @Override
    protected FluidState getFluidState(BlockState state) {
        return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
    }

    @Override
    protected BlockState updateShape(BlockState state, Direction direction, BlockState neighbor, LevelAccessor level, BlockPos position, BlockPos neighborPosition) {
        if (state.getValue(WATERLOGGED)) level.scheduleTick(position, Fluids.WATER, Fluids.WATER.getTickDelay(level));
        return super.updateShape(state, direction, neighbor, level, position, neighborPosition);
    }

    @Override
    protected boolean propagatesSkylightDown(BlockState state, BlockGetter level, BlockPos position) { return state.getFluidState().isEmpty(); }

    @Override
    protected int getLightBlock(BlockState state, BlockGetter level, BlockPos position) { return 0; }

    @Override
    protected VoxelShape getVisualShape(BlockState state, BlockGetter level, BlockPos position, CollisionContext context) { return Shapes.empty(); }

    @Override
    public BlockEntity newBlockEntity(BlockPos position, BlockState state) { return new ChillooEggBlockEntity(position, state); }

    @Override
    protected void onPlace(BlockState state, Level level, BlockPos position, BlockState previous, boolean moving) {
        super.onPlace(state, level, position, previous, moving);
        if (!level.isClientSide() && !previous.is(this)) level.scheduleTick(position, this, 20);
    }

    @Override
    protected void tick(BlockState state, ServerLevel level, BlockPos position, RandomSource random) {
        if (!(level.getBlockEntity(position) instanceof ChillooEggBlockEntity egg)) return;
        if (egg.advanceIncubation()) {
            level.destroyBlock(position, false);
            level.playSound(null, position, SoundEvents.TURTLE_EGG_CRACK, SoundSource.BLOCKS, 1, -1);
            EcosystemContent.CHILLOO.get().spawn(level, position, EntitySpawnReason.MOB_SUMMONED);
            return;
        }
        if (random.nextDouble() < 0.1) level.playSound(null, position, SoundEvents.TURTLE_EGG_CRACK, SoundSource.BLOCKS, 1, 1);
        level.scheduleTick(position, this, 20);
    }

    @Override
    protected void onRemove(BlockState state, Level level, BlockPos position, BlockState replacement, boolean moving) {
        if (!state.is(replacement.getBlock())) {
            if (level.getBlockEntity(position) instanceof ChillooEggBlockEntity egg) {
                Containers.dropContents(level, position, egg);
                level.updateNeighbourForOutputSignal(position, this);
            }
            super.onRemove(state, level, position, replacement, moving);
        }
    }

    @Override
    protected boolean hasAnalogOutputSignal(BlockState state) { return true; }

    @Override
    protected int getAnalogOutputSignal(BlockState state, Level level, BlockPos position) {
        return level.getBlockEntity(position) instanceof ChillooEggBlockEntity egg ? AbstractContainerMenu.getRedstoneSignalFromContainer(egg) : 0;
    }

    @Override
    protected MenuProvider getMenuProvider(BlockState state, Level level, BlockPos position) {
        return level.getBlockEntity(position) instanceof ChillooEggBlockEntity egg ? egg : null;
    }

    @Override
    protected boolean triggerEvent(BlockState state, Level level, BlockPos position, int id, int data) {
        super.triggerEvent(state, level, position, id, data);
        return level.getBlockEntity(position) instanceof ChillooEggBlockEntity egg && egg.triggerEvent(id, data);
    }
}
