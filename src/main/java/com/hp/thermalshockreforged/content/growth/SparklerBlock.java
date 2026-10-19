package com.hp.thermalshockreforged.content.growth;

import net.minecraft.core.*;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.*;
import net.minecraft.world.level.block.state.properties.*;
import net.minecraft.world.level.material.*;
import net.minecraft.world.phys.shapes.*;

public final class SparklerBlock extends Block implements SimpleWaterloggedBlock {
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    public SparklerBlock(int light) {
        super(Properties.of().mapColor(MapColor.TERRACOTTA_BLUE).sound(SoundType.CHAIN).instabreak().lightLevel(s->light).noCollission().noOcclusion().pushReaction(PushReaction.DESTROY).isRedstoneConductor((s,l,p)->false));
        registerDefaultState(defaultBlockState().setValue(WATERLOGGED, false));
    }
    @Override protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) { super.createBlockStateDefinition(builder); builder.add(WATERLOGGED); }
    @Override public BlockState getStateForPlacement(BlockPlaceContext context) { return defaultBlockState().setValue(WATERLOGGED, context.getLevel().getFluidState(context.getClickedPos()).getType() == Fluids.WATER); }
    @Override protected FluidState getFluidState(BlockState state) { return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state); }
    @Override protected BlockState updateShape(BlockState state, Direction facing, BlockState neighbor, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
        if (state.getValue(WATERLOGGED)) level.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
        return super.updateShape(state, facing, neighbor, level, pos, neighborPos);
    }
    @Override protected int getLightBlock(BlockState state, BlockGetter level, BlockPos pos) { return 0; }
    @Override protected boolean propagatesSkylightDown(BlockState state, BlockGetter level, BlockPos pos) { return state.getFluidState().isEmpty(); }
    @Override protected VoxelShape getVisualShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) { return Shapes.empty(); }
    private void checkSoil(Level level, BlockPos pos) {
        if (!(level instanceof ServerLevel server) || GrowthSoil.accepts(level, pos, true)) return;
        if (!level.getBlockState(pos).is(this) || !level.destroyBlock(pos, false) || level.getBlockState(pos).is(this)) return;
        var drop = new ItemEntity(level, pos.getX()+.5, pos.getY()+.5, pos.getZ()+.5, new ItemStack(BuiltInRegistries.ITEM.get(ResourceLocation.fromNamespaceAndPath("thermal_shock_reforged", "steelstem"))));
        drop.setPickUpDelay(10);
        server.addFreshEntity(drop);
    }
    @Override protected void onPlace(BlockState state, Level level, BlockPos pos, BlockState previous, boolean moving) { super.onPlace(state, level, pos, previous, moving); checkSoil(level, pos); }
    @Override protected void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, BlockPos from, boolean moving) { super.neighborChanged(state, level, pos, block, from, moving); checkSoil(level, pos); }
    @Override public boolean onDestroyedByPlayer(BlockState state, Level level, BlockPos pos, Player player, boolean harvest, FluidState fluid) {
        boolean result = super.onDestroyedByPlayer(state, level, pos, player, harvest, fluid);
        if (result && !level.getBlockState(pos).is(this)) GrowthHarvest.harvest(level, pos, GrowthKind.SPARKLER, player);
        return result;
    }
}
