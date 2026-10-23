package com.hp.thermalshockreforged.content.weapons;

import com.hp.thermalshockreforged.world.timing.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public final class FlareGunCycleEntity extends Entity implements ServerEndTickTask {
    @Override public boolean hurtServer(net.minecraft.server.level.ServerLevel level, net.minecraft.world.damagesource.DamageSource source, float damage) { return false; }
    private int elapsed;
    private boolean alternate;
    private boolean armed;
    public FlareGunCycleEntity(EntityType<? extends FlareGunCycleEntity> type, Level level) { super(type, level); noPhysics = true; setNoGravity(true); }
    public static boolean submit(ServerLevel level, Vec3 anchor, boolean alternate) {
        var task = WeaponContent.FLARE_GUN_CYCLE.get().create(level, net.minecraft.world.entity.EntitySpawnReason.TRIGGERED);
        if (task == null) return false;
        task.setPos(anchor); task.alternate = alternate; task.armed = true;
        return level.addFreshEntity(task);
    }
    @Override protected void defineSynchedData(SynchedEntityData.Builder builder) { }
    @Override public boolean canTeleport(Level from, Level to) { return false; }
    @Override public void onAddedToLevel() { super.onAddedToLevel(); if (armed && level() instanceof ServerLevel server) ServerEndTickTasks.schedule(server, getUUID()); }
    @Override public void tick() { super.tick(); if (level() instanceof ServerLevel server) { if (armed) ServerEndTickTasks.schedule(server, getUUID()); else discard(); } }
    @Override public void endTick(ServerLevel level) {
        if (!armed || isRemoved()) return;
        int stage = ++elapsed;
        if (stage >= (alternate ? 20 : 18)) { armed = false; discard(); }
        if (alternate && stage == 3) level.playSound(null, blockPosition(), SoundEvents.STEM_STEP, SoundSource.NEUTRAL, 1, 0);
        if ((!alternate && (stage == 8 || stage == 18)) || (alternate && stage == 20)) {
            level.playSound(null, blockPosition(), SoundEvents.STONE_BUTTON_CLICK_OFF, SoundSource.NEUTRAL, 1.5F, stage == 18 ? -2 : 0);
        }
        if (armed) ServerEndTickTasks.schedule(level, getUUID());
    }
    @Override protected void addAdditionalSaveData(net.minecraft.world.level.storage.ValueOutput output) { output.putInt("FlareCycleElapsed", elapsed); output.putBoolean("FlareCycleAlternate", alternate); output.putBoolean("FlareCycleArmed", armed); }
    @Override protected void readAdditionalSaveData(net.minecraft.world.level.storage.ValueInput input) {
        alternate = input.getBooleanOr("FlareCycleAlternate", false); elapsed = Math.clamp(input.getIntOr("FlareCycleElapsed", 0), 0, 20);
        armed = input.getBooleanOr("FlareCycleArmed", false) && elapsed < (alternate ? 20 : 18);
    }
}
