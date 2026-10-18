package com.hp.thermalshockreforged.world.thermite;

import com.hp.thermalshockreforged.ThermalShockReforged;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

public final class ThermiteDetonationEntity extends Entity {
    private BlockPos anchor = BlockPos.ZERO;
    private String dimension = "";
    private int remaining = 3;
    private boolean armed;
    private boolean consumed;
    public ThermiteDetonationEntity(EntityType<? extends ThermiteDetonationEntity> type, Level level) { super(type, level); noPhysics = true; setNoGravity(true); }
    public static boolean submit(ServerLevel level, BlockPos pos) {
        var task = ThermiteContent.DETONATION.get().create(level);
        if (task == null) return false;
        task.anchor = pos.immutable(); task.dimension = level.dimension().location().toString(); task.armed = true;
        task.setPos(pos.getX(), pos.getY(), pos.getZ());
        return level.addFreshEntity(task);
    }
    @Override protected void defineSynchedData(SynchedEntityData.Builder builder) { }
    @Override public boolean canChangeDimensions(Level from, Level to) { return false; }
    @Override public void onAddedToLevel() { super.onAddedToLevel(); if (armed && !consumed && level() instanceof ServerLevel server) ThermiteContent.schedule(server, getUUID()); }
    @Override public void tick() {
        super.tick();
        if (level() instanceof ServerLevel server) {
            if (!armed || consumed || !dimension.equals(server.dimension().location().toString())) { discard(); return; }
            setPos(anchor.getX(), anchor.getY(), anchor.getZ());
            ThermiteContent.schedule(server, getUUID());
        }
    }
    void endTick(ServerLevel level) {
        if (isRemoved() || !armed || consumed || !dimension.equals(level.dimension().location().toString())) { discard(); return; }
        if (--remaining > 0) { ThermiteContent.schedule(level, getUUID()); return; }
        consumed = true; discard();
        ThermalShockReforged.LOGGER.debug("Thermite detonation committed: position={}, dimension={}", anchor, dimension);
        level.explode(null, anchor.getX(), anchor.getY(), anchor.getZ(), 1, Level.ExplosionInteraction.BLOCK);
    }
    @Override protected void addAdditionalSaveData(CompoundTag tag) {
        tag.putLong("ThermiteAnchor", anchor.asLong()); tag.putString("ThermiteDimension", dimension);
        tag.putInt("ThermiteRemaining", remaining); tag.putBoolean("ThermiteArmed", armed); tag.putBoolean("ThermiteConsumed", consumed);
    }
    @Override protected void readAdditionalSaveData(CompoundTag tag) {
        anchor = BlockPos.of(tag.getLong("ThermiteAnchor")); dimension = tag.getString("ThermiteDimension");
        remaining = Math.clamp(tag.getInt("ThermiteRemaining"), 1, 3); armed = tag.getBoolean("ThermiteArmed") && tag.contains("ThermiteAnchor"); consumed = tag.getBoolean("ThermiteConsumed");
        noPhysics = true; setNoGravity(true);
    }
}
