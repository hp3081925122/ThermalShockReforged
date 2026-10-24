package com.hp.thermalshockreforged.entity.sanctum;
import com.geckolib.animation.object.PlayState;
import com.hp.thermalshockreforged.compat.LegacyAnimationController;
import com.geckolib.animatable.manager.AnimatableManager;

import com.hp.thermalshockreforged.ThermalShockReforged;
import com.hp.thermalshockreforged.content.status.StatusContent;
import java.util.UUID;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.*;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.*;
import net.minecraft.sounds.*;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.*;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.*;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.*;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import com.geckolib.animatable.GeoEntity;
import com.geckolib.animatable.instance.AnimatableInstanceCache;
import com.geckolib.animation.*;
import com.geckolib.util.GeckoLibUtil;

public final class ErrantEntity extends Monster implements GeoEntity, com.hp.thermalshockreforged.world.timing.ServerEndTickTask {
    private static final EntityDataAccessor<String> TEXTURE = SynchedEntityData.defineId(ErrantEntity.class, EntityDataSerializers.STRING);
    private static final Identifier ADVANCEMENT = Identifier.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, "errant_get");
    private final AnimatableInstanceCache animations = GeckoLibUtil.createInstanceCache(this);
    private final ErrantCombat combat = new ErrantCombat(this);
    private boolean attributesApplied;
    private boolean loadedFromSave;
    private boolean deathCommitted;
    private boolean experiencePending;
    private UUID experienceAttacker;

    public ErrantEntity(EntityType<? extends ErrantEntity> type, Level level) {
        super(type, level);
        xpReward = 30;
        if (sanctum()) setPersistenceRequired();
        applyConfiguredAttributes();
    }
    public boolean sanctum() { return BuiltInRegistries.ENTITY_TYPE.getKey(getType()).getPath().equals("sanctum_errant"); }
    public static AttributeSupplier.Builder createAttributes(boolean sanctum) {
        return Mob.createMobAttributes().add(Attributes.MOVEMENT_SPEED, .3).add(Attributes.MAX_HEALTH, sanctum ? 40 : 62).add(Attributes.ARMOR, 6)
                .add(Attributes.ATTACK_DAMAGE, 6).add(Attributes.FOLLOW_RANGE, sanctum ? 12 : 32).add(Attributes.KNOCKBACK_RESISTANCE, .3)
                .add(Attributes.ATTACK_KNOCKBACK, 1).add(Attributes.STEP_HEIGHT, 1);
    }
    private void applyConfiguredAttributes() {
        if (level().isClientSide() || attributesApplied || !SanctumConfig.SPEC.isLoaded()) return;
        float previousHealth = getHealth();
        var values = sanctum() ? SanctumConfig.SANCTUM_ERRANT : SanctumConfig.ERRANT;
        getAttribute(Attributes.MAX_HEALTH).setBaseValue(values.health().get());
        getAttribute(Attributes.ARMOR).setBaseValue(values.armor().get());
        getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(values.damage().get());
        getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(values.speed().get());
        getAttribute(Attributes.KNOCKBACK_RESISTANCE).setBaseValue(values.resistance().get());
        getAttribute(Attributes.ATTACK_KNOCKBACK).setBaseValue(values.knockback().get());
        setHealth(loadedFromSave ? Math.min(previousHealth, getMaxHealth()) : getMaxHealth());
        attributesApplied = true;
        ThermalShockReforged.LOGGER.debug("Sanctum errant attributes applied: entity={}, loaded={}, health={}, maxHealth={}, attackDamage={}", getUUID(), loadedFromSave, getHealth(), getMaxHealth(), getAttributeValue(Attributes.ATTACK_DAMAGE));
    }
    @Override public void onAddedToLevel() { super.onAddedToLevel(); applyConfiguredAttributes(); if (level() instanceof ServerLevel server) combat.schedule(server); }
    public void beginAttack(LivingEntity victim) { combat.begin(victim); }
    @Override public void baseTick() { super.baseTick(); combat.baseTick(); }
    @Override public void tick() { super.tick(); if (level() instanceof ServerLevel server) combat.schedule(server); }
    @Override public void endTick(ServerLevel level) { combat.endTick(level); }
    @Override protected void defineSynchedData(SynchedEntityData.Builder builder) { super.defineSynchedData(builder); builder.define(TEXTURE, "dweller"); }
    public String texture() { return entityData.get(TEXTURE); }
    public void setTexture(String texture) { entityData.set(TEXTURE, texture); }
    public void setAnimation(String animation) {
        if (!"empty".equals(animation) && !"undefined".equals(animation)) triggerAnim("procedure", animation);
    }
    @Override protected void registerGoals() {
        super.registerGoals();
        goalSelector.addGoal(1, new MeleeAttackGoal(this, 1, false) {
            @Override protected boolean canPerformAttack(LivingEntity target) { return isTimeToAttack() && distanceToSqr(target) <= 9; }
        });
        targetSelector.addGoal(2, new HurtByTargetGoal(this).setAlertOthers());
        goalSelector.addGoal(3, new RandomStrollGoal(this, 1));
        goalSelector.addGoal(4, new RandomLookAroundGoal(this));
        targetSelector.addGoal(5, new NearestAttackableTargetGoal<>(this, Player.class, true, false));
    }
    @Override public boolean removeWhenFarAway(double distance) { return sanctum() ? false : super.removeWhenFarAway(distance); }
    @Override protected SoundEvent getHurtSound(DamageSource source) { return SoundEvents.GENERIC_HURT; }
    @Override protected SoundEvent getDeathSound() { return SoundEvents.GENERIC_HURT; }
    @Override public boolean hurtServer(net.minecraft.server.level.ServerLevel level, DamageSource source, float amount) {
        if (hasEffect(StatusContent.PARRYING) && level() instanceof ServerLevel server) {
            addEffect(new MobEffectInstance(MobEffects.INSTANT_HEALTH, 1, 0, true, false));
            server.playSound(null, blockPosition(), SoundEvents.ENCHANTMENT_TABLE_USE, SoundSource.NEUTRAL, .8F, 3);
        }
        return super.hurtServer(level, source, amount);
    }
    @Override public void die(DamageSource source) {
        super.die(source);
        if (!dead || deathCommitted || !(level() instanceof ServerLevel server)) return;
        deathCommitted = true;
        if (source.getEntity() instanceof ServerPlayer player) {
            var advancement = server.getServer().getAdvancements().get(ADVANCEMENT);
            if (advancement == null) { ThermalShockReforged.LOGGER.warn("Sanctum errant advancement is missing: {}", ADVANCEMENT); return; }
            var progress = player.getAdvancements().getOrStartProgress(advancement);
            if (!progress.isDone()) for (String criterion : progress.getRemainingCriteria()) player.getAdvancements().award(advancement, criterion);
        }
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
        super.addAdditionalSaveData(output);
        output.putString("Texture", texture());
        combat.save(output);
        output.putBoolean("SanctumErrantDeathCommitted", deathCommitted);
        output.putBoolean("SanctumErrantExperiencePending", experiencePending);
        if (experienceAttacker != null) output.putString("SanctumErrantExperienceAttacker", experienceAttacker.toString());
    }
    @Override public void readAdditionalSaveData(net.minecraft.world.level.storage.ValueInput input) {
        loadedFromSave = true;
        attributesApplied = false;
        super.readAdditionalSaveData(input);
if (input.read("Texture", com.mojang.serialization.Codec.PASSTHROUGH).isPresent()) setTexture(input.getStringOr("Texture", ""));
        deathCommitted = input.getBooleanOr("SanctumErrantDeathCommitted", false);
        experiencePending = input.getBooleanOr("SanctumErrantExperiencePending", false);
        experienceAttacker = input.getString("SanctumErrantExperienceAttacker").map(java.util.UUID::fromString).orElse(null);
        applyConfiguredAttributes();
        combat.load(input);
    }
    @Override public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        var procedure = new LegacyAnimationController<>(this, "procedure", 2, state -> PlayState.STOP);
        for (String clip : java.util.List.of("idle", "walk", "aggroidle", "aggro", "attack", "block", "hold", "dodge")) procedure.triggerableAnim(clip, RawAnimation.begin().thenPlay(clip));
        controllers.add(new LegacyAnimationController<>(this, "movement", 2, state -> {
            if (procedure.isPlayingTriggeredAnimation()) return PlayState.STOP;
            if ((state.isMoving() || state.getLimbSwingAmount() <= -.15F || state.getLimbSwingAmount() >= .15F) && !isAggressive()) return state.setAndContinue(RawAnimation.begin().thenLoop("walk"));
            if (isShiftKeyDown()) return state.setAndContinue(RawAnimation.begin().thenLoop("idle"));
            if (isAggressive() && state.isMoving()) return state.setAndContinue(RawAnimation.begin().thenLoop("aggro"));
            return state.setAndContinue(RawAnimation.begin().thenLoop("aggroidle"));
        }));
        controllers.add(procedure);
    }
    @Override public AnimatableInstanceCache getAnimatableInstanceCache() { return animations; }
}
