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
        CompoundTag data = player.getPersistentData().getCompound(Player.PERSISTED_NBT_TAG).getCompound(ThermalShockReforged.MOD_ID).getCompound("AtlasCoordinates").getCompound(dimension.location().toString());
        if (!data.contains("X", 99) || !data.contains("Y", 99) || !data.contains("Z", 99)) return fallback;
        double x = data.getDouble("X"), y = data.getDouble("Y"), z = data.getDouble("Z");
        return Double.isFinite(x) && Double.isFinite(y) && Double.isFinite(z) ? new Vec3(x, y, z) : fallback;
    }
    static void set(Player player, ResourceKey<Level> dimension, BlockPos pos) {
        CompoundTag persisted = player.getPersistentData().getCompound(Player.PERSISTED_NBT_TAG);
        CompoundTag mod = persisted.getCompound(ThermalShockReforged.MOD_ID);
        CompoundTag locations = mod.getCompound("AtlasCoordinates");
        CompoundTag coordinates = new CompoundTag();
        coordinates.putDouble("X", pos.getX()); coordinates.putDouble("Y", pos.getY()); coordinates.putDouble("Z", pos.getZ());
        locations.put(dimension.location().toString(), coordinates); mod.put("AtlasCoordinates", locations);
        persisted.put(ThermalShockReforged.MOD_ID, mod); player.getPersistentData().put(Player.PERSISTED_NBT_TAG, persisted);
    }
}
