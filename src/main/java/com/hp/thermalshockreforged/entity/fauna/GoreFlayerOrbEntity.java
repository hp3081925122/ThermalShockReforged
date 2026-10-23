package com.hp.thermalshockreforged.entity.fauna;
import net.minecraft.world.entity.projectile.arrow.AbstractArrow;

import com.hp.thermalshockreforged.ThermalShockReforged;
import com.hp.thermalshockreforged.world.timing.ServerEndTickTask;
import com.hp.thermalshockreforged.world.timing.ServerEndTickTasks;
import net.minecraft.server.level.ServerLevel;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.projectile.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public final class GoreFlayerOrbEntity extends AbstractArrow implements ItemSupplier, ServerEndTickTask {
    private int remainingTicks = 40;
    private boolean lifetimeStarted;

    public GoreFlayerOrbEntity(EntityType<? extends GoreFlayerOrbEntity> type, Level level) { super(type, level); }

    public GoreFlayerOrbEntity(LivingEntity owner, Level level) {
        super(FaunaEntities.GORE_FLAYER_ORB.get(), owner, level, new ItemStack(FaunaEntities.item("gore_projectile")), null);
    }

    @Override
    public ItemStack getItem() { return new ItemStack(FaunaEntities.item("gore_projectile")); }

    @Override
    protected ItemStack getDefaultPickupItem() { return new ItemStack(FaunaEntities.item("gore_projectile")); }

    @Override
    protected void doPostHurtEffects(LivingEntity entity) {
        super.doPostHurtEffects(entity);
        entity.setArrowCount(entity.getArrowCount() - 1);
    }

    @Override
    protected void doKnockback(LivingEntity entity, DamageSource source) {
        var direction = getDeltaMovement().multiply(1, 0, 1).normalize();
        if (direction.lengthSqr() > 0) entity.knockback(0.6, -direction.x, -direction.z);
    }

    @Override
    public void tick() {
        super.tick();
        setNoGravity(true);
        if (level().isClientSide()) level().addParticle(FaunaEntities.WHITE_ORB.get(), getX(), getY(), getZ(), 0, 0, 0);
        if (inGround) discard();
        if (!isRemoved() && level() instanceof ServerLevel server) {
            if (!lifetimeStarted) {
                lifetimeStarted = true;
                ThermalShockReforged.LOGGER.debug("Gore Flayer orb lifetime armed: projectile={}, remainingEndTicks={}", getUUID(), remainingTicks);
            }
            ServerEndTickTasks.schedule(server, getUUID());
        }
    }

    @Override
    public void onAddedToLevel() { super.onAddedToLevel(); if (lifetimeStarted && level() instanceof ServerLevel server) ServerEndTickTasks.schedule(server, getUUID()); }

    @Override
    public void endTick(ServerLevel server) {
        if (isRemoved() || !lifetimeStarted) return;
        if (--remainingTicks <= 0) discard();
        else ServerEndTickTasks.schedule(server, getUUID());
    }

    @Override
    public void addAdditionalSaveData(net.minecraft.world.level.storage.ValueOutput output) {
        super.addAdditionalSaveData(output);
        output.putInt("RemainingTicks", remainingTicks);
        output.putBoolean("GoreOrbLifetimeStarted", lifetimeStarted);
    }

    @Override
    public void readAdditionalSaveData(net.minecraft.world.level.storage.ValueInput input) {
        super.readAdditionalSaveData(input);
        remainingTicks = input.getString("RemainingTicks").isPresent() ? Math.clamp(input.getIntOr("RemainingTicks", 0), 1, 40) : 40;
        lifetimeStarted = input.getString("GoreOrbLifetimeStarted").isPresent() ? input.getBooleanOr("GoreOrbLifetimeStarted", false) : input.getString("RemainingTicks").isPresent();
    }

    public static GoreFlayerOrbEntity shoot(Level level, LivingEntity owner, float pullingPower) {
        var orb = new GoreFlayerOrbEntity(owner, level);
        var view = owner.getViewVector(1);
        orb.shoot(view.x, view.y, view.z, pullingPower * 0.2F, 0);
        orb.setSilent(true);
        orb.setCritArrow(false);
        orb.setBaseDamage(owner.getAttributeValue(Attributes.ATTACK_DAMAGE) * 5);
        level.addFreshEntity(orb);
        return orb;
    }

    public static GoreFlayerOrbEntity shoot(LivingEntity owner, LivingEntity target) {
        var orb = new GoreFlayerOrbEntity(owner, owner.level());
        double dx = target.getX() - owner.getX();
        double dz = target.getZ() - owner.getZ();
        double dy = target.getY() + target.getEyeHeight() - 1.1;
        orb.shoot(dx, dy - orb.getY() + Math.hypot(dx, dz) * 0.2, dz, 0.2F, 12);
        orb.setSilent(true);
        orb.setCritArrow(false);
        orb.setBaseDamage(owner.getAttributeValue(Attributes.ATTACK_DAMAGE) * 5);
        owner.level().addFreshEntity(orb);
        return orb;
    }
}
