package com.hp.thermalshockreforged.entity.protoi;
import com.hp.thermalshockreforged.content.decor.nativeblocks.NativeDecorContent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.ItemSupplier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.entity.ai.attributes.Attributes;
public final class SunbeamBulletEntity extends AbstractArrow implements ItemSupplier {
    public SunbeamBulletEntity(EntityType<? extends SunbeamBulletEntity> type, Level level) { super(type, level); }
    public SunbeamBulletEntity(EntityType<? extends SunbeamBulletEntity> type, LivingEntity owner, Level level) { super(type, owner, level, ItemStack.EMPTY, ItemStack.EMPTY); }
    @Override protected ItemStack getPickupItem() { return new ItemStack(NativeDecorContent.LIGHT_BLOCKING_BARRIER.get()); }
    @Override protected ItemStack getDefaultPickupItem() { return new ItemStack(NativeDecorContent.LIGHT_BLOCKING_BARRIER.get()); }
    @Override public ItemStack getItem() { return new ItemStack(NativeDecorContent.LIGHT_BLOCKING_BARRIER.get()); }
    @Override protected void doPostHurtEffects(LivingEntity entity) { super.doPostHurtEffects(entity); entity.setArrowCount(Math.max(0, entity.getArrowCount() - 1)); }
    @Override protected void onHitBlock(BlockHitResult hit) { super.onHitBlock(hit); level().destroyBlock(hit.getBlockPos(), false); }
    @Override public void tick() { super.tick(); if (inGround) discard(); }
    @Override protected void doKnockback(LivingEntity target, net.minecraft.world.damagesource.DamageSource source) { double resistance = Math.max(0, 1 - target.getAttributeValue(Attributes.KNOCKBACK_RESISTANCE)); Vec3 impulse = getDeltaMovement().multiply(1, 0, 1).normalize().scale(5 * .6 * resistance); if (impulse.lengthSqr() > 0) { target.push(impulse.x, .1, impulse.z); target.hurtMarked = true; } }
    public static SunbeamBulletEntity shoot(Level level, LivingEntity owner, double damage, int knockback) { var bullet = new SunbeamBulletEntity(ProtoiContent.SUNBEAM_BULLET.get(), owner, level); bullet.shootFromRotation(owner, owner.getXRot(), owner.getYRot(), 0, 2, 0); bullet.setSilent(true); bullet.setBaseDamage(damage); level.addFreshEntity(bullet); return bullet; }
}
