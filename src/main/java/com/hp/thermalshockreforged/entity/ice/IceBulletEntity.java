package com.hp.thermalshockreforged.entity.ice;

import com.hp.thermalshockreforged.content.MaterialContent;
import com.hp.thermalshockreforged.entity.fauna.FaunaEntities;
import java.util.UUID;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.projectile.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public final class IceBulletEntity extends AbstractArrow implements ItemSupplier {
    private UUID caster;
    private int lifetime;
    private double impactKnockback;
    public IceBulletEntity(EntityType<? extends IceBulletEntity> type, Level level) { super(type, level); }
    @Override public ItemStack getItem() { return new ItemStack(MaterialContent.items().get("ice_projectile").get()); }
    @Override protected ItemStack getDefaultPickupItem() { return new ItemStack(MaterialContent.items().get("ice_projectile").get()); }
    public void configure(IceCreatureEntity source, boolean owned, double multiplier, double knockback) {
        caster = source.getUUID();
        if (owned) setOwner(source);
        double damage = source.getAttributeValue(Attributes.ATTACK_DAMAGE) * multiplier;
        setBaseDamage(Double.isFinite(damage) ? Math.max(0, damage) : 0);
        impactKnockback = Double.isFinite(knockback) ? Math.max(0, Math.min(2, knockback)) : 0;
        setSilent(true); setCritArrow(false);
    }
    public void configureUnowned(UUID source, double damage) {
        caster = source;
        setOwner(null);
        setBaseDamage(Double.isFinite(damage) ? Math.clamp(damage, 0, Float.MAX_VALUE) : 0);
        impactKnockback = 0;
        pickup = Pickup.DISALLOWED;
        setSilent(true); setCritArrow(false);
    }
    @Override protected boolean canHitEntity(Entity entity) { return (caster == null || !caster.equals(entity.getUUID())) && super.canHitEntity(entity); }
    @Override protected void doPostHurtEffects(LivingEntity entity) { super.doPostHurtEffects(entity); entity.setArrowCount(Math.max(0, entity.getArrowCount() - 1)); }
    @Override protected void doKnockback(LivingEntity target, DamageSource source) {
        double amount = impactKnockback * Math.max(0, 1 - target.getAttributeValue(Attributes.KNOCKBACK_RESISTANCE));
        var impulse = getDeltaMovement().multiply(1, 0, 1).normalize().scale(amount);
        if (impulse.lengthSqr() > 0) target.push(impulse.x, 0.1 * Math.max(0, 1 - target.getAttributeValue(Attributes.KNOCKBACK_RESISTANCE)), impulse.z);
    }
    @Override public void tick() {
        if (!level().isClientSide && lifetime >= 40) { discard(); return; }
        super.tick();
        setNoGravity(true);
        level().addParticle(FaunaEntities.WHITE_ORB.get(), getX(), getY(), getZ(), 0, 0, 0);
        if (inGround || !level().isClientSide && ++lifetime >= 40) discard();
    }
    @Override public void addAdditionalSaveData(CompoundTag tag) { super.addAdditionalSaveData(tag); tag.putInt("IceLifetime", lifetime); tag.putDouble("IceKnockback", impactKnockback); if (caster != null) tag.putUUID("IceCaster", caster); }
    @Override public void readAdditionalSaveData(CompoundTag tag) { super.readAdditionalSaveData(tag); lifetime = Math.max(0, Math.min(40, tag.getInt("IceLifetime"))); double kb = tag.getDouble("IceKnockback"); impactKnockback = Double.isFinite(kb) ? Math.max(0, Math.min(2, kb)) : 0; caster = tag.hasUUID("IceCaster") ? tag.getUUID("IceCaster") : null; if (!Double.isFinite(getBaseDamage()) || getBaseDamage() < 0) setBaseDamage(0); }
}
