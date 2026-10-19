package com.hp.thermalshockreforged.entity.projectile;

import com.hp.thermalshockreforged.ThermalShockReforged;
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
    public static void fireBurstShot(net.minecraft.server.level.ServerLevel level, LivingEntity shooter, java.util.UUID owner, net.minecraft.world.phys.Vec3 origin, net.minecraft.world.phys.Vec3 direction) {
        var bullet = new VolcanoBulletEntity(ProjectileContent.VOLCANO_BULLET.get(), level);
        if (shooter != null) bullet.setOwner(shooter);
        else ((com.hp.thermalshockreforged.mixin.ProjectileOwnerAccess) (Object) bullet).thermalShockReforged$setOwnerUUID(owner);
        bullet.setPos(origin); bullet.setBaseDamage(3); bullet.setImpactKnockback(1); bullet.setSilent(true); bullet.setCritArrow(false);
        ((com.hp.thermalshockreforged.mixin.ArrowPiercingAccess) (Object) bullet).thermalShockReforged$setPierceLevel((byte) 2);
        bullet.shoot(direction.x, direction.y, direction.z, 4, 20); level.addFreshEntity(bullet);
    }
    private java.util.UUID sparkTask;
    private int sparkSubmissions;
    public VolcanoBulletEntity(EntityType<? extends VolcanoBulletEntity> type, Level level) { super(type, level); setImpactKnockback(1); }
    public VolcanoBulletEntity(LivingEntity owner, Level level) { super(ProjectileContent.VOLCANO_BULLET.get(), owner, level, new ItemStack(Items.FIRE_CHARGE)); setImpactKnockback(1); }
    @Override protected ItemStack projectileItem() { return new ItemStack(Items.FIRE_CHARGE); }
    @Override protected int lifetimeTicks() { return 3; }
    @Override public void tick() {
        super.tick();
        if (level() instanceof net.minecraft.server.level.ServerLevel server && sparkSubmissions < 3) {
            var submitted = VolcanoSparkEntity.submit(server, this, sparkTask);
            if (submitted != null) { sparkTask = submitted; sparkSubmissions++; }
        }
    }
    @Override protected void onHitEntity(EntityHitResult hit) { super.onHitEntity(hit); Entity target = hit.getEntity(); if (!(target instanceof Player) && level() instanceof net.minecraft.server.level.ServerLevel server) { server.playSound(null, blockPosition(), BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, "flareshoot")), SoundSource.NEUTRAL, 1, 1); target.hurt(new DamageSource(server.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(DamageTypes.UNATTRIBUTED_FIREBALL)), 3); discard(); } }
    @Override protected void onHitBlock(BlockHitResult hit) { super.onHitBlock(hit); if (getOwner() != null && !(getOwner() instanceof Player) && level() instanceof net.minecraft.server.level.ServerLevel server) { server.playSound(null, hit.getBlockPos(), BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, "flareshoot")), SoundSource.NEUTRAL, 1, 1); discard(); } }
    @Override public void addAdditionalSaveData(CompoundTag tag) { super.addAdditionalSaveData(tag); if (sparkTask != null) tag.putUUID("VolcanoSparkTask", sparkTask); tag.putInt("VolcanoSparkSubmissions", sparkSubmissions); }
    @Override public void readAdditionalSaveData(CompoundTag tag) { super.readAdditionalSaveData(tag); sparkTask = tag.hasUUID("VolcanoSparkTask") ? tag.getUUID("VolcanoSparkTask") : null; sparkSubmissions = Math.clamp(tag.getInt("VolcanoSparkSubmissions"), 0, 3); }
    public static VolcanoBulletEntity shoot(Level level, LivingEntity owner, RandomSource random) { return shoot(level, owner, random, 1, 2, 1); }
    public static VolcanoBulletEntity shoot(Level level, LivingEntity owner, RandomSource random, float pullingPower) { return shoot(level, owner, random, pullingPower, 2, 1); }
    public static VolcanoBulletEntity shoot(Level level, LivingEntity owner, RandomSource random, float power, double damage, int knockback) { var bullet = new VolcanoBulletEntity(owner, level); var view = owner.getViewVector(1); bullet.shoot(view.x, view.y, view.z, power * 2, 0); bullet.setSilent(true); bullet.setCritArrow(false); bullet.setBaseDamage(damage); bullet.setImpactKnockback(knockback); level.addFreshEntity(bullet); return bullet; }
    public static VolcanoBulletEntity shoot(LivingEntity owner, LivingEntity target) { var bullet = new VolcanoBulletEntity(owner, owner.level()); double dx = target.getX() - owner.getX(); double dy = target.getY() + target.getEyeHeight() - 1.1; double dz = target.getZ() - owner.getZ(); bullet.shoot(dx, dy - bullet.getY() + Math.hypot(dx, dz) * .2F, dz, 2, 12); bullet.setSilent(true); bullet.setBaseDamage(2); bullet.setImpactKnockback(1); bullet.setCritArrow(false); owner.level().addFreshEntity(bullet); return bullet; }
}
