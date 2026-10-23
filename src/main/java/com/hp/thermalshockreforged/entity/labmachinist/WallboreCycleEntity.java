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
    @Override protected void addAdditionalSaveData(net.minecraft.world.level.storage.ValueOutput output) { output.putBoolean("Armed", armed); output.putInt("Remaining", remaining); output.putInt("Age", age); output.putInt("Frozen", frozen); output.putInt("Facing", facing.get2DDataValue()); output.putLong("Anchor", anchor.asLong()); }
    @Override protected void readAdditionalSaveData(net.minecraft.world.level.storage.ValueInput input) { armed = input.getBooleanOr("Armed", false); remaining = Math.clamp(input.getIntOr("Remaining", 0), 1, 19); age = Math.clamp(input.getIntOr("Age", 0), -1, 22); frozen = Math.clamp(input.getIntOr("Frozen", 0), 0, 1); facing = Direction.from2DDataValue(input.getIntOr("Facing", 0)); anchor = BlockPos.of(input.getLongOr("Anchor", 0L)); }
}
