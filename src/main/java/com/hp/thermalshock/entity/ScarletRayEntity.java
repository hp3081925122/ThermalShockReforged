package com.hp.thermalshock.entity;

import com.hp.thermalshock.content.MaterialContent;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.PanicGoal;
import net.minecraft.world.entity.ai.goal.RandomSwimmingGoal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.navigation.WaterBoundPathNavigation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.PathType;
import net.neoforged.neoforge.common.NeoForgeMod;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

public final class ScarletRayEntity extends PathfinderMob implements GeoEntity {
    private static final EntityDataAccessor<String> TEXTURE = SynchedEntityData.defineId(ScarletRayEntity.class, EntityDataSerializers.STRING);
    private static final RawAnimation SWIM = RawAnimation.begin().thenLoop("swim");
    private final AnimatableInstanceCache animations = GeckoLibUtil.createInstanceCache(this);

    public ScarletRayEntity(EntityType<? extends ScarletRayEntity> type, Level level) {
        super(type, level);
        xpReward = 1;
        setPathfindingMalus(PathType.WATER, 0);
        moveControl = new PassiveSwimMoveControl(this);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes().add(Attributes.MOVEMENT_SPEED, 1).add(Attributes.MAX_HEALTH, 6).add(Attributes.ARMOR, 0).add(Attributes.ATTACK_DAMAGE, 0).add(Attributes.FOLLOW_RANGE, 16).add(Attributes.STEP_HEIGHT, 0.6).add(NeoForgeMod.SWIM_SPEED, 1);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(TEXTURE, "scarletray");
    }

    public String texture() {
        return entityData.get(TEXTURE);
    }

    public void setTexture(String texture) {
        entityData.set(TEXTURE, texture);
    }

    public void setAnimation(String animation) {
        triggerAnim("procedure", animation);
    }

    @Override
    protected PathNavigation createNavigation(Level level) {
        return new WaterBoundPathNavigation(this, level);
    }

    @Override
    protected void registerGoals() {
        goalSelector.addGoal(1, new PanicGoal(this, 1.2));
        goalSelector.addGoal(2, new RandomSwimmingGoal(this, 1, 40));
    }

    @Override
    protected void dropCustomDeathLoot(ServerLevel level, DamageSource source, boolean recentlyHit) {
        super.dropCustomDeathLoot(level, source, recentlyHit);
        spawnAtLocation(MaterialContent.items().get("scarlet_marrow").get());
    }

    @Override
    public SoundEvent getAmbientSound() {
        return SoundEvents.COD_AMBIENT;
    }

    @Override
    protected void playStepSound(BlockPos position, BlockState state) {
        playSound(SoundEvents.COD_FLOP, 0.15F, 1);
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.COD_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.COD_DEATH;
    }

    @Override
    public boolean hurt(DamageSource source, float damage) {
        return !source.is(DamageTypes.DROWN) && super.hurt(source, damage);
    }

    @Override
    public boolean checkSpawnObstruction(LevelReader level) {
        return level.isUnobstructed(this);
    }

    @Override
    public boolean isPushedByFluid() {
        return false;
    }

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
        controllers.add(new AnimationController<>(this, "movement", 4, state -> state.setAndContinue(SWIM)));
        controllers.add(new AnimationController<>(this, "procedure", 4, state -> PlayState.STOP)
                .triggerableAnim("swim", RawAnimation.begin().thenPlay("swim")));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return animations;
    }
}
