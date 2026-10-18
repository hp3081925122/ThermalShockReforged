package com.hp.thermalshock.entity;

import com.hp.thermalshock.config.CombatConfig;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.phys.Vec3;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

public final class ForecastEntity extends Monster implements GeoEntity {
    private static final EntityDataAccessor<String> TEXTURE = SynchedEntityData.defineId(ForecastEntity.class, EntityDataSerializers.STRING);
    private static final RawAnimation IDLE = RawAnimation.begin().thenLoop("idle");
    private final AnimatableInstanceCache animations = GeckoLibUtil.createInstanceCache(this);
    private int driftCooldown;
    private int attackCooldown;

    public ForecastEntity(EntityType<? extends ForecastEntity> type, Level level) {
        super(type, level);
        xpReward = 8;
        setNoGravity(true);
        getAttribute(Attributes.MAX_HEALTH).setBaseValue(CombatConfig.FORECAST_HEALTH.get());
        getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(CombatConfig.FORECAST_DAMAGE.get());
        setHealth(getMaxHealth());
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes().add(Attributes.MAX_HEALTH, 36).add(Attributes.MOVEMENT_SPEED, 0.3).add(Attributes.ARMOR, 6).add(Attributes.ATTACK_DAMAGE, 10).add(Attributes.FOLLOW_RANGE, 16);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(TEXTURE, "forecast");
    }

    public String texture() {
        return entityData.get(TEXTURE);
    }

    @Override
    protected void registerGoals() {
        goalSelector.addGoal(1, new MeleeAttackGoal(this, 1.2, false) {
            @Override
            protected boolean canPerformAttack(net.minecraft.world.entity.LivingEntity target) {
                return isTimeToAttack() && distanceToSqr(target) <= 100.0 && getSensing().hasLineOfSight(target);
            }
        });
        targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, false, true));
        targetSelector.addGoal(3, new HurtByTargetGoal(this));
        goalSelector.addGoal(4, new RandomLookAroundGoal(this));
    }

    @Override
    public boolean doHurtTarget(Entity target) {
        if (level().isClientSide || attackCooldown > 0 || !target.isAlive() || target.level() != level()) return false;
        ZapWarningEntity zap = ModEntities.ZAP.get().create(level());
        if (zap == null) return false;
        zap.moveTo(target.getX(), target.getY(), target.getZ(), random.nextFloat() * 360, 0);
        zap.arm(this, CombatConfig.FORECAST_ZAP_MULTIPLIER.get());
        if (!level().addFreshEntity(zap)) return false;
        attackCooldown = CombatConfig.FORECAST_COOLDOWN.get();
        triggerAnim("combat", "attack");
        level().playSound(null, target.blockPosition(), SoundEvents.BEACON_ACTIVATE, getSoundSource(), 1, 3);
        return true;
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (level().isClientSide || !isAlive() || isNoAi()) return;
        if (attackCooldown > 0) attackCooldown--;
        if (driftCooldown-- <= 0) {
            setDeltaMovement(new Vec3(random.nextDouble() * 2 - 1, random.nextDouble() * 0.4 - 0.2, random.nextDouble() * 2 - 1));
            driftCooldown = 41;
            hasImpulse = true;
        }
        var target = getTarget();
        if (target != null && target.isAlive() && distanceToSqr(target) <= 100) getLookControl().setLookAt(target);
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        return !source.is(DamageTypes.FALL) && !source.is(DamageTypes.LIGHTNING_BOLT) && super.hurt(source, amount);
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.GENERIC_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.GENERIC_DEATH;
    }

    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty, MobSpawnType reason, SpawnGroupData data) {
        SpawnGroupData result = super.finalizeSpawn(level, difficulty, reason, data);
        setPos(getX(), getY() + 1, getZ());
        return result;
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putString("Texture", texture());
        tag.putInt("ZapCooldown", attackCooldown);
        tag.putInt("DriftCooldown", driftCooldown);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        if (tag.contains("Texture")) entityData.set(TEXTURE, tag.getString("Texture"));
        attackCooldown = Math.max(0, tag.getInt("ZapCooldown"));
        driftCooldown = Math.max(0, tag.getInt("DriftCooldown"));
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "combat", 4, state -> state.setAndContinue(IDLE)).triggerableAnim("attack", RawAnimation.begin().thenPlay("attack2")));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return animations;
    }
}
