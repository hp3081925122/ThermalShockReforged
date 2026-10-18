package com.hp.thermalshockreforged.content.decor.nativeblocks;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

class FacingDecorBlock extends NativeDecorBlock {
    static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
    enum Outline { FULL, BARS, CORNER }
    private static final VoxelShape NORTH = Block.box(0, 0, 0, 16, 32, 1);
    private static final VoxelShape SOUTH = Block.box(0, 0, 15, 16, 32, 16);
    private static final VoxelShape EAST = Block.box(15, 0, 0, 16, 32, 16);
    private static final VoxelShape WEST = Block.box(0, 0, 0, 1, 32, 16);
    private static final VoxelShape NORTH_CORNER = Shapes.or(EAST, NORTH);
    private static final VoxelShape SOUTH_CORNER = Shapes.or(WEST, SOUTH);
    private static final VoxelShape EAST_CORNER = Shapes.or(SOUTH, EAST);
    private static final VoxelShape WEST_CORNER = Shapes.or(NORTH, WEST);
    private final Outline outline;

    FacingDecorBlock(Properties properties, Traits traits, Outline outline, String... descriptions) {
        super(properties, traits, null, descriptions);
        this.outline = outline;
        registerDefaultState(stateDefinition.any().setValue(FACING, Direction.NORTH));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    @Override
    protected BlockState rotate(BlockState state, Rotation rotation) {
        return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
    }

    @Override
    protected BlockState mirror(BlockState state, Mirror mirror) {
        return state.rotate(mirror.getRotation(state.getValue(FACING)));
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return switch (outline) {
            case FULL -> super.getShape(state, level, pos, context);
            case BARS -> switch (state.getValue(FACING)) {
                case NORTH -> NORTH;
                case EAST -> EAST;
                case WEST -> WEST;
                default -> SOUTH;
            };
            case CORNER -> switch (state.getValue(FACING)) {
                case NORTH -> NORTH_CORNER;
                case EAST -> EAST_CORNER;
                case WEST -> WEST_CORNER;
                default -> SOUTH_CORNER;
            };
        };
    }
}
