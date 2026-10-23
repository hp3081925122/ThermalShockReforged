package com.hp.thermalshockreforged.content.weapons;

import com.hp.thermalshockreforged.world.timing.ServerEndTickTask;
import com.hp.thermalshockreforged.world.timing.ServerEndTickTasks;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

public final class SpeleothemSoundEntity extends Entity implements ServerEndTickTask {
    @Override public boolean hurtServer(net.minecraft.server.level.ServerLevel level, net.minecraft.world.damagesource.DamageSource source, float damage) { return false; }
    private int remaining = 35;
    private int firstRemaining = 3;
    private boolean armed;
    private boolean single;
    private float singleVolume = .7F;
    private float singlePitch = 3;
    public SpeleothemSoundEntity(EntityType<? extends SpeleothemSoundEntity> type, Level level) { super(type, level); noPhysics = true; setNoGravity(true); }
    public static void submit(ServerLevel level, double x, double y, double z, int first, int last) {
        var task = WeaponContent.SPELEOTHEM_SOUND.get().create(level, net.minecraft.world.entity.EntitySpawnReason.TRIGGERED);
        if (task == null) return;
        task.setPos(x, y, z); task.remaining = last; task.firstRemaining = last - first; task.armed = true; level.addFreshEntity(task);
    }
    public static void submitSingle(ServerLevel level, double x, double y, double z, int delay, float volume, float pitch) {
        var task = WeaponContent.SPELEOTHEM_SOUND.get().create(level, net.minecraft.world.entity.EntitySpawnReason.TRIGGERED);
        if (task == null) return;
        task.setPos(x, y, z); task.remaining = delay; task.single = true; task.singleVolume = volume; task.singlePitch = pitch; task.armed = true; level.addFreshEntity(task);
    }
    @Override public void onAddedToLevel() { super.onAddedToLevel(); if (armed && level() instanceof ServerLevel server) ServerEndTickTasks.schedule(server, getUUID()); }
    @Override public boolean canTeleport(Level from, Level to) { return false; }
    @Override public void tick() { super.tick(); if (level() instanceof ServerLevel server) { if (armed) ServerEndTickTasks.schedule(server, getUUID()); else discard(); } }
    @Override public void endTick(ServerLevel level) {
        if (!armed || isRemoved()) return;
        remaining--;
        if (single) {
            if (remaining <= 0) { armed = false; level.playSound(null, blockPosition(), SoundEvents.EXPERIENCE_ORB_PICKUP, SoundSource.NEUTRAL, singleVolume, singlePitch); discard(); }
            else ServerEndTickTasks.schedule(level, getUUID());
            return;
        }
        if (remaining == firstRemaining) level.playSound(null, blockPosition(), SoundEvents.EXPERIENCE_ORB_PICKUP, SoundSource.NEUTRAL, .4F, 3);
        if (remaining == 0) { level.playSound(null, blockPosition(), SoundEvents.EXPERIENCE_ORB_PICKUP, SoundSource.NEUTRAL, .4F, 1); armed = false; discard(); }
        else ServerEndTickTasks.schedule(level, getUUID());
    }
    @Override protected void defineSynchedData(net.minecraft.network.syncher.SynchedEntityData.Builder builder) { }
    @Override protected void addAdditionalSaveData(net.minecraft.world.level.storage.ValueOutput output) { output.putInt("SpearSoundRemaining", remaining); output.putInt("SpearSoundFirst", firstRemaining); output.putBoolean("SpearSoundArmed", armed); output.putBoolean("SpearSoundSingle", single); output.putFloat("SpearSoundVolume", singleVolume); output.putFloat("SpearSoundPitch", singlePitch); }
    @Override protected void readAdditionalSaveData(net.minecraft.world.level.storage.ValueInput input) {
        remaining = Math.clamp(input.getIntOr("SpearSoundRemaining", 0), 1, 55); firstRemaining = Math.clamp(input.getIntOr("SpearSoundFirst", 0), 1, 29); armed = input.getBooleanOr("SpearSoundArmed", false); single = input.getBooleanOr("SpearSoundSingle", false);
        singleVolume = Float.isFinite(input.getFloatOr("SpearSoundVolume", 0.0F)) ? Math.clamp(input.getFloatOr("SpearSoundVolume", 0.0F), 0, 1) : .7F;
        singlePitch = Float.isFinite(input.getFloatOr("SpearSoundPitch", 0.0F)) ? Math.clamp(input.getFloatOr("SpearSoundPitch", 0.0F), 0, 4) : 3;
    }
}
