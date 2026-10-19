package com.hp.thermalshockreforged.entity.protoi;

import com.hp.thermalshockreforged.ThermalShockReforged;
import com.hp.thermalshockreforged.config.CombatConfig;
import com.hp.thermalshockreforged.world.timing.ServerEndTickTask;
import com.hp.thermalshockreforged.world.timing.ServerEndTickTasks;
import java.util.UUID;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.*;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.*;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import software.bernie.geckolib.animation.*;

public final class ProtoiEpsilonEntity extends ProtoiEntityBase implements ServerEndTickTask {
    private static final ResourceLocation DRIFT_SPEED = ResourceLocation.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, "epsilon_drift_speed");
    private int cooldown;
    private int driftRemaining;
    private boolean impulsePending;
    private boolean driftLockPending;
    private UUID attackTask;
    private boolean attributesApplied;
    private boolean loadedFromSave;
    private boolean experiencePending;
    private UUID experienceAttacker;

    public ProtoiEpsilonEntity(EntityType<? extends ProtoiEpsilonEntity> type, Level level) {
        super(type, level, "protoi_epsilon", true, 2304);
        xpReward = 23;
        setNoGravity(true);
        applyConfiguredAttributes();
    }
    public static AttributeSupplier.Builder createAttributes() {
        return attributes(.3, 20, 10, 3, 64).add(Attributes.KNOCKBACK_RESISTANCE, 1).add(Attributes.FLYING_SPEED, .3).add(Attributes.STEP_HEIGHT, 0);
    }
    private void applyConfiguredAttributes() {
        if (level().isClientSide || attributesApplied || !CombatConfig.SPEC.isLoaded()) return;
        float health = getHealth();
        getAttribute(Attributes.MAX_HEALTH).setBaseValue(CombatConfig.EPSILON_HEALTH.get());
        getAttribute(Attributes.ARMOR).setBaseValue(CombatConfig.EPSILON_ARMOR.get());
        getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(CombatConfig.EPSILON_DAMAGE.get());
        getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(CombatConfig.EPSILON_SPEED.get());
        getAttribute(Attributes.FLYING_SPEED).setBaseValue(CombatConfig.EPSILON_FLIGHT_SPEED.get());
        getAttribute(Attributes.FOLLOW_RANGE).setBaseValue(CombatConfig.EPSILON_FOLLOW_RANGE.get());
        getAttribute(Attributes.KNOCKBACK_RESISTANCE).setBaseValue(CombatConfig.EPSILON_RESISTANCE.get());
        setHealth(loadedFromSave ? Math.min(health, getMaxHealth()) : getMaxHealth());
        attributesApplied = true;
    }
    public void beginAttack(LivingEntity victim) {
        if (!(level() instanceof ServerLevel server) || victim == this || victim.level() != level() || cooldown > 0) return;
        UUID task = EpsilonAttackEntity.submit(server, this, victim);
        if (task == null) {
            ThermalShockReforged.LOGGER.warn("Protoi Epsilon attack submission failed: caster={}, target={}", getUUID(), victim.getUUID());
            return;
        }
        attackTask = task;
        cooldown = CombatConfig.EPSILON_COOLDOWN.get();
        lookAt(EntityAnchorArgument.Anchor.EYES, victim.getEyePosition());
    }
    void finishAttack(UUID task) { if (task.equals(attackTask)) attackTask = null; }
    @Override protected void registerGoals() {
        goalSelector.addGoal(1, new MeleeAttackGoal(this, 1.2, false) {
            @Override protected boolean canPerformAttack(LivingEntity target) { return isTimeToAttack() && distanceToSqr(target) <= 2304; }
        });
        targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, false, true));
        targetSelector.addGoal(3, new HurtByTargetGoal(this));
        goalSelector.addGoal(4, new RandomLookAroundGoal(this));
    }
    @Override protected PathNavigation createNavigation(Level level) { return new FlyingPathNavigation(this, level); }
    @Override protected boolean persistentByDefault() { return false; }
    @Override public boolean removeWhenFarAway(double distance) { return true; }
    @Override public boolean isWithinMeleeAttackRange(LivingEntity target) { return distanceToSqr(target) <= 2304; }
    @Override protected boolean ignoresDamage(DamageSource source) { return source.is(DamageTypes.FALL); }
    @Override public void setNoGravity(boolean ignored) { super.setNoGravity(true); }
    @Override protected void playStepSound(BlockPos pos, BlockState state) {
        var sound = state.getSoundType(level(), pos, this);
        playSound(sound.getStepSound(), sound.getVolume() * .15F, sound.getPitch());
    }
    private static SoundEvent sound(String id) { return BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, id)); }
    @Override public SoundEvent getAmbientSound() { return sound("delta_ambient"); }
    @Override protected SoundEvent getHurtSound(DamageSource source) { return sound("gamma_hurt"); }
    @Override protected SoundEvent getDeathSound() { return sound("gamma_hurt"); }
    @Override public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty, MobSpawnType reason, SpawnGroupData data) {
        SpawnGroupData result = super.finalizeSpawn(level, difficulty, reason, data);
        setNoGravity(true);
        teleportTo(getX(), getY() + 1, getZ());
        return result;
    }
    @Override public void baseTick() {
        super.baseTick();
        if (!(level() instanceof ServerLevel server)) return;
        if (cooldown > 0) cooldown--;
        if (driftRemaining > 0) driftRemaining--;
        if (driftRemaining == 0 && !hasEffect(MobEffects.MOVEMENT_SLOWDOWN)) impulsePending = true;
        syncDriftSpeed();
        server.playSound(null, blockPosition(), SoundEvents.BAMBOO_PLACE, SoundSource.HOSTILE, .1F, 3);
        if (impulsePending || driftLockPending) ServerEndTickTasks.schedule(server, getUUID());
    }
    @Override public void tick() {
        super.tick();
        if (level() instanceof ServerLevel server && attackTask != null && server.getEntity(attackTask) instanceof EpsilonAttackEntity task) task.capture(this);
    }
    private void syncDriftSpeed() {
        var speed = getAttribute(Attributes.MOVEMENT_SPEED);
        if (speed == null) return;
        double multiplier = 0;
        if (driftRemaining > 0) {
            var external = getEffect(MobEffects.MOVEMENT_SLOWDOWN);
            double externalFactor = external == null ? 1 : Math.max(0, 1 + -.15F * (double)(external.getAmplifier() + 1));
            if (externalFactor > 0) multiplier = Math.min(1, (1 + -.15F * 3.0) / externalFactor) - 1;
        }
        var previous = speed.getModifier(DRIFT_SPEED);
        if (previous != null && previous.amount() != multiplier) speed.removeModifier(DRIFT_SPEED);
        if (multiplier != 0 && !speed.hasModifier(DRIFT_SPEED)) speed.addTransientModifier(new AttributeModifier(DRIFT_SPEED, multiplier, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
    }
    @Override public void endTick(ServerLevel level) {
        if (isRemoved()) return;
        boolean applyImpulse = impulsePending;
        boolean applyLock = driftLockPending;
        impulsePending = false;
        driftLockPending = applyImpulse;
        if (applyLock) { driftRemaining = 40; syncDriftSpeed(); }
        if (applyImpulse) setDeltaMovement(Mth.nextDouble(getRandom(), -1, 1), Mth.nextDouble(getRandom(), -.2, .2), Mth.nextDouble(getRandom(), -1, 1));
        if (driftLockPending) ServerEndTickTasks.schedule(level, getUUID());
    }
    @Override public void onAddedToLevel() {
        super.onAddedToLevel(); applyConfiguredAttributes();
        if (level() instanceof ServerLevel server) {
            syncDriftSpeed();
            if (impulsePending || driftLockPending) ServerEndTickTasks.schedule(server, getUUID());
        }
    }
    @Override public void triggerProcedure(String animation) { if ("idle".equals(animation) || "ready".equals(animation)) triggerAnim("procedure", animation); }
    @Override public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        var procedure = new AnimationController<>(this, "procedure", 0, state -> PlayState.STOP).triggerableAnim("idle", RawAnimation.begin().thenPlay("idle")).triggerableAnim("ready", RawAnimation.begin().thenPlay("ready"));
        controllers.add(new AnimationController<>(this, "movement", 0, state -> procedure.isPlayingTriggeredAnimation() ? PlayState.STOP : state.setAndContinue(RawAnimation.begin().thenLoop("idle"))));
        controllers.add(procedure);
    }
    @Override protected void dropExperience(Entity attacker) { experiencePending = true; experienceAttacker = attacker == null ? null : attacker.getUUID(); }
    @Override protected void tickDeath() {
        if (++deathTime >= 20 && !isRemoved()) {
            remove(RemovalReason.KILLED);
            if (experiencePending && level() instanceof ServerLevel server) {
                experiencePending = false;
                super.dropExperience(experienceAttacker == null ? null : server.getEntity(experienceAttacker));
            }
        }
    }
    @Override public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putInt("EpsilonCooldown", cooldown); tag.putInt("EpsilonDriftRemaining", driftRemaining);
        tag.putBoolean("EpsilonImpulsePending", impulsePending); tag.putBoolean("EpsilonLockPending", driftLockPending);
        if (attackTask != null) tag.putUUID("EpsilonAttackTask", attackTask);
        tag.putBoolean("EpsilonExperiencePending", experiencePending);
        if (experienceAttacker != null) tag.putUUID("EpsilonExperienceAttacker", experienceAttacker);
    }
    @Override public void readAdditionalSaveData(CompoundTag tag) {
        loadedFromSave = true;
        super.readAdditionalSaveData(tag);
        attributesApplied = false; applyConfiguredAttributes();
        cooldown = Math.clamp(tag.getInt("EpsilonCooldown"), 0, 72000);
        driftRemaining = Math.clamp(tag.getInt("EpsilonDriftRemaining"), 0, 40);
        impulsePending = tag.getBoolean("EpsilonImpulsePending"); driftLockPending = tag.getBoolean("EpsilonLockPending");
        attackTask = tag.hasUUID("EpsilonAttackTask") ? tag.getUUID("EpsilonAttackTask") : null;
        experiencePending = tag.getBoolean("EpsilonExperiencePending");
        experienceAttacker = tag.hasUUID("EpsilonExperienceAttacker") ? tag.getUUID("EpsilonExperienceAttacker") : null;
        if (!level().isClientSide) syncDriftSpeed();
    }
}
