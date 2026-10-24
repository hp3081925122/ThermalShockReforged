package com.hp.thermalshockreforged.entity.qo;
import com.hp.thermalshockreforged.compat.LegacyAnimationController;
import com.geckolib.animatable.manager.AnimatableManager;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.*;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.*;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.*;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.*;
import net.minecraft.world.entity.ai.control.FlyingMoveControl;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.navigation.*;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import com.geckolib.animatable.GeoEntity;
import com.geckolib.animatable.instance.AnimatableInstanceCache;
import com.geckolib.animation.*;
import com.geckolib.util.GeckoLibUtil;

public final class ThermoidEntity extends Monster implements GeoEntity {
    private static final EntityDataAccessor<String> TEXTURE = SynchedEntityData.defineId(ThermoidEntity.class, EntityDataSerializers.STRING);
    private final AnimatableInstanceCache animations = GeckoLibUtil.createInstanceCache(this);
    private int remainingTicks;
    public ThermoidEntity(EntityType<? extends ThermoidEntity> type, Level level) {
        super(type, level); xpReward = 0; setCustomName(Component.translatable("entity.thermal_shock_reforged.thermoid.display_name")); setCustomNameVisible(true); setPersistenceRequired(); setNoGravity(true); moveControl = new FlyingMoveControl(this, 10, true);
    }
    public static AttributeSupplier.Builder createAttributes() { return Mob.createMobAttributes().add(Attributes.MOVEMENT_SPEED, .3).add(Attributes.MAX_HEALTH, 75).add(Attributes.ARMOR, 100).add(Attributes.ATTACK_DAMAGE, 10).add(Attributes.FOLLOW_RANGE, 64).add(Attributes.KNOCKBACK_RESISTANCE, 1).add(Attributes.ATTACK_KNOCKBACK, 2).add(Attributes.FLYING_SPEED, .3).add(Attributes.STEP_HEIGHT, .6); }
    @Override protected void defineSynchedData(SynchedEntityData.Builder builder) { super.defineSynchedData(builder); builder.define(TEXTURE, "thermoid"); }
    public String texture() { return entityData.get(TEXTURE); }
    @Override protected PathNavigation createNavigation(Level level) { return new FlyingPathNavigation(this, level); }
    @Override protected void registerGoals() {
        targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, Monster.class, false, true));
        goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.5, false) { @Override protected boolean canPerformAttack(LivingEntity target) { return isTimeToAttack() && distanceToSqr(target) <= getBbWidth() * getBbWidth() + target.getBbWidth(); } });
        goalSelector.addGoal(3, new RandomStrollGoal(this, .8, 20) { @Override protected Vec3 getPosition() { return position().add((random.nextFloat() * 2 - 1) * 16, (random.nextFloat() * 2 - 1) * 16, (random.nextFloat() * 2 - 1) * 16); } });
        goalSelector.addGoal(4, new RandomLookAroundGoal(this));
    }
    @Override public SpawnGroupData finalizeSpawn(ServerLevelAccessor world, DifficultyInstance difficulty, EntitySpawnReason reason, SpawnGroupData group) {
        var result = super.finalizeSpawn(world, difficulty, reason, group); remainingTicks = 320;
        if (world instanceof ServerLevel server) server.sendParticles(ParticleTypes.POOF, getX(), getY(), getZ(), 5, .1, .1, .1, .1);
        return result;
    }
    @Override public void baseTick() {
        super.baseTick();
        if (level() instanceof ServerLevel server && --remainingTicks <= 0) { discard(); server.sendParticles(ParticleTypes.POOF, getX(), getY(), getZ(), 5, .001, .001, .001, 0); }
    }
    @Override public boolean hurtServer(net.minecraft.server.level.ServerLevel level, DamageSource source, float amount) { return !(source.getDirectEntity() instanceof Player) && !DefectorMachinistEntity.ignores(source) && super.hurtServer(level, source, amount); }
    @Override public boolean removeWhenFarAway(double distance) { return false; }
    @Override public boolean isPushable() { return false; }
    @Override protected void doPush(Entity other) { }
    @Override protected void pushEntities() { }
    @Override public void setNoGravity(boolean ignored) { super.setNoGravity(true); }
    @Override public void aiStep() { super.aiStep(); setNoGravity(true); }
    @Override public boolean causeFallDamage(double distance, float multiplier, DamageSource source) { return false; }
    @Override protected void checkFallDamage(double distance, boolean onGround, BlockState state, BlockPos pos) { }
    @Override protected SoundEvent getHurtSound(DamageSource source) { return SoundEvents.GENERIC_HURT; }
    @Override protected SoundEvent getDeathSound() { return SoundEvents.GENERIC_DEATH; }
    @Override protected void tickDeath() { if (++deathTime >= 20) remove(RemovalReason.KILLED); }
    @Override public void addAdditionalSaveData(net.minecraft.world.level.storage.ValueOutput output) { super.addAdditionalSaveData(output); output.putString("Texture", texture()); output.putInt("ThermoidRemainingTicks", remainingTicks); }
    @Override public void readAdditionalSaveData(net.minecraft.world.level.storage.ValueInput input) { super.readAdditionalSaveData(input); if (input.read("Texture", com.mojang.serialization.Codec.PASSTHROUGH).isPresent()) entityData.set(TEXTURE, input.getStringOr("Texture", "")); remainingTicks = Math.clamp(input.getIntOr("ThermoidRemainingTicks", 0), 0, 320); setPersistenceRequired(); setNoGravity(true); }
    @Override public void registerControllers(AnimatableManager.ControllerRegistrar controllers) { controllers.add(new LegacyAnimationController<>(this, "movement", 4, state -> state.setAndContinue(RawAnimation.begin().thenLoop("idle")))); }
    @Override public AnimatableInstanceCache getAnimatableInstanceCache() { return animations; }
}
