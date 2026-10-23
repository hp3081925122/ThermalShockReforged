package com.hp.thermalshockreforged.entity;
import com.hp.thermalshockreforged.compat.LegacyAnimationController;

import net.minecraft.core.BlockPos;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Blocks;
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
import com.geckolib.animatable.GeoEntity;
import com.geckolib.animatable.instance.AnimatableInstanceCache;
import com.geckolib.animatable.manager.AnimatableManager;
import com.geckolib.animation.RawAnimation;
import com.geckolib.animation.object.PlayState;
import com.geckolib.util.GeckoLibUtil;

public final class PondlerEntity extends PathfinderMob implements GeoEntity {
    private static final EntityDataAccessor<String> TEXTURE = SynchedEntityData.defineId(PondlerEntity.class, EntityDataSerializers.STRING);
    private static final RawAnimation WATER_IDLE = RawAnimation.begin().thenLoop("idle");
    private static final RawAnimation LAND_IDLE = RawAnimation.begin().thenLoop("landidle");
    private final AnimatableInstanceCache animations = GeckoLibUtil.createInstanceCache(this);
    private int jumpCooldown;

    public PondlerEntity(EntityType<? extends PondlerEntity> type, Level level) {
        super(type, level);
        xpReward = 2;
        setPathfindingMalus(PathType.WATER, 0);
        moveControl = new PassiveSwimMoveControl(this);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes().add(Attributes.MOVEMENT_SPEED, 1).add(Attributes.MAX_HEALTH, 2).add(Attributes.ARMOR, 0).add(Attributes.ATTACK_DAMAGE, 0).add(Attributes.FOLLOW_RANGE, 16).add(Attributes.STEP_HEIGHT, 0.6).add(NeoForgeMod.SWIM_SPEED, 1);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(TEXTURE, "green_pondler");
    }

    public String texture() {
        return entityData.get(TEXTURE);
    }

    public void setTexture(String texture) {
        entityData.set(TEXTURE, "orange_pondler".equals(texture) ? "orange_pondler" : "green_pondler");
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
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty, EntitySpawnReason reason, SpawnGroupData group) {
        SpawnGroupData result = super.finalizeSpawn(level, difficulty, reason, group);
        setTexture(random.nextDouble() < 0.5 ? "orange_pondler" : "green_pondler");
        return result;
    }

    @Override
    public void baseTick() {
        super.baseTick();
        if (level().isClientSide() || !isAlive()) return;
        if (jumpCooldown > 0) jumpCooldown--;
        if (jumpCooldown > 0 || level().getBlockState(blockPosition().below()).canOcclude()) return;
        if (level().getBlockState(blockPosition()).is(Blocks.AIR)) {
            var look = getLookAngle();
            setDeltaMovement(look.x * 0.2, 1, look.z * 0.2);
            jumpCooldown = (int) Math.round(80 + random.nextDouble() * 220);
            setAnimation("jump");
        } else {
            setDeltaMovement(0, 0.5, 0);
        }
        hurtMarked = true;
    }

    @Override
    protected float getDamageAfterMagicAbsorb(DamageSource source, float damage) {
        if (!level().getBlockState(blockPosition()).is(Blocks.AIR) && !source.is(DamageTypeTags.BYPASSES_EFFECTS) && !source.is(DamageTypeTags.BYPASSES_RESISTANCE)) {
            damageContainers.peek().setReduction(net.neoforged.neoforge.common.damagesource.DamageContainer.Reduction.MOB_EFFECTS, Math.max(0, damage));
            if (damage > 0 && damage < 3.4028235E37F && source.getEntity() instanceof net.minecraft.server.level.ServerPlayer player) {
                player.awardStat(net.minecraft.stats.Stats.DAMAGE_DEALT_RESISTED, Math.round(damage * 10));
            }
            return 0;
        }
        return super.getDamageAfterMagicAbsorb(source, damage);
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
    public boolean hurtServer(net.minecraft.server.level.ServerLevel level, DamageSource source, float damage) {
        if (!level().getBlockState(blockPosition()).is(Blocks.AIR)) {
            var look = getLookAngle();
            setDeltaMovement(look.x, 0, look.z);
            hurtMarked = true;
        }
        return !source.is(DamageTypes.DROWN) && super.hurtServer(level, source, damage);
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
    public void addAdditionalSaveData(net.minecraft.world.level.storage.ValueOutput output) {
        super.addAdditionalSaveData(output);
        output.putString("Texture", texture());
        output.putInt("PondlerJumpCooldown", jumpCooldown);
    }

    @Override
    public void readAdditionalSaveData(net.minecraft.world.level.storage.ValueInput input) {
        super.readAdditionalSaveData(input);
        if (input.getString("Texture").isPresent()) setTexture(input.getStringOr("Texture", ""));
        jumpCooldown = Math.max(0, Math.min(300, input.getIntOr("PondlerJumpCooldown", 0)));
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        var procedure = new LegacyAnimationController<>(this, "procedure", 4, state -> PlayState.STOP)
                .triggerableAnim("jump", RawAnimation.begin().thenPlay("jump"))
                .triggerableAnim("landidle", RawAnimation.begin().thenPlay("landidle"))
                .triggerableAnim("idle", RawAnimation.begin().thenPlay("idle"));
        controllers.add(new LegacyAnimationController<>(this, "movement", 4, state -> procedure.isPlayingTriggeredAnimation()
                ? PlayState.STOP : state.setAndContinue(isInWaterOrBubble() ? WATER_IDLE : LAND_IDLE)));
        controllers.add(procedure);
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return animations;
    }
}
