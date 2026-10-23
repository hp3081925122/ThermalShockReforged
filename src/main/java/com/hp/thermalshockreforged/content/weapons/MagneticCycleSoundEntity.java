package com.hp.thermalshockreforged.content.weapons;

import com.hp.thermalshockreforged.ThermalShockReforged;
import com.hp.thermalshockreforged.world.timing.ServerEndTickTask;
import com.hp.thermalshockreforged.world.timing.ServerEndTickTasks;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

public final class MagneticCycleSoundEntity extends Entity implements ServerEndTickTask {
    private int remaining = 5;
    private int phase;
    private boolean armed;
    public MagneticCycleSoundEntity(EntityType<? extends MagneticCycleSoundEntity> type, Level level) { super(type, level); noPhysics = true; setNoGravity(true); }
    public static void submit(ServerLevel level, double x, double y, double z) {
        var task = WeaponContent.MAGNETIC_CYCLE_SOUND.get().create(level);
        if (task == null) return;
        task.setPos(x, y, z); task.armed = true;
        if (!level.addFreshEntity(task)) ThermalShockReforged.LOGGER.warn("Unable to submit magnetic crossbow cycle sounds at {}", task.blockPosition());
    }
    @Override protected void defineSynchedData(SynchedEntityData.Builder builder) { }
    @Override public boolean canChangeDimensions(Level from, Level to) { return false; }
    @Override public void onAddedToLevel() { super.onAddedToLevel(); if (armed && level() instanceof ServerLevel server) ServerEndTickTasks.schedule(server, getUUID()); }
    @Override public void tick() {
        super.tick();
        if (level() instanceof ServerLevel server) {
            if (!armed) discard();
            else ServerEndTickTasks.schedule(server, getUUID());
        }
    }
    @Override public void endTick(ServerLevel level) {
        if (!armed || isRemoved()) return;
        if (--remaining > 0) { ServerEndTickTasks.schedule(level, getUUID()); return; }
        float volume = phase == 0 ? .3F : phase == 1 ? .2F : .1F;
        float pitch = phase == 0 ? -1 : phase == 1 ? 1 : 2;
        phase++; remaining = 5;
        level.playSound(null, blockPosition(), SoundEvents.CROSSBOW_SHOOT, SoundSource.NEUTRAL, volume, pitch);
        if (phase == 3) { armed = false; discard(); }
        else ServerEndTickTasks.schedule(level, getUUID());
    }
    @Override protected void addAdditionalSaveData(net.minecraft.world.level.storage.ValueOutput output) { output.putInt("Remaining", remaining); output.putInt("Phase", phase); output.putBoolean("Armed", armed); }
    @Override protected void readAdditionalSaveData(net.minecraft.world.level.storage.ValueInput input) { remaining = Math.clamp(input.getIntOr("Remaining", 0), 1, 5); phase = Math.clamp(input.getIntOr("Phase", 0), 0, 3); armed = input.getBooleanOr("Armed", false) && phase < 3; }
}
