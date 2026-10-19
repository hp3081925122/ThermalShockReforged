package com.hp.thermalshockreforged.entity.projectile;

import com.hp.thermalshockreforged.content.MaterialContent;
import com.hp.thermalshockreforged.entity.cavebloomer.CavebloomerContent;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public final class BloodBulletEntity extends LegacyArrowEntity {
    public BloodBulletEntity(EntityType<? extends BloodBulletEntity> type, Level level) { super(type, level); setImpactKnockback(5); }
    public BloodBulletEntity(LivingEntity owner, Level level) { super(ProjectileContent.BLOOD_BULLET.get(), owner, level, new ItemStack(MaterialContent.items().get("gore_projectile").get())); setImpactKnockback(5); }
    @Override protected boolean fliesWithoutGravity() { return true; }
    public void setSkillKnockback(double value) { setImpactKnockback(value); }
    @Override protected int lifetimeTicks() { return 40; }
    @Override protected ItemStack projectileItem() { return new ItemStack(MaterialContent.items().get("gore_projectile").get()); }
    @Override public void tick() {
        super.tick();
        if (level() instanceof net.minecraft.server.level.ServerLevel server) server.sendParticles(CavebloomerContent.RED_ORB.get(), getX(), getY(), getZ(), 1, .0001, .0001, .0001, 0);
    }
    public static BloodBulletEntity shoot(Level level, LivingEntity owner, RandomSource random) { return shoot(level, owner, random, .1F, 5, 5); }
    public static BloodBulletEntity shoot(Level level, LivingEntity owner, RandomSource random, float pullingPower) { return shoot(level, owner, random, pullingPower * .1F, 5, 5); }
    public static BloodBulletEntity shoot(Level level, LivingEntity owner, RandomSource random, float power, double damage, int knockback) {
        var bullet = new BloodBulletEntity(owner, level); var view = owner.getViewVector(1); bullet.shoot(view.x, view.y, view.z, power * 2, 0); bullet.setSilent(true); bullet.setCritArrow(false); bullet.setBaseDamage(damage); bullet.setImpactKnockback(knockback); level.addFreshEntity(bullet); return bullet;
    }
    public static BloodBulletEntity shoot(LivingEntity owner, LivingEntity target) {
        var bullet = new BloodBulletEntity(owner, owner.level()); double dx = target.getX() - owner.getX(); double dy = target.getY() + target.getEyeHeight() - 1.1; double dz = target.getZ() - owner.getZ(); bullet.shoot(dx, dy - bullet.getY() + Math.hypot(dx, dz) * .2F, dz, .2F, 12); bullet.setSilent(true); bullet.setBaseDamage(5); bullet.setImpactKnockback(5); bullet.setCritArrow(false); owner.level().addFreshEntity(bullet); return bullet;
    }
}
