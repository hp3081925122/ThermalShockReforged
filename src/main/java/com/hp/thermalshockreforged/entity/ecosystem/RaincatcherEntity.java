package com.hp.thermalshockreforged.entity.ecosystem;

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
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.*;
import software.bernie.geckolib.util.GeckoLibUtil;

public final class RaincatcherEntity extends Monster implements GeoEntity {
    private static final EntityDataAccessor<String> TEXTURE = SynchedEntityData.defineId(RaincatcherEntity.class, EntityDataSerializers.STRING);
    private final AnimatableInstanceCache animations = GeckoLibUtil.createInstanceCache(this);

    public RaincatcherEntity(EntityType<? extends RaincatcherEntity> type, Level level) {
        super(type, level);
        xpReward = 3;
    }

    public static AttributeSupplier.Builder createAttributes() { return Mob.createMobAttributes().add(Attributes.MOVEMENT_SPEED, 0.2).add(Attributes.MAX_HEALTH, 11).add(Attributes.ARMOR, 1).add(Attributes.ATTACK_DAMAGE, 3).add(Attributes.FOLLOW_RANGE, 32).add(Attributes.STEP_HEIGHT, 0.6); }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(TEXTURE, "raincatcher");
    }

    public String texture() { return entityData.get(TEXTURE); }

    public void setTexture(String texture) { entityData.set(TEXTURE, texture); }

    public void setAnimation(String animation) { triggerAnim("procedure", animation); }

    @Override
    protected void registerGoals() {
        goalSelector.addGoal(1, new MeleeAttackGoal(this, 1.7, false) {
            @Override
            protected boolean canPerformAttack(LivingEntity target) { return isTimeToAttack() && isWithinMeleeAttackRange(target); }
        });
        targetSelector.addGoal(2, new HurtByTargetGoal(this).setAlertOthers());
        goalSelector.addGoal(3, new RandomStrollGoal(this, 0.8));
        goalSelector.addGoal(4, new RandomLookAroundGoal(this));
        targetSelector.addGoal(5, new NearestAttackableTargetGoal<>(this, Player.class, true, false));
        goalSelector.addGoal(6, new AvoidEntityGoal<>(this, LivingEntity.class, 6, 1, 1.2, entity -> EcosystemContent.hasType(entity, "monolith")));
    }

    @Override
    public boolean isWithinMeleeAttackRange(LivingEntity target) { return distanceToSqr(target) <= getBbWidth() * getBbWidth() + target.getBbWidth(); }

    @Override
    public SoundEvent getAmbientSound() { return SoundEvents.SKELETON_AMBIENT; }

    @Override
    protected void playStepSound(BlockPos pos, BlockState state) { playSound(SoundEvents.SKELETON_STEP, 0.15F, 1); }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return net.minecraft.core.registries.BuiltInRegistries.SOUND_EVENT.get(net.minecraft.resources.ResourceLocation.fromNamespaceAndPath("thermal_shock_reforged", "bone_hit"));
    }

    @Override
    protected SoundEvent getDeathSound() { return SoundEvents.SKELETON_DEATH; }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putString("Texture", texture());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        if (tag.contains("Texture")) setTexture(tag.getString("Texture"));
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        var procedure = new AnimationController<>(this, "procedure", 4, state -> PlayState.STOP)
                .triggerableAnim("idle", RawAnimation.begin().thenPlay("idle"))
                .triggerableAnim("walk", RawAnimation.begin().thenPlay("walk"))
                .triggerableAnim("run", RawAnimation.begin().thenPlay("run"))
                .triggerableAnim("walk2", RawAnimation.begin().thenPlay("walk2"));
        controllers.add(new AnimationController<>(this, "movement", 4, state -> procedure.isPlayingTriggeredAnimation()
                ? PlayState.STOP : state.setAndContinue(RawAnimation.begin().thenLoop(isAggressive() && state.isMoving() ? "run" : !isAggressive() && (state.isMoving() || Math.abs(state.getLimbSwingAmount()) >= 0.15F) ? "walk" : "idle"))));
        controllers.add(procedure);
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() { return animations; }
}
