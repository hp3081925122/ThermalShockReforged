package com.hp.thermalshockreforged.world;

import com.hp.thermalshockreforged.ThermalShockReforged;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;

public final class WorldProgressData extends SavedData {
    private static final String ID = ThermalShockReforged.MOD_ID + "_world_progress";
    private static final Factory<WorldProgressData> FACTORY = new Factory<>(WorldProgressData::new, WorldProgressData::load);
    private double worldCrux;

    public static WorldProgressData get(ServerLevel level) {
        return level.getServer().overworld().getDataStorage().computeIfAbsent(FACTORY, ID);
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
        return data;
    }
    @Override public CompoundTag save(CompoundTag tag, HolderLookup.Provider lookup) {
        tag.putDouble("WorldCrux", worldCrux);
        return tag;
    }
}
