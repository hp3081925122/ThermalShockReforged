package com.hp.thermalshockreforged.entity.projectile;

import com.hp.thermalshockreforged.entity.PondlerEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import java.util.Comparator;

public final class NetBulletEntity extends LegacyArrowEntity {
    public NetBulletEntity(EntityType<? extends NetBulletEntity> type, Level level) { super(type, level); }
    public NetBulletEntity(LivingEntity owner, Level level) { super(ProjectileContent.NET_BULLET.get(), owner, level, ItemStack.EMPTY); }
    @Override protected ItemStack projectileItem() { return new ItemStack(com.hp.thermalshockreforged.content.decor.geo.GeoDecorContent.item(com.hp.thermalshockreforged.content.decor.geo.GeoDecorKind.YELLOW_ORB)); }
    @Override public void tick() {
        super.tick();
        if (level() instanceof ServerLevel server) {
            var center = position();
            var targets = server.getEntitiesOfClass(PondlerEntity.class, AABB.ofSize(center, 2, 2, 2));
            if (targets.isEmpty()) return;
            targets.sort(Comparator.comparingDouble(target -> target.distanceToSqr(center)));
            for (var pondler : targets) pondler.hurt(server.damageSources().generic(), 10);
            server.playSound(null, BlockPos.containing(position()), SoundEvents.AZALEA_BREAK, SoundSource.NEUTRAL, 1, 1);
            server.playSound(null, BlockPos.containing(position()), SoundEvents.EXPERIENCE_ORB_PICKUP, SoundSource.NEUTRAL, 1, 1);
            discard();
        }
    }
    public static NetBulletEntity shoot(Level level, LivingEntity owner, RandomSource random) { return shoot(level, owner, random, .7F, 1, 0); }
    public static NetBulletEntity shoot(Level level, LivingEntity owner, RandomSource random, float pullingPower) { return shoot(level, owner, random, pullingPower * .7F, 1, 0); }
    public static NetBulletEntity shoot(Level level, LivingEntity owner, RandomSource random, float power, double damage, int knockback) {
        var bullet = new NetBulletEntity(owner, level); var view = owner.getViewVector(1); bullet.shoot(view.x, view.y, view.z, power * 2, 0); bullet.setSilent(true); bullet.setCritArrow(false); bullet.setBaseDamage(damage); bullet.setImpactKnockback(knockback); level.addFreshEntity(bullet); return bullet;
    }
    public static NetBulletEntity shoot(LivingEntity owner, LivingEntity target) {
        var bullet = new NetBulletEntity(owner, owner.level()); double dx = target.getX() - owner.getX(), dy = target.getY() + target.getEyeHeight() - 1.1, dz = target.getZ() - owner.getZ(); bullet.shoot(dx, dy - bullet.getY() + Math.hypot(dx, dz) * .2F, dz, 1.4F, 12); bullet.setSilent(true); bullet.setBaseDamage(1); bullet.setImpactKnockback(0); bullet.setCritArrow(false); owner.level().addFreshEntity(bullet); return bullet;
    }
}
