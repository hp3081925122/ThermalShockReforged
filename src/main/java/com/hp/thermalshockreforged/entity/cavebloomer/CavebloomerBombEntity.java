package com.hp.thermalshockreforged.entity.cavebloomer;

import com.hp.thermalshockreforged.content.MaterialContent;
import com.hp.thermalshockreforged.mixin.ArrowPiercingAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.projectile.arrow.AbstractArrow;
import net.minecraft.world.entity.projectile.ItemSupplier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.neoforge.event.EventHooks;
import java.util.UUID;

public final class CavebloomerBombEntity extends AbstractArrow implements ItemSupplier {
    private UUID caster;
    private double impactDamage = -1;
    private double impactKnockback;
    private boolean blockCommitted;
    public CavebloomerBombEntity(EntityType<? extends CavebloomerBombEntity> type, Level level) { super(type, level); pickup = Pickup.DISALLOWED; }
    private boolean poison() { return BuiltInRegistries.ENTITY_TYPE.getKey(getType()).getPath().equals("poison_bomb"); }
    @Override public ItemStack getItem() { return getDefaultPickupItem(); }
    @Override protected ItemStack getDefaultPickupItem() { return new ItemStack(MaterialContent.items().get(poison() ? "poison_projectile" : "gore_projectile").get()); }
    public void configure(LivingEntity owner, double directMultiplier, double explosionMultiplier, double knockback, byte piercing) {
        caster = owner.getUUID(); setOwner(owner);
        double base = owner.getAttributeValue(Attributes.ATTACK_DAMAGE);
        double direct = base * directMultiplier, explosion = base * explosionMultiplier;
        setBaseDamage(Double.isFinite(direct) ? Math.clamp(direct, 0, Float.MAX_VALUE) : 0);
        impactDamage = Double.isFinite(explosion) ? Math.clamp(explosion, 0, Float.MAX_VALUE) : 0;
        impactKnockback = Double.isFinite(knockback) ? Math.clamp(knockback, 0, 0.6) : 0;
        ((ArrowPiercingAccess)(Object)this).thermalShockReforged$setPierceLevel(piercing); setSilent(true);
    }
    @Override public void setOwner(Entity owner) { super.setOwner(owner); pickup = Pickup.DISALLOWED; }
    @Override protected boolean canHitEntity(Entity target) { return (caster == null || !caster.equals(target.getUUID())) && super.canHitEntity(target); }
    @Override protected void doPostHurtEffects(LivingEntity target) { super.doPostHurtEffects(target); target.setArrowCount(Math.max(0, target.getArrowCount() - 1)); }
    @Override protected void doKnockback(LivingEntity target, DamageSource source) {
        double resistance = Math.clamp(1 - target.getAttributeValue(Attributes.KNOCKBACK_RESISTANCE), 0, 1);
        var velocity = getDeltaMovement().multiply(1, 0, 1).normalize().scale(impactKnockback * resistance);
        if (velocity.lengthSqr() > 0) target.push(velocity.x, 0.1 * resistance, velocity.z);
    }
    @Override protected void onHitBlock(BlockHitResult hit) {
        if (blockCommitted) return;
        blockCommitted = true;
        super.onHitBlock(hit);
        if (!(level() instanceof ServerLevel server)) return;
        var type = (poison() ? CavebloomerContent.POISON_IMPACT : CavebloomerContent.SHOCK_IMPACT).get();
        var carrier = type.create(server, net.minecraft.world.entity.EntitySpawnReason.MOB_SUMMONED);
        if (carrier == null) return;
        var pos = hit.getBlockPos().above();
        carrier.snapTo(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, server.getRandom().nextFloat() * 360, 0);
        carrier.configureDamage(impactDamage < 0 ? poison() ? 6 : 10 : impactDamage);
        EventHooks.finalizeMobSpawn(carrier, server, server.getCurrentDifficultyAt(pos), EntitySpawnReason.MOB_SUMMONED, null);
        if (!carrier.initialized() || carrier.isSpawnCancelled()) { carrier.discard(); return; }
        carrier.setYRot(server.getRandom().nextFloat() * 360);
        if (!carrier.isRemoved()) server.addFreshEntity(carrier);
    }
    @Override public void tick() {
        super.tick();
        level().addParticle(CavebloomerContent.RED_ORB.get(), getX(), getY(), getZ(), 0, 0, 0);
        if (isInGround()) discard();
    }
    @Override public void addAdditionalSaveData(net.minecraft.world.level.storage.ValueOutput output) {
        super.addAdditionalSaveData(output); if (caster != null) output.putString("BombCaster", caster.toString());
        output.putDouble("BombImpactDamage", impactDamage); output.putDouble("BombKnockback", impactKnockback); output.putBoolean("BombBlockCommitted", blockCommitted);
    }
    @Override public void readAdditionalSaveData(net.minecraft.world.level.storage.ValueInput input) {
        super.readAdditionalSaveData(input); caster = input.getString("BombCaster").map(java.util.UUID::fromString).orElse(null);
        double value = input.read("BombImpactDamage", com.mojang.serialization.Codec.PASSTHROUGH).isPresent() ? input.getDoubleOr("BombImpactDamage", 0.0) : -1;
        impactDamage = Double.isFinite(value) ? Math.clamp(value, -1, Float.MAX_VALUE) : 0;
        value = input.getDoubleOr("BombKnockback", 0.0); impactKnockback = Double.isFinite(value) ? Math.clamp(value, 0, 0.6) : 0;
        blockCommitted = input.getBooleanOr("BombBlockCommitted", false); pickup = Pickup.DISALLOWED;
    }
}
