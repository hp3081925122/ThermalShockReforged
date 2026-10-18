package com.hp.thermalshock.entity.mangler;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public final class HeraldDeathEffectEntity extends Entity {
    private int remaining;
    private int delay;
    private float eyeHeight;
    private long startedAt;
    public HeraldDeathEffectEntity(EntityType<? extends HeraldDeathEffectEntity> type, Level level) { super(type, level); noPhysics = true; setNoGravity(true); }
    void arm(Vec3 origin, float height) { setPos(origin); eyeHeight = height; remaining = 15; delay = 4; startedAt = level().getGameTime(); emit(); }
    private void emit() { if (!(level() instanceof ServerLevel server) || remaining <= 0) return; server.sendParticles(ParticleTypes.EXPLOSION, getX(), getY() + eyeHeight, getZ(), 1, 2, 3, 2, 0); server.playSound(null, BlockPos.containing(position()), HeraldEntity.sound("mob_death_explode"), SoundSource.HOSTILE, 2, 2); remaining--; }
    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) { }
    @Override
    public void tick() { super.tick(); if (level().isClientSide) return; if (remaining <= 0) { discard(); return; } if (level().getGameTime() == startedAt) return; if (--delay <= 0) { emit(); delay = 4; if (remaining <= 0) discard(); } }
    @Override
    protected void addAdditionalSaveData(CompoundTag tag) { tag.putInt("Remaining", remaining); tag.putInt("Delay", delay); tag.putFloat("VisualEyeHeight", eyeHeight); }
    @Override
    protected void readAdditionalSaveData(CompoundTag tag) { remaining = Math.max(0, Math.min(15, tag.getInt("Remaining"))); delay = Math.max(1, Math.min(4, tag.getInt("Delay"))); eyeHeight = tag.getFloat("VisualEyeHeight"); startedAt = level().getGameTime(); }
}
