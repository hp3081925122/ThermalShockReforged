package com.hp.thermalshockreforged.entity.moonoid;

import com.hp.thermalshockreforged.entity.base.AnimatedMonsterEntity;
import com.hp.thermalshockreforged.entity.starrite.StarriteEntity;
import net.minecraft.sounds.*;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.*;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.level.Level;

public final class MoonoidEntity extends AnimatedMonsterEntity {
    public MoonoidEntity(EntityType<? extends MoonoidEntity> type, Level level) { super(type, level, "moonoid", 0); setPersistenceRequired(); }
    public static AttributeSupplier.Builder attributes() { return Mob.createMobAttributes().add(Attributes.MAX_HEALTH, 6).add(Attributes.ARMOR, 0).add(Attributes.ATTACK_DAMAGE, 0).add(Attributes.MOVEMENT_SPEED, .3).add(Attributes.FOLLOW_RANGE, 16).add(Attributes.STEP_HEIGHT, 1); }
    @Override protected void registerGoals() {
        goalSelector.addGoal(1, new RandomStrollGoal(this, 1));
        goalSelector.addGoal(2, new AvoidEntityGoal<>(this, StarriteEntity.class, 6, 1, 1.2));
        goalSelector.addGoal(3, new RandomLookAroundGoal(this));
        goalSelector.addGoal(4, new FloatGoal(this));
    }
    @Override protected int animationTransitionTicks() { return 0; }
    @Override public boolean removeWhenFarAway(double distance) { return false; }
    @Override protected SoundEvent getHurtSound(DamageSource source) { return SoundEvents.GENERIC_HURT; }
    @Override protected SoundEvent getDeathSound() { return SoundEvents.GENERIC_DEATH; }
}
