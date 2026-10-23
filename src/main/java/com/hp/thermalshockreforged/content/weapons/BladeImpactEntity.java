package com.hp.thermalshockreforged.content.weapons;

import com.hp.thermalshockreforged.ThermalShockReforged;
import com.hp.thermalshockreforged.content.ModDamageTypes;
import com.hp.thermalshockreforged.content.status.StatusContent;
import com.hp.thermalshockreforged.entity.cavebloomer.CavebloomerContent;
import com.hp.thermalshockreforged.world.timing.ServerEndTickTask;
import com.hp.thermalshockreforged.world.timing.ServerEndTickTasks;
import java.util.Comparator;
import java.util.UUID;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;

public final class BladeImpactEntity extends Entity implements ServerEndTickTask {
    private int remaining = 5;
    private UUID owner;
    private UUID victim;
    private boolean shock;
    private boolean armed;
    public BladeImpactEntity(EntityType<? extends BladeImpactEntity> type, Level level) { super(type, level); noPhysics = true; setNoGravity(true); }
    public static void submit(ServerLevel level, LivingEntity victim, LivingEntity attacker, boolean shock) {
        var task = WeaponContent.BLADE_IMPACT.get().create(level);
        if (task == null) return;
        task.owner = attacker.getUUID(); task.victim = victim.getUUID(); task.shock = shock; task.armed = true;
        task.setPos(victim.getX(), victim.getY(), victim.getZ());
        if (!level.addFreshEntity(task)) ThermalShockReforged.LOGGER.warn("Unable to submit delayed blade impact: victim={}, owner={}", task.victim, task.owner);
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
        armed = false; discard();
        if (shock) {
            if (level.getEntity(victim) instanceof LivingEntity target) target.addEffect(new MobEffectInstance(StatusContent.ELECTRIFIED, 240, 1, false, false));
            level.playSound(null, blockPosition(), BuiltInRegistries.SOUND_EVENT.getValue(Identifier.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, "sparkexplode")), SoundSource.NEUTRAL, .6F, 3);
            level.sendParticles(WeaponContent.BLUE_PLUS_BLAST.get(), getX(), getY(), getZ(), 1, .1, .1, .1, 0);
        } else {
            level.playSound(null, blockPosition(), SoundEvents.GENERIC_EXPLODE.value(), SoundSource.PLAYERS, 1, 1);
            level.sendParticles(CavebloomerContent.RED_EXPLODE.get(), getX(), getY(), getZ(), 5, 1, 1, 1, 0);
            var source = new DamageSource(level.registryAccess().lookupOrThrow(Registries.DAMAGE_TYPE).getOrThrow(ModDamageTypes.THERM_IFRAME_BYPASS), level.getEntity(owner));
            var nearby = level.getEntitiesOfClass(Entity.class, new AABB(position(), position()).inflate(3), other -> !other.getUUID().equals(owner) && other != this && !(other instanceof ItemEntity) && !(other instanceof TamableAnimal pet && pet.isTame()));
            nearby.sort(Comparator.comparingDouble(other -> other.distanceToSqr(position())));
            for (var target : nearby) target.hurt(source, 10);
        }
        ThermalShockReforged.LOGGER.debug("Delayed blade impact resolved: shock={}, victim={}, owner={}, position={}", shock, victim, owner, position());
    }
    @Override protected void addAdditionalSaveData(net.minecraft.world.level.storage.ValueOutput output) {
        output.putInt("BladeRemaining", remaining); output.putBoolean("BladeShock", shock); output.putBoolean("BladeArmed", armed);
        if (owner != null) output.putString("BladeOwner", owner.toString());
        if (victim != null) output.putString("BladeVictim", victim.toString());
    }
    @Override protected void readAdditionalSaveData(net.minecraft.world.level.storage.ValueInput input) {
        remaining = Math.clamp(input.getIntOr("BladeRemaining", 0), 1, 5); shock = input.getBooleanOr("BladeShock", false);
        owner = input.getString("BladeOwner").map(java.util.UUID::fromString).orElse(null); victim = input.getString("BladeVictim").map(java.util.UUID::fromString).orElse(null);
        armed = input.getBooleanOr("BladeArmed", false) && owner != null && victim != null;
        noPhysics = true; setNoGravity(true);
    }
}
