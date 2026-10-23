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
import net.minecraft.resources.Identifier;
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
        task.dimension = level.dimension().identifier().toString(); task.capture(owner);
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
            if (caster == null || fired >= 8 || !dimension.equals(server.dimension().identifier().toString())) { discard(); return; }
            sampleTarget(server);
            if (server.getEntity(caster) instanceof ProtoiDeltaEntity owner) capture(owner);
            ServerEndTickTasks.schedule(server, getUUID());
        }
    }
    @Override public void endTick(ServerLevel level) {
        if (isRemoved() || caster == null || fired >= 8 || !dimension.equals(level.dimension().identifier().toString())) return;
        if (--remaining > 0) { ServerEndTickTasks.schedule(level, getUUID()); return; }
        sampleTarget(level);
        ProtoiDeltaEntity owner = level.getEntity(caster) instanceof ProtoiDeltaEntity delta ? delta : null;
        Vec3 look = faceTarget(owner);
        fired++; remaining = 3;
        level.playSound(null, blockPosition(), BuiltInRegistries.SOUND_EVENT.getValue(Identifier.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, "laser_fires")), SoundSource.HOSTILE, 1, 1);
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
    @Override protected void addAdditionalSaveData(net.minecraft.world.level.storage.ValueOutput output) {
        if (caster != null) output.putString("Caster", caster.toString());
        if (target != null) output.putString("Target", target.toString());
        output.putDouble("EyesX", targetEyes.x); output.putDouble("EyesY", targetEyes.y); output.putDouble("EyesZ", targetEyes.z);
        output.putDouble("MuzzleX", muzzle.x); output.putDouble("MuzzleY", muzzle.y); output.putDouble("MuzzleZ", muzzle.z);
        output.putString("Dimension", dimension); output.putDouble("Damage", damage); output.putInt("Remaining", remaining); output.putInt("Fired", fired);
    }
    @Override protected void readAdditionalSaveData(net.minecraft.world.level.storage.ValueInput input) {
        caster = input.getString("Caster").map(java.util.UUID::fromString).orElse(null);
        target = input.getString("Target").map(java.util.UUID::fromString).orElse(null);
        targetEyes = new Vec3(input.getDoubleOr("EyesX", 0.0), input.getDoubleOr("EyesY", 0.0), input.getDoubleOr("EyesZ", 0.0));
        muzzle = new Vec3(input.getDoubleOr("MuzzleX", 0.0), input.getDoubleOr("MuzzleY", 0.0), input.getDoubleOr("MuzzleZ", 0.0));
        dimension = input.getStringOr("Dimension", "");
        double value = input.getDoubleOr("Damage", 0.0); damage = Double.isFinite(value) ? Math.clamp(value, 0, Float.MAX_VALUE) : 0;
        remaining = Math.clamp(input.getIntOr("Remaining", 0), 1, 20); fired = Math.clamp(input.getIntOr("Fired", 0), 0, 8);
        if (!finite(targetEyes) || !finite(muzzle)) fired = 8;
    }
    private static boolean finite(Vec3 value) { return Double.isFinite(value.x) && Double.isFinite(value.y) && Double.isFinite(value.z); }
}
