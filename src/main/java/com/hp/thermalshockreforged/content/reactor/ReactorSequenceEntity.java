package com.hp.thermalshockreforged.content.reactor;

import com.hp.thermalshockreforged.ThermalShockReforged;
import com.hp.thermalshockreforged.entity.ModEntities;
import com.hp.thermalshockreforged.entity.gaze.GazeContent;
import com.hp.thermalshockreforged.entity.ice.IceContent;
import com.hp.thermalshockreforged.entity.squall.SquallContent;
import com.hp.thermalshockreforged.world.timing.*;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.*;
import net.minecraft.world.entity.*;
import net.minecraft.world.level.Level;

public final class ReactorSequenceEntity extends Entity implements ServerEndTickTask {
    private BlockPos anchor = BlockPos.ZERO;
    private String dimension = "";
    private int elapsed;
    private int pulses;
    private boolean armed;
    private boolean finished;
    public ReactorSequenceEntity(EntityType<? extends ReactorSequenceEntity> type, Level level) { super(type, level); noPhysics = true; setNoGravity(true); }
    void prepare(ServerLevel level, BlockPos pos) { anchor = pos.immutable(); dimension = level.dimension().location().toString(); armed = true; setPos(pos.getX(), pos.getY(), pos.getZ()); }
    void firstPulse(ServerLevel level) { if (armed && !finished && pulses == 0 && arenaLoaded(level)) { pulses++; pulse(level); } }
    @Override protected void defineSynchedData(SynchedEntityData.Builder builder) { }
    @Override public boolean canChangeDimensions(Level from, Level to) { return false; }
    @Override public void onAddedToLevel() { super.onAddedToLevel(); if (armed && !finished && level() instanceof ServerLevel server) ServerEndTickTasks.schedule(server, getUUID()); }
    @Override public void tick() {
        super.tick();
        if (level() instanceof ServerLevel server) {
            if (!valid(server)) { discard(); return; }
            setPos(anchor.getX(), anchor.getY(), anchor.getZ());
            ServerEndTickTasks.schedule(server, getUUID());
        }
    }
    private boolean valid(ServerLevel level) { return armed && !finished && dimension.equals(level.dimension().location().toString()); }
    private boolean arenaLoaded(ServerLevel level) {
        for (int x : new int[]{-7, 0, 7}) for (int z : new int[]{-7, 0, 7}) if (!level.hasChunkAt(anchor.offset(x, 0, z))) return false;
        return true;
    }
    @Override public void endTick(ServerLevel level) {
        if (isRemoved() || !valid(level)) return;
        boolean pulseDue = pulses < 25 && (pulses == 0 || elapsed + 1 >= pulses * 60);
        if (pulseDue && !arenaLoaded(level)) { ServerEndTickTasks.schedule(level, getUUID()); return; }
        if (pulses == 0) { pulses++; pulse(level); ServerEndTickTasks.schedule(level, getUUID()); return; }
        elapsed++;
        if (pulseDue) { pulses++; pulse(level); }
        if (elapsed >= 1900) {
            finished = true;
            spawn(level, SquallContent.SQUALL.get(), anchor.above(3));
            if (level.getBlockState(anchor).is(ReactorContent.CORE.get()) && level.getBlockEntity(anchor) instanceof ReactorCoreBlockEntity core && core.owns(getUUID())) level.destroyBlock(anchor, false);
            level.playSound(null, anchor, SoundEvents.ITEM_BREAK, SoundSource.BLOCKS, 1.5F, -1);
            ThermalShockReforged.LOGGER.debug("Reactor sequence {} completed at {} in {}", getUUID(), anchor, dimension);
            discard();
        } else ServerEndTickTasks.schedule(level, getUUID());
    }
    private void pulse(ServerLevel level) {
        level.sendParticles(ReactorContent.SHOCKWAVE.get(), anchor.getX() + .5, anchor.getY() - .5, anchor.getZ() + .5, 1, 0, 0, 0, 0);
        EntityType<?> type = random.nextDouble() < .9 ? (random.nextDouble() < .7 ? ModEntities.GALVANITE.get() : IceContent.FROSTILYTE_CRYOMANCER.get()) : GazeContent.LENS.get();
        BlockPos pos = BlockPos.containing(anchor.getX() - 7 + random.nextDouble() * 15, anchor.getY() - 1, anchor.getZ() - 7 + random.nextDouble() * 15);
        spawn(level, type, pos);
        level.playSound(null, anchor, SoundEvents.ENDER_EYE_DEATH, SoundSource.BLOCKS, 1, -2);
    }
    private void spawn(ServerLevel level, EntityType<?> type, BlockPos pos) {
        Entity spawned = type.spawn(level, pos, MobSpawnType.MOB_SUMMONED);
        if (spawned != null && level.getEntity(spawned.getUUID()) == spawned) spawned.setDeltaMovement(0, 0, 0);
        else ThermalShockReforged.LOGGER.debug("Reactor summon rejected: sequence={}, type={}, position={}", getUUID(), BuiltInRegistries.ENTITY_TYPE.getKey(type), pos);
    }
    @Override protected void addAdditionalSaveData(CompoundTag tag) {
        tag.putLong("ReactorAnchor", anchor.asLong()); tag.putString("ReactorDimension", dimension); tag.putInt("ReactorElapsed", elapsed); tag.putInt("ReactorPulses", pulses); tag.putBoolean("ReactorArmed", armed); tag.putBoolean("ReactorFinished", finished);
    }
    @Override protected void readAdditionalSaveData(CompoundTag tag) {
        anchor = BlockPos.of(tag.getLong("ReactorAnchor")); dimension = tag.getString("ReactorDimension"); elapsed = Math.clamp(tag.getInt("ReactorElapsed"), 0, 1900); pulses = Math.clamp(tag.getInt("ReactorPulses"), 0, 25); armed = tag.getBoolean("ReactorArmed") && tag.contains("ReactorAnchor"); finished = tag.getBoolean("ReactorFinished"); noPhysics = true; setNoGravity(true);
    }
}
