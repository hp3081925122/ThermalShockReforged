package com.hp.thermalshockreforged.entity.projectile;

import com.hp.thermalshockreforged.ThermalShockReforged;
import com.hp.thermalshockreforged.content.ModSounds;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;

public final class VolcanoBulletEntity extends LegacyArrowEntity {
    private int sparkDelay = 3;
    public VolcanoBulletEntity(EntityType<? extends VolcanoBulletEntity> type, Level level) { super(type, level); setImpactKnockback(1); }
    public VolcanoBulletEntity(LivingEntity owner, Level level) { super(ProjectileContent.VOLCANO_BULLET.get(), owner, level, new ItemStack(Items.FIRE_CHARGE)); setImpactKnockback(1); }
    @Override protected ItemStack projectileItem() { return new ItemStack(Items.FIRE_CHARGE); }
    @Override public void tick() {
        super.tick();
        if (!level().isClientSide && --sparkDelay <= 0) { if (level() instanceof net.minecraft.server.level.ServerLevel server) { server.sendParticles(ProjectileContent.VOLCANO_SPARK.get(), getX(), getY(), getZ(), 1, .0001, .0001, .0001, 0); server.playSound(null, blockPosition(), net.minecraft.sounds.SoundEvents.FIREWORK_ROCKET_TWINKLE, SoundSource.NEUTRAL, .5F, 1); } discard(); }
    }
    @Override protected void onHitEntity(EntityHitResult hit) { super.onHitEntity(hit); Entity target = hit.getEntity(); if (!(target instanceof Player)) { if (level() instanceof net.minecraft.server.level.ServerLevel server) server.playSound(null, target.blockPosition(), BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, "flareshoot")), SoundSource.NEUTRAL, 1, 1); target.hurt(new DamageSource(level().registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(DamageTypes.UNATTRIBUTED_FIREBALL)), 3); discard(); } }
    @Override protected void onHitBlock(BlockHitResult hit) { super.onHitBlock(hit); if (level() instanceof net.minecraft.server.level.ServerLevel server) { server.playSound(null, hit.getBlockPos(), BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, "flareshoot")), SoundSource.NEUTRAL, 1, 1); discard(); } }
    @Override public void addAdditionalSaveData(CompoundTag tag) { super.addAdditionalSaveData(tag); tag.putInt("VolcanoSparkDelay", sparkDelay); }
    @Override public void readAdditionalSaveData(CompoundTag tag) { super.readAdditionalSaveData(tag); sparkDelay = Math.max(1, Math.min(3, tag.getInt("VolcanoSparkDelay"))); }
    public static VolcanoBulletEntity shoot(Level level, LivingEntity owner, RandomSource random) { return shoot(level, owner, random, 1, 2, 1); }
    public static VolcanoBulletEntity shoot(Level level, LivingEntity owner, RandomSource random, float pullingPower) { return shoot(level, owner, random, pullingPower, 2, 1); }
    public static VolcanoBulletEntity shoot(Level level, LivingEntity owner, RandomSource random, float power, double damage, int knockback) { var bullet = new VolcanoBulletEntity(owner, level); var view = owner.getViewVector(1); bullet.shoot(view.x, view.y, view.z, power * 2, 0); bullet.setSilent(true); bullet.setCritArrow(false); bullet.setBaseDamage(damage); bullet.setImpactKnockback(knockback); level.addFreshEntity(bullet); return bullet; }
    public static VolcanoBulletEntity shoot(LivingEntity owner, LivingEntity target) { var bullet = new VolcanoBulletEntity(owner, owner.level()); double dx = target.getX() - owner.getX(); double dy = target.getY() + target.getEyeHeight() - 1.1; double dz = target.getZ() - owner.getZ(); bullet.shoot(dx, dy - bullet.getY() + Math.hypot(dx, dz) * .2F, dz, 2, 12); bullet.setSilent(true); bullet.setBaseDamage(2); bullet.setImpactKnockback(1); bullet.setCritArrow(false); owner.level().addFreshEntity(bullet); return bullet; }
}
