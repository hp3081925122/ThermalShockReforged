package com.hp.thermalshockreforged.entity.orb;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public final class StopperOrbEntity extends OrbEntityBase {
    public StopperOrbEntity(EntityType<? extends StopperOrbEntity> type, Level level) { super(type, level, "entity.thermal_shock_reforged.stopper_orb_sneak"); }
    @Override protected boolean canAffect(Player player) { return !player.isShiftKeyDown(); }
    @Override protected void spawnImpactParticles(net.minecraft.server.level.ServerLevel level) { level.sendParticles(OrbContent.BLUE_X_BLAST.get(), getX(), getY(), getZ(), 5, 1, 1, 1, 0); }
}
