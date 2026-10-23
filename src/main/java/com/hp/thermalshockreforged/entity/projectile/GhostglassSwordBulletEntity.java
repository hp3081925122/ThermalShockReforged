package com.hp.thermalshockreforged.entity.projectile;

import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

public final class GhostglassSwordBulletEntity extends GreenOrbBulletBase {
    public GhostglassSwordBulletEntity(EntityType<? extends GhostglassSwordBulletEntity> type, Level level) { super(type, level); }
    public GhostglassSwordBulletEntity(LivingEntity owner, Level level) { super(ProjectileContent.GHOSTGLASS_SWORD_BULLET.get(), owner, level); setImpactKnockback(1); }
    public static GhostglassSwordBulletEntity shoot(Level level, LivingEntity owner, RandomSource random) { return shoot(level, owner, random, .7F, 2, 1); }
    public static GhostglassSwordBulletEntity shoot(Level level, LivingEntity owner, RandomSource random, float pullingPower) { return shoot(level, owner, random, pullingPower * .7F, 2, 1); }
    public static GhostglassSwordBulletEntity shoot(Level level, LivingEntity owner, RandomSource random, float power, double damage, int knockback) {
        var bullet = new GhostglassSwordBulletEntity(owner, level); var view = owner.getViewVector(1); bullet.shoot(view.x, view.y, view.z, power * 2, 0); bullet.setSilent(true); bullet.setCritArrow(false); bullet.setBaseDamage(damage); bullet.setImpactKnockback(knockback); level.addFreshEntity(bullet); level.playSound(null, owner.blockPosition(), net.minecraft.core.registries.BuiltInRegistries.SOUND_EVENT.getValue(net.minecraft.resources.Identifier.fromNamespaceAndPath("thermal_shock_reforged", "ghostbullet")), net.minecraft.sounds.SoundSource.PLAYERS, 1, 1 / (random.nextFloat() * .5F + 1) + power / 2); return bullet;
    }
    public static GhostglassSwordBulletEntity shoot(LivingEntity owner, LivingEntity target) {
        var bullet = new GhostglassSwordBulletEntity(owner, owner.level()); double dx = target.getX() - owner.getX(), dy = target.getY() + target.getEyeHeight() - 1.1, dz = target.getZ() - owner.getZ(); bullet.shoot(dx, dy - bullet.getY() + Math.hypot(dx, dz) * .2F, dz, 1.4F, 12); bullet.setSilent(true); bullet.setBaseDamage(2); bullet.setImpactKnockback(1); bullet.setCritArrow(false); owner.level().addFreshEntity(bullet); owner.level().playSound(null, owner.blockPosition(), net.minecraft.core.registries.BuiltInRegistries.SOUND_EVENT.getValue(net.minecraft.resources.Identifier.fromNamespaceAndPath("thermal_shock_reforged", "ghostbullet")), net.minecraft.sounds.SoundSource.PLAYERS, 1, 1 / (owner.getRandom().nextFloat() * .5F + 1)); return bullet;
    }
}
