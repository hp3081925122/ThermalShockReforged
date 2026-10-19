package com.hp.thermalshockreforged.entity.projectile;

import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

public final class MagnetCrossbowBulletEntity extends GreenOrbBulletBase {
    public MagnetCrossbowBulletEntity(EntityType<? extends MagnetCrossbowBulletEntity> type, Level level) { super(type, level); }
    public MagnetCrossbowBulletEntity(LivingEntity owner, Level level) { super(ProjectileContent.MAGNET_CROSSBOW_BULLET.get(), owner, level); setImpactKnockback(1); }
    public static MagnetCrossbowBulletEntity shoot(Level level, LivingEntity owner, RandomSource random) { return shoot(level, owner, random, .7F, 5, 1); }
    public static MagnetCrossbowBulletEntity shoot(Level level, LivingEntity owner, RandomSource random, float pullingPower) { return shoot(level, owner, random, pullingPower * .7F, 5, 1); }
    public static MagnetCrossbowBulletEntity shoot(Level level, LivingEntity owner, RandomSource random, float power, double damage, int knockback) {
        var bullet = new MagnetCrossbowBulletEntity(owner, level); var view = owner.getViewVector(1); bullet.shoot(view.x, view.y, view.z, power * 2, 0); bullet.setSilent(true); bullet.setCritArrow(false); bullet.setBaseDamage(damage); bullet.setImpactKnockback(knockback); level.addFreshEntity(bullet); return bullet;
    }
    public static MagnetCrossbowBulletEntity shoot(LivingEntity owner, LivingEntity target) {
        return shoot(owner, target, 5);
    }
    public static MagnetCrossbowBulletEntity shoot(LivingEntity owner, LivingEntity target, double damage) {
        var bullet = new MagnetCrossbowBulletEntity(owner, owner.level()); double dx = target.getX() - owner.getX(), dy = target.getY() + target.getEyeHeight() - 1.1, dz = target.getZ() - owner.getZ(); bullet.shoot(dx, dy - bullet.getY() + Math.hypot(dx, dz) * .2F, dz, 1.4F, 12); bullet.setSilent(true); bullet.setBaseDamage(damage); bullet.setImpactKnockback(1); bullet.setCritArrow(false); owner.level().addFreshEntity(bullet); return bullet;
    }
}
