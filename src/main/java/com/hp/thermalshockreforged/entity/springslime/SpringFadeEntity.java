package com.hp.thermalshockreforged.entity.springslime;

import com.hp.thermalshockreforged.world.timing.*;
import java.util.UUID;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.*;
import net.minecraft.world.entity.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public final class SpringFadeEntity extends Entity implements ServerEndTickTask {
    private UUID spring;
    private Vec3 anchor = Vec3.ZERO;
    private String dimension = "";
    private int remaining = 20;
    private boolean armed;
    private boolean consumed;
    public SpringFadeEntity(EntityType<? extends SpringFadeEntity> type, Level level) { super(type, level); noPhysics = true; setNoGravity(true); }
    public static boolean submit(ServerLevel level, SpringslimeEntity source) {
        var task = SpringslimeContent.FADE.get().create(level);
        if (task == null) return false;
        task.spring = source.getUUID(); task.anchor = source.position(); task.dimension = level.dimension().location().toString(); task.armed = true;
        task.setPos(task.anchor);
        return level.addFreshEntity(task);
    }
    @Override protected void defineSynchedData(SynchedEntityData.Builder builder) { }
    @Override public boolean canChangeDimensions(Level from, Level to) { return false; }
    @Override public void onAddedToLevel() { super.onAddedToLevel(); if (armed && !consumed && level() instanceof ServerLevel server) ServerEndTickTasks.schedule(server, getUUID()); }
    @Override public void tick() {
        super.tick();
        if (level() instanceof ServerLevel server) {
            if (!armed || consumed || !dimension.equals(server.dimension().location().toString())) { discard(); return; }
            setPos(anchor); ServerEndTickTasks.schedule(server, getUUID());
        }
    }
    @Override public void endTick(ServerLevel level) {
        if (isRemoved() || !armed || consumed || !dimension.equals(level.dimension().location().toString())) { discard(); return; }
        if (--remaining > 0) { ServerEndTickTasks.schedule(level, getUUID()); return; }
        consumed = true; discard();
        if (spring != null && level.getEntity(spring) instanceof SpringslimeEntity source) source.discard();
        level.playSound(null, anchor.x, anchor.y, anchor.z, SoundEvents.SLIME_DEATH, SoundSource.NEUTRAL, 1, 1);
        level.sendParticles(ParticleTypes.POOF, anchor.x, anchor.y, anchor.z, 15, 1, 1, 1, .3);
    }
    @Override protected void addAdditionalSaveData(CompoundTag tag) { if (spring != null) tag.putUUID("SpringSource", spring); tag.putDouble("SpringX", anchor.x); tag.putDouble("SpringY", anchor.y); tag.putDouble("SpringZ", anchor.z); tag.putString("SpringDimension", dimension); tag.putInt("SpringRemaining", remaining); tag.putBoolean("SpringArmed", armed); tag.putBoolean("SpringConsumed", consumed); }
    @Override protected void readAdditionalSaveData(CompoundTag tag) {
        spring = tag.hasUUID("SpringSource") ? tag.getUUID("SpringSource") : null;
        anchor = new Vec3(tag.getDouble("SpringX"), tag.getDouble("SpringY"), tag.getDouble("SpringZ")); dimension = tag.getString("SpringDimension");
        remaining = Math.clamp(tag.getInt("SpringRemaining"), 1, 20); armed = tag.getBoolean("SpringArmed") && spring != null && Double.isFinite(anchor.x) && Double.isFinite(anchor.y) && Double.isFinite(anchor.z); consumed = tag.getBoolean("SpringConsumed");
        noPhysics = true; setNoGravity(true);
    }
}
