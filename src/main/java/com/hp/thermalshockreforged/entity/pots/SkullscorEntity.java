package com.hp.thermalshockreforged.entity.pots;

import com.hp.thermalshockreforged.entity.gardenling.GardenlingEntity;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.*;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.*;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.*;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.*;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.*;
import software.bernie.geckolib.util.GeckoLibUtil;

public final class SkullscorEntity extends Monster implements GeoEntity {
    private static final EntityDataAccessor<String> TEXTURE = SynchedEntityData.defineId(SkullscorEntity.class, EntityDataSerializers.STRING);
    private final AnimatableInstanceCache animations = GeckoLibUtil.createInstanceCache(this);
    private boolean experiencePending;
    private UUID experienceAttacker;
    private boolean swinging;
    private long lastSwing;
    public SkullscorEntity(EntityType<? extends SkullscorEntity> type, Level level) { super(type, level); xpReward = 3; }
    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes().add(Attributes.MOVEMENT_SPEED, 0.3).add(Attributes.MAX_HEALTH, 8).add(Attributes.ARMOR, 4)
                .add(Attributes.ATTACK_DAMAGE, 2).add(Attributes.FOLLOW_RANGE, 16).add(Attributes.ATTACK_KNOCKBACK, 0.8).add(Attributes.STEP_HEIGHT, 0.6);
    }
    @Override protected void defineSynchedData(SynchedEntityData.Builder builder) { super.defineSynchedData(builder); builder.define(TEXTURE, "skullscor"); }
    public String texture() { return entityData.get(TEXTURE); }
    public void setTexture(String value) { entityData.set(TEXTURE, value); }
    public void setAnimation(String value) { triggerAnim("procedure", value); }
    @Override protected void registerGoals() {
        goalSelector.addGoal(1, new MeleeAttackGoal(this, 1.2, false) {
            @Override protected boolean canPerformAttack(LivingEntity target) { return isTimeToAttack() && isWithinMeleeAttackRange(target); }
        });
        targetSelector.addGoal(2, new HurtByTargetGoal(this));
        goalSelector.addGoal(3, new RandomStrollGoal(this, 0.8));
        goalSelector.addGoal(4, new RandomLookAroundGoal(this));
        goalSelector.addGoal(5, new LeapAtTargetGoal(this, 0.5F));
        targetSelector.addGoal(6, new NearestAttackableTargetGoal<>(this, Player.class, false, false));
        targetSelector.addGoal(7, new NearestAttackableTargetGoal<>(this, GardenlingEntity.class, false, false));
    }
    @Override public boolean isWithinMeleeAttackRange(LivingEntity target) { return distanceToSqr(target) <= 4; }
    @Override public SoundEvent getAmbientSound() { return SoundEvents.SPIDER_AMBIENT; }
    @Override protected SoundEvent getHurtSound(DamageSource source) { return SoundEvents.SPIDER_HURT; }
    @Override protected SoundEvent getDeathSound() { return SoundEvents.SPIDER_DEATH; }
    @Override protected void playStepSound(BlockPos pos, BlockState state) { playSound(SoundEvents.SPIDER_STEP, 0.15F, 1); }
    @Override public void aiStep() { super.aiStep(); updateSwingTime(); }
    @Override protected void dropExperience(Entity attacker) { experiencePending = true; experienceAttacker = attacker == null ? null : attacker.getUUID(); }
    @Override protected void tickDeath() {
        if (++deathTime >= 20 && !isRemoved()) {
            remove(RemovalReason.KILLED);
            if (experiencePending && level() instanceof ServerLevel server) {
                experiencePending = false;
                super.dropExperience(experienceAttacker == null ? null : server.getEntity(experienceAttacker));
            }
        }
    }
    @Override public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag); tag.putString("Texture", texture()); tag.putBoolean("SkullscorExperiencePending", experiencePending);
        if (experienceAttacker != null) tag.putUUID("SkullscorExperienceAttacker", experienceAttacker);
    }
    @Override public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag); if (tag.contains("Texture")) setTexture(tag.getString("Texture"));
        experiencePending = tag.getBoolean("SkullscorExperiencePending");
        experienceAttacker = tag.hasUUID("SkullscorExperienceAttacker") ? tag.getUUID("SkullscorExperienceAttacker") : null;
    }
    @Override public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        var procedure = new AnimationController<>(this, "procedure", 4, state -> PlayState.STOP);
        for (String clip : new String[]{"idle", "walk", "attack"}) procedure.triggerableAnim(clip, RawAnimation.begin().thenPlay(clip));
        controllers.add(new AnimationController<>(this, "movement", 4, state -> {
            if (procedure.isPlayingTriggeredAnimation()) return PlayState.STOP;
            return state.setAndContinue(RawAnimation.begin().thenLoop(state.isMoving() || Math.abs(state.getLimbSwingAmount()) >= 0.15F ? "walk" : "idle"));
        }));
        controllers.add(new AnimationController<>(this, "attacking", 4, state -> {
            if (getAttackAnim(state.getPartialTick()) > 0 && !swinging) { swinging = true; lastSwing = level().getGameTime(); }
            if (swinging && lastSwing + 7 <= level().getGameTime()) swinging = false;
            if (swinging && state.getController().getAnimationState() == AnimationController.State.STOPPED) {
                state.getController().forceAnimationReset();
                return state.setAndContinue(RawAnimation.begin().thenPlay("attack"));
            }
            return PlayState.CONTINUE;
        }));
        controllers.add(procedure);
    }
    @Override public AnimatableInstanceCache getAnimatableInstanceCache() { return animations; }
}
