package com.hp.thermalshockreforged.content.weapons;

import com.hp.thermalshockreforged.ThermalShockReforged;
import com.hp.thermalshockreforged.entity.projectile.VolcanoBulletEntity;
import com.hp.thermalshockreforged.world.timing.*;
import java.util.UUID;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.*;
import net.minecraft.sounds.*;
import net.minecraft.world.entity.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public final class VolcanoBurstEntity extends Entity implements ServerEndTickTask {
    @Override public boolean hurtServer(net.minecraft.server.level.ServerLevel level, net.minecraft.world.damagesource.DamageSource source, float damage) { return false; }
    private UUID owner;
    private int ownerEntityId;
    private Vec3 lastLook = new Vec3(0, 0, 1);
    private int elapsed;
    private boolean armed;
    public VolcanoBurstEntity(EntityType<? extends VolcanoBurstEntity> type, Level level) { super(type, level); noPhysics = true; setNoGravity(true); }
    public static boolean submit(ServerLevel level, LivingEntity shooter) {
        var task = WeaponContent.VOLCANO_BURST.get().create(level, net.minecraft.world.entity.EntitySpawnReason.TRIGGERED);
        if (task == null) return false;
        task.setPos(shooter.position()); task.owner = shooter.getUUID(); task.ownerEntityId = shooter.getId(); task.lastLook = shooter.getLookAngle(); task.armed = true;
        if (!level.addFreshEntity(task)) return false;
        task.fire(level, shooter);
        ThermalShockReforged.LOGGER.debug("Volcano burst started: task={}, owner={}, anchor={}", task.getUUID(), task.owner, task.position());
        return true;
    }
    @Override protected void defineSynchedData(SynchedEntityData.Builder builder) { }
    @Override public boolean canTeleport(Level from, Level to) { return false; }
    @Override public void onAddedToLevel() { super.onAddedToLevel(); if (armed && level() instanceof ServerLevel server) ServerEndTickTasks.schedule(server, getUUID()); }
    @Override public void tick() { super.tick(); if (level() instanceof ServerLevel server) { if (!armed) discard(); else ServerEndTickTasks.schedule(server, getUUID()); } }
    @Override public void endTick(ServerLevel level) {
        if (!armed || isRemoved()) return;
        int stage = ++elapsed;
        if (stage <= 4) {
            var shooter = level.getServer().getPlayerList().getPlayer(owner);
            fire(level, shooter != null && shooter.getId() == ownerEntityId && !shooter.isRemoved() ? shooter : null);
        } else if (stage == 16) {
            level.playSound(null, blockPosition(), SoundEvents.STONE_BUTTON_CLICK_ON, SoundSource.NEUTRAL, 1.5F, -4);
        }
        if (stage >= 20) {
            armed = false; discard();
            level.playSound(null, blockPosition(), SoundEvents.STONE_BUTTON_CLICK_OFF, SoundSource.NEUTRAL, 1.5F, -4);
        } else ServerEndTickTasks.schedule(level, getUUID());
    }
    private void fire(ServerLevel level, LivingEntity shooter) {
        if (shooter != null) lastLook = shooter.getLookAngle();
        VolcanoBulletEntity.fireBurstShot(level, shooter, owner, position().add(0, 1.5, 0), lastLook);
        level.playSound(null, blockPosition(), SoundEvents.WITHER_SKELETON_HURT, SoundSource.NEUTRAL, .7F, 1);
        if (shooter != null) { shooter.setDeltaMovement(shooter.getDeltaMovement().add(lastLook.scale(-.3))); shooter.hurtMarked = true; }
    }
    @Override protected void addAdditionalSaveData(net.minecraft.world.level.storage.ValueOutput output) {
        if (owner != null) output.putString("VolcanoOwner", owner.toString());
        output.putInt("VolcanoOwnerEntityId", ownerEntityId); output.putInt("VolcanoElapsed", elapsed); output.putBoolean("VolcanoArmed", armed);
        output.putDouble("VolcanoLookX", lastLook.x); output.putDouble("VolcanoLookY", lastLook.y); output.putDouble("VolcanoLookZ", lastLook.z);
    }
    @Override protected void readAdditionalSaveData(net.minecraft.world.level.storage.ValueInput input) {
        owner = input.getString("VolcanoOwner").map(java.util.UUID::fromString).orElse(null);
        ownerEntityId = input.getIntOr("VolcanoOwnerEntityId", 0); elapsed = Math.clamp(input.getIntOr("VolcanoElapsed", 0), 0, 20); armed = owner != null && elapsed < 20 && input.getBooleanOr("VolcanoArmed", false);
        var look = new Vec3(input.getDoubleOr("VolcanoLookX", 0.0), input.getDoubleOr("VolcanoLookY", 0.0), input.getDoubleOr("VolcanoLookZ", 0.0));
        lastLook = Double.isFinite(look.x) && Double.isFinite(look.y) && Double.isFinite(look.z) && look.lengthSqr() > 1.0E-8 ? look.normalize() : new Vec3(0, 0, 1);
    }
}
