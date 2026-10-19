package com.hp.thermalshockreforged.entity.protoi;

import com.hp.thermalshockreforged.ThermalShockReforged;
import com.hp.thermalshockreforged.content.status.aura.AuraContent;
import com.hp.thermalshockreforged.entity.projectile.ProjectileContent;
import com.hp.thermalshockreforged.world.timing.ServerEndTickTask;
import com.hp.thermalshockreforged.world.timing.ServerEndTickTasks;
import java.util.UUID;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public final class EpsilonAttackEntity extends Entity implements ServerEndTickTask {
    private UUID caster;
    private UUID target;
    private Vec3 casterPosition = Vec3.ZERO;
    private Vec3 targetEyes = Vec3.ZERO;
    private Vec3 targetPosition = Vec3.ZERO;
    private double eyeHeight = .5;
    private BlockPos soundOrigin = BlockPos.ZERO;
    private String dimension = "";
    private int remaining = 20;
    private int phase;
    public EpsilonAttackEntity(EntityType<? extends EpsilonAttackEntity> type, Level level) { super(type, level); noPhysics = true; setNoGravity(true); }
    public static UUID submit(ServerLevel level, ProtoiEpsilonEntity owner, LivingEntity victim) {
        var task = ProtoiContent.EPSILON_ATTACK.get().create(level);
        if (task == null) return null;
        task.caster = owner.getUUID(); task.target = victim.getUUID();
        task.targetEyes = victim.getEyePosition(); task.targetPosition = victim.position();
        task.soundOrigin = victim.blockPosition(); task.dimension = level.dimension().location().toString(); task.capture(owner);
        return level.addFreshEntity(task) ? task.getUUID() : null;
    }
    void capture(ProtoiEpsilonEntity owner) {
        if (!owner.getUUID().equals(caster) || owner.level() != level()) return;
        casterPosition = owner.position(); eyeHeight = owner.getEyeHeight(); setPos(casterPosition);
    }
    private void sampleTarget(ServerLevel level) {
        Entity victim = target == null ? null : level.getEntity(target);
        if (victim != null) { targetEyes = victim.getEyePosition(); targetPosition = victim.position(); }
    }
    private Vec3 faceTarget(ProtoiEpsilonEntity owner) {
        if (owner != null) {
            owner.lookAt(EntityAnchorArgument.Anchor.EYES, targetEyes); capture(owner);
            setYRot(owner.getYRot()); setXRot(owner.getXRot());
            return owner.getLookAngle();
        }
        Vec3 delta = targetEyes.subtract(casterPosition.add(0, eyeHeight, 0));
        double horizontal = Math.sqrt(delta.x * delta.x + delta.z * delta.z);
        setXRot(Mth.wrapDegrees((float)(-(Mth.atan2(delta.y, horizontal) * 180.0F / (float)Math.PI))));
        setYRot(Mth.wrapDegrees((float)(Mth.atan2(delta.z, delta.x) * 180.0F / (float)Math.PI) - 90.0F));
        return calculateViewVector(getXRot(), getYRot());
    }
    @Override protected void defineSynchedData(SynchedEntityData.Builder builder) { }
    @Override public boolean canChangeDimensions(Level from, Level to) { return false; }
    @Override public void onAddedToLevel() { super.onAddedToLevel(); if (caster != null && phase < 2 && level() instanceof ServerLevel server) ServerEndTickTasks.schedule(server, getUUID()); }
    @Override public void tick() {
        super.tick();
        if (level() instanceof ServerLevel server) {
            if (caster == null || phase >= 2 || !dimension.equals(server.dimension().location().toString())) { discard(); return; }
            sampleTarget(server);
            if (server.getEntity(caster) instanceof ProtoiEpsilonEntity owner) capture(owner);
            ServerEndTickTasks.schedule(server, getUUID());
        }
    }
    @Override public void endTick(ServerLevel level) {
        if (isRemoved() || caster == null || phase >= 2 || !dimension.equals(level.dimension().location().toString())) return;
        if (--remaining > 0) { ServerEndTickTasks.schedule(level, getUUID()); return; }
        sampleTarget(level);
        ProtoiEpsilonEntity owner = level.getEntity(caster) instanceof ProtoiEpsilonEntity epsilon ? epsilon : null;
        Vec3 look = faceTarget(owner);
        int action = phase++;
        remaining = 20;
        if (action == 0) {
            level.playSound(null, soundOrigin, SoundEvents.BLAZE_SHOOT, SoundSource.HOSTILE, 1, 1);
            var bomb = ProjectileContent.EPSILON_BOMB.get().spawn(level, BlockPos.containing(casterPosition.x, casterPosition.y + 1, casterPosition.z), MobSpawnType.MOB_SUMMONED);
            if (bomb != null && !bomb.isRemoved()) {
                bomb.setYRot(getYRot()); bomb.setYBodyRot(getYRot()); bomb.setYHeadRot(getYRot()); bomb.setXRot(getXRot());
                bomb.setDeltaMovement(look.x * 2, look.y * 1.5, look.z * 2);
            } else ThermalShockReforged.LOGGER.debug("Protoi Epsilon bomb spawn was rejected: caster={}, task={}", caster, getUUID());
            ServerEndTickTasks.schedule(level, getUUID());
        } else {
            level.playSound(null, soundOrigin, SoundEvents.FIREWORK_ROCKET_BLAST_FAR, SoundSource.HOSTILE, 1, -3);
            if (owner != null) {
                owner.addEffect(new MobEffectInstance(AuraContent.DAMAGING_AURA, 20, 6, false, true));
                float dx = (float)(targetPosition.x - casterPosition.x), dy = (float)(targetPosition.y - casterPosition.y), dz = (float)(targetPosition.z - casterPosition.z);
                boolean distant = Mth.sqrt(dx * dx + dy * dy + dz * dz) > 14;
                owner.setDeltaMovement(look.x * (distant ? 2.2 : 1), look.y * (distant ? 1.2 : .7), look.z * (distant ? 2.2 : 1));
                owner.finishAttack(getUUID());
            }
            discard();
        }
    }
    @Override protected void addAdditionalSaveData(CompoundTag tag) {
        if (caster != null) tag.putUUID("Caster", caster);
        if (target != null) tag.putUUID("Target", target);
        tag.putDouble("CasterX", casterPosition.x); tag.putDouble("CasterY", casterPosition.y); tag.putDouble("CasterZ", casterPosition.z);
        tag.putDouble("TargetX", targetPosition.x); tag.putDouble("TargetY", targetPosition.y); tag.putDouble("TargetZ", targetPosition.z);
        tag.putDouble("EyesX", targetEyes.x); tag.putDouble("EyesY", targetEyes.y); tag.putDouble("EyesZ", targetEyes.z);
        tag.putDouble("EyeHeight", eyeHeight); tag.putLong("SoundOrigin", soundOrigin.asLong()); tag.putString("Dimension", dimension);
        tag.putInt("Remaining", remaining); tag.putInt("Phase", phase);
    }
    @Override protected void readAdditionalSaveData(CompoundTag tag) {
        caster = tag.hasUUID("Caster") ? tag.getUUID("Caster") : null;
        target = tag.hasUUID("Target") ? tag.getUUID("Target") : null;
        casterPosition = new Vec3(tag.getDouble("CasterX"), tag.getDouble("CasterY"), tag.getDouble("CasterZ"));
        targetPosition = new Vec3(tag.getDouble("TargetX"), tag.getDouble("TargetY"), tag.getDouble("TargetZ"));
        targetEyes = new Vec3(tag.getDouble("EyesX"), tag.getDouble("EyesY"), tag.getDouble("EyesZ"));
        eyeHeight = tag.getDouble("EyeHeight"); soundOrigin = BlockPos.of(tag.getLong("SoundOrigin")); dimension = tag.getString("Dimension");
        remaining = Math.clamp(tag.getInt("Remaining"), 1, 20); phase = Math.clamp(tag.getInt("Phase"), 0, 2);
        if (!finite(casterPosition) || !finite(targetPosition) || !finite(targetEyes) || !Double.isFinite(eyeHeight)) phase = 2;
    }
    private static boolean finite(Vec3 value) { return Double.isFinite(value.x) && Double.isFinite(value.y) && Double.isFinite(value.z); }
}
