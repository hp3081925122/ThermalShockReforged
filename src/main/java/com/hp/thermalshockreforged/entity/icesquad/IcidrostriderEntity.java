package com.hp.thermalshockreforged.entity.icesquad;

import com.hp.thermalshockreforged.ThermalShockReforged;
import com.hp.thermalshockreforged.mixin.ArrowPiercingAccess;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.*;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.*;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.*;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.*;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.ThrownPotion;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.*;
import software.bernie.geckolib.util.GeckoLibUtil;
import java.util.UUID;

public final class IcidrostriderEntity extends Monster implements GeoEntity {
    private static final EntityDataAccessor<String> TEXTURE = SynchedEntityData.defineId(IcidrostriderEntity.class, EntityDataSerializers.STRING);
    private final AnimatableInstanceCache animations = GeckoLibUtil.createInstanceCache(this);
    private long nextFireAt;
    private boolean experiencePending;
    private UUID experienceAttacker;
    public IcidrostriderEntity(EntityType<? extends IcidrostriderEntity> type, Level level) { super(type, level); xpReward = 0; setPersistenceRequired(); }
    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes().add(Attributes.MOVEMENT_SPEED, 0.15).add(Attributes.MAX_HEALTH, 25).add(Attributes.ARMOR, 0)
                .add(Attributes.ATTACK_DAMAGE, 1).add(Attributes.FOLLOW_RANGE, 16).add(Attributes.KNOCKBACK_RESISTANCE, 1).add(Attributes.STEP_HEIGHT, 1);
    }
    @Override protected void defineSynchedData(SynchedEntityData.Builder builder) { super.defineSynchedData(builder); builder.define(TEXTURE, "icidrostider"); }
    public String texture() { return entityData.get(TEXTURE); }
    public void setTexture(String texture) { entityData.set(TEXTURE, texture); }
    public void setAnimation(String animation) { if (!"undefined".equals(animation) && !"empty".equals(animation)) triggerAnim("procedure", animation); }
    @Override public boolean removeWhenFarAway(double distance) { return false; }
    @Override public boolean canCollideWith(Entity entity) { return true; }
    @Override public boolean canBeCollidedWith() { return true; }
    @Override public boolean doHurtTarget(Entity target) { return false; }
    @Override public boolean hurt(DamageSource source, float amount) {
        return !(source.getDirectEntity() instanceof AbstractArrow) && !(source.getDirectEntity() instanceof ThrownPotion) && !(source.getDirectEntity() instanceof AreaEffectCloud)
                && !source.is(DamageTypes.FALL) && !source.is(DamageTypes.CACTUS) && !source.is(DamageTypes.DROWN) && !source.is(DamageTypes.DRAGON_BREATH) && super.hurt(source, amount);
    }
    @Override protected void dropCustomDeathLoot(ServerLevel server, DamageSource source, boolean recentlyHit) { super.dropCustomDeathLoot(server, source, recentlyHit); spawnAtLocation(new ItemStack(IceSquadContent.STRIDER_CONSTRUCTOR.get())); }
    @Override protected SoundEvent getHurtSound(DamageSource source) { return SoundEvents.IRON_GOLEM_DAMAGE; }
    @Override protected SoundEvent getDeathSound() { return SoundEvents.IRON_GOLEM_DAMAGE; }
    @Override protected void playStepSound(BlockPos pos, BlockState state) { playSound(SoundEvents.IRON_GOLEM_STEP, 0.15F, 1); }
    @Override public InteractionResult mobInteract(Player player, InteractionHand hand) {
        if (!isAlive()) return InteractionResult.PASS;
        if (!level().isClientSide && player.startRiding(this)) {
            level().playSound(null, blockPosition(), SoundEvents.IRON_GOLEM_REPAIR, SoundSource.NEUTRAL, 0.5F, 2.5F);
            player.displayClientMessage(Component.translatable("message.thermal_shock_reforged.strider_controls", Component.keybind("key.thermal_shock_reforged.icidrostrider_attack")), true);
        }
        return InteractionResult.sidedSuccess(level().isClientSide);
    }
    @Override public LivingEntity getControllingPassenger() { return getFirstPassenger() instanceof LivingEntity passenger ? passenger : null; }
    private void followPassenger(LivingEntity passenger) {
        setYRot(passenger.getYRot()); yRotO = getYRot(); setXRot(passenger.getXRot() * 0.5F);
        setRot(getYRot(), getXRot()); yBodyRot = passenger.getYRot(); yHeadRot = passenger.getYRot();
    }
    @Override protected void tickRidden(Player player, Vec3 movement) { super.tickRidden(player, movement); followPassenger(player); }
    @Override protected Vec3 getRiddenInput(Player player, Vec3 movement) { return new Vec3(player.xxa, 0, player.zza); }
    @Override protected float getRiddenSpeed(Player player) { return (float)getAttributeValue(Attributes.MOVEMENT_SPEED); }
    @Override public void travel(Vec3 movement) {
        LivingEntity passenger = getControllingPassenger();
        if (passenger != null && !(passenger instanceof Player)) {
            followPassenger(passenger); setSpeed((float)getAttributeValue(Attributes.MOVEMENT_SPEED));
            super.travel(new Vec3(passenger.xxa, 0, passenger.zza)); calculateEntityAnimation(true);
        } else super.travel(movement);
    }
    public void requestFire(Player rider) {
        if (!(level() instanceof ServerLevel server) || !isAlive() || !rider.isAlive() || rider.getVehicle() != this || getControllingPassenger() != rider || server.getGameTime() < nextFireAt) return;
        nextFireAt = server.getGameTime() + 10;
        setAnimation("attack");
        server.playSound(null, rider.blockPosition(), SoundEvents.EVOKER_FANGS_ATTACK, SoundSource.NEUTRAL, 0.4F, 3);
        IceSquadProjectileEntity projectile = IceSquadContent.STRIDER_ATTACK.get().create(server);
        if (projectile == null) { ThermalShockReforged.LOGGER.debug("Strider projectile creation failed: strider={}, rider={}", getUUID(), rider.getUUID()); return; }
        projectile.setOwner(rider); projectile.configure(this, false, 3.5, 0);
        ((ArrowPiercingAccess)(Object)projectile).thermalShockReforged$setPierceLevel((byte)5); projectile.setCritArrow(true);
        projectile.setPos(rider.position());
        Vec3 look = rider.getLookAngle(); projectile.shoot(look.x, 0, look.z, 1, 0);
        server.addFreshEntity(projectile);
    }
    @Override protected void dropExperience(Entity attacker) { experiencePending = true; experienceAttacker = attacker == null ? null : attacker.getUUID(); }
    @Override protected void tickDeath() {
        if (++deathTime >= 1 && !isRemoved()) {
            remove(RemovalReason.KILLED);
            if (experiencePending && level() instanceof ServerLevel server) { experiencePending = false; super.dropExperience(experienceAttacker == null ? null : server.getEntity(experienceAttacker)); }
        }
    }
    @Override public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag); tag.putString("Texture", texture()); tag.putInt("StriderFireCooldown", (int)Math.clamp(nextFireAt - level().getGameTime(), 0L, 10L));
        tag.putBoolean("StriderExperiencePending", experiencePending); if (experienceAttacker != null) tag.putUUID("StriderExperienceAttacker", experienceAttacker);
    }
    @Override public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag); setPersistenceRequired(); if (tag.contains("Texture")) setTexture(tag.getString("Texture"));
        nextFireAt = level().getGameTime() + Math.clamp(tag.getInt("StriderFireCooldown"), 0, 10);
        experiencePending = tag.getBoolean("StriderExperiencePending"); experienceAttacker = tag.hasUUID("StriderExperienceAttacker") ? tag.getUUID("StriderExperienceAttacker") : null;
    }
    @Override public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        var procedure = new AnimationController<>(this, "procedure", 0, state -> PlayState.STOP)
                .triggerableAnim("attack", RawAnimation.begin().thenPlay("attack")).triggerableAnim("walk", RawAnimation.begin().thenPlay("walk")).triggerableAnim("idle", RawAnimation.begin().thenPlay("idle"));
        controllers.add(new AnimationController<>(this, "movement", 0, state -> {
            if (procedure.isPlayingTriggeredAnimation()) return PlayState.STOP;
            return state.setAndContinue(RawAnimation.begin().thenLoop(state.isMoving() || Math.abs(state.getLimbSwingAmount()) >= 0.15F ? "walk" : "idle"));
        })); controllers.add(procedure);
    }
    @Override public AnimatableInstanceCache getAnimatableInstanceCache() { return animations; }
}
