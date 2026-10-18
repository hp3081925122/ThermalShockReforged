package com.hp.thermalshockreforged.world;

import com.hp.thermalshockreforged.ThermalShockReforged;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;

public final class WorldProgressData extends SavedData {
    private static final String ID = ThermalShockReforged.MOD_ID + "_world_progress";
    private static final Factory<WorldProgressData> FACTORY = new Factory<>(WorldProgressData::new, WorldProgressData::load);
    private static final java.util.concurrent.ConcurrentMap<net.minecraft.server.MinecraftServer, WorldProgressData> LOADED = new java.util.concurrent.ConcurrentHashMap<>();
    private double worldCrux;
    private boolean shalespringExists;
    private boolean shalespringPlacing;
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
    public int oreTier1() { return oreTier1; }
    public int oreTier2() { return oreTier2; }
    public void initializeOreChoices(ServerLevel level) {
        if (!level.getServer().isSameThread()) throw new IllegalStateException("Ore selection must run on the server thread");
        if (oreTier1 == 0) {
            if (Math.random() < 0.5) { oreTier1 = 1; setDirty(); }
            else if (Math.random() >= 0.5) { oreTier1 = 2; setDirty(); }
        }
        if (oreTier2 == 0) {
            if (Math.random() < 0.5) { oreTier2 = 1; setDirty(); }
            else if (Math.random() >= 0.5) { oreTier2 = 2; setDirty(); }
        }
    }

    public static WorldProgressData get(ServerLevel level) {
        return level.getServer().overworld().getDataStorage().computeIfAbsent(FACTORY, ID);
    }
    public boolean beginShalespringPlacement() {
        if (shalespringExists || shalespringPlacing) return false;
        shalespringPlacing = true;
        return true;
    }
    public void finishShalespringPlacement(boolean placed) {
        shalespringPlacing = false;
        if (placed && !shalespringExists) {
            shalespringExists = true;
            setDirty();
        }
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
        double value = tag.getDouble("WorldCrux");
        data.worldCrux = Double.isFinite(value) ? Math.max(0, value) : 0;
        data.shalespringExists = tag.getBoolean("ShalespringExists");
        data.oreTier1 = validOreTier(tag.getInt("OreTier1"));
        data.oreTier2 = validOreTier(tag.getInt("OreTier2"));
        return data;
    }
    private static int validOreTier(int value) { return value == 1 || value == 2 ? value : 0; }
    @Override public CompoundTag save(CompoundTag tag, HolderLookup.Provider lookup) {
        tag.putDouble("WorldCrux", worldCrux);
        tag.putBoolean("ShalespringExists", shalespringExists);
        tag.putInt("OreTier1", oreTier1);
        tag.putInt("OreTier2", oreTier2);
        return tag;
    }
}
