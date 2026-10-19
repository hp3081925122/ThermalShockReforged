package com.hp.thermalshockreforged.entity.effigy;

import com.hp.thermalshockreforged.ThermalShockReforged;
import com.hp.thermalshockreforged.world.timing.*;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.*;
import net.minecraft.world.damagesource.*;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public final class EffigyStrikeEntity extends Entity implements ServerEndTickTask {
    private static final ResourceKey<DamageType> DAMAGE = ResourceKey.create(Registries.DAMAGE_TYPE, EffigyContent.id("errant_damage"));
    private UUID caster;
    private UUID target;
    private int targetEntityId;
    private boolean playerTarget;
    private BlockPos soundOrigin = BlockPos.ZERO;
    private double damage;
    private int remaining = 19;
    private boolean armed;
    private boolean followCaster = true;
    public EffigyStrikeEntity(EntityType<? extends EffigyStrikeEntity> type, Level level) { super(type, level); noPhysics = true; setNoGravity(true); }
    static boolean submit(ServerLevel level, EffigyEntity caster, LivingEntity target) {
        var task = EffigyContent.STRIKE.get().create(level);
        if (task == null) return false;
        task.caster = caster.getUUID(); task.target = target.getUUID(); task.targetEntityId = target.getId(); task.playerTarget = target instanceof Player;
        task.soundOrigin = target.blockPosition(); task.setPos(caster.position()); task.damage = caster.getAttributeValue(Attributes.ATTACK_DAMAGE) * (8.0 / 3);
        task.armed = true;
        if (!level.addFreshEntity(task) || level.getEntity(task.getUUID()) != task) return false;
        caster.trackStrike(task.getUUID());
        ThermalShockReforged.LOGGER.debug("Effigy strike committed: task={}, caster={}, target={}, remaining={}", task.getUUID(), task.caster, task.target, task.remaining);
        return true;
    }
    void capture(EffigyEntity owner) { setPos(owner.position()); double amount = owner.getAttributeValue(Attributes.ATTACK_DAMAGE) * (8.0 / 3); damage = Double.isFinite(amount) ? Math.clamp(amount, 0, Float.MAX_VALUE) : 0; }
    void release(EffigyEntity owner, RemovalReason reason) { capture(owner); if (reason.shouldDestroy() || reason == RemovalReason.CHANGED_DIMENSION) followCaster = false; ThermalShockReforged.LOGGER.debug("Effigy strike caster removed: task={}, caster={}, reason={}, following={}", getUUID(), caster, reason, followCaster); }
    @Override protected void defineSynchedData(SynchedEntityData.Builder builder) { }
    @Override public boolean canChangeDimensions(Level from, Level to) { return false; }
    @Override public void onAddedToLevel() { super.onAddedToLevel(); if (armed && level() instanceof ServerLevel server) ServerEndTickTasks.schedule(server, getUUID()); }
    @Override public void tick() { super.tick(); if (level() instanceof ServerLevel server) { if (!armed) { discard(); return; } if (followCaster && server.getEntity(caster) instanceof EffigyEntity owner) capture(owner); ServerEndTickTasks.schedule(server, getUUID()); } }
    @Override public void endTick(ServerLevel level) {
        if (!armed || isRemoved()) return;
        if (--remaining > 0) { ServerEndTickTasks.schedule(level, getUUID()); return; }
        armed = false;
        var owner = level.getEntity(caster);
        if (owner instanceof EffigyEntity effigy) { if (followCaster) capture(effigy); effigy.forgetStrike(getUUID()); }
        discard();
        level.playSound(null, soundOrigin, SoundEvents.PLAYER_ATTACK_SWEEP, SoundSource.HOSTILE, 1, 2);
        var victim = level.getEntity(target);
        if (!(victim instanceof LivingEntity living) || !living.isAlive() || playerTarget && victim.getId() != targetEntityId || victim.distanceToSqr(position()) > 9) return;
        double amount = damage - living.getArmorValue() * .6;
        if (Double.isFinite(amount) && amount > 0) { boolean accepted = living.hurt(new DamageSource(level.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(DAMAGE)), (float)Math.min(amount, Float.MAX_VALUE)); ThermalShockReforged.LOGGER.debug("Effigy strike resolved: task={}, target={}, damage={}, accepted={}", getUUID(), target, amount, accepted); }
    }
    @Override protected void addAdditionalSaveData(CompoundTag tag) {
        if (caster != null) tag.putUUID("Caster", caster); if (target != null) tag.putUUID("Target", target);
        tag.putInt("TargetEntityId", targetEntityId); tag.putBoolean("PlayerTarget", playerTarget); tag.putLong("SoundOrigin", soundOrigin.asLong());
        tag.putDouble("Damage", damage); tag.putInt("Remaining", remaining); tag.putBoolean("Armed", armed); tag.putBoolean("FollowCaster", followCaster);
    }
    @Override protected void readAdditionalSaveData(CompoundTag tag) {
        caster = tag.hasUUID("Caster") ? tag.getUUID("Caster") : null; target = tag.hasUUID("Target") ? tag.getUUID("Target") : null;
        targetEntityId = tag.getInt("TargetEntityId"); playerTarget = tag.getBoolean("PlayerTarget"); soundOrigin = BlockPos.of(tag.getLong("SoundOrigin"));
        double amount = tag.getDouble("Damage"); damage = Double.isFinite(amount) ? Math.clamp(amount, 0, Float.MAX_VALUE) : 0;
        remaining = Math.clamp(tag.getInt("Remaining"), 1, 19); armed = tag.getBoolean("Armed") && caster != null && target != null && !caster.equals(target);
        followCaster = !tag.contains("FollowCaster") || tag.getBoolean("FollowCaster");
    }
}
