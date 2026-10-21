package com.hp.thermalshockreforged.entity.sanctum;

import com.hp.thermalshockreforged.ThermalShockReforged;
import com.hp.thermalshockreforged.content.decor.geo.*;
import com.hp.thermalshockreforged.world.timing.*;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.*;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.*;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.storage.loot.*;
import net.minecraft.world.level.storage.loot.parameters.*;
import net.minecraft.world.phys.Vec3;

public final class PylonCoreActivationEntity extends Entity implements ServerEndTickTask {
    private BlockPos anchor = BlockPos.ZERO;
    private String dimension = "";
    private boolean explosive;
    private ResourceKey<LootTable> loot;
    private boolean armed;
    private boolean consumed;
    private int remaining = 40;
    public PylonCoreActivationEntity(EntityType<? extends PylonCoreActivationEntity> type, Level level) { super(type, level); noPhysics = true; setNoGravity(true); }
    public static boolean begin(ServerLevel level, BlockPos pos, ResourceKey<LootTable> loot) {
        boolean explosive = loot == null;
        if (!(level.getBlockEntity(pos) instanceof GeoDecorBlockEntity core) || core.kind != GeoDecorKind.PYLON_CORE || core.getPersistentData().hasUUID(PylonCoreBlock.ACTIVATION_TOKEN)) return false;
        if (!explosive && level.getServer().reloadableRegistries().getLootTable(loot) == LootTable.EMPTY) {
            ThermalShockReforged.LOGGER.warn("Pylon core dust table is unavailable; keeping inactive core: position={}", pos); return false;
        }
        var task = SanctumContent.CORE_ACTIVATION.get().create(level);
        if (task == null) return false;
        task.anchor = pos.immutable(); task.setPos(Vec3.atLowerCornerOf(pos)); task.dimension = level.dimension().location().toString(); task.explosive = explosive; task.loot = loot; task.armed = true;
        var data = core.getPersistentData(); data.putUUID(PylonCoreBlock.ACTIVATION_TOKEN, task.getUUID()); core.setChanged();
        if (!level.addFreshEntity(task)) {
            task.discard();
            if (level.getBlockEntity(pos) == core && data.hasUUID(PylonCoreBlock.ACTIVATION_TOKEN) && data.getUUID(PylonCoreBlock.ACTIVATION_TOKEN).equals(task.getUUID())) { data.remove(PylonCoreBlock.ACTIVATION_TOKEN); core.setChanged(); }
            ThermalShockReforged.LOGGER.warn("Pylon core activation could not be scheduled: position={}, explosive={}", pos, explosive); return false;
        }
        if (level.getBlockState(pos).is(GeoDecorContent.block(GeoDecorKind.PYLON_CORE))) level.setBlock(pos, level.getBlockState(pos).setValue(GeoDecorBlock.ANIMATION, 1), 3);
        level.playSound(null, pos, SoundEvents.CONDUIT_ACTIVATE, SoundSource.NEUTRAL, 1, explosive ? -1 : 1);
        ThermalShockReforged.LOGGER.debug("Pylon core activation committed: position={}, dimension={}, explosive={}, task={}", pos, task.dimension, explosive, task.getUUID());
        return true;
    }
    @Override protected void defineSynchedData(SynchedEntityData.Builder builder) { }
    @Override public boolean canChangeDimensions(Level from, Level to) { return false; }
    @Override public void onAddedToLevel() { super.onAddedToLevel(); if (armed && !consumed && level() instanceof ServerLevel server) ServerEndTickTasks.schedule(server, getUUID()); }
    @Override public void tick() {
        super.tick();
        if (level() instanceof ServerLevel server) {
            if (!armed || consumed || !dimension.equals(server.dimension().location().toString())) { discard(); return; }
            setPos(Vec3.atLowerCornerOf(anchor)); ServerEndTickTasks.schedule(server, getUUID());
        }
    }
    private GeoDecorBlockEntity ownCore(ServerLevel level) {
        if (!level.hasChunkAt(anchor) || !level.getBlockState(anchor).is(GeoDecorContent.block(GeoDecorKind.PYLON_CORE)) || !(level.getBlockEntity(anchor) instanceof GeoDecorBlockEntity core)) return null;
        var data = core.getPersistentData();
        return data.hasUUID(PylonCoreBlock.ACTIVATION_TOKEN) && data.getUUID(PylonCoreBlock.ACTIVATION_TOKEN).equals(getUUID()) ? core : null;
    }
    private void release(ServerLevel level, GeoDecorBlockEntity core) {
        if (core == null) return;
        core.getPersistentData().remove(PylonCoreBlock.ACTIVATION_TOKEN); core.setChanged();
        if (level.getBlockState(anchor).is(core.getBlockState().getBlock())) level.setBlock(anchor, level.getBlockState(anchor).setValue(GeoDecorBlock.ANIMATION, 0), 3);
    }
    @Override public void endTick(ServerLevel level) {
        if (isRemoved() || !armed || consumed || !dimension.equals(level.dimension().location().toString())) { discard(); return; }
        if (--remaining > 0) { ServerEndTickTasks.schedule(level, getUUID()); return; }
        consumed = true; discard();
        var core = ownCore(level);
        LootTable table = null;
        if (!explosive) table = level.getServer().reloadableRegistries().getLootTable(loot);
        if (!explosive && table == LootTable.EMPTY) {
            release(level, core); ThermalShockReforged.LOGGER.warn("Pylon core conversion cancelled because dust table disappeared: position={}, originalCorePresent={}", anchor, core != null); return;
        }
        if (core != null && !level.setBlock(anchor, Blocks.AIR.defaultBlockState(), 3)) {
            release(level, core);
            if (!explosive) { ThermalShockReforged.LOGGER.warn("Pylon core conversion removal was rejected: position={}", anchor); return; }
        }
        if (explosive) { level.explode(null, anchor.getX(), anchor.getY() + 1.5, anchor.getZ(), 4, Level.ExplosionInteraction.BLOCK); return; }
        level.playSound(null, anchor, SoundEvents.CONDUIT_DEACTIVATE, SoundSource.NEUTRAL, 1, 1);
        level.playSound(null, anchor, SoundEvents.GENERIC_EXTINGUISH_FIRE, SoundSource.NEUTRAL, .8F, 1);
        Vec3 origin = new Vec3(anchor.getX() + .5, anchor.getY() + 2.5, anchor.getZ() + .5);
        var params = new LootParams.Builder(level).withParameter(LootContextParams.ORIGIN, origin).create(LootContextParamSets.CHEST);
        for (var stack : table.getRandomItems(params)) { var drop = new ItemEntity(level, origin.x, origin.y, origin.z, stack); drop.setDefaultPickUpDelay(); level.addFreshEntity(drop); }
        level.sendParticles(ParticleTypes.END_ROD, anchor.getX(), anchor.getY() + 1.5, anchor.getZ(), 30, .2, .2, .2, .2);
    }
    @Override protected void addAdditionalSaveData(CompoundTag tag) {
        tag.putLong("CoreAnchor", anchor.asLong()); tag.putString("CoreDimension", dimension); tag.putBoolean("CoreExplosive", explosive); if (loot != null) tag.putString("CoreLoot", loot.location().toString()); tag.putBoolean("CoreArmed", armed); tag.putBoolean("CoreConsumed", consumed); tag.putInt("CoreRemaining", remaining);
    }
    @Override protected void readAdditionalSaveData(CompoundTag tag) {
        anchor = BlockPos.of(tag.getLong("CoreAnchor")); dimension = tag.getString("CoreDimension"); explosive = tag.getBoolean("CoreExplosive"); consumed = tag.getBoolean("CoreConsumed"); remaining = Math.clamp(tag.getInt("CoreRemaining"), 1, 40);
        var lootId = ResourceLocation.tryParse(tag.getString("CoreLoot"));
        loot = null;
        if (tag.contains("CoreLoot", 8) && lootId != null) loot = ResourceKey.create(Registries.LOOT_TABLE, lootId);
        armed = tag.getBoolean("CoreArmed") && tag.contains("CoreAnchor", 99) && ResourceLocation.tryParse(dimension) != null && (explosive || loot != null);
    }
}
