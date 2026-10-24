package com.hp.thermalshockreforged.entity.sanctum;
import com.geckolib.animation.object.PlayState;
import com.hp.thermalshockreforged.compat.LegacyAnimationController;
import com.geckolib.animatable.manager.AnimatableManager;

import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.resources.Identifier;

import com.hp.thermalshockreforged.ThermalShockReforged;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
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

public final class RippleEntity extends Monster implements GeoEntity {
    private static final Identifier SQUALL = Identifier.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, "squall");
    private static final EntityDataAccessor<String> TEXTURE = SynchedEntityData.defineId(RippleEntity.class, EntityDataSerializers.STRING);
    private final AnimatableInstanceCache animations = GeckoLibUtil.createInstanceCache(this);
    private int cooldown;
    private int movementLock;
    private boolean deathCommitted;
    private boolean experiencePending;
    private UUID experienceAttacker;

    public RippleEntity(EntityType<? extends RippleEntity> type, Level level) { super(type, level); xpReward = 8; }
    public static AttributeSupplier.Builder createAttributes(boolean sanctum) {
        return Mob.createMobAttributes().add(Attributes.MAX_HEALTH, sanctum ? 35 : 40).add(Attributes.ARMOR, 6).add(Attributes.ATTACK_DAMAGE, 7)
                .add(Attributes.MOVEMENT_SPEED, 0.15).add(Attributes.FOLLOW_RANGE, sanctum ? 16 : 32).add(Attributes.ATTACK_KNOCKBACK, 0.8).add(Attributes.STEP_HEIGHT, 1.6);
    }
    private static final Identifier MOVEMENT_LOCK = Identifier.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, "ripple_movement_lock");
    public void beginAttack(LivingEntity victim) {
        if (!(level() instanceof ServerLevel server) || !isAlive() || victim == this || cooldown > 0 || victim.level() != level()) return;
        if (!RippleCastEntity.submit(server, this, victim)) {
            ThermalShockReforged.LOGGER.warn("Ripple cast could not be scheduled: caster={}, target={}", getUUID(), victim.getUUID());
            return;
        }
        cooldown = 180;
        movementLock = 40;
        syncMovementLock();
        setAnimation("attack");
        server.playSound(null, victim.blockPosition(), sound("galvanitehurt"), net.minecraft.sounds.SoundSource.HOSTILE, .8F, 3);
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
    @Override protected void defineSynchedData(SynchedEntityData.Builder builder) { super.defineSynchedData(builder); builder.define(TEXTURE, "ripple"); }
    public String texture() { return entityData.get(TEXTURE); }
    public void setTexture(String texture) { entityData.set(TEXTURE, texture); }
    public void setAnimation(String animation) {
        if (!"empty".equals(animation) && !"undefined".equals(animation)) triggerAnim("procedure", animation);
    }
    @Override protected EntityDimensions getDefaultDimensions(Pose pose) { return super.getDefaultDimensions(pose).withEyeHeight(0.8F); }
    @Override protected void registerGoals() {
        super.registerGoals();
        goalSelector.addGoal(1, new MeleeAttackGoal(this, 1.2, false) {
            @Override protected boolean canPerformAttack(LivingEntity target) {
                return isTimeToAttack() && distanceToSqr(target) <= 25;
            }
        });
        targetSelector.addGoal(2, new HurtByTargetGoal(this).setAlertOthers());
        goalSelector.addGoal(3, new RandomStrollGoal(this, 1));
        goalSelector.addGoal(4, new RandomLookAroundGoal(this));
        targetSelector.addGoal(5, new NearestAttackableTargetGoal<>(this, Player.class, true, false));
        goalSelector.addGoal(6, new AvoidEntityGoal<>(this, Monster.class, 32, 1, 1.2,
                entity -> SQUALL.equals(BuiltInRegistries.ENTITY_TYPE.getKey(entity.getType()))) {
            @Override public boolean canUse() { return BuiltInRegistries.ENTITY_TYPE.getOptional(SQUALL).isPresent() && super.canUse(); }
        });
    }
    @Override protected void playStepSound(BlockPos pos, BlockState state) { playSound(SoundEvents.CHAIN_PLACE, 0.15F, 1); }
    private SoundEvent sound(String path) { return BuiltInRegistries.SOUND_EVENT.getValue(Identifier.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, path)); }
    @Override protected SoundEvent getHurtSound(DamageSource source) { return sound("galvanitehurt"); }
    @Override protected SoundEvent getDeathSound() { return sound("galvanitedeath"); }
    @Override public boolean hurtServer(net.minecraft.server.level.ServerLevel level, DamageSource source, float amount) { return !source.is(DamageTypes.LIGHTNING_BOLT) && !source.is(DamageTypes.WITHER) && !source.is(DamageTypes.WITHER_SKULL) && super.hurtServer(level, source, amount); }
    @Override public void die(DamageSource source) {
        super.die(source);
        if (!dead || deathCommitted || !(level() instanceof ServerLevel server)) return;
        deathCommitted = true;
    }
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
        super.addAdditionalSaveData(output); output.putString("Texture", texture()); output.putInt("RippleCooldown", cooldown); output.putInt("RippleMovementLock", movementLock);
        output.putBoolean("RippleDeathCommitted", deathCommitted); output.putBoolean("RippleExperiencePending", experiencePending);
        if (experienceAttacker != null) output.putString("RippleExperienceAttacker", experienceAttacker.toString());
    }
    @Override public void readAdditionalSaveData(net.minecraft.world.level.storage.ValueInput input) {
        super.readAdditionalSaveData(input);
if (input.read("Texture", com.mojang.serialization.Codec.PASSTHROUGH).isPresent()) setTexture(input.getStringOr("Texture", ""));
        cooldown = Math.clamp(input.getIntOr("RippleCooldown", 0), 0, 180);
        movementLock = Math.clamp(input.getIntOr("RippleMovementLock", 0), 0, 40);
        if (!level().isClientSide()) syncMovementLock();
        deathCommitted = input.getBooleanOr("RippleDeathCommitted", false);
        experiencePending = input.getBooleanOr("RippleExperiencePending", false);
        experienceAttacker = input.getString("RippleExperienceAttacker").map(java.util.UUID::fromString).orElse(null);
    }
    @Override public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        var procedure = new LegacyAnimationController<>(this, "procedure", 4, state -> PlayState.STOP);
        for (String clip : java.util.List.of("attack", "idle", "walk", "melee")) procedure.triggerableAnim(clip, RawAnimation.begin().thenPlay(clip));
        controllers.add(new LegacyAnimationController<>(this, "movement", 4, state -> {
            if (procedure.isPlayingTriggeredAnimation()) return PlayState.STOP;
            if (state.isMoving() || state.getLimbSwingAmount() <= -.15F || state.getLimbSwingAmount() >= .15F) return state.setAndContinue(RawAnimation.begin().thenLoop("walk"));
            return state.setAndContinue(RawAnimation.begin().thenLoop("idle"));
        }));
        controllers.add(procedure);
    }
    @Override public AnimatableInstanceCache getAnimatableInstanceCache() { return animations; }
}
