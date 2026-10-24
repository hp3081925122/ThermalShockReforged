package com.hp.thermalshockreforged.entity.sentrarius;
import com.geckolib.animation.object.PlayState;
import com.hp.thermalshockreforged.compat.LegacyAnimationController;
import com.geckolib.animatable.manager.AnimatableManager;

import com.hp.thermalshockreforged.content.utility.UtilityContent;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.*;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.*;
import net.minecraft.world.*;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.*;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import com.geckolib.animatable.GeoEntity;
import com.geckolib.animatable.instance.AnimatableInstanceCache;
import com.geckolib.animation.*;
import com.geckolib.util.GeckoLibUtil;

public final class SentrariusEntity extends PathfinderMob implements GeoEntity {
    private static final EntityDataAccessor<String> TEXTURE = SynchedEntityData.defineId(SentrariusEntity.class, EntityDataSerializers.STRING);
    private final AnimatableInstanceCache animations = GeckoLibUtil.createInstanceCache(this);
    private int feeding;
    private boolean experiencePending;
    private UUID experienceAttacker;
    public SentrariusEntity(EntityType<? extends SentrariusEntity> type, Level level) { super(type, level); xpReward = 3; }
    public static AttributeSupplier.Builder createAttributes() { return Mob.createMobAttributes().add(Attributes.TEMPT_RANGE, 10).add(Attributes.MAX_HEALTH, 20).add(Attributes.ARMOR, 8).add(Attributes.ATTACK_DAMAGE, 0).add(Attributes.MOVEMENT_SPEED, .15).add(Attributes.FOLLOW_RANGE, 16).add(Attributes.KNOCKBACK_RESISTANCE, .5).add(Attributes.STEP_HEIGHT, 1); }
    @Override protected void defineSynchedData(SynchedEntityData.Builder builder) { super.defineSynchedData(builder); builder.define(TEXTURE, "sentariusmouth"); }
    public String texture() { return entityData.get(TEXTURE); }
    public void setTexture(String texture) { entityData.set(TEXTURE, texture); }
    @Override protected void registerGoals() { goalSelector.addGoal(1, new TemptGoal(this, 1, Ingredient.of(Items.BONE), false)); goalSelector.addGoal(2, new TemptGoal(this, 1, Ingredient.of(Blocks.BONE_BLOCK.asItem()), false)); goalSelector.addGoal(3, new FollowMobGoal(this, 1, 10, 5)); goalSelector.addGoal(4, new RandomStrollGoal(this, 1)); goalSelector.addGoal(5, new PanicGoal(this, 1.2)); goalSelector.addGoal(6, new FloatGoal(this)); }
    @Override protected InteractionResult mobInteract(Player player, InteractionHand hand) {
        super.mobInteract(player, hand);
        if (!level().isClientSide() && feeding == 0 && player.getMainHandItem().is(Items.BONE)) {
            feeding = 30;
            player.getInventory().clearOrCountMatchingItems(stack -> stack.is(Items.BONE), 1, player.inventoryMenu.getCraftSlots());
            level().playSound(null, blockPosition(), SoundEvents.EVOKER_FANGS_ATTACK, SoundSource.NEUTRAL, 1, 1);
            triggerAnim("procedure", "bite");
        }
        return InteractionResult.SUCCESS;
    }
    @Override protected void tickEffects() { super.tickEffects(); if (!level().isClientSide() && feeding > 0 && --feeding == 0) finishFeeding(); }
    @Override public boolean removeAllEffects() { boolean removed = super.removeAllEffects(); if (!level().isClientSide() && feeding > 0) { feeding = 0; finishFeeding(); return true; } return removed; }
    private void finishFeeding() {
        if (!(level() instanceof ServerLevel server)) return;
        server.playSound(null, blockPosition(), SoundEvents.STEM_PLACE, SoundSource.NEUTRAL, 1, 2);
        for (var drop : com.hp.thermalshockreforged.content.loot.LootContent.roll(server, "gameplay/sentrarius_feeding", this)) com.hp.thermalshockreforged.content.loot.LootContent.spawn(server, position().add(0, 1, 0), drop, 10);
    }
    @Override public void aiStep() { super.aiStep(); updateSwingTime(); }
    @Override protected void playStepSound(BlockPos pos, BlockState state) { playSound(SoundEvents.RAVAGER_STEP, .15F, 1); }
    @Override protected SoundEvent getHurtSound(DamageSource source) { return SoundEvents.GENERIC_HURT; }
    @Override protected SoundEvent getDeathSound() { return SoundEvents.GENERIC_DEATH; }
    @Override protected void dropExperience(net.minecraft.server.level.ServerLevel level, Entity attacker) { experiencePending = true; experienceAttacker = attacker == null ? null : attacker.getUUID(); }
    @Override protected void tickDeath() { if (++deathTime >= 20 && !isRemoved()) { remove(RemovalReason.KILLED); if (experiencePending && level() instanceof ServerLevel server) { experiencePending = false; super.dropExperience(server, experienceAttacker == null ? null : server.getEntity(experienceAttacker)); } } }
    @Override public void addAdditionalSaveData(net.minecraft.world.level.storage.ValueOutput output) { super.addAdditionalSaveData(output); output.putString("Texture", texture()); output.putInt("SentrariusFeeding", feeding); output.putBoolean("SentrariusExperiencePending", experiencePending); if (experienceAttacker != null) output.putString("SentrariusExperienceAttacker", experienceAttacker.toString()); }
    @Override public void readAdditionalSaveData(net.minecraft.world.level.storage.ValueInput input) { super.readAdditionalSaveData(input); if (input.read("Texture", com.mojang.serialization.Codec.PASSTHROUGH).isPresent()) setTexture(input.getStringOr("Texture", "")); feeding = Math.clamp(input.getIntOr("SentrariusFeeding", 0), 0, 30); experiencePending = input.getBooleanOr("SentrariusExperiencePending", false); experienceAttacker = input.getString("SentrariusExperienceAttacker").map(java.util.UUID::fromString).orElse(null); }
    @Override public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        var procedure = new LegacyAnimationController<>(this, "procedure", 4, state -> PlayState.STOP).triggerableAnim("bite", RawAnimation.begin().thenPlay("bite"));
        controllers.add(new LegacyAnimationController<>(this, "movement", 4, state -> procedure.isPlayingTriggeredAnimation() ? PlayState.STOP : state.setAndContinue(RawAnimation.begin().thenLoop(state.isMoving() || Math.abs(state.getLimbSwingAmount()) >= .15F ? "walk" : "idle")))); controllers.add(procedure);
    }
    @Override public AnimatableInstanceCache getAnimatableInstanceCache() { return animations; }
}
