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
    private int remaining = 35;
    private int firstRemaining = 3;
    private boolean armed;
    public SpeleothemSoundEntity(EntityType<? extends SpeleothemSoundEntity> type, Level level) { super(type, level); noPhysics = true; setNoGravity(true); }
    public static void submit(ServerLevel level, double x, double y, double z, int first, int last) {
        var task = WeaponContent.SPELEOTHEM_SOUND.get().create(level);
        if (task == null) return;
        task.setPos(x, y, z); task.remaining = last; task.firstRemaining = last - first; task.armed = true; level.addFreshEntity(task);
    }
    @Override public void onAddedToLevel() { super.onAddedToLevel(); if (armed && level() instanceof ServerLevel server) ServerEndTickTasks.schedule(server, getUUID()); }
    @Override public boolean canChangeDimensions(Level from, Level to) { return false; }
    @Override public void tick() { super.tick(); if (level() instanceof ServerLevel server) { if (armed) ServerEndTickTasks.schedule(server, getUUID()); else discard(); } }
    @Override public void endTick(ServerLevel level) {
        if (!armed || isRemoved()) return;
        remaining--;
        if (remaining == firstRemaining) level.playSound(null, blockPosition(), SoundEvents.EXPERIENCE_ORB_PICKUP, SoundSource.NEUTRAL, .4F, 3);
        if (remaining == 0) { level.playSound(null, blockPosition(), SoundEvents.EXPERIENCE_ORB_PICKUP, SoundSource.NEUTRAL, .4F, 1); armed = false; discard(); }
        else ServerEndTickTasks.schedule(level, getUUID());
    }
    @Override protected void defineSynchedData(net.minecraft.network.syncher.SynchedEntityData.Builder builder) { }
    @Override protected void addAdditionalSaveData(CompoundTag tag) { tag.putInt("SpearSoundRemaining", remaining); tag.putInt("SpearSoundFirst", firstRemaining); tag.putBoolean("SpearSoundArmed", armed); }
    @Override protected void readAdditionalSaveData(CompoundTag tag) { remaining = Math.clamp(tag.getInt("SpearSoundRemaining"), 1, 55); firstRemaining = Math.clamp(tag.getInt("SpearSoundFirst"), 1, 29); armed = tag.getBoolean("SpearSoundArmed"); }
}
