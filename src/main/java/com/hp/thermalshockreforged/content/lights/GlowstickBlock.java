package com.hp.thermalshockreforged.content.lights;

import java.util.List;
import net.minecraft.core.*;
import net.minecraft.world.Containers;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.*;
import net.minecraft.world.level.block.state.properties.*;
import net.minecraft.world.level.material.*;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.phys.shapes.*;

public final class GlowstickBlock extends Block implements EntityBlock, SimpleWaterloggedBlock {
    public static final IntegerProperty ANIMATION = IntegerProperty.create("animation", 0, 1);
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    private static final VoxelShape SHAPE = box(7, 0, 7, 9, 16, 9);

    public GlowstickBlock() {
        super(Properties.of().mapColor(MapColor.COLOR_LIGHT_BLUE).sound(SoundType.SPORE_BLOSSOM).instabreak().lightLevel(state -> 9)
                .noCollision().noOcclusion().pushReaction(PushReaction.DESTROY)
                .emissiveRendering((s, l, p) -> true).isRedstoneConductor((s, l, p) -> false));
        registerDefaultState(stateDefinition.any().setValue(ANIMATION, 0).setValue(WATERLOGGED, false));
    }

    @Override protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) { builder.add(ANIMATION, WATERLOGGED); }
    @Override protected RenderShape getRenderShape(BlockState state) { return RenderShape.INVISIBLE; }
    @Override public BlockEntity newBlockEntity(BlockPos pos, BlockState state) { return new GlowstickBlockEntity(pos, state); }
    @Override protected int getLightDampening(BlockState state) { return 0; }
    @Override protected boolean propagatesSkylightDown(BlockState state) { return state.getFluidState().isEmpty(); }
    @Override protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) { return SHAPE; }
    @Override public BlockState getStateForPlacement(BlockPlaceContext context) {
        return defaultBlockState().setValue(WATERLOGGED, context.getLevel().getFluidState(context.getClickedPos()).getType() == Fluids.WATER);
    }
    @Override protected FluidState getFluidState(BlockState state) { return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state); }
    @Override protected BlockState updateShape(BlockState state, net.minecraft.world.level.LevelReader level, net.minecraft.world.level.ScheduledTickAccess ticks, BlockPos pos, Direction facing, BlockPos neighborPos, BlockState neighbor, net.minecraft.util.RandomSource random) {
        if (state.getValue(WATERLOGGED)) ticks.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
        return super.updateShape(state, level, ticks, pos, facing, neighborPos, neighbor, random);
    }
    private void checkHanging(Level level, BlockPos pos) {
        if (!level.isClientSide() && level.getBlockState(pos.above()).is(Blocks.AIR)) level.destroyBlock(pos, true);
    }
    @Override protected void onPlace(BlockState state, Level level, BlockPos pos, BlockState previous, boolean moving) {
        super.onPlace(state, level, pos, previous, moving);
        checkHanging(level, pos);
    }
    @Override protected void neighborChanged(BlockState state, Level level, BlockPos pos, Block neighbor, net.minecraft.world.level.redstone.Orientation neighborPos, boolean moving) {
        super.neighborChanged(state, level, pos, neighbor, neighborPos, moving);
        checkHanging(level, pos);
    }
}
