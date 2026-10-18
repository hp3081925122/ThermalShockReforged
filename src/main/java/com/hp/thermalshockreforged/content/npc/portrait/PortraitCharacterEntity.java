package com.hp.thermalshockreforged.content.npc.portrait;

import com.hp.thermalshockreforged.content.atlas.AtlasContent;
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
    private static final String[] NAMES = {"§aNyx (ThermaDev)", "§aPente (Hexacant)", "§aMC (MintFox007)", "§eIbexEndeavors", "§aUnpleasant Gradient (ThermaDev)"};
    private final AnimatableInstanceCache animations = GeckoLibUtil.createInstanceCache(this);
    public PortraitCharacterEntity(EntityType<? extends PortraitCharacterEntity> type, Level level) { super(type, level); xpReward = 0; setCustomName(Component.literal(NAMES[0])); setCustomNameVisible(true); setPersistenceRequired(); }
    public static AttributeSupplier.Builder createAttributes() { return Mob.createMobAttributes().add(Attributes.MAX_HEALTH, 75).add(Attributes.ARMOR, 30).add(Attributes.KNOCKBACK_RESISTANCE, 1).add(Attributes.MOVEMENT_SPEED, 0).add(Attributes.ATTACK_DAMAGE, 0).add(Attributes.FOLLOW_RANGE, 16).add(Attributes.STEP_HEIGHT, 0); }
    @Override protected void defineSynchedData(SynchedEntityData.Builder builder) { super.defineSynchedData(builder); builder.define(TEXTURE, TEXTURES[0]); }
    public String texture() { return entityData.get(TEXTURE); }
    private void texture(String texture) { entityData.set(TEXTURE, texture); }
    @Override public void onSyncedDataUpdated(EntityDataAccessor<?> accessor) { super.onSyncedDataUpdated(accessor); if (TEXTURE.equals(accessor)) refreshDimensions(); }
    @Override protected EntityDimensions getDefaultDimensions(Pose pose) { return super.getDefaultDimensions(pose).scale(texture().equals(TEXTURES[0]) ? 1 : .5F).withEyeHeight(1.62F); }
    @Override protected void registerGoals() { super.registerGoals(); goalSelector.addGoal(1, new LookAtPlayerGoal(this, Player.class, 16)); }
    @Override public boolean removeWhenFarAway(double distance) { return false; }
    @Override public boolean hurt(DamageSource source, float amount) {
        if (source.getDirectEntity() instanceof AbstractArrow || source.getDirectEntity() instanceof ThrownPotion || source.getDirectEntity() instanceof AreaEffectCloud) return false;
        if (source.is(DamageTypes.IN_FIRE) || source.is(DamageTypes.FALL) || source.is(DamageTypes.CACTUS) || source.is(DamageTypes.DROWN) || source.is(DamageTypes.LIGHTNING_BOLT) || source.is(DamageTypes.EXPLOSION) || source.is(DamageTypes.TRIDENT) || source.is(DamageTypes.FALLING_ANVIL) || source.is(DamageTypes.DRAGON_BREATH) || source.is(DamageTypes.WITHER) || source.is(DamageTypes.WITHER_SKULL)) return false;
        return super.hurt(source, amount);
    }
    @Override protected InteractionResult mobInteract(Player player, InteractionHand hand) {
        super.mobInteract(player, hand);
        if (player instanceof ServerPlayer server) {
            if (player.getAbilities().instabuild && player.getMainHandItem().is(AtlasContent.KEYSTONE.get())) {
                int next = TEXTURES.length - 1;
                for (int i = 0; i < TEXTURES.length; i++) if (texture().equals(TEXTURES[i])) { next = (i + 1) % TEXTURES.length; break; }
                texture(TEXTURES[next]); setCustomName(Component.literal(NAMES[next]));
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
