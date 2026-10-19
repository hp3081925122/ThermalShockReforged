package com.hp.thermalshockreforged.entity.legacy;

import com.hp.thermalshockreforged.content.MaterialContent;
import com.hp.thermalshockreforged.entity.base.AnimatedMonsterEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;

public final class SolaEntity extends AnimatedMonsterEntity {
    private float previousScale = Float.NaN;

    public SolaEntity(EntityType<? extends SolaEntity> type, Level level) { super(type, level, "zerkglow", 10); }
    public float visualScale() { return getHealth() < 4 ? 1 : getHealth() / 3; }
    @Override protected EntityDimensions getDefaultDimensions(Pose pose) { return super.getDefaultDimensions(pose).scale(visualScale()); }
    @Override public void baseTick() {
        super.baseTick();
        float scale = visualScale();
        if (Float.compare(previousScale, scale) != 0) { previousScale = scale; refreshDimensions(); }
    }

    @Override protected void registerGoals() {
        super.registerGoals();
        goalSelector.addGoal(1, new MeleeAttackGoal(this, 1.2, false) {
            @Override protected boolean canPerformAttack(LivingEntity target) { return isTimeToAttack() && isWithinMeleeAttackRange(target); }
        });
        targetSelector.addGoal(2, new HurtByTargetGoal(this));
        goalSelector.addGoal(3, new RandomStrollGoal(this, .8));
        goalSelector.addGoal(4, new RandomLookAroundGoal(this));
        targetSelector.addGoal(5, new NearestAttackableTargetGoal<>(this, Player.class, false, false));
    }

    @Override public boolean isWithinMeleeAttackRange(LivingEntity target) { return distanceToSqr(target) <= 4; }
    @Override public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty, MobSpawnType reason, SpawnGroupData data) {
        var result = super.finalizeSpawn(level, difficulty, reason, data);
        if (random.nextDouble() < .5) setHealth(6);
        return result;
    }

    @Override protected void tickEffects() {
        if (!com.hp.thermalshockreforged.content.remainingcombat.CryoCooldownVisual.tickClientEffects(
                this, false, (effect, refresh) -> onEffectUpdated(effect, refresh, null))) super.tickEffects();
    }
    @Override public boolean hurt(DamageSource source, float amount) { return !source.is(DamageTypes.IN_FIRE) && super.hurt(source, amount); }
    @Override protected void playStepSound(BlockPos pos, BlockState state) { playSound(SoundEvents.SLIME_JUMP, .15F, 1); }
    @Override protected SoundEvent getHurtSound(DamageSource source) { return SoundEvents.SLIME_HURT; }
    @Override protected SoundEvent getDeathSound() { return SoundEvents.SLIME_DEATH; }
    @Override protected void dropCustomDeathLoot(ServerLevel level, DamageSource source, boolean recentlyHit) {
        super.dropCustomDeathLoot(level, source, recentlyHit);
        spawnAtLocation(new ItemStack(MaterialContent.items().get("pathiolite_scrap").get()));
    }

    public static AttributeSupplier.Builder createAttributes() {
        return createMobAttributes().add(Attributes.MOVEMENT_SPEED, .2).add(Attributes.MAX_HEALTH, 9).add(Attributes.ARMOR, 10).add(Attributes.ATTACK_DAMAGE, 4).add(Attributes.FOLLOW_RANGE, 16);
    }
}
