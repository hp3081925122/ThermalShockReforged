package com.hp.thermalshockreforged.entity.projectile;

import com.hp.thermalshockreforged.ThermalShockReforged;
import com.hp.thermalshockreforged.content.ModDamageTypes;
import com.hp.thermalshockreforged.content.decor.geo.GeoDecorContent;
import com.hp.thermalshockreforged.content.decor.geo.GeoDecorKind;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;

public final class GrenadeBulletEntity extends LegacyArrowEntity {
    private static final TagKey<EntityType<?>> CLORO_WEAK = TagKey.create(Registries.ENTITY_TYPE, Identifier.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, "cloro_weak"));
    public GrenadeBulletEntity(EntityType<? extends GrenadeBulletEntity> type, Level level) { super(type, level); }
    public GrenadeBulletEntity(LivingEntity owner, Level level) { super(ProjectileContent.GRENADE_BULLET.get(), owner, level, new ItemStack(GeoDecorContent.item(GeoDecorKind.YELLOW_ORB))); }
    @Override protected ItemStack projectileItem() { return new ItemStack(GeoDecorContent.item(GeoDecorKind.YELLOW_ORB)); }
    @Override protected void onHitEntity(EntityHitResult hit) { super.onHitEntity(hit); Entity target = hit.getEntity(); if (!(target instanceof Player) && level() instanceof net.minecraft.server.level.ServerLevel server) { server.explode(null, getX(), getY(), getZ(), 3, Level.ExplosionInteraction.TNT); if (target.getType().is(CLORO_WEAK)) target.hurt(new net.minecraft.world.damagesource.DamageSource(server.registryAccess().lookupOrThrow(Registries.DAMAGE_TYPE).getOrThrow(ModDamageTypes.THERM_IFRAME_BYPASS)), 6); } }
    @Override protected void onHitBlock(BlockHitResult hit) { super.onHitBlock(hit); if (level() instanceof net.minecraft.server.level.ServerLevel server) server.explode(null, hit.getBlockPos().getX(), hit.getBlockPos().getY(), hit.getBlockPos().getZ(), 2, Level.ExplosionInteraction.TNT); }
    public static boolean fireWeaponShot(net.minecraft.server.level.ServerLevel level, LivingEntity owner) {
        var bullet = ProjectileContent.GRENADE_BULLET.get().create(level);
        if (bullet == null) return false;
        bullet.setOwner(owner);
        bullet.setBaseDamage(3);
        bullet.setImpactKnockback(1);
        bullet.setSilent(true);
        bullet.setCritArrow(false);
        bullet.setPos(owner.getX(), owner.getY() + 1.5, owner.getZ());
        var look = owner.getLookAngle();
        bullet.shoot(look.x, look.y, look.z, 2, 1);
        return level.addFreshEntity(bullet);
    }
    public static GrenadeBulletEntity shoot(Level level, LivingEntity owner, RandomSource random) { return shoot(level, owner, random, .7F, 1, 0); }
    public static GrenadeBulletEntity shoot(Level level, LivingEntity owner, RandomSource random, float pullingPower) { return shoot(level, owner, random, pullingPower * .7F, 1, 0); }
    public static GrenadeBulletEntity shoot(Level level, LivingEntity owner, RandomSource random, float power, double damage, int knockback) { var bullet = new GrenadeBulletEntity(owner, level); var view = owner.getViewVector(1); bullet.shoot(view.x, view.y, view.z, power * 2, 0); bullet.setSilent(true); bullet.setCritArrow(false); bullet.setBaseDamage(damage); bullet.setImpactKnockback(knockback); bullet.igniteForSeconds(100); level.addFreshEntity(bullet); return bullet; }
    public static GrenadeBulletEntity shoot(LivingEntity owner, LivingEntity target) { var bullet = new GrenadeBulletEntity(owner, owner.level()); double dx = target.getX() - owner.getX(); double dy = target.getY() + target.getEyeHeight() - 1.1; double dz = target.getZ() - owner.getZ(); bullet.shoot(dx, dy - bullet.getY() + Math.hypot(dx, dz) * .2F, dz, 1.4F, 12); bullet.setSilent(true); bullet.setBaseDamage(1); bullet.setImpactKnockback(0); bullet.setCritArrow(false); bullet.igniteForSeconds(100); owner.level().addFreshEntity(bullet); return bullet; }
}
