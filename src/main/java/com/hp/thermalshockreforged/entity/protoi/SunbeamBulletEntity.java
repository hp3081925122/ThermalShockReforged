package com.hp.thermalshockreforged.entity.protoi;
import com.hp.thermalshockreforged.content.decor.nativeblocks.NativeDecorContent;
import com.hp.thermalshockreforged.entity.cavebloomer.CavebloomerContent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import com.hp.thermalshockreforged.entity.projectile.LegacyArrowEntity;
import com.hp.thermalshockreforged.mixin.ArrowPiercingAccess;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
public final class SunbeamBulletEntity extends LegacyArrowEntity {
    public SunbeamBulletEntity(EntityType<? extends SunbeamBulletEntity> type, Level level) { super(type, level); }
    public SunbeamBulletEntity(EntityType<? extends SunbeamBulletEntity> type, LivingEntity owner, Level level) { super(type, owner, level, new ItemStack(NativeDecorContent.LIGHT_BLOCKING_BARRIER.get())); }
    @Override protected ItemStack projectileItem() { return new ItemStack(NativeDecorContent.LIGHT_BLOCKING_BARRIER.get()); }
    public void configure(double damage, double knockback, byte piercing) {
        setBaseDamage(Double.isFinite(damage) ? Math.clamp(damage, 0, Float.MAX_VALUE) : 0);
        setImpactKnockback(Double.isFinite(knockback) ? Math.clamp(knockback, 0, 2) : 0);
        ((ArrowPiercingAccess)(Object)this).thermalShockReforged$setPierceLevel(piercing); setSilent(true); setCritArrow(false);
    }
    @Override protected void onHitBlock(BlockHitResult hit) { super.onHitBlock(hit); if (level() instanceof net.minecraft.server.level.ServerLevel server) server.sendParticles(CavebloomerContent.NUKE_CLOUD.get(), hit.getBlockPos().getX(), hit.getBlockPos().getY(), hit.getBlockPos().getZ(), 1, 0, 0, 0, 0); }
    public static SunbeamBulletEntity shoot(Level level, LivingEntity owner, double damage, int knockback) { return shoot(level, owner, owner.getRandom(), 1, damage, knockback); }
    public static SunbeamBulletEntity shoot(Level level, LivingEntity owner, net.minecraft.util.RandomSource random) { return shoot(level, owner, random, 1, 20, 1); }
    public static SunbeamBulletEntity shoot(Level level, LivingEntity owner, net.minecraft.util.RandomSource random, float power) { return shoot(level, owner, random, power, 20, 1); }
    public static SunbeamBulletEntity shoot(Level level, LivingEntity owner, net.minecraft.util.RandomSource random, float power, double damage, int knockback) {
        var bullet = new SunbeamBulletEntity(ProtoiContent.SUNBEAM_BULLET.get(), owner, level);
        Vec3 look = owner.getViewVector(1);
        bullet.configure(damage, knockback, (byte)0); bullet.shoot(look.x, look.y, look.z, power * 2, 0);
        level.addFreshEntity(bullet); return bullet;
    }
    public static SunbeamBulletEntity shoot(LivingEntity owner, LivingEntity target) {
        var bullet = new SunbeamBulletEntity(ProtoiContent.SUNBEAM_BULLET.get(), owner, owner.level());
        double dx = target.getX() - owner.getX(), dz = target.getZ() - owner.getZ();
        bullet.configure(20, 1, (byte)0);
        bullet.shoot(dx, target.getY() + target.getEyeHeight() - 1.1 - bullet.getY() + Math.hypot(dx, dz) * .2F, dz, 2, 12);
        owner.level().addFreshEntity(bullet); return bullet;
    }
}
