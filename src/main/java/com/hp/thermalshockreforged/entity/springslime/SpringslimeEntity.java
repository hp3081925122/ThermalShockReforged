package com.hp.thermalshockreforged.entity.springslime;

import com.hp.thermalshockreforged.ThermalShockReforged;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.*;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.*;
import net.minecraft.world.damagesource.*;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.*;
import software.bernie.geckolib.util.GeckoLibUtil;

public final class SpringslimeEntity extends PathfinderMob implements GeoEntity {
    private static final EntityDataAccessor<Boolean> POPPED = SynchedEntityData.defineId(SpringslimeEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<String> TEXTURE = SynchedEntityData.defineId(SpringslimeEntity.class, EntityDataSerializers.STRING);
    private final AnimatableInstanceCache animations = GeckoLibUtil.createInstanceCache(this);
    public SpringslimeEntity(EntityType<? extends SpringslimeEntity> type, Level level) { super(type, level); xpReward = 0; }
    public static AttributeSupplier.Builder createAttributes() { return Mob.createMobAttributes().add(Attributes.MAX_HEALTH, 1).add(Attributes.MOVEMENT_SPEED, 0).add(Attributes.ARMOR, 0).add(Attributes.ATTACK_DAMAGE, 0).add(Attributes.FOLLOW_RANGE, 16).add(Attributes.KNOCKBACK_RESISTANCE, 1).add(Attributes.STEP_HEIGHT, 0); }
    @Override protected void defineSynchedData(SynchedEntityData.Builder builder) { super.defineSynchedData(builder); builder.define(POPPED, false); builder.define(TEXTURE, "new_splat"); }
    @Override protected void registerGoals() { }
    public String texture() { return entityData.get(TEXTURE); }
    @Override public boolean hurt(DamageSource source, float amount) { return !source.is(DamageTypes.FALL) && super.hurt(source, amount); }
    @Override protected SoundEvent getHurtSound(DamageSource source) { return SoundEvents.SLIME_HURT; }
    @Override protected SoundEvent getDeathSound() { return SoundEvents.SLIME_DEATH; }
    @Override protected void tickDeath() { if (++deathTime >= 15) remove(RemovalReason.KILLED); }
    @Override public void playerTouch(Player player) {
        super.playerTouch(player);
        if (!isAlive()) return;
        if (level().isClientSide) { if (player.isLocalPlayer()) { player.setDeltaMovement(0, 1.4, 0); triggerAnim("procedure", "2"); } return; }
        if (!(level() instanceof ServerLevel server)) return;
        if (!entityData.get(POPPED)) {
            if (!SpringFadeEntity.submit(server, this)) return;
            entityData.set(POPPED, true);
        }
        triggerAnim("procedure", "2");
        ThermalShockReforged.LOGGER.debug("Springslime collision animation triggered: entity={}, player={}, popped={}", getUUID(), player.getUUID(), entityData.get(POPPED));
        player.setDeltaMovement(0, 1.4, 0); player.hurtMarked = true;
        server.playSound(null, blockPosition(), SoundEvents.SLIME_SQUISH, SoundSource.NEUTRAL, 1, 1);
    }
    @Override public void addAdditionalSaveData(CompoundTag tag) { super.addAdditionalSaveData(tag); tag.putString("Texture", texture()); tag.putBoolean("SpringPopped", entityData.get(POPPED)); }
    @Override public void readAdditionalSaveData(CompoundTag tag) { super.readAdditionalSaveData(tag); if (tag.contains("Texture")) entityData.set(TEXTURE, tag.getString("Texture")); entityData.set(POPPED, tag.getBoolean("SpringPopped")); }
    @Override public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        var procedure = new AnimationController<>(this, "procedure", 4, state -> PlayState.STOP).triggerableAnim("2", RawAnimation.begin().thenPlay("2"));
        controllers.add(new AnimationController<>(this, "movement", 4, state -> procedure.isPlayingTriggeredAnimation() ? PlayState.STOP : isDeadOrDying() ? state.setAndContinue(RawAnimation.begin().thenPlay("2")) : state.setAndContinue(RawAnimation.begin().thenLoop("1"))));
        controllers.add(procedure);
    }
    @Override public AnimatableInstanceCache getAnimatableInstanceCache() { return animations; }
}
