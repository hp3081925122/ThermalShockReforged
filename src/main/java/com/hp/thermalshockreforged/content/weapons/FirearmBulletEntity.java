package com.hp.thermalshockreforged.content.weapons;
import net.minecraft.world.entity.projectile.arrow.AbstractArrow;

import com.hp.thermalshockreforged.ThermalShockReforged;
import com.hp.thermalshockreforged.content.ModDamageTypes;
import com.hp.thermalshockreforged.content.decor.geo.*;
import com.hp.thermalshockreforged.world.timing.*;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.*;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.*;

public final class FirearmBulletEntity extends AbstractArrow implements ItemSupplier, ServerEndTickTask {
    private static final TagKey<EntityType<?>> PYRO_WEAK = weak("pyro_weak");
    private static final TagKey<EntityType<?>> CRYO_WEAK = weak("cryo_weak");
    private int remaining;
    private boolean armed;
    public FirearmBulletEntity(EntityType<? extends FirearmBulletEntity> type, Level level) {
        super(type, level);
        remaining = isHailfall() ? 40 : 7;
        pickup = Pickup.DISALLOWED;
    }
    private static TagKey<EntityType<?>> weak(String id) { return TagKey.create(Registries.ENTITY_TYPE, Identifier.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, id)); }
    public boolean isHailfall() { return getType() == WeaponContent.HAILFALL_BULLET.get(); }
    @Override public ItemStack getItem() { return new ItemStack(GeoDecorContent.item(isHailfall() ? GeoDecorKind.HAIL : GeoDecorKind.YELLOW_ORB)); }
    @Override protected ItemStack getDefaultPickupItem() { return getItem(); }
    @Override protected ItemStack getPickupItem() { return ItemStack.EMPTY; }
    @Override public void setOwner(Entity owner) { super.setOwner(owner); pickup = Pickup.DISALLOWED; }
    @Override protected void doPostHurtEffects(LivingEntity target) { super.doPostHurtEffects(target); target.setArrowCount(Math.max(0, target.getArrowCount() - 1)); }
    @Override protected void onHitEntity(EntityHitResult hit) {
        super.onHitEntity(hit);
        Entity target = hit.getEntity();
        if (!(level() instanceof ServerLevel server) || target instanceof Player) return;
        DamageSource elemental = new DamageSource(server.registryAccess().lookupOrThrow(Registries.DAMAGE_TYPE).getOrThrow(ModDamageTypes.THERM_IFRAME_BYPASS));
        if (isHailfall()) target.hurt(elemental, 1);
        sound(server, blockPosition(), SoundEvents.GENERIC_HURT, 1);
        sound(server, blockPosition(), isHailfall() ? SoundEvents.GLASS_BREAK : SoundEvents.FIREWORK_ROCKET_BLAST, isHailfall() ? 2 : 1);
        if (target.is(isHailfall() ? CRYO_WEAK : PYRO_WEAK)) target.hurt(elemental, 6);
    }
    @Override protected void onHitBlock(BlockHitResult hit) {
        super.onHitBlock(hit);
        if (isHailfall() && getOwner() != null && !(getOwner() instanceof Player) && level() instanceof ServerLevel server) {
            discard(); sound(server, hit.getBlockPos(), SoundEvents.GENERIC_HURT, 1); sound(server, hit.getBlockPos(), SoundEvents.GLASS_BREAK, 2);
        }
    }
    private static void sound(ServerLevel level, BlockPos pos, SoundEvent sound, float pitch) { level.playSound(null, pos, sound, SoundSource.NEUTRAL, 1, pitch); }
    @Override public void tick() {
        super.tick();
        setNoGravity(true);
        if (isHailfall()) level().addParticle(ParticleTypes.SNOWFLAKE, getX() + .5, getY() + .5, getZ() + .5, 0, 0, 0);
        if (isInGround()) discard();
        if (!isRemoved() && level() instanceof ServerLevel server) { armed = true; ServerEndTickTasks.schedule(server, getUUID()); }
    }
    @Override public void endTick(ServerLevel level) {
        if (isRemoved() || !armed) return;
        if (--remaining <= 0) discard(); else ServerEndTickTasks.schedule(level, getUUID());
    }
    @Override public void addAdditionalSaveData(net.minecraft.world.level.storage.ValueOutput output) { super.addAdditionalSaveData(output); output.putInt("FirearmRemaining", remaining); output.putBoolean("FirearmArmed", armed); }
    @Override public void readAdditionalSaveData(net.minecraft.world.level.storage.ValueInput input) {
        super.readAdditionalSaveData(input);
        remaining = input.read("FirearmRemaining", com.mojang.serialization.Codec.PASSTHROUGH).isPresent() ? Math.clamp(input.getIntOr("FirearmRemaining", 0), 1, isHailfall() ? 40 : 7) : isHailfall() ? 40 : 7;
        armed = input.getBooleanOr("FirearmArmed", false); pickup = Pickup.DISALLOWED;
        if (!Double.isFinite(input.getDoubleOr("damage", 2.0)) || input.getDoubleOr("damage", 2.0) < 0) setBaseDamage(0);
    }
}
