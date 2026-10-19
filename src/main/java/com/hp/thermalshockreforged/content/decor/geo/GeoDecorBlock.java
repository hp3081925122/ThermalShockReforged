package com.hp.thermalshockreforged.content.decor.geo;

import java.util.List;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.RandomSource;
import net.minecraft.core.*;
import net.minecraft.world.*;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.*;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.*;
import net.minecraft.world.level.block.state.properties.*;
import net.minecraft.world.level.material.*;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.phys.shapes.*;

public class GeoDecorBlock extends Block implements EntityBlock {
    public static final IntegerProperty ANIMATION = IntegerProperty.create("animation", 0, 1);
    public final GeoDecorKind kind;
    public GeoDecorBlock(GeoDecorKind kind) {
        super(kind.properties());
        this.kind = kind;
        registerDefaultState(defaultBlockState().setValue(animationProperty(), 0));
    }
    protected IntegerProperty animationProperty() { return ANIMATION; }
    @Override protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) { builder.add(animationProperty()); }
    @Override protected RenderShape getRenderShape(BlockState state) { return RenderShape.ENTITYBLOCK_ANIMATED; }
    @Override public BlockEntity newBlockEntity(BlockPos pos, BlockState state) { return new GeoDecorBlockEntity(kind, pos, state); }
    @Override protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) { return kind.shape; }
    @Override protected int getLightBlock(BlockState state, BlockGetter level, BlockPos pos) { return 0; }
    @Override protected boolean propagatesSkylightDown(BlockState state, BlockGetter level, BlockPos pos) { return true; }
    @Override public boolean shouldDisplayFluidOverlay(BlockState state, BlockAndTintGetter level, BlockPos pos, FluidState fluid) {
        return kind == GeoDecorKind.VOLTFLOWER_BULB || super.shouldDisplayFluidOverlay(state, level, pos, fluid);
    }
    @Override public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        super.animateTick(state, level, pos, random);
        if (kind == GeoDecorKind.CRYSTAL_SOPEL_TROPHY || kind == GeoDecorKind.GUNNER_TROPHY || kind == GeoDecorKind.BURNER_TROPHY || kind == GeoDecorKind.CAVEBLOOMER_TROPHY) level.addParticle(ParticleTypes.END_ROD, pos.getX() + random.nextDouble(), pos.getY() + random.nextDouble() * 2, pos.getZ() + random.nextDouble(), 0, 0.1, 0);
    }
    @Override protected List<ItemStack> getDrops(BlockState state, LootParams.Builder builder) {
        var drops = super.getDrops(state, builder);
        if (!drops.isEmpty() || !kind.fallbackDrop) return drops;
        return List.of(kind == GeoDecorKind.BIG_GLASS_BOTTLE ? new ItemStack(Items.GLASS_BOTTLE) : new ItemStack(this));
    }
    @Override protected MenuProvider getMenuProvider(BlockState state, Level level, BlockPos pos) {
        return level.getBlockEntity(pos) instanceof GeoDecorBlockEntity entity ? entity : null;
    }
    @Override protected boolean triggerEvent(BlockState state, Level level, BlockPos pos, int id, int param) {
        super.triggerEvent(state, level, pos, id, param);
        return level.getBlockEntity(pos) instanceof GeoDecorBlockEntity entity && entity.triggerEvent(id, param);
    }
    @Override protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState replacement, boolean moving) {
        if (!state.is(replacement.getBlock())) {
            if (!level.isClientSide && level.getBlockEntity(pos) instanceof GeoDecorBlockEntity entity) {
                Containers.dropContents(level, pos, entity);
                level.updateNeighbourForOutputSignal(pos, this);
            }
            super.onRemove(state, level, pos, replacement, moving);
        }
    }
    @Override protected boolean hasAnalogOutputSignal(BlockState state) { return kind.comparator; }
    @Override protected int getAnalogOutputSignal(BlockState state, Level level, BlockPos pos) {
        return kind.comparator && level.getBlockEntity(pos) instanceof GeoDecorBlockEntity entity ? AbstractContainerMenu.getRedstoneSignalFromContainer(entity) : 0;
    }
    public static class Waterlogged extends GeoDecorBlock implements SimpleWaterloggedBlock {
        public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
        public Waterlogged(GeoDecorKind kind) { super(kind); registerDefaultState(defaultBlockState().setValue(WATERLOGGED, false)); }
        @Override protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) { super.createBlockStateDefinition(builder); builder.add(WATERLOGGED); }
        @Override public BlockState getStateForPlacement(BlockPlaceContext context) {
            return defaultBlockState().setValue(WATERLOGGED, context.getLevel().getFluidState(context.getClickedPos()).getType() == Fluids.WATER);
        }
        @Override protected FluidState getFluidState(BlockState state) { return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state); }
        @Override protected boolean propagatesSkylightDown(BlockState state, BlockGetter level, BlockPos pos) { return state.getFluidState().isEmpty(); }
        @Override protected BlockState updateShape(BlockState state, Direction side, BlockState neighbor, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
            if (state.getValue(WATERLOGGED)) level.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
            return super.updateShape(state, side, neighbor, level, pos, neighborPos);
        }
    }
    public static class Directional extends GeoDecorBlock {
        public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
        public Directional(GeoDecorKind kind) { super(kind); registerDefaultState(defaultBlockState().setValue(FACING, Direction.NORTH)); }
        @Override protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) { super.createBlockStateDefinition(builder); builder.add(FACING); }
        @Override public BlockState getStateForPlacement(BlockPlaceContext context) { return defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite()); }
        @Override protected BlockState rotate(BlockState state, Rotation rotation) { return state.setValue(FACING, rotation.rotate(state.getValue(FACING))); }
        @Override protected BlockState mirror(BlockState state, Mirror mirror) { return state.rotate(mirror.getRotation(state.getValue(FACING))); }
    }
}
