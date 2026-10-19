package com.hp.thermalshockreforged.entity.huntress;

import com.hp.thermalshockreforged.ThermalShockReforged;
import com.hp.thermalshockreforged.entity.base.AnimatedMonsterEntity;
import java.util.List;
import java.util.UUID;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.*;
import net.minecraft.sounds.*;
import net.minecraft.world.BossEvent;
import net.minecraft.world.damagesource.*;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.*;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public final class HuntressBossEntity extends AnimatedMonsterEntity {
    private static final net.minecraft.resources.ResourceLocation MOVEMENT_LOCK = HuntressContent.id("huntress_movement_lock");
    private final ServerBossEvent bossInfo = new ServerBossEvent(getDisplayName(), BossEvent.BossBarColor.PINK, BossEvent.BossBarOverlay.PROGRESS);
    private int cooldown;
    private int movementLock;
    private int gravityFailsafe;
    private boolean attributesApplied;
    private boolean loaded;
    private boolean submitting;
    private UUID activeTask;
    public HuntressBossEntity(EntityType<? extends HuntressBossEntity> type, Level level) { super(type, level, "huntress", 700); }
    public static AttributeSupplier.Builder createAttributes() { return Mob.createMobAttributes().add(Attributes.MAX_HEALTH, 300).add(Attributes.ARMOR, 8).add(Attributes.ATTACK_DAMAGE, 8).add(Attributes.MOVEMENT_SPEED, .35).add(Attributes.FOLLOW_RANGE, 32).add(Attributes.KNOCKBACK_RESISTANCE, 1).add(Attributes.ATTACK_KNOCKBACK, 1).add(Attributes.STEP_HEIGHT, .6); }
    @Override protected void registerGoals() {
        goalSelector.addGoal(1, new MeleeAttackGoal(this, 1.75, false) { @Override protected boolean canPerformAttack(LivingEntity target) { return isTimeToAttack() && isWithinMeleeAttackRange(target); } });
        targetSelector.addGoal(2, new HurtByTargetGoal(this)); goalSelector.addGoal(3, new RandomLookAroundGoal(this)); targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(this, Player.class, false, false));
    }
    @Override public boolean isWithinMeleeAttackRange(LivingEntity target) { return distanceToSqr(target) <= 81; }
    @Override public boolean doHurtTarget(Entity target) { if (target instanceof LivingEntity living) beginAttack(living); return false; }
    public void beginAttack(LivingEntity victim) {
        if (!(level() instanceof ServerLevel server) || !isAlive() || cooldown > 0 || submitting || victim == this || !victim.isAlive() || victim.level() != level()) return;
        applyConfiguration();
        if (distanceTo(victim) <= 3) {
            cooldown = HuntressConfig.ROLLBACK_COOLDOWN.get(); triggerProcedure("rollback");
            var look = getLookAngle(); setDeltaMovement(-2 * look.x, 1, -2 * look.z); hasImpulse = true;
            return;
        }
        int skill = random.nextDouble() < HuntressConfig.TRIPLE_CHANCE.get() ? 1 : 2;
        HuntressTaskEntity task; submitting = true;
        try { task = HuntressTaskEntity.submit(server, this, victim, skill); } finally { submitting = false; }
        if (task == null) { ThermalShockReforged.LOGGER.debug("Huntress attack insertion failed: caster={}, target={}, skill={}", getUUID(), victim.getUUID(), skill); return; }
        activeTask = task.getUUID();
        cooldown = skill == 1 ? HuntressConfig.TRIPLE_COOLDOWN.get() : HuntressConfig.SLAM_COOLDOWN.get(); movementLock = skill == 1 ? 20 : 40;
        if (skill == 2) { setNoGravity(true); gravityFailsafe = 35; }
        synchronizeMovementLock(); getNavigation().stop(); triggerProcedure(skill == 1 ? "triplehit" : "dissa");
        ThermalShockReforged.LOGGER.debug("Huntress attack committed: caster={}, target={}, skill={}", getUUID(), victim.getUUID(), skill);
    }
    void releaseGravity() { gravityFailsafe = 0; setNoGravity(false); }
    private void synchronizeMovementLock() {
        var speed = getAttribute(Attributes.MOVEMENT_SPEED);
        if (movementLock > 0) { if (!speed.hasModifier(MOVEMENT_LOCK)) speed.addTransientModifier(new AttributeModifier(MOVEMENT_LOCK, -1, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)); } else speed.removeModifier(MOVEMENT_LOCK);
    }
    private void applyConfiguration() {
        if (attributesApplied || !HuntressConfig.SPEC.isLoaded() || level().isClientSide) return;
        float previous = getHealth();
        getAttribute(Attributes.MAX_HEALTH).setBaseValue(HuntressConfig.HEALTH.get()); getAttribute(Attributes.ARMOR).setBaseValue(HuntressConfig.ARMOR.get()); getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(HuntressConfig.DAMAGE.get()); getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(HuntressConfig.SPEED.get()); getAttribute(Attributes.KNOCKBACK_RESISTANCE).setBaseValue(HuntressConfig.RESISTANCE.get());
        setHealth(loaded ? Math.min(previous, getMaxHealth()) : getMaxHealth()); attributesApplied = true;
    }
    @Override public void onAddedToLevel() { super.onAddedToLevel(); applyConfiguration(); if (!level().isClientSide) synchronizeMovementLock(); }
    @Override public void tick() { if (!level().isClientSide) { applyConfiguration(); if (cooldown > 0) cooldown--; if (movementLock > 0) movementLock--; if (gravityFailsafe > 0 && --gravityFailsafe == 0) setNoGravity(false); synchronizeMovementLock(); } super.tick(); }
    @Override public boolean canChangeDimensions(Level from, Level to) { return false; }
    @Override public void remove(RemovalReason reason) { if (activeTask != null && level() instanceof ServerLevel server && server.getEntity(activeTask) instanceof HuntressTaskEntity task) task.captureCaster(this); super.remove(reason); }
    @Override public boolean hurt(DamageSource source, float amount) { return !(source.is(DamageTypes.FALL) || source.is(DamageTypes.CACTUS) || source.is(DamageTypes.DROWN)) && super.hurt(source, amount); }
    @Override protected SoundEvent getHurtSound(DamageSource source) { return SoundEvents.GENERIC_HURT; }
    @Override protected SoundEvent getDeathSound() { return SoundEvents.GENERIC_DEATH; }
    @Override public void startSeenByPlayer(ServerPlayer player) { super.startSeenByPlayer(player); bossInfo.addPlayer(player); }
    @Override public void stopSeenByPlayer(ServerPlayer player) { super.stopSeenByPlayer(player); bossInfo.removePlayer(player); }
    @Override protected void customServerAiStep() { super.customServerAiStep(); bossInfo.setProgress(getHealth() / getMaxHealth()); }
    @Override protected List<String> procedureAnimations() { return List.of("rollback", "triplehit", "dissa", "reappear2"); }
    @Override protected String movementAnimation(boolean moving) { return !onGround() ? "reappear" : moving ? "sprint" : "idle2"; }
    @Override public void addAdditionalSaveData(CompoundTag tag) { super.addAdditionalSaveData(tag); tag.putInt("HuntressCooldown", cooldown); tag.putInt("HuntressMovementLock", movementLock); tag.putInt("HuntressGravityFailsafe", gravityFailsafe); if (activeTask != null) tag.putUUID("HuntressActiveTask", activeTask); }
    @Override public void readAdditionalSaveData(CompoundTag tag) { super.readAdditionalSaveData(tag); loaded = true; attributesApplied = false; cooldown = Math.clamp(tag.getInt("HuntressCooldown"), 0, 72000); movementLock = Math.clamp(tag.getInt("HuntressMovementLock"), 0, 40); gravityFailsafe = Math.clamp(tag.getInt("HuntressGravityFailsafe"), 0, 35); activeTask = tag.hasUUID("HuntressActiveTask") ? tag.getUUID("HuntressActiveTask") : null; }
}
