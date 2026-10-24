package com.hp.thermalshockreforged.entity.labmachinist;
import com.geckolib.animation.object.PlayState;
import com.hp.thermalshockreforged.compat.LegacyAnimationController;
import com.geckolib.animatable.manager.AnimatableManager;

import com.hp.thermalshockreforged.content.MaterialContent;
import com.hp.thermalshockreforged.entity.qo.*;
import net.minecraft.ChatFormatting;
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
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import com.geckolib.animatable.GeoEntity;
import com.geckolib.animatable.instance.AnimatableInstanceCache;
import com.geckolib.animation.*;
import com.geckolib.util.GeckoLibUtil;

public final class LabMachinistEntity extends PathfinderMob implements GeoEntity {
    private static final EntityDataAccessor<String> TEXTURE = SynchedEntityData.defineId(LabMachinistEntity.class, EntityDataSerializers.STRING);
    private final AnimatableInstanceCache animations = GeckoLibUtil.createInstanceCache(this);
    public LabMachinistEntity(EntityType<? extends LabMachinistEntity> type, Level level) { super(type, level); xpReward = 0; setCustomName(Component.translatable("entity.thermal_shock_reforged.lab_machinist").withStyle(ChatFormatting.GREEN)); setCustomNameVisible(true); setPersistenceRequired(); }
    public static AttributeSupplier.Builder createAttributes() { return Mob.createMobAttributes().add(Attributes.MAX_HEALTH, 75).add(Attributes.ARMOR, 64).add(Attributes.MOVEMENT_SPEED, 0).add(Attributes.ATTACK_DAMAGE, 0).add(Attributes.FOLLOW_RANGE, 16).add(Attributes.KNOCKBACK_RESISTANCE, 1).add(Attributes.STEP_HEIGHT, 0); }
    @Override protected void defineSynchedData(SynchedEntityData.Builder builder) { super.defineSynchedData(builder); builder.define(TEXTURE, "qo"); }
    public String texture() { return entityData.get(TEXTURE); }
    @Override public boolean removeWhenFarAway(double distance) { return false; }
    @Override protected net.minecraft.world.phys.Vec3 getPassengerAttachmentPoint(Entity passenger, EntityDimensions dimensions, float scale) { return new net.minecraft.world.phys.Vec3(0, dimensions.height() * .75 + 2, 0); }
    public boolean isInvulnerableTo(DamageSource source) {
        if (source.is(net.minecraft.tags.DamageTypeTags.BYPASSES_INVULNERABILITY)) return false;
        return !(source.getEntity() instanceof net.minecraft.world.entity.player.Player player && player.isCreative());
    }
    @Override protected InteractionResult mobInteract(Player player, InteractionHand hand) {
        super.mobInteract(player, hand);
        if (hand == InteractionHand.MAIN_HAND && player instanceof ServerPlayer server && isAlive()) {
            if (!LabPlayerState.introduced(server)) {
                if (LabContent.open(server, this, blockPosition(), LabPage.INTRO)) LabPlayerState.introduce(server);
            } else {
                ItemStack held = server.getMainHandItem(); QoPage page = null;
                if (held.is(MaterialContent.items().get("phobotite").get())) page = QoPage.PHOBOTITE;
                else if (held.is(MaterialContent.items().get("cryonic_energy").get())) page = QoPage.CRYO_ENERGY;
                else if (held.is(MaterialContent.items().get("neural_chip").get())) page = QoPage.NEURAL_CHIP;
                else if (held.is(MaterialContent.items().get("cryonic_alloy").get())) page = QoPage.CRYO_ALLOY;
                else if (held.is(MaterialContent.items().get("frigid_altar_key").get())) page = QoPage.ALTAR_KEY;
                else if (held.is(MaterialContent.items().get("stellar_scrap").get())) page = QoPage.STELLAR_SCRAP;
                else if (held.is(MaterialContent.items().get("stratus_alloy").get()) && !QoMenu.completed(server, "uneasy_alliance_1")) page = QoPage.STRATUS_1;
                if (page == null) LabContent.open(server, this, blockPosition(), LabPage.MAIN);
                else QoContent.open(server, this, blockPosition(), page);
            }
        }
        return InteractionResult.SUCCESS;
    }
    @Override protected void tickDeath() { if (++deathTime >= 20) remove(RemovalReason.KILLED); }
    @Override public void addAdditionalSaveData(net.minecraft.world.level.storage.ValueOutput output) { super.addAdditionalSaveData(output); output.putString("Texture", texture()); }
    @Override public void readAdditionalSaveData(net.minecraft.world.level.storage.ValueInput input) { super.readAdditionalSaveData(input);
if (input.read("Texture", com.mojang.serialization.Codec.PASSTHROUGH).isPresent()) entityData.set(TEXTURE, input.getStringOr("Texture", "")); setPersistenceRequired(); }
    @Override public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        var procedure = new LegacyAnimationController<>(this, "procedure", 4, state -> PlayState.STOP).triggerableAnim("notice", RawAnimation.begin().thenPlay("notice"));
        controllers.add(new LegacyAnimationController<>(this, "movement", 4, state -> procedure.isPlayingTriggeredAnimation() ? PlayState.STOP : state.setAndContinue(RawAnimation.begin().thenLoop("idle"))));
        controllers.add(procedure);
    }
    @Override public AnimatableInstanceCache getAnimatableInstanceCache() { return animations; }
    public boolean shouldPlayAnimsWhileGamePaused() { return true; }
}
