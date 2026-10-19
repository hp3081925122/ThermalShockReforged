package com.hp.thermalshockreforged.entity.liftbug;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.*;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.*;
import net.minecraft.world.level.material.MapColor;

public final class LiftbugConstructorBlock extends Block {
    public LiftbugConstructorBlock() { super(BlockBehaviour.Properties.of().mapColor(MapColor.METAL).sound(SoundType.GLASS).strength(1, 10)); }
    @Override public int getLightBlock(BlockState state, BlockGetter level, BlockPos pos) { return 15; }
    @Override protected void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean moving) {
        super.onPlace(state, level, pos, oldState, moving);
        if (!(level instanceof ServerLevel server) || oldState.is(this) || !level.getBlockState(pos).is(this)) return;
        boolean clear = level.hasChunksAt(pos.getX() - 1, pos.getZ() - 1, pos.getX() + 1, pos.getZ() + 1);
        for (int dx = -1; dx <= 1 && clear; dx++) for (int dz = -1; dz <= 1; dz++) if ((dx != 0 || dz != 0) && !level.isEmptyBlock(pos.offset(dx, 0, dz))) { clear = false; break; }
        if (clear) {
            level.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
            var bug = LiftbugContent.LIFTBUG.get().spawn(server, pos, MobSpawnType.MOB_SUMMONED);
            if (bug != null && server.getEntity(bug.getUUID()) == bug) {
                bug.setDeltaMovement(0, 0, 0);
                server.sendParticles(ParticleTypes.POOF, pos.getX() + .5, pos.getY() + .5, pos.getZ() + .5, 20, .5, .5, .5, 0);
                server.playSound(null, pos, SoundEvents.SNIFFER_EGG_HATCH, SoundSource.NEUTRAL, 1, 1);
                return;
            }
            if (bug != null) bug.discard();
        } else level.destroyBlock(pos, false);
        Block.dropResources(state, server, pos);
        server.playSound(null, pos, SoundEvents.NOTE_BLOCK_BASS.value(), SoundSource.NEUTRAL, 1, 1);
        for (int dx = -2; dx <= 2; dx++) for (int dz = -2; dz <= 2; dz++) server.sendParticles(ParticleTypes.CRIT, pos.getX() + dx + .5, pos.getY() + .5, pos.getZ() + dz + .5, 1, .001, .001, .001, 0);
    }
}
