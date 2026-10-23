package com.hp.thermalshockreforged.entity.monolith;
import com.geckolib.animation.object.PlayState;
import com.hp.thermalshockreforged.compat.LegacyAnimationController;
import com.geckolib.animatable.manager.AnimatableManager;

import com.hp.thermalshockreforged.ThermalShockReforged;
import com.hp.thermalshockreforged.entity.attack.BossDeathEffectEntity;
import com.hp.thermalshockreforged.entity.mangler.ManglerContent;
import java.util.*;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.*;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.*;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.*;
import net.neoforged.neoforge.event.EventHooks;
import com.geckolib.animation.*;

public final class MonolithEntity extends MonolithBaseEntity {
    private static final net.minecraft.resources.Identifier ATTACK_SLOW = MonolithContent.id("monolith_attack_slow");
    private int cooldown;
    private int slowTicks;
    private boolean submitting;
    private boolean initialized;
    private boolean initializationPending;
    private int initializationRetry;
    private boolean deathVisual;
    private Vec3 initialOrigin = Vec3.ZERO;
    private UUID activeAttack;
    public MonolithEntity(EntityType<? extends MonolithEntity> type, Level level) { super(type, level, "monolith25"); }
    @Override protected void registerGoals() {
        goalSelector.addGoal(1, new MeleeAttackGoal(this, 1.2, false) { @Override protected boolean canPerformAttack(LivingEntity target) { return isTimeToAttack() && isWithinMeleeAttackRange(target); } });
        targetSelector.addGoal(2, new HurtByTargetGoal(this));
        goalSelector.addGoal(3, new RandomStrollGoal(this, .8));
        goalSelector.addGoal(4, new RandomLookAroundGoal(this));
        targetSelector.addGoal(5, new NearestAttackableTargetGoal<>(this, Player.class, true, true));
    }
    @Override public boolean isWithinMeleeAttackRange(LivingEntity target) { return distanceToSqr(target) <= 16; }
    @Override public boolean doHurtTarget(Entity victim) { if (victim instanceof LivingEntity living) beginAttack(living); return false; }
    public void beginAttack(LivingEntity victim) {
        if (!(level() instanceof ServerLevel server) || !isAlive() || converted || converting || submitting || cooldown > 0 || !victim.isAlive() || victim == this || victim.level() != level()) return;
        MonolithTaskEntity task;
        submitting = true;
        try { task = MonolithTaskEntity.submitAttack(server, this, victim); }
        finally { submitting = false; }
        if (task == null) { ThermalShockReforged.LOGGER.debug("Monolith attack insertion failed: caster={}, target={}", getUUID(), victim.getUUID()); return; }
        activeAttack = task.getUUID();
        cooldown = MonolithConfig.ATTACK_COOLDOWN.get();
        slowTicks = 40;
        synchronizeSlow();
        triggerProcedure("attack");
        server.playSound(null, victim.blockPosition(), sound("monolithambient"), SoundSource.HOSTILE, 1, 5);
        ThermalShockReforged.LOGGER.debug("Monolith attack committed: caster={}, target={}, cooldown={}", getUUID(), victim.getUUID(), cooldown);
    }
    private void synchronizeSlow() {
        if (level().isClientSide()) return;
        var speed = getAttribute(Attributes.MOVEMENT_SPEED);
        var actualSlow = getEffect(MobEffects.SLOWNESS);
        double amount = actualSlow == null ? -.3 : actualSlow.getAmplifier() == 0 ? .7 / .85 - 1 : 0;
        var previous = speed.getModifier(ATTACK_SLOW);
        if (slowTicks <= 0 || amount == 0) speed.removeModifier(ATTACK_SLOW);
        else if (previous == null || previous.amount() != amount) { speed.removeModifier(ATTACK_SLOW); speed.addTransientModifier(new AttributeModifier(ATTACK_SLOW, amount, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)); }
    }
    @Override public SpawnGroupData finalizeSpawn(ServerLevelAccessor world, DifficultyInstance difficulty, EntitySpawnReason reason, SpawnGroupData data) {
        var result = super.finalizeSpawn(world, difficulty, reason, data);
        if (!initialized && world instanceof ServerLevel) { initialized = true; initializationPending = true; initialOrigin = position(); }
        return result;
    }
    @Override public void onAddedToLevel() { super.onAddedToLevel(); synchronizeSlow(); submitInitialization(); }
    private void submitInitialization() {
        if (!(level() instanceof ServerLevel server) || !initializationPending || submitting || initializationRetry > 0) return;
        boolean success;
        submitting = true;
        try { success = MonolithTaskEntity.submitInitialization(server, initialOrigin); }
        finally { submitting = false; }
        if (success) { initializationPending = false; server.playSound(null, BlockPos.containing(initialOrigin), sound("monolithhurt"), SoundSource.NEUTRAL, .5F, 2); }
        else { initializationRetry = 20; ThermalShockReforged.LOGGER.debug("Monolith initialization insertion failed: caster={}, position={}", getUUID(), initialOrigin); }
    }
    @Override public void tick() {
        if (!level().isClientSide()) { if (cooldown > 0) cooldown--; if (slowTicks > 0) slowTicks--; if (initializationRetry > 0) initializationRetry--; synchronizeSlow(); }
        super.tick();
        if (!level().isClientSide()) { synchronizeSlow(); submitInitialization(); }
    }
    @Override public void baseTick() {
        super.baseTick();
        if (!(level() instanceof ServerLevel server) || !isAlive() || converting || converted || conversionRetry > 0) return;
        var playerBounds = AABB.ofSize(position(), 180, 180, 180);
        if (server.players().stream().anyMatch(player -> !player.isRemoved() && player.getBoundingBox().intersects(playerBounds))) return;
        converting = true;
        try {
            var replacement = MonolithContent.DORMANT.get().create(server, net.minecraft.world.entity.EntitySpawnReason.MOB_SUMMONED);
            if (replacement == null) { conversionRetry = 20; return; }
            var pos = blockPosition();
            replacement.moveTo(pos.getX() + .5, pos.getY(), pos.getZ() + .5, getYRot(), 0);
            replacement.yBodyRot = getYRot(); replacement.yHeadRot = getYRot(); replacement.setDeltaMovement(Vec3.ZERO);
            EventHooks.finalizeMobSpawn(replacement, server, server.getCurrentDifficultyAt(pos), EntitySpawnReason.MOB_SUMMONED, null);
            if (replacement.isRemoved() || replacement.isSpawnCancelled() || !server.addFreshEntity(replacement)) { replacement.discard(); conversionRetry = 20; ThermalShockReforged.LOGGER.debug("Monolith sleep conversion rejected: source={}, position={}", getUUID(), pos); return; }
            converted = true;
            if (activeAttack != null && server.getEntity(activeAttack) instanceof MonolithTaskEntity task) task.captureCaster(this);
            replacement.playAmbientSound();
            ThermalShockReforged.LOGGER.debug("Monolith became dormant: source={}, replacement={}", getUUID(), replacement.getUUID());
            discard();
        } finally { converting = false; }
    }
    static SoundEvent sound(String name) { return BuiltInRegistries.SOUND_EVENT.getValue(MonolithContent.id(name)); }
    @Override public SoundEvent getAmbientSound() { return sound("monolithambient"); }
    @Override protected SoundEvent getHurtSound(DamageSource source) { return sound("monolithhurt"); }
    @Override protected SoundEvent getDeathSound() { return sound("monolithdeath"); }
    @Override protected void playStepSound(BlockPos pos, BlockState state) { playSound(sound("stalkerstep"), .15F, 1); }
    @Override public void die(DamageSource source) { super.die(source); if (isDeadOrDying() && !deathVisual && level() instanceof ServerLevel server) { deathVisual = true; BossDeathEffectEntity.submit(server, ManglerContent.HERALD_DEATH_EFFECT.get(), position(), getEyeHeight()); } }
    @Override protected int deathDuration() { return 60; }
    @Override protected int animationTransitionTicks() { return 1; }
    @Override protected List<String> procedureAnimations() { return List.of("attack"); }
    @Override public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        var procedure = new LegacyAnimationController<>(this, "procedure", 1, state -> PlayState.STOP).triggerableAnim("attack", RawAnimation.begin().thenPlay("attack"));
        controllers.add(new LegacyAnimationController<>(this, "movement", 1, state -> {
            if (procedure.isPlayingTriggeredAnimation()) return PlayState.STOP;
            if (state.isMoving() || state.getLimbSwingAmount() <= -.15F || state.getLimbSwingAmount() >= .15F) return state.setAndContinue(RawAnimation.begin().thenLoop("walk"));
            if (isDeadOrDying()) return state.setAndContinue(RawAnimation.begin().thenPlay("death"));
            return state.setAndContinue(RawAnimation.begin().thenLoop("idle"));
        }));
        controllers.add(procedure);
    }
    @Override public void addAdditionalSaveData(net.minecraft.world.level.storage.ValueOutput output) {
        super.addAdditionalSaveData(output); output.putInt("MonolithCooldown", cooldown); output.putInt("MonolithSlowTicks", slowTicks); output.putBoolean("MonolithInitialized", initialized); output.putBoolean("MonolithInitializationPending", initializationPending); output.putInt("MonolithInitializationRetry", initializationRetry); output.putBoolean("MonolithDeathVisual", deathVisual); output.putDouble("MonolithInitialX", initialOrigin.x); output.putDouble("MonolithInitialY", initialOrigin.y); output.putDouble("MonolithInitialZ", initialOrigin.z); if (activeAttack != null) output.putString("MonolithActiveAttack", activeAttack.toString());
    }
    @Override public void readAdditionalSaveData(net.minecraft.world.level.storage.ValueInput input) {
        super.readAdditionalSaveData(input); cooldown = Math.clamp(input.getIntOr("MonolithCooldown", 0), 0, 72000); slowTicks = Math.clamp(input.getIntOr("MonolithSlowTicks", 0), 0, 40); initialized = !input.getString("MonolithInitialized").isPresent() || input.getBooleanOr("MonolithInitialized", false); initializationPending = input.getBooleanOr("MonolithInitializationPending", false); initializationRetry = Math.clamp(input.getIntOr("MonolithInitializationRetry", 0), 0, 20); deathVisual = input.getBooleanOr("MonolithDeathVisual", false); initialOrigin = new Vec3(input.getDoubleOr("MonolithInitialX", 0.0), input.getDoubleOr("MonolithInitialY", 0.0), input.getDoubleOr("MonolithInitialZ", 0.0)); if (!Double.isFinite(initialOrigin.x) || !Double.isFinite(initialOrigin.y) || !Double.isFinite(initialOrigin.z)) initializationPending = false; activeAttack = input.getString("MonolithActiveAttack").map(java.util.UUID::fromString).orElse(null);
        var legacySlow = getEffect(MobEffects.SLOWNESS);
        if (!input.getString("MonolithSlowTicks").isPresent() && legacySlow != null && legacySlow.getAmplifier() == 1 && legacySlow.getDuration() <= 40 && legacySlow.isAmbient() && !legacySlow.isVisible()) { slowTicks = Math.max(slowTicks, Math.max(0, legacySlow.getDuration())); removeEffect(MobEffects.SLOWNESS); }
        synchronizeSlow();
    }
}
