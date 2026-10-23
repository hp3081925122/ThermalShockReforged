package com.hp.thermalshockreforged.content.npc.concierge;
import com.geckolib.animation.object.PlayState;
import com.hp.thermalshockreforged.compat.LegacyAnimationController;
import com.geckolib.animatable.manager.AnimatableManager;

import com.hp.thermalshockreforged.ThermalShockReforged;
import com.hp.thermalshockreforged.world.timing.ServerEndTickTask;
import com.hp.thermalshockreforged.world.timing.ServerEndTickTasks;
import net.minecraft.server.level.ServerLevel;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.*;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.*;
import net.minecraft.world.damagesource.*;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.*;
import net.minecraft.world.level.Level;
import com.geckolib.animatable.GeoEntity;
import com.geckolib.animatable.instance.AnimatableInstanceCache;
import com.geckolib.animation.*;
import com.geckolib.util.GeckoLibUtil;

public final class ConciergeEntity extends PathfinderMob implements GeoEntity, ServerEndTickTask {
    private static final EntityDataAccessor<String> TEXTURE = SynchedEntityData.defineId(ConciergeEntity.class, EntityDataSerializers.STRING);
    private final AnimatableInstanceCache animations = GeckoLibUtil.createInstanceCache(this);
    private int noticeSchedule;
    public ConciergeEntity(EntityType<? extends ConciergeEntity> type, Level level) { super(type, level); xpReward = 0; setCustomName(Component.translatable("entity.thermal_shock_reforged.concierge.display_name")); setCustomNameVisible(true); setPersistenceRequired(); }
    public static AttributeSupplier.Builder createAttributes() { return Mob.createMobAttributes().add(Attributes.MAX_HEALTH, 75).add(Attributes.ARMOR, 64).add(Attributes.MOVEMENT_SPEED, 0).add(Attributes.ATTACK_DAMAGE, 0).add(Attributes.FOLLOW_RANGE, 16).add(Attributes.KNOCKBACK_RESISTANCE, 1).add(Attributes.STEP_HEIGHT, 0); }
    @Override protected void defineSynchedData(SynchedEntityData.Builder builder) { super.defineSynchedData(builder); builder.define(TEXTURE, "concierge"); }
    public String texture() { return entityData.get(TEXTURE); }
    @Override public boolean removeWhenFarAway(double distance) { return false; }
    public boolean isInvulnerableTo(DamageSource source) {
        if (source.is(net.minecraft.tags.DamageTypeTags.BYPASSES_INVULNERABILITY)) return false;
        return !(source.getEntity() instanceof net.minecraft.world.entity.player.Player player && player.isCreative());
    }
    @Override protected InteractionResult mobInteract(Player player, InteractionHand hand) {
        super.mobInteract(player, hand);
        if (player instanceof ServerPlayer serverPlayer) {
            if (isShiftKeyDown()) {
                triggerAnim("procedure", "notice");
                noticeSchedule |= 1 << 19;
                ServerEndTickTasks.schedule(server((net.minecraft.server.level.ServerLevel) player.level()), getUUID());
                ThermalShockReforged.LOGGER.debug("Concierge notice armed: entity={}, pendingEndTickSlots={}", getUUID(), Integer.bitCount(noticeSchedule));
            } else {
                ConciergeContent.open(serverPlayer, this, blockPosition(), ConciergePage.MAIN);
            }
        }
        return InteractionResult.SUCCESS;
    }
    @Override public SpawnGroupData finalizeSpawn(net.minecraft.world.level.ServerLevelAccessor world, net.minecraft.world.DifficultyInstance difficulty, EntitySpawnReason reason, SpawnGroupData group) { var result = super.finalizeSpawn(world, difficulty, reason, group); setNoGravity(true); return result; }
    @Override public void tick() {
        super.tick();
        if (noticeSchedule != 0 && level() instanceof ServerLevel server) ServerEndTickTasks.schedule(server, getUUID());
    }
    @Override public void onAddedToLevel() { super.onAddedToLevel(); if (noticeSchedule != 0 && level() instanceof ServerLevel server) ServerEndTickTasks.schedule(server, getUUID()); }
    @Override public void endTick(ServerLevel server) {
        if (isRemoved() || noticeSchedule == 0) return;
        boolean completed = (noticeSchedule & 1) != 0;
        noticeSchedule >>>= 1;
        if (completed) setShiftKeyDown(false);
        if (noticeSchedule != 0) ServerEndTickTasks.schedule(server, getUUID());
    }
    @Override protected void tickDeath() { if (++deathTime >= 20) remove(RemovalReason.KILLED); }
    @Override public void addAdditionalSaveData(net.minecraft.world.level.storage.ValueOutput output) { super.addAdditionalSaveData(output); output.putString("Texture", texture()); output.putInt("ConciergeNoticeSchedule", noticeSchedule); output.putBoolean("ConciergeNoticeShifted", isShiftKeyDown()); }
    @Override public void readAdditionalSaveData(net.minecraft.world.level.storage.ValueInput input) {
        super.readAdditionalSaveData(input);
        if (!tag.contains("NeoForgeData", 10) && tag.contains("ForgeData", 10)) getPersistentData().merge(tag.getCompound("ForgeData"));
        if (input.getString("Texture").isPresent()) entityData.set(TEXTURE, input.getStringOr("Texture", ""));
        int legacyTicks = Math.clamp(input.getIntOr("ConciergeNoticeTicks", 0), 0, 20);
        noticeSchedule = input.getString("ConciergeNoticeSchedule").isPresent() ? input.getIntOr("ConciergeNoticeSchedule", 0) & 0xfffff : legacyTicks > 0 ? 1 << (legacyTicks - 1) : 0;
        if (noticeSchedule != 0) setShiftKeyDown(!input.getString("ConciergeNoticeShifted").isPresent() || input.getBooleanOr("ConciergeNoticeShifted", false));
        setPersistenceRequired();
    }
    @Override public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        var procedure = new LegacyAnimationController<>(this, "procedure", 4, state -> PlayState.STOP).triggerableAnim("notice", RawAnimation.begin().thenPlay("notice"));
        controllers.add(new LegacyAnimationController<>(this, "movement", 4, state -> procedure.isPlayingTriggeredAnimation() ? PlayState.STOP : state.setAndContinue(RawAnimation.begin().thenLoop("idle"))));
        controllers.add(procedure);
    }
    @Override public AnimatableInstanceCache getAnimatableInstanceCache() { return animations; }
    @Override public boolean shouldPlayAnimsWhileGamePaused() { return true; }
}
