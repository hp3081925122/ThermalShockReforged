package com.hp.thermalshockreforged.entity.weaponcore;

import com.hp.thermalshockreforged.world.timing.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.*;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.*;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.*;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.*;
import software.bernie.geckolib.util.GeckoLibUtil;

public final class FlareGunCoreEntity extends PathfinderMob implements GeoEntity, ServerEndTickTask {
    private static final EntityDataAccessor<String> TEXTURE = SynchedEntityData.defineId(FlareGunCoreEntity.class, EntityDataSerializers.STRING);
    private static final EntityDataAccessor<Boolean> LANDED = SynchedEntityData.defineId(FlareGunCoreEntity.class, EntityDataSerializers.BOOLEAN);
    private final AnimatableInstanceCache animations = GeckoLibUtil.createInstanceCache(this);
    private int remaining;
    private boolean initialized;
    private boolean deathHandled;
    public FlareGunCoreEntity(EntityType<? extends FlareGunCoreEntity> type, Level level) {
        super(type, level);
        xpReward = 0;
        setCustomName(Component.translatable("entity.thermal_shock_reforged.flare_gun_core.prompt"));
        setCustomNameVisible(true);
    }
    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes().add(Attributes.MOVEMENT_SPEED, 0).add(Attributes.MAX_HEALTH, 1).add(Attributes.ARMOR, 0)
                .add(Attributes.ATTACK_DAMAGE, 0).add(Attributes.FOLLOW_RANGE, 16).add(Attributes.STEP_HEIGHT, 0);
    }
    @Override protected void defineSynchedData(SynchedEntityData.Builder builder) { super.defineSynchedData(builder); builder.define(TEXTURE, "flare_core"); builder.define(LANDED, false); }
    public String texture() { return entityData.get(TEXTURE); }
    public void setTexture(String texture) { entityData.set(TEXTURE, texture); }
    @Override protected void registerGoals() {
        goalSelector.addGoal(1, new MeleeAttackGoal(this, 1.2, false) { @Override protected boolean canPerformAttack(LivingEntity target) { return isTimeToAttack() && isWithinMeleeAttackRange(target); } });
        goalSelector.addGoal(2, new RandomStrollGoal(this, 1));
        targetSelector.addGoal(3, new HurtByTargetGoal(this));
        goalSelector.addGoal(4, new RandomLookAroundGoal(this));
        goalSelector.addGoal(5, new FloatGoal(this));
    }
    @Override public boolean isWithinMeleeAttackRange(LivingEntity target) { return distanceToSqr(target) <= getBbWidth() * getBbWidth() + target.getBbWidth(); }
    @Override public boolean hurt(DamageSource source, float amount) { return !source.is(DamageTypes.FALL) && super.hurt(source, amount); }
    @Override public boolean causeFallDamage(float distance, float multiplier, DamageSource source) {
        if (!level().isClientSide) entityData.set(LANDED, true);
        return super.causeFallDamage(distance, multiplier, source);
    }
    @Override protected SoundEvent getHurtSound(DamageSource source) { return SoundEvents.ANVIL_BREAK; }
    @Override protected SoundEvent getDeathSound() { return SoundEvents.ANVIL_BREAK; }
    @Override public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty, MobSpawnType reason, SpawnGroupData data) {
        var result = super.finalizeSpawn(level, difficulty, reason, data);
        initialized = true; remaining = 120;
        addEffect(new MobEffectInstance(MobEffects.GLOWING, 9000, 1, false, false));
        return result;
    }
    @Override public void onAddedToLevel() { super.onAddedToLevel(); if (initialized && level() instanceof ServerLevel server) ServerEndTickTasks.schedule(server, getUUID()); }
    @Override public void tick() {
        super.tick();
        if (initialized && !isRemoved() && level() instanceof ServerLevel server) ServerEndTickTasks.schedule(server, getUUID());
    }
    @Override public void endTick(ServerLevel level) { if (initialized && !isRemoved() && --remaining <= 0) discard(); }
    @Override public void die(DamageSource source) {
        super.die(source);
        if (!dead || deathHandled || !(level() instanceof ServerLevel server)) return;
        deathHandled = true;
        if (source.getEntity() == null) return;
        var origin = position();
        discard();
        CoreDetonation.submit(server, getUUID(), origin, source.getEntity());
    }
    @Override public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag); tag.putString("Texture", texture()); tag.putBoolean("FlareCoreLanded", entityData.get(LANDED));
        tag.putBoolean("FlareCoreInitialized", initialized); tag.putInt("FlareCoreRemaining", remaining); tag.putBoolean("FlareCoreDeathHandled", deathHandled);
    }
    @Override public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        if (tag.contains("Texture") && tag.getString("Texture").matches("[a-z0-9_./-]+")) setTexture(tag.getString("Texture"));
        entityData.set(LANDED, tag.getBoolean("FlareCoreLanded")); initialized = tag.getBoolean("FlareCoreInitialized");
        remaining = Math.clamp(tag.getInt("FlareCoreRemaining"), 0, 120); deathHandled = tag.getBoolean("FlareCoreDeathHandled");
    }
    @Override public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "movement", 4, state -> entityData.get(LANDED) ? PlayState.STOP : state.setAndContinue(RawAnimation.begin().thenLoop("0"))));
        controllers.add(new AnimationController<>(this, "procedure", 4, state -> entityData.get(LANDED) ? state.setAndContinue(RawAnimation.begin().thenPlayAndHold("1")) : PlayState.STOP));
    }
    @Override public AnimatableInstanceCache getAnimatableInstanceCache() { return animations; }
}
