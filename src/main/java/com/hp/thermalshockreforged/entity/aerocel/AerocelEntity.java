package com.hp.thermalshockreforged.entity.aerocel;

import com.hp.thermalshockreforged.entity.base.AnimatedPathfinderEntity;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.*;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.*;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.*;
import net.minecraft.world.entity.ai.control.FlyingMoveControl;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.navigation.*;
import net.minecraft.world.entity.projectile.arrow.AbstractArrow;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public final class AerocelEntity extends AnimatedPathfinderEntity {
    public AerocelEntity(EntityType<? extends AerocelEntity> type, Level level) {
        super(type, level, "cloudercel", 1);
        moveControl = new FlyingMoveControl(this, 10, true);
    }
    public static AttributeSupplier.Builder attributes() {
        return Mob.createMobAttributes().add(Attributes.MAX_HEALTH, 120).add(Attributes.ARMOR, 7).add(Attributes.ATTACK_DAMAGE, 0)
            .add(Attributes.MOVEMENT_SPEED, .1).add(Attributes.FOLLOW_RANGE, 16).add(Attributes.FLYING_SPEED, .1).add(Attributes.STEP_HEIGHT, .6).add(Attributes.KNOCKBACK_RESISTANCE, 1);
    }
    @Override protected void registerGoals() {
        goalSelector.addGoal(1, new RandomStrollGoal(this, .8, 20) {
            @Override protected Vec3 getPosition() { return position().add((random.nextFloat() * 2 - 1) * 16, (random.nextFloat() * 2 - 1) * 16, (random.nextFloat() * 2 - 1) * 16); }
        });
        goalSelector.addGoal(2, new RandomLookAroundGoal(this));
    }
    @Override protected PathNavigation createNavigation(Level level) { return new FlyingPathNavigation(this, level); }
    @Override protected String movementAnimation(boolean moving) { return "idle"; }
    @Override protected List<String> procedureAnimations() { return List.of("idle"); }
    @Override public void setNoGravity(boolean ignored) { super.setNoGravity(true); }
    @Override public void aiStep() { super.aiStep(); setNoGravity(true); }
    @Override public boolean causeFallDamage(double distance, float multiplier, DamageSource source) { return false; }
    @Override protected void checkFallDamage(double distance, boolean grounded, BlockState state, BlockPos pos) { }
    @Override public boolean hurtServer(net.minecraft.server.level.ServerLevel level, DamageSource source, float amount) {
        return !(source.getDirectEntity() instanceof AbstractArrow || source.is(DamageTypes.FALL) || source.is(DamageTypes.DROWN) || source.is(DamageTypes.LIGHTNING_BOLT)) && super.hurtServer(level, source, amount);
    }
    @Override protected SoundEvent getAmbientSound() { return SoundEvents.COD_AMBIENT; }
    @Override protected SoundEvent getHurtSound(DamageSource source) { return SoundEvents.COD_HURT; }
    @Override protected SoundEvent getDeathSound() { return SoundEvents.COD_DEATH; }
    @Override protected void playStepSound(BlockPos pos, BlockState state) { playSound(SoundEvents.COD_FLOP, .15F, 1); }
    @Override public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty, EntitySpawnReason reason, SpawnGroupData group) {
        var result = super.finalizeSpawn(level, difficulty, reason, group);
        var destination = getBoundingBox().move(0, 5, 0);
        if (destination.minY >= level.getMinY() && destination.maxY <= (level.getMaxY() + 1)
            && level.hasChunksAt(BlockPos.containing(destination.minX, destination.minY, destination.minZ), BlockPos.containing(destination.maxX, destination.maxY, destination.maxZ))
            && level.isEmptyBlock(BlockPos.containing(getX(), getY() + 5, getZ())) && level.noCollision(this, destination)) teleportTo(getX(), getY() + 5, getZ());
        return result;
    }
}
