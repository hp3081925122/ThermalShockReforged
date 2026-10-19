package com.hp.thermalshockreforged.entity.magnetite;

import com.hp.thermalshockreforged.config.CombatConfig;
import com.hp.thermalshockreforged.ThermalShockReforged;
import com.hp.thermalshockreforged.entity.base.AnimatedMonsterEntity;
import java.util.List;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.RangedAttackGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.level.Level;

public final class MagnetiteEntity extends AnimatedMonsterEntity implements RangedAttackMob {
    private boolean attributesApplied;
    private boolean loadedFromSave;

    public MagnetiteEntity(EntityType<? extends MagnetiteEntity> type, Level level) { super(type, level, "magnetite", 7); applyConfiguredAttributes(); }
    @Override public boolean isWithinMeleeAttackRange(LivingEntity target) { return distanceToSqr(target) <= getBbWidth() * getBbWidth() + target.getBbWidth(); }
    private boolean closeTarget() { return getTarget() != null && isWithinMeleeAttackRange(getTarget()); }
    @Override protected void registerGoals() {
        super.registerGoals();
        goalSelector.addGoal(1, new MeleeAttackGoal(this, 1.2, false) {
            @Override public boolean canUse() { return closeTarget() && super.canUse(); }
            @Override public boolean canContinueToUse() { return closeTarget() && super.canContinueToUse(); }
            @Override public void start() { super.start(); ThermalShockReforged.LOGGER.debug("Magnetite attack mode: entity={}, mode=melee", getUUID()); }
            @Override protected boolean canPerformAttack(LivingEntity target) { return isTimeToAttack() && isWithinMeleeAttackRange(target); }
        });
        targetSelector.addGoal(2, new HurtByTargetGoal(this));
        goalSelector.addGoal(3, new RandomStrollGoal(this, .8));
        goalSelector.addGoal(4, new AvoidEntityGoal<>(this, Player.class, 4, 1, 1.2));
        goalSelector.addGoal(5, new RandomLookAroundGoal(this));
        targetSelector.addGoal(6, new NearestAttackableTargetGoal<>(this, Player.class, false, false));
        int interval = CombatConfig.SPEC.isLoaded() ? CombatConfig.MAGNETITE_INTERVAL.get() : 80;
        goalSelector.addGoal(1, new RangedAttackGoal(this, 1.25, interval, 10) {
            @Override public boolean canUse() { return !closeTarget() && super.canUse(); }
            @Override public boolean canContinueToUse() { return canUse(); }
            @Override public void start() { super.start(); ThermalShockReforged.LOGGER.debug("Magnetite attack mode: entity={}, mode=ranged", getUUID()); }
        });
    }

    @Override public void performRangedAttack(LivingEntity target, float velocity) {
        MagnetiteBulletEntity.shoot(this, target, getAttributeValue(Attributes.ATTACK_DAMAGE) * CombatConfig.MAGNETITE_BULLET_MULTIPLIER.get(), CombatConfig.MAGNETITE_BULLET_KNOCKBACK.get());
    }

    @Override public boolean hurt(DamageSource source, float amount) {
        return !(source.getDirectEntity() instanceof AbstractArrow) && !source.is(DamageTypes.FALL) && !source.is(DamageTypes.DROWN) && super.hurt(source, amount);
    }
    @Override protected void tickEffects() {
        if (!com.hp.thermalshockreforged.content.remainingcombat.CryoCooldownVisual.tickClientEffects(
                this, false, (effect, refresh) -> onEffectUpdated(effect, refresh, null))) super.tickEffects();
    }
    @Override protected SoundEvent getAmbientSound() { return SoundEvents.BLAZE_AMBIENT; }
    @Override protected SoundEvent getHurtSound(DamageSource source) { return SoundEvents.BLAZE_HURT; }
    @Override protected SoundEvent getDeathSound() { return SoundEvents.BLAZE_DEATH; }
    @Override protected List<String> procedureAnimations() { return List.of("idle", "walk", "shield"); }
    @Override protected String movementAnimation(boolean moving) { return moving && !isAggressive() ? "walk" : "idle"; }

    private void applyConfiguredAttributes() {
        if (level().isClientSide || attributesApplied || !CombatConfig.SPEC.isLoaded()) return;
        float health = getHealth();
        getAttribute(Attributes.MAX_HEALTH).setBaseValue(CombatConfig.MAGNETITE_HEALTH.get());
        getAttribute(Attributes.ARMOR).setBaseValue(CombatConfig.MAGNETITE_ARMOR.get());
        getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(CombatConfig.MAGNETITE_DAMAGE.get());
        getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(CombatConfig.MAGNETITE_SPEED.get());
        getAttribute(Attributes.FOLLOW_RANGE).setBaseValue(CombatConfig.MAGNETITE_FOLLOW_RANGE.get());
        getAttribute(Attributes.KNOCKBACK_RESISTANCE).setBaseValue(CombatConfig.MAGNETITE_RESISTANCE.get());
        getAttribute(Attributes.ATTACK_KNOCKBACK).setBaseValue(CombatConfig.MAGNETITE_MELEE_KNOCKBACK.get());
        setHealth(loadedFromSave ? Math.min(health, getMaxHealth()) : getMaxHealth());
        attributesApplied = true;
    }
    @Override public void onAddedToLevel() { super.onAddedToLevel(); applyConfiguredAttributes(); }
    @Override public void readAdditionalSaveData(CompoundTag tag) {
        loadedFromSave = true;
        super.readAdditionalSaveData(tag);
        attributesApplied = false;
        applyConfiguredAttributes();
    }
    public static AttributeSupplier.Builder createAttributes() {
        return createMobAttributes().add(Attributes.MOVEMENT_SPEED, .3).add(Attributes.MAX_HEALTH, 35).add(Attributes.ARMOR, 6).add(Attributes.ATTACK_DAMAGE, 3).add(Attributes.FOLLOW_RANGE, 16).add(Attributes.KNOCKBACK_RESISTANCE, .1).add(Attributes.ATTACK_KNOCKBACK, .1).add(Attributes.STEP_HEIGHT, 1);
    }
}
