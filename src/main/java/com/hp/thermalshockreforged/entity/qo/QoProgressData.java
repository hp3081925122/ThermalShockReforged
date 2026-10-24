package com.hp.thermalshockreforged.entity.qo;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;
import com.mojang.serialization.Codec;
import net.minecraft.resources.Identifier;

public final class QoProgressData extends SavedData {
    private static final SavedDataType<QoProgressData> FACTORY = new SavedDataType<>(Identifier.fromNamespaceAndPath("thermal_shock_reforged", "qo_progress"), QoProgressData::new, net.minecraft.nbt.CompoundTag.CODEC.xmap(QoProgressData::load, data -> data.save(new net.minecraft.nbt.CompoundTag())));
    private double contacts;
    public static QoProgressData get(ServerLevel level) { return level.getServer().overworld().getDataStorage().computeIfAbsent(FACTORY); }
    public double contacts() { return contacts; }
    public void advance() { contacts++; setDirty(); }
    private static QoProgressData load(CompoundTag tag) {
        var data = new QoProgressData(); double value = tag.getDoubleOr("NPCiceArtifact", 0); data.contacts = Double.isFinite(value) && value >= 0 ? value : 0; return data;
    }
    private CompoundTag save(CompoundTag tag) { tag.putDouble("NPCiceArtifact", contacts); return tag; }
}
