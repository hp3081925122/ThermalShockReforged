package com.hp.thermalshockreforged.entity.sanctum;
import com.geckolib.animation.object.PlayState;
import com.hp.thermalshockreforged.compat.LegacyAnimationController;
import com.geckolib.animatable.manager.AnimatableManager;

import com.hp.thermalshockreforged.ThermalShockReforged;
import com.hp.thermalshockreforged.content.atlas.AtlasContent;
import com.hp.thermalshockreforged.entity.attack.BossDeathEffectEntity;
import com.hp.thermalshockreforged.entity.mangler.ManglerContent;
import com.hp.thermalshockreforged.world.sanctum.PylonBuildingLock;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.*;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.*;
import net.minecraft.world.*;
import net.minecraft.world.damagesource.*;
import net.minecraft.world.effect.*;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.*;
import net.minecraft.world.entity.ai.control.FlyingMoveControl;
import net.minecraft.world.entity.ai.navigation.*;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.*;
import com.geckolib.animatable.GeoEntity;
import com.geckolib.animatable.instance.AnimatableInstanceCache;
import com.geckolib.animation.*;
import com.geckolib.util.GeckoLibUtil;

public final class GuardianPylonEntity extends Monster implements GeoEntity {
    private static final EntityDataAccessor<String> TEXTURE = SynchedEntityData.defineId(GuardianPylonEntity.class, EntityDataSerializers.STRING);
    private final AnimatableInstanceCache animations = GeckoLibUtil.createInstanceCache(this);
    private boolean activated;
    private boolean deathVisualStarted;
    private boolean experiencePending;
    private UUID experienceAttacker;
    public GuardianPylonEntity(EntityType<? extends GuardianPylonEntity> type, Level level) {
        super(type, level); xpReward = 0; setPersistenceRequired(); setNoGravity(true); moveControl = new FlyingMoveControl(this, 10, true);
    }
    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes().add(Attributes.MOVEMENT_SPEED, 0).add(Attributes.MAX_HEALTH, 330).add(Attributes.ARMOR, 0)
                .add(Attributes.ATTACK_DAMAGE, 0).add(Attributes.FOLLOW_RANGE, 16).add(Attributes.FLYING_SPEED, 0).add(Attributes.STEP_HEIGHT, 0);
    }
    @Override protected void defineSynchedData(SynchedEntityData.Builder builder) { super.defineSynchedData(builder); builder.define(TEXTURE, "surge_pylon"); }
    public String texture() { return entityData.get(TEXTURE); }
    public void setTexture(String texture) { entityData.set(TEXTURE, texture); }
    @Override protected PathNavigation createNavigation(Level level) { return new FlyingPathNavigation(this, level); }
    @Override public boolean removeWhenFarAway(double distance) { return false; }
    @Override public boolean causeFallDamage(double distance, float multiplier, DamageSource source) { return false; }
    @Override protected void checkFallDamage(double y, boolean onGround, BlockState state, BlockPos pos) { }
    @Override public void setNoGravity(boolean ignored) { super.setNoGravity(true); }
    @Override public void aiStep() { super.aiStep(); setNoGravity(true); }
    @Override public void baseTick() {
        super.baseTick();
        makeStuckInBlock(Blocks.AIR.defaultBlockState(), new Vec3(.25, .05, .25));
        if (level() instanceof ServerLevel server) {
            Vec3 center = position().add(0, 22, 0);
            for (Player player : server.getEntitiesOfClass(Player.class, new AABB(center, center).inflate(40), entity -> true)) PylonBuildingLock.refresh(player);
        }
    }
    @Override public boolean hurtServer(net.minecraft.server.level.ServerLevel level, DamageSource source, float amount) {
        if (!level().isClientSide()) addEffect(new MobEffectInstance(MobEffects.REGENERATION, 20, 199));
        return !source.is(DamageTypes.IN_FIRE) && !source.is(DamageTypes.DROWN) && super.hurtServer(level, source, amount);
    }
    @Override public InteractionResult mobInteract(Player player, InteractionHand hand) {
        super.mobInteract(player, hand);
        if (level() instanceof ServerLevel server && isAlive() && player.getMainHandItem().is(AtlasContent.VIRUSTONE.get())) {
            if (!activated) {
                if (!PylonActivationEntity.submit(server, position())) {
                    ThermalShockReforged.LOGGER.warn("Guardian pylon activation could not be scheduled: pylon={}, player={}", getUUID(), player.getUUID());
                    return InteractionResult.SUCCESS;
                }
                activated = true;
                ThermalShockReforged.LOGGER.debug("Guardian pylon activation committed: pylon={}, player={}, position={}, dimension={}", getUUID(), player.getUUID(), position(), server.dimension().identifier());
            }
            server.playSound(null, blockPosition(), SoundEvents.ENDER_DRAGON_DEATH, SoundSource.AMBIENT, 1, 3);
            hurt(damageSources().generic(), 330);
        }
        return InteractionResult.SUCCESS;
    }
    @Override public void die(DamageSource source) {
        super.die(source);
        if (dead && !deathVisualStarted && level() instanceof ServerLevel server) {
            deathVisualStarted = true;
            BossDeathEffectEntity.submit(server, ManglerContent.HERALD_DEATH_EFFECT.get(), position(), getEyeHeight());
        }
    }
    @Override protected void dropExperience(net.minecraft.server.level.ServerLevel level, Entity attacker) { experiencePending = true; experienceAttacker = attacker == null ? null : attacker.getUUID(); }
    @Override protected void tickDeath() {
        if (++deathTime >= 60 && !isRemoved()) {
            remove(RemovalReason.KILLED);
            if (level() instanceof ServerLevel server) {
                if (experiencePending) { experiencePending = false; super.dropExperience(server, experienceAttacker == null ? null : server.getEntity(experienceAttacker)); }
                server.sendParticles(ParticleTypes.END_ROD, getX(), getY(), getZ(), 5, 1.5, 1.5, 1.5, .5);
                if (random.nextDouble() < .4) server.playSound(null, blockPosition(), SoundEvents.CONDUIT_AMBIENT_SHORT, SoundSource.NEUTRAL, 1, 1);
            }
        }
    }
    @Override public void addAdditionalSaveData(net.minecraft.world.level.storage.ValueOutput output) {
        super.addAdditionalSaveData(output); output.putString("Texture", texture()); output.putBoolean("PylonActivated", activated); output.putBoolean("PylonDeathVisualStarted", deathVisualStarted);
        output.putBoolean("PylonExperiencePending", experiencePending); if (experienceAttacker != null) output.putString("PylonExperienceAttacker", experienceAttacker.toString());
    }
    @Override public void readAdditionalSaveData(net.minecraft.world.level.storage.ValueInput input) {
        super.readAdditionalSaveData(input); if (input.getString("Texture").isPresent()) setTexture(input.getStringOr("Texture", ""));
activated = input.getBooleanOr("PylonActivated", false); deathVisualStarted = input.getBooleanOr("PylonDeathVisualStarted", false); experiencePending = input.getBooleanOr("PylonExperiencePending", false);
        experienceAttacker = input.getString("PylonExperienceAttacker").map(java.util.UUID::fromString).orElse(null);
    }
    @Override public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        var procedure = new LegacyAnimationController<>(this, "procedure", 4, state -> PlayState.STOP).triggerableAnim("0", RawAnimation.begin().thenPlay("0"));
        controllers.add(new LegacyAnimationController<>(this, "movement", 4, state -> procedure.isPlayingTriggeredAnimation() ? PlayState.STOP : state.setAndContinue(RawAnimation.begin().thenLoop("0"))));
        controllers.add(procedure);
    }
    @Override public AnimatableInstanceCache getAnimatableInstanceCache() { return animations; }
}
