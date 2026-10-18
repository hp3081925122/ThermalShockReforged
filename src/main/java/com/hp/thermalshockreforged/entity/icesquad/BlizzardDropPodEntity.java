package com.hp.thermalshockreforged.entity.icesquad;

import com.hp.thermalshockreforged.ThermalShockReforged;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.*;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.*;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.*;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.*;
import net.minecraft.world.level.*;
import net.minecraft.world.phys.Vec3;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.*;
import software.bernie.geckolib.util.GeckoLibUtil;
import java.util.UUID;

public final class BlizzardDropPodEntity extends Monster implements GeoEntity {
    private static final EntityDataAccessor<String> TEXTURE = SynchedEntityData.defineId(BlizzardDropPodEntity.class, EntityDataSerializers.STRING);
    private final AnimatableInstanceCache animations = GeckoLibUtil.createInstanceCache(this);
    private boolean initialized;
    private boolean smokeSent;
    private boolean handedOff;
    private int deploymentAge;
    private Vec3 anchor = Vec3.ZERO;
    private String dimension = "";
    private long startedAt = Long.MIN_VALUE;
    private boolean attributesApplied;
    private boolean loadedFromSave;
    private boolean experiencePending;
    private UUID experienceAttacker;
    public BlizzardDropPodEntity(EntityType<? extends BlizzardDropPodEntity> type, Level level) { super(type, level); xpReward = 0; setPersistenceRequired(); applyConfiguredAttributes(); }
    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes().add(Attributes.MOVEMENT_SPEED, 0).add(Attributes.MAX_HEALTH, BastionCombatConfig.POD_HEALTH.getDefault()).add(Attributes.ARMOR, BastionCombatConfig.POD_ARMOR.getDefault())
                .add(Attributes.ATTACK_DAMAGE, 0).add(Attributes.FOLLOW_RANGE, 16).add(Attributes.KNOCKBACK_RESISTANCE, 1).add(Attributes.STEP_HEIGHT, 0);
    }
    void applyConfiguredAttributes() {
        if (level().isClientSide || attributesApplied || !BastionCombatConfig.SPEC.isLoaded()) return;
        float health = getHealth(); getAttribute(Attributes.MAX_HEALTH).setBaseValue(BastionCombatConfig.POD_HEALTH.get()); getAttribute(Attributes.ARMOR).setBaseValue(BastionCombatConfig.POD_ARMOR.get());
        setHealth(loadedFromSave ? Math.min(health, getMaxHealth()) : getMaxHealth()); attributesApplied = true;
    }
    @Override protected void defineSynchedData(SynchedEntityData.Builder builder) { super.defineSynchedData(builder); builder.define(TEXTURE, "blizzarddroppod"); }
    public String texture() { return entityData.get(TEXTURE); }
    public void setTexture(String texture) { entityData.set(TEXTURE, texture); }
    public void setAnimation(String animation) { if (!"empty".equals(animation) && !"undefined".equals(animation)) triggerAnim("procedure", animation); }
    @Override public boolean removeWhenFarAway(double distance) { return false; }
    @Override public boolean hurt(DamageSource source, float amount) {
        return !source.is(DamageTypes.IN_FIRE) && !(source.getDirectEntity() instanceof AbstractArrow) && !(source.getDirectEntity() instanceof Player)
                && !(source.getDirectEntity() instanceof ThrownPotion) && !(source.getDirectEntity() instanceof AreaEffectCloud)
                && !source.is(DamageTypes.FALL) && !source.is(DamageTypes.CACTUS) && !source.is(DamageTypes.DROWN) && !source.is(DamageTypes.LIGHTNING_BOLT)
                && !source.is(DamageTypes.EXPLOSION) && !source.is(DamageTypes.TRIDENT) && !source.is(DamageTypes.FALLING_ANVIL)
                && !source.is(DamageTypes.DRAGON_BREATH) && !source.is(DamageTypes.WITHER) && !source.is(DamageTypes.WITHER_SKULL) && super.hurt(source, amount);
    }
    @Override public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty, MobSpawnType reason, SpawnGroupData group) {
        var result = super.finalizeSpawn(level, difficulty, reason, group);
        if (!level.isClientSide() && !initialized && !handedOff) {
            initialized = true; anchor = position(); dimension = level().dimension().location().toString(); startedAt = level().getGameTime(); deploymentAge = 0;
        }
        return result;
    }
    @Override public void tick() {
        if (!level().isClientSide) applyConfiguredAttributes();
        super.tick();
        if (!(level() instanceof ServerLevel server)) return;
        if (handedOff) { discard(); return; }
        if (!initialized) return;
        if (!isAlive() || !dimension.equals(server.dimension().location().toString())) { initialized = false; return; }
        if (server.getGameTime() == startedAt) return;
        int age = ++deploymentAge;
        if (age >= 15 && !smokeSent) {
            smokeSent = true;
            server.sendParticles(IceSquadContent.FOG_CLOUD.get(), anchor.x + 0.5, anchor.y, anchor.z + 0.5, 25, 2, 2, 2, 0);
            server.playSound(null, BlockPos.containing(anchor), SoundEvents.ANVIL_PLACE, SoundSource.HOSTILE, 2, -4);
        }
        if (age >= 25) {
            handedOff = true;
            server.playSound(null, BlockPos.containing(anchor), SoundEvents.DRAGON_FIREBALL_EXPLODE, SoundSource.HOSTILE, 1, 2);
            server.playSound(null, BlockPos.containing(anchor), BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, "magnetcrossbowfire")), SoundSource.HOSTILE, 1, -3);
            if (!DeploymentTaskEntity.submit(server, anchor, getUUID(), false)) ThermalShockReforged.LOGGER.debug("Blizzard final deployment rejected: pod={}, anchor={}", getUUID(), anchor);
            discard();
        }
    }
    @Override protected void dropExperience(Entity attacker) { experiencePending = true; experienceAttacker = attacker == null ? null : attacker.getUUID(); }
    @Override protected void tickDeath() {
        if (++deathTime >= 20 && !isRemoved()) {
            remove(RemovalReason.KILLED);
            if (experiencePending && level() instanceof ServerLevel server) { experiencePending = false; super.dropExperience(experienceAttacker == null ? null : server.getEntity(experienceAttacker)); }
        }
    }
    @Override public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag); tag.putString("Texture", texture()); tag.putBoolean("PodInitialized", initialized); tag.putBoolean("PodSmokeSent", smokeSent); tag.putBoolean("PodHandedOff", handedOff); tag.putInt("PodAge", deploymentAge); tag.putLong("PodStartedAt", startedAt);
        tag.putDouble("PodAnchorX", anchor.x); tag.putDouble("PodAnchorY", anchor.y); tag.putDouble("PodAnchorZ", anchor.z); tag.putString("PodDimension", dimension);
        tag.putBoolean("PodExperiencePending", experiencePending); if (experienceAttacker != null) tag.putUUID("PodExperienceAttacker", experienceAttacker);
    }
    @Override public void readAdditionalSaveData(CompoundTag tag) {
        loadedFromSave = true; super.readAdditionalSaveData(tag); attributesApplied = false; applyConfiguredAttributes(); setPersistenceRequired(); if (tag.contains("Texture")) setTexture(tag.getString("Texture"));
        initialized = tag.getBoolean("PodInitialized"); smokeSent = tag.getBoolean("PodSmokeSent"); handedOff = tag.getBoolean("PodHandedOff"); deploymentAge = Math.clamp(tag.getInt("PodAge"), 0, 25);
        anchor = new Vec3(tag.getDouble("PodAnchorX"), tag.getDouble("PodAnchorY"), tag.getDouble("PodAnchorZ")); dimension = tag.getString("PodDimension"); startedAt = tag.contains("PodStartedAt") ? tag.getLong("PodStartedAt") : Long.MIN_VALUE;
        if (!Double.isFinite(anchor.x) || !Double.isFinite(anchor.y) || !Double.isFinite(anchor.z)) initialized = false;
        experiencePending = tag.getBoolean("PodExperiencePending"); experienceAttacker = tag.hasUUID("PodExperienceAttacker") ? tag.getUUID("PodExperienceAttacker") : null;
    }
    @Override public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        var procedure = new AnimationController<>(this, "procedure", 4, state -> PlayState.STOP).triggerableAnim("spawn", RawAnimation.begin().thenPlay("spawn")).triggerableAnim("idle", RawAnimation.begin().thenPlay("idle"));
        controllers.add(new AnimationController<>(this, "movement", 4, state -> procedure.isPlayingTriggeredAnimation() ? PlayState.STOP : state.setAndContinue(RawAnimation.begin().thenLoop("spawn")))); controllers.add(procedure);
    }
    @Override public AnimatableInstanceCache getAnimatableInstanceCache() { return animations; }
}
