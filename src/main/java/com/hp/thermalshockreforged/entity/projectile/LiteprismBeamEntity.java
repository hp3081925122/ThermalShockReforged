package com.hp.thermalshockreforged.entity.projectile;

import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

public final class LiteprismBeamEntity extends ShortBeamEntity {
    public static int fireWeaponLine(net.minecraft.server.level.ServerLevel level, LivingEntity shooter) {
        var origin = shooter.getEyePosition();
        var direction = shooter.getLookAngle();
        var obstacle = level.clip(new net.minecraft.world.level.ClipContext(origin, origin.add(direction.scale(20)), net.minecraft.world.level.ClipContext.Block.COLLIDER, net.minecraft.world.level.ClipContext.Fluid.NONE, shooter));
        double visibleLength = obstacle.getType() == net.minecraft.world.phys.HitResult.Type.MISS ? 20 : origin.distanceTo(obstacle.getLocation());
        int emitted = 0;
        for (int segment = 0; segment < 20 && segment < visibleLength; segment++) {
            var position = origin.add(direction.scale(segment));
            var beam = new LiteprismBeamEntity(ProjectileContent.LITEPRISM_BEAM.get(), level);
            beam.setOwner(shooter); beam.setPos(position); beam.setBaseDamage(7); beam.setImpactKnockback(0); beam.setSilent(true); beam.setCritArrow(false);
            ((com.hp.thermalshockreforged.mixin.ArrowPiercingAccess) (Object) beam).thermalShockReforged$setPierceLevel((byte) 5);
            beam.shoot(direction.x, direction.y, direction.z, 1, 0);
            if (level.addFreshEntity(beam)) emitted++;
            level.sendParticles(net.minecraft.core.particles.ParticleTypes.END_ROD, position.x, position.y, position.z, 1, 0, 0, 0, 0);
        }
        return emitted;
    }
    public LiteprismBeamEntity(EntityType<? extends LiteprismBeamEntity> type, Level level) { super(type, level); }
    public LiteprismBeamEntity(LivingEntity owner, Level level) { super(ProjectileContent.LITEPRISM_BEAM.get(), owner, level); }
    @Override protected boolean thistle() { return false; }
    public static LiteprismBeamEntity shoot(Level level, LivingEntity owner, RandomSource random) { return shoot(level, owner, random, 0, 3, 0); }
    public static LiteprismBeamEntity shoot(Level level, LivingEntity owner, RandomSource random, float pullingPower) { return shoot(level, owner, random, 0, 3, 0); }
    public static LiteprismBeamEntity shoot(Level level, LivingEntity owner, RandomSource random, float power, double damage, int knockback) { var bullet = new LiteprismBeamEntity(owner, level); var view = owner.getViewVector(1); bullet.shoot(view.x, view.y, view.z, power * 2, 0); bullet.setSilent(true); bullet.setCritArrow(false); bullet.setBaseDamage(damage); bullet.setImpactKnockback(knockback); level.addFreshEntity(bullet); return bullet; }
    public static LiteprismBeamEntity shoot(LivingEntity owner, LivingEntity target) { var bullet = new LiteprismBeamEntity(owner, owner.level()); double dx = target.getX() - owner.getX(), dy = target.getY() + target.getEyeHeight() - 1.1, dz = target.getZ() - owner.getZ(); bullet.shoot(dx, dy - bullet.getY() + Math.hypot(dx, dz) * .2F, dz, 0, 12); bullet.setSilent(true); bullet.setBaseDamage(3); bullet.setImpactKnockback(0); bullet.setCritArrow(false); owner.level().addFreshEntity(bullet); return bullet; }
}
