package com.hp.thermalshockreforged.entity.attack;

import com.hp.thermalshockreforged.ThermalShockReforged;
import com.hp.thermalshockreforged.world.timing.*;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public final class BossDeathEffectEntity extends Entity implements ServerEndTickTask {
    @Override public boolean hurtServer(net.minecraft.server.level.ServerLevel level, net.minecraft.world.damagesource.DamageSource source, float damage) { return false; }
    private Vec3 origin = Vec3.ZERO;
    private int remaining;
    private int delay = 4;
    private float eyeHeight;
    private String dimension = "";
    public BossDeathEffectEntity(EntityType<? extends BossDeathEffectEntity> type, Level level) { super(type, level); noPhysics = true; setNoGravity(true); }
    public static boolean submit(ServerLevel level, EntityType<BossDeathEffectEntity> type, Vec3 origin, float eyeHeight) {
        var task = type.create(level, net.minecraft.world.entity.EntitySpawnReason.TRIGGERED);
        if (task == null) return false;
        task.origin = origin; task.setPos(origin); task.eyeHeight = eyeHeight; task.remaining = 15; task.dimension = level.dimension().identifier().toString();
        if (!level.addFreshEntity(task)) return false;
        task.emit(level);
        return true;
    }
    private void emit(ServerLevel level) {
        if (remaining <= 0) return;
        remaining--;
        level.sendParticles(ParticleTypes.EXPLOSION, origin.x, origin.y + eyeHeight, origin.z, 1, 2, 3, 2, 0);
        level.playSound(null, BlockPos.containing(origin), BuiltInRegistries.SOUND_EVENT.getValue(Identifier.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, "mob_death_explode")), SoundSource.HOSTILE, 2, 2);
    }
    @Override protected void defineSynchedData(SynchedEntityData.Builder builder) { }
    @Override public boolean canTeleport(Level from, Level to) { return false; }
    @Override public void onAddedToLevel() { super.onAddedToLevel(); if (remaining > 0 && level() instanceof ServerLevel server) ServerEndTickTasks.schedule(server, getUUID()); }
    @Override public void tick() {
        super.tick();
        if (level() instanceof ServerLevel server) {
            if (remaining <= 0 || !dimension.equals(server.dimension().identifier().toString())) { discard(); return; }
            setPos(origin); ServerEndTickTasks.schedule(server, getUUID());
        }
    }
    @Override public void endTick(ServerLevel level) {
        if (isRemoved() || remaining <= 0 || !dimension.equals(level.dimension().identifier().toString())) return;
        if (--delay > 0) { ServerEndTickTasks.schedule(level, getUUID()); return; }
        delay = 4; emit(level);
        if (remaining <= 0) discard(); else ServerEndTickTasks.schedule(level, getUUID());
    }
    @Override protected void addAdditionalSaveData(net.minecraft.world.level.storage.ValueOutput output) {
        output.putInt("Remaining", remaining); output.putInt("Delay", delay); output.putFloat("VisualEyeHeight", eyeHeight);
        output.putString("BossVisualDimension", dimension); output.putDouble("BossVisualX", origin.x); output.putDouble("BossVisualY", origin.y); output.putDouble("BossVisualZ", origin.z);
    }
    @Override protected void readAdditionalSaveData(net.minecraft.world.level.storage.ValueInput input) {
        remaining = Math.clamp(input.getIntOr("Remaining", 0), 0, 15); delay = Math.clamp(input.getIntOr("Delay", 0), 1, 4);
        eyeHeight = input.getFloatOr("VisualEyeHeight", 0.0F); if (!Float.isFinite(eyeHeight)) eyeHeight = 0;
        dimension = input.read("BossVisualDimension", com.mojang.serialization.Codec.PASSTHROUGH).isPresent() ? input.getStringOr("BossVisualDimension", "") : level().dimension().identifier().toString();
        origin = input.read("BossVisualX", com.mojang.serialization.Codec.PASSTHROUGH).isPresent() ? new Vec3(input.getDoubleOr("BossVisualX", 0.0), input.getDoubleOr("BossVisualY", 0.0), input.getDoubleOr("BossVisualZ", 0.0)) : position();
        if (!Double.isFinite(origin.x) || !Double.isFinite(origin.y) || !Double.isFinite(origin.z)) remaining = 0;
    }
}
