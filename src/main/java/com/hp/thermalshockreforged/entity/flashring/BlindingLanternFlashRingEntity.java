package com.hp.thermalshockreforged.entity.flashring;

import com.hp.thermalshockreforged.entity.base.AnimatedMonsterEntity;
import com.hp.thermalshockreforged.world.timing.*;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.*;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.*;
import net.minecraft.world.entity.ai.control.FlyingMoveControl;
import net.minecraft.world.entity.ai.navigation.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.*;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.state.BlockState;

public final class BlindingLanternFlashRingEntity extends AnimatedMonsterEntity implements ServerEndTickTask {
    private boolean initialized;
    private int remaining;
    public BlindingLanternFlashRingEntity(EntityType<? extends BlindingLanternFlashRingEntity> type, Level level) { super(type, level, "yellow_light_flash", 0); moveControl = new FlyingMoveControl(this, 10, true); setPersistenceRequired(); }
    public static AttributeSupplier.Builder createAttributes() { return Mob.createMobAttributes().add(Attributes.MAX_HEALTH, 1).add(Attributes.ARMOR, 0).add(Attributes.ATTACK_DAMAGE, 0).add(Attributes.MOVEMENT_SPEED, 0).add(Attributes.FLYING_SPEED, 0).add(Attributes.FOLLOW_RANGE, 16).add(Attributes.STEP_HEIGHT, 0); }
    @Override protected PathNavigation createNavigation(Level level) { return new FlyingPathNavigation(this, level); }
    @Override public SpawnGroupData finalizeSpawn(ServerLevelAccessor world, DifficultyInstance difficulty, MobSpawnType reason, SpawnGroupData group) {
        var result = super.finalizeSpawn(world, difficulty, reason, group);
        if (!initialized) { initialized = true; remaining = 10; if (level() instanceof ServerLevel server) ServerEndTickTasks.schedule(server, getUUID()); }
        return result;
    }
    @Override public void onAddedToLevel() { super.onAddedToLevel(); if (initialized && level() instanceof ServerLevel server) ServerEndTickTasks.schedule(server, getUUID()); }
    @Override public void endTick(ServerLevel server) { if (isRemoved() || !initialized) return; if (remaining <= 0 || --remaining == 0) discard(); else ServerEndTickTasks.schedule(server, getUUID()); }
    @Override public boolean removeWhenFarAway(double distance) { return false; }
    @Override public void setNoGravity(boolean value) { super.setNoGravity(true); }
    @Override public void aiStep() { super.aiStep(); setNoGravity(true); }
    @Override public boolean causeFallDamage(float distance, float multiplier, DamageSource source) { return false; }
    @Override protected void checkFallDamage(double y, boolean onGround, BlockState state, BlockPos pos) { }
    @Override public boolean isPushable() { return false; }
    @Override protected void doPush(Entity entity) { }
    @Override protected void pushEntities() { }
    @Override public boolean hurt(DamageSource source, float amount) {

        if (source.getDirectEntity() instanceof AbstractArrow || source.getDirectEntity() instanceof Player || source.getDirectEntity() instanceof ThrownPotion || source.getDirectEntity() instanceof AreaEffectCloud) return false;
        if (source.is(DamageTypes.IN_FIRE) || source.is(DamageTypes.FALL) || source.is(DamageTypes.CACTUS) || source.is(DamageTypes.DROWN) || source.is(DamageTypes.LIGHTNING_BOLT) || source.is(DamageTypes.EXPLOSION) || source.is(DamageTypes.TRIDENT) || source.is(DamageTypes.FALLING_ANVIL) || source.is(DamageTypes.DRAGON_BREATH) || source.is(DamageTypes.WITHER) || source.is(DamageTypes.WITHER_SKULL)) return false;
        return super.hurt(source, amount);
    }
    @Override protected String movementAnimation(boolean moving) { return "spinburst"; }
    @Override protected List<String> procedureAnimations() { return List.of("spinburst"); }
    @Override public void addAdditionalSaveData(CompoundTag tag) { super.addAdditionalSaveData(tag); tag.putBoolean("FlashRingInitialized", initialized); tag.putInt("FlashRingRemaining", remaining); }
    @Override public void readAdditionalSaveData(CompoundTag tag) { super.readAdditionalSaveData(tag); initialized = tag.getBoolean("FlashRingInitialized"); remaining = Math.clamp(tag.getInt("FlashRingRemaining"), 0, 10); }
}
