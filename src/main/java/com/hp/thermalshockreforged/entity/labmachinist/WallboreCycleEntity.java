package com.hp.thermalshockreforged.entity.labmachinist;

import com.hp.thermalshockreforged.world.timing.*;
import net.minecraft.core.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.*;
import net.minecraft.world.entity.*;
import net.minecraft.world.level.Level;

public final class WallboreCycleEntity extends Entity implements ServerEndTickTask {
    private boolean armed;
    private int remaining = 19, frozen, age = -1;
    private BlockPos anchor = BlockPos.ZERO;
    private Direction facing = Direction.NORTH;
    public WallboreCycleEntity(EntityType<? extends WallboreCycleEntity> type, Level level) { super(type, level); noPhysics = true; setNoGravity(true); }
    void arm(BlockPos pos, Direction facing, int frozen) { armed = true; anchor = pos.immutable(); this.facing = facing; this.frozen = frozen; setPos(pos.getX() + .5, pos.getY() + .5, pos.getZ() + .5); }
    @Override protected void defineSynchedData(SynchedEntityData.Builder builder) { }
    @Override public boolean canChangeDimensions(Level from, Level to) { return false; }
    @Override public void onAddedToLevel() { super.onAddedToLevel(); if (armed && level() instanceof ServerLevel server) ServerEndTickTasks.schedule(server, getUUID()); }
    @Override public void tick() { super.tick(); if (level() instanceof ServerLevel server) { if (!armed) discard(); else ServerEndTickTasks.schedule(server, getUUID()); } }
    @Override public void endTick(ServerLevel server) {
        if (!armed || isRemoved()) return;
        if (age < 0) {
            if (--remaining > 0) { ServerEndTickTasks.schedule(server, getUUID()); return; }
            age = 0;
            if (!server.hasChunkAt(anchor) || !(server.getBlockEntity(anchor) instanceof LabMachineBlockEntity machine) || !machine.produce(server, getUUID(), facing, frozen)) { armed = false; discard(); return; }
        } else {
            age++; if (age <= 22 && age % 2 == 0) server.playSound(null, anchor, SoundEvents.DEEPSLATE_BREAK, SoundSource.BLOCKS, 1, 1);
            if (age <= 18 && age % 3 == 0) server.playSound(null, anchor, SoundEvents.NETHER_BRICKS_BREAK, SoundSource.BLOCKS, 1, 1);
            if (age >= 22) { armed = false; discard(); return; }
        }
        ServerEndTickTasks.schedule(server, getUUID());
    }
    @Override protected void addAdditionalSaveData(CompoundTag tag) { tag.putBoolean("Armed", armed); tag.putInt("Remaining", remaining); tag.putInt("Age", age); tag.putInt("Frozen", frozen); tag.putInt("Facing", facing.get2DDataValue()); tag.putLong("Anchor", anchor.asLong()); }
    @Override protected void readAdditionalSaveData(CompoundTag tag) { armed = tag.getBoolean("Armed"); remaining = Math.clamp(tag.getInt("Remaining"), 1, 19); age = Math.clamp(tag.getInt("Age"), -1, 22); frozen = Math.clamp(tag.getInt("Frozen"), 0, 1); facing = Direction.from2DDataValue(tag.getInt("Facing")); anchor = BlockPos.of(tag.getLong("Anchor")); }
}
