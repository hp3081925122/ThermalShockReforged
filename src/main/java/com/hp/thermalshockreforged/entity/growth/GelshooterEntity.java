package com.hp.thermalshockreforged.entity.growth;
import com.geckolib.animation.object.PlayState;
import com.hp.thermalshockreforged.compat.LegacyAnimationController;
import com.geckolib.animatable.manager.AnimatableManager;

import com.hp.thermalshockreforged.content.growth.GrowthContent;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.*;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.*;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.*;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.*;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.*;
import com.geckolib.animatable.GeoEntity;
import com.geckolib.animatable.instance.AnimatableInstanceCache;
import com.geckolib.animation.*;
import com.geckolib.util.GeckoLibUtil;

public final class GelshooterEntity extends PathfinderMob implements RangedAttackMob,GeoEntity {
    private static final byte SHOOT_EVENT=120;
    private static final EntityDataAccessor<Boolean> SHOOT=SynchedEntityData.defineId(GelshooterEntity.class,EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<String> ANIMATION=SynchedEntityData.defineId(GelshooterEntity.class,EntityDataSerializers.STRING);
    private static final EntityDataAccessor<String> TEXTURE=SynchedEntityData.defineId(GelshooterEntity.class,EntityDataSerializers.STRING);
    private final AnimatableInstanceCache animations=GeckoLibUtil.createInstanceCache(this);
    public String animationprocedure="empty";
    private String previousAnimation="empty";
    private boolean swinging;
    private boolean shootAnimationRequested;
    private long lastSwing;
    public GelshooterEntity(EntityType<? extends GelshooterEntity> type,Level level) { super(type,level); xpReward=0; setPersistenceRequired(); }
    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes().add(Attributes.MOVEMENT_SPEED,0).add(Attributes.MAX_HEALTH,10).add(Attributes.ARMOR,0).add(Attributes.ATTACK_DAMAGE,3)
                .add(Attributes.FOLLOW_RANGE,64).add(Attributes.KNOCKBACK_RESISTANCE,1).add(Attributes.STEP_HEIGHT,.6);
    }
    @Override protected void defineSynchedData(SynchedEntityData.Builder builder) { super.defineSynchedData(builder); builder.define(SHOOT,false); builder.define(ANIMATION,"undefined"); builder.define(TEXTURE,"gelshooter"); }
    public String texture() { return entityData.get(TEXTURE); }
    public void setTexture(String texture) { entityData.set(TEXTURE,texture); }
    public String getSyncedAnimation() { return entityData.get(ANIMATION); }
    public void setAnimation(String animation) { entityData.set(ANIMATION,animation); }
    @Override protected void registerGoals() {
        super.registerGoals();
        targetSelector.addGoal(1,new NearestAttackableTargetGoal<>(this,Monster.class,false,true));
        goalSelector.addGoal(2,new RandomLookAroundGoal(this));
        goalSelector.addGoal(1,new RangedAttackGoal(this,1.25,40,10) {
            @Override public boolean canContinueToUse() { return canUse(); }
            @Override public void tick() { entityData.set(SHOOT,false); super.tick(); }
            @Override public void stop() { super.stop(); entityData.set(SHOOT,false); }
        });
    }
    @Override public void performRangedAttack(LivingEntity target,float distanceFactor) {
        if(!(level() instanceof ServerLevel server))return;
        entityData.set(SHOOT,true);
        server.broadcastEntityEvent(this,SHOOT_EVENT);
        GelshooterBulletEntity.shoot(this,target);
    }
    @Override public void handleEntityEvent(byte event) {
        if(event==SHOOT_EVENT)shootAnimationRequested=true;
        else super.handleEntityEvent(event);
    }
    @Override public boolean removeWhenFarAway(double distance) { return false; }
    @Override public boolean hurtServer(net.minecraft.server.level.ServerLevel level,DamageSource source,float amount) { return !source.is(DamageTypes.FALL)&&super.hurtServer(level,source,amount); }
    @Override protected SoundEvent getHurtSound(DamageSource source) { return SoundEvents.GENERIC_HURT; }
    @Override protected SoundEvent getDeathSound() { return SoundEvents.GENERIC_DEATH; }
    @Override public SpawnGroupData finalizeSpawn(ServerLevelAccessor level,DifficultyInstance difficulty,EntitySpawnReason reason,SpawnGroupData group) {
        var result=super.finalizeSpawn(level,difficulty,reason,group);
        if (level instanceof ServerLevel server) server.sendParticles(ParticleTypes.POOF,getX(),getY(),getZ(),5,.1,.1,.1,.1);
        return result;
    }
    @Override public void addAdditionalSaveData(net.minecraft.world.level.storage.ValueOutput output) { super.addAdditionalSaveData(output); output.putString("Texture",texture()); }
    @Override public void readAdditionalSaveData(net.minecraft.world.level.storage.ValueInput input) { super.readAdditionalSaveData(input); setPersistenceRequired(); if(input.getString("Texture").isPresent())setTexture(input.getStringOr("Texture", "")); }
    @Override public void aiStep() { super.aiStep(); updateSwingTime(); }
    @Override public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new LegacyAnimationController<>(this,"movement",4,state->animationprocedure.equals("empty")?state.setAndContinue(RawAnimation.begin().thenLoop("0")):PlayState.STOP));
        controllers.add(new LegacyAnimationController<>(this,"attacking",4,state->{
            if(getAttackAnim(state.getPartialTick())>0&&!swinging) { swinging=true; lastSwing=level().getGameTime(); }
            if(swinging&&lastSwing+7<=level().getGameTime())swinging=false;
            if((shootAnimationRequested||swinging||entityData.get(SHOOT))&&state.getController().getAnimationState()==LegacyAnimationController.State.STOPPED) {
                shootAnimationRequested=false;
                state.getController().forceAnimationReset();
                return state.setAndContinue(RawAnimation.begin().thenPlay("shoot"));
            }
            return PlayState.CONTINUE;
        }));
        controllers.add(new LegacyAnimationController<>(this,"procedure",4,state->{
            if((!animationprocedure.equals("empty")&&state.getController().getAnimationState()==LegacyAnimationController.State.STOPPED)||(!animationprocedure.equals(previousAnimation)&&!animationprocedure.equals("empty"))) {
                if(!animationprocedure.equals(previousAnimation))state.getController().forceAnimationReset();
                state.getController().setAnimation(RawAnimation.begin().thenPlay(animationprocedure));
                if(state.getController().getAnimationState()==LegacyAnimationController.State.STOPPED) { animationprocedure="empty"; state.getController().forceAnimationReset(); }
            } else if(animationprocedure.equals("empty")) { previousAnimation="empty"; return PlayState.STOP; }
            previousAnimation=animationprocedure;
            return PlayState.CONTINUE;
        }));
    }
    @Override public AnimatableInstanceCache getAnimatableInstanceCache() { return animations; }
}
