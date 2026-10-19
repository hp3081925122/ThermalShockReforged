package com.hp.thermalshockreforged.entity.magnetite;

import com.hp.thermalshockreforged.content.decor.geo.GeoDecorContent;
import com.hp.thermalshockreforged.content.decor.geo.GeoDecorKind;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.ItemSupplier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public final class MagnetiteBulletEntity extends AbstractArrow implements ItemSupplier {
    public MagnetiteBulletEntity(EntityType<? extends MagnetiteBulletEntity> type, Level level) { super(type, level); }
    public MagnetiteBulletEntity(EntityType<? extends MagnetiteBulletEntity> type, LivingEntity owner, Level level) { super(type, owner, level, ItemStack.EMPTY, ItemStack.EMPTY); }
    @Override protected ItemStack getPickupItem() { return new ItemStack(GeoDecorContent.item(GeoDecorKind.GREEN_ORB)); }
    @Override protected ItemStack getDefaultPickupItem() { return new ItemStack(GeoDecorContent.item(GeoDecorKind.GREEN_ORB)); }
    @Override public ItemStack getItem() { return new ItemStack(GeoDecorContent.item(GeoDecorKind.GREEN_ORB)); }
    @Override protected void doPostHurtEffects(LivingEntity entity) { super.doPostHurtEffects(entity); entity.setArrowCount(Math.max(0, entity.getArrowCount() - 1)); }
    @Override public void tick() { super.tick(); if (inGround) discard(); }
    public static MagnetiteBulletEntity shoot(LivingEntity owner, LivingEntity target) {
        var bullet = new MagnetiteBulletEntity(MagnetiteContent.BULLET.get(), owner, owner.level());
        double dx = target.getX() - owner.getX(), dy = target.getY() + target.getEyeHeight() - 1.1, dz = target.getZ() - owner.getZ();
        bullet.shoot(dx, dy - bullet.getY() + Math.hypot(dx, dz) * .2, dz, 1.4F, 12); bullet.setSilent(true); bullet.setBaseDamage(2); bullet.setCritArrow(false); owner.level().addFreshEntity(bullet);
        owner.level().playSound(null, owner.getX(), owner.getY(), owner.getZ(), net.minecraft.core.registries.BuiltInRegistries.SOUND_EVENT.get(net.minecraft.resources.ResourceLocation.fromNamespaceAndPath("thermal_shock_reforged", "ghostbullet")), SoundSource.PLAYERS, 1, 1);
        return bullet;
    }
}
