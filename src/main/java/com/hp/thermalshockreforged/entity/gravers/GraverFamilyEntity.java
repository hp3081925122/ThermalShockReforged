package com.hp.thermalshockreforged.entity.gravers;
import com.geckolib.animation.object.PlayState;
import com.hp.thermalshockreforged.compat.LegacyAnimationController;
import com.geckolib.animatable.manager.AnimatableManager;

import com.hp.thermalshockreforged.entity.base.AnimatedMonsterEntity;
import com.hp.thermalshockreforged.entity.monolith.MonolithEntity;
import java.util.List;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.*;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.*;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.core.BlockPos;
import com.geckolib.animation.*;

public abstract class GraverFamilyEntity extends AnimatedMonsterEntity {
    private boolean attributesApplied;
    private boolean loaded;
    protected GraverFamilyEntity(EntityType<? extends GraverFamilyEntity> type, Level level, String texture, int experience) { super(type, level, texture, experience); }
    public final int variant() { return this instanceof NeonLimeGraverEntity ? 1 : this instanceof AssimilatorEntity ? 2 : this instanceof AssimilatedGraverEntity ? 3 : this instanceof RazoraptorEntity ? 4 : 0; }
    @Override protected void registerGoals() {
        super.registerGoals();
        int kind = variant();
        goalSelector.addGoal(1, new MeleeAttackGoal(this, kind == 0 ? 1.7 : kind == 4 ? 1.2 : 2, false) {
            @Override protected boolean canPerformAttack(LivingEntity target) { return isTimeToAttack() && isWithinMeleeAttackRange(target); }
        });
        var hurt = new HurtByTargetGoal(this);
        targetSelector.addGoal(2, kind == 4 ? hurt : hurt.setAlertOthers());
        if (kind == 2) goalSelector.addGoal(3, new LeapAtTargetGoal(this, .5F));
        goalSelector.addGoal(kind == 2 ? 4 : 3, new RandomStrollGoal(this, kind == 1 || kind == 4 ? 1 : kind == 3 ? .9 : .8));
        goalSelector.addGoal(kind == 2 ? 5 : 4, new RandomLookAroundGoal(this));
        targetSelector.addGoal(kind == 2 ? 6 : 5, new NearestAttackableTargetGoal<>(this, Player.class, kind != 4, false));
        if (kind == 2) targetSelector.addGoal(7, new NearestAttackableTargetGoal<>(this, GraverEntity.class, true, false));
        if (kind == 0 || kind == 2 || kind == 3) goalSelector.addGoal(kind == 2 ? 8 : 6, new AvoidEntityGoal<>(this, MonolithEntity.class, kind == 2 ? 24 : 6, 1, 1.2));
    }
    @Override public boolean isWithinMeleeAttackRange(LivingEntity target) { return distanceToSqr(target) <= (variant() == 4 ? 9 : getBbWidth() * getBbWidth() + target.getBbWidth()); }
    @Override protected List<String> procedureAnimations() {
        return switch (variant()) {
            case 1 -> List.of("idle", "walk");
            case 2 -> List.of("idle", "walk", "bounce");
            case 3 -> List.of("idle", "walk", "run", "walk2");
            case 4 -> List.of("idle", "walk", "run", "attack", "hold");
            default -> List.of("idle", "walk", "walk2", "hurt", "death");
        };
    }
    @Override public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        var procedure = new LegacyAnimationController<>(this, "procedure", 4, state -> PlayState.STOP);
        for (var clip : procedureAnimations()) procedure.triggerableAnim(clip, RawAnimation.begin().thenPlay(clip));
        controllers.add(new LegacyAnimationController<>(this, "movement", 4, state -> {
            if (procedure.isPlayingTriggeredAnimation()) return PlayState.STOP;
            boolean moving = state.isMoving() || state.getLimbSwingAmount() <= -.15F || state.getLimbSwingAmount() >= .15F;
            int kind = variant();
            if (kind == 1) return state.setAndContinue(RawAnimation.begin().thenLoop(moving ? "walk" : "idle"));
            if (!isAggressive() && moving) return state.setAndContinue(RawAnimation.begin().thenLoop("walk"));
            if (kind == 0 && isDeadOrDying()) return state.setAndContinue(RawAnimation.begin().thenPlay("death"));
            if (kind == 2 && isDeadOrDying()) return state.setAndContinue(RawAnimation.begin().thenLoop("idle"));
            String clip = isAggressive() && state.isMoving() ? kind == 0 ? "walk2" : kind == 2 ? "walk" : "run" : "idle";
            return state.setAndContinue(RawAnimation.begin().thenLoop(clip));
        }));
        controllers.add(procedure);
    }
    @Override protected SoundEvent getAmbientSound() { return variant() == 2 || variant() == 4 ? null : SoundEvents.SKELETON_AMBIENT; }
    @Override protected SoundEvent getHurtSound(DamageSource source) { return variant() == 2 || variant() == 4 ? super.getHurtSound(source) : variant() == 3 ? SoundEvents.SKELETON_HURT : net.minecraft.core.registries.BuiltInRegistries.SOUND_EVENT.getValue(GraverContent.id("bone_hit")); }
    @Override protected SoundEvent getDeathSound() { return variant() == 2 || variant() == 4 ? super.getDeathSound() : SoundEvents.SKELETON_DEATH; }
    @Override protected void playStepSound(BlockPos pos, BlockState state) { if (variant() == 2 || variant() == 4) super.playStepSound(pos, state); else playSound(SoundEvents.SKELETON_STEP, .15F, 1); }
    private void applyConfiguration() {
        if (level().isClientSide() || attributesApplied || !GraverConfig.SPEC.isLoaded()) return;
        int kind = variant();
        float health = getHealth();
        getAttribute(Attributes.MAX_HEALTH).setBaseValue(GraverConfig.HEALTH[kind].get());
        getAttribute(Attributes.ARMOR).setBaseValue(GraverConfig.ARMOR[kind].get());
        getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(GraverConfig.DAMAGE[kind].get());
        getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(GraverConfig.SPEED[kind].get());
        setHealth(loaded ? Math.min(health, getMaxHealth()) : getMaxHealth());
        if (kind == 1) xpReward = GraverConfig.RARE_EXPERIENCE.get();
        attributesApplied = true;
    }
    @Override public void onAddedToLevel() { super.onAddedToLevel(); applyConfiguration(); }
    @Override public void tick() { applyConfiguration(); super.tick(); }
    @Override public void readAdditionalSaveData(net.minecraft.world.level.storage.ValueInput input) { loaded = true; super.readAdditionalSaveData(input); attributesApplied = false; }
    public static AttributeSupplier.Builder createAttributes(int kind) {
        var builder = createMobAttributes().add(Attributes.MAX_HEALTH, GraverConfig.DEFAULT_HEALTH[kind]).add(Attributes.ARMOR, GraverConfig.DEFAULT_ARMOR[kind]).add(Attributes.ATTACK_DAMAGE, GraverConfig.DEFAULT_DAMAGE[kind]).add(Attributes.MOVEMENT_SPEED, GraverConfig.DEFAULT_SPEED[kind]).add(Attributes.FOLLOW_RANGE, 32).add(Attributes.STEP_HEIGHT, kind == 4 ? 1 : .6);
        if (kind >= 3) builder.add(Attributes.KNOCKBACK_RESISTANCE, .2).add(Attributes.ATTACK_KNOCKBACK, kind == 3 ? .2 : .5);
        return builder;
    }
}
