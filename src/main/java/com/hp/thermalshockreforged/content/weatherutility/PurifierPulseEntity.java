package com.hp.thermalshockreforged.content.weatherutility;

import com.hp.thermalshockreforged.ThermalShockReforged;
import com.hp.thermalshockreforged.content.MaterialContent;
import com.hp.thermalshockreforged.content.decor.geo.GeoDecorContent;
import com.hp.thermalshockreforged.content.decor.geo.GeoDecorKind;
import com.hp.thermalshockreforged.content.decor.nativeblocks.NativeDecorContent;
import com.hp.thermalshockreforged.content.growth.GrowthContent;
import com.hp.thermalshockreforged.content.plants.PlantContent;
import com.hp.thermalshockreforged.content.terrain.TerrainContent;
import com.hp.thermalshockreforged.world.timing.ServerEndTickTask;
import com.hp.thermalshockreforged.world.timing.ServerEndTickTasks;
import java.util.Set;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public final class PurifierPulseEntity extends Entity implements ServerEndTickTask {
    private BlockPos anchor = BlockPos.ZERO;
    private String dimension = "";
    private int remaining = 20;
    private boolean armed;
    public PurifierPulseEntity(EntityType<? extends PurifierPulseEntity> type, Level level) { super(type, level); noPhysics = true; setNoGravity(true); }
    static boolean submit(ServerLevel level, BlockPos pos, PurifierBlockEntity owner) {
        if (owner.activated()) return false;
        var pulse = WeatherUtilityContent.PURIFIER_PULSE.get().create(level);
        if (pulse == null) return false;
        pulse.anchor = pos.immutable(); pulse.dimension = level.dimension().location().toString(); pulse.armed = true;
        pulse.setPos(pos.getX() + .5, pos.getY() + .5, pos.getZ() + .5);
        owner.assign(pulse.getUUID());
        if (!level.addFreshEntity(pulse) || pulse.isRemoved() || level.getEntity(pulse.getUUID()) != pulse) {
            pulse.discard();
            if (level.getBlockEntity(pos) == owner && owner.owns(pulse.getUUID())) owner.assign(null);
            ThermalShockReforged.LOGGER.debug("Purifier pulse insertion rejected: position={}, dimension={}", pos, level.dimension().location());
            return false;
        }
        ThermalShockReforged.LOGGER.debug("Purifier pulse committed: task={}, position={}, remaining={}", pulse.getUUID(), pos, pulse.remaining);
        return true;
    }
    @Override protected void defineSynchedData(SynchedEntityData.Builder builder) { }
    @Override public boolean canChangeDimensions(Level from, Level to) { return false; }
    @Override public void onAddedToLevel() { super.onAddedToLevel(); if (armed && level() instanceof ServerLevel server) ServerEndTickTasks.schedule(server, getUUID()); }
    @Override public void tick() {
        super.tick();
        if (level() instanceof ServerLevel server) {
            if (!valid(server)) { discard(); return; }
            setPos(anchor.getX() + .5, anchor.getY() + .5, anchor.getZ() + .5);
            ServerEndTickTasks.schedule(server, getUUID());
        }
    }
    private boolean valid(ServerLevel level) { return armed && dimension.equals(level.dimension().location().toString()); }
    private boolean areaLoaded(ServerLevel level) {
        for (int x = (anchor.getX() - 15) >> 4; x <= (anchor.getX() + 15) >> 4; x++)
            for (int z = (anchor.getZ() - 15) >> 4; z <= (anchor.getZ() + 15) >> 4; z++)
                if (!level.hasChunk(x, z)) return false;
        return true;
    }
    @Override public void endTick(ServerLevel level) {
        if (isRemoved() || !valid(level)) return;
        if (remaining == 1 && !areaLoaded(level)) { ServerEndTickTasks.schedule(level, getUUID()); return; }
        if (--remaining > 0) { ServerEndTickTasks.schedule(level, getUUID()); return; }
        armed = false;
        try {
            int changed = purify(level);
            if (level.getBlockState(anchor).is(WeatherUtilityContent.PURIFIER.get()) && level.getBlockEntity(anchor) instanceof PurifierBlockEntity owner && owner.owns(getUUID())) level.destroyBlock(anchor, false);
            level.sendParticles(ParticleTypes.HAPPY_VILLAGER, anchor.getX() + .5, anchor.getY() + .5, anchor.getZ() + .5, 15, 2, 2, 2, .1);
            level.sendParticles(ParticleTypes.POOF, anchor.getX() + .5, anchor.getY() + .5, anchor.getZ() + .5, 10, 1, 1, 1, 0);
            ThermalShockReforged.LOGGER.debug("Purifier pulse completed: task={}, position={}, changed={}", getUUID(), anchor, changed);
        } finally { discard(); }
    }
    private int purify(ServerLevel level) {
        Block permafrost = MaterialContent.blocks().get("permafrost").get();
        Block soapstone = MaterialContent.blocks().get("soapstone").get();
        Block cobbledShale = MaterialContent.blocks().get("cobbled_shale").get();
        Set<Block> bodies = Set.of(NativeDecorContent.SPIRE_BASE.get(), GeoDecorContent.block(GeoDecorKind.SPIRE_BLOSSOM), MaterialContent.blocks().get("thin_spire_bulb").get(), MaterialContent.blocks().get("spire_bulb").get());
        Set<Block> stems = Set.of(MaterialContent.blocks().get("spire_stem").get(), MaterialContent.blocks().get("spire_tendrils").get(), GrowthContent.SPIRE_BLOSSOM_STEM.get(), GrowthContent.SPIRE_BLOOM_RUBICON.get());
        int changed = 0;
        for (int y = -15; y <= 15; y++) for (int x = -15; x <= 15; x++) for (int z = -15; z <= 15; z++) {
            if (x * x / 225.0 + y * y / 225.0 + z * z / 225.0 > 1) continue;
            BlockPos pos = anchor.offset(x, y, z);
            BlockState state = level.getBlockState(pos);
            if (state.is(permafrost) || state.is(NativeDecorContent.SNOWY_PERMAFROST.get())) changed += replace(level, pos, soapstone);
            if (level.getBlockState(pos).is(TerrainContent.GRASSY_SHALE.get()) && random.nextDouble() < .6) changed += replace(level, pos, NativeDecorContent.PURIFIED_GRASSY_SHALE.get());
            if (level.getBlockState(pos).is(TerrainContent.SNOWY_SHALE.get()) && random.nextDouble() < .6) changed += replace(level, pos, TerrainContent.GRASSY_SHALE.get());
            if (level.getBlockState(pos).is(TerrainContent.LOQUE_GRASS.get())) changed += replace(level, pos, PlantContent.PURIFIED_LOQUE_GRASS.get());
            if (level.getBlockState(pos).is(TerrainContent.LOQUE_LEAVES.get()) && random.nextDouble() < .6) changed += replace(level, pos, TerrainContent.PURIFIED_LOQUE_LEAVES.get());
            if (bodies.contains(level.getBlockState(pos).getBlock()) && random.nextDouble() < .6) changed += replace(level, pos, cobbledShale);
            if (stems.contains(level.getBlockState(pos).getBlock()) && random.nextDouble() < .6 && level.destroyBlock(pos, false)) changed++;
        }
        return changed;
    }
    private static int replace(ServerLevel level, BlockPos pos, Block target) { return level.setBlock(pos, target.withPropertiesOf(level.getBlockState(pos)), 3) ? 1 : 0; }
    @Override protected void addAdditionalSaveData(CompoundTag tag) {
        tag.putLong("PurifierAnchor", anchor.asLong()); tag.putString("PurifierDimension", dimension); tag.putInt("PurifierRemaining", remaining); tag.putBoolean("PurifierArmed", armed);
    }
    @Override protected void readAdditionalSaveData(CompoundTag tag) {
        anchor = BlockPos.of(tag.getLong("PurifierAnchor")); dimension = tag.getString("PurifierDimension"); remaining = Math.clamp(tag.getInt("PurifierRemaining"), 1, 20);
        armed = tag.getBoolean("PurifierArmed") && tag.contains("PurifierAnchor"); noPhysics = true; setNoGravity(true);
    }
}
