package com.hp.thermalshockreforged.content.weapons;

import com.hp.thermalshockreforged.ThermalShockReforged;
import com.hp.thermalshockreforged.content.ModDamageTypes;
import com.hp.thermalshockreforged.world.timing.*;
import java.util.Comparator;
import java.util.UUID;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;

public final class BuzzsawSweepEntity extends Entity implements ServerEndTickTask {
    @Override public boolean hurtServer(net.minecraft.server.level.ServerLevel level, net.minecraft.world.damagesource.DamageSource source, float damage) { return false; }
    private UUID owner;
    private int ownerEntityId;
    private int elapsed;
    private boolean armed;
    public BuzzsawSweepEntity(EntityType<? extends BuzzsawSweepEntity> type, Level level) { super(type, level); noPhysics = true; setNoGravity(true); }
    public static BuzzsawSweepEntity submit(ServerLevel level, LivingEntity wielder) {
        var sweep = WeaponContent.BUZZSAW_SWEEP.get().create(level, net.minecraft.world.entity.EntitySpawnReason.TRIGGERED);
        if (sweep == null) return null;
        sweep.setPos(wielder.position()); sweep.owner = wielder.getUUID(); sweep.ownerEntityId = wielder.getId(); sweep.armed = true;
        return level.addFreshEntity(sweep) ? sweep : null;
    }
    @Override protected void defineSynchedData(SynchedEntityData.Builder builder) { }
    @Override public boolean canTeleport(Level from, Level to) { return false; }
    @Override public void onAddedToLevel() { super.onAddedToLevel(); if (armed && level() instanceof ServerLevel server) ServerEndTickTasks.schedule(server, getUUID()); }
    @Override public void tick() { super.tick(); if (level() instanceof ServerLevel server) { if (armed) ServerEndTickTasks.schedule(server, getUUID()); else discard(); } }
    @Override public void endTick(ServerLevel level) {
        if (!armed || isRemoved()) return;
        if (!(level.getEntity(owner) instanceof LivingEntity wielder) || wielder.getId() != ownerEntityId || !wielder.isAlive() || wielder.isRemoved()) {
            armed = false; discard();
            ThermalShockReforged.LOGGER.debug("Buzzsaw sweep cancelled: task={}, owner={}, elapsed={}, reason=original_wielder_unavailable", getUUID(), owner, elapsed);
            return;
        }
        setPos(wielder.position());
        if (++elapsed >= 4) { armed = false; discard(); }
        cut(level, wielder);
        if (armed) ServerEndTickTasks.schedule(level, getUUID());
    }
    public void cut(ServerLevel level, LivingEntity wielder) {
        var center = wielder.position();
        var targets = level.getEntitiesOfClass(Entity.class, new AABB(center, center).inflate(1), target -> target != this && !target.getUUID().equals(owner) && !(target instanceof Player));
        targets.sort(Comparator.comparingDouble(target -> target.distanceToSqr(center)));
        var damage = new DamageSource(level.registryAccess().lookupOrThrow(Registries.DAMAGE_TYPE).getOrThrow(ModDamageTypes.THERM_IFRAME_BYPASS));
        for (Entity target : targets) {
            target.hurt(damage, 2);
            level.sendParticles(WeaponContent.BLOOD_SPLATTER.get(), target.getX(), target.getEyeY(), target.getZ(), 0, 0, 1, 0, 1);
        }
    }
    @Override protected void addAdditionalSaveData(net.minecraft.world.level.storage.ValueOutput output) {
        if (owner != null) output.putString("BuzzsawOwner", owner.toString());
        output.putInt("BuzzsawOwnerEntityId", ownerEntityId); output.putInt("BuzzsawElapsed", elapsed); output.putBoolean("BuzzsawArmed", armed);
    }
    @Override protected void readAdditionalSaveData(net.minecraft.world.level.storage.ValueInput input) {
        owner = input.getString("BuzzsawOwner").map(java.util.UUID::fromString).orElse(null);
        ownerEntityId = input.getIntOr("BuzzsawOwnerEntityId", 0); elapsed = Math.clamp(input.getIntOr("BuzzsawElapsed", 0), 0, 4);
        armed = owner != null && elapsed < 4 && input.getBooleanOr("BuzzsawArmed", false);
    }
}
