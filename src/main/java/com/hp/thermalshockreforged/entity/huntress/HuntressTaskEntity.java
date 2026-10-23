package com.hp.thermalshockreforged.entity.huntress;

import com.hp.thermalshockreforged.content.npc.huntress.HuntressEntity;
import com.hp.thermalshockreforged.world.timing.*;
import java.lang.ref.WeakReference;
import java.util.*;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.*;
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
import net.minecraft.world.phys.*;

public final class HuntressTaskEntity extends Entity implements ServerEndTickTask {
    private static final ResourceKey<DamageType> DAMAGE_TYPE = ResourceKey.create(Registries.DAMAGE_TYPE, HuntressContent.id("errant_damage"));
    private UUID caster;
    private UUID target;
    private int targetEntityId;
    private boolean playerTarget;
    private WeakReference<LivingEntity> originalPlayer;
    private Vec3 soundOrigin = Vec3.ZERO;
    private Vec3 casterOrigin = Vec3.ZERO;
    private Vec3 targetOrigin = Vec3.ZERO;
    private Vec3 look = Vec3.ZERO;
    private String dimension = "";
    private double eyeHeight;
    private double damage;
    private double knockback;
    private int skill;
    private int elapsed;
    private boolean armed;
    public HuntressTaskEntity(EntityType<? extends HuntressTaskEntity> type, Level level) { super(type, level); noPhysics = true; setNoGravity(true); }
    public static boolean submitInitialization(ServerLevel server, HuntressEntity npc, Vec3 origin) {
        var task = HuntressContent.TASK.get().create(server, net.minecraft.world.entity.EntitySpawnReason.MOB_SUMMONED);
        if (task == null) return false;
        task.caster = npc.getUUID(); task.soundOrigin = origin; task.casterOrigin = origin; task.dimension = server.dimension().identifier().toString(); task.armed = true; task.setPos(origin);
        return server.addFreshEntity(task);
    }
    static HuntressTaskEntity submit(ServerLevel server, HuntressBossEntity owner, LivingEntity victim, int skill) {
        var task = HuntressContent.TASK.get().create(server, net.minecraft.world.entity.EntitySpawnReason.MOB_SUMMONED);
        if (task == null) return null;
        task.caster = owner.getUUID(); task.target = victim.getUUID(); task.targetEntityId = victim.getId(); task.playerTarget = victim instanceof Player;
        if (task.playerTarget) task.originalPlayer = new WeakReference<>(victim);
        task.soundOrigin = victim.position(); task.casterOrigin = owner.position(); task.targetOrigin = victim.position(); task.look = owner.getLookAngle(); task.eyeHeight = owner.getEyeHeight(); task.skill = skill; task.dimension = server.dimension().identifier().toString();
        task.damage = finite(owner.getAttributeValue(Attributes.ATTACK_DAMAGE) * (skill == 1 ? HuntressConfig.TRIPLE_MULTIPLIER.get() : HuntressConfig.SLAM_MULTIPLIER.get())); task.knockback = finite(HuntressConfig.KNOCKBACK.get()); task.armed = true; task.setPos(owner.position());
        return server.addFreshEntity(task) ? task : null;
    }
    private HuntressBossEntity owner(ServerLevel server) { return caster != null && server.getEntity(caster) instanceof HuntressBossEntity owner ? owner : null; }
    private LivingEntity victim(ServerLevel server) { return target != null && server.getEntity(target) instanceof LivingEntity living && (!playerTarget || living.getId() == targetEntityId && originalPlayer != null && originalPlayer.get() == living) ? living : null; }
    void captureCaster(HuntressBossEntity owner) { casterOrigin = owner.position(); look = owner.getLookAngle(); eyeHeight = owner.getEyeHeight(); setPos(casterOrigin); }
    private void snapshot(ServerLevel server, HuntressBossEntity owner) { if (owner != null) captureCaster(owner); var victim = victim(server); if (victim != null) targetOrigin = victim.position(); setPos(casterOrigin); }
    @Override protected void defineSynchedData(SynchedEntityData.Builder builder) { }
    @Override public boolean canChangeDimensions(Level from, Level to) { return false; }
    @Override public void onAddedToLevel() { super.onAddedToLevel(); if (armed && level() instanceof ServerLevel server) ServerEndTickTasks.schedule(server, getUUID()); }
    @Override public void tick() {
        super.tick();
        if (level() instanceof ServerLevel server) {
            if (!armed || !dimension.equals(server.dimension().identifier().toString())) { discard(); return; }
            if (skill != 0) snapshot(server, owner(server));
            ServerEndTickTasks.schedule(server, getUUID());
        }
    }
    @Override public void endTick(ServerLevel server) {
        if (isRemoved() || !armed || !dimension.equals(server.dimension().identifier().toString())) return;
        var owner = owner(server);
        if (skill != 0) {
            if (owner == null) for (var other : server.getServer().getAllLevels()) if (other != server && caster != null && other.getEntity(caster) instanceof HuntressBossEntity moved) { moved.releaseGravity(); armed = false; discard(); return; }
            snapshot(server, owner);
        }
        int current = ++elapsed;
        if (skill == 0) {
            if (current == 2) { sound(server, SoundEvents.BASALT_FALL, SoundSource.NEUTRAL, 1); sound(server, SoundEvents.GENERIC_BIG_FALL, SoundSource.NEUTRAL, 1); }
            if (current == 8) { sound(server, SoundEvents.BEE_HURT, SoundSource.NEUTRAL, -1); sound(server, SoundEvents.BOOK_PAGE_TURN, SoundSource.NEUTRAL, 1); }
        } else if (skill == 1) {
            if (current == 10 || current == 17) { faceAndDash(owner); sound(server, SoundEvents.PLAYER_ATTACK_SWEEP, SoundSource.HOSTILE, 2); }
            if (current == 12) strike(server, false);
            if (current == 19) strike(server, true);
        } else if (skill == 2) {
            if (current == 24) {
                sound(server, SoundEvents.BEE_HURT, SoundSource.HOSTILE, 1);
                if (owner != null) owner.triggerProcedure("reappear2");
                double x = targetOrigin.x, z = targetOrigin.z;
                if (random.nextDouble() < .5) { x += random.nextInt(9) - 4; z += random.nextInt(9) - 4; }
                teleportOwner(owner, new Vec3(x, targetOrigin.y + 7, z));
            }
            if (current == 34) {
                if (owner != null) owner.releaseGravity();
                for (int i = 0; i < 3; i++) sound(server, SoundEvents.SHIELD_BREAK, SoundSource.HOSTILE, 3);
                teleportOwner(owner, targetOrigin);
            }
            if (current == 44) {
                sound(server, SoundEvents.GENERIC_EXPLODE.value(), SoundSource.HOSTILE, 1);
                server.sendParticles(ParticleTypes.EXPLOSION_EMITTER, soundOrigin.x, soundOrigin.y, soundOrigin.z, 1, .001, .001, .001, 0);
                strike(server, false);
            }
        }
        int end = skill == 0 ? 8 : skill == 1 ? 19 : 44;
        if (current >= end) { armed = false; discard(); } else ServerEndTickTasks.schedule(server, getUUID());
    }
    private void faceAndDash(HuntressBossEntity owner) {
        if (owner != null) { owner.lookAt(EntityAnchorArgument.Anchor.EYES, targetOrigin); owner.yBodyRot = owner.getYRot(); owner.yHeadRot = owner.getYRot(); look = owner.getLookAngle(); owner.setDeltaMovement(look); owner.hasImpulse = true; }
        else { var delta = targetOrigin.subtract(casterOrigin.add(0, eyeHeight, 0)); if (delta.lengthSqr() > 0) look = delta.normalize(); }
    }
    private void teleportOwner(HuntressBossEntity owner, Vec3 position) { casterOrigin = position; if (owner != null) { owner.teleportTo(position.x, position.y, position.z); casterOrigin = owner.position(); } setPos(casterOrigin); }
    private void strike(ServerLevel server, boolean second) {
        var entities = server.getEntitiesOfClass(Entity.class, new AABB(casterOrigin, casterOrigin).inflate(2.5), entity -> entity != this).stream().sorted(Comparator.comparingDouble(entity -> entity.distanceToSqr(casterOrigin))).toList();
        var source = new DamageSource(server.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(DAMAGE_TYPE));
        for (var entity : entities) {
            if (entity.getUUID().equals(caster) || entity instanceof LivingEntity living && living.isBlocking()) continue;
            double armor = entity instanceof LivingEntity living ? living.getArmorValue() : 0;
            float amount = (float) finite(damage - armor * .6);
            if (amount > 0) entity.hurt(source, amount);
            double resistance = entity instanceof LivingEntity living ? Mth.clamp(living.getAttributeValue(Attributes.KNOCKBACK_RESISTANCE), 0, 1) : 0;
            if (!Double.isFinite(resistance)) resistance = 1;
            double strength = knockback * (1 - resistance);
            if (strength > 0) { Vec3 vector = skill == 2 ? new Vec3(0, 1, 0) : second ? new Vec3(look.x, .4, look.z) : look; entity.setDeltaMovement(vector.scale(strength)); entity.hasImpulse = true; entity.hurtMarked = true; }
        }
    }
    private void sound(ServerLevel server, SoundEvent sound, SoundSource source, float pitch) { server.playSound(null, BlockPos.containing(soundOrigin), sound, source, 1, pitch); }
    private static double finite(double value) { return Double.isFinite(value) ? Math.clamp(value, 0, Float.MAX_VALUE) : 0; }
    private static boolean valid(Vec3 value) { return Double.isFinite(value.x) && Double.isFinite(value.y) && Double.isFinite(value.z); }
    private static void putVector(CompoundTag tag, String key, Vec3 value) { output.putDouble(key + "X", value.x); output.putDouble(key + "Y", value.y); output.putDouble(key + "Z", value.z); }
    private static Vec3 getVector(CompoundTag tag, String key) { return new Vec3(tag.getDouble(key + "X"), tag.getDouble(key + "Y"), tag.getDouble(key + "Z")); }
    @Override protected void addAdditionalSaveData(net.minecraft.world.level.storage.ValueOutput output) {
        if (caster != null) output.putString("HuntressCaster", caster.toString()); if (target != null) output.putString("HuntressTarget", target.toString());
        output.putInt("HuntressTargetEntityId", targetEntityId); output.putBoolean("HuntressPlayerTarget", playerTarget); output.putString("HuntressDimension", dimension); output.putInt("HuntressSkill", skill); output.putInt("HuntressElapsed", elapsed); output.putBoolean("HuntressArmed", armed); output.putDouble("HuntressDamage", damage); output.putDouble("HuntressKnockback", knockback); output.putDouble("HuntressEyeHeight", eyeHeight);
        putVector(tag, "HuntressSound", soundOrigin); putVector(tag, "HuntressCasterPos", casterOrigin); putVector(tag, "HuntressTargetPos", targetOrigin); putVector(tag, "HuntressLook", look);
    }
    @Override protected void readAdditionalSaveData(net.minecraft.world.level.storage.ValueInput input) {
        caster = input.getString("HuntressCaster").map(java.util.UUID::fromString).orElse(null); target = input.getString("HuntressTarget").map(java.util.UUID::fromString).orElse(null); targetEntityId = input.getIntOr("HuntressTargetEntityId", 0); playerTarget = !input.getString("HuntressPlayerTarget").isPresent() || input.getBooleanOr("HuntressPlayerTarget", false); originalPlayer = null;
        dimension = input.getStringOr("HuntressDimension", ""); skill = Math.clamp(input.getIntOr("HuntressSkill", 0), 0, 2); elapsed = Math.clamp(input.getIntOr("HuntressElapsed", 0), 0, 44); damage = finite(input.getDoubleOr("HuntressDamage", 0.0)); knockback = Math.clamp(finite(input.getDoubleOr("HuntressKnockback", 0.0)), 0, 2); eyeHeight = Math.clamp(finite(input.getDoubleOr("HuntressEyeHeight", 0.0)), 0, 16);
        soundOrigin = getVector(tag, "HuntressSound"); casterOrigin = getVector(tag, "HuntressCasterPos"); targetOrigin = getVector(tag, "HuntressTargetPos"); look = getVector(tag, "HuntressLook");
        int end = skill == 0 ? 8 : skill == 1 ? 19 : 44;
        armed = input.getBooleanOr("HuntressArmed", false) && caster != null && (skill == 0 || target != null && !target.equals(caster)) && elapsed < end && valid(soundOrigin) && valid(casterOrigin) && valid(targetOrigin) && valid(look) && look.lengthSqr() <= 1.01;
        noPhysics = true; setNoGravity(true);
    }
}
