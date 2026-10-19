package com.hp.thermalshockreforged.entity.projectile;

import com.hp.thermalshockreforged.ThermalShockReforged;
import com.hp.thermalshockreforged.world.timing.ServerEndTickTask;
import com.hp.thermalshockreforged.world.timing.ServerEndTickTasks;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.ItemSupplier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public abstract class LegacyArrowEntity extends AbstractArrow implements ItemSupplier, ServerEndTickTask {
    private double impactKnockback;
    private int remainingLifetime = -1;

    protected LegacyArrowEntity(EntityType<? extends LegacyArrowEntity> type, Level level) { super(type, level); pickup = Pickup.DISALLOWED; }
    protected LegacyArrowEntity(EntityType<? extends LegacyArrowEntity> type, LivingEntity owner, Level level, ItemStack item) {
        this(type, level);
        setPos(owner.getX(), owner.getEyeY() - .1F, owner.getZ());
        setPickupItemStack(item);
        setOwner(owner);
    }
    protected abstract ItemStack projectileItem();
    protected boolean fliesWithoutGravity() { return false; }
    protected int lifetimeTicks() { return 0; }
    @Override public ItemStack getItem() { return projectileItem(); }
    @Override protected ItemStack getDefaultPickupItem() { return projectileItem(); }
    @Override protected ItemStack getPickupItem() { return ItemStack.EMPTY; }
    @Override public void setOwner(Entity owner) { super.setOwner(owner); pickup = Pickup.DISALLOWED; }
    @Override protected void doPostHurtEffects(LivingEntity entity) { super.doPostHurtEffects(entity); entity.setArrowCount(Math.max(0, entity.getArrowCount() - 1)); }
    protected void setImpactKnockback(double value) { impactKnockback = Double.isFinite(value) ? Math.clamp(value, 0, 20) : 0; }
    @Override protected void doKnockback(LivingEntity target, DamageSource source) {
        Vec3 direction = getDeltaMovement().multiply(1, 0, 1).normalize();
        double resistance = Math.max(0, 1 - target.getAttributeValue(Attributes.KNOCKBACK_RESISTANCE));
        if (direction.lengthSqr() > 0 && resistance > 0 && impactKnockback > 0) {
            Vec3 impulse = direction.scale(impactKnockback * .6 * resistance);
            target.push(impulse.x, .1, impulse.z);
            target.hurtMarked = true;
        }
    }
    @Override public void tick() {
        super.tick();
        if (fliesWithoutGravity()) setNoGravity(true);
        if (inGround) discard();
        if (!isRemoved() && lifetimeTicks() > 0 && level() instanceof ServerLevel server) {
            if (remainingLifetime < 0) remainingLifetime = lifetimeTicks();
            ServerEndTickTasks.schedule(server, getUUID());
        }
    }
    @Override public void endTick(ServerLevel level) { if (!isRemoved() && remainingLifetime > 0 && --remainingLifetime == 0) discard(); }
    @Override public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putDouble("ImpactKnockback", impactKnockback);
        tag.putInt("ProjectileRemainingLifetime", remainingLifetime);
    }
    @Override public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        setImpactKnockback(tag.getDouble("ImpactKnockback"));
        remainingLifetime = tag.contains("ProjectileRemainingLifetime") ? Math.clamp(tag.getInt("ProjectileRemainingLifetime"), -1, lifetimeTicks()) : -1;
        if (remainingLifetime == 0 && lifetimeTicks() > 0) remainingLifetime = 1;
        pickup = Pickup.DISALLOWED;
        if (!Double.isFinite(getBaseDamage()) || getBaseDamage() < 0) {
            ThermalShockReforged.LOGGER.debug("Reset invalid projectile base damage for {}", getUUID());
            setBaseDamage(0);
        }
    }
}
