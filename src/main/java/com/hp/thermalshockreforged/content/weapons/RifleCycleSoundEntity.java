package com.hp.thermalshockreforged.content.weapons;

import com.hp.thermalshockreforged.world.timing.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.*;
import net.minecraft.world.entity.*;
import net.minecraft.world.level.Level;

public final class RifleCycleSoundEntity extends Entity implements ServerEndTickTask {
    @Override public boolean hurtServer(net.minecraft.server.level.ServerLevel level, net.minecraft.world.damagesource.DamageSource source, float damage) { return false; }
    private int remaining = 5;
    private boolean armed;
    public RifleCycleSoundEntity(EntityType<? extends RifleCycleSoundEntity> type, Level level) { super(type, level); noPhysics = true; setNoGravity(true); }
    public static void submit(ServerLevel level, double x, double y, double z) {
        var task = WeaponContent.RIFLE_CYCLE_SOUND.get().create(level, net.minecraft.world.entity.EntitySpawnReason.TRIGGERED);
        if (task == null) return;
        task.setPos(x, y, z); task.armed = true; level.addFreshEntity(task);
    }
    @Override protected void defineSynchedData(SynchedEntityData.Builder builder) { }
    @Override public boolean canTeleport(Level from, Level to) { return false; }
    @Override public void onAddedToLevel() { super.onAddedToLevel(); if (armed && level() instanceof ServerLevel server) ServerEndTickTasks.schedule(server, getUUID()); }
    @Override public void tick() { super.tick(); if (level() instanceof ServerLevel server) { if (!armed) discard(); else ServerEndTickTasks.schedule(server, getUUID()); } }
    @Override public void endTick(ServerLevel level) {
        if (!armed || isRemoved()) return;
        if (--remaining > 0) { ServerEndTickTasks.schedule(level, getUUID()); return; }
        armed = false; discard(); level.playSound(null, blockPosition(), SoundEvents.STONE_BUTTON_CLICK_OFF, SoundSource.NEUTRAL, 1.5F, -2);
    }
    @Override protected void addAdditionalSaveData(net.minecraft.world.level.storage.ValueOutput output) { output.putInt("RifleCycleRemaining", remaining); output.putBoolean("RifleCycleArmed", armed); }
    @Override protected void readAdditionalSaveData(net.minecraft.world.level.storage.ValueInput input) { remaining = Math.clamp(input.getIntOr("RifleCycleRemaining", 0), 1, 5); armed = input.getBooleanOr("RifleCycleArmed", false); }
}
