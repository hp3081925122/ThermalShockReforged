package com.hp.thermalshockreforged.entity.projectile;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.ItemSupplier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public abstract class LegacyArrowEntity extends AbstractArrow implements ItemSupplier {
    private double impactKnockback;

    protected LegacyArrowEntity(EntityType<? extends LegacyArrowEntity> type, Level level) { super(type, level); }
    protected LegacyArrowEntity(EntityType<? extends LegacyArrowEntity> type, LivingEntity owner, Level level, ItemStack item) { super(type, owner, level, item, null); }
    protected abstract ItemStack projectileItem();
    @Override public ItemStack getItem() { return projectileItem(); }
    @Override protected ItemStack getDefaultPickupItem() { return projectileItem(); }
    @Override protected void doPostHurtEffects(LivingEntity entity) { super.doPostHurtEffects(entity); entity.setArrowCount(Math.max(0, entity.getArrowCount() - 1)); }
    protected void setImpactKnockback(double value) { impactKnockback = Math.max(0, value); }
    @Override protected void doKnockback(LivingEntity target, DamageSource source) {
        Vec3 direction = getDeltaMovement().multiply(1, 0, 1).normalize();
        double resistance = Math.max(0, 1 - target.getAttributeValue(Attributes.KNOCKBACK_RESISTANCE));
        if (direction.lengthSqr() > 0 && resistance > 0 && impactKnockback > 0) {
            target.knockback(impactKnockback * .6 * resistance, -direction.x, -direction.z);
            target.hurtMarked = true;
        }
    }
    @Override public void tick() { super.tick(); setNoGravity(true); if (inGround) discard(); }
    @Override public void addAdditionalSaveData(CompoundTag tag) { super.addAdditionalSaveData(tag); tag.putDouble("ImpactKnockback", impactKnockback); }
    @Override public void readAdditionalSaveData(CompoundTag tag) { super.readAdditionalSaveData(tag); double value = tag.getDouble("ImpactKnockback"); impactKnockback = Double.isFinite(value) ? Math.max(0, Math.min(20, value)) : 0; }
}
