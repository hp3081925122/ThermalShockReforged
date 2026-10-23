package com.hp.thermalshockreforged.content.growth;

import net.minecraft.core.*;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
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
        super(Properties.of().mapColor(MapColor.TERRACOTTA_BLUE).sound(SoundType.CHAIN).instabreak().lightLevel(s->light).noCollision().noOcclusion().pushReaction(PushReaction.DESTROY).isRedstoneConductor((s,l,p)->false));
        registerDefaultState(defaultBlockState().setValue(WATERLOGGED, false));
    }
    @Override protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) { super.createBlockStateDefinition(builder); builder.add(WATERLOGGED); }
    @Override public BlockState getStateForPlacement(BlockPlaceContext context) { return defaultBlockState().setValue(WATERLOGGED, context.getLevel().getFluidState(context.getClickedPos()).getType() == Fluids.WATER); }
    @Override protected FluidState getFluidState(BlockState state) { return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state); }
    @Override protected BlockState updateShape(BlockState state, net.minecraft.world.level.LevelReader level, net.minecraft.world.level.ScheduledTickAccess ticks, BlockPos pos, Direction facing, BlockPos neighborPos, BlockState neighbor, net.minecraft.util.RandomSource random) {
        if (state.getValue(WATERLOGGED)) ticks.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
        return super.updateShape(state, level, ticks, pos, facing, neighborPos, neighbor, random);
    }
    @Override protected int getLightDampening(BlockState state) { return 0; }
    @Override protected boolean propagatesSkylightDown(BlockState state) { return state.getFluidState().isEmpty(); }
    @Override protected VoxelShape getVisualShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) { return Shapes.empty(); }
    private void checkSoil(Level level, BlockPos pos) {
        if (!(level instanceof ServerLevel server) || GrowthSoil.accepts(level, pos, true)) return;
        var state = level.getBlockState(pos);
        if (!state.is(this) || !level.destroyBlock(pos, false) || level.getBlockState(pos).is(this)) return;
        com.hp.thermalshockreforged.content.loot.LootContent.dropBlockTable(server, "gameplay/sparkler_withering", state, pos, net.minecraft.world.phys.Vec3.atCenterOf(pos));
    }
    @Override protected void onPlace(BlockState state, Level level, BlockPos pos, BlockState previous, boolean moving) { super.onPlace(state, level, pos, previous, moving); checkSoil(level, pos); }
    @Override protected void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, net.minecraft.world.level.redstone.Orientation from, boolean moving) { super.neighborChanged(state, level, pos, block, from, moving); checkSoil(level, pos); }
}
