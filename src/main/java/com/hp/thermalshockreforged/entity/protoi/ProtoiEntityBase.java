package com.hp.thermalshockreforged.entity.protoi;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.FlyingMoveControl;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.*;
import software.bernie.geckolib.util.GeckoLibUtil;

public abstract class ProtoiEntityBase extends PathfinderMob implements GeoEntity {
    private final AnimatableInstanceCache animations = GeckoLibUtil.createInstanceCache(this);
    private static final EntityDataAccessor<String> TEXTURE = SynchedEntityData.defineId(ProtoiEntityBase.class, EntityDataSerializers.STRING);
    private final boolean flying;
    private final double attackReach;
    private String procedure = "empty";

    protected ProtoiEntityBase(EntityType<? extends ProtoiEntityBase> type, Level level, String texture, boolean flying, double attackReach) {
        super(type, level);
        entityData.set(TEXTURE, texture);
        this.flying = flying;
        this.attackReach = attackReach;
        if (persistentByDefault()) setPersistenceRequired();
        if (flying) moveControl = new FlyingMoveControl(this, 10, true);
    }

    protected void registerGoals() {
        super.registerGoals();
        goalSelector.addGoal(1, new MeleeAttackGoal(this, 1.2, false) {
            @Override protected boolean canPerformAttack(LivingEntity target) { return isTimeToAttack() && distanceToSqr(target) <= attackReach; }
        });
        targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, false, true));
        targetSelector.addGoal(3, new HurtByTargetGoal(this));
        goalSelector.addGoal(4, new RandomStrollGoal(this, 1.0));
        goalSelector.addGoal(5, new RandomLookAroundGoal(this));
    }

    public String texture() { return entityData.get(TEXTURE); }
    public void setTexture(String texture) { entityData.set(TEXTURE, texture); }
    public void triggerProcedure(String animation) { if (animation != null && !animation.isBlank() && !animation.equals("empty")) procedure = animation; }
    public boolean flyingProtoi() { return flying; }
    protected boolean persistentByDefault() { return true; }
    protected boolean ignoresDamage(DamageSource source) { return source.is(DamageTypes.IN_FIRE) || source.is(DamageTypes.FALL) || source.is(DamageTypes.DROWN); }
    @Override public boolean removeWhenFarAway(double distance) { return false; }
    @Override public boolean causeFallDamage(float distance, float multiplier, DamageSource source) { return false; }
    @Override public boolean hurt(DamageSource source, float amount) { return !ignoresDamage(source) && super.hurt(source, amount); }
    @Override protected void playStepSound(BlockPos pos, net.minecraft.world.level.block.state.BlockState state) { }
    @Override protected SoundEvent getAmbientSound() { return null; }
    @Override protected void defineSynchedData(SynchedEntityData.Builder builder) { super.defineSynchedData(builder); builder.define(TEXTURE, ""); }
    @Override public void addAdditionalSaveData(CompoundTag tag) { super.addAdditionalSaveData(tag); tag.putString("Texture", texture()); tag.putString("Procedure", procedure); }
    @Override public void readAdditionalSaveData(CompoundTag tag) { super.readAdditionalSaveData(tag); if (tag.contains("Texture")) setTexture(tag.getString("Texture")); procedure = tag.contains("Procedure") ? tag.getString("Procedure") : "empty"; }
    @Override public void aiStep() { super.aiStep(); if (flying) setNoGravity(true); updateSwingTime(); }
    @Override public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "movement", 4, state -> {
            if (!procedure.equals("empty")) return PlayState.STOP;
            return state.setAndContinue(RawAnimation.begin().thenLoop(flying ? "idle" : (state.isMoving() ? "walk" : "idle")));
        }));
        controllers.add(new AnimationController<>(this, "procedure", 4, state -> procedure.equals("empty") ? PlayState.STOP : state.setAndContinue(RawAnimation.begin().thenPlay(procedure))));
    }
    @Override public AnimatableInstanceCache getAnimatableInstanceCache() { return animations; }
    public static AttributeSupplier.Builder attributes(double speed, double health, double armor, double damage, double follow) {
        return Mob.createMobAttributes().add(Attributes.MOVEMENT_SPEED, speed).add(Attributes.MAX_HEALTH, health).add(Attributes.ARMOR, armor).add(Attributes.ATTACK_DAMAGE, damage).add(Attributes.FOLLOW_RANGE, follow);
    }
}
