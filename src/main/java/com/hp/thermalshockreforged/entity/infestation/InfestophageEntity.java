package com.hp.thermalshockreforged.entity.infestation;
import com.geckolib.animation.object.PlayState;
import com.hp.thermalshockreforged.compat.LegacyAnimationController;
import com.geckolib.animatable.manager.AnimatableManager;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.*;
import net.minecraft.sounds.*;
import net.minecraft.world.damagesource.*;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.ai.attributes.*;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import com.geckolib.animatable.GeoEntity;
import com.geckolib.animatable.instance.AnimatableInstanceCache;
import com.geckolib.animation.*;
import com.geckolib.util.GeckoLibUtil;

public final class InfestophageEntity extends Monster implements GeoEntity {
    private static final EntityDataAccessor<String> TEXTURE = SynchedEntityData.defineId(InfestophageEntity.class, EntityDataSerializers.STRING);
    private final AnimatableInstanceCache animations = GeckoLibUtil.createInstanceCache(this);

    public InfestophageEntity(EntityType<? extends InfestophageEntity> type, Level level) {
        super(type, level);
        xpReward = 3;
        setPersistenceRequired();
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes().add(Attributes.MOVEMENT_SPEED, 0.3).add(Attributes.MAX_HEALTH, 40).add(Attributes.ARMOR, 6).add(Attributes.ATTACK_DAMAGE, 4).add(Attributes.FOLLOW_RANGE, 64).add(Attributes.KNOCKBACK_RESISTANCE, 0.2).add(Attributes.ATTACK_KNOCKBACK, 0.5).add(Attributes.STEP_HEIGHT, 1);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(TEXTURE, "infestor");
    }

    public String texture() { return entityData.get(TEXTURE); }

    public void setTexture(String texture) { entityData.set(TEXTURE, texture); }

    public void setAnimation(String animation) { triggerAnim("procedure", animation); }

    @Override
    protected void registerGoals() {
        goalSelector.addGoal(1, new MeleeAttackGoal(this, 1.2, false) {
            @Override
            protected boolean canPerformAttack(LivingEntity target) { return isTimeToAttack() && mob.distanceToSqr(target) <= 4; }
        });
        targetSelector.addGoal(2, new HurtByTargetGoal(this).setAlertOthers());
        goalSelector.addGoal(3, new RandomStrollGoal(this, 0.8));
        goalSelector.addGoal(4, new RandomLookAroundGoal(this));
        targetSelector.addGoal(5, new NearestAttackableTargetGoal<>(this, LivingEntity.class, true, false));
    }

    @Override
    public boolean isWithinMeleeAttackRange(LivingEntity target) { return distanceToSqr(target) <= 4; }

    @Override
    public boolean removeWhenFarAway(double distance) { return false; }

    @Override
    public SoundEvent getAmbientSound() { return SoundEvents.SILVERFISH_AMBIENT; }

    @Override
    protected void playStepSound(BlockPos pos, BlockState state) { playSound(SoundEvents.SKELETON_STEP, 0.15F, 1); }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) { return SoundEvents.SILVERFISH_HURT; }

    @Override
    protected SoundEvent getDeathSound() { return SoundEvents.SILVERFISH_DEATH; }

    @Override
    public void addAdditionalSaveData(net.minecraft.world.level.storage.ValueOutput output) {
        super.addAdditionalSaveData(output);
        output.putString("Texture", texture());
    }

    @Override
    public void readAdditionalSaveData(net.minecraft.world.level.storage.ValueInput input) {
        super.readAdditionalSaveData(input);
        if (input.getString("Texture").isPresent()) setTexture(input.getStringOr("Texture", ""));
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        var procedure = new LegacyAnimationController<>(this, "procedure", 0, state -> PlayState.STOP)
                .triggerableAnim("idle", RawAnimation.begin().thenPlay("idle"))
                .triggerableAnim("walk", RawAnimation.begin().thenPlay("walk"))
                .triggerableAnim("aggro", RawAnimation.begin().thenPlay("aggro"));
        controllers.add(new LegacyAnimationController<>(this, "movement", 0, state -> procedure.isPlayingTriggeredAnimation()
                ? PlayState.STOP : state.setAndContinue(RawAnimation.begin().thenLoop(state.isMoving() || Math.abs(state.getLimbSwingAmount()) >= 0.15F ? "walk" : "idle"))));
        controllers.add(procedure);
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() { return animations; }
}
