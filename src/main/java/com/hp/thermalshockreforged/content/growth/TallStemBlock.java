package com.hp.thermalshockreforged.content.growth;

import net.minecraft.core.*;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.*;
import net.minecraft.world.level.block.state.properties.*;
import net.minecraft.world.level.material.*;
import net.neoforged.neoforge.common.CommonHooks;

public class TallStemBlock extends SugarCaneBlock {
    private final int maximumHeight;
    public TallStemBlock() {
        this(Properties.of().mapColor(MapColor.PLANT).randomTicks().sound(SoundType.BAMBOO).instabreak().speedFactor(.8f).jumpFactor(.8f).noCollission().offsetType(OffsetType.NONE).pushReaction(PushReaction.DESTROY), 9);
    }
    protected TallStemBlock(Properties properties, int maximumHeight) { super(properties); this.maximumHeight = maximumHeight; }
    @Override public int getFlammability(BlockState state, BlockGetter level, BlockPos pos, Direction face) { return 100; }
    @Override public int getFireSpreadSpeed(BlockState state, BlockGetter level, BlockPos pos, Direction face) { return 60; }
    @Override protected boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        BlockState soil = level.getBlockState(pos.below());
        if (soil.is(this)) return true;
        var decision = soil.canSustainPlant(level, pos.below(), Direction.UP, state);
        if (!decision.isDefault()) return decision.isTrue();
        return soil.is(BlockTags.DIRT) || soil.is(Blocks.FARMLAND) || super.canSurvive(state, level, pos);
    }
    @Override protected void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        boolean water = this instanceof Waterlogged && level.getBlockState(pos.above()).is(Blocks.WATER);
        if (!level.isEmptyBlock(pos.above()) && !water) return;
        int height = 1;
        while (level.getBlockState(pos.below(height)).is(this)) height++;
        if (height >= maximumHeight || !CommonHooks.canCropGrow(level, pos, state, true)) return;
        int age = state.getValue(AGE);
        if (age == 15) {
            BlockState grown = defaultBlockState();
            if (this instanceof Waterlogged) grown = grown.setValue(Waterlogged.WATERLOGGED, water);
            if (level.setBlockAndUpdate(pos.above(), grown)) CommonHooks.fireCropGrowPost(level, pos.above(), grown);
            level.setBlock(pos, state.setValue(AGE, 0), 4);
        } else level.setBlock(pos, state.setValue(AGE, age + 1), 4);
    }
    public static final class Waterlogged extends TallStemBlock implements SimpleWaterloggedBlock {
        public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
        public Waterlogged() {
            super(Properties.of().mapColor(MapColor.PLANT).randomTicks().sound(SoundType.GRASS).instabreak().hasPostProcess((s,l,p)->true).emissiveRendering((s,l,p)->true).lightLevel(s->10).noCollission().offsetType(OffsetType.XZ).pushReaction(PushReaction.DESTROY), 12);
            registerDefaultState(defaultBlockState().setValue(WATERLOGGED, false));
        }
        @Override protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) { super.createBlockStateDefinition(builder); builder.add(WATERLOGGED); }
        @Override public BlockState getStateForPlacement(BlockPlaceContext context) {
            BlockState placed=super.getStateForPlacement(context);
            return placed==null?null:placed.setValue(WATERLOGGED,context.getLevel().getFluidState(context.getClickedPos()).getType()==Fluids.WATER);
        }
        @Override protected FluidState getFluidState(BlockState state) { return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state); }
        @Override protected BlockState updateShape(BlockState state, Direction facing, BlockState neighbor, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
            if (state.getValue(WATERLOGGED)) level.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
            return super.updateShape(state, facing, neighbor, level, pos, neighborPos);
        }
    }
}
