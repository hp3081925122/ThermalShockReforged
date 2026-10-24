package com.hp.thermalshockreforged.entity.sanctum;
import com.geckolib.animation.object.PlayState;
import com.hp.thermalshockreforged.compat.LegacyAnimationController;
import com.geckolib.animatable.manager.AnimatableManager;

import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.resources.Identifier;

import com.hp.thermalshockreforged.ThermalShockReforged;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.*;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.*;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import com.geckolib.animatable.GeoEntity;
import com.geckolib.animatable.instance.AnimatableInstanceCache;
import com.geckolib.animation.*;
import com.geckolib.util.GeckoLibUtil;

public final class FrostilyteGunnerEntity extends Monster implements GeoEntity {
    private static final EntityDataAccessor<String> TEXTURE = SynchedEntityData.defineId(FrostilyteGunnerEntity.class, EntityDataSerializers.STRING);
    private final AnimatableInstanceCache animations = GeckoLibUtil.createInstanceCache(this);
    private int cooldown;
    private int movementLock;
    private boolean experiencePending;
    private UUID experienceAttacker;

    public FrostilyteGunnerEntity(EntityType<? extends FrostilyteGunnerEntity> type, Level level) { super(type, level); xpReward = 10; setPersistenceRequired(); refreshDimensions(); }
    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes().add(Attributes.MAX_HEALTH, 32).add(Attributes.ARMOR, 8).add(Attributes.ATTACK_DAMAGE, 5)
                .add(Attributes.MOVEMENT_SPEED, .25).add(Attributes.FOLLOW_RANGE, 16).add(Attributes.ATTACK_KNOCKBACK, .2).add(Attributes.STEP_HEIGHT, .6);
    }
    private static final Identifier MOVEMENT_LOCK = Identifier.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, "gunner_movement_lock");
    public void beginAttack(LivingEntity victim) {
        if (!(level() instanceof ServerLevel server) || !isAlive() || victim == this || cooldown > 0 || victim.level() != level()) return;
        if (!GunnerVolleyEntity.submit(server, this, victim)) {
            ThermalShockReforged.LOGGER.warn("Frostilyte gunner volley could not be scheduled: caster={}, target={}", getUUID(), victim.getUUID());
            return;
        }
        cooldown = 60;
        movementLock = 29;
        syncMovementLock();
        setAnimation("attack");
        server.playSound(null, victim.blockPosition(), SoundEvents.STRAY_AMBIENT, net.minecraft.sounds.SoundSource.HOSTILE, 1, 3);
        lookAt(EntityAnchorArgument.Anchor.EYES, victim.getEyePosition());
    }
    @Override public void baseTick() {
        super.baseTick();
        if (level().isClientSide()) return;
        if (cooldown > 0) cooldown--;
        if (movementLock > 0) movementLock--;
        syncMovementLock();
    }
    private void syncMovementLock() {
        var speed = getAttribute(Attributes.MOVEMENT_SPEED);
        if (movementLock > 0 && !speed.hasModifier(MOVEMENT_LOCK)) speed.addTransientModifier(new AttributeModifier(MOVEMENT_LOCK, -1, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
        if (movementLock <= 0) speed.removeModifier(MOVEMENT_LOCK);
    }
    @Override protected void defineSynchedData(SynchedEntityData.Builder builder) { super.defineSynchedData(builder); builder.define(TEXTURE, "gunner_a"); }
    public String texture() { return entityData.get(TEXTURE); }
    public void setTexture(String texture) { entityData.set(TEXTURE, texture); }
    public void setAnimation(String animation) {
        if (!"empty".equals(animation) && !"undefined".equals(animation)) triggerAnim("procedure", animation);
    }
    @Override protected EntityDimensions getDefaultDimensions(Pose pose) { return super.getDefaultDimensions(pose).scale(1.1F); }
    @Override public boolean removeWhenFarAway(double distance) { return false; }
    @Override protected void registerGoals() {
        super.registerGoals();
        goalSelector.addGoal(1, new MeleeAttackGoal(this, 1.2, true) {
            @Override protected boolean canPerformAttack(LivingEntity target) { return isTimeToAttack() && distanceToSqr(target) <= 256; }
        });
        targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true, true));
        goalSelector.addGoal(3, new LookAtPlayerGoal(this, Player.class, 12));
        goalSelector.addGoal(4, new AvoidEntityGoal<>(this, Player.class, 3, 1, 1.2));
        targetSelector.addGoal(5, new HurtByTargetGoal(this).setAlertOthers());
        goalSelector.addGoal(6, new WaterAvoidingRandomStrollGoal(this, 0));
        goalSelector.addGoal(7, new RandomLookAroundGoal(this));
        goalSelector.addGoal(8, new OpenDoorGoal(this, true));
    }
    @Override public SoundEvent getAmbientSound() { return SoundEvents.STRAY_AMBIENT; }
    @Override protected void playStepSound(BlockPos pos, BlockState state) { playSound(SoundEvents.STRAY_STEP, .15F, 1); }
    @Override protected SoundEvent getHurtSound(DamageSource source) { return SoundEvents.STRAY_HURT; }
    @Override protected SoundEvent getDeathSound() { return SoundEvents.STRAY_DEATH; }
    @Override public boolean hurtServer(net.minecraft.server.level.ServerLevel level, DamageSource source, float amount) { return !source.is(DamageTypes.DROWN) && super.hurtServer(level, source, amount); }
    @Override protected void dropExperience(net.minecraft.server.level.ServerLevel level, Entity attacker) { experiencePending = true; experienceAttacker = attacker == null ? null : attacker.getUUID(); }
    @Override protected void tickDeath() {
        if (++deathTime >= 20 && !isRemoved()) {
            remove(RemovalReason.KILLED);
            if (experiencePending && level() instanceof ServerLevel server) {
                experiencePending = false;
                super.dropExperience(server, experienceAttacker == null ? null : server.getEntity(experienceAttacker));
            }
        }
    }
    @Override public void addAdditionalSaveData(net.minecraft.world.level.storage.ValueOutput output) {
        super.addAdditionalSaveData(output); output.putString("Texture", texture()); output.putInt("GunnerCooldown", cooldown); output.putInt("GunnerMovementLock", movementLock);
        output.putBoolean("GunnerExperiencePending", experiencePending);
        if (experienceAttacker != null) output.putString("GunnerExperienceAttacker", experienceAttacker.toString());
    }
    @Override public void readAdditionalSaveData(net.minecraft.world.level.storage.ValueInput input) {
        super.readAdditionalSaveData(input);
if (input.read("Texture", com.mojang.serialization.Codec.PASSTHROUGH).isPresent()) setTexture(input.getStringOr("Texture", ""));
        cooldown = Math.clamp(input.getIntOr("GunnerCooldown", 0), 0, 60);
        movementLock = Math.clamp(input.getIntOr("GunnerMovementLock", 0), 0, 29);
        if (!level().isClientSide()) syncMovementLock();
        experiencePending = input.getBooleanOr("GunnerExperiencePending", false);
        experienceAttacker = input.getString("GunnerExperienceAttacker").map(java.util.UUID::fromString).orElse(null);
    }
    @Override public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        var procedure = new LegacyAnimationController<>(this, "procedure", 4, state -> PlayState.STOP);
        for (String clip : java.util.List.of("attack", "idle", "walk", "aggro")) procedure.triggerableAnim(clip, RawAnimation.begin().thenPlay(clip));
        controllers.add(new LegacyAnimationController<>(this, "movement", 4, state -> {
            if (procedure.isPlayingTriggeredAnimation()) return PlayState.STOP;
            if ((state.isMoving() || state.getLimbSwingAmount() <= -.15F || state.getLimbSwingAmount() >= .15F) && !isAggressive()) return state.setAndContinue(RawAnimation.begin().thenLoop("walk"));
            if (isAggressive() && state.isMoving()) return state.setAndContinue(RawAnimation.begin().thenLoop("aggro"));
            return state.setAndContinue(RawAnimation.begin().thenLoop("idle"));
        }));
        controllers.add(procedure);
    }
    @Override public AnimatableInstanceCache getAnimatableInstanceCache() { return animations; }
}
