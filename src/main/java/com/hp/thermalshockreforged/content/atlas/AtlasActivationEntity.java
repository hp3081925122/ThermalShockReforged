package com.hp.thermalshockreforged.content.atlas;

import com.hp.thermalshockreforged.ThermalShockReforged;
import com.hp.thermalshockreforged.world.timing.*;
import net.minecraft.core.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.*;
import net.minecraft.world.entity.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;

public final class AtlasActivationEntity extends Entity implements ServerEndTickTask {
    private BlockPos anchor = BlockPos.ZERO;
    private Direction direction = Direction.UP;
    private String dimension = "";
    private int remaining = 20;
    private int phase;
    private boolean armed;
    public AtlasActivationEntity(EntityType<? extends AtlasActivationEntity> type, Level level) { super(type, level); noPhysics = true; setNoGravity(true); }
    void prepare(ServerLevel level, BlockPos pos, Direction face) { anchor = pos.immutable(); direction = face; dimension = level.dimension().location().toString(); armed = true; setPos(pos.getX(), pos.getY(), pos.getZ()); }
    @Override protected void defineSynchedData(SynchedEntityData.Builder builder) { }
    @Override public boolean canChangeDimensions(Level from, Level to) { return false; }
    @Override public void onAddedToLevel() { super.onAddedToLevel(); if (armed && phase < 2 && level() instanceof ServerLevel server) ServerEndTickTasks.schedule(server, getUUID()); }
    @Override public void tick() {
        super.tick();
        if (level() instanceof ServerLevel server) {
            if (!armed || phase >= 2 || !dimension.equals(server.dimension().location().toString())) { discard(); return; }
            setPos(anchor.getX(), anchor.getY(), anchor.getZ()); ServerEndTickTasks.schedule(server, getUUID());
        }
    }
    @Override public void endTick(ServerLevel level) {
        if (isRemoved() || !armed || phase >= 2 || !dimension.equals(level.dimension().location().toString())) { discard(); return; }
        if (--remaining > 0) { ServerEndTickTasks.schedule(level, getUUID()); return; }
        if (phase == 0) {
            phase = 1; remaining = 60;
            var template = level.getStructureManager().get(AtlasContent.BASE_TEMPLATE);
            var rotation = switch (direction) { case NORTH -> Rotation.CLOCKWISE_180; case EAST -> Rotation.COUNTERCLOCKWISE_90; case WEST -> Rotation.CLOCKWISE_90; default -> Rotation.NONE; };
            var origin = anchor.offset(-1, 0, -1);
            if (template.isEmpty() || !template.get().placeInWorld(level, origin, origin, new StructurePlaceSettings().setRotation(rotation).setMirror(Mirror.NONE).setIgnoreEntities(false), level.random, 3)) ThermalShockReforged.LOGGER.warn("Atlas base placement failed: position={}, dimension={}", anchor, dimension);
            level.playSound(null, anchor, SoundEvents.GENERIC_SPLASH, SoundSource.NEUTRAL, 1, 0);
            level.playSound(null, anchor, SoundEvents.MINECART_RIDING, SoundSource.NEUTRAL, 1, -2);
            ServerEndTickTasks.schedule(level, getUUID());
        } else {
            phase = 2; discard();
            level.playSound(null, anchor, SoundEvents.END_PORTAL_SPAWN, SoundSource.NEUTRAL, 1, 5);
        }
    }
    @Override protected void addAdditionalSaveData(CompoundTag tag) { tag.putLong("AtlasAnchor", anchor.asLong()); tag.putString("AtlasDimension", dimension); tag.putInt("AtlasFace", direction.ordinal()); tag.putInt("AtlasRemaining", remaining); tag.putInt("AtlasPhase", phase); tag.putBoolean("AtlasArmed", armed); }
    @Override protected void readAdditionalSaveData(CompoundTag tag) {
        anchor = BlockPos.of(tag.getLong("AtlasAnchor")); dimension = tag.getString("AtlasDimension"); direction = Direction.values()[Math.clamp(tag.getInt("AtlasFace"), 0, 5)]; phase = Math.clamp(tag.getInt("AtlasPhase"), 0, 2); remaining = Math.clamp(tag.getInt("AtlasRemaining"), 1, phase == 0 ? 20 : 60); armed = tag.getBoolean("AtlasArmed") && tag.contains("AtlasAnchor"); noPhysics = true; setNoGravity(true);
    }
}
