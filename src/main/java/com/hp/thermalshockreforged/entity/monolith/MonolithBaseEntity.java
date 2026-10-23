package com.hp.thermalshockreforged.entity.monolith;

import com.hp.thermalshockreforged.entity.base.AnimatedMonsterEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.damagesource.*;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.*;
import net.minecraft.world.level.Level;

public abstract class MonolithBaseEntity extends AnimatedMonsterEntity {
    private boolean attributesApplied;
    private boolean loadedFromSave;
    protected int conversionRetry;
    protected boolean converting;
    protected boolean converted;
    protected MonolithBaseEntity(EntityType<? extends MonolithBaseEntity> type, Level level, String texture) { super(type, level, texture, 30); setPersistenceRequired(); refreshDimensions(); }
    protected final boolean dormant() { return this instanceof DormantMonolithEntity; }
    public static AttributeSupplier.Builder createAttributes(boolean dormant) { return Mob.createMobAttributes().add(Attributes.MAX_HEALTH, 160).add(Attributes.ARMOR, 16).add(Attributes.ATTACK_DAMAGE, dormant ? 12 : 14).add(Attributes.MOVEMENT_SPEED, dormant ? 0 : .3).add(Attributes.FOLLOW_RANGE, 128).add(Attributes.KNOCKBACK_RESISTANCE, 1).add(Attributes.ATTACK_KNOCKBACK, .6).add(Attributes.STEP_HEIGHT, 2); }
    @Override protected EntityDimensions getDefaultDimensions(Pose pose) { return super.getDefaultDimensions(pose).scale(1.7F); }
    @Override public boolean removeWhenFarAway(double distance) { return false; }
    @Override public boolean hurtServer(net.minecraft.server.level.ServerLevel level, DamageSource source, float amount) { return !(source.is(DamageTypes.IN_FIRE) || source.is(DamageTypes.FALL) || source.is(DamageTypes.CACTUS) || source.is(DamageTypes.DROWN)) && super.hurtServer(level, source, amount); }
    private void applyConfiguredAttributes() {
        if (level().isClientSide() || attributesApplied || !MonolithConfig.SPEC.isLoaded()) return;
        int index = dormant() ? 1 : 0;
        float previous = getHealth();
        getAttribute(Attributes.MAX_HEALTH).setBaseValue(MonolithConfig.HEALTH[index].get());
        getAttribute(Attributes.ARMOR).setBaseValue(MonolithConfig.ARMOR[index].get());
        getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(MonolithConfig.DAMAGE[index].get());
        getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(MonolithConfig.SPEED[index].get());
        getAttribute(Attributes.KNOCKBACK_RESISTANCE).setBaseValue(MonolithConfig.RESISTANCE[index].get());
        getAttribute(Attributes.ATTACK_KNOCKBACK).setBaseValue(MonolithConfig.KNOCKBACK[index].get());
        setHealth(loadedFromSave ? Math.min(previous, getMaxHealth()) : getMaxHealth());
        attributesApplied = true;
    }
    @Override public void onAddedToLevel() { super.onAddedToLevel(); applyConfiguredAttributes(); }
    @Override public void tick() { if (!level().isClientSide()) { applyConfiguredAttributes(); if (conversionRetry > 0) conversionRetry--; } super.tick(); }
    @Override public void addAdditionalSaveData(net.minecraft.world.level.storage.ValueOutput output) { super.addAdditionalSaveData(output); output.putInt("MonolithConversionRetry", conversionRetry); output.putBoolean("MonolithConverted", converted); }
    @Override public void readAdditionalSaveData(net.minecraft.world.level.storage.ValueInput input) { loadedFromSave = true; super.readAdditionalSaveData(input); attributesApplied = false; conversionRetry = Math.clamp(input.getIntOr("MonolithConversionRetry", 0), 0, 20); converted = input.getBooleanOr("MonolithConverted", false); applyConfiguredAttributes(); setPersistenceRequired(); }
}
