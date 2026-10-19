package com.hp.thermalshockreforged.entity.projectile;

import com.hp.thermalshockreforged.ThermalShockReforged;
import com.hp.thermalshockreforged.content.ModDamageTypes;
import com.hp.thermalshockreforged.content.decor.geo.GeoDecorContent;
import com.hp.thermalshockreforged.content.decor.geo.GeoDecorKind;
import com.hp.thermalshockreforged.entity.projectile.ProjectileContent;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;

public final class FlareBulletEntity extends LegacyArrowEntity {
    private static final TagKey<EntityType<?>> PYRO_WEAK = TagKey.create(Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, "pyro_weak"));
    public FlareBulletEntity(EntityType<? extends FlareBulletEntity> type, Level level) { super(type, level); }
    public FlareBulletEntity(LivingEntity owner, Level level) { super(ProjectileContent.FLARE_BULLET.get(), owner, level, new ItemStack(GeoDecorContent.item(GeoDecorKind.YELLOW_ORB))); }
    @Override protected ItemStack projectileItem() { return new ItemStack(GeoDecorContent.item(GeoDecorKind.YELLOW_ORB)); }
    @Override protected void onHitEntity(EntityHitResult hit) { super.onHitEntity(hit); if (!(hit.getEntity() instanceof Player)) { impact(hit.getEntity().blockPosition(), hit.getEntity()); } }
    @Override protected void onHitBlock(BlockHitResult hit) { super.onHitBlock(hit); impact(hit.getBlockPos(), null); }
    private void impact(BlockPos pos, Entity target) {
        if (!(level() instanceof net.minecraft.server.level.ServerLevel server)) return;
        server.playSound(null, pos, SoundEvents.GENERIC_HURT, SoundSource.NEUTRAL, 1, 1);
        server.playSound(null, pos, SoundEvents.FIREWORK_ROCKET_BLAST, SoundSource.NEUTRAL, 1, 1);
        server.sendParticles(ProjectileContent.ORANGE_RING_BLAST.get(), getX(), target == null ? getY() + .9 : getY(), getZ(), 1, 0, 0, 0, 0);
        if (target != null && target.getType().is(PYRO_WEAK)) target.hurt(new DamageSource(server.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(ModDamageTypes.THERM_IFRAME_BYPASS)), 6);
        if (target == null) discard();
    }
    @Override public void tick() { super.tick(); if (!level().isClientSide) setRemainingFireTicks(100); }
    public static FlareBulletEntity shoot(Level level, LivingEntity owner, RandomSource random) { return shoot(level, owner, random, .7F, 1, 0); }
    public static FlareBulletEntity shoot(Level level, LivingEntity owner, RandomSource random, float pullingPower) { return shoot(level, owner, random, pullingPower * .7F, 1, 0); }
    public static FlareBulletEntity shoot(Level level, LivingEntity owner, RandomSource random, float power, double damage, int knockback) {
        var bullet = new FlareBulletEntity(owner, level); var view = owner.getViewVector(1); bullet.shoot(view.x, view.y, view.z, power * 2, 0); bullet.setSilent(true); bullet.setCritArrow(false); bullet.setBaseDamage(damage); bullet.setImpactKnockback(knockback); bullet.setRemainingFireTicks(100); level.addFreshEntity(bullet); return bullet;
    }
    public static FlareBulletEntity shoot(LivingEntity owner, LivingEntity target) {
        var bullet = new FlareBulletEntity(owner, owner.level()); double dx = target.getX() - owner.getX(); double dy = target.getY() + target.getEyeHeight() - 1.1; double dz = target.getZ() - owner.getZ(); bullet.shoot(dx, dy - bullet.getY() + Math.hypot(dx, dz) * .2F, dz, 1.4F, 12); bullet.setSilent(true); bullet.setBaseDamage(1); bullet.setImpactKnockback(0); bullet.setCritArrow(false); bullet.setRemainingFireTicks(100); owner.level().addFreshEntity(bullet); return bullet;
    }
}
