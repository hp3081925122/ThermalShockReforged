package com.hp.thermalshockreforged.world.thermite;

import com.hp.thermalshockreforged.ThermalShockReforged;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

public final class ThermiteDetonationEntity extends Entity implements com.hp.thermalshockreforged.world.timing.ServerEndTickTask {
    @Override public boolean hurtServer(net.minecraft.server.level.ServerLevel level, net.minecraft.world.damagesource.DamageSource source, float damage) { return false; }
    private BlockPos anchor = BlockPos.ZERO;
    private String dimension = "";
    private int remaining = 3;
    private boolean armed;
    private boolean consumed;
    public ThermiteDetonationEntity(EntityType<? extends ThermiteDetonationEntity> type, Level level) { super(type, level); noPhysics = true; setNoGravity(true); }
    public static boolean submit(ServerLevel level, BlockPos pos) {
        var task = ThermiteContent.DETONATION.get().create(level, net.minecraft.world.entity.EntitySpawnReason.TRIGGERED);
        if (task == null) return false;
        task.anchor = pos.immutable(); task.dimension = level.dimension().identifier().toString(); task.armed = true;
        task.setPos(pos.getX(), pos.getY(), pos.getZ());
        return level.addFreshEntity(task);
    }
    @Override protected void defineSynchedData(SynchedEntityData.Builder builder) { }
    @Override public boolean canTeleport(Level from, Level to) { return false; }
    @Override public void onAddedToLevel() { super.onAddedToLevel(); if (armed && !consumed && level() instanceof ServerLevel server) com.hp.thermalshockreforged.world.timing.ServerEndTickTasks.schedule(server, getUUID()); }
    @Override public void tick() {
        super.tick();
        if (level() instanceof ServerLevel server) {
            if (!armed || consumed || !dimension.equals(server.dimension().identifier().toString())) { discard(); return; }
            setPos(anchor.getX(), anchor.getY(), anchor.getZ());
            com.hp.thermalshockreforged.world.timing.ServerEndTickTasks.schedule(server, getUUID());
        }
    }
    @Override public void endTick(ServerLevel level) {
        if (isRemoved() || !armed || consumed || !dimension.equals(level.dimension().identifier().toString())) { discard(); return; }
        if (--remaining > 0) { com.hp.thermalshockreforged.world.timing.ServerEndTickTasks.schedule(level, getUUID()); return; }
        consumed = true; discard();
        ThermalShockReforged.LOGGER.debug("Thermite detonation committed: position={}, dimension={}", anchor, dimension);
        level.explode(null, anchor.getX(), anchor.getY(), anchor.getZ(), 1, Level.ExplosionInteraction.BLOCK);
    }
    @Override protected void addAdditionalSaveData(net.minecraft.world.level.storage.ValueOutput output) {
        output.putLong("ThermiteAnchor", anchor.asLong()); output.putString("ThermiteDimension", dimension);
        output.putInt("ThermiteRemaining", remaining); output.putBoolean("ThermiteArmed", armed); output.putBoolean("ThermiteConsumed", consumed);
    }
    @Override protected void readAdditionalSaveData(net.minecraft.world.level.storage.ValueInput input) {
        anchor = BlockPos.of(input.getLongOr("ThermiteAnchor", 0L)); dimension = input.getStringOr("ThermiteDimension", "");
        remaining = Math.clamp(input.getIntOr("ThermiteRemaining", 0), 1, 3); armed = input.getBooleanOr("ThermiteArmed", false) && input.getString("ThermiteAnchor").isPresent(); consumed = input.getBooleanOr("ThermiteConsumed", false);
        noPhysics = true; setNoGravity(true);
    }
}
