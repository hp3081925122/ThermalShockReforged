package com.hp.thermalshockreforged.content.machines;

import com.hp.thermalshockreforged.ThermalShockReforged;
import com.hp.thermalshockreforged.content.decor.geo.GrandBlasphemerBlockEntity;
import com.hp.thermalshockreforged.world.timing.ServerEndTickTask;
import com.hp.thermalshockreforged.world.timing.ServerEndTickTasks;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

public final class BlasphemerProcessEntity extends Entity implements ServerEndTickTask {
    private BlockPos anchor = BlockPos.ZERO;
    private UUID token;
    private int elapsed;
    private boolean armed;
    public BlasphemerProcessEntity(EntityType<? extends BlasphemerProcessEntity> type, Level level) { super(type, level); noPhysics = true; setNoGravity(true); }
    public static boolean submit(ServerLevel level, BlockPos pos, UUID token) {
        var task = MachineContent.BLASPHEMER_PROCESS.get().create(level);
        if (task == null) return false;
        task.anchor = pos.immutable(); task.token = token; task.armed = true;
        task.setPos(pos.getX() + .5, pos.getY() + .5, pos.getZ() + .5);
        if (level.addFreshEntity(task)) return true;
        ThermalShockReforged.LOGGER.warn("Unable to submit blasphemer process at {}", pos);
        return false;
    }
    @Override protected void defineSynchedData(SynchedEntityData.Builder builder) { }
    @Override public boolean canChangeDimensions(Level from, Level to) { return false; }
    @Override public void onAddedToLevel() { super.onAddedToLevel(); if (armed && level() instanceof ServerLevel server) ServerEndTickTasks.schedule(server, getUUID()); }
    @Override public void tick() {
        super.tick();
        if (level() instanceof ServerLevel server) {
            if (!armed) discard();
            else ServerEndTickTasks.schedule(server, getUUID());
        }
    }
    @Override public void endTick(ServerLevel level) {
        if (!armed || isRemoved()) return;
        elapsed++;
        switch (elapsed) {
            case 40 -> {
                level.playSound(null, anchor, BuiltInRegistries.SOUND_EVENT.get(MachineContent.id("buzzsaw")), SoundSource.NEUTRAL, 1, -3);
                level.playSound(null, anchor, SoundEvents.WARDEN_DIG, SoundSource.NEUTRAL, 1, 2);
            }
            case 104 -> level.playSound(null, anchor, SoundEvents.ITEM_PICKUP, SoundSource.NEUTRAL, 1, 1);
            case 134 -> {
                level.playSound(null, anchor, SoundEvents.ITEM_PICKUP, SoundSource.NEUTRAL, 1, 1);
                if (!MachineContent.drop(level, anchor.getX() + .5, anchor.getY() + 1, anchor.getZ() + .5, new net.minecraft.world.item.ItemStack(com.hp.thermalshockreforged.content.MaterialContent.items().get("infected_core_sample").get())))
                    ThermalShockReforged.LOGGER.warn("Blasphemer core sample output rejected at {}", anchor);
            }
            case 180 -> {
                armed = false;
                if (level.hasChunkAt(anchor) && level.getBlockEntity(anchor) instanceof GrandBlasphemerBlockEntity machine) machine.finish(token);
                discard();
            }
        }
        if (armed) ServerEndTickTasks.schedule(level, getUUID());
    }
    @Override protected void addAdditionalSaveData(CompoundTag tag) {
        tag.putLong("MachineAnchor", anchor.asLong()); tag.putInt("Elapsed", elapsed); tag.putBoolean("Armed", armed);
        if (token != null) tag.putUUID("ProcessToken", token);
    }
    @Override protected void readAdditionalSaveData(CompoundTag tag) {
        anchor = BlockPos.of(tag.getLong("MachineAnchor")); elapsed = Math.clamp(tag.getInt("Elapsed"), 0, 180);
        token = tag.hasUUID("ProcessToken") ? tag.getUUID("ProcessToken") : null;
        armed = tag.getBoolean("Armed") && tag.contains("MachineAnchor") && token != null && elapsed < 180;
        noPhysics = true; setNoGravity(true);
    }
}
