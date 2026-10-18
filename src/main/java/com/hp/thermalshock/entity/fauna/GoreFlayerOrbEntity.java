package com.hp.thermalshock.entity.fauna;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.projectile.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public final class GoreFlayerOrbEntity extends AbstractArrow implements ItemSupplier {
    private int remainingTicks = 41;

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
        if (level().isClientSide) level().addParticle(FaunaEntities.WHITE_ORB.get(), getX(), getY(), getZ(), 0, 0, 0);
        if (inGround || !level().isClientSide && --remainingTicks <= 0) discard();
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putInt("RemainingTicks", remainingTicks);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        remainingTicks = tag.contains("RemainingTicks") ? Math.max(1, Math.min(41, tag.getInt("RemainingTicks"))) : 41;
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
