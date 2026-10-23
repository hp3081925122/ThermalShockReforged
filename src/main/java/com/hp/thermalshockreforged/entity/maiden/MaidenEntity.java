package com.hp.thermalshockreforged.entity.maiden;

import com.hp.thermalshockreforged.ThermalShockReforged;
import com.hp.thermalshockreforged.content.status.StatusContent;
import com.hp.thermalshockreforged.entity.base.AnimatedMonsterEntity;
import com.hp.thermalshockreforged.entity.attack.BossDeathEffectEntity;
import com.hp.thermalshockreforged.entity.mangler.ManglerContent;
import java.util.*;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.*;
import net.minecraft.world.damagesource.*;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.*;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public final class MaidenEntity extends AnimatedMonsterEntity {
    private static final net.minecraft.resources.Identifier MOVEMENT_LOCK = MaidenContent.id("maiden_movement_lock");
    private int cooldown;
    private int movementLock;
    private boolean submitting;
    private boolean deathVisual;
    private boolean configured;
    private boolean loaded;
    private UUID activeTask;
    public MaidenEntity(EntityType<? extends MaidenEntity> type, Level level) { super(type, level, "maiden", 50); }
    public static AttributeSupplier.Builder createAttributes() { return Mob.createMobAttributes().add(Attributes.MAX_HEALTH, 50).add(Attributes.ARMOR, 6).add(Attributes.ATTACK_DAMAGE, 10).add(Attributes.MOVEMENT_SPEED, .3).add(Attributes.FOLLOW_RANGE, 16).add(Attributes.KNOCKBACK_RESISTANCE, 1).add(Attributes.ATTACK_KNOCKBACK, 1).add(Attributes.STEP_HEIGHT, 1); }
    @Override protected void registerGoals() {
        goalSelector.addGoal(1, new MeleeAttackGoal(this, 1.2, false) { @Override protected boolean canPerformAttack(LivingEntity target) { return isTimeToAttack() && isWithinMeleeAttackRange(target); } }); targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, false, true)); goalSelector.addGoal(3, new RandomStrollGoal(this, 1)); targetSelector.addGoal(4, new HurtByTargetGoal(this)); goalSelector.addGoal(5, new FloatGoal(this));
    }
    @Override public boolean isWithinMeleeAttackRange(LivingEntity target) { return distanceToSqr(target) <= 256; }
    public boolean canBeginAttack() { return cooldown <= 0 && !submitting && isAlive(); }
    @Override public boolean doHurtTarget(net.minecraft.server.level.ServerLevel level, Entity target) { if (target instanceof LivingEntity living) beginAttack(living); return false; }
    public void beginAttack(LivingEntity victim) {
        if (!(level() instanceof ServerLevel server) || !canBeginAttack() || victim == this || !victim.isAlive() || victim.level() != level()) return;
        applyConfiguration(); boolean single = random.nextDouble() < MaidenConfig.SINGLE_CHANCE.get(); MaidenVolleyEntity task;
        submitting = true; try { task = MaidenVolleyEntity.submit(server, this, victim, single); } finally { submitting = false; }
        if (task == null) { ThermalShockReforged.LOGGER.debug("Maiden volley insertion failed: caster={}, target={}", getUUID(), victim.getUUID()); return; }
        activeTask = task.getUUID(); cooldown = MaidenConfig.COOLDOWN.get(); movementLock = single ? 44 : 56; synchronizeMovementLock(); getNavigation().stop(); triggerProcedure(single ? "attack1" : "attack2");
        ThermalShockReforged.LOGGER.debug("Maiden volley committed: caster={}, target={}, single={}", getUUID(), victim.getUUID(), single);
    }
    private void synchronizeMovementLock() { var speed = getAttribute(Attributes.MOVEMENT_SPEED); if (movementLock > 0) { if (!speed.hasModifier(MOVEMENT_LOCK)) speed.addTransientModifier(new AttributeModifier(MOVEMENT_LOCK, -1, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)); } else speed.removeModifier(MOVEMENT_LOCK); }
    private void applyConfiguration() {
        if (configured || level().isClientSide() || !MaidenConfig.SPEC.isLoaded()) return; float health = getHealth();
        getAttribute(Attributes.MAX_HEALTH).setBaseValue(MaidenConfig.HEALTH.get()); getAttribute(Attributes.ARMOR).setBaseValue(MaidenConfig.ARMOR.get()); getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(MaidenConfig.DAMAGE.get()); getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(MaidenConfig.SPEED.get()); getAttribute(Attributes.KNOCKBACK_RESISTANCE).setBaseValue(MaidenConfig.RESISTANCE.get()); setHealth(loaded ? Math.min(health, getMaxHealth()) : getMaxHealth()); configured = true;
    }
    @Override public void onAddedToLevel() { super.onAddedToLevel(); applyConfiguration(); if (!level().isClientSide()) synchronizeMovementLock(); }
    @Override public void tick() { if (!level().isClientSide()) { applyConfiguration(); if (cooldown > 0) cooldown--; if (movementLock > 0) movementLock--; synchronizeMovementLock(); } super.tick(); }
    @Override public void baseTick() {
        super.baseTick();
        if (hasEffect(StatusContent.STUNNED)) {
            if (level() instanceof ServerLevel server) server.sendParticles(ParticleTypes.ENCHANTED_HIT, getX(), getY() + 3.5, getZ(), 1, .001, .001, .001, 0);
            if (level().isClientSide()) level().playLocalSound(getX(), getY(), getZ(), SoundEvents.DISPENSER_FAIL, SoundSource.NEUTRAL, .2F, 5, false); else level().playSound(null, blockPosition(), SoundEvents.DISPENSER_FAIL, SoundSource.NEUTRAL, .2F, 5);
        }
    }
    @Override public boolean hurtServer(net.minecraft.server.level.ServerLevel level, DamageSource source, float amount) { return !(source.is(DamageTypes.IN_FIRE) || source.is(DamageTypes.FALL) || source.is(DamageTypes.CACTUS) || source.is(DamageTypes.DROWN) || source.is(DamageTypes.EXPLOSION)) && super.hurtServer(level, source, amount); }
    @Override protected SoundEvent getHurtSound(DamageSource source) { return SoundEvents.IRON_GOLEM_HURT; }
    @Override protected SoundEvent getDeathSound() { return SoundEvents.IRON_GOLEM_DEATH; }
    @Override public void die(DamageSource source) { super.die(source); if (dead && !deathVisual && level() instanceof ServerLevel server) { deathVisual = true; BossDeathEffectEntity.submit(server, ManglerContent.HERALD_DEATH_EFFECT.get(), position(), getEyeHeight()); } }
    @Override public void remove(RemovalReason reason) { if (activeTask != null && level() instanceof ServerLevel server && server.getEntity(activeTask) instanceof MaidenVolleyEntity task) task.captureCaster(this); super.remove(reason); }
    @Override protected int deathDuration() { return 60; }
    @Override protected List<String> procedureAnimations() { return List.of("attack1", "attack2"); }
    @Override public void addAdditionalSaveData(net.minecraft.world.level.storage.ValueOutput output) { super.addAdditionalSaveData(output); output.putInt("MaidenCooldown", cooldown); output.putInt("MaidenMovementLock", movementLock); output.putBoolean("MaidenDeathVisual", deathVisual); if (activeTask != null) output.putString("MaidenActiveTask", activeTask.toString()); }
    @Override public void readAdditionalSaveData(net.minecraft.world.level.storage.ValueInput input) { super.readAdditionalSaveData(input); loaded = true; configured = false; cooldown = Math.clamp(input.getIntOr("MaidenCooldown", 0), 0, 72000); movementLock = Math.clamp(input.getIntOr("MaidenMovementLock", 0), 0, 56); deathVisual = input.getBooleanOr("MaidenDeathVisual", false); activeTask = input.getString("MaidenActiveTask").map(java.util.UUID::fromString).orElse(null); }
}
