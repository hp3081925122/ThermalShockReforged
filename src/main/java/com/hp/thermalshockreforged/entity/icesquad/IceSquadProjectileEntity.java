package com.hp.thermalshockreforged.entity.icesquad;

import com.hp.thermalshockreforged.ThermalShockReforged;
import com.hp.thermalshockreforged.world.timing.ServerEndTickTask;
import com.hp.thermalshockreforged.world.timing.ServerEndTickTasks;
import com.hp.thermalshockreforged.content.decor.geo.GeoDecorContent;
import com.hp.thermalshockreforged.content.decor.geo.GeoDecorKind;
import com.hp.thermalshockreforged.content.decor.nativeblocks.NativeDecorContent;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.arrow.AbstractArrow;
import net.minecraft.world.entity.projectile.ItemSupplier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import java.util.UUID;

public final class IceSquadProjectileEntity extends AbstractArrow implements ItemSupplier, ServerEndTickTask {
    private UUID caster;
    private int remainingEndTicks;
    private boolean lifetimeStarted;
    private double impactKnockback;
    private boolean hitConsumed;
    public IceSquadProjectileEntity(EntityType<? extends IceSquadProjectileEntity> type, Level level) { super(type, level); }
    private String kind() { return BuiltInRegistries.ENTITY_TYPE.getKey(getType()).getPath(); }
    private boolean strider() { return kind().equals("strider_attack"); }
    private boolean reactive() { return kind().equals("icidrogunner_bullet"); }
    private int lifetime() {
        if (strider()) return 5;
        return 80;
    }
    @Override public ItemStack getItem() { return getDefaultPickupItem(); }
    @Override protected ItemStack getDefaultPickupItem() {
        if (strider()) return new ItemStack(NativeDecorContent.LIGHT_BLOCKING_BARRIER.get().asItem());
        return new ItemStack(GeoDecorContent.item(GeoDecorKind.BLUE_ORB));
    }
    public void configure(LivingEntity source, boolean owned, double multiplier, double knockback) {
        caster = source.getUUID();
        if (owned) setOwner(source);
        double damage = source.getAttributeValue(Attributes.ATTACK_DAMAGE) * multiplier;
        setBaseDamage(Double.isFinite(damage) ? Math.max(0, damage) : 0);
        impactKnockback = Double.isFinite(knockback) ? Math.clamp(knockback, 0, 0.6) : 0;
        setSilent(true);
    }
    @Override public void setOwner(Entity owner) { super.setOwner(owner); pickup = Pickup.DISALLOWED; }
    @Override protected boolean canHitEntity(Entity target) { return (caster == null || !caster.equals(target.getUUID())) && super.canHitEntity(target); }
    @Override protected void doPostHurtEffects(LivingEntity target) { super.doPostHurtEffects(target); target.setArrowCount(Math.max(0, target.getArrowCount() - 1)); }
    @Override protected void doKnockback(LivingEntity target, DamageSource source) {
        double resistance = Math.clamp(1 - target.getAttributeValue(Attributes.KNOCKBACK_RESISTANCE), 0, 1);
        var impulse = getDeltaMovement().multiply(1, 0, 1).normalize().scale(impactKnockback * resistance);
        if (impulse.lengthSqr() > 0) target.push(impulse.x, 0.1 * resistance, impulse.z);
    }
    private boolean claim(Entity target) {
        if (!reactive() || target == null) return true;
        if (hitConsumed) return false;
        hitConsumed = true;
        return true;
    }
    @Override protected void onHitEntity(EntityHitResult result) {
        if (!claim(result.getEntity())) return;
        super.onHitEntity(result);
        extraHit(result.getEntity(), getX(), getY(), getZ());
    }
    @Override protected void onHitBlock(BlockHitResult result) {
        if (!claim(getOwner())) return;
        super.onHitBlock(result);
        var pos = result.getBlockPos();
        extraHit(getOwner(), pos.getX(), pos.getY(), pos.getZ());
    }
    @Override public void playerTouch(Player player) {
        if (!claim(player)) return;
        super.playerTouch(player);
        extraHit(player, getX(), getY(), getZ());
    }
    private void extraHit(Entity target, double x, double y, double z) {
        if (!reactive() || target == null) return;
        if (!level().isClientSide()) discard();
        String targetId = BuiltInRegistries.ENTITY_TYPE.getKey(target.getType()).toString();
        if (targetId.equals("thermal_shock_reforged:icidrogunner") || targetId.equals("thermal_shock_reforged:icidroburner")) return;
        if (!level().isClientSide() && target instanceof LivingEntity living && living != getOwner()) living.addEffect(new MobEffectInstance(MobEffects.SLOWNESS, 40, 0));
        if (level() instanceof ServerLevel server && caster != null && target instanceof LivingEntity living && server.getEntity(caster) instanceof BastionEntity boss) BastionEntity.thermalShock(server, living, boss, true);
        sound(x, y, z, SoundEvents.GENERIC_HURT, 3);
        sound(x, y, z, SoundEvents.GLASS_BREAK, 6);
    }
    private void sound(double x, double y, double z, SoundEvent sound, float pitch) {
        if (level().isClientSide()) level().playLocalSound(x, y, z, sound, SoundSource.NEUTRAL, 1, pitch, false);
        else level().playSound(null, BlockPos.containing(x, y, z), sound, SoundSource.NEUTRAL, 1, pitch);
    }
    @Override public void tick() {
        if (!level().isClientSide() && lifetimeStarted && remainingEndTicks <= 0) { discard(); return; }
        super.tick();
        if (reactive()) level().addParticle(IceSquadContent.LOD_TRAIL.get(), getX(), getY(), getZ(), 0, 0, 0);
        else if (!strider()) level().addParticle(ParticleTypes.SOUL_FIRE_FLAME, getX(), getY(), getZ(), 0, 0, 0);
        setNoGravity(true);
        if (isInGround()) discard();
        if (!isRemoved() && level() instanceof ServerLevel server) {
            if (!lifetimeStarted) {
                lifetimeStarted = true; remainingEndTicks = lifetime();
                ThermalShockReforged.LOGGER.debug("Ice squad projectile lifetime armed: entity={}, kind={}, remainingEndTicks={}", getUUID(), kind(), remainingEndTicks);
            }
            ServerEndTickTasks.schedule(server, getUUID());
        }
    }
    @Override public void onAddedToLevel() {
        super.onAddedToLevel();
        if (lifetimeStarted && level() instanceof ServerLevel server) ServerEndTickTasks.schedule(server, getUUID());
    }
    @Override public void endTick(ServerLevel server) {
        if (isRemoved() || !lifetimeStarted) return;
        if (--remainingEndTicks <= 0) {
            ThermalShockReforged.LOGGER.debug("Ice squad projectile lifetime expired: entity={}, kind={}", getUUID(), kind());
            discard();
        } else ServerEndTickTasks.schedule(server, getUUID());
    }
    @Override public void addAdditionalSaveData(net.minecraft.world.level.storage.ValueOutput output) {
        super.addAdditionalSaveData(output); output.putInt("SquadProjectileRemainingEndTicks", remainingEndTicks); output.putBoolean("SquadProjectileLifetimeStarted", lifetimeStarted);
        output.putDouble("SquadProjectileKnockback", impactKnockback); output.putBoolean("SquadProjectileHit", hitConsumed);
        if (caster != null) output.putString("SquadProjectileCaster", caster.toString());
    }
    @Override public void readAdditionalSaveData(net.minecraft.world.level.storage.ValueInput input) {
        super.readAdditionalSaveData(input); pickup = Pickup.DISALLOWED;
        int legacyAge = Math.clamp(input.getIntOr("SquadProjectileAge", 0), 0, lifetime());
        lifetimeStarted = input.read("SquadProjectileLifetimeStarted", com.mojang.serialization.Codec.PASSTHROUGH).isPresent() ? input.getBooleanOr("SquadProjectileLifetimeStarted", false) : legacyAge > 0;
        remainingEndTicks = input.read("SquadProjectileRemainingEndTicks", com.mojang.serialization.Codec.PASSTHROUGH).isPresent() ? Math.clamp(input.getIntOr("SquadProjectileRemainingEndTicks", 0), 0, lifetime()) : lifetime() - legacyAge;
        impactKnockback = Double.isFinite(input.getDoubleOr("SquadProjectileKnockback", 0.0)) ? Math.clamp(input.getDoubleOr("SquadProjectileKnockback", 0.0), 0, 0.6) : 0;
        hitConsumed = input.getBooleanOr("SquadProjectileHit", false); caster = input.getString("SquadProjectileCaster").map(java.util.UUID::fromString).orElse(null);
        if (!Double.isFinite(input.getDoubleOr("damage", 2.0)) || input.getDoubleOr("damage", 2.0) < 0) setBaseDamage(0);
    }
}
