package com.hp.thermalshockreforged.entity.squall;

import com.hp.thermalshockreforged.ThermalShockReforged;
import com.hp.thermalshockreforged.world.timing.ServerEndTickTask;
import com.hp.thermalshockreforged.world.timing.ServerEndTickTasks;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.*;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.*;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ThrownPotion;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public final class SquallEntity extends Monster implements ServerEndTickTask {
    private static final Vec3 ATTACK_STUCK = new Vec3(.25, .05, .25);
    private int remainingPulses;
    private boolean attributesApplied;
    private boolean loadedFromSave;
    private boolean deathAwarded;
    public SquallEntity(EntityType<? extends SquallEntity> type, Level level) { super(type, level); xpReward = 0; }
    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes().add(Attributes.MAX_HEALTH, 554).add(Attributes.ARMOR, 0).add(Attributes.ATTACK_DAMAGE, 10).add(Attributes.MOVEMENT_SPEED, 0).add(Attributes.FOLLOW_RANGE, 64).add(Attributes.KNOCKBACK_RESISTANCE, 1).add(Attributes.ATTACK_KNOCKBACK, .6).add(Attributes.STEP_HEIGHT, 5);
    }
    @Override protected void registerGoals() {
        super.registerGoals();
        goalSelector.addGoal(1, new MeleeAttackGoal(this, 1.2, false) { @Override protected boolean canPerformAttack(LivingEntity target) { return isTimeToAttack() && isWithinMeleeAttackRange(target); } });
        goalSelector.addGoal(2, new RandomLookAroundGoal(this));
        targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, Player.class, false, false));
    }
    @Override public boolean isWithinMeleeAttackRange(LivingEntity target) { return distanceToSqr(target) <= 36; }
    @Override public Vec3 getVehicleAttachmentPoint(Entity vehicle) { return new Vec3(0, .35, 0); }
    @Override public boolean ignoreExplosion(Explosion explosion) { return true; }
    @Override public boolean isPushedByFluid() { return false; }
    @Override public boolean hurt(DamageSource source, float amount) {
        if (source.is(DamageTypes.IN_FIRE) || source.getDirectEntity() instanceof ThrownPotion || source.getDirectEntity() instanceof AreaEffectCloud || source.is(DamageTypes.FALL) || source.is(DamageTypes.CACTUS) || source.is(DamageTypes.DROWN) || source.is(DamageTypes.LIGHTNING_BOLT) || source.is(DamageTypes.EXPLOSION) || source.is(DamageTypes.PLAYER_EXPLOSION) || source.is(DamageTypes.FALLING_ANVIL) || source.is(DamageTypes.DRAGON_BREATH) || source.is(DamageTypes.WITHER) || source.is(DamageTypes.WITHER_SKULL)) return false;
        return super.hurt(source, amount);
    }
    private void applyConfiguredAttributes() {
        if (level().isClientSide || attributesApplied || !SquallConfig.SPEC.isLoaded()) return;
        float previous = getHealth();
        getAttribute(Attributes.MAX_HEALTH).setBaseValue(SquallConfig.HEALTH.get());
        getAttribute(Attributes.ARMOR).setBaseValue(SquallConfig.ARMOR.get());
        getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(SquallConfig.DAMAGE.get());
        getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(SquallConfig.SPEED.get());
        getAttribute(Attributes.KNOCKBACK_RESISTANCE).setBaseValue(SquallConfig.RESISTANCE.get());
        getAttribute(Attributes.ATTACK_KNOCKBACK).setBaseValue(SquallConfig.KNOCKBACK.get());
        setHealth(loadedFromSave ? Math.min(previous, getMaxHealth()) : getMaxHealth());
        attributesApplied = true;
    }
    @Override public void onAddedToLevel() { super.onAddedToLevel(); applyConfiguredAttributes(); schedulePulses(); }
    @Override public void baseTick() {
        applyConfiguredAttributes();
        super.baseTick();
        if (isRemoved()) return;
        AABB presence = AABB.ofSize(position(), 88, 88, 88);
        AABB darkness = new AABB(position(), position()).inflate(30);
        boolean hasNearbyPlayer = false;
        Player nearest = null;
        double nearestDistance = Double.MAX_VALUE;
        for (Player player : level().players()) {
            if (player.isRemoved()) continue;
            if (presence.intersects(player.getBoundingBox())) hasNearbyPlayer = true;
            double distance = player.distanceToSqr(this);
            if (distance < nearestDistance) { nearestDistance = distance; nearest = player; }
            if (!level().isClientSide && darkness.intersects(player.getBoundingBox())) {
                player.addEffect(new MobEffectInstance(MobEffects.DARKNESS, 40, 1));
                player.displayClientMessage(Component.translatable("message.thermal_shock_reforged.squall_seek_light").withStyle(ChatFormatting.RED), true);
            }
        }
        if (hasNearbyPlayer) {
            if (level().isClientSide) {
                if (isAlive()) level().addParticle(SquallContent.VISAGE.get(), getX(), getY() + 1.4, getZ(), 0, 0, 0);
                level().addParticle(SquallContent.SMOKE.get(), getX(), getY() + 1, getZ(), 0, 0, 0);
            } else if (nearest != null) {
                lookAt(EntityAnchorArgument.Anchor.FEET, nearest.position());
                setYBodyRot(getYRot());
                setYHeadRot(getYRot());
                if (!isFallFlying()) setOnGround(true);
                getNavigation().stop();
            }
            setDeltaMovement(getLookAngle().scale(.25));
        }
        schedulePulses();
    }
    public boolean attemptPlayerAttack(Player victim) {
        if (!(level() instanceof ServerLevel server)) return false;
        var soundOrigin = victim.blockPosition();
        Level victimLevel = victim.level();
        if (victimLevel.getBrightness(LightLayer.BLOCK, soundOrigin) >= 11) {
            discard();
            victimLevel.playSound(null, soundOrigin, SoundEvents.WITHER_SPAWN, SoundSource.HOSTILE, 1, -3);
            ThermalShockReforged.LOGGER.debug("Squall dispersed by block light: squall={}, victim={}", getUUID(), victim.getUUID());
            return true;
        }
        makeStuckInBlock(Blocks.AIR.defaultBlockState(), ATTACK_STUCK);
        remainingPulses = 59;
        ServerEndTickTasks.schedule(server, getUUID());
        victimLevel.playSound(null, soundOrigin, BuiltInRegistries.SOUND_EVENT.get(SquallContent.id("monolithhurt")), SoundSource.HOSTILE, 1, -3);
        ThermalShockReforged.LOGGER.debug("Squall dark attack slow committed: squall={}, victim={}, remainingEndPulses={}", getUUID(), victim.getUUID(), remainingPulses);
        return false;
    }
    private void schedulePulses() { if (remainingPulses > 0 && !isRemoved() && level() instanceof ServerLevel server) ServerEndTickTasks.schedule(server, getUUID()); }
    @Override public void endTick(ServerLevel level) {
        if (isRemoved() || level != level() || remainingPulses <= 0) return;
        makeStuckInBlock(Blocks.AIR.defaultBlockState(), ATTACK_STUCK);
        remainingPulses--;
        schedulePulses();
    }
    @Override public void die(DamageSource source) {
        super.die(source);
        if (!dead || deathAwarded || !(source.getEntity() instanceof ServerPlayer player)) return;
        deathAwarded = true;
        var advancement = player.server.getAdvancements().get(SquallContent.id("squall_kill_get"));
        if (advancement == null) ThermalShockReforged.LOGGER.warn("Squall kill advancement is unavailable: squall_kill_get");
        else {
            var progress = player.getAdvancements().getOrStartProgress(advancement);
            if (!progress.isDone()) for (String criterion : progress.getRemainingCriteria()) player.getAdvancements().award(advancement, criterion);
        }
    }
    @Override public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putInt("SquallRemainingPulses", remainingPulses);
        tag.putBoolean("SquallDeathAwarded", deathAwarded);
        if (remainingPulses > 0 || ATTACK_STUCK.equals(stuckSpeedMultiplier)) {
            tag.putDouble("SquallStuckX", stuckSpeedMultiplier.x);
            tag.putDouble("SquallStuckY", stuckSpeedMultiplier.y);
            tag.putDouble("SquallStuckZ", stuckSpeedMultiplier.z);
        }
    }
    @Override public void readAdditionalSaveData(CompoundTag tag) {
        loadedFromSave = true;
        super.readAdditionalSaveData(tag);
        attributesApplied = false;
        remainingPulses = Math.clamp(tag.getInt("SquallRemainingPulses"), 0, 59);
        deathAwarded = tag.getBoolean("SquallDeathAwarded");
        stuckSpeedMultiplier = new Vec3(validMultiplier(tag.getDouble("SquallStuckX")), validMultiplier(tag.getDouble("SquallStuckY")), validMultiplier(tag.getDouble("SquallStuckZ")));
        applyConfiguredAttributes();
    }
    private static double validMultiplier(double value) { return Double.isFinite(value) ? Math.clamp(value, 0, 1) : 0; }
}
