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
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public final class GunnerVolleyEntity extends Entity implements ServerEndTickTask {
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
        var task = SanctumContent.GUNNER_VOLLEY.get().create(level);
        if (task == null) return false;
        task.caster = owner.getUUID(); task.target = victim.getUUID(); task.targetEyes = victim.getEyePosition();
        task.soundOrigin = victim.blockPosition(); task.dimension = level.dimension().location().toString();
        task.capture(owner); task.setPos(owner.position());
        return level.addFreshEntity(task);
    }
    private void capture(FrostilyteGunnerEntity owner) {
        muzzle = owner.getEyePosition(); look = owner.getLookAngle();
        double value = owner.getAttributeValue(Attributes.ATTACK_DAMAGE);
        damage = Double.isFinite(value) ? Math.clamp(value, 0, Float.MAX_VALUE) : 0;
    }
    @Override protected void defineSynchedData(SynchedEntityData.Builder builder) { }
    @Override public boolean canChangeDimensions(Level from, Level to) { return false; }
    @Override public void onAddedToLevel() { super.onAddedToLevel(); if (caster != null && fired < 3 && level() instanceof ServerLevel server) ServerEndTickTasks.schedule(server, getUUID()); }
    @Override public void tick() {
        super.tick();
        if (level() instanceof ServerLevel server) {
            if (caster == null || fired >= 3 || !dimension.equals(server.dimension().location().toString())) { discard(); return; }
            Entity victim = target == null ? null : server.getEntity(target);
            if (victim != null) targetEyes = victim.getEyePosition();
            if (server.getEntity(caster) instanceof FrostilyteGunnerEntity owner) { capture(owner); setPos(owner.position()); }
            ServerEndTickTasks.schedule(server, getUUID());
        }
    }
    @Override public void endTick(ServerLevel level) {
        if (isRemoved() || caster == null || fired >= 3 || !dimension.equals(level.dimension().location().toString())) return;
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
        level.playSound(null, soundOrigin, BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, "hailfire")), SoundSource.HOSTILE, 1, 1);
        var bullet = IceContent.ICE_BULLET.get().create(level);
        if (bullet != null) {
            bullet.configureUnowned(caster, damage);
            bullet.setPos(muzzle);
            bullet.shoot(look.x, look.y, look.z, 1.25F, 0);
            level.addFreshEntity(bullet);
        }
        if (fired >= 3) discard(); else ServerEndTickTasks.schedule(level, getUUID());
    }
    @Override protected void addAdditionalSaveData(CompoundTag tag) {
        if (caster != null) tag.putUUID("GunnerCaster", caster);
        if (target != null) tag.putUUID("GunnerTarget", target);
        tag.putDouble("GunnerTargetX", targetEyes.x); tag.putDouble("GunnerTargetY", targetEyes.y); tag.putDouble("GunnerTargetZ", targetEyes.z);
        tag.putDouble("GunnerMuzzleX", muzzle.x); tag.putDouble("GunnerMuzzleY", muzzle.y); tag.putDouble("GunnerMuzzleZ", muzzle.z);
        tag.putDouble("GunnerLookX", look.x); tag.putDouble("GunnerLookY", look.y); tag.putDouble("GunnerLookZ", look.z);
        tag.putLong("GunnerSoundOrigin", soundOrigin.asLong()); tag.putString("GunnerDimension", dimension);
        tag.putDouble("GunnerDamage", damage); tag.putInt("GunnerRemaining", remaining); tag.putInt("GunnerFired", fired);
    }
    @Override protected void readAdditionalSaveData(CompoundTag tag) {
        caster = tag.hasUUID("GunnerCaster") ? tag.getUUID("GunnerCaster") : null;
        target = tag.hasUUID("GunnerTarget") ? tag.getUUID("GunnerTarget") : null;
        targetEyes = new Vec3(tag.getDouble("GunnerTargetX"), tag.getDouble("GunnerTargetY"), tag.getDouble("GunnerTargetZ"));
        muzzle = new Vec3(tag.getDouble("GunnerMuzzleX"), tag.getDouble("GunnerMuzzleY"), tag.getDouble("GunnerMuzzleZ"));
        look = new Vec3(tag.getDouble("GunnerLookX"), tag.getDouble("GunnerLookY"), tag.getDouble("GunnerLookZ"));
        soundOrigin = BlockPos.of(tag.getLong("GunnerSoundOrigin")); dimension = tag.getString("GunnerDimension");
        double value = tag.getDouble("GunnerDamage"); damage = Double.isFinite(value) ? Math.clamp(value, 0, Float.MAX_VALUE) : 0;
        remaining = Math.clamp(tag.getInt("GunnerRemaining"), 1, 11); fired = Math.clamp(tag.getInt("GunnerFired"), 0, 3);
        if (!finite(targetEyes) || !finite(muzzle) || !finite(look)) fired = 3;
    }
    private static boolean finite(Vec3 vector) { return Double.isFinite(vector.x) && Double.isFinite(vector.y) && Double.isFinite(vector.z); }
}
