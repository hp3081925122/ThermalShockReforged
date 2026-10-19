package com.hp.thermalshockreforged.entity.projectile;

import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

public final class LiteprismBeamEntity extends ShortBeamEntity {
    public LiteprismBeamEntity(EntityType<? extends LiteprismBeamEntity> type, Level level) { super(type, level); }
    public LiteprismBeamEntity(LivingEntity owner, Level level) { super(ProjectileContent.LITEPRISM_BEAM.get(), owner, level); }
    @Override protected boolean thistle() { return false; }
    public static LiteprismBeamEntity shoot(Level level, LivingEntity owner, RandomSource random) { return shoot(level, owner, random, 0, 3, 0); }
    public static LiteprismBeamEntity shoot(Level level, LivingEntity owner, RandomSource random, float pullingPower) { return shoot(level, owner, random, 0, 3, 0); }
    public static LiteprismBeamEntity shoot(Level level, LivingEntity owner, RandomSource random, float power, double damage, int knockback) { var bullet = new LiteprismBeamEntity(owner, level); var view = owner.getViewVector(1); bullet.shoot(view.x, view.y, view.z, power * 2, 0); bullet.setSilent(true); bullet.setCritArrow(false); bullet.setBaseDamage(damage); bullet.setImpactKnockback(knockback); level.addFreshEntity(bullet); return bullet; }
    public static LiteprismBeamEntity shoot(LivingEntity owner, LivingEntity target) { var bullet = new LiteprismBeamEntity(owner, owner.level()); double dx = target.getX() - owner.getX(), dy = target.getY() + target.getEyeHeight() - 1.1, dz = target.getZ() - owner.getZ(); bullet.shoot(dx, dy - bullet.getY() + Math.hypot(dx, dz) * .2F, dz, 0, 12); bullet.setSilent(true); bullet.setBaseDamage(3); bullet.setImpactKnockback(0); bullet.setCritArrow(false); owner.level().addFreshEntity(bullet); return bullet; }
}
