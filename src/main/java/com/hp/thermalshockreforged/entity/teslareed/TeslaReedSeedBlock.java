package com.hp.thermalshockreforged.entity.teslareed;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.*;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.*;
import net.minecraft.world.level.material.MapColor;

public final class TeslaReedSeedBlock extends Block {
    public TeslaReedSeedBlock() { super(BlockBehaviour.Properties.of().mapColor(MapColor.PLANT).sound(SoundType.GLASS).strength(1, 10)); }
    @Override public int getLightBlock(BlockState state, BlockGetter level, BlockPos pos) { return 15; }
    @Override protected void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean moving) {
        super.onPlace(state, level, pos, oldState, moving);
        if (!(level instanceof ServerLevel server) || oldState.is(this) || !level.getBlockState(pos).is(this)) return;
        if (!level.setBlock(pos, Blocks.AIR.defaultBlockState(), 3) || level.getBlockState(pos).is(this)) {
            com.hp.thermalshockreforged.ThermalShockReforged.LOGGER.debug("Tesla reed seed consumption rejected: position={}, dimension={}", pos, server.dimension().identifier());
            return;
        }
        var plant = TeslaReedContent.TESLA_REED.get().spawn(server, pos, EntitySpawnReason.MOB_SUMMONED);
        if (plant == null || server.getEntity(plant.getUUID()) != plant) { if (plant != null) plant.discard(); Block.dropResources(state, server, pos); return; }
        server.sendParticles(ParticleTypes.POOF, pos.getX() + .5, pos.getY() + .5, pos.getZ() + .5, 20, .5, .5, .5, 0);
        server.playSound(null, pos, SoundEvents.GRASS_PLACE, SoundSource.BLOCKS, 1, 2);
    }
}
