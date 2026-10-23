package com.hp.thermalshockreforged.entity.icidroviathan;
import net.minecraft.world.InteractionResult;

import com.hp.thermalshockreforged.content.decor.nativeblocks.NativeDecorContent;
import com.hp.thermalshockreforged.entity.base.AnimatedPathfinderEntity;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.*;
import net.minecraft.world.*;
import net.minecraft.world.damagesource.*;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public final class IcidroviathanEntity extends AnimatedPathfinderEntity {
    private static final Identifier TERRAIN_SPEED = Identifier.fromNamespaceAndPath("thermal_shock_reforged", "icidroviathan_terrain_speed");
    private int terrainSlowTicks;
    public IcidroviathanEntity(EntityType<? extends IcidroviathanEntity> type, Level level) { super(type, level, "icidroviathan_slagged", 0); setPersistenceRequired(); }
    public static AttributeSupplier.Builder attributes() {
        return Mob.createMobAttributes().add(Attributes.MAX_HEALTH, 255).add(Attributes.ARMOR, 16).add(Attributes.ATTACK_DAMAGE, 0)
            .add(Attributes.MOVEMENT_SPEED, .15).add(Attributes.FOLLOW_RANGE, 16).add(Attributes.STEP_HEIGHT, 0);
    }
    @Override protected String movementAnimation(boolean moving) { return moving ? "swim" : "idle"; }
    @Override protected List<String> procedureAnimations() { return List.of("idle", "swim"); }
    @Override public boolean removeWhenFarAway(double distance) { return false; }
    @Override public boolean canCollideWith(Entity entity) { return true; }
    public boolean canBeCollidedWith() { return true; }
    @Override public boolean isPushable() { return false; }
    @Override protected void doPush(Entity entity) { }
    @Override protected void pushEntities() { }
    @Override public LivingEntity getControllingPassenger() { return getFirstPassenger() instanceof LivingEntity living ? living : null; }
    @Override protected Vec3 getPassengerAttachmentPoint(Entity passenger, EntityDimensions dimensions, float scale) { return new Vec3(0, dimensions.height() * .75 - .5, 0); }
    @Override protected void tickRidden(Player player, Vec3 input) { super.tickRidden(player, input); followRider(player); }
    @Override protected Vec3 getRiddenInput(Player player, Vec3 input) { return new Vec3(0, 0, player.zza); }
    @Override protected float getRiddenSpeed(Player player) { return (float)getAttributeValue(Attributes.MOVEMENT_SPEED); }
    private void followRider(LivingEntity rider) { setRot(rider.getYRot(), rider.getXRot() * .5F); yRotO = yBodyRot = yHeadRot = getYRot(); }
    @Override public void travel(Vec3 input) {
        var rider = getControllingPassenger();
        if (rider != null && !(rider instanceof Player)) { followRider(rider); setSpeed((float)getAttributeValue(Attributes.MOVEMENT_SPEED)); super.travel(new Vec3(0, 0, rider.zza)); }
        else super.travel(input);
    }
    @Override protected InteractionResult mobInteract(Player player, InteractionHand hand) {
        var result = super.mobInteract(player, hand);
        if (result.consumesAction() || hand != InteractionHand.MAIN_HAND || !isAlive()) return result;
        if (level().isClientSide()) return InteractionResult.SUCCESS;
        return player.getVehicle() == this || player.startRiding(this) ? InteractionResult.CONSUME : InteractionResult.PASS;
    }
    @Override public void baseTick() {
        super.baseTick();
        if (level().isClientSide()) return;
        if (!level().getBlockState(BlockPos.containing(getX(), getY() - 1, getZ())).is(NativeDecorContent.MANTLESLAG.get())) terrainSlowTicks = 5;
        else if (terrainSlowTicks > 0) --terrainSlowTicks;
        updateTerrainSpeed();
    }
    private void updateTerrainSpeed() {
        var speed = getAttribute(Attributes.MOVEMENT_SPEED);
        var actual = getEffect(MobEffects.SLOWNESS);
        double factor = actual == null ? 1 : 1 - (double).15F * (actual.getAmplifier() + 1);
        double limit = 1 - (double).15F * 4;
        double amount = terrainSlowTicks > 0 && factor > limit ? limit / factor - 1 : 0;
        var previous = speed.getModifier(TERRAIN_SPEED);
        if (previous != null && previous.amount() == amount) return;
        if (previous != null) speed.removeModifier(TERRAIN_SPEED);
        if (amount != 0) speed.addTransientModifier(new AttributeModifier(TERRAIN_SPEED, amount, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
    }
    @Override public boolean removeAllEffects() { boolean result = super.removeAllEffects(); if (!level().isClientSide() && terrainSlowTicks > 0) { terrainSlowTicks = 0; updateTerrainSpeed(); return true; } return result; }
    @Override public boolean hurtServer(net.minecraft.server.level.ServerLevel level, DamageSource source, float amount) { return !source.is(DamageTypes.IN_FIRE) && !source.is(DamageTypes.FALL) && !source.is(DamageTypes.DROWN) && super.hurtServer(level, source, amount); }
    @Override protected SoundEvent getHurtSound(DamageSource source) { return SoundEvents.GENERIC_HURT; }
    @Override protected SoundEvent getDeathSound() { return SoundEvents.GENERIC_DEATH; }
    @Override public void addAdditionalSaveData(net.minecraft.world.level.storage.ValueOutput output) { super.addAdditionalSaveData(output); output.putInt("IcidroviathanTerrainSlow", terrainSlowTicks); }
    @Override public void readAdditionalSaveData(net.minecraft.world.level.storage.ValueInput input) { super.readAdditionalSaveData(input); terrainSlowTicks = Math.clamp(input.getIntOr("IcidroviathanTerrainSlow", 0), 0, 5); updateTerrainSpeed(); }
}
