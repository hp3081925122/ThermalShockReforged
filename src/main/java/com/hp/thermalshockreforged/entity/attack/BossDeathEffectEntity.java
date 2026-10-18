package com.hp.thermalshockreforged.entity.attack;

import com.hp.thermalshockreforged.ThermalShockReforged;
import com.hp.thermalshockreforged.world.timing.*;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public final class BossDeathEffectEntity extends Entity implements ServerEndTickTask {
    private Vec3 origin = Vec3.ZERO;
    private int remaining;
    private int delay = 4;
    private float eyeHeight;
    private String dimension = "";
    public BossDeathEffectEntity(EntityType<? extends BossDeathEffectEntity> type, Level level) { super(type, level); noPhysics = true; setNoGravity(true); }
    public static boolean submit(ServerLevel level, EntityType<BossDeathEffectEntity> type, Vec3 origin, float eyeHeight) {
        var task = type.create(level);
        if (task == null) return false;
        task.origin = origin; task.setPos(origin); task.eyeHeight = eyeHeight; task.remaining = 15; task.dimension = level.dimension().location().toString();
        if (!level.addFreshEntity(task)) return false;
        task.emit(level);
        return true;
    }
    private void emit(ServerLevel level) {
        if (remaining <= 0) return;
        remaining--;
        level.sendParticles(ParticleTypes.EXPLOSION, origin.x, origin.y + eyeHeight, origin.z, 1, 2, 3, 2, 0);
        level.playSound(null, BlockPos.containing(origin), BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, "mob_death_explode")), SoundSource.HOSTILE, 2, 2);
    }
    @Override protected void defineSynchedData(SynchedEntityData.Builder builder) { }
    @Override public boolean canChangeDimensions(Level from, Level to) { return false; }
    @Override public void onAddedToLevel() { super.onAddedToLevel(); if (remaining > 0 && level() instanceof ServerLevel server) ServerEndTickTasks.schedule(server, getUUID()); }
    @Override public void tick() {
        super.tick();
        if (level() instanceof ServerLevel server) {
            if (remaining <= 0 || !dimension.equals(server.dimension().location().toString())) { discard(); return; }
            setPos(origin); ServerEndTickTasks.schedule(server, getUUID());
        }
    }
    @Override public void endTick(ServerLevel level) {
        if (isRemoved() || remaining <= 0 || !dimension.equals(level.dimension().location().toString())) return;
        if (--delay > 0) { ServerEndTickTasks.schedule(level, getUUID()); return; }
        delay = 4; emit(level);
        if (remaining <= 0) discard(); else ServerEndTickTasks.schedule(level, getUUID());
    }
    @Override protected void addAdditionalSaveData(CompoundTag tag) {
        tag.putInt("Remaining", remaining); tag.putInt("Delay", delay); tag.putFloat("VisualEyeHeight", eyeHeight);
        tag.putString("BossVisualDimension", dimension); tag.putDouble("BossVisualX", origin.x); tag.putDouble("BossVisualY", origin.y); tag.putDouble("BossVisualZ", origin.z);
    }
    @Override protected void readAdditionalSaveData(CompoundTag tag) {
        remaining = Math.clamp(tag.getInt("Remaining"), 0, 15); delay = Math.clamp(tag.getInt("Delay"), 1, 4);
        eyeHeight = tag.getFloat("VisualEyeHeight"); if (!Float.isFinite(eyeHeight)) eyeHeight = 0;
        dimension = tag.contains("BossVisualDimension") ? tag.getString("BossVisualDimension") : level().dimension().location().toString();
        origin = tag.contains("BossVisualX") ? new Vec3(tag.getDouble("BossVisualX"), tag.getDouble("BossVisualY"), tag.getDouble("BossVisualZ")) : position();
        if (!Double.isFinite(origin.x) || !Double.isFinite(origin.y) || !Double.isFinite(origin.z)) remaining = 0;
    }
}
