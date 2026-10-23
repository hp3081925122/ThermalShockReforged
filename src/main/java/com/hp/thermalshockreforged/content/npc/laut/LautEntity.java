package com.hp.thermalshockreforged.content.npc.laut;
import com.hp.thermalshockreforged.compat.LegacyAnimationController;
import com.geckolib.animatable.manager.AnimatableManager;

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
import net.minecraft.world.level.Level;
import com.geckolib.animatable.GeoEntity;
import com.geckolib.animatable.instance.AnimatableInstanceCache;
import com.geckolib.animation.*;
import com.geckolib.util.GeckoLibUtil;

public final class LautEntity extends PathfinderMob implements GeoEntity {
    private static final EntityDataAccessor<Boolean> COMPLETED = SynchedEntityData.defineId(LautEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<String> TEXTURE = SynchedEntityData.defineId(LautEntity.class, EntityDataSerializers.STRING);
    private final AnimatableInstanceCache animations = GeckoLibUtil.createInstanceCache(this);
    public LautEntity(EntityType<? extends LautEntity> type, Level level) { super(type, level); xpReward = 0; setCustomName(Component.translatable("entity.thermal_shock_reforged.laut").withStyle(ChatFormatting.GREEN)); setCustomNameVisible(true); setPersistenceRequired(); }
    public static AttributeSupplier.Builder createAttributes() { return Mob.createMobAttributes().add(Attributes.MAX_HEALTH, 75).add(Attributes.ARMOR, 30).add(Attributes.MOVEMENT_SPEED, 0).add(Attributes.ATTACK_DAMAGE, 0).add(Attributes.FOLLOW_RANGE, 16).add(Attributes.KNOCKBACK_RESISTANCE, 1).add(Attributes.STEP_HEIGHT, 0); }
    @Override protected void defineSynchedData(SynchedEntityData.Builder builder) { super.defineSynchedData(builder); builder.define(COMPLETED, false); builder.define(TEXTURE, "laut"); }
    @Override protected void registerGoals() { }
    public boolean completed() { return entityData.get(COMPLETED); }
    public String texture() { return entityData.get(TEXTURE); }
    boolean claim() { if (completed() || !isAlive()) return false; entityData.set(COMPLETED, true); getPersistentData().putBoolean("hasplenkys", true); return true; }
    void release() { entityData.set(COMPLETED, false); getPersistentData().putBoolean("hasplenkys", false); }
    @Override public boolean removeWhenFarAway(double distance) { return false; }
    public boolean isInvulnerableTo(DamageSource source) {
        if (source.is(net.minecraft.tags.DamageTypeTags.BYPASSES_INVULNERABILITY)) return false;
        return !(source.getEntity() instanceof net.minecraft.world.entity.player.Player player && player.isCreative());
    }
    @Override protected InteractionResult mobInteract(Player player, InteractionHand hand) {
        super.mobInteract(player, hand);
        if (!completed() && player instanceof ServerPlayer serverPlayer) LautContent.open(serverPlayer, this, blockPosition(), LautQuest.available(serverPlayer) >= 3 ? LautPage.HAVE : LautPage.NEED);
        return InteractionResult.SUCCESS;
    }
    @Override protected void tickDeath() { if (++deathTime >= 20) remove(RemovalReason.KILLED); }
    @Override public void addAdditionalSaveData(net.minecraft.world.level.storage.ValueOutput output) { super.addAdditionalSaveData(output); output.putString("Texture", texture()); output.putBoolean("LautHasPlenkys", completed()); }
    @Override public void readAdditionalSaveData(net.minecraft.world.level.storage.ValueInput input) {
        super.readAdditionalSaveData(input);
if (input.getString("Texture").isPresent()) entityData.set(TEXTURE, input.getStringOr("Texture", ""));
        entityData.set(COMPLETED, input.getBooleanOr("LautHasPlenkys", false) || getPersistentData().getBoolean("hasplenkys"));
        setPersistenceRequired();
    }
    @Override public void registerControllers(AnimatableManager.ControllerRegistrar controllers) { controllers.add(new LegacyAnimationController<>(this, "movement", 4, state -> state.setAndContinue(RawAnimation.begin().thenLoop(completed() ? "1" : "0")))); }
    @Override public AnimatableInstanceCache getAnimatableInstanceCache() { return animations; }
}
