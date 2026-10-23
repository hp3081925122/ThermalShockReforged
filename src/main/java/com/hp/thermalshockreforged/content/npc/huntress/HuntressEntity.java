package com.hp.thermalshockreforged.content.npc.huntress;
import com.geckolib.animation.object.PlayState;
import com.hp.thermalshockreforged.compat.LegacyAnimationController;
import com.geckolib.animatable.manager.AnimatableManager;

import com.hp.thermalshockreforged.ThermalShockReforged;
import com.hp.thermalshockreforged.entity.huntress.HuntressTaskEntity;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.*;
import net.minecraft.server.level.*;
import net.minecraft.world.*;
import net.minecraft.world.damagesource.*;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.*;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.*;
import net.minecraft.world.level.*;
import net.minecraft.world.phys.Vec3;
import com.geckolib.animatable.GeoEntity;
import com.geckolib.animatable.instance.AnimatableInstanceCache;
import com.geckolib.animation.*;
import com.geckolib.util.GeckoLibUtil;

public final class HuntressEntity extends PathfinderMob implements GeoEntity {
    private static final EntityDataAccessor<String> TEXTURE = SynchedEntityData.defineId(HuntressEntity.class, EntityDataSerializers.STRING);
    private final AnimatableInstanceCache animations = GeckoLibUtil.createInstanceCache(this);
    private boolean initializationPending;
    private boolean initialized;
    private boolean animationPending;
    private Vec3 initializationOrigin = Vec3.ZERO;
    private int retry;
    public HuntressEntity(EntityType<? extends HuntressEntity> type, Level level) { super(type, level); xpReward = 0; setCustomName(Component.translatable("entity.thermal_shock_reforged.huntress.display_name").withStyle(ChatFormatting.GREEN)); setCustomNameVisible(true); setPersistenceRequired(); }
    public static AttributeSupplier.Builder createAttributes() { return Mob.createMobAttributes().add(Attributes.MAX_HEALTH, 300).add(Attributes.ARMOR, 8).add(Attributes.MOVEMENT_SPEED, 0).add(Attributes.ATTACK_DAMAGE, 0).add(Attributes.FOLLOW_RANGE, 16).add(Attributes.KNOCKBACK_RESISTANCE, 1).add(Attributes.STEP_HEIGHT, 0); }
    public String texture() { return entityData.get(TEXTURE); }
    public void setTexture(String texture) { entityData.set(TEXTURE, texture); }
    @Override protected void defineSynchedData(SynchedEntityData.Builder builder) { super.defineSynchedData(builder); builder.define(TEXTURE, "huntress"); }
    @Override protected void registerGoals() { goalSelector.addGoal(1, new LookAtPlayerGoal(this, Player.class, 6)); goalSelector.addGoal(2, new RandomLookAroundGoal(this)); }
    @Override public boolean removeWhenFarAway(double distance) { return false; }
    public boolean isInvulnerableTo(DamageSource source) {
        if (source.is(net.minecraft.tags.DamageTypeTags.BYPASSES_INVULNERABILITY)) return false;
        return !(source.getEntity() instanceof net.minecraft.world.entity.player.Player player && player.isCreative());
    }
    @Override protected InteractionResult mobInteract(Player player, InteractionHand hand) { super.mobInteract(player, hand); if (player instanceof ServerPlayer server) HuntressMenus.open(server, this, HuntressPage.MAIN); return InteractionResult.SUCCESS; }
    @Override public SpawnGroupData finalizeSpawn(ServerLevelAccessor world, DifficultyInstance difficulty, EntitySpawnReason reason, SpawnGroupData group) { var result = super.finalizeSpawn(world, difficulty, reason, group); if (!initialized) { initialized = true; setNoGravity(true); initializationOrigin = position(); initializationPending = true; animationPending = true; } return result; }
    private void submitInitialization() {
        if (!initializationPending || retry > 0 || !(level() instanceof ServerLevel server)) return;
        initializationPending = false;
        if (!HuntressTaskEntity.submitInitialization(server, this, initializationOrigin)) { initializationPending = true; retry = 20; ThermalShockReforged.LOGGER.debug("Huntress initialization insertion failed: npc={}", getUUID()); }
    }
    @Override public void onAddedToLevel() { super.onAddedToLevel(); submitInitialization(); }
    @Override public void tick() { super.tick(); if (!level().isClientSide()) { if (retry > 0) retry--; submitInitialization(); if (animationPending) { animationPending = false; triggerAnim("procedure", "npcspawn"); } } }
    @Override public void aiStep() { super.aiStep(); updateSwingTime(); }
    @Override protected void tickDeath() { if (++deathTime >= 20 && !isRemoved()) remove(RemovalReason.KILLED); }
    @Override public void addAdditionalSaveData(net.minecraft.world.level.storage.ValueOutput output) { super.addAdditionalSaveData(output); output.putString("Texture", texture()); output.putBoolean("HuntressInitialized", initialized); output.putBoolean("HuntressInitializationPending", initializationPending); output.putBoolean("HuntressAnimationPending", animationPending); output.putInt("HuntressInitializationRetry", retry); output.putDouble("HuntressInitialX", initializationOrigin.x); output.putDouble("HuntressInitialY", initializationOrigin.y); output.putDouble("HuntressInitialZ", initializationOrigin.z); }
    @Override public void readAdditionalSaveData(net.minecraft.world.level.storage.ValueInput input) { super.readAdditionalSaveData(input); if (input.getString("Texture").isPresent()) setTexture(input.getStringOr("Texture", "")); initialized = !input.getString("HuntressInitialized").isPresent() || input.getBooleanOr("HuntressInitialized", false); initializationPending = input.getBooleanOr("HuntressInitializationPending", false); animationPending = input.getBooleanOr("HuntressAnimationPending", false); retry = Math.clamp(input.getIntOr("HuntressInitializationRetry", 0), 0, 20); initializationOrigin = new Vec3(input.getDoubleOr("HuntressInitialX", 0.0), input.getDoubleOr("HuntressInitialY", 0.0), input.getDoubleOr("HuntressInitialZ", 0.0)); if (!Double.isFinite(initializationOrigin.lengthSqr())) { initializationPending = false; initializationOrigin = position(); } setPersistenceRequired(); }
    @Override public void registerControllers(AnimatableManager.ControllerRegistrar controllers) { var procedure = new LegacyAnimationController<>(this, "procedure", 0, state -> PlayState.STOP).triggerableAnim("npcspawn", RawAnimation.begin().thenPlay("npcspawn")); controllers.add(new LegacyAnimationController<>(this, "movement", 0, state -> procedure.isPlayingTriggeredAnimation() ? PlayState.STOP : state.setAndContinue(RawAnimation.begin().thenLoop("idle3")))); controllers.add(procedure); }
    @Override public AnimatableInstanceCache getAnimatableInstanceCache() { return animations; }
}
