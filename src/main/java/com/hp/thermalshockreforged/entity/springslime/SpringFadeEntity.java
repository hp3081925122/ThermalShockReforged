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
        task.spring = source.getUUID(); task.anchor = source.position(); task.dimension = level.dimension().identifier().toString(); task.armed = true;
        task.setPos(task.anchor);
        return level.addFreshEntity(task);
    }
    @Override protected void defineSynchedData(SynchedEntityData.Builder builder) { }
    @Override public boolean canChangeDimensions(Level from, Level to) { return false; }
    @Override public void onAddedToLevel() { super.onAddedToLevel(); if (armed && !consumed && level() instanceof ServerLevel server) ServerEndTickTasks.schedule(server, getUUID()); }
    @Override public void tick() {
        super.tick();
        if (level() instanceof ServerLevel server) {
            if (!armed || consumed || !dimension.equals(server.dimension().identifier().toString())) { discard(); return; }
            setPos(anchor); ServerEndTickTasks.schedule(server, getUUID());
        }
    }
    @Override public void endTick(ServerLevel level) {
        if (isRemoved() || !armed || consumed || !dimension.equals(level.dimension().identifier().toString())) { discard(); return; }
        if (--remaining > 0) { ServerEndTickTasks.schedule(level, getUUID()); return; }
        consumed = true; discard();
        if (spring != null && level.getEntity(spring) instanceof SpringslimeEntity source) source.discard();
        level.playSound(null, net.minecraft.core.BlockPos.containing(anchor), SoundEvents.SLIME_DEATH, SoundSource.NEUTRAL, 1, 1);
        com.hp.thermalshockreforged.ThermalShockReforged.LOGGER.debug("Springslime fade sound completed: task={}, source={}, anchor={}", getUUID(), spring, anchor);
        level.sendParticles(ParticleTypes.POOF, anchor.x, anchor.y, anchor.z, 15, 1, 1, 1, .3);
    }
    @Override protected void addAdditionalSaveData(net.minecraft.world.level.storage.ValueOutput output) { if (spring != null) output.putString("SpringSource", spring.toString()); output.putDouble("SpringX", anchor.x); output.putDouble("SpringY", anchor.y); output.putDouble("SpringZ", anchor.z); output.putString("SpringDimension", dimension); output.putInt("SpringRemaining", remaining); output.putBoolean("SpringArmed", armed); output.putBoolean("SpringConsumed", consumed); }
    @Override protected void readAdditionalSaveData(net.minecraft.world.level.storage.ValueInput input) {
        spring = input.getString("SpringSource").map(java.util.UUID::fromString).orElse(null);
        anchor = new Vec3(input.getDoubleOr("SpringX", 0.0), input.getDoubleOr("SpringY", 0.0), input.getDoubleOr("SpringZ", 0.0)); dimension = input.getStringOr("SpringDimension", "");
        remaining = Math.clamp(input.getIntOr("SpringRemaining", 0), 1, 20); armed = input.getBooleanOr("SpringArmed", false) && spring != null && Double.isFinite(anchor.x) && Double.isFinite(anchor.y) && Double.isFinite(anchor.z); consumed = input.getBooleanOr("SpringConsumed", false);
        noPhysics = true; setNoGravity(true);
    }
}
