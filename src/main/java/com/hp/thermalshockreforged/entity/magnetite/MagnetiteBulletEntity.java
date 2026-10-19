package com.hp.thermalshockreforged.entity.magnetite;

import com.hp.thermalshockreforged.ThermalShockReforged;
import com.hp.thermalshockreforged.entity.projectile.GreenOrbBulletBase;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public final class MagnetiteBulletEntity extends GreenOrbBulletBase {
    public MagnetiteBulletEntity(EntityType<? extends MagnetiteBulletEntity> type, Level level) { super(type, level); }
    public MagnetiteBulletEntity(EntityType<? extends MagnetiteBulletEntity> type, LivingEntity owner, Level level) { super(type, owner, level); }
    @Override protected boolean affects(Entity target) { return target != null && !(target instanceof MagnetiteEntity); }
    @Override protected boolean discardAfterEntityEffects() { return false; }
    @Override public void playerTouch(Player player) { super.playerTouch(player); applyGreenEffects(player, position(), true); }
    public static MagnetiteBulletEntity shoot(Level level, LivingEntity owner, RandomSource random) { return shoot(level, owner, random, .7F, 2, 1); }
    public static MagnetiteBulletEntity shoot(Level level, LivingEntity owner, RandomSource random, float pullingPower) { return shoot(level, owner, random, pullingPower * .7F, 2, 1); }
    public static MagnetiteBulletEntity shoot(Level level, LivingEntity owner, RandomSource random, float power, double damage, int knockback) {
        var bullet = new MagnetiteBulletEntity(MagnetiteContent.BULLET.get(), owner, level);
        var view = owner.getViewVector(1);
        bullet.shoot(view.x, view.y, view.z, power * 2, 0);
        bullet.setSilent(true); bullet.setBaseDamage(damage); bullet.setImpactKnockback(knockback); bullet.setCritArrow(false);
        level.addFreshEntity(bullet);
        level.playSound(null, owner.getX(), owner.getY(), owner.getZ(), BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, "ghostbullet")), SoundSource.PLAYERS, 1, 1 / (random.nextFloat() * .5F + 1) + power / 2);
        return bullet;
    }
    public static MagnetiteBulletEntity shoot(LivingEntity owner, LivingEntity target) {
        var bullet = new MagnetiteBulletEntity(MagnetiteContent.BULLET.get(), owner, owner.level());
        double dx = target.getX() - owner.getX(), dy = target.getY() + target.getEyeHeight() - 1.1, dz = target.getZ() - owner.getZ();
        bullet.shoot(dx, dy - bullet.getY() + Math.hypot(dx, dz) * .2F, dz, 1.4F, 12);
        bullet.setSilent(true); bullet.setBaseDamage(2); bullet.setImpactKnockback(1); bullet.setCritArrow(false);
        owner.level().addFreshEntity(bullet);
        owner.level().playSound(null, owner.getX(), owner.getY(), owner.getZ(), BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, "ghostbullet")), SoundSource.PLAYERS, 1, 1 / (owner.getRandom().nextFloat() * .5F + 1));
        return bullet;
    }
}
