package com.hp.thermalshockreforged.entity.sopel;

import com.hp.thermalshockreforged.entity.base.AnimatedMonsterEntity;
import java.util.List;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.*;
import net.minecraft.sounds.*;
import net.minecraft.util.Mth;
import net.minecraft.world.BossEvent;
import net.minecraft.world.damagesource.*;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.*;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public final class HighDirectorSopelEntity extends AnimatedMonsterEntity {
    private final ServerBossEvent bossInfo = new ServerBossEvent(getUUID(), getDisplayName(), BossEvent.BossBarColor.PINK, BossEvent.BossBarOverlay.NOTCHED_6);
    private int cooldown;
    private boolean loaded;
    private boolean attributesApplied;
    public HighDirectorSopelEntity(EntityType<? extends HighDirectorSopelEntity> type, Level level) { super(type, level, "sopel", 10000); setPersistenceRequired(); }
    public static AttributeSupplier.Builder createAttributes() { return Mob.createMobAttributes().add(Attributes.MAX_HEALTH, 450).add(Attributes.ARMOR, 12).add(Attributes.ATTACK_DAMAGE, 3).add(Attributes.MOVEMENT_SPEED, 0).add(Attributes.FOLLOW_RANGE, 32).add(Attributes.KNOCKBACK_RESISTANCE, 1).add(Attributes.STEP_HEIGHT, 1); }
    @Override protected void registerGoals() {
        targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, Player.class, false, false));
        goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.2, false) { @Override protected boolean canPerformAttack(LivingEntity target) { return isTimeToAttack() && isWithinMeleeAttackRange(target); } });
    }
    @Override public boolean isWithinMeleeAttackRange(LivingEntity target) { return distanceToSqr(target) <= 256; }
    public void displace(LivingEntity victim, Entity owner) {
        if (!(victim.level() instanceof ServerLevel server)) return;
        if (owner instanceof HighDirectorSopelEntity sopel && sopel.cooldown > 0 || owner instanceof LivingEntity living && living.getPersistentData().getIntOr(SopelEntityContent.OWNER_COOLDOWN, 0) > 0) return;
        int delay = SopelConfig.COOLDOWN.get();
        if (owner instanceof HighDirectorSopelEntity sopel) sopel.cooldown = delay;
        else if (owner instanceof LivingEntity living) living.getPersistentData().putInt(SopelEntityContent.OWNER_COOLDOWN, delay);
        int direction = random.nextInt(4);
        server.playSound(null, victim.blockPosition(), BuiltInRegistries.SOUND_EVENT.getValue(SopelEntityContent.id("magnetcrossbowfire")), SoundSource.HOSTILE, 1, 3);
        if (direction != 1) {
            double x = victim.getX() + (direction == 2 ? -1 : 0);
            double z = victim.getZ() + (direction == 3 ? 1 : direction == 0 ? -1 : 0);
            if (owner instanceof ServerPlayer player) player.connection.teleport(x, victim.getY(), z, owner.getYRot(), owner.getXRot());
            else owner.teleportTo(x, victim.getY(), z);
        }
        double resistance = victim.getAttributeValue(Attributes.KNOCKBACK_RESISTANCE);
        double strength = SopelConfig.PUSH.get() * (1 - (Double.isFinite(resistance) ? Mth.clamp(resistance, 0, 1) : 1));
        if (Double.isFinite(strength) && strength > 0) {
            victim.setDeltaMovement(direction == 1 ? strength : direction == 2 ? -strength : 0, 0, direction == 3 ? strength : direction == 0 ? -strength : 0);
            victim.hurtMarked = true; victim.hurtMarked = true;
        }
    }
    private void applyConfiguration() {
        if (attributesApplied || level().isClientSide() || !SopelConfig.SPEC.isLoaded()) return;
        float previous = getHealth();
        getAttribute(Attributes.MAX_HEALTH).setBaseValue(SopelConfig.HEALTH.get()); getAttribute(Attributes.ARMOR).setBaseValue(SopelConfig.ARMOR.get()); getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(SopelConfig.DAMAGE.get()); getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(SopelConfig.SPEED.get()); getAttribute(Attributes.KNOCKBACK_RESISTANCE).setBaseValue(SopelConfig.RESISTANCE.get());
        setHealth(loaded ? Math.min(previous, getMaxHealth()) : getMaxHealth()); attributesApplied = true;
    }
    @Override public void onAddedToLevel() { super.onAddedToLevel(); applyConfiguration(); }
    @Override public void tick() { applyConfiguration(); super.tick(); }
    @Override protected void tickEffects() { super.tickEffects(); if (!level().isClientSide() && cooldown > 0) cooldown--; }
    @Override public boolean removeAllEffects() { boolean removed = super.removeAllEffects(); if (!level().isClientSide() && cooldown > 0) { cooldown = 0; return true; } return removed; }
    @Override public boolean removeWhenFarAway(double distance) { return false; }
    @Override public boolean canTeleport(Level from, Level to) { return false; }
    @Override public boolean hurtServer(net.minecraft.server.level.ServerLevel level, DamageSource source, float amount) { return !(source.is(DamageTypes.FALL) || source.is(DamageTypes.DROWN)) && super.hurtServer(level, source, amount); }
    @Override protected SoundEvent getHurtSound(DamageSource source) { return SoundEvents.GENERIC_HURT; }
    @Override protected SoundEvent getDeathSound() { return SoundEvents.GENERIC_DEATH; }
    @Override public void startSeenByPlayer(ServerPlayer player) { super.startSeenByPlayer(player); bossInfo.addPlayer(player); }
    @Override public void stopSeenByPlayer(ServerPlayer player) { super.stopSeenByPlayer(player); bossInfo.removePlayer(player); }
    @Override protected void customServerAiStep(net.minecraft.server.level.ServerLevel level) { super.customServerAiStep(level); bossInfo.setProgress(getHealth() / getMaxHealth()); }
    @Override protected int deathDuration() { return 60; }
    @Override protected String movementAnimation(boolean moving) { return "idle2"; }
    @Override protected List<String> procedureAnimations() { return List.of("idle", "idle2", "dash"); }
    @Override public void addAdditionalSaveData(net.minecraft.world.level.storage.ValueOutput output) { super.addAdditionalSaveData(output); output.putInt("SopelCooldown", cooldown); }
    @Override public void readAdditionalSaveData(net.minecraft.world.level.storage.ValueInput input) { loaded = true; super.readAdditionalSaveData(input); attributesApplied = false; cooldown = Math.clamp(input.getIntOr("SopelCooldown", 0), 0, 72000); }
}
