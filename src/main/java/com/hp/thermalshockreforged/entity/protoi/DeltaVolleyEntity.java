package com.hp.thermalshockreforged.entity.protoi;

import com.hp.thermalshockreforged.ThermalShockReforged;
import com.hp.thermalshockreforged.config.CombatConfig;
import com.hp.thermalshockreforged.entity.ice.IceContent;
import com.hp.thermalshockreforged.world.timing.ServerEndTickTask;
import com.hp.thermalshockreforged.world.timing.ServerEndTickTasks;
import java.util.UUID;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public final class DeltaVolleyEntity extends Entity implements ServerEndTickTask {
    private UUID caster;
    private UUID target;
    private Vec3 targetEyes = Vec3.ZERO;
    private Vec3 muzzle = Vec3.ZERO;
    private String dimension = "";
    private double damage = 4;
    private int remaining = 20;
    private int fired;
    public DeltaVolleyEntity(EntityType<? extends DeltaVolleyEntity> type, Level level) { super(type, level); noPhysics = true; setNoGravity(true); }
    public static UUID submit(ServerLevel level, ProtoiDeltaEntity owner, LivingEntity victim) {
        var task = ProtoiContent.DELTA_VOLLEY.get().create(level);
        if (task == null) return null;
        task.caster = owner.getUUID(); task.target = victim.getUUID(); task.targetEyes = victim.getEyePosition();
        task.dimension = level.dimension().location().toString(); task.capture(owner);
        return level.addFreshEntity(task) ? task.getUUID() : null;
    }
    void capture(ProtoiDeltaEntity owner) {
        if (!owner.getUUID().equals(caster) || owner.level() != level()) return;
        setPos(owner.position()); muzzle = owner.getEyePosition();
        double value = owner.getAttributeValue(Attributes.ATTACK_DAMAGE) * CombatConfig.DELTA_BULLET_MULTIPLIER.get();
        damage = Double.isFinite(value) ? Math.clamp(value, 0, Float.MAX_VALUE) : 0;
    }
    private void sampleTarget(ServerLevel level) {
        Entity victim = target == null ? null : level.getEntity(target);
        if (victim != null) targetEyes = victim.getEyePosition();
    }
    private Vec3 faceTarget(ProtoiDeltaEntity owner) {
        if (owner != null) {
            owner.lookAt(EntityAnchorArgument.Anchor.EYES, targetEyes); capture(owner);
            return owner.getLookAngle();
        }
        Vec3 delta = targetEyes.subtract(muzzle);
        double horizontal = Math.sqrt(delta.x * delta.x + delta.z * delta.z);
        setXRot(Mth.wrapDegrees((float)(-(Mth.atan2(delta.y, horizontal) * 180.0F / (float)Math.PI))));
        setYRot(Mth.wrapDegrees((float)(Mth.atan2(delta.z, delta.x) * 180.0F / (float)Math.PI) - 90.0F));
        return calculateViewVector(getXRot(), getYRot());
    }
    @Override protected void defineSynchedData(SynchedEntityData.Builder builder) { }
    @Override public boolean canChangeDimensions(Level from, Level to) { return false; }
    @Override public void onAddedToLevel() { super.onAddedToLevel(); if (caster != null && fired < 8 && level() instanceof ServerLevel server) ServerEndTickTasks.schedule(server, getUUID()); }
    @Override public void tick() {
        super.tick();
        if (level() instanceof ServerLevel server) {
            if (caster == null || fired >= 8 || !dimension.equals(server.dimension().location().toString())) { discard(); return; }
            sampleTarget(server);
            if (server.getEntity(caster) instanceof ProtoiDeltaEntity owner) capture(owner);
            ServerEndTickTasks.schedule(server, getUUID());
        }
    }
    @Override public void endTick(ServerLevel level) {
        if (isRemoved() || caster == null || fired >= 8 || !dimension.equals(level.dimension().location().toString())) return;
        if (--remaining > 0) { ServerEndTickTasks.schedule(level, getUUID()); return; }
        sampleTarget(level);
        ProtoiDeltaEntity owner = level.getEntity(caster) instanceof ProtoiDeltaEntity delta ? delta : null;
        Vec3 look = faceTarget(owner);
        fired++; remaining = 3;
        level.playSound(null, blockPosition(), BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, "laser_fires")), SoundSource.HOSTILE, 1, 1);
        var bullet = IceContent.ICE_BULLET.get().create(level);
        if (bullet != null) {
            bullet.configureUnowned(caster, damage);
            if (owner != null) bullet.setOwner(owner);
            bullet.setPos(muzzle);
            bullet.shoot(look.x, look.y, look.z, .6F, 0);
            if (!level.addFreshEntity(bullet)) ThermalShockReforged.LOGGER.debug("Protoi Delta bullet spawn was rejected: caster={}, volley={}, shot={}", caster, getUUID(), fired);
        }
        if (fired == 8) {
            if (owner != null) owner.finishVolley(getUUID());
            discard();
        } else ServerEndTickTasks.schedule(level, getUUID());
    }
    @Override protected void addAdditionalSaveData(CompoundTag tag) {
        if (caster != null) tag.putUUID("Caster", caster);
        if (target != null) tag.putUUID("Target", target);
        tag.putDouble("EyesX", targetEyes.x); tag.putDouble("EyesY", targetEyes.y); tag.putDouble("EyesZ", targetEyes.z);
        tag.putDouble("MuzzleX", muzzle.x); tag.putDouble("MuzzleY", muzzle.y); tag.putDouble("MuzzleZ", muzzle.z);
        tag.putString("Dimension", dimension); tag.putDouble("Damage", damage); tag.putInt("Remaining", remaining); tag.putInt("Fired", fired);
    }
    @Override protected void readAdditionalSaveData(CompoundTag tag) {
        caster = tag.hasUUID("Caster") ? tag.getUUID("Caster") : null;
        target = tag.hasUUID("Target") ? tag.getUUID("Target") : null;
        targetEyes = new Vec3(tag.getDouble("EyesX"), tag.getDouble("EyesY"), tag.getDouble("EyesZ"));
        muzzle = new Vec3(tag.getDouble("MuzzleX"), tag.getDouble("MuzzleY"), tag.getDouble("MuzzleZ"));
        dimension = tag.getString("Dimension");
        double value = tag.getDouble("Damage"); damage = Double.isFinite(value) ? Math.clamp(value, 0, Float.MAX_VALUE) : 0;
        remaining = Math.clamp(tag.getInt("Remaining"), 1, 20); fired = Math.clamp(tag.getInt("Fired"), 0, 8);
        if (!finite(targetEyes) || !finite(muzzle)) fired = 8;
    }
    private static boolean finite(Vec3 value) { return Double.isFinite(value.x) && Double.isFinite(value.y) && Double.isFinite(value.z); }
}
