package com.hp.thermalshockreforged.content.vilebathblocks;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.RandomSource;
import net.minecraft.world.*;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;

final class ThermalGeyserBlock extends Block implements EntityBlock {
    ThermalGeyserBlock() { super(Properties.of().mapColor(MapColor.COLOR_GRAY).instrument(NoteBlockInstrument.BASEDRUM).sound(SoundType.NETHERRACK).strength(1, 5).requiresCorrectToolForDrops()); }
    @Override protected int getLightDampening(BlockState state) { return 15; }
    @Override public BlockEntity newBlockEntity(BlockPos pos, BlockState state) { return new ThermalGeyserBlockEntity(pos, state); }
    @Override public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) { level.addParticle(ParticleTypes.CAMPFIRE_SIGNAL_SMOKE, pos.getX() + .5, pos.getY() + 1, pos.getZ() + .5, 0, .1, 0); }
    @Override protected MenuProvider getMenuProvider(BlockState state, Level level, BlockPos pos) { return level.getBlockEntity(pos) instanceof ThermalGeyserBlockEntity geyser ? geyser : null; }
    @Override protected boolean triggerEvent(BlockState state, Level level, BlockPos pos, int id, int parameter) { super.triggerEvent(state, level, pos, id, parameter); return level.getBlockEntity(pos) instanceof ThermalGeyserBlockEntity geyser && geyser.triggerEvent(id, parameter); }
    @Override protected void affectNeighborsAfterRemoval(BlockState state, net.minecraft.server.level.ServerLevel level, BlockPos pos, boolean moving) { Containers.updateNeighboursAfterDestroy(state, level, pos); }
    @Override protected boolean hasAnalogOutputSignal(BlockState state) { return true; }
    @Override protected int getAnalogOutputSignal(BlockState state, Level level, BlockPos pos, net.minecraft.core.Direction direction) { return level.getBlockEntity(pos) instanceof ThermalGeyserBlockEntity geyser ? AbstractContainerMenu.getRedstoneSignalFromContainer(geyser) : 0; }
}
