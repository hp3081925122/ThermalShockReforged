package com.hp.thermalshockreforged.entity.projectile;

import com.hp.thermalshockreforged.ThermalShockReforged;
import com.hp.thermalshockreforged.content.ModDamageTypes;
import com.hp.thermalshockreforged.content.weapons.DiskResonanceEntity;
import com.hp.thermalshockreforged.content.weapons.WeaponContent;
import com.hp.thermalshockreforged.entity.weaponcore.FlareGunCoreEntity;
import java.util.HashSet;
import java.util.Set;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
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
    private static final ThreadLocal<Set<LivingEntity>> DISCHARGING = new ThreadLocal<>();
    public LiteprismDiskProjectileEntity(EntityType<? extends LiteprismDiskProjectileEntity> type, Level level) { super(type, level); }
    private LiteprismDiskProjectileEntity(ServerLevel level, LivingEntity owner) { super(WeaponContent.LITEPRISM_DISK_PROJECTILE.get(), owner, level, new ItemStack(WeaponContent.LITEPRISM_DISK.get())); }
    @Override protected ItemStack projectileItem() { return new ItemStack(WeaponContent.LITEPRISM_DISK.get()); }
    public static boolean fire(ServerLevel level, LivingEntity owner) {
        var disk = new LiteprismDiskProjectileEntity(level, owner);
        var direction = owner.getViewVector(1);
        disk.shoot(direction.x, direction.y, direction.z, 2, 0);
        disk.setSilent(true); disk.setCritArrow(false); disk.setBaseDamage(2); disk.setImpactKnockback(0);
        if (!level.addFreshEntity(disk)) return false;
        level.playSound(null, owner.getX(), owner.getY(), owner.getZ(), SoundEvents.TRIDENT_THROW.value(), SoundSource.PLAYERS, 1, 1 / (level.getRandom().nextFloat() * .5F + 1) + .5F);
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
                Set<LivingEntity> active = DISCHARGING.get();
                if (active == null) { active = new HashSet<>(); DISCHARGING.set(active); }
                if (active.add(living)) {
                    try {
                        living.hurt(new DamageSource(server.registryAccess().lookupOrThrow(Registries.DAMAGE_TYPE).getOrThrow(ModDamageTypes.THERM_IFRAME_BYPASS)), 10);
                        server.sendParticles(WeaponContent.PRISM_CLUSTER.get(), getX(), living.getEyeY(), getZ(), 1, .0001, .0001, .0001, 0);
                        living.removeEffect(WeaponContent.LITEPRISM_BUILDUP);
                        server.playSound(null, blockPosition(), BuiltInRegistries.SOUND_EVENT.getValue(Identifier.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, "prism_breakout")), SoundSource.NEUTRAL, 1, 1);
                        ThermalShockReforged.LOGGER.debug("Liteprism buildup discharged: target={}", living.getUUID());
                    } finally {
                        active.remove(living);
                        if (active.isEmpty()) DISCHARGING.remove();
                    }
                }
            }
        }
        if (target instanceof FlareGunCoreEntity) DiskResonanceEntity.submit(server, position(), getOwner());
    }
}
