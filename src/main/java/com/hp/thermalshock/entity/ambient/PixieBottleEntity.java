package com.hp.thermalshock.entity.ambient;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.*;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.projectile.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.*;

public final class PixieBottleEntity extends AbstractArrow implements ItemSupplier {
    private boolean released;
    public PixieBottleEntity(EntityType<? extends PixieBottleEntity> type, Level level) { super(type, level); }
    public PixieBottleEntity(LivingEntity owner, Level level) { super(AmbientContent.PIXIE_BOTTLE.get(), owner, level, new ItemStack(AmbientContent.BOTTLED_PIXIE.get()), null); }
    @Override
    public ItemStack getItem() { return new ItemStack(AmbientContent.BOTTLED_PIXIE.get()); }
    @Override
    protected ItemStack getDefaultPickupItem() { return getItem(); }
    @Override
    protected void doPostHurtEffects(LivingEntity target) { super.doPostHurtEffects(target); target.setArrowCount(target.getArrowCount() - 1); }
    @Override
    protected void doKnockback(LivingEntity target, DamageSource source) { }
    @Override
    protected void onHitEntity(EntityHitResult result) {
        if (released) return;
        pickup = Pickup.DISALLOWED;
        super.onHitEntity(result);
        releasePixie(getX(), getY(), getZ());
    }
    @Override
    protected void onHitBlock(BlockHitResult result) { super.onHitBlock(result); var pos = result.getBlockPos(); releasePixie(pos.getX(), pos.getY(), pos.getZ()); }
    private void releasePixie(double x, double y, double z) {
        if (released || !(level() instanceof ServerLevel server)) return;
        released = true;
        pickup = Pickup.DISALLOWED;
        var pixie = AmbientContent.PIXIE.get().spawn(server, BlockPos.containing(x + 0.5, y + 1, z + 0.5), MobSpawnType.MOB_SUMMONED);
        if (pixie != null) pixie.setDeltaMovement(Vec3.ZERO);
        server.playSound(null, BlockPos.containing(x, y, z), SoundEvents.SPLASH_POTION_BREAK, SoundSource.NEUTRAL, 1, 1);
        discard();
    }
    @Override
    public void addAdditionalSaveData(CompoundTag tag) { super.addAdditionalSaveData(tag); tag.putBoolean("Released", released); }
    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        released = tag.getBoolean("Released");
        if (released) { pickup = Pickup.DISALLOWED; discard(); }
    }
    @Override
    public void tick() { super.tick(); if (inGround) discard(); }
    public static PixieBottleEntity shoot(Level level, LivingEntity owner, float power) {
        var bottle = new PixieBottleEntity(owner, level);
        var view = owner.getViewVector(1);
        bottle.shoot(view.x, view.y, view.z, power * 2, 0);
        bottle.setSilent(true); bottle.setCritArrow(false); bottle.setBaseDamage(1);
        level.addFreshEntity(bottle);
        level.playSound(null, owner.getX(), owner.getY(), owner.getZ(), SoundEvents.SPLASH_POTION_THROW, SoundSource.PLAYERS, 1, 1 / (level.random.nextFloat() * 0.5F + 1) + power / 2);
        return bottle;
    }
    public static PixieBottleEntity shoot(LivingEntity owner, LivingEntity target) {
        var bottle = new PixieBottleEntity(owner, owner.level());
        double dx = target.getX() - owner.getX(), dz = target.getZ() - owner.getZ();
        double dy = target.getY() + target.getEyeHeight() - 1.1;
        bottle.shoot(dx, dy - bottle.getY() + Math.hypot(dx, dz) * 0.2, dz, 1.4F, 12);
        bottle.setSilent(true); bottle.setCritArrow(false); bottle.setBaseDamage(owner.getAttributeValue(Attributes.ATTACK_DAMAGE));
        owner.level().addFreshEntity(bottle);
        owner.level().playSound(null, owner.getX(), owner.getY(), owner.getZ(), SoundEvents.SPLASH_POTION_THROW, SoundSource.PLAYERS, 1, 1 / (owner.getRandom().nextFloat() * 0.5F + 1));
        return bottle;
    }
}
