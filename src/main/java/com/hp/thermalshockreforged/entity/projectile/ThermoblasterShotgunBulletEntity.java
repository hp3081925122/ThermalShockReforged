package com.hp.thermalshockreforged.entity.projectile;

import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

public final class ThermoblasterShotgunBulletEntity extends ThermoblasterBulletBase {
    public ThermoblasterShotgunBulletEntity(EntityType<? extends ThermoblasterShotgunBulletEntity> type, Level level) { super(type, level); }
    public ThermoblasterShotgunBulletEntity(LivingEntity owner, Level level) { super(ProjectileContent.THERMOBLASTER_SHOTGUN_BULLET.get(), owner, level); }
    @Override protected boolean shotgun() { return true; }
    public static ThermoblasterShotgunBulletEntity shoot(Level level, LivingEntity owner, RandomSource random) { return shoot(level, owner, random, 1.3F, 1, 0); }
    public static ThermoblasterShotgunBulletEntity shoot(Level level, LivingEntity owner, RandomSource random, float pullingPower) { return shoot(level, owner, random, pullingPower * 1.3F, 1, 0); }
    public static ThermoblasterShotgunBulletEntity shoot(Level level, LivingEntity owner, RandomSource random, float power, double damage, int knockback) {
        var bullet = new ThermoblasterShotgunBulletEntity(owner, level); var view = owner.getViewVector(1); bullet.shoot(view.x, view.y, view.z, power * 2, 0); bullet.setSilent(true); bullet.setCritArrow(false); bullet.setBaseDamage(damage); bullet.setImpactKnockback(knockback); bullet.igniteForSeconds(100); level.addFreshEntity(bullet); return bullet;
    }
    public static ThermoblasterShotgunBulletEntity shoot(LivingEntity owner, LivingEntity target) {
        var bullet = new ThermoblasterShotgunBulletEntity(owner, owner.level()); double dx = target.getX() - owner.getX(), dy = target.getY() + target.getEyeHeight() - 1.1, dz = target.getZ() - owner.getZ(); bullet.shoot(dx, dy - bullet.getY() + Math.hypot(dx, dz) * .2F, dz, 2.6F, 12); bullet.setSilent(true); bullet.setBaseDamage(1); bullet.setImpactKnockback(0); bullet.setCritArrow(false); bullet.igniteForSeconds(100); owner.level().addFreshEntity(bullet); return bullet;
    }
}
