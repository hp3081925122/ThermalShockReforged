package com.hp.thermalshockreforged.content.icebreaker;

import com.hp.thermalshockreforged.ThermalShockReforged;
import com.hp.thermalshockreforged.content.decor.geo.GeoDecorContent;
import com.hp.thermalshockreforged.content.decor.geo.GeoDecorKind;
import com.hp.thermalshockreforged.world.timing.ServerEndTickTask;
import com.hp.thermalshockreforged.world.timing.ServerEndTickTasks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

public final class IcebreakerPulseEntity extends Entity implements ServerEndTickTask {
    @Override public boolean hurtServer(net.minecraft.server.level.ServerLevel level, net.minecraft.world.damagesource.DamageSource source, float damage) { return false; }
    private BlockPos anchor = BlockPos.ZERO;
    private String dimension = "";
    private int phase;
    private int remaining = 20;
    private boolean armed;

    public IcebreakerPulseEntity(EntityType<? extends IcebreakerPulseEntity> type, Level level) {
        super(type, level);
        noPhysics = true;
        setNoGravity(true);
    }

    static boolean submit(ServerLevel level, BlockPos pos, IcebreakerBlockEntity owner) {
        if (owner.activated()) return false;
        var task = IcebreakerContent.ICEBREAKER_PULSE.get().create(level, net.minecraft.world.entity.EntitySpawnReason.TRIGGERED);
        if (task == null) return false;
        task.anchor = pos.immutable();
        task.dimension = level.dimension().identifier().toString();
        task.armed = true;
        task.setPos(pos.getX() + .5, pos.getY() + .5, pos.getZ() + .5);
        owner.assign(task.getUUID());
        if (!level.addFreshEntity(task) || task.isRemoved() || level.getEntity(task.getUUID()) != task) {
            task.discard();
            if (level.getBlockEntity(pos) == owner && owner.owns(task.getUUID())) owner.assign(null);
            ThermalShockReforged.LOGGER.debug("Icebreaker task insertion rejected: position={}, dimension={}", pos, level.dimension().identifier());
            return false;
        }
        ThermalShockReforged.LOGGER.debug("Icebreaker task committed: task={}, position={}", task.getUUID(), pos);
        return true;
    }

    @Override protected void defineSynchedData(SynchedEntityData.Builder builder) { }
    @Override public boolean canTeleport(Level from, Level to) { return false; }
    @Override public void onAddedToLevel() {
        super.onAddedToLevel();
        if (armed && level() instanceof ServerLevel server) ServerEndTickTasks.schedule(server, getUUID());
    }

    @Override public void tick() {
        super.tick();
        if (level() instanceof ServerLevel server) {
            if (!valid(server)) { discard(); return; }
            setPos(anchor.getX() + .5, anchor.getY() + .5, anchor.getZ() + .5);
            ServerEndTickTasks.schedule(server, getUUID());
        }
    }

    private boolean valid(ServerLevel level) {
        return armed && phase >= 0 && phase < 2 && dimension.equals(level.dimension().identifier().toString());
    }

    private boolean areaLoaded(ServerLevel level) {
        for (int x = (anchor.getX() - 9) >> 4; x <= (anchor.getX() + 9) >> 4; x++) {
            int dx = Math.max(0, Math.max((x << 4) - anchor.getX(), anchor.getX() - ((x << 4) + 15)));
            for (int z = (anchor.getZ() - 9) >> 4; z <= (anchor.getZ() + 9) >> 4; z++) {
                int dz = Math.max(0, Math.max((z << 4) - anchor.getZ(), anchor.getZ() - ((z << 4) + 15)));
                if (dx * dx + dz * dz <= 81 && !level.hasChunk(x, z)) return false;
            }
        }
        return true;
    }

    @Override public void endTick(ServerLevel level) {
        if (isRemoved() || !valid(level)) return;
        if (phase == 0 && remaining == 1 && !areaLoaded(level)) {
            ServerEndTickTasks.schedule(level, getUUID());
            return;
        }
        if (--remaining > 0) { ServerEndTickTasks.schedule(level, getUUID()); return; }
        if (phase == 0) {
            phase = 1;
            remaining = 10;
            melt(level);
            ServerEndTickTasks.schedule(level, getUUID());
        } else {
            armed = false;
            phase = 2;
            try {
                if (level.getBlockState(anchor).is(GeoDecorContent.block(GeoDecorKind.ICEBREAKER))
                        && level.getBlockEntity(anchor) instanceof IcebreakerBlockEntity owner && owner.owns(getUUID())) {
                    level.setBlock(anchor, Blocks.AIR.defaultBlockState(), 3);
                }
                level.sendParticles(ParticleTypes.POOF, anchor.getX() + .5, anchor.getY() + .5, anchor.getZ() + .5, 10, 1, 1, 1, 0);
                ThermalShockReforged.LOGGER.debug("Icebreaker task completed: task={}, position={}", getUUID(), anchor);
            } finally { discard(); }
        }
    }

    private void melt(ServerLevel level) {
        level.playSound(null, anchor, SoundEvents.ANVIL_PLACE, SoundSource.BLOCKS, 1, 1);
        level.sendParticles(ParticleTypes.CAMPFIRE_COSY_SMOKE, anchor.getX() + .7, anchor.getY() + .1, anchor.getZ() + .7, 10, 1, 0, 1, 0);
        int changed = 0;
        for (int y = -9; y <= 9; y++) for (int x = -9; x <= 9; x++) for (int z = -9; z <= 9; z++) {
            if (x * x / 81.0 + y * y / 81.0 + z * z / 81.0 > 1) continue;
            BlockPos pos = anchor.offset(x, y, z);
            if (!level.getBlockState(pos).is(Blocks.ICE)) continue;
            if (level.setBlock(pos, Blocks.WATER.defaultBlockState(), 3)) changed++;
            if (random.nextDouble() < .7) level.levelEvent(2001, pos, Block.getId(Blocks.ICE.defaultBlockState()));
        }
        ThermalShockReforged.LOGGER.debug("Icebreaker melt committed: task={}, position={}, changed={}", getUUID(), anchor, changed);
    }

    @Override protected void addAdditionalSaveData(net.minecraft.world.level.storage.ValueOutput output) {
        output.putLong("IcebreakerAnchor", anchor.asLong());
        output.putString("IcebreakerDimension", dimension);
        output.putInt("IcebreakerPhase", phase);
        output.putInt("IcebreakerRemaining", remaining);
        output.putBoolean("IcebreakerArmed", armed);
    }

    @Override protected void readAdditionalSaveData(net.minecraft.world.level.storage.ValueInput input) {
        anchor = BlockPos.of(input.getLongOr("IcebreakerAnchor", 0L));
        dimension = input.getStringOr("IcebreakerDimension", "");
        phase = input.getIntOr("IcebreakerPhase", 0);
        remaining = Math.clamp(input.getIntOr("IcebreakerRemaining", 0), 1, phase == 0 ? 20 : 10);
        armed = input.getBooleanOr("IcebreakerArmed", false) && input.read("IcebreakerAnchor", com.mojang.serialization.Codec.PASSTHROUGH).isPresent() && phase >= 0 && phase < 2;
        noPhysics = true;
        setNoGravity(true);
    }
}
