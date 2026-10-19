package com.hp.thermalshockreforged.content.weapons;

import com.hp.thermalshockreforged.ThermalShockReforged;
import com.hp.thermalshockreforged.entity.projectile.ThermoblasterBulletBase;
import com.hp.thermalshockreforged.world.timing.*;
import java.util.UUID;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public final class ThermoblasterBurstEntity extends Entity implements ServerEndTickTask {
    private UUID owner;
    private int ownerEntityId;
    private long animationInstance;
    private Vec3 lastLook = new Vec3(0, 0, 1);
    private int elapsed;
    private boolean zenith;
    private boolean armed;
    public ThermoblasterBurstEntity(EntityType<? extends ThermoblasterBurstEntity> type, Level level) { super(type, level); noPhysics = true; setNoGravity(true); }
    public static boolean submit(ServerLevel level, LivingEntity wielder, long animationInstance, boolean zenith) {
        var task = WeaponContent.THERMOBLASTER_BURST.get().create(level);
        if (task == null) return false;
        task.setPos(wielder.position()); task.owner = wielder.getUUID(); task.ownerEntityId = wielder.getId(); task.lastLook = wielder.getLookAngle();
        task.animationInstance = animationInstance; task.zenith = zenith; task.armed = true;
        if (!level.addFreshEntity(task)) return false;
        task.fire(level, wielder);
        return true;
    }
    @Override protected void defineSynchedData(SynchedEntityData.Builder builder) { }
    @Override public boolean canChangeDimensions(Level from, Level to) { return false; }
    @Override public void onAddedToLevel() { super.onAddedToLevel(); if (armed && level() instanceof ServerLevel server) ServerEndTickTasks.schedule(server, getUUID()); }
    @Override public void tick() { super.tick(); if (level() instanceof ServerLevel server) { if (armed) ServerEndTickTasks.schedule(server, getUUID()); else discard(); } }
    @Override public void endTick(ServerLevel level) {
        if (!armed || isRemoved()) return;
        int stage = ++elapsed;
        if (stage >= 4) { armed = false; discard(); }
        if (stage == 2 || stage == 4) {
            var wielder = level.getServer().getPlayerList().getPlayer(owner);
            fire(level, wielder != null && wielder.getId() == ownerEntityId && !wielder.isRemoved() ? wielder : null);
        }
        if (armed) ServerEndTickTasks.schedule(level, getUUID());
    }
    private void fire(ServerLevel level, LivingEntity wielder) {
        if (wielder != null) lastLook = wielder.getLookAngle();
        ThermoblasterBulletBase.fireWeaponShot(level, wielder, owner, position().add(0, 1.5, 0), lastLook, false, zenith ? 10 : 8);
        if (wielder != null) (zenith ? WeaponContent.ZENITH_THERMOBLASTER : WeaponContent.COBALT_THERMOBLASTER).get().triggerAnim(wielder, animationInstance, "procedure", "1");
        level.playSound(null, blockPosition(), BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, "thermoblasterburstfire")), SoundSource.NEUTRAL, 1, 1);
    }
    @Override protected void addAdditionalSaveData(CompoundTag tag) {
        if (owner != null) tag.putUUID("ThermoblasterOwner", owner);
        tag.putInt("ThermoblasterOwnerEntityId", ownerEntityId); tag.putLong("ThermoblasterAnimationInstance", animationInstance);
        tag.putInt("ThermoblasterElapsed", elapsed); tag.putBoolean("ThermoblasterZenith", zenith); tag.putBoolean("ThermoblasterArmed", armed);
        tag.putDouble("ThermoblasterLookX", lastLook.x); tag.putDouble("ThermoblasterLookY", lastLook.y); tag.putDouble("ThermoblasterLookZ", lastLook.z);
    }
    @Override protected void readAdditionalSaveData(CompoundTag tag) {
        owner = tag.hasUUID("ThermoblasterOwner") ? tag.getUUID("ThermoblasterOwner") : null;
        ownerEntityId = tag.getInt("ThermoblasterOwnerEntityId"); animationInstance = tag.getLong("ThermoblasterAnimationInstance");
        elapsed = Math.clamp(tag.getInt("ThermoblasterElapsed"), 0, 4); zenith = tag.getBoolean("ThermoblasterZenith"); armed = owner != null && elapsed < 4 && tag.getBoolean("ThermoblasterArmed");
        var look = new Vec3(tag.getDouble("ThermoblasterLookX"), tag.getDouble("ThermoblasterLookY"), tag.getDouble("ThermoblasterLookZ"));
        lastLook = Double.isFinite(look.x) && Double.isFinite(look.y) && Double.isFinite(look.z) && look.lengthSqr() > 1.0E-8 ? look.normalize() : new Vec3(0, 0, 1);
    }
}
