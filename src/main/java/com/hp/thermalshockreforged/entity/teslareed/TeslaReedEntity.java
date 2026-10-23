package com.hp.thermalshockreforged.entity.teslareed;

import com.hp.thermalshockreforged.entity.base.AnimatedPathfinderEntity;
import com.hp.thermalshockreforged.content.plants.tall.TallPlantContent;
import java.util.List;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.*;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.*;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.*;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.*;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.*;

public final class TeslaReedEntity extends AnimatedPathfinderEntity {
    private int cooldown;
    private boolean submitting;
    public TeslaReedEntity(EntityType<? extends TeslaReedEntity> type, Level level) { super(type, level, "tesla_reed", 0); setPersistenceRequired(); }
    @Override protected void registerGoals() {
        targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, Monster.class, false, false));
        goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.2, false) {
            @Override protected boolean canPerformAttack(LivingEntity target) { return isTimeToAttack() && isWithinMeleeAttackRange(target); }
        });
        goalSelector.addGoal(3, new RandomLookAroundGoal(this));
    }
    @Override public boolean isWithinMeleeAttackRange(LivingEntity target) { return distanceToSqr(target) <= 25; }
    @Override public boolean removeWhenFarAway(double distance) { return false; }
    @Override protected String movementAnimation(boolean moving) { return "0"; }
    @Override protected List<String> procedureAnimations() { return List.of("0", "shoot"); }
    public void beginAttack(LivingEntity target) {
        if (!(level() instanceof ServerLevel server) || !isAlive() || cooldown > 0 || submitting || target == this || target.level() != level()) return;
        submitting = true;
        boolean accepted;
        try { accepted = TeslaPulseEntity.submit(server, target.position()); } finally { submitting = false; }
        if (!accepted) return;
        cooldown = 80;
        triggerProcedure("shoot");
        server.playSound(null, target.blockPosition(), SoundEvents.CONDUIT_ACTIVATE, SoundSource.NEUTRAL, 1, 3);
    }
    @Override public void baseTick() { super.baseTick(); if (!level().isClientSide() && cooldown > 0) cooldown--; }
    @Override public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty, EntitySpawnReason reason, SpawnGroupData data) {
        var result = super.finalizeSpawn(level, difficulty, reason, data);
        if (level instanceof ServerLevel server) server.sendParticles(ParticleTypes.POOF, getX(), getY(), getZ(), 5, .1, .1, .1, .1);
        return result;
    }
    @Override public boolean hurtServer(net.minecraft.server.level.ServerLevel level, DamageSource source, float amount) { return !source.is(DamageTypes.FALL) && super.hurtServer(level, source, amount); }
    @Override protected SoundEvent getHurtSound(DamageSource source) { return SoundEvents.GENERIC_HURT; }
    @Override protected SoundEvent getDeathSound() { return SoundEvents.GENERIC_DEATH; }
    @Override public void addAdditionalSaveData(net.minecraft.world.level.storage.ValueOutput output) { super.addAdditionalSaveData(output); output.putInt("TeslaCooldown", cooldown); }
    @Override public void readAdditionalSaveData(net.minecraft.world.level.storage.ValueInput input) { super.readAdditionalSaveData(input); cooldown = Math.clamp(input.getIntOr("TeslaCooldown", 0), 0, 80); }
    public static AttributeSupplier.Builder attributes() { return Mob.createMobAttributes().add(Attributes.MOVEMENT_SPEED, 0).add(Attributes.MAX_HEALTH, 10).add(Attributes.ARMOR, 0).add(Attributes.ATTACK_DAMAGE, 3).add(Attributes.FOLLOW_RANGE, 64).add(Attributes.KNOCKBACK_RESISTANCE, 1).add(Attributes.STEP_HEIGHT, .6); }
}
