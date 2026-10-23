package com.hp.thermalshockreforged.entity.icesquad;
import net.minecraft.world.entity.projectile.arrow.AbstractArrow;
import net.minecraft.world.entity.projectile.throwableitemprojectile.AbstractThrownPotion;
import com.geckolib.animation.object.PlayState;
import com.hp.thermalshockreforged.compat.LegacyAnimationController;
import com.geckolib.animatable.manager.AnimatableManager;

import com.hp.thermalshockreforged.ThermalShockReforged;
import com.hp.thermalshockreforged.world.timing.ServerEndTickTask;
import com.hp.thermalshockreforged.world.timing.ServerEndTickTasks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.*;
import net.minecraft.resources.Identifier;
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
import com.geckolib.animatable.GeoEntity;
import com.geckolib.animatable.instance.AnimatableInstanceCache;
import com.geckolib.animation.*;
import com.geckolib.util.GeckoLibUtil;
import java.util.UUID;

public final class BlizzardDropPodEntity extends Monster implements GeoEntity, ServerEndTickTask {
    private static final EntityDataAccessor<String> TEXTURE = SynchedEntityData.defineId(BlizzardDropPodEntity.class, EntityDataSerializers.STRING);
    private final AnimatableInstanceCache animations = GeckoLibUtil.createInstanceCache(this);
    private boolean initialized;
    private boolean smokeSent;
    private boolean handedOff;
    private int deploymentAge;
    private Vec3 anchor = Vec3.ZERO;
    private String dimension = "";
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
        if (level().isClientSide() || attributesApplied || !BastionCombatConfig.SPEC.isLoaded()) return;
        float health = getHealth(); getAttribute(Attributes.MAX_HEALTH).setBaseValue(BastionCombatConfig.POD_HEALTH.get()); getAttribute(Attributes.ARMOR).setBaseValue(BastionCombatConfig.POD_ARMOR.get());
        setHealth(loadedFromSave ? Math.min(health, getMaxHealth()) : getMaxHealth()); attributesApplied = true;
    }
    @Override protected void defineSynchedData(SynchedEntityData.Builder builder) { super.defineSynchedData(builder); builder.define(TEXTURE, "blizzarddroppod"); }
    public String texture() { return entityData.get(TEXTURE); }
    public void setTexture(String texture) { entityData.set(TEXTURE, texture); }
    public void setAnimation(String animation) { if (!"empty".equals(animation) && !"undefined".equals(animation)) triggerAnim("procedure", animation); }
    @Override public boolean removeWhenFarAway(double distance) { return false; }
    @Override public boolean hurtServer(net.minecraft.server.level.ServerLevel level, DamageSource source, float amount) {
        return !source.is(DamageTypes.IN_FIRE) && !(source.getDirectEntity() instanceof AbstractArrow) && !(source.getDirectEntity() instanceof Player)
                && !(source.getDirectEntity() instanceof AbstractThrownPotion) && !(source.getDirectEntity() instanceof AreaEffectCloud)
                && !source.is(DamageTypes.FALL) && !source.is(DamageTypes.CACTUS) && !source.is(DamageTypes.DROWN) && !source.is(DamageTypes.LIGHTNING_BOLT)
                && !source.is(DamageTypes.EXPLOSION) && !source.is(DamageTypes.TRIDENT) && !source.is(DamageTypes.FALLING_ANVIL)
                && !source.is(DamageTypes.DRAGON_BREATH) && !source.is(DamageTypes.WITHER) && !source.is(DamageTypes.WITHER_SKULL) && super.hurtServer(level, source, amount);
    }
    @Override public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty, EntitySpawnReason reason, SpawnGroupData group) {
        var result = super.finalizeSpawn(level, difficulty, reason, group);
        if (!level.isClientSide() && !initialized && !handedOff) {
            initialized = true; anchor = position(); dimension = level().dimension().identifier().toString(); deploymentAge = 0;
        }
        return result;
    }
    @Override public void onAddedToLevel() { super.onAddedToLevel(); if (initialized && !handedOff && isAlive() && level() instanceof ServerLevel server) ServerEndTickTasks.schedule(server, getUUID()); }
    @Override public void tick() {
        if (!level().isClientSide()) applyConfiguredAttributes();
        super.tick();
        if (!(level() instanceof ServerLevel server)) return;
        if (handedOff) { discard(); return; }
        if (!initialized) return;
        if (!isAlive() || !dimension.equals(server.dimension().identifier().toString())) { initialized = false; return; }
        ServerEndTickTasks.schedule(server, getUUID());
    }
    @Override public void endTick(ServerLevel server) {
        if (!initialized || handedOff || isRemoved()) return;
        if (!isAlive() || !dimension.equals(server.dimension().identifier().toString())) { initialized = false; return; }
        int age = ++deploymentAge;
        if (age >= 15 && !smokeSent) {
            smokeSent = true;
            server.sendParticles(IceSquadContent.FOG_CLOUD.get(), anchor.x + 0.5, anchor.y, anchor.z + 0.5, 25, 2, 2, 2, 0);
            server.playSound(null, BlockPos.containing(anchor), SoundEvents.ANVIL_PLACE, SoundSource.HOSTILE, 2, -4);
        }
        if (age >= 25) {
            handedOff = true;
            server.playSound(null, BlockPos.containing(anchor), SoundEvents.DRAGON_FIREBALL_EXPLODE, SoundSource.HOSTILE, 1, 2);
            server.playSound(null, BlockPos.containing(anchor), BuiltInRegistries.SOUND_EVENT.getValue(Identifier.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, "magnetcrossbowfire")), SoundSource.HOSTILE, 1, -3);
            if (!DeploymentTaskEntity.submit(server, anchor, getUUID(), false)) ThermalShockReforged.LOGGER.debug("Blizzard final deployment rejected: pod={}, anchor={}", getUUID(), anchor);
            discard();
        } else ServerEndTickTasks.schedule(server, getUUID());
    }
    @Override protected void dropExperience(net.minecraft.server.level.ServerLevel level, Entity attacker) { experiencePending = true; experienceAttacker = attacker == null ? null : attacker.getUUID(); }
    @Override protected void tickDeath() {
        if (++deathTime >= 20 && !isRemoved()) {
            remove(RemovalReason.KILLED);
            if (experiencePending && level() instanceof ServerLevel server) { experiencePending = false; super.dropExperience(server, experienceAttacker == null ? null : server.getEntity(experienceAttacker)); }
        }
    }
    @Override public void addAdditionalSaveData(net.minecraft.world.level.storage.ValueOutput output) {
        super.addAdditionalSaveData(output); output.putString("Texture", texture()); output.putBoolean("PodInitialized", initialized); output.putBoolean("PodSmokeSent", smokeSent); output.putBoolean("PodHandedOff", handedOff); output.putInt("PodAge", deploymentAge);
        output.putDouble("PodAnchorX", anchor.x); output.putDouble("PodAnchorY", anchor.y); output.putDouble("PodAnchorZ", anchor.z); output.putString("PodDimension", dimension);
        output.putBoolean("PodExperiencePending", experiencePending); if (experienceAttacker != null) output.putString("PodExperienceAttacker", experienceAttacker.toString());
    }
    @Override public void readAdditionalSaveData(net.minecraft.world.level.storage.ValueInput input) {
        loadedFromSave = true; super.readAdditionalSaveData(input); attributesApplied = false; applyConfiguredAttributes(); setPersistenceRequired(); if (input.getString("Texture").isPresent()) setTexture(input.getStringOr("Texture", ""));
        initialized = input.getBooleanOr("PodInitialized", false); smokeSent = input.getBooleanOr("PodSmokeSent", false); handedOff = input.getBooleanOr("PodHandedOff", false); deploymentAge = Math.clamp(input.getIntOr("PodAge", 0), 0, 25);
        anchor = new Vec3(input.getDoubleOr("PodAnchorX", 0.0), input.getDoubleOr("PodAnchorY", 0.0), input.getDoubleOr("PodAnchorZ", 0.0)); dimension = input.getStringOr("PodDimension", "");
        if (!Double.isFinite(anchor.x) || !Double.isFinite(anchor.y) || !Double.isFinite(anchor.z)) initialized = false;
        experiencePending = input.getBooleanOr("PodExperiencePending", false); experienceAttacker = input.getString("PodExperienceAttacker").map(java.util.UUID::fromString).orElse(null);
    }
    @Override public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        var procedure = new LegacyAnimationController<>(this, "procedure", 4, state -> PlayState.STOP).triggerableAnim("spawn", RawAnimation.begin().thenPlay("spawn")).triggerableAnim("idle", RawAnimation.begin().thenPlay("idle"));
        controllers.add(new LegacyAnimationController<>(this, "movement", 4, state -> procedure.isPlayingTriggeredAnimation() ? PlayState.STOP : state.setAndContinue(RawAnimation.begin().thenLoop("spawn")))); controllers.add(procedure);
    }
    @Override public AnimatableInstanceCache getAnimatableInstanceCache() { return animations; }
}
