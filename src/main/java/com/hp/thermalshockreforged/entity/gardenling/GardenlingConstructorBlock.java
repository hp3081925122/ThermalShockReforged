package com.hp.thermalshockreforged.entity.gardenling;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.*;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;

final class GardenlingConstructorBlock extends Block {
    GardenlingConstructorBlock() { super(Properties.of().mapColor(MapColor.COLOR_PURPLE).sound(SoundType.GLASS).strength(1, 10)); }

    @Override
    protected int getLightBlock(BlockState state, BlockGetter level, BlockPos pos) { return 15; }

    @Override
    protected void onPlace(BlockState state, Level level, BlockPos pos, BlockState previous, boolean moving) {
        super.onPlace(state, level, pos, previous, moving);
        if (previous.is(this) || !(level instanceof ServerLevel server) || !level.getBlockState(pos).is(this) || !server.removeBlock(pos, false)) return;
        GardenlingContent.GARDENLING.get().spawn(server, pos, EntitySpawnReason.MOB_SUMMONED);
        server.sendParticles(ParticleTypes.POOF, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, 20, 0.5, 0.5, 0.5, 0);
        server.playSound(null, pos, SoundEvents.BEACON_ACTIVATE, SoundSource.BLOCKS, 1, 2);
        server.playSound(null, pos, SoundEvents.ANVIL_PLACE, SoundSource.BLOCKS, 1, 2);
    }
}
