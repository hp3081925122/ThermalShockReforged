package com.hp.thermalshockreforged.entity.gravers;

import com.hp.thermalshockreforged.world.timing.*;
import java.lang.ref.WeakReference;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.*;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.*;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public final class RazoraptorStrikeEntity extends Entity implements ServerEndTickTask {
    private static final ResourceKey<DamageType> DAMAGE_TYPE = ResourceKey.create(Registries.DAMAGE_TYPE, GraverContent.id("errant_damage"));
    private UUID caster;
    private UUID target;
    private int targetEntityId;
    private boolean playerTarget;
    private WeakReference<LivingEntity> originalPlayer;
    private Vec3 soundOrigin = Vec3.ZERO;
    private Vec3 look = Vec3.ZERO;
    private String dimension = "";
    private double damage;
    private double knockback;
    private int elapsed;
    private boolean armed;
    public RazoraptorStrikeEntity(EntityType<? extends RazoraptorStrikeEntity> type, Level level) { super(type, level); noPhysics = true; setNoGravity(true); }
    static boolean submit(ServerLevel server, RazoraptorEntity owner, LivingEntity victim) {
        var task = GraverContent.STRIKE.get().create(server, net.minecraft.world.entity.EntitySpawnReason.MOB_SUMMONED);
        if (task == null) return false;
        task.caster = owner.getUUID(); task.target = victim.getUUID(); task.targetEntityId = victim.getId(); task.playerTarget = victim instanceof Player;
        if (task.playerTarget) task.originalPlayer = new WeakReference<>(victim);
        task.soundOrigin = victim.position(); task.setPos(owner.position()); task.look = owner.getLookAngle(); task.dimension = server.dimension().identifier().toString();
        task.damage = finite(owner.getAttributeValue(Attributes.ATTACK_DAMAGE) * GraverConfig.STRIKE_MULTIPLIER.get());
        task.knockback = GraverConfig.STRIKE_KNOCKBACK.get(); task.armed = true;
        return server.addFreshEntity(task);
    }
    private RazoraptorEntity owner(ServerLevel server) { return caster != null && server.getEntity(caster) instanceof RazoraptorEntity owner ? owner : null; }
    private void snapshot(RazoraptorEntity owner) { if (owner != null) { setPos(owner.position()); look = owner.getLookAngle(); } }
    @Override protected void defineSynchedData(SynchedEntityData.Builder builder) { }
    @Override public boolean canChangeDimensions(Level from, Level to) { return false; }
    @Override public void onAddedToLevel() { super.onAddedToLevel(); if (armed && level() instanceof ServerLevel server) ServerEndTickTasks.schedule(server, getUUID()); }
    @Override public void tick() {
        super.tick();
        if (level() instanceof ServerLevel server) {
            if (!armed || !dimension.equals(server.dimension().identifier().toString())) { discard(); return; }
            snapshot(owner(server));
            ServerEndTickTasks.schedule(server, getUUID());
        }
    }
    @Override public void endTick(ServerLevel server) {
        if (!armed || isRemoved()) return;
        var owner = owner(server);
        if (owner == null) for (var other : server.getServer().getAllLevels()) if (other != server && caster != null && other.getEntity(caster) != null) { armed = false; discard(); return; }
        snapshot(owner);
        if (++elapsed < 10) { ServerEndTickTasks.schedule(server, getUUID()); return; }
        armed = false;
        server.playSound(null, BlockPos.containing(soundOrigin), SoundEvents.PLAYER_ATTACK_SWEEP, SoundSource.NEUTRAL, 1, 4);
        if (target != null && !target.equals(caster) && server.getEntity(target) instanceof LivingEntity victim && victim.isAlive() && victim.distanceToSqr(position()) <= 9 && (!playerTarget || victim.getId() == targetEntityId && originalPlayer != null && originalPlayer.get() == victim)) {
            double amount = finite(damage - victim.getArmorValue() * .4);
            if (amount > 0) victim.hurt(new DamageSource(server.registryAccess().lookupOrThrow(Registries.DAMAGE_TYPE).getOrThrow(DAMAGE_TYPE)), (float) amount);
            double resistance = Mth.clamp(victim.getAttributeValue(Attributes.KNOCKBACK_RESISTANCE), 0, 1);
            double strength = Double.isFinite(resistance) ? knockback * (1 - resistance) : 0;
            if (strength > 0) { victim.setDeltaMovement(look.scale(strength)); victim.hurtMarked = true; victim.hurtMarked = true; }
        }
        discard();
    }
    private static double finite(double value) { return Double.isFinite(value) ? Math.clamp(value, 0, Float.MAX_VALUE) : 0; }
    private static boolean valid(Vec3 value) { return Double.isFinite(value.x) && Double.isFinite(value.y) && Double.isFinite(value.z); }
    @Override protected void addAdditionalSaveData(net.minecraft.world.level.storage.ValueOutput output) {
        if (caster != null) output.putString("RazoraptorCaster", caster.toString()); if (target != null) output.putString("RazoraptorTarget", target.toString());
        output.putInt("RazoraptorTargetEntityId", targetEntityId); output.putBoolean("RazoraptorPlayerTarget", playerTarget); output.putString("RazoraptorDimension", dimension);
        output.putDouble("RazoraptorSoundX", soundOrigin.x); output.putDouble("RazoraptorSoundY", soundOrigin.y); output.putDouble("RazoraptorSoundZ", soundOrigin.z);
        output.putDouble("RazoraptorLookX", look.x); output.putDouble("RazoraptorLookY", look.y); output.putDouble("RazoraptorLookZ", look.z);
        output.putDouble("RazoraptorDamage", damage); output.putDouble("RazoraptorKnockback", knockback); output.putInt("RazoraptorElapsed", elapsed); output.putBoolean("RazoraptorArmed", armed);
    }
    @Override protected void readAdditionalSaveData(net.minecraft.world.level.storage.ValueInput input) {
        caster = input.getString("RazoraptorCaster").map(java.util.UUID::fromString).orElse(null); target = input.getString("RazoraptorTarget").map(java.util.UUID::fromString).orElse(null);
        targetEntityId = input.getIntOr("RazoraptorTargetEntityId", 0); playerTarget = !input.getString("RazoraptorPlayerTarget").isPresent() || input.getBooleanOr("RazoraptorPlayerTarget", false); originalPlayer = null; dimension = input.getStringOr("RazoraptorDimension", "");
        soundOrigin = new Vec3(input.getDoubleOr("RazoraptorSoundX", 0.0), input.getDoubleOr("RazoraptorSoundY", 0.0), input.getDoubleOr("RazoraptorSoundZ", 0.0)); look = new Vec3(input.getDoubleOr("RazoraptorLookX", 0.0), input.getDoubleOr("RazoraptorLookY", 0.0), input.getDoubleOr("RazoraptorLookZ", 0.0));
        damage = finite(input.getDoubleOr("RazoraptorDamage", 0.0)); knockback = Math.clamp(finite(input.getDoubleOr("RazoraptorKnockback", 0.0)), 0, 2); elapsed = Math.clamp(input.getIntOr("RazoraptorElapsed", 0), 0, 10);
        armed = input.getBooleanOr("RazoraptorArmed", false) && elapsed < 10 && caster != null && target != null && !caster.equals(target) && valid(soundOrigin) && valid(look) && look.lengthSqr() <= 1.01;
    }
}
