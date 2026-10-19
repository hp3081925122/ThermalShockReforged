package com.hp.thermalshockreforged.entity.projectile;

import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

public final class ThermoblasterBulletEntity extends ThermoblasterBulletBase {
    public ThermoblasterBulletEntity(EntityType<? extends ThermoblasterBulletEntity> type, Level level) { super(type, level); }
    public ThermoblasterBulletEntity(LivingEntity owner, Level level) { super(ProjectileContent.THERMOBLASTER_BULLET.get(), owner, level); }
    @Override protected boolean shotgun() { return false; }
    public static ThermoblasterBulletEntity shoot(Level level, LivingEntity owner, RandomSource random) { return shoot(level, owner, random, 1.3F, 1, 0); }
    public static ThermoblasterBulletEntity shoot(Level level, LivingEntity owner, RandomSource random, float pullingPower) { return shoot(level, owner, random, pullingPower * 1.3F, 1, 0); }
    public static ThermoblasterBulletEntity shoot(Level level, LivingEntity owner, RandomSource random, float power, double damage, int knockback) {
        var bullet = new ThermoblasterBulletEntity(owner, level); var view = owner.getViewVector(1); bullet.shoot(view.x, view.y, view.z, power * 2, 0); bullet.setSilent(true); bullet.setCritArrow(false); bullet.setBaseDamage(damage); bullet.setImpactKnockback(knockback); level.addFreshEntity(bullet); return bullet;
    }
    public static ThermoblasterBulletEntity shoot(LivingEntity owner, LivingEntity target) {
        var bullet = new ThermoblasterBulletEntity(owner, owner.level()); double dx = target.getX() - owner.getX(), dy = target.getY() + target.getEyeHeight() - 1.1, dz = target.getZ() - owner.getZ(); bullet.shoot(dx, dy - bullet.getY() + Math.hypot(dx, dz) * .2F, dz, 2.6F, 12); bullet.setSilent(true); bullet.setBaseDamage(1); bullet.setImpactKnockback(0); bullet.setCritArrow(false); owner.level().addFreshEntity(bullet); return bullet;
    }
}
