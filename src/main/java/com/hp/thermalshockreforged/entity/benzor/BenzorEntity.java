package com.hp.thermalshockreforged.entity.benzor;

import com.hp.thermalshockreforged.content.npc.benzor.*;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.*;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.*;
import net.minecraft.world.*;
import net.minecraft.world.damagesource.*;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.*;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.*;
import net.minecraft.world.level.*;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.*;
import software.bernie.geckolib.util.GeckoLibUtil;

public final class BenzorEntity extends PathfinderMob implements GeoEntity {
    private static final EntityDataAccessor<String> TEXTURE = SynchedEntityData.defineId(BenzorEntity.class, EntityDataSerializers.STRING);
    private final AnimatableInstanceCache animations = GeckoLibUtil.createInstanceCache(this);
    public BenzorEntity(EntityType<? extends BenzorEntity> type, Level level) { super(type, level); xpReward = 0; setCustomName(Component.translatable("entity.thermal_shock_reforged.benzor.display_name").withStyle(ChatFormatting.GREEN)); setCustomNameVisible(true); setPersistenceRequired(); }
    public static AttributeSupplier.Builder createAttributes() { return Mob.createMobAttributes().add(Attributes.MAX_HEALTH, 75).add(Attributes.ARMOR, 64).add(Attributes.MOVEMENT_SPEED, 0).add(Attributes.ATTACK_DAMAGE, 0).add(Attributes.FOLLOW_RANGE, 16).add(Attributes.KNOCKBACK_RESISTANCE, 1).add(Attributes.STEP_HEIGHT, 0); }
    @Override protected void defineSynchedData(SynchedEntityData.Builder builder) { super.defineSynchedData(builder); builder.define(TEXTURE, "benzor"); }
    public String texture() { return entityData.get(TEXTURE); }
    public void setTexture(String texture) { entityData.set(TEXTURE, texture); }
    @Override protected void registerGoals() { goalSelector.addGoal(1, new LookAtPlayerGoal(this, Player.class, 6)); }
    @Override public boolean canCollideWith(Entity entity) { return true; }
    @Override public boolean canBeCollidedWith() { return true; }
    @Override public boolean removeWhenFarAway(double distance) { return false; }
    @Override public boolean isInvulnerableTo(DamageSource source) {
        if (source.is(net.minecraft.tags.DamageTypeTags.BYPASSES_INVULNERABILITY)) return false;
        return !(source.getEntity() instanceof net.minecraft.world.entity.player.Player player && player.isCreative());
    }
    @Override public SpawnGroupData finalizeSpawn(ServerLevelAccessor world, DifficultyInstance difficulty, MobSpawnType reason, SpawnGroupData group) { var result = super.finalizeSpawn(world, difficulty, reason, group); setNoGravity(true); return result; }
    @Override protected InteractionResult mobInteract(Player player, InteractionHand hand) {
        super.mobInteract(player, hand);
        if (player instanceof ServerPlayer server) {
            if (isShiftKeyDown()) { setShiftKeyDown(false); level().playSound(null, blockPosition(), SoundEvents.WARDEN_EMERGE, SoundSource.NEUTRAL, .6F, 3); }
            else if (BenzorMenus.open(server, this, BenzorPage.MAIN)) server.getPersistentData().putBoolean(BenzorMenus.INFO, true);
        }
        return InteractionResult.sidedSuccess(level().isClientSide);
    }
    @Override public void aiStep() { super.aiStep(); updateSwingTime(); }
    @Override protected void tickDeath() { if (++deathTime >= 20 && !isRemoved()) remove(RemovalReason.KILLED); }
    @Override public void addAdditionalSaveData(CompoundTag tag) { super.addAdditionalSaveData(tag); tag.putString("Texture", texture()); }
    @Override public void readAdditionalSaveData(CompoundTag tag) { super.readAdditionalSaveData(tag); if (tag.contains("Texture")) setTexture(tag.getString("Texture")); setPersistenceRequired(); }
    @Override public void registerControllers(AnimatableManager.ControllerRegistrar controllers) { controllers.add(new AnimationController<>(this, "movement", 4, state -> state.setAndContinue(RawAnimation.begin().thenLoop(isShiftKeyDown() ? "idle" : "idle2")))); }
    @Override public AnimatableInstanceCache getAnimatableInstanceCache() { return animations; }
}
