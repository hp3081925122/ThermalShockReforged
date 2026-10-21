package com.hp.thermalshockreforged.content.npc.portrait;

import com.hp.thermalshockreforged.content.atlas.AtlasContent;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.*;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.*;
import net.minecraft.world.damagesource.*;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.*;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.*;
import net.minecraft.world.level.Level;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.*;
import software.bernie.geckolib.util.GeckoLibUtil;

public final class PortraitCharacterEntity extends Monster implements GeoEntity {
    private static final EntityDataAccessor<String> TEXTURE = SynchedEntityData.defineId(PortraitCharacterEntity.class, EntityDataSerializers.STRING);
    private static final String[] TEXTURES = {"portrait_default", "portrait_pente", "portrait_mc", "portrait_ibex", "portrait_gradient"};
    private static final String[] NAME_KEYS = {
            "entity.thermal_shock_reforged.portrait_nyx",
            "entity.thermal_shock_reforged.portrait_pente",
            "entity.thermal_shock_reforged.portrait_mc",
            "entity.thermal_shock_reforged.portrait_ibex",
            "entity.thermal_shock_reforged.portrait_gradient"
    };
    private final AnimatableInstanceCache animations = GeckoLibUtil.createInstanceCache(this);
    public PortraitCharacterEntity(EntityType<? extends PortraitCharacterEntity> type, Level level) { super(type, level); xpReward = 0; setCustomName(name(0)); setCustomNameVisible(true); setPersistenceRequired(); }
    private static Component name(int index) { return Component.translatable(NAME_KEYS[index]).withStyle(index == 3 ? ChatFormatting.YELLOW : ChatFormatting.GREEN); }
    public static AttributeSupplier.Builder createAttributes() { return Mob.createMobAttributes().add(Attributes.MAX_HEALTH, 75).add(Attributes.ARMOR, 30).add(Attributes.KNOCKBACK_RESISTANCE, 1).add(Attributes.MOVEMENT_SPEED, 0).add(Attributes.ATTACK_DAMAGE, 0).add(Attributes.FOLLOW_RANGE, 16).add(Attributes.STEP_HEIGHT, 0); }
    @Override protected void defineSynchedData(SynchedEntityData.Builder builder) { super.defineSynchedData(builder); builder.define(TEXTURE, TEXTURES[0]); }
    public String texture() { return entityData.get(TEXTURE); }
    private void texture(String texture) { entityData.set(TEXTURE, texture); }
    @Override public void onSyncedDataUpdated(EntityDataAccessor<?> accessor) { super.onSyncedDataUpdated(accessor); if (TEXTURE.equals(accessor)) refreshDimensions(); }
    @Override protected EntityDimensions getDefaultDimensions(Pose pose) { return super.getDefaultDimensions(pose).scale(texture().equals(TEXTURES[0]) ? 1 : .5F).withEyeHeight(1.62F); }
    @Override protected void registerGoals() { super.registerGoals(); goalSelector.addGoal(1, new LookAtPlayerGoal(this, Player.class, 16)); }
    @Override public boolean removeWhenFarAway(double distance) { return false; }
    @Override public boolean isInvulnerableTo(DamageSource source) {
        if (source.is(net.minecraft.tags.DamageTypeTags.BYPASSES_INVULNERABILITY)) return false;
        return !(source.getEntity() instanceof net.minecraft.world.entity.player.Player player && player.isCreative());
    }
    @Override protected InteractionResult mobInteract(Player player, InteractionHand hand) {
        super.mobInteract(player, hand);
        if (player instanceof ServerPlayer server) {
            if (player.getAbilities().instabuild && player.getMainHandItem().is(AtlasContent.KEYSTONE.get())) {
                int next = TEXTURES.length - 1;
                for (int i = 0; i < TEXTURES.length; i++) if (texture().equals(TEXTURES[i])) { next = (i + 1) % TEXTURES.length; break; }
                texture(TEXTURES[next]); setCustomName(name(next));
            } else if (texture().equals("portrait_default")) PortraitContent.open(server, this, false);
            else if (texture().equals("portrait_ibex")) PortraitContent.open(server, this, true);
        }
        return InteractionResult.sidedSuccess(level().isClientSide);
    }
    @Override protected void tickDeath() { if (++deathTime >= 20) remove(RemovalReason.KILLED); }
    @Override public void addAdditionalSaveData(CompoundTag tag) { super.addAdditionalSaveData(tag); tag.putString("Texture", texture()); }
    @Override public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        if (!tag.contains("NeoForgeData", 10) && tag.contains("ForgeData", 10)) getPersistentData().merge(tag.getCompound("ForgeData"));
        if (tag.contains("Texture", 8)) texture(tag.getString("Texture"));
        setPersistenceRequired();
    }
    @Override public void registerControllers(AnimatableManager.ControllerRegistrar controllers) { controllers.add(new AnimationController<>(this, "movement", 4, state -> state.setAndContinue(RawAnimation.begin().thenLoop("idle")))); }
    @Override public AnimatableInstanceCache getAnimatableInstanceCache() { return animations; }
}
