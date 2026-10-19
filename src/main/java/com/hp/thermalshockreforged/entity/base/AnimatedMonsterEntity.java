package com.hp.thermalshockreforged.entity.base;

import java.util.List;
import java.util.UUID;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

public abstract class AnimatedMonsterEntity extends Monster implements GeoEntity {
    private static final EntityDataAccessor<String> TEXTURE = SynchedEntityData.defineId(AnimatedMonsterEntity.class, EntityDataSerializers.STRING);
    private final AnimatableInstanceCache animations = GeckoLibUtil.createInstanceCache(this);
    private boolean experiencePending;
    private UUID experienceAttacker;

    protected AnimatedMonsterEntity(EntityType<? extends AnimatedMonsterEntity> type, Level level, String texture, int experience) {
        super(type, level);
        entityData.set(TEXTURE, texture);
        xpReward = experience;
    }

    public String texture() { return entityData.get(TEXTURE); }
    public void setTexture(String texture) { entityData.set(TEXTURE, texture); }
    protected List<String> procedureAnimations() { return List.of("idle", "walk"); }
    protected String movementAnimation(boolean moving) { return moving ? "walk" : "idle"; }
    protected int animationTransitionTicks() { return 4; }
    protected int deathDuration() { return 20; }
    public void triggerProcedure(String animation) { if (animation != null && procedureAnimations().contains(animation)) triggerAnim("procedure", animation); }
    @Override protected void defineSynchedData(SynchedEntityData.Builder builder) { super.defineSynchedData(builder); builder.define(TEXTURE, ""); }
    @Override public void aiStep() { super.aiStep(); updateSwingTime(); }

    @Override public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        var procedure = new AnimationController<>(this, "procedure", animationTransitionTicks(), state -> PlayState.STOP);
        for (String clip : procedureAnimations()) procedure.triggerableAnim(clip, RawAnimation.begin().thenPlay(clip));
        controllers.add(new AnimationController<>(this, "movement", animationTransitionTicks(), state -> {
            if (procedure.isPlayingTriggeredAnimation()) return PlayState.STOP;
            boolean moving = state.isMoving() || state.getLimbSwingAmount() <= -.15F || state.getLimbSwingAmount() >= .15F;
            return state.setAndContinue(RawAnimation.begin().thenLoop(movementAnimation(moving)));
        }));
        controllers.add(procedure);
    }

    @Override public AnimatableInstanceCache getAnimatableInstanceCache() { return animations; }
    @Override protected void dropExperience(Entity attacker) { experiencePending = true; experienceAttacker = attacker == null ? null : attacker.getUUID(); }
    @Override protected void tickDeath() {
        if (++deathTime >= deathDuration() && !isRemoved()) {
            remove(RemovalReason.KILLED);
            if (experiencePending && level() instanceof ServerLevel server) {
                experiencePending = false;
                super.dropExperience(experienceAttacker == null ? null : server.getEntity(experienceAttacker));
            }
        }
    }

    @Override public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putString("Texture", texture());
        tag.putBoolean("AnimatedExperiencePending", experiencePending);
        if (experienceAttacker != null) tag.putUUID("AnimatedExperienceAttacker", experienceAttacker);
    }

    @Override public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        if (tag.contains("Texture")) setTexture(tag.getString("Texture"));
        experiencePending = tag.getBoolean("AnimatedExperiencePending");
        experienceAttacker = tag.hasUUID("AnimatedExperienceAttacker") ? tag.getUUID("AnimatedExperienceAttacker") : null;
    }
}
