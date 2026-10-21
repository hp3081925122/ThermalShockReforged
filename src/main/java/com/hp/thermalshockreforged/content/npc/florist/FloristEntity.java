package com.hp.thermalshockreforged.content.npc.florist;

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
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.*;
import software.bernie.geckolib.util.GeckoLibUtil;

public final class FloristEntity extends PathfinderMob implements GeoEntity {
    private static final EntityDataAccessor<String> TEXTURE = SynchedEntityData.defineId(FloristEntity.class, EntityDataSerializers.STRING);
    private final AnimatableInstanceCache animations = GeckoLibUtil.createInstanceCache(this);
    public FloristEntity(EntityType<? extends FloristEntity> type, Level level) { super(type, level); xpReward = 0; setCustomName(Component.literal("§aFlorist")); setCustomNameVisible(true); setPersistenceRequired(); }
    public static AttributeSupplier.Builder createAttributes() { return Mob.createMobAttributes().add(Attributes.MAX_HEALTH, 75).add(Attributes.ARMOR, 30).add(Attributes.MOVEMENT_SPEED, 0).add(Attributes.ATTACK_DAMAGE, 0).add(Attributes.FOLLOW_RANGE, 16).add(Attributes.KNOCKBACK_RESISTANCE, 1).add(Attributes.STEP_HEIGHT, 0); }
    @Override protected void defineSynchedData(SynchedEntityData.Builder builder) { super.defineSynchedData(builder); builder.define(TEXTURE, "florist"); }
    @Override protected void registerGoals() { }
    public String texture() { return entityData.get(TEXTURE); }
    @Override public boolean removeWhenFarAway(double distance) { return false; }
    @Override public boolean hurt(DamageSource source, float amount) {
        if (source.getDirectEntity() instanceof AbstractArrow || source.getDirectEntity() instanceof ThrownPotion || source.getDirectEntity() instanceof AreaEffectCloud) return false;
        if (source.is(DamageTypes.IN_FIRE) || source.is(DamageTypes.FALL) || source.is(DamageTypes.CACTUS) || source.is(DamageTypes.DROWN) || source.is(DamageTypes.LIGHTNING_BOLT) || source.is(DamageTypes.EXPLOSION) || source.is(DamageTypes.TRIDENT) || source.is(DamageTypes.FALLING_ANVIL) || source.is(DamageTypes.DRAGON_BREATH) || source.is(DamageTypes.WITHER) || source.is(DamageTypes.WITHER_SKULL)) return false;
        return super.hurt(source, amount);
    }
    @Override protected InteractionResult mobInteract(Player player, InteractionHand hand) {
        super.mobInteract(player, hand);
        if (player instanceof ServerPlayer serverPlayer) FloristContent.open(serverPlayer, this, blockPosition(), FloristPage.MAIN);
        return InteractionResult.sidedSuccess(level().isClientSide);
    }
    @Override public SpawnGroupData finalizeSpawn(net.minecraft.world.level.ServerLevelAccessor world, net.minecraft.world.DifficultyInstance difficulty, MobSpawnType reason, SpawnGroupData group) { var result = super.finalizeSpawn(world, difficulty, reason, group); setNoGravity(true); return result; }
    @Override protected void tickDeath() { if (++deathTime >= 20) remove(RemovalReason.KILLED); }
    @Override public void addAdditionalSaveData(CompoundTag tag) { super.addAdditionalSaveData(tag); tag.putString("Texture", texture()); }
    @Override public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        if (!tag.contains("NeoForgeData", 10) && tag.contains("ForgeData", 10)) getPersistentData().merge(tag.getCompound("ForgeData"));
        if (tag.contains("Texture")) entityData.set(TEXTURE, tag.getString("Texture"));
        setPersistenceRequired();
    }
    @Override public void registerControllers(AnimatableManager.ControllerRegistrar controllers) { controllers.add(new AnimationController<>(this, "movement", 4, state -> state.setAndContinue(RawAnimation.begin().thenLoop("Idle")))); }
    @Override public AnimatableInstanceCache getAnimatableInstanceCache() { return animations; }
    @Override public boolean shouldPlayAnimsWhileGamePaused() { return true; }
}
