package com.hp.thermalshockreforged.entity.beetles;
import net.minecraft.world.entity.projectile.arrow.AbstractArrow;
import net.minecraft.world.entity.projectile.throwableitemprojectile.AbstractThrownPotion;
import com.hp.thermalshockreforged.compat.LegacyAnimationController;
import com.geckolib.animatable.manager.AnimatableManager;

import com.hp.thermalshockreforged.ThermalShockReforged;
import java.util.*;
import net.minecraft.core.*;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.*;
import net.minecraft.server.level.*;
import net.minecraft.sounds.*;
import net.minecraft.world.damagesource.*;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.*;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.AABB;
import com.geckolib.animatable.GeoEntity;
import com.geckolib.animatable.instance.AnimatableInstanceCache;
import com.geckolib.animation.*;
import com.geckolib.util.GeckoLibUtil;

public final class DormantBeetleCrasherEntity extends PathfinderMob implements GeoEntity {
    private static final EntityDataAccessor<String> TEXTURE = SynchedEntityData.defineId(DormantBeetleCrasherEntity.class, EntityDataSerializers.STRING);
    private final AnimatableInstanceCache animations = GeckoLibUtil.createInstanceCache(this);
    private boolean converted;
    private int retryDelay;
    private boolean experiencePending;
    private UUID experienceAttacker;
    public DormantBeetleCrasherEntity(EntityType<? extends DormantBeetleCrasherEntity> type, Level level) { super(type, level); xpReward = 2; setPersistenceRequired(); }
    public static AttributeSupplier.Builder createAttributes() { return Mob.createMobAttributes().add(Attributes.MAX_HEALTH, 2).add(Attributes.ARMOR, 0).add(Attributes.ATTACK_DAMAGE, 0).add(Attributes.MOVEMENT_SPEED, 0).add(Attributes.FOLLOW_RANGE, 16).add(Attributes.STEP_HEIGHT, .6); }
    @Override protected void defineSynchedData(SynchedEntityData.Builder builder) { super.defineSynchedData(builder); builder.define(TEXTURE, "texturespringy"); }
    public String texture() { return entityData.get(TEXTURE); }
    @Override public void baseTick() {
        super.baseTick();
        if (!(level() instanceof ServerLevel server) || !isAlive() || converted) return;
        if (retryDelay > 0) { retryDelay--; return; }
        var nearby = server.getEntitiesOfClass(Player.class, AABB.ofSize(position(), 18, 18, 18), player -> true);
        if (nearby.isEmpty()) return;
        if (random.nextDouble() < .05) server.levelEvent(2001, blockPosition(), Block.getId(server.getBlockState(BlockPos.containing(getX(), getY() - 1, getZ()))));
        AABB trigger = AABB.ofSize(position(), 10, 10, 10);
        Player closest = null;
        double distance = Double.MAX_VALUE;
        for (var player : nearby) if (player.getBoundingBox().intersects(trigger) && player.distanceToSqr(this) < distance) { closest = player; distance = player.distanceToSqr(this); }
        if (!(closest instanceof ServerPlayer player) || player.gameMode.getGameModeForPlayer() != GameType.SURVIVAL) return;
        var replacement = BeetleContent.CRASHER.get().create(server, null, blockPosition(), EntitySpawnReason.MOB_SUMMONED, false, false);
        if (replacement == null || replacement.isRemoved() || replacement.isSpawnCancelled() || !server.addFreshEntity(replacement)) { retryDelay = 20; ThermalShockReforged.LOGGER.debug("Dormant beetle conversion rejected: source={}, position={}", getUUID(), blockPosition()); return; }
        converted = true;
        replacement.setDeltaMovement(0, .3, 0);
        replacement.setDeltaMovement(replacement.getDeltaMovement());
        server.playSound(null, blockPosition(), SoundEvents.WITHER_BREAK_BLOCK, SoundSource.NEUTRAL, .5F, 0);
        server.sendParticles(ParticleTypes.POOF, getX(), getY(), getZ(), 30, .8, 1, .8, .2);
        ThermalShockReforged.LOGGER.debug("Dormant beetle converted: source={}, replacement={}, position={}", getUUID(), replacement.getUUID(), blockPosition());
        discard();
    }
    @Override public boolean hurtServer(net.minecraft.server.level.ServerLevel level, DamageSource source, float amount) { return !(source.is(DamageTypes.IN_FIRE) || source.getDirectEntity() instanceof AbstractArrow || source.getDirectEntity() instanceof Player || source.getDirectEntity() instanceof AbstractThrownPotion || source.getDirectEntity() instanceof AreaEffectCloud || source.is(DamageTypes.FALL) || source.is(DamageTypes.CACTUS) || source.is(DamageTypes.DROWN) || source.is(DamageTypes.LIGHTNING_BOLT) || source.is(DamageTypes.EXPLOSION) || source.is(DamageTypes.TRIDENT) || source.is(DamageTypes.FALLING_ANVIL) || source.is(DamageTypes.DRAGON_BREATH) || source.is(DamageTypes.WITHER) || source.is(DamageTypes.WITHER_SKULL)) && super.hurtServer(level, source, amount); }
    @Override public boolean isPushable() { return false; }
    @Override protected void doPush(Entity entity) { }
    @Override protected void pushEntities() { }
    @Override public boolean removeWhenFarAway(double distance) { return false; }
    @Override public void aiStep() { super.aiStep(); updateSwingTime(); }
    @Override protected void dropExperience(net.minecraft.server.level.ServerLevel level, Entity attacker) { experiencePending = true; experienceAttacker = attacker == null ? null : attacker.getUUID(); }
    @Override protected void tickDeath() { if (++deathTime >= 40 && !isRemoved()) { remove(RemovalReason.KILLED); if (experiencePending && level() instanceof ServerLevel server) { experiencePending = false; super.dropExperience(server, experienceAttacker == null ? null : server.getEntity(experienceAttacker)); } } }
    @Override public void addAdditionalSaveData(net.minecraft.world.level.storage.ValueOutput output) { super.addAdditionalSaveData(output); output.putString("Texture", texture()); output.putBoolean("BeetleConverted", converted); output.putInt("BeetleRetryDelay", retryDelay); output.putBoolean("BeetleExperiencePending", experiencePending); if (experienceAttacker != null) output.putString("BeetleExperienceAttacker", experienceAttacker.toString()); }
    @Override public void readAdditionalSaveData(net.minecraft.world.level.storage.ValueInput input) { super.readAdditionalSaveData(input); if (input.getString("Texture").isPresent()) entityData.set(TEXTURE, input.getStringOr("Texture", "")); converted = input.getBooleanOr("BeetleConverted", false); retryDelay = Math.clamp(input.getIntOr("BeetleRetryDelay", 0), 0, 20); experiencePending = input.getBooleanOr("BeetleExperiencePending", false); experienceAttacker = input.getString("BeetleExperienceAttacker").map(java.util.UUID::fromString).orElse(null); setPersistenceRequired(); }
    @Override public void registerControllers(AnimatableManager.ControllerRegistrar controllers) { controllers.add(new LegacyAnimationController<>(this, "movement", 4, state -> state.setAndContinue(RawAnimation.begin().thenLoop("idle")))); }
    @Override public AnimatableInstanceCache getAnimatableInstanceCache() { return animations; }
}
