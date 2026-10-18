package com.hp.thermalshockreforged.entity.springslime;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.projectile.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.*;

public final class SpringslimeProjectile extends AbstractArrow implements ItemSupplier {
    private boolean consumed;
    public SpringslimeProjectile(EntityType<? extends SpringslimeProjectile> type, Level level) { super(type, level); pickup = Pickup.DISALLOWED; }
    @Override public void setOwner(Entity owner) { super.setOwner(owner); pickup = Pickup.DISALLOWED; }
    @Override protected ItemStack getDefaultPickupItem() { return new ItemStack(SpringslimeContent.COMPRESSED.get()); }
    @Override public ItemStack getItem() { return getDefaultPickupItem(); }
    @Override protected void doPostHurtEffects(LivingEntity entity) { super.doPostHurtEffects(entity); entity.setArrowCount(Math.max(0, entity.getArrowCount() - 1)); }
    @Override protected void doKnockback(LivingEntity target, DamageSource source) {
        double resistance = Math.clamp(1 - target.getAttributeValue(Attributes.KNOCKBACK_RESISTANCE), 0, 1);
        var impulse = getDeltaMovement().multiply(1, 0, 1).normalize().scale(.6 * resistance);
        if (impulse.lengthSqr() > 0) target.push(impulse.x, .1 * resistance, impulse.z);
    }
    @Override protected void onHitEntity(EntityHitResult hit) {
        if (consumed) return;
        consumed = true;
        try { super.onHitEntity(hit); } finally { discard(); }
    }
    @Override protected void onHitBlock(BlockHitResult hit) {
        if (consumed) return;
        consumed = true;
        try {
            super.onHitBlock(hit);
            if (level() instanceof ServerLevel server) {
                var spring = SpringslimeContent.SPRING.get().spawn(server, hit.getBlockPos().offset(1, 1, 1), MobSpawnType.MOB_SUMMONED);
                if (spring != null) spring.setDeltaMovement(0, 0, 0);
            }
        } finally { discard(); }
    }
    @Override public void tick() { super.tick(); if (inGround || consumed) discard(); }
    @Override public void addAdditionalSaveData(CompoundTag tag) { super.addAdditionalSaveData(tag); tag.putBoolean("SpringImpactConsumed", consumed); }
    @Override public void readAdditionalSaveData(CompoundTag tag) { super.readAdditionalSaveData(tag); consumed = tag.getBoolean("SpringImpactConsumed"); pickup = Pickup.DISALLOWED; }
}
