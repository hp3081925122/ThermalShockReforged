package com.hp.thermalshockreforged.entity.titans;

import com.hp.thermalshockreforged.ThermalShockReforged;
import com.hp.thermalshockreforged.world.timing.ServerEndTickTask;
import com.hp.thermalshockreforged.world.timing.ServerEndTickTasks;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public final class TitanSwingEntity extends Entity implements ServerEndTickTask {
    private static final ResourceKey<DamageType> DAMAGE = ResourceKey.create(Registries.DAMAGE_TYPE, Identifier.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, "errant_damage"));
    private UUID caster;
    private UUID target;
    private int targetEntityId;
    private boolean targetPlayer;
    private java.lang.ref.WeakReference<LivingEntity> originalPlayerTarget;
    private Vec3 targetOrigin = Vec3.ZERO;
    private double damage;
    private int elapsed;
    private boolean mantle;
    private boolean armed;

    public TitanSwingEntity(EntityType<? extends TitanSwingEntity> type, Level level) { super(type, level); noPhysics = true; setNoGravity(true); }
    public static boolean submit(ServerLevel server, SkeletalTitanEntity owner, LivingEntity victim) {
        var swing = TitanContent.SWING.get().create(server, net.minecraft.world.entity.EntitySpawnReason.MOB_SUMMONED);
        if (swing == null) return false;
        swing.caster = owner.getUUID();
        swing.target = victim.getUUID();
        swing.targetEntityId = victim.getId();
        swing.targetPlayer = victim instanceof net.minecraft.world.entity.player.Player;
        if (swing.targetPlayer) swing.originalPlayerTarget = new java.lang.ref.WeakReference<>(victim);
        swing.targetOrigin = victim.position();
        swing.mantle = owner.kind() == SkeletalTitanEntity.Kind.MANTLE;
        swing.damage = finiteDamage(owner.getAttributeValue(Attributes.ATTACK_DAMAGE) * TitanCombatConfig.SWING_MULTIPLIER[owner.kind().ordinal()].get());
        swing.setPos(owner.position());
        swing.armed = true;
        return server.addFreshEntity(swing);
    }
    @Override protected void defineSynchedData(SynchedEntityData.Builder builder) { }
    @Override public boolean canChangeDimensions(Level from, Level to) { return false; }
    @Override public void onAddedToLevel() { super.onAddedToLevel(); if (armed && level() instanceof ServerLevel server) ServerEndTickTasks.schedule(server, getUUID()); }
    private SkeletalTitanEntity caster(ServerLevel server) { return caster != null && server.getEntity(caster) instanceof SkeletalTitanEntity owner ? owner : null; }
    @Override public void tick() {
        super.tick();
        if (level() instanceof ServerLevel server) {
            if (!armed) { discard(); return; }
            var owner = caster(server);
            if (owner != null) setPos(owner.position());
            ServerEndTickTasks.schedule(server, getUUID());
        }
    }
    @Override public void endTick(ServerLevel server) {
        if (!armed || isRemoved()) return;
        if (++elapsed < 20) { ServerEndTickTasks.schedule(server, getUUID()); return; }
        armed = false;
        var owner = caster(server);
        if (owner != null) { setPos(owner.position()); owner.triggerProcedure("bigswing2"); }
        else for (ServerLevel other : server.getServer().getAllLevels()) {
            if (other != server && caster != null && other.getEntity(caster) != null) { discard(); return; }
        }
        server.playSound(null, BlockPos.containing(targetOrigin), SoundEvents.WITHER_BREAK_BLOCK, SoundSource.HOSTILE, 1, 1);
        server.sendParticles(ParticleTypes.CAMPFIRE_COSY_SMOKE, getX(), getY(), getZ(), 30, 3, 0, 3, 0);
        if (target != null && !target.equals(caster) && server.getEntity(target) instanceof LivingEntity victim && (!targetPlayer || victim.getId() == targetEntityId && originalPlayerTarget != null && originalPlayerTarget.get() == victim) && victim.isAlive()
            && victim.position().distanceTo(targetOrigin) <= 1.7 && victim.distanceToSqr(position()) <= 16) {
            if (damage > 0) victim.hurt(new DamageSource(server.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(DAMAGE)), (float) damage);
            if (mantle) {
                victim.igniteForSeconds(5);
                server.sendParticles(ParticleTypes.FLAME, targetOrigin.x, targetOrigin.y, targetOrigin.z, 18, 1, 1, 1, 0);
            }
        }
        discard();
    }
    private static double finiteDamage(double value) { return Double.isFinite(value) ? Math.clamp(value, 0, Float.MAX_VALUE) : 0; }
    @Override protected void addAdditionalSaveData(net.minecraft.world.level.storage.ValueOutput output) {
        if (caster != null) output.putString("TitanSwingCaster", caster.toString());
        if (target != null) output.putString("TitanSwingTarget", target.toString());
        output.putInt("TitanSwingTargetEntityId", targetEntityId);
        output.putBoolean("TitanSwingTargetPlayer", targetPlayer);
        output.putDouble("TitanSwingTargetX", targetOrigin.x);
        output.putDouble("TitanSwingTargetY", targetOrigin.y);
        output.putDouble("TitanSwingTargetZ", targetOrigin.z);
        output.putDouble("TitanSwingDamage", damage);
        output.putInt("TitanSwingElapsed", elapsed);
        output.putBoolean("TitanSwingMantle", mantle);
        output.putBoolean("TitanSwingArmed", armed);
    }
    @Override protected void readAdditionalSaveData(net.minecraft.world.level.storage.ValueInput input) {
        caster = input.getString("TitanSwingCaster").map(java.util.UUID::fromString).orElse(null);
        target = input.getString("TitanSwingTarget").map(java.util.UUID::fromString).orElse(null);
        targetEntityId = input.getIntOr("TitanSwingTargetEntityId", 0);
        targetPlayer = !input.getString("TitanSwingTargetPlayer").isPresent() || input.getBooleanOr("TitanSwingTargetPlayer", false);
        originalPlayerTarget = null;
        targetOrigin = new Vec3(input.getDoubleOr("TitanSwingTargetX", 0.0), input.getDoubleOr("TitanSwingTargetY", 0.0), input.getDoubleOr("TitanSwingTargetZ", 0.0));
        damage = finiteDamage(input.getDoubleOr("TitanSwingDamage", 0.0));
        elapsed = Math.clamp(input.getIntOr("TitanSwingElapsed", 0), 0, 20);
        mantle = input.getBooleanOr("TitanSwingMantle", false);
        armed = input.getBooleanOr("TitanSwingArmed", false) && caster != null && target != null && elapsed < 20 && Double.isFinite(targetOrigin.x) && Double.isFinite(targetOrigin.y) && Double.isFinite(targetOrigin.z);
    }
}
