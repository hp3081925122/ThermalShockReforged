package com.hp.thermalshockreforged.world;

import com.hp.thermalshockreforged.ThermalShockReforged;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;
import com.mojang.serialization.Codec;
import net.minecraft.resources.Identifier;

public final class WorldProgressData extends SavedData {
    private static final String ID = ThermalShockReforged.MOD_ID + "_world_progress";
    private static final SavedDataType<WorldProgressData> FACTORY = new SavedDataType<>(Identifier.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, "world_progress"), WorldProgressData::new, Codec.unit(new WorldProgressData()));
    private static final java.util.concurrent.ConcurrentMap<net.minecraft.server.MinecraftServer, WorldProgressData> LOADED = new java.util.concurrent.ConcurrentHashMap<>();
    private double worldCrux;
    private volatile boolean altarUnlocked;
    private volatile boolean squallSpawn = true;
    private boolean palaceExists;
    private boolean palacePlacing;
    private boolean shalespringExists;
    private boolean shalespringPlacing;
    private boolean keystoneExists;
    private boolean keystonePlacing;
    private boolean keystonePuzzleHintShown;
    private boolean keystoneLeverHintShown;
    private int keystoneNoticeTicks = -1;
    private net.minecraft.resources.Identifier keystoneNoticeDimension;
    private net.minecraft.core.BlockPos keystoneNoticeAnchor = net.minecraft.core.BlockPos.ZERO;
    private volatile int oreTier1;
    private volatile int oreTier2;

    public static void register() {
        net.neoforged.neoforge.common.NeoForge.EVENT_BUS.addListener(WorldProgressData::levelLoaded);
        net.neoforged.neoforge.common.NeoForge.EVENT_BUS.addListener(WorldProgressData::serverStopped);
    }
    private static void levelLoaded(net.neoforged.neoforge.event.level.LevelEvent.Load event) {
        if (event.getLevel() instanceof ServerLevel level && level.dimension().equals(net.minecraft.world.level.Level.OVERWORLD)) {
            LOADED.put(level.getServer(), get(level));
        }
    }
    private static void serverStopped(net.neoforged.neoforge.event.server.ServerStoppedEvent event) { LOADED.remove(event.getServer()); }
    public static WorldProgressData forGeneration(ServerLevel level) {
        WorldProgressData data = LOADED.get(level.getServer());
        if (data == null) throw new IllegalStateException("World progress was not loaded before chunk generation");
        return data;
    }
    public boolean altarUnlocked() { return altarUnlocked; }
    public boolean squallSpawn() { return squallSpawn; }
    public boolean unlockAltars() {
        if (altarUnlocked) return false;
        altarUnlocked = true; setDirty(); return true;
    }
    public int oreTier1() { return oreTier1; }
    public int oreTier2() { return oreTier2; }
    public void initializeOreChoices(ServerLevel level) {
        if (!level.getServer().isSameThread()) throw new IllegalStateException("Ore selection must run on the server thread");
        if (oreTier1 != 0 && oreTier2 != 0) return;
        if (oreTier1 == 0) oreTier1 = level.getRandom().nextInt(2) + 1;
        if (oreTier2 == 0) oreTier2 = level.getRandom().nextInt(2) + 1;
        setDirty();
        ThermalShockReforged.LOGGER.debug("Ore choices initialized: tier1={}, tier2={}", oreTier1, oreTier2);
        OreChoicePayload.broadcast(level);
    }

    public static WorldProgressData get(ServerLevel level) {
        return level.getServer().overworld().getDataStorage().computeIfAbsent(FACTORY);
    }
    public boolean beginPalacePlacement() {
        if (palaceExists || palacePlacing) return false;
        palacePlacing = true;
        return true;
    }
    public void finishPalacePlacement(boolean placed) {
        palacePlacing = false;
        if (placed && !palaceExists) {
            palaceExists = true;
            setDirty();
        }
    }
    public boolean beginShalespringPlacement() {
        if (shalespringExists || shalespringPlacing) return false;
        shalespringPlacing = true;
        return true;
    }
    public void finishShalespringPlacement(boolean placed) {
        shalespringPlacing = false;
        if (placed) markShalespringExists();
    }
    public void markShalespringExists() {
        if (!shalespringExists) {
            shalespringExists = true;
            setDirty();
        }
    }
    public boolean beginKeystonePlacement() {
        if (keystoneExists || keystonePlacing) return false;
        keystonePlacing = true;
        return true;
    }
    public void finishKeystonePlacement(boolean placed, net.minecraft.resources.Identifier dimension, net.minecraft.core.BlockPos anchor) {
        keystonePlacing = false;
        if (placed && !keystoneExists) {
            keystoneExists = true;
            keystoneNoticeTicks = 0;
            keystoneNoticeDimension = dimension;
            keystoneNoticeAnchor = anchor.immutable();
            setDirty();
        }
    }
    public int advanceKeystoneNotice() {
        if (keystoneNoticeTicks < 0 || keystoneNoticeTicks >= 200) return -1;
        int elapsed = ++keystoneNoticeTicks;
        if (elapsed == 200) keystoneNoticeTicks = -1;
        setDirty();
        return elapsed;
    }
    public net.minecraft.resources.Identifier keystoneNoticeDimension() { return keystoneNoticeDimension; }
    public net.minecraft.core.BlockPos keystoneNoticeAnchor() { return keystoneNoticeAnchor; }
    public boolean keystoneExists() { return keystoneExists; }
    public boolean keystoneNoticeComplete() { return keystoneExists && keystoneNoticeTicks < 0; }
    public boolean markKeystonePuzzleHintShown() {
        if (keystonePuzzleHintShown) return false;
        keystonePuzzleHintShown = true;
        setDirty();
        return true;
    }
    public boolean markKeystoneLeverHintShown() {
        if (keystoneLeverHintShown) return false;
        keystoneLeverHintShown = true;
        setDirty();
        return true;
    }
    public double worldCrux() { return worldCrux; }
    public void advanceCrux(double stage) {
        if (Double.isFinite(stage) && stage > worldCrux) {
            worldCrux = stage;
            setDirty();
        }
    }
    private static WorldProgressData load(CompoundTag tag, HolderLookup.Provider lookup) {
        var data = new WorldProgressData();
        double value = tag.getDoubleOr("WorldCrux", 0);
        data.worldCrux = Double.isFinite(value) ? Math.max(0, value) : 0;
        data.altarUnlocked = tag.getBooleanOr("AltarUnlocked", false);
        data.squallSpawn = !tag.contains("SquallSpawn") || tag.getBooleanOr("SquallSpawn", false);
        data.palaceExists = tag.getBooleanOr("PalaceExists", false);
        data.shalespringExists = tag.getBooleanOr("ShalespringExists", false);
        data.keystoneExists = tag.getBooleanOr("KeystoneExists", false);
        data.keystonePuzzleHintShown = tag.getBooleanOr("KeystonePuzzleHintShown", false);
        data.keystoneLeverHintShown = tag.getBooleanOr("KeystoneLeverHintShown", false);
        data.keystoneNoticeDimension = net.minecraft.resources.Identifier.tryParse(tag.getStringOr("KeystoneNoticeDimension", ""));
        data.keystoneNoticeAnchor = net.minecraft.core.BlockPos.of(tag.getLongOr("KeystoneNoticeAnchor", 0L));
        int noticeTicks = tag.getIntOr("KeystoneNoticeTicks", 0);
        if (data.keystoneExists && data.keystoneNoticeDimension != null && tag.get("KeystoneNoticeTicks") instanceof net.minecraft.nbt.NumericTag && noticeTicks >= 0 && noticeTicks < 200) data.keystoneNoticeTicks = noticeTicks;
        data.oreTier1 = validOreTier(tag.getIntOr("OreTier1", 0));
        data.oreTier2 = validOreTier(tag.getIntOr("OreTier2", 0));
        return data;
    }
    private static int validOreTier(int value) { return value == 1 || value == 2 ? value : 0; }
    @Override public CompoundTag save(CompoundTag tag, HolderLookup.Provider lookup) {
        tag.putDouble("WorldCrux", worldCrux);
        tag.putBoolean("AltarUnlocked", altarUnlocked);
        tag.putBoolean("SquallSpawn", squallSpawn);
        tag.putBoolean("PalaceExists", palaceExists);
        tag.putBoolean("ShalespringExists", shalespringExists);
        tag.putBoolean("KeystoneExists", keystoneExists);
        tag.putBoolean("KeystonePuzzleHintShown", keystonePuzzleHintShown);
        tag.putBoolean("KeystoneLeverHintShown", keystoneLeverHintShown);
        tag.putInt("KeystoneNoticeTicks", keystoneNoticeTicks);
        if (keystoneNoticeDimension != null) tag.putString("KeystoneNoticeDimension", keystoneNoticeDimension.toString());
        tag.putLong("KeystoneNoticeAnchor", keystoneNoticeAnchor.asLong());
        tag.putInt("OreTier1", oreTier1);
        tag.putInt("OreTier2", oreTier2);
        return tag;
    }
}
