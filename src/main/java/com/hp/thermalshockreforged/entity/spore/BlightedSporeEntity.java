package com.hp.thermalshockreforged.entity.spore;
import com.geckolib.animation.object.PlayState;
import com.hp.thermalshockreforged.compat.LegacyAnimationController;
import com.geckolib.animatable.manager.AnimatableManager;

import com.hp.thermalshockreforged.content.status.StatusContent;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.*;
import net.minecraft.world.damagesource.*;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.*;
import net.minecraft.world.entity.ai.control.FlyingMoveControl;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.navigation.*;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraft.sounds.*;
import com.geckolib.animatable.GeoEntity;
import com.geckolib.animatable.instance.AnimatableInstanceCache;
import com.geckolib.animation.*;
import com.geckolib.util.GeckoLibUtil;

public final class BlightedSporeEntity extends Monster implements GeoEntity {
    private static final EntityDataAccessor<String> TEXTURE = SynchedEntityData.defineId(BlightedSporeEntity.class, EntityDataSerializers.STRING);
    private final AnimatableInstanceCache animations = GeckoLibUtil.createInstanceCache(this);
    public BlightedSporeEntity(EntityType<? extends BlightedSporeEntity> type, Level level) {
        super(type, level);
        xpReward = 1;
        moveControl = new FlyingMoveControl(this, 10, true);
    }
    public static AttributeSupplier.Builder createAttributes() { return Mob.createMobAttributes().add(Attributes.MOVEMENT_SPEED, 0.5).add(Attributes.MAX_HEALTH, 1).add(Attributes.ARMOR, 0).add(Attributes.ATTACK_DAMAGE, 1).add(Attributes.FOLLOW_RANGE, 16).add(Attributes.STEP_HEIGHT, 0.6).add(Attributes.FLYING_SPEED, 0.5); }
    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) { super.defineSynchedData(builder); builder.define(TEXTURE, "blighted_spore"); }
    public String texture() { return entityData.get(TEXTURE); }
    public void setTexture(String value) { entityData.set(TEXTURE, value); }
    public void setAnimation(String value) { triggerAnim("procedure", value); }
    @Override
    protected PathNavigation createNavigation(Level level) { return new FlyingPathNavigation(this, level); }
    @Override
    protected void registerGoals() {
        goalSelector.addGoal(1, new MeleeAttackGoal(this, 1.2, false) { @Override protected boolean canPerformAttack(LivingEntity target) { return isTimeToAttack() && isWithinMeleeAttackRange(target); } });
        goalSelector.addGoal(2, new RandomStrollGoal(this, 0.8, 20) { @Override protected Vec3 getPosition() { return position().add((random.nextFloat() * 2 - 1) * 16, (random.nextFloat() * 2 - 1) * 16, (random.nextFloat() * 2 - 1) * 16); } });
        goalSelector.addGoal(3, new RandomLookAroundGoal(this));
    }
    @Override
    public boolean isWithinMeleeAttackRange(LivingEntity target) { return distanceToSqr(target) <= getBbWidth() * getBbWidth() + target.getBbWidth(); }
    @Override
    public void baseTick() {
        super.baseTick();
        if (level().isClientSide() || level().getNearestPlayer(getX(), getY(), getZ(), 5, true) == null || level().getServer() == null) return;
        Player nearest = null;
        double distance = Double.MAX_VALUE;
        for (var player : level().getServer().getPlayerList().getPlayers()) {
            double candidate = player.distanceToSqr(position());
            if (candidate < distance) { distance = candidate; nearest = player; }
        }
        if (nearest == null) return;
        lookAt(net.minecraft.commands.arguments.EntityAnchorArgument.Anchor.FEET, nearest.position());
        yBodyRot = getYRot(); yHeadRot = getYRot();
        setDeltaMovement(getLookAngle());
    }
    @Override
    public void playerTouch(Player player) {
        super.playerTouch(player);
        if (level().isClientSide()) return;
        player.addEffect(new MobEffectInstance(StatusContent.INFECTED, 360, 0));
        if (random.nextDouble() < 0.3) level().playSound(null, blockPosition(), SoundEvents.SLIME_HURT, SoundSource.NEUTRAL, 1, 5);
    }
    @Override
    public SoundEvent getAmbientSound() { return SoundEvents.CONDUIT_AMBIENT; }
    @Override
    protected SoundEvent getHurtSound(DamageSource source) { return SoundEvents.SCULK_CATALYST_BREAK; }
    @Override
    protected SoundEvent getDeathSound() { return SoundEvents.SCULK_CATALYST_BREAK; }
    @Override
    public boolean hurtServer(net.minecraft.server.level.ServerLevel level, DamageSource source, float amount) { return !source.is(DamageTypes.IN_FIRE) && !source.is(DamageTypes.FALL) && super.hurtServer(level, source, amount); }
    @Override
    public boolean causeFallDamage(double distance, float multiplier, DamageSource source) { return false; }
    @Override
    protected void checkFallDamage(double distance, boolean ground, net.minecraft.world.level.block.state.BlockState state, net.minecraft.core.BlockPos pos) { }
    @Override
    public void setNoGravity(boolean ignored) { super.setNoGravity(true); }
    @Override
    public void aiStep() { super.aiStep(); setNoGravity(true); }
    @Override
    public void addAdditionalSaveData(net.minecraft.world.level.storage.ValueOutput output) { super.addAdditionalSaveData(output); output.putString("Texture", texture()); }
    @Override
    public void readAdditionalSaveData(net.minecraft.world.level.storage.ValueInput input) { super.readAdditionalSaveData(input); if (input.getString("Texture").isPresent()) setTexture(input.getStringOr("Texture", "")); }
    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        var procedure = new LegacyAnimationController<>(this, "procedure", 4, state -> PlayState.STOP)
                .triggerableAnim("idle", RawAnimation.begin().thenPlay("idle"))
                .triggerableAnim("heartattack", RawAnimation.begin().thenPlay("heartattack"))
                .triggerableAnim("heartattackloop", RawAnimation.begin().thenPlay("heartattackloop"));
        controllers.add(new LegacyAnimationController<>(this, "movement", 4, state -> {
            if (procedure.isPlayingTriggeredAnimation()) return PlayState.STOP;
            if ((state.isMoving() || Math.abs(state.getLimbSwingAmount()) >= 0.15F) && onGround()) return state.setAndContinue(RawAnimation.begin().thenLoop("idle"));
            return state.setAndContinue(isDeadOrDying() ? RawAnimation.begin().thenPlay("heartattackloop") : RawAnimation.begin().thenLoop("idle"));
        }));
        controllers.add(procedure);
    }
    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() { return animations; }
}
