package com.hp.thermalshockreforged.content.weapons;

import com.hp.thermalshockreforged.ThermalShockReforged;
import com.hp.thermalshockreforged.content.ModDamageTypes;
import com.hp.thermalshockreforged.content.status.StatusContent;
import com.hp.thermalshockreforged.world.timing.*;
import java.util.Comparator;
import java.util.UUID;
import net.minecraft.core.registries.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.*;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;

public final class HiVoltPulseEntity extends Entity implements ServerEndTickTask {
    private UUID owner;
    private int elapsed;
    private boolean electric;
    private boolean armed;
    public HiVoltPulseEntity(EntityType<? extends HiVoltPulseEntity> type, Level level) { super(type, level); noPhysics = true; setNoGravity(true); }
    public static void submit(ServerLevel level, LivingEntity wielder, boolean electric) {
        var task = WeaponContent.HI_VOLT_PULSE.get().create(level);
        if (task == null) return;
        task.setPos(wielder.position()); task.owner = wielder.getUUID(); task.electric = electric; task.armed = true;
        if (!level.addFreshEntity(task)) return;
        if (electric) task.pulse(level);
        ThermalShockReforged.LOGGER.debug("Hi-Volt sequence started: task={}, owner={}, electric={}, anchor={}", task.getUUID(), task.owner, electric, task.position());
    }
    @Override protected void defineSynchedData(SynchedEntityData.Builder builder) { }
    @Override public boolean canChangeDimensions(Level from, Level to) { return false; }
    @Override public void onAddedToLevel() { super.onAddedToLevel(); if (armed && level() instanceof ServerLevel server) ServerEndTickTasks.schedule(server, getUUID()); }
    @Override public void tick() { super.tick(); if (level() instanceof ServerLevel server) { if (armed) ServerEndTickTasks.schedule(server, getUUID()); else discard(); } }
    @Override public void endTick(ServerLevel level) {
        if (!armed || isRemoved()) return;
        int stage = ++elapsed;
        int last = electric ? 122 : 16;
        if (stage >= last) { armed = false; discard(); }
        if (!electric) {
            if (stage == 16) level.playSound(null, blockPosition(), SoundEvents.EXPERIENCE_ORB_PICKUP, SoundSource.NEUTRAL, .7F, 3);
        } else {
            if (stage <= 18 && stage % 2 == 0) pulse(level);
            if (stage == 20) {
                var targets = targets(level);
                for (Entity target : targets) if (target instanceof LivingEntity living) living.addEffect(new MobEffectInstance(StatusContent.ELECTRIFIED, 360, 0));
                level.playSound(null, blockPosition(), BuiltInRegistries.SOUND_EVENT.getValue(Identifier.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, "galvanitehurt")), SoundSource.NEUTRAL, 1, 7);
                level.playSound(null, blockPosition(), SoundEvents.IRON_GOLEM_HURT, SoundSource.NEUTRAL, .7F, 5);
            }
            if (stage == 120 || stage == 122) level.playSound(null, blockPosition(), SoundEvents.EXPERIENCE_ORB_PICKUP, SoundSource.NEUTRAL, .7F, 4);
        }
        if (armed) ServerEndTickTasks.schedule(level, getUUID());
    }
    private java.util.List<Entity> targets(ServerLevel level) {
        var targets = level.getEntitiesOfClass(Entity.class, new AABB(position(), position()).inflate(2.5), target -> target != this && !target.getUUID().equals(owner));
        targets.sort(Comparator.comparingDouble(target -> target.distanceToSqr(position())));
        return targets;
    }
    private void pulse(ServerLevel level) {
        level.playSound(null, blockPosition(), SoundEvents.PLAYER_ATTACK_SWEEP, SoundSource.NEUTRAL, .8F, 4);
        var damage = new DamageSource(level.registryAccess().lookupOrThrow(Registries.DAMAGE_TYPE).getOrThrow(ModDamageTypes.THERM_IFRAME_BYPASS));
        for (Entity target : targets(level)) target.hurt(damage, 1.5F);
    }
    @Override protected void addAdditionalSaveData(net.minecraft.world.level.storage.ValueOutput output) {
        if (owner != null) output.putString("HiVoltOwner", owner.toString());
        output.putInt("HiVoltElapsed", elapsed); output.putBoolean("HiVoltElectric", electric); output.putBoolean("HiVoltArmed", armed);
    }
    @Override protected void readAdditionalSaveData(net.minecraft.world.level.storage.ValueInput input) {
        owner = input.getString("HiVoltOwner").map(java.util.UUID::fromString).orElse(null);
        electric = input.getBooleanOr("HiVoltElectric", false); elapsed = Math.clamp(input.getIntOr("HiVoltElapsed", 0), 0, electric ? 122 : 16);
        armed = owner != null && elapsed < (electric ? 122 : 16) && input.getBooleanOr("HiVoltArmed", false);
    }
}
