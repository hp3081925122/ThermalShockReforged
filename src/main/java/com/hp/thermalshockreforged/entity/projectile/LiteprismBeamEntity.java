package com.hp.thermalshockreforged.entity.projectile;

import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

public final class LiteprismBeamEntity extends ShortBeamEntity {
    public static int fireSylviaLine(net.minecraft.server.level.ServerLevel level, LivingEntity shooter) {
        var origin = shooter.getEyePosition();
        var direction = shooter.getLookAngle();
        var obstacle = level.clip(new net.minecraft.world.level.ClipContext(origin, origin.add(direction.scale(48)), net.minecraft.world.level.ClipContext.Block.COLLIDER, net.minecraft.world.level.ClipContext.Fluid.NONE, shooter));
        double visibleLength = obstacle.getType() == net.minecraft.world.phys.HitResult.Type.MISS ? 48 : origin.distanceTo(obstacle.getLocation());
        int emitted = 0;
        for (int segment = 0; segment < 48 && segment < visibleLength; segment++) {
            var position = origin.add(direction.scale(segment));
            var beam = new LiteprismBeamEntity(ProjectileContent.LITEPRISM_BEAM.get(), level);
            beam.setOwner(shooter); beam.setPos(position); beam.setBaseDamage(35); beam.setImpactKnockback(2); beam.setSilent(true); beam.setCritArrow(false);
            ((com.hp.thermalshockreforged.mixin.ArrowPiercingAccess) (Object) beam).thermalShockReforged$setPierceLevel((byte) 5);
            beam.shoot(direction.x, direction.y, direction.z, 1, 0);
            if (level.addFreshEntity(beam)) {
                emitted++;
                level.sendParticles(net.minecraft.core.particles.ParticleTypes.SONIC_BOOM, position.x, position.y, position.z, 1, 0, 0, 0, 0);
            }
        }
        return emitted;
    }
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
        }
        var right = direction.cross(new net.minecraft.world.phys.Vec3(0, 1, 0));
        if (right.lengthSqr() < 1.0E-4) right = new net.minecraft.world.phys.Vec3(1, 0, 0);
        right = right.normalize().scale(.3);
        if (shooter.getMainArm() == net.minecraft.world.entity.HumanoidArm.LEFT) right = right.scale(-1);
        var muzzle = origin.add(direction.scale(.7)).add(right).add(0, -.22, 0);
        var end = origin.add(direction.scale(visibleLength));
        var span = end.subtract(muzzle);
        level.sendParticles(com.hp.thermalshockreforged.content.weapons.WeaponContent.LITEPRISM_BEAM.get(), muzzle.x, muzzle.y, muzzle.z, 0, span.x, span.y, span.z, 1);
        level.sendParticles(com.hp.thermalshockreforged.content.weapons.WeaponContent.LITEPRISM_FLARE.get(), muzzle.x, muzzle.y, muzzle.z, 0, .3, 0, 0, 1);
        level.sendParticles(com.hp.thermalshockreforged.content.weapons.WeaponContent.LITEPRISM_FLARE.get(), end.x, end.y, end.z, 0, .5, 0, 0, 1);
        for (double along = 1.5; along < visibleLength; along += 1.5) {
            var at = origin.add(direction.scale(along));
            level.sendParticles(com.hp.thermalshockreforged.content.weapons.WeaponContent.LITEPRISM_SPARK.get(), at.x, at.y, at.z, 0, (level.random.nextDouble() - .5) * .08, .02 + level.random.nextDouble() * .04, (level.random.nextDouble() - .5) * .08, 1);
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
