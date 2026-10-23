package com.hp.thermalshockreforged.entity.sanctum;

import com.hp.thermalshockreforged.ThermalShockReforged;
import com.hp.thermalshockreforged.entity.ice.IceContent;
import com.hp.thermalshockreforged.world.timing.ServerEndTickTask;
import com.hp.thermalshockreforged.world.timing.ServerEndTickTasks;
import java.util.UUID;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public final class GunnerVolleyEntity extends Entity implements ServerEndTickTask {
    @Override public boolean hurtServer(net.minecraft.server.level.ServerLevel level, net.minecraft.world.damagesource.DamageSource source, float damage) { return false; }
    private UUID caster;
    private UUID target;
    private Vec3 targetEyes = Vec3.ZERO;
    private Vec3 muzzle = Vec3.ZERO;
    private Vec3 look = new Vec3(0, 0, 1);
    private BlockPos soundOrigin = BlockPos.ZERO;
    private String dimension = "";
    private double damage = 5;
    private int remaining = 11;
    private int fired;
    public GunnerVolleyEntity(EntityType<? extends GunnerVolleyEntity> type, Level level) { super(type, level); noPhysics = true; setNoGravity(true); }
    public static boolean submit(ServerLevel level, FrostilyteGunnerEntity owner, LivingEntity victim) {
        var task = SanctumContent.GUNNER_VOLLEY.get().create(level, net.minecraft.world.entity.EntitySpawnReason.TRIGGERED);
        if (task == null) return false;
        task.caster = owner.getUUID(); task.target = victim.getUUID(); task.targetEyes = victim.getEyePosition();
        task.soundOrigin = victim.blockPosition(); task.dimension = level.dimension().identifier().toString();
        task.capture(owner); task.setPos(owner.position());
        return level.addFreshEntity(task);
    }
    private void capture(FrostilyteGunnerEntity owner) {
        muzzle = owner.getEyePosition(); look = owner.getLookAngle();
        double value = owner.getAttributeValue(Attributes.ATTACK_DAMAGE);
        damage = Double.isFinite(value) ? Math.clamp(value, 0, Float.MAX_VALUE) : 0;
    }
    @Override protected void defineSynchedData(SynchedEntityData.Builder builder) { }
    @Override public boolean canTeleport(Level from, Level to) { return false; }
    @Override public void onAddedToLevel() { super.onAddedToLevel(); if (caster != null && fired < 3 && level() instanceof ServerLevel server) ServerEndTickTasks.schedule(server, getUUID()); }
    @Override public void tick() {
        super.tick();
        if (level() instanceof ServerLevel server) {
            if (caster == null || fired >= 3 || !dimension.equals(server.dimension().identifier().toString())) { discard(); return; }
            Entity victim = target == null ? null : server.getEntity(target);
            if (victim != null) targetEyes = victim.getEyePosition();
            if (server.getEntity(caster) instanceof FrostilyteGunnerEntity owner) { capture(owner); setPos(owner.position()); }
            ServerEndTickTasks.schedule(server, getUUID());
        }
    }
    @Override public void endTick(ServerLevel level) {
        if (isRemoved() || caster == null || fired >= 3 || !dimension.equals(level.dimension().identifier().toString())) return;
        if (--remaining > 0) { ServerEndTickTasks.schedule(level, getUUID()); return; }
        if (level.getEntity(caster) == null) {
            for (ServerLevel other : level.getServer().getAllLevels()) {
                if (other != level && other.getEntity(caster) != null) { fired = 3; discard(); return; }
            }
        }
        fired++;
        remaining = 5;
        Entity victim = target == null ? null : level.getEntity(target);
        if (victim != null) targetEyes = victim.getEyePosition();
        if (level.getEntity(caster) instanceof FrostilyteGunnerEntity owner) {
            owner.lookAt(EntityAnchorArgument.Anchor.EYES, targetEyes); capture(owner);
        } else {
            Vec3 direction = targetEyes.subtract(muzzle);
            if (direction.lengthSqr() > 1.0E-12) look = direction.normalize();
        }
        level.playSound(null, soundOrigin, BuiltInRegistries.SOUND_EVENT.getValue(Identifier.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, "hailfire")), SoundSource.HOSTILE, 1, 1);
        var bullet = IceContent.ICE_BULLET.get().create(level, net.minecraft.world.entity.EntitySpawnReason.TRIGGERED);
        if (bullet != null) {
            bullet.configureUnowned(caster, damage);
            bullet.setPos(muzzle);
            bullet.shoot(look.x, look.y, look.z, 1.25F, 0);
            level.addFreshEntity(bullet);
        }
        if (fired >= 3) discard(); else ServerEndTickTasks.schedule(level, getUUID());
    }
    @Override protected void addAdditionalSaveData(net.minecraft.world.level.storage.ValueOutput output) {
        if (caster != null) output.putString("GunnerCaster", caster.toString());
        if (target != null) output.putString("GunnerTarget", target.toString());
        output.putDouble("GunnerTargetX", targetEyes.x); output.putDouble("GunnerTargetY", targetEyes.y); output.putDouble("GunnerTargetZ", targetEyes.z);
        output.putDouble("GunnerMuzzleX", muzzle.x); output.putDouble("GunnerMuzzleY", muzzle.y); output.putDouble("GunnerMuzzleZ", muzzle.z);
        output.putDouble("GunnerLookX", look.x); output.putDouble("GunnerLookY", look.y); output.putDouble("GunnerLookZ", look.z);
        output.putLong("GunnerSoundOrigin", soundOrigin.asLong()); output.putString("GunnerDimension", dimension);
        output.putDouble("GunnerDamage", damage); output.putInt("GunnerRemaining", remaining); output.putInt("GunnerFired", fired);
    }
    @Override protected void readAdditionalSaveData(net.minecraft.world.level.storage.ValueInput input) {
        caster = input.getString("GunnerCaster").map(java.util.UUID::fromString).orElse(null);
        target = input.getString("GunnerTarget").map(java.util.UUID::fromString).orElse(null);
        targetEyes = new Vec3(input.getDoubleOr("GunnerTargetX", 0.0), input.getDoubleOr("GunnerTargetY", 0.0), input.getDoubleOr("GunnerTargetZ", 0.0));
        muzzle = new Vec3(input.getDoubleOr("GunnerMuzzleX", 0.0), input.getDoubleOr("GunnerMuzzleY", 0.0), input.getDoubleOr("GunnerMuzzleZ", 0.0));
        look = new Vec3(input.getDoubleOr("GunnerLookX", 0.0), input.getDoubleOr("GunnerLookY", 0.0), input.getDoubleOr("GunnerLookZ", 0.0));
        soundOrigin = BlockPos.of(input.getLongOr("GunnerSoundOrigin", 0L)); dimension = input.getStringOr("GunnerDimension", "");
        double value = input.getDoubleOr("GunnerDamage", 0.0); damage = Double.isFinite(value) ? Math.clamp(value, 0, Float.MAX_VALUE) : 0;
        remaining = Math.clamp(input.getIntOr("GunnerRemaining", 0), 1, 11); fired = Math.clamp(input.getIntOr("GunnerFired", 0), 0, 3);
        if (!finite(targetEyes) || !finite(muzzle) || !finite(look)) fired = 3;
    }
    private static boolean finite(Vec3 vector) { return Double.isFinite(vector.x) && Double.isFinite(vector.y) && Double.isFinite(vector.z); }
}
