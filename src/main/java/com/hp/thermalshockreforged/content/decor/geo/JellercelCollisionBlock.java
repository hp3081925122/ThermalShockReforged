package com.hp.thermalshockreforged.content.decor.geo;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public final class JellercelCollisionBlock extends Block implements SimpleWaterloggedBlock {
    public static final IntegerProperty PART = IntegerProperty.create("part", 0, 7);
    private static final VoxelShape SHAPE = Block.box(.1, .1, .1, 15.9, 15.9, 15.9);
    public JellercelCollisionBlock() {
        super(BlockBehaviour.Properties.of().sound(SoundType.EMPTY).strength(2, 1).noOcclusion().randomTicks().pushReaction(PushReaction.BLOCK).isRedstoneConductor((state, level, pos) -> false));
        registerDefaultState(defaultBlockState().setValue(PART, 0).setValue(BlockStateProperties.WATERLOGGED, false));
    }
    @Override protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) { builder.add(PART, BlockStateProperties.WATERLOGGED); }
    @Override protected RenderShape getRenderShape(BlockState state) { return RenderShape.INVISIBLE; }
    @Override protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) { return SHAPE; }
    @Override protected VoxelShape getVisualShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) { return Shapes.empty(); }
    @Override protected int getLightBlock(BlockState state, BlockGetter level, BlockPos pos) { return 0; }
    @Override protected boolean propagatesSkylightDown(BlockState state, BlockGetter level, BlockPos pos) { return state.getFluidState().isEmpty(); }
    @Override protected FluidState getFluidState(BlockState state) { return state.getValue(BlockStateProperties.WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state); }
    @Override public ItemStack getCloneItemStack(LevelReader level, BlockPos pos, BlockState state) { return new ItemStack(GeoDecorContent.block(GeoDecorKind.LARGE_JELLERCEL)); }

    private static BlockPos anchor(Level level, BlockPos pos, BlockState state) {
        int part = state.getValue(PART);
        if (part != 0) {
            BlockPos at = pos.offset(-(part & 1), -((part >> 1) & 1), -((part >> 2) & 1));
            return level.hasChunkAt(at) && level.getBlockState(at).is(GeoDecorContent.block(GeoDecorKind.LARGE_JELLERCEL)) ? at : null;
        }
        for (int candidate = 1; candidate <= 7; candidate++) {
            BlockPos at = pos.offset(-(candidate & 1), -((candidate >> 1) & 1), -((candidate >> 2) & 1));
            if (level.hasChunkAt(at) && level.getBlockState(at).is(GeoDecorContent.block(GeoDecorKind.LARGE_JELLERCEL))) return at;
        }
        return null;
    }

    static boolean belongsTo(Level level, BlockPos pos, BlockState state, BlockPos core, int part) {
        if (!state.is(GeoDecorContent.JELLERCEL_COLLISION.get())) return false;
        int stored = state.getValue(PART);
        if (stored != 0) return stored == part;
        BlockPos found = anchor(level, pos, state);
        return found == null || found.equals(core);
    }

    @Override public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockState state = defaultBlockState().setValue(BlockStateProperties.WATERLOGGED, context.getLevel().getFluidState(context.getClickedPos()).getType() == Fluids.WATER);
        return anchor(context.getLevel(), context.getClickedPos(), state) == null ? null : state;
    }

    @Override protected BlockState updateShape(BlockState state, Direction side, BlockState neighbor, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
        if (state.getValue(BlockStateProperties.WATERLOGGED)) level.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
        return super.updateShape(state, side, neighbor, level, pos, neighborPos);
    }

    @Override protected void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        int part = state.getValue(PART);
        for (int candidate = part == 0 ? 1 : part; candidate <= (part == 0 ? 7 : part); candidate++) {
            BlockPos at = pos.offset(-(candidate & 1), -((candidate >> 1) & 1), -((candidate >> 2) & 1));
            if (!level.hasChunkAt(at)) return;
        }
        if (anchor(level, pos, state) == null) level.setBlock(pos, state.getFluidState().createLegacyBlock(), 3);
    }

    @Override public BlockState playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
        if (!level.isClientSide() && player.isCreative()) {
            BlockPos core = anchor(level, pos, state);
            if (core != null) level.destroyBlock(core, false);
        }
        return super.playerWillDestroy(level, pos, state, player);
    }

    @Override protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState replacement, boolean moving) {
        if (!level.isClientSide() && !state.is(replacement.getBlock())) {
            BlockPos core = anchor(level, pos, state);
            if (core != null) level.destroyBlock(core, true);
        }
        super.onRemove(state, level, pos, replacement, moving);
    }

    @Override protected void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
        super.entityInside(state, level, pos, entity);
        if (!level.isClientSide()) entity.hurt(level.damageSources().lightningBolt(), 1);
    }
    @Override public void stepOn(Level level, BlockPos pos, BlockState state, Entity entity) {
        super.stepOn(level, pos, state, entity);
        if (!level.isClientSide()) entity.hurt(level.damageSources().lightningBolt(), 1);
    }
}
