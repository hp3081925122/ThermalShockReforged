package com.hp.thermalshockreforged.content.atlas;

import com.hp.thermalshockreforged.ThermalShockReforged;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public final class AtlasCoordinates {
    private AtlasCoordinates() { }
    public static Vec3 get(Player player, ResourceKey<Level> dimension) {
        Vec3 fallback = dimension.equals(Level.OVERWORLD) ? Vec3.ZERO : dimension.equals(AtlasContent.PALACE) ? new Vec3(0, 194, 0) : new Vec3(324, 73, 324);
        CompoundTag data = player.getPersistentData().getCompoundOrEmpty(Player.PERSISTED_NBT_TAG).getCompoundOrEmpty(ThermalShockReforged.MOD_ID).getCompoundOrEmpty("AtlasCoordinates").getCompoundOrEmpty(dimension.identifier().toString());
        if (!(data.get("X") instanceof net.minecraft.nbt.NumericTag) || !(data.get("Y") instanceof net.minecraft.nbt.NumericTag) || !(data.get("Z") instanceof net.minecraft.nbt.NumericTag)) return fallback;
        double x = data.getDoubleOr("X", 0), y = data.getDoubleOr("Y", 0), z = data.getDoubleOr("Z", 0);
        return Double.isFinite(x) && Double.isFinite(y) && Double.isFinite(z) ? new Vec3(x, y, z) : fallback;
    }
    static void set(Player player, ResourceKey<Level> dimension, BlockPos pos) {
        CompoundTag persisted = player.getPersistentData().getCompoundOrEmpty(Player.PERSISTED_NBT_TAG);
        CompoundTag mod = persisted.getCompoundOrEmpty(ThermalShockReforged.MOD_ID);
        CompoundTag locations = mod.getCompoundOrEmpty("AtlasCoordinates");
        CompoundTag coordinates = new CompoundTag();
        coordinates.putDouble("X", pos.getX()); coordinates.putDouble("Y", pos.getY()); coordinates.putDouble("Z", pos.getZ());
        locations.put(dimension.identifier().toString(), coordinates); mod.put("AtlasCoordinates", locations);
        persisted.put(ThermalShockReforged.MOD_ID, mod); player.getPersistentData().put(Player.PERSISTED_NBT_TAG, persisted);
    }
}
