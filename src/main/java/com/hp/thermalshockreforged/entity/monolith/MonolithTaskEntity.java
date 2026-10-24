package com.hp.thermalshockreforged.entity.monolith;

import com.hp.thermalshockreforged.world.timing.*;
import java.lang.ref.WeakReference;
import java.util.*;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.*;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.*;

public final class MonolithTaskEntity extends Entity implements ServerEndTickTask {
    @Override public boolean hurtServer(net.minecraft.server.level.ServerLevel level, net.minecraft.world.damagesource.DamageSource source, float damage) { return false; }
    private UUID caster;
    private UUID target;
    private int targetEntityId;
    private boolean playerTarget;
    private WeakReference<LivingEntity> originalPlayer;
    private Vec3 origin = Vec3.ZERO;
    private Vec3 casterOrigin = Vec3.ZERO;
    private String dimension = "";
    private double damage;
    private int elapsed;
    private boolean attack;
    private boolean armed;
    public MonolithTaskEntity(EntityType<? extends MonolithTaskEntity> type, Level level) { super(type, level); noPhysics = true; setNoGravity(true); }
    static MonolithTaskEntity submitAttack(ServerLevel server, MonolithEntity owner, LivingEntity victim) {
        var task = MonolithContent.TASK.get().create(server, net.minecraft.world.entity.EntitySpawnReason.MOB_SUMMONED);
        if (task == null) return null;
        task.caster = owner.getUUID(); task.target = victim.getUUID(); task.targetEntityId = victim.getId(); task.playerTarget = victim instanceof Player;
        if (task.playerTarget) task.originalPlayer = new WeakReference<>(victim);
        task.origin = victim.position(); task.captureCaster(owner); task.attack = true; task.dimension = server.dimension().identifier().toString(); task.damage = finite(owner.getAttributeValue(Attributes.ATTACK_DAMAGE) * MonolithConfig.STRIKE_MULTIPLIER.get()); task.armed = true;
        if (server.addFreshEntity(task)) return task;
        task.discard(); return null;
    }
    static boolean submitInitialization(ServerLevel server, Vec3 position) {
        var task = MonolithContent.TASK.get().create(server, net.minecraft.world.entity.EntitySpawnReason.MOB_SUMMONED);
        if (task == null) return false;
        task.origin = position; task.casterOrigin = position; task.setPos(position); task.dimension = server.dimension().identifier().toString(); task.armed = true;
        if (server.addFreshEntity(task)) return true;
        task.discard(); return false;
    }
    void captureCaster(MonolithEntity owner) { casterOrigin = owner.position(); setPos(casterOrigin); }
    private MonolithEntity owner(ServerLevel server) { return caster != null && server.getEntity(caster) instanceof MonolithEntity owner ? owner : null; }
    @Override protected void defineSynchedData(SynchedEntityData.Builder builder) { }
    @Override public boolean canTeleport(Level from, Level to) { return false; }
    @Override public void onAddedToLevel() { super.onAddedToLevel(); if (armed && level() instanceof ServerLevel server) ServerEndTickTasks.schedule(server, getUUID()); }
    @Override public void tick() {
        super.tick();
        if (level() instanceof ServerLevel server) {
            if (!armed || !dimension.equals(server.dimension().identifier().toString())) { discard(); return; }
            var owner = owner(server); if (attack && owner != null) captureCaster(owner);
            ServerEndTickTasks.schedule(server, getUUID());
        }
    }
    @Override public void endTick(ServerLevel server) {
        if (isRemoved() || !armed || !dimension.equals(server.dimension().identifier().toString())) return;
        if (attack) {
            var owner = owner(server);
            if (owner != null) captureCaster(owner);
            else for (var other : server.getServer().getAllLevels()) if (other != server && caster != null && other.getEntity(caster) != null) { armed = false; discard(); return; }
        }
        int current = ++elapsed;
        if (attack && (current == 12 || current == 22)) {
            server.playSound(null, BlockPos.containing(origin), MonolithEntity.sound("monolithhurt"), SoundSource.HOSTILE, 1, 5);
            if (target != null && !target.equals(caster) && server.getEntity(target) instanceof LivingEntity victim && victim.isAlive() && (!playerTarget || victim.getId() == targetEntityId && originalPlayer != null && originalPlayer.get() == victim) && victim.position().distanceTo(casterOrigin) <= 6) {
                float amount = (float)finite(damage - victim.getArmorValue() * .2);
                if (amount > 0) victim.hurt(new DamageSource(server.registryAccess().lookupOrThrow(Registries.DAMAGE_TYPE).getOrThrow(DamageTypes.GENERIC)), amount);
            }
        } else if (!attack && current == 20) {
            var closest = server.getEntitiesOfClass(DormantMonolithEntity.class, AABB.ofSize(origin, 128, 128, 128), entity -> true).stream().min(Comparator.comparingDouble(entity -> entity.distanceToSqr(origin))).orElse(null);
            if (closest != null) closest.hurt(new DamageSource(server.registryAccess().lookupOrThrow(Registries.DAMAGE_TYPE).getOrThrow(DamageTypes.EXPLOSION)), 1);
        }
        if (current >= (attack ? 22 : 20)) { armed = false; discard(); } else ServerEndTickTasks.schedule(server, getUUID());
    }
    private static double finite(double value) { return Double.isFinite(value) ? Math.clamp(value, 0, Float.MAX_VALUE) : 0; }
    private static boolean valid(Vec3 value) { return Double.isFinite(value.x) && Double.isFinite(value.y) && Double.isFinite(value.z); }
    @Override protected void addAdditionalSaveData(net.minecraft.world.level.storage.ValueOutput output) {
        if (caster != null) output.putString("MonolithTaskCaster", caster.toString()); if (target != null) output.putString("MonolithTaskTarget", target.toString()); output.putInt("MonolithTaskTargetEntityId", targetEntityId); output.putBoolean("MonolithTaskPlayerTarget", playerTarget); output.putDouble("MonolithTaskX", origin.x); output.putDouble("MonolithTaskY", origin.y); output.putDouble("MonolithTaskZ", origin.z); output.putDouble("MonolithTaskCasterX", casterOrigin.x); output.putDouble("MonolithTaskCasterY", casterOrigin.y); output.putDouble("MonolithTaskCasterZ", casterOrigin.z); output.putString("MonolithTaskDimension", dimension); output.putDouble("MonolithTaskDamage", damage); output.putInt("MonolithTaskElapsed", elapsed); output.putBoolean("MonolithTaskAttack", attack); output.putBoolean("MonolithTaskArmed", armed);
    }
    @Override protected void readAdditionalSaveData(net.minecraft.world.level.storage.ValueInput input) {
        caster = input.getString("MonolithTaskCaster").map(java.util.UUID::fromString).orElse(null); target = input.getString("MonolithTaskTarget").map(java.util.UUID::fromString).orElse(null); targetEntityId = input.getIntOr("MonolithTaskTargetEntityId", 0); playerTarget = !input.read("MonolithTaskPlayerTarget", com.mojang.serialization.Codec.PASSTHROUGH).isPresent() || input.getBooleanOr("MonolithTaskPlayerTarget", false); originalPlayer = null; origin = new Vec3(input.getDoubleOr("MonolithTaskX", 0.0), input.getDoubleOr("MonolithTaskY", 0.0), input.getDoubleOr("MonolithTaskZ", 0.0)); casterOrigin = new Vec3(input.getDoubleOr("MonolithTaskCasterX", 0.0), input.getDoubleOr("MonolithTaskCasterY", 0.0), input.getDoubleOr("MonolithTaskCasterZ", 0.0)); dimension = input.getStringOr("MonolithTaskDimension", ""); damage = finite(input.getDoubleOr("MonolithTaskDamage", 0.0)); elapsed = Math.clamp(input.getIntOr("MonolithTaskElapsed", 0), 0, 22); attack = input.getBooleanOr("MonolithTaskAttack", false); armed = input.getBooleanOr("MonolithTaskArmed", false) && valid(origin) && valid(casterOrigin) && elapsed < (attack ? 22 : 20) && (!attack || caster != null && target != null && !caster.equals(target)); noPhysics = true; setNoGravity(true);
    }
}
