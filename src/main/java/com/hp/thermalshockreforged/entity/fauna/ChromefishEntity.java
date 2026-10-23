package com.hp.thermalshockreforged.entity.fauna;
import com.hp.thermalshockreforged.compat.LegacyAnimationController;
import com.geckolib.animatable.manager.AnimatableManager;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.*;
import net.minecraft.world.entity.ai.navigation.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.PathType;
import net.neoforged.neoforge.common.NeoForgeMod;
import com.geckolib.animatable.GeoEntity;
import com.geckolib.animatable.instance.AnimatableInstanceCache;
import com.geckolib.animation.*;
import com.geckolib.util.GeckoLibUtil;

public final class ChromefishEntity extends PathfinderMob implements GeoEntity {
    private static final EntityDataAccessor<String> TEXTURE = SynchedEntityData.defineId(ChromefishEntity.class, EntityDataSerializers.STRING);
    private final AnimatableInstanceCache animations = GeckoLibUtil.createInstanceCache(this);

    public ChromefishEntity(EntityType<? extends ChromefishEntity> type, Level level) {
        super(type, level);
        xpReward = 1;
        setPathfindingMalus(PathType.WATER, 0);
        moveControl = new FaunaSwimMoveControl(this);
    }

    public static AttributeSupplier.Builder createAttributes() { return Mob.createMobAttributes().add(Attributes.MOVEMENT_SPEED, 2).add(Attributes.MAX_HEALTH, 2).add(Attributes.ARMOR, 2).add(Attributes.ATTACK_DAMAGE, 0).add(Attributes.FOLLOW_RANGE, 16).add(Attributes.STEP_HEIGHT, 0.6).add(NeoForgeMod.SWIM_SPEED, 2); }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(TEXTURE, "chromefish");
    }

    public String texture() { return entityData.get(TEXTURE); }

    public void setTexture(String texture) { entityData.set(TEXTURE, texture); }

    public void setAnimation(String animation) { triggerAnim("movement", animation); }

    @Override
    protected void registerGoals() {
        goalSelector.addGoal(1, new PanicGoal(this, 1.2));
        goalSelector.addGoal(2, new RandomSwimmingGoal(this, 1, 40));
    }

    @Override
    protected PathNavigation createNavigation(Level level) { return new WaterBoundPathNavigation(this, level); }

    @Override
    public boolean checkSpawnObstruction(LevelReader level) { return level.isUnobstructed(this); }

    @Override
    public boolean isPushedByFluid() { return false; }

    @Override
    public boolean hurtServer(net.minecraft.server.level.ServerLevel level, DamageSource source, float amount) { return !source.is(DamageTypes.DROWN) && super.hurtServer(level, source, amount); }

    @Override
    public SoundEvent getAmbientSound() { return SoundEvents.COD_AMBIENT; }

    @Override
    protected void playStepSound(BlockPos pos, BlockState state) { playSound(SoundEvents.COD_FLOP, 0.15F, 1); }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) { return SoundEvents.COD_HURT; }

    @Override
    protected SoundEvent getDeathSound() { return SoundEvents.COD_DEATH; }

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
        controllers.add(new LegacyAnimationController<>(this, "movement", 4, state -> {
            return state.setAndContinue(RawAnimation.begin().thenLoop(isInWaterOrBubble() ? "swim" : "idle"));
        })
                .triggerableAnim("swim", RawAnimation.begin().thenPlay("swim"))
                .triggerableAnim("idle", RawAnimation.begin().thenPlay("idle")));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() { return animations; }
}
