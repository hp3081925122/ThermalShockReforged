package com.hp.thermalshockreforged.content.metalblocks;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.AttachFace;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;

public final class MetalStorageBlock extends Block {
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
    public static final EnumProperty<AttachFace> FACE = BlockStateProperties.ATTACH_FACE;

    public MetalStorageBlock(MapColor mapColor) {
        super(Properties.of().mapColor(mapColor).instrument(NoteBlockInstrument.BASEDRUM).sound(SoundType.METAL).strength(2, 10).requiresCorrectToolForDrops());
        registerDefaultState(defaultBlockState().setValue(FACING, Direction.NORTH).setValue(FACE, AttachFace.WALL));
    }
    @Override protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) { builder.add(FACING, FACE); }
    @Override public BlockState getStateForPlacement(BlockPlaceContext context) {
        AttachFace face = switch (context.getNearestLookingDirection()) {
            case UP -> AttachFace.CEILING;
            case DOWN -> AttachFace.FLOOR;
            default -> AttachFace.WALL;
        };
        return defaultBlockState().setValue(FACE, face).setValue(FACING, context.getHorizontalDirection().getOpposite());
    }
    @Override protected BlockState rotate(BlockState state, Rotation rotation) { return state.setValue(FACING, rotation.rotate(state.getValue(FACING))); }
    @Override protected BlockState mirror(BlockState state, Mirror mirror) { return state.rotate(mirror.getRotation(state.getValue(FACING))); }
    @Override protected void onPlace(BlockState state, Level level, BlockPos pos, BlockState previous, boolean moving) {
        super.onPlace(state, level, pos, previous, moving);
        if (level instanceof ServerLevel server) server.sendParticles(ParticleTypes.ELECTRIC_SPARK, pos.getX() + .5, pos.getY() + .5, pos.getZ() + .5, 10, .5, .5, .5, .04);
    }
}
