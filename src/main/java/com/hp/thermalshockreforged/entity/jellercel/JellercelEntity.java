package com.hp.thermalshockreforged.entity.jellercel;

import com.hp.thermalshockreforged.content.MaterialContent;
import com.hp.thermalshockreforged.entity.PassiveSwimMoveControl;
import java.util.UUID;
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
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.RandomSwimmingGoal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.navigation.WaterBoundPathNavigation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.PathType;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

public final class JellercelEntity extends PathfinderMob implements GeoEntity {
    private static final EntityDataAccessor<String> TEXTURE = SynchedEntityData.defineId(JellercelEntity.class, EntityDataSerializers.STRING);
    private static final RawAnimation IDLE = RawAnimation.begin().thenLoop("idle");
    private final AnimatableInstanceCache animations = GeckoLibUtil.createInstanceCache(this);
    private final JellercelKind kind;
    private boolean experiencePending;
    private UUID experienceAttacker;

    public JellercelEntity(EntityType<? extends JellercelEntity> type, Level level, JellercelKind kind) {
        super(type, level);
        this.kind = kind;
        xpReward = 1;
        entityData.set(TEXTURE, kind.id + "_black");
        setPathfindingMalus(PathType.WATER, 0);
        moveControl = new PassiveSwimMoveControl(this);
        goalSelector.addGoal(1, new RandomSwimmingGoal(this, kind.swimmingGoalSpeed, 40));
    }

    public JellercelKind kind() { return kind; }
    public String texture() { return entityData.get(TEXTURE); }
    public void setTexture(String texture) { entityData.set(TEXTURE, texture); }
    public void setAnimation(String animation) { if ("idle".equals(animation)) triggerAnim("procedure", animation); }
    @Override protected void defineSynchedData(SynchedEntityData.Builder builder) { super.defineSynchedData(builder); builder.define(TEXTURE, ""); }
    @Override protected PathNavigation createNavigation(Level level) { return new WaterBoundPathNavigation(this, level); }
    @Override public boolean checkSpawnObstruction(LevelReader level) { return level.isUnobstructed(this); }
    @Override public boolean isPushedByFluid() { return false; }
    @Override public boolean hurt(DamageSource source, float damage) { return !source.is(DamageTypes.DROWN) && super.hurt(source, damage); }
    @Override public void aiStep() { super.aiStep(); updateSwingTime(); }
    @Override public SoundEvent getAmbientSound() { return SoundEvents.COD_AMBIENT; }
    @Override protected SoundEvent getHurtSound(DamageSource source) { return SoundEvents.COD_HURT; }
    @Override protected SoundEvent getDeathSound() { return SoundEvents.COD_DEATH; }
    @Override protected void playStepSound(BlockPos pos, BlockState state) { playSound(SoundEvents.COD_FLOP, .15F, 1); }

    @Override public void playerTouch(Player player) {
        super.playerTouch(player);
        if (!level().isClientSide) player.hurt(damageSources().cactus(), 1.0F);
    }

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
        super.addAdditionalSaveData(tag);
        tag.putString("Texture", texture());
        tag.putBoolean("JellercelExperiencePending", experiencePending);
        if (experienceAttacker != null) tag.putUUID("JellercelExperienceAttacker", experienceAttacker);
    }

    @Override public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        if (tag.contains("Texture")) setTexture(tag.getString("Texture"));
        experiencePending = tag.getBoolean("JellercelExperiencePending");
        experienceAttacker = tag.hasUUID("JellercelExperienceAttacker") ? tag.getUUID("JellercelExperienceAttacker") : null;
    }

    @Override public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        var procedure = new AnimationController<>(this, "procedure", 4, state -> PlayState.STOP).triggerableAnim("idle", RawAnimation.begin().thenPlay("idle"));
        controllers.add(new AnimationController<>(this, "movement", 4, state -> procedure.isPlayingTriggeredAnimation() ? PlayState.STOP : state.setAndContinue(IDLE)));
        controllers.add(procedure);
    }

    @Override public AnimatableInstanceCache getAnimatableInstanceCache() { return animations; }
}
