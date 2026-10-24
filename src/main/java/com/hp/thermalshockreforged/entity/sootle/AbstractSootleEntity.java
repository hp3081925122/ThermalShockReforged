package com.hp.thermalshockreforged.entity.sootle;
import com.geckolib.animation.object.PlayState;
import com.hp.thermalshockreforged.compat.LegacyAnimationController;
import com.geckolib.animatable.manager.AnimatableManager;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.*;
import net.minecraft.sounds.*;
import net.minecraft.world.damagesource.*;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.*;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;
import com.geckolib.animatable.GeoEntity;
import com.geckolib.animatable.instance.AnimatableInstanceCache;
import com.geckolib.animation.*;
import com.geckolib.animation.state.AnimationTest;
import com.geckolib.util.GeckoLibUtil;

public abstract class AbstractSootleEntity extends Monster implements GeoEntity {
    private static final EntityDataAccessor<String> TEXTURE = SynchedEntityData.defineId(AbstractSootleEntity.class, EntityDataSerializers.STRING);
    private final AnimatableInstanceCache animations = GeckoLibUtil.createInstanceCache(this);
    protected AbstractSootleEntity(EntityType<? extends AbstractSootleEntity> type, Level level, int experience) { super(type, level); xpReward = experience; setPersistenceRequired(); }
    public static AttributeSupplier.Builder createAttributes() { return Mob.createMobAttributes().add(Attributes.MOVEMENT_SPEED, 0.2).add(Attributes.MAX_HEALTH, 30).add(Attributes.ARMOR, 0).add(Attributes.ATTACK_DAMAGE, 3).add(Attributes.FOLLOW_RANGE, 16).add(Attributes.STEP_HEIGHT, 0.6); }
    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) { super.defineSynchedData(builder); builder.define(TEXTURE, "sootling"); }
    public String texture() { return entityData.get(TEXTURE); }
    public void setTexture(String value) { entityData.set(TEXTURE, value); }
    public void setAnimation(String value) { triggerAnim("procedure", value); }
    @Override
    public boolean removeWhenFarAway(double distance) { return false; }
    @Override
    public boolean hurtServer(net.minecraft.server.level.ServerLevel level, DamageSource source, float amount) { return !source.is(DamageTypes.IN_FIRE) && super.hurtServer(level, source, amount); }
    @Override
    protected SoundEvent getHurtSound(DamageSource source) { return SoundEvents.GENERIC_HURT; }
    @Override
    protected SoundEvent getDeathSound() { return SoundEvents.GENERIC_DEATH; }
    @Override
    public void addAdditionalSaveData(net.minecraft.world.level.storage.ValueOutput output) { super.addAdditionalSaveData(output); output.putString("Texture", texture()); }
    @Override
    public void readAdditionalSaveData(net.minecraft.world.level.storage.ValueInput input) { super.readAdditionalSaveData(input); if (input.read("Texture", com.mojang.serialization.Codec.PASSTHROUGH).isPresent()) setTexture(input.getStringOr("Texture", "")); }
    protected abstract String movement(com.hp.thermalshockreforged.compat.LegacyAnimationController.LegacyAnimationState<?> state);
    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        var procedure = new LegacyAnimationController<>(this, "procedure", 4, state -> PlayState.STOP)
                .triggerableAnim("idle", RawAnimation.begin().thenPlay("idle"))
                .triggerableAnim("walk", RawAnimation.begin().thenPlay("walk"))
                .triggerableAnim("run", RawAnimation.begin().thenPlay("run"))
                .triggerableAnim("attack", RawAnimation.begin().thenPlay("attack"))
                .triggerableAnim("attack2", RawAnimation.begin().thenPlay("attack2"))
                .triggerableAnim("dig", RawAnimation.begin().thenPlay("dig"))
                .triggerableAnim("alert", RawAnimation.begin().thenPlay("alert"));
        controllers.add(new LegacyAnimationController<>(this, "movement", 4, state -> procedure.isPlayingTriggeredAnimation() ? PlayState.STOP : state.setAndContinue(RawAnimation.begin().thenLoop(movement(state)))));
        controllers.add(procedure);
    }
    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() { return animations; }
}
