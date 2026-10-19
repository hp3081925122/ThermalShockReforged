package com.hp.thermalshockreforged.entity.qo;

import com.hp.thermalshockreforged.content.MaterialContent;
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
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.*;
import software.bernie.geckolib.util.GeckoLibUtil;

public final class DefectorMachinistEntity extends PathfinderMob implements GeoEntity {
    private static final EntityDataAccessor<String> TEXTURE = SynchedEntityData.defineId(DefectorMachinistEntity.class, EntityDataSerializers.STRING);
    private final AnimatableInstanceCache animations = GeckoLibUtil.createInstanceCache(this);
    private int idleCooldown;
    public DefectorMachinistEntity(EntityType<? extends DefectorMachinistEntity> type, Level level) { super(type, level); xpReward = 0; setCustomName(Component.translatable("entity.thermal_shock_reforged.defector_machinist.display_name")); setCustomNameVisible(true); setPersistenceRequired(); }
    public static AttributeSupplier.Builder createAttributes() { return Mob.createMobAttributes().add(Attributes.MAX_HEALTH, 75).add(Attributes.ARMOR, 64).add(Attributes.MOVEMENT_SPEED, 0).add(Attributes.ATTACK_DAMAGE, 0).add(Attributes.FOLLOW_RANGE, 16).add(Attributes.KNOCKBACK_RESISTANCE, 1).add(Attributes.STEP_HEIGHT, 0); }
    @Override protected void defineSynchedData(SynchedEntityData.Builder builder) { super.defineSynchedData(builder); builder.define(TEXTURE, "qo"); }
    public String texture() { return entityData.get(TEXTURE); }
    @Override public boolean removeWhenFarAway(double distance) { return false; }
    static boolean ignores(DamageSource source) {
        return source.getDirectEntity() instanceof AbstractArrow || source.getDirectEntity() instanceof ThrownPotion || source.getDirectEntity() instanceof AreaEffectCloud || source.is(DamageTypes.IN_FIRE) || source.is(DamageTypes.FALL) || source.is(DamageTypes.CACTUS) || source.is(DamageTypes.DROWN) || source.is(DamageTypes.LIGHTNING_BOLT) || source.is(DamageTypes.EXPLOSION) || source.is(DamageTypes.TRIDENT) || source.is(DamageTypes.FALLING_ANVIL) || source.is(DamageTypes.DRAGON_BREATH) || source.is(DamageTypes.WITHER) || source.is(DamageTypes.WITHER_SKULL);
    }
    @Override public boolean hurt(DamageSource source, float amount) { return !ignores(source) && super.hurt(source, amount); }
    @Override protected InteractionResult mobInteract(Player player, InteractionHand hand) {
        super.mobInteract(player, hand);
        if (hand == InteractionHand.MAIN_HAND && player instanceof ServerPlayer server && isAlive()) {
            var progress = QoProgressData.get(server.serverLevel());
            double contacts = progress.contacts();
            QoPage page = contacts == 0 ? QoPage.INTRO_1 : contacts == 1 ? QoPage.INTRO_2 : contacts == 2 ? QoPage.INTRO_3 : contacts == 3 ? QoPage.INTRO_4 : contacts == 4 ? QoPage.INTRO_5 : QoPage.EXHAUST;
            if (contacts >= 4) {
                ItemStack held = server.getMainHandItem();
                if (held.is(MaterialContent.items().get("phobotite").get())) page = QoPage.PHOBOTITE;
                else if (held.is(MaterialContent.items().get("cryonic_energy").get())) page = QoPage.CRYO_ENERGY;
                else if (held.is(MaterialContent.items().get("neural_chip").get())) page = QoPage.NEURAL_CHIP;
                else if (held.is(MaterialContent.items().get("cryonic_alloy").get())) page = QoPage.CRYO_ALLOY;
                else if (held.is(MaterialContent.items().get("frigid_altar_key").get())) page = QoPage.ALTAR_KEY;
                else if (held.is(MaterialContent.items().get("stellar_scrap").get())) page = QoPage.STELLAR_SCRAP;
                else if (held.is(MaterialContent.items().get("stratus_alloy").get()) && !QoMenu.completed(server, "uneasy_alliance_1")) page = QoPage.STRATUS_1;
            }
            if (QoContent.open(server, this, blockPosition(), page)) {
                progress.advance();
                if (contacts == 4) QoContent.give(server, new ItemStack(QoContent.ICE_ARTIFACT.get()));
            }
        }
        return InteractionResult.sidedSuccess(level().isClientSide);
    }
    @Override public void baseTick() {
        super.baseTick();
        if (!level().isClientSide && idleCooldown > 0) idleCooldown--;
        if (!level().isClientSide && idleCooldown == 0 && random.nextDouble() < .005) { idleCooldown = 120; triggerAnim("procedure", "idlejunkyard2"); }
    }
    @Override protected void tickDeath() { if (++deathTime >= 20) remove(RemovalReason.KILLED); }
    @Override public void addAdditionalSaveData(CompoundTag tag) { super.addAdditionalSaveData(tag); tag.putString("Texture", texture()); tag.putInt("QoIdleCooldown", idleCooldown); }
    @Override public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        if (!tag.contains("NeoForgeData", 10) && tag.contains("ForgeData", 10)) getPersistentData().merge(tag.getCompound("ForgeData"));
        if (tag.contains("Texture")) entityData.set(TEXTURE, tag.getString("Texture"));
        idleCooldown = Math.clamp(tag.getInt("QoIdleCooldown"), 0, 120); setPersistenceRequired();
    }
    @Override public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        var procedure = new AnimationController<>(this, "procedure", 4, state -> PlayState.STOP).triggerableAnim("idlejunkyard2", RawAnimation.begin().thenPlay("idlejunkyard2"));
        controllers.add(new AnimationController<>(this, "movement", 4, state -> procedure.isPlayingTriggeredAnimation() ? PlayState.STOP : state.setAndContinue(RawAnimation.begin().thenLoop("idlejunkyard")))); controllers.add(procedure);
    }
    @Override public AnimatableInstanceCache getAnimatableInstanceCache() { return animations; }
}
