package com.hp.thermalshockreforged.content.outcrops;

import net.minecraft.core.BlockPos;
import net.minecraft.world.Containers;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.*;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;

public final class OutcropBlock extends Block implements EntityBlock {
    public static final IntegerProperty ANIMATION = IntegerProperty.create("animation", 0, 1);
    public final OutcropKind kind;
    public OutcropBlock(OutcropKind kind) {
        super(com.hp.thermalshockreforged.ModRegistration.block().mapColor(kind.mapColor).instrument(NoteBlockInstrument.BASEDRUM).sound(SoundType.NETHERRACK).strength(3, 5).requiresCorrectToolForDrops());
        this.kind = kind;
        registerDefaultState(defaultBlockState().setValue(ANIMATION, 0));
    }
    @Override protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) { builder.add(ANIMATION); }
    @Override protected RenderShape getRenderShape(BlockState state) { return RenderShape.INVISIBLE; }
    @Override public BlockEntity newBlockEntity(BlockPos pos, BlockState state) { return new OutcropBlockEntity(kind, pos, state); }
    @Override protected boolean propagatesSkylightDown(BlockState state) { return true; }
    @Override protected int getLightDampening(BlockState state) { return 0; }
    @Override protected MenuProvider getMenuProvider(BlockState state, Level level, BlockPos pos) {
        return level.getBlockEntity(pos) instanceof OutcropBlockEntity entity ? entity : null;
    }
    @Override protected boolean triggerEvent(BlockState state, Level level, BlockPos pos, int id, int parameter) {
        super.triggerEvent(state, level, pos, id, parameter);
        var entity = level.getBlockEntity(pos);
        return entity != null && entity.triggerEvent(id, parameter);
    }
    @Override protected void affectNeighborsAfterRemoval(BlockState state, net.minecraft.server.level.ServerLevel level, BlockPos pos, boolean moving) { Containers.updateNeighboursAfterDestroy(state, level, pos); }
}
