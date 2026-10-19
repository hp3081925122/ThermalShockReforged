package com.hp.thermalshockreforged.entity.liftbug;

import com.hp.thermalshockreforged.entity.base.AnimatedPathfinderEntity;
import java.util.List;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.*;
import net.minecraft.world.*;
import net.minecraft.world.damagesource.*;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.*;
import net.minecraft.world.entity.ai.control.FlyingMoveControl;
import net.minecraft.world.entity.ai.navigation.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public final class LiftbugEntity extends AnimatedPathfinderEntity {
    public LiftbugEntity(EntityType<? extends LiftbugEntity> type, Level level) {
        super(type, level, "liftbug", 0);
        setPersistenceRequired();
        moveControl = new FlyingMoveControl(this, 10, true);
    }
    public static AttributeSupplier.Builder attributes() {
        return Mob.createMobAttributes().add(Attributes.MAX_HEALTH, 2).add(Attributes.ARMOR, 0).add(Attributes.ATTACK_DAMAGE, 0)
            .add(Attributes.MOVEMENT_SPEED, 0).add(Attributes.FOLLOW_RANGE, 16).add(Attributes.FLYING_SPEED, 0).add(Attributes.STEP_HEIGHT, 0);
    }
    @Override protected PathNavigation createNavigation(Level level) { return new FlyingPathNavigation(this, level); }
    @Override protected String movementAnimation(boolean moving) { return "idle"; }
    @Override protected List<String> procedureAnimations() { return List.of("idle"); }
    @Override public boolean removeWhenFarAway(double distance) { return false; }
    @Override public boolean canCollideWith(Entity other) { return true; }
    @Override public boolean canBeCollidedWith() { return true; }
    @Override public boolean isPushable() { return false; }
    @Override protected void doPush(Entity other) { }
    @Override protected void pushEntities() { }
    @Override public void setNoGravity(boolean ignored) { super.setNoGravity(true); }
    @Override public void aiStep() { super.aiStep(); setNoGravity(true); }
    @Override public void baseTick() { super.baseTick(); setDeltaMovement(0, isShiftKeyDown() ? .1 : -.1, 0); }
    @Override public boolean causeFallDamage(float distance, float multiplier, DamageSource source) { return false; }
    @Override protected void checkFallDamage(double distance, boolean grounded, BlockState state, BlockPos pos) { }
    @Override public boolean hurt(DamageSource source, float amount) { return !source.is(DamageTypes.FALL) && super.hurt(source, amount); }
    @Override protected SoundEvent getHurtSound(DamageSource source) { return SoundEvents.GENERIC_HURT; }
    @Override protected SoundEvent getDeathSound() { return SoundEvents.GENERIC_DEATH; }
    @Override public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty, MobSpawnType reason, SpawnGroupData group) {
        var result = super.finalizeSpawn(level, difficulty, reason, group);
        lookAt(EntityAnchorArgument.Anchor.EYES, position());
        return result;
    }
    @Override protected InteractionResult mobInteract(Player player, InteractionHand hand) {
        var parent = super.mobInteract(player, hand);
        if (parent.consumesAction()) return parent;
        if (!isAlive() || hand != InteractionHand.MAIN_HAND) return parent;
        if (level().isClientSide) return InteractionResult.SUCCESS;
        if (player.getVehicle() != this && !player.startRiding(this)) return InteractionResult.PASS;
        if (player.getVehicle() != this || getFirstPassenger() != player) return InteractionResult.PASS;
        setShiftKeyDown(!isShiftKeyDown());
        level().playSound(null, blockPosition(), SoundEvents.NOTE_BLOCK_BELL.value(), SoundSource.NEUTRAL, 1, isShiftKeyDown() ? 5 : 1);
        hurtMarked = true;
        return InteractionResult.CONSUME;
    }
    @Override protected Vec3 getPassengerAttachmentPoint(Entity passenger, EntityDimensions dimensions, float scale) { return new Vec3(0, dimensions.height() * .75, 0); }
    @Override public void addAdditionalSaveData(CompoundTag tag) { super.addAdditionalSaveData(tag); tag.putBoolean("LiftbugAscending", isShiftKeyDown()); }
    @Override public void readAdditionalSaveData(CompoundTag tag) { super.readAdditionalSaveData(tag); setShiftKeyDown(tag.getBoolean("LiftbugAscending")); }
}
