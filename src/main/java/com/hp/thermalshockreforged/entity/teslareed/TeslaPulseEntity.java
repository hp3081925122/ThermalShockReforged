package com.hp.thermalshockreforged.entity.teslareed;

import com.hp.thermalshockreforged.content.status.StatusContent;
import com.hp.thermalshockreforged.world.timing.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.*;
import net.minecraft.world.effect.*;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.*;

public final class TeslaPulseEntity extends Entity implements ServerEndTickTask {
    @Override public boolean hurtServer(net.minecraft.server.level.ServerLevel level, net.minecraft.world.damagesource.DamageSource source, float damage) { return false; }
    private int remaining = 13;
    private int phase;
    private int soundCopies;
    private boolean armed;
    public TeslaPulseEntity(EntityType<? extends TeslaPulseEntity> type, Level level) { super(type, level); noPhysics = true; setNoGravity(true); }
    public static boolean submit(ServerLevel level, Vec3 origin) {
        var task = TeslaReedContent.PULSE.get().create(level, net.minecraft.world.entity.EntitySpawnReason.TRIGGERED);
        if (task == null) return false;
        task.setPos(origin); task.armed = true;
        return level.addFreshEntity(task);
    }
    @Override protected void defineSynchedData(SynchedEntityData.Builder builder) { }
    @Override public boolean canTeleport(Level from, Level to) { return false; }
    @Override public void onAddedToLevel() { super.onAddedToLevel(); if (armed && level() instanceof ServerLevel server) ServerEndTickTasks.schedule(server, getUUID()); }
    @Override public void tick() { super.tick(); if (level() instanceof ServerLevel server) { if (armed) ServerEndTickTasks.schedule(server, getUUID()); else discard(); } }
    @Override public void endTick(ServerLevel level) {
        if (!armed || isRemoved()) return;
        if (--remaining > 0) { ServerEndTickTasks.schedule(level, getUUID()); return; }
        int committed = phase++;
        if (committed == 0) {
            var targets = level.getEntitiesOfClass(Monster.class, new AABB(position(), position()).inflate(4), entity -> true);
            soundCopies = targets.size();
            for (var target : targets) {
                target.addEffect(new MobEffectInstance(StatusContent.ELECTRIFIED, 120, 0));
                target.addEffect(new MobEffectInstance(MobEffects.SLOWNESS, 90, 4));
                target.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 90, 1));
            }
        }
        for (int index = 0; index < soundCopies; index++) level.playSound(null, blockPosition(), SoundEvents.AXE_WAX_OFF, SoundSource.NEUTRAL, 1, 3);
        if (phase >= 4 || soundCopies == 0) { armed = false; discard(); }
        else { remaining = 2; ServerEndTickTasks.schedule(level, getUUID()); }
    }
    @Override protected void addAdditionalSaveData(net.minecraft.world.level.storage.ValueOutput output) { output.putBoolean("Armed", armed); output.putInt("Remaining", remaining); output.putInt("Phase", phase); output.putInt("SoundCopies", soundCopies); }
    @Override protected void readAdditionalSaveData(net.minecraft.world.level.storage.ValueInput input) { remaining = Math.clamp(input.getIntOr("Remaining", 0), 1, 13); phase = Math.clamp(input.getIntOr("Phase", 0), 0, 4); soundCopies = Math.max(0, input.getIntOr("SoundCopies", 0)); armed = input.getBooleanOr("Armed", false) && phase < 4; }
}
