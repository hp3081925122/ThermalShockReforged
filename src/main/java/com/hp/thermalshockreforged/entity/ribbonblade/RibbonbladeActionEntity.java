package com.hp.thermalshockreforged.entity.ribbonblade;

import com.hp.thermalshockreforged.world.timing.*;
import java.util.Comparator;
import java.util.UUID;
import net.minecraft.commands.arguments.EntityAnchorArgument.Anchor;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.*;
import net.minecraft.world.damagesource.*;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.*;

public final class RibbonbladeActionEntity extends Entity implements ServerEndTickTask {
    private static final ResourceKey<DamageType> DAMAGE = ResourceKey.create(Registries.DAMAGE_TYPE, RibbonbladeContent.id("errant_damage"));
    private UUID caster;
    private UUID target;
    private Vec3 lastTarget = Vec3.ZERO;
    private Vec3 look = new Vec3(0, 0, 1);
    private BlockPos soundOrigin = BlockPos.ZERO;
    private double damage;
    private double knockback;
    private int remaining;
    private boolean strike;
    private boolean armed;
    public RibbonbladeActionEntity(EntityType<? extends RibbonbladeActionEntity> type, Level level) { super(type, level); noPhysics = true; setNoGravity(true); }
    public static RibbonbladeActionEntity submitStrike(ServerLevel level, RibbonbladeGalvaniteEntity caster, LivingEntity target, Vec3 destination) {
        var task = RibbonbladeContent.ACTION.get().create(level);
        if (task == null) return null;
        task.caster = caster.getUUID(); task.target = target.getUUID(); task.lastTarget = target.position(); task.soundOrigin = target.blockPosition();
        task.strike = true; task.armed = true; task.remaining = 17; task.capture(caster); task.setPos(destination);
        return level.addFreshEntity(task) ? task : null;
    }
    public static void submitSpawn(ServerLevel level, RibbonbladeGalvaniteEntity caster) {
        var task = RibbonbladeContent.ACTION.get().create(level);
        if (task == null) return;
        task.caster = caster.getUUID(); task.setPos(caster.position()); task.soundOrigin = caster.blockPosition(); task.remaining = 5; task.armed = true;
        level.addFreshEntity(task);
    }
    void capture(RibbonbladeGalvaniteEntity owner) { setPos(owner.position()); look = owner.getLookAngle(); double value = owner.getAttributeValue(Attributes.ATTACK_DAMAGE) * RibbonbladeConfig.MULTIPLIER.get(); damage = Double.isFinite(value) ? Math.clamp(value, 0, Float.MAX_VALUE) : 0; knockback = RibbonbladeConfig.KNOCKBACK.get(); }
    @Override protected void defineSynchedData(SynchedEntityData.Builder builder) { }
    @Override public boolean canChangeDimensions(Level from, Level to) { return false; }
    @Override public void onAddedToLevel() { super.onAddedToLevel(); if (armed && level() instanceof ServerLevel server) ServerEndTickTasks.schedule(server, getUUID()); }
    @Override public void tick() {
        super.tick();
        if (!(level() instanceof ServerLevel server)) return;
        if (!armed) { discard(); return; }
        if (strike) {
            if (server.getEntity(caster) instanceof RibbonbladeGalvaniteEntity owner) capture(owner);
            if (target != null && server.getEntity(target) instanceof LivingEntity victim) lastTarget = victim.position();
        }
        ServerEndTickTasks.schedule(server, getUUID());
    }
    @Override public void endTick(ServerLevel level) {
        if (!armed || isRemoved()) return;
        if (--remaining > 0) { ServerEndTickTasks.schedule(level, getUUID()); return; }
        armed = false;
        discard();
        if (!strike) { level.playSound(null, soundOrigin, BuiltInRegistries.SOUND_EVENT.get(RibbonbladeContent.id("ribbonblade_spawns")), SoundSource.HOSTILE, 1, 1); return; }
        var source = level.getEntity(caster);
        if (source == null) for (var other : level.getServer().getAllLevels()) if (other != level && other.getEntity(caster) != null) return;
        if (source instanceof RibbonbladeGalvaniteEntity owner) {
            if (target != null && level.getEntity(target) instanceof LivingEntity victim) lastTarget = victim.position();
            owner.lookAt(Anchor.EYES, lastTarget); look = owner.getLookAngle();
            owner.setDeltaMovement(look); owner.hurtMarked = true; capture(owner);
        } else {
            Vec3 direction = lastTarget.subtract(position().add(0, 1.7, 0));
            if (direction.lengthSqr() > 1.0E-8) look = direction.normalize();
        }
        level.playSound(null, soundOrigin, SoundEvents.PLAYER_ATTACK_SWEEP, SoundSource.HOSTILE, 1, 2);
        var targets = level.getEntities(this, new AABB(position(), position()).inflate(1.5), entity -> !entity.getUUID().equals(caster) && !(entity instanceof LivingEntity living && living.isBlocking()));
        targets.sort(Comparator.comparingDouble(entity -> entity.distanceToSqr(position())));
        var damageSource = new DamageSource(level.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(DAMAGE));
        for (var victim : targets) {
            double amount = damage - (victim instanceof LivingEntity living ? living.getArmorValue() * .4 : 0);
            if (Double.isFinite(amount) && amount > 0) victim.hurt(damageSource, (float)Math.min(amount, Float.MAX_VALUE));
            double resistance = victim instanceof LivingEntity living ? Math.clamp(living.getAttributeValue(Attributes.KNOCKBACK_RESISTANCE), 0, 1) : 0;
            double strength = knockback * (1 - resistance);
            if (strength > 0) { victim.setDeltaMovement(look.scale(strength)); victim.hurtMarked = true; }
        }
    }
    @Override protected void addAdditionalSaveData(CompoundTag tag) {
        if (caster != null) tag.putUUID("Caster", caster); if (target != null) tag.putUUID("Target", target);
        tag.putDouble("TargetX", lastTarget.x); tag.putDouble("TargetY", lastTarget.y); tag.putDouble("TargetZ", lastTarget.z);
        tag.putDouble("LookX", look.x); tag.putDouble("LookY", look.y); tag.putDouble("LookZ", look.z);
        tag.putLong("SoundOrigin", soundOrigin.asLong()); tag.putDouble("Damage", damage); tag.putDouble("Knockback", knockback);
        tag.putInt("Remaining", remaining); tag.putBoolean("Strike", strike); tag.putBoolean("Armed", armed);
    }
    @Override protected void readAdditionalSaveData(CompoundTag tag) {
        caster = tag.hasUUID("Caster") ? tag.getUUID("Caster") : null; target = tag.hasUUID("Target") ? tag.getUUID("Target") : null;
        lastTarget = new Vec3(tag.getDouble("TargetX"), tag.getDouble("TargetY"), tag.getDouble("TargetZ")); look = new Vec3(tag.getDouble("LookX"), tag.getDouble("LookY"), tag.getDouble("LookZ"));
        soundOrigin = BlockPos.of(tag.getLong("SoundOrigin")); double amount = tag.getDouble("Damage"); damage = Double.isFinite(amount) ? Math.clamp(amount, 0, Float.MAX_VALUE) : 0;
        double strength = tag.getDouble("Knockback"); knockback = Double.isFinite(strength) ? Math.clamp(strength, 0, 2) : .6;
        strike = tag.getBoolean("Strike"); remaining = Math.clamp(tag.getInt("Remaining"), 1, strike ? 17 : 5);
        armed = tag.getBoolean("Armed") && caster != null && finite(lastTarget) && finite(look);
    }
    private static boolean finite(Vec3 value) { return Double.isFinite(value.x) && Double.isFinite(value.y) && Double.isFinite(value.z); }
}
