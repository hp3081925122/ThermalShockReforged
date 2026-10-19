package com.hp.thermalshockreforged.entity.projectile;

import com.hp.thermalshockreforged.ThermalShockReforged;
import com.hp.thermalshockreforged.content.ModDamageTypes;
import com.hp.thermalshockreforged.content.weapons.DiskResonanceEntity;
import com.hp.thermalshockreforged.content.weapons.WeaponContent;
import com.hp.thermalshockreforged.entity.weaponcore.FlareGunCoreEntity;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;

public final class LiteprismDiskProjectileEntity extends LegacyArrowEntity {
    public LiteprismDiskProjectileEntity(EntityType<? extends LiteprismDiskProjectileEntity> type, Level level) { super(type, level); }
    private LiteprismDiskProjectileEntity(ServerLevel level, LivingEntity owner) { super(WeaponContent.LITEPRISM_DISK_PROJECTILE.get(), owner, level, new ItemStack(WeaponContent.LITEPRISM_DISK.get())); }
    @Override protected ItemStack projectileItem() { return new ItemStack(WeaponContent.LITEPRISM_DISK.get()); }
    public static boolean fire(ServerLevel level, LivingEntity owner) {
        var disk = new LiteprismDiskProjectileEntity(level, owner);
        var direction = owner.getViewVector(1);
        disk.shoot(direction.x, direction.y, direction.z, 2, 0);
        disk.setSilent(true); disk.setCritArrow(false); disk.setBaseDamage(2); disk.setImpactKnockback(0);
        if (!level.addFreshEntity(disk)) return false;
        level.playSound(null, owner.getX(), owner.getY(), owner.getZ(), SoundEvents.TRIDENT_THROW.value(), SoundSource.PLAYERS, 1, 1 / (level.random.nextFloat() * .5F + 1) + .5F);
        return true;
    }
    @Override protected void onHitEntity(EntityHitResult hit) {
        super.onHitEntity(hit);
        if (!(level() instanceof ServerLevel server) || getOwner() == null) return;
        var target = hit.getEntity();
        if (target instanceof LivingEntity living) {
            var previous = living.getEffect(WeaponContent.LITEPRISM_BUILDUP);
            living.addEffect(new MobEffectInstance(WeaponContent.LITEPRISM_BUILDUP, 20, previous == null ? 0 : previous.getAmplifier() + 1));
            var buildup = living.getEffect(WeaponContent.LITEPRISM_BUILDUP);
            if (buildup != null && buildup.getAmplifier() >= 6) {
                living.removeEffect(WeaponContent.LITEPRISM_BUILDUP);
                living.hurt(new DamageSource(server.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(ModDamageTypes.THERM_IFRAME_BYPASS)), 10);
                server.sendParticles(WeaponContent.PRISM_CLUSTER.get(), getX(), living.getEyeY(), getZ(), 1, .0001, .0001, .0001, 0);
                server.playSound(null, blockPosition(), BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, "prism_breakout")), SoundSource.NEUTRAL, 1, 1);
                ThermalShockReforged.LOGGER.debug("Liteprism buildup discharged: target={}", living.getUUID());
            }
        }
        if (target instanceof FlareGunCoreEntity) DiskResonanceEntity.submit(server, position(), getOwner());
    }
}
