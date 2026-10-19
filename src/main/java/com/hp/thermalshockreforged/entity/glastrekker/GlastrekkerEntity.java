package com.hp.thermalshockreforged.entity.glastrekker;

import com.hp.thermalshockreforged.ThermalShockReforged;
import com.hp.thermalshockreforged.content.MaterialContent;
import com.hp.thermalshockreforged.entity.base.AnimatedMonsterEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.*;
import net.minecraft.world.*;
import net.minecraft.world.damagesource.*;
import net.minecraft.world.effect.*;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.*;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.event.EventHooks;

public final class GlastrekkerEntity extends AnimatedMonsterEntity {
    private final boolean armored;
    public GlastrekkerEntity(EntityType<? extends GlastrekkerEntity> type, Level level, boolean armored) {
        super(type, level, armored ? "seatreadercorrialarmor" : "seatreader", 3);
        this.armored = armored;
        if (armored) setPersistenceRequired();
    }
    public boolean armored() { return armored; }
    @Override protected void registerGoals() {
        super.registerGoals();
        goalSelector.addGoal(1, new RandomStrollGoal(this, 1));
        goalSelector.addGoal(2, new RandomLookAroundGoal(this));
    }
    @Override public boolean removeWhenFarAway(double distance) { return !armored && super.removeWhenFarAway(distance); }
    @Override protected SoundEvent getHurtSound(DamageSource source) { return SoundEvents.GENERIC_HURT; }
    @Override protected SoundEvent getDeathSound() { return SoundEvents.GENERIC_DEATH; }
    @Override public boolean hurt(DamageSource source, float amount) {
        if (source.is(DamageTypes.FALL) || source.is(DamageTypes.DROWN)) return false;
        if (armored && (source.getDirectEntity() instanceof AbstractArrow || source.is(DamageTypes.CACTUS))) return false;
        return super.hurt(source, amount);
    }
    @Override public void baseTick() {
        super.baseTick();
        if (!level().isClientSide && isInWater()) {
            addEffect(new MobEffectInstance(MobEffects.DOLPHINS_GRACE, 2, 3, true, false));
            addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 2, 3, true, false));
        }
    }
    @Override public void playerTouch(Player player) {
        super.playerTouch(player);
        if (!armored && !level().isClientSide) player.addEffect(new MobEffectInstance(MobEffects.SLOW_FALLING, 1, 1, true, false));
    }
    @Override public InteractionResult mobInteract(Player player, InteractionHand hand) {
        if (!isAlive()) return InteractionResult.PASS;
        if (armored) {
            if (!level().isClientSide) player.startRiding(this);
        } else if (hand == InteractionHand.MAIN_HAND && player.getMainHandItem().is(MaterialContent.items().get("glastrekker_armor").get())) {
            if (level() instanceof ServerLevel server) upgrade(server, player);
        }
        return InteractionResult.sidedSuccess(level().isClientSide);
    }
    private void upgrade(ServerLevel server, Player player) {
        var replacement = GlastrekkerContent.ARMOURED_GLASTREKKER.get().create(server);
        if (replacement == null) return;
        BlockPos pos = blockPosition();
        replacement.moveTo(pos.getX() + .5, pos.getY(), pos.getZ() + .5, getYRot(), getXRot());
        EventHooks.finalizeMobSpawn(replacement, server, server.getCurrentDifficultyAt(pos), MobSpawnType.MOB_SUMMONED, null);
        if (replacement.isSpawnCancelled() || replacement.isRemoved()) { replacement.discard(); return; }
        replacement.setYRot(getYRot()); replacement.yBodyRot = getYRot(); replacement.yHeadRot = getYRot(); replacement.setXRot(getXRot());
        replacement.setDeltaMovement(Vec3.ZERO);
        replacement.setCustomName(getCustomName()); replacement.setCustomNameVisible(isCustomNameVisible());
        replacement.setHealth(getHealth() / getMaxHealth() * replacement.getMaxHealth());
        for (MobEffectInstance effect : getActiveEffects()) replacement.addEffect(new MobEffectInstance(effect));
        if (!server.addFreshEntity(replacement)) { replacement.discard(); return; }
        discard();
        if (!player.getAbilities().instabuild) player.getMainHandItem().shrink(1);
        server.playSound(null, pos, SoundEvents.HORSE_ARMOR, SoundSource.NEUTRAL, 1, 1);
        if (player instanceof ServerPlayer user) {
            var advancement = user.server.getAdvancements().get(ResourceLocation.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, "glastrekker_armor_get"));
            if (advancement != null) {
                var progress = user.getAdvancements().getOrStartProgress(advancement);
                if (!progress.isDone()) for (String criterion : progress.getRemainingCriteria()) user.getAdvancements().award(advancement, criterion);
            }
        }
    }
    @Override public LivingEntity getControllingPassenger() { return armored && getFirstPassenger() instanceof LivingEntity passenger ? passenger : null; }
    private void followPassenger(LivingEntity passenger) {
        setYRot(passenger.getYRot()); yRotO = getYRot(); setXRot(passenger.getXRot() * .5F);
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
    @Override protected Vec3 getPassengerAttachmentPoint(Entity passenger, EntityDimensions dimensions, float scale) {
        return new Vec3(0, dimensions.height() * .75 + (armored ? 1.2 : 0), 0);
    }
    public static AttributeSupplier.Builder attributes(boolean armored) {
        return createMobAttributes().add(Attributes.MAX_HEALTH, 70).add(Attributes.MOVEMENT_SPEED, armored ? .35 : .3)
            .add(Attributes.ARMOR, armored ? 9 : 4).add(Attributes.ATTACK_DAMAGE, 3).add(Attributes.FOLLOW_RANGE, 32)
            .add(Attributes.KNOCKBACK_RESISTANCE, 1).add(Attributes.STEP_HEIGHT, 2);
    }
}
