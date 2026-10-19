package com.hp.thermalshockreforged.entity.projectile;

import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

public final class ThistleBeamEntity extends ShortBeamEntity {
    public static int fireWeaponLine(net.minecraft.server.level.ServerLevel level, LivingEntity shooter) {
        var origin = shooter.getEyePosition();
        var direction = shooter.getLookAngle();
        var obstacle = level.clip(new net.minecraft.world.level.ClipContext(origin, origin.add(direction.scale(40)), net.minecraft.world.level.ClipContext.Block.COLLIDER, net.minecraft.world.level.ClipContext.Fluid.NONE, shooter));
        double length = obstacle.getType() == net.minecraft.world.phys.HitResult.Type.MISS ? 40 : origin.distanceTo(obstacle.getLocation());
        boolean poison = shooter.getHealth() < shooter.getMaxHealth() / 2;
        int emitted = 0;
        for (int segment = 1; segment <= 40 && (obstacle.getType() == net.minecraft.world.phys.HitResult.Type.MISS || segment < length); segment++) {
            var position = origin.add(direction.scale(segment));
            ShortBeamEntity beam = poison ? new ThistleBeamEntity(ProjectileContent.THISTLE_BEAM.get(), level) : new LiteprismBeamEntity(ProjectileContent.LITEPRISM_BEAM.get(), level);
            beam.setOwner(shooter);
            beam.setPos(position);
            beam.setBaseDamage(6);
            beam.setImpactKnockback(1);
            beam.setSilent(true);
            beam.setCritArrow(false);
            beam.shoot(direction.x, direction.y, direction.z, 1, 0);
            if (level.addFreshEntity(beam)) {
                emitted++;
                var particle = origin.add(direction.scale(segment - 1));
                level.sendParticles(net.minecraft.core.particles.ParticleTypes.HAPPY_VILLAGER, particle.x, particle.y, particle.z, 1, 0, 0, 0, 0);
            }
        }
        return emitted;
    }
    public ThistleBeamEntity(EntityType<? extends ThistleBeamEntity> type, Level level) { super(type, level); }
    public ThistleBeamEntity(LivingEntity owner, Level level) { super(ProjectileContent.THISTLE_BEAM.get(), owner, level); }
    @Override protected boolean thistle() { return true; }
    public static ThistleBeamEntity shoot(Level level, LivingEntity owner, RandomSource random) { return shoot(level, owner, random, 0, 3, 0); }
    public static ThistleBeamEntity shoot(Level level, LivingEntity owner, RandomSource random, float pullingPower) { return shoot(level, owner, random, 0, 3, 0); }
    public static ThistleBeamEntity shoot(Level level, LivingEntity owner, RandomSource random, float power, double damage, int knockback) { var bullet = new ThistleBeamEntity(owner, level); var view = owner.getViewVector(1); bullet.shoot(view.x, view.y, view.z, power * 2, 0); bullet.setSilent(true); bullet.setCritArrow(false); bullet.setBaseDamage(damage); bullet.setImpactKnockback(knockback); level.addFreshEntity(bullet); return bullet; }
    public static ThistleBeamEntity shoot(LivingEntity owner, LivingEntity target) { var bullet = new ThistleBeamEntity(owner, owner.level()); double dx = target.getX() - owner.getX(), dy = target.getY() + target.getEyeHeight() - 1.1, dz = target.getZ() - owner.getZ(); bullet.shoot(dx, dy - bullet.getY() + Math.hypot(dx, dz) * .2F, dz, 0, 12); bullet.setSilent(true); bullet.setBaseDamage(3); bullet.setImpactKnockback(0); bullet.setCritArrow(false); owner.level().addFreshEntity(bullet); return bullet; }
}
