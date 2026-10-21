package com.hp.thermalshockreforged.entity.orb;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public final class MoverOrbEntity extends OrbEntityBase {
    public MoverOrbEntity(EntityType<? extends MoverOrbEntity> type, Level level) { super(type, level, "entity.thermal_shock_reforged.mover_orb_sprint"); }
    @Override protected boolean canAffect(Player player) { return !player.isSprinting(); }
    @Override protected void spawnImpactParticles(net.minecraft.server.level.ServerLevel level) { level.sendParticles(ParticleTypes.SMALL_FLAME, getX(), getY(), getZ(), 5, 1, 1, 1, 0); }
}
