package com.hp.thermalshockreforged.entity.monolith;

import com.hp.thermalshockreforged.ThermalShockReforged;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.event.EventHooks;

public final class DormantMonolithEntity extends MonolithBaseEntity {
    public DormantMonolithEntity(EntityType<? extends DormantMonolithEntity> type, Level level) { super(type, level, "monolithsleep25"); }
    @Override public boolean canCollideWith(Entity entity) { return true; }
    public boolean canBeCollidedWith() { return true; }
    @Override protected String movementAnimation(boolean moving) { return "sleep"; }
    @Override protected java.util.List<String> procedureAnimations() { return java.util.List.of(); }
    @Override public boolean hurtServer(net.minecraft.server.level.ServerLevel level, DamageSource source, float amount) {
        if (!(source.getEntity() instanceof Player) || !(level() instanceof ServerLevel server)) return super.hurtServer(level, source, amount);
        if (!isAlive() || isRemoved() || converted || converting || conversionRetry > 0) return false;
        converting = true;
        try {
            var replacement = MonolithContent.ACTIVE.get().create(server, net.minecraft.world.entity.EntitySpawnReason.MOB_SUMMONED);
            if (replacement == null) { conversionRetry = 20; return false; }
            var pos = blockPosition();
            replacement.moveTo(pos.getX() + .5, pos.getY(), pos.getZ() + .5, getYRot(), 0);
            EventHooks.finalizeMobSpawn(replacement, server, server.getCurrentDifficultyAt(pos), EntitySpawnReason.MOB_SUMMONED, null);
            replacement.setYRot(getYRot()); replacement.yBodyRot = getYRot(); replacement.yHeadRot = getYRot(); replacement.setXRot(getXRot()); replacement.setDeltaMovement(Vec3.ZERO);
            if (replacement.isRemoved() || replacement.isSpawnCancelled() || !server.addFreshEntity(replacement)) { replacement.discard(); conversionRetry = 20; ThermalShockReforged.LOGGER.debug("Dormant monolith wake rejected: source={}, position={}", getUUID(), pos); return false; }
            converted = true;
            replacement.playAmbientSound();
            ThermalShockReforged.LOGGER.debug("Dormant monolith awakened: source={}, replacement={}, owner={}, amount={}", getUUID(), replacement.getUUID(), source.getEntity().getUUID(), amount);
            discard();
        } finally { converting = false; }
        return false;
    }
}
