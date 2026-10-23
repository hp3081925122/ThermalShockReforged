package com.hp.thermalshockreforged.entity.qo;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;
import com.mojang.serialization.Codec;
import net.minecraft.resources.Identifier;

public final class QoProgressData extends SavedData {
    private static final SavedDataType<QoProgressData> FACTORY = new SavedDataType<>(Identifier.fromNamespaceAndPath("thermal_shock_reforged", "qo_progress"), QoProgressData::new, Codec.unit(new QoProgressData()));
    private double contacts;
    public static QoProgressData get(ServerLevel level) { return level.getServer().overworld().getDataStorage().computeIfAbsent(FACTORY); }
    public double contacts() { return contacts; }
    public void advance() { contacts++; setDirty(); }
    private static QoProgressData load(CompoundTag tag, HolderLookup.Provider lookup) {
        var data = new QoProgressData(); double value = tag.getDouble("NPCiceArtifact"); data.contacts = Double.isFinite(value) && value >= 0 ? value : 0; return data;
    }
    @Override public CompoundTag save(CompoundTag tag, HolderLookup.Provider lookup) { tag.putDouble("NPCiceArtifact", contacts); return tag; }
}
