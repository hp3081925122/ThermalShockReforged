package com.hp.thermalshockreforged.world.sanctum;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

public final class PylonBuildingLock {
    private static final String REMAINING = "thermal_shock_reforged:pylon_building_lock";
    private PylonBuildingLock() { }
    public static void refresh(Player player) {
        if (!player.level().isClientSide()) player.getPersistentData().putInt(REMAINING, 100);
    }
    public static boolean active(Entity entity) { return entity instanceof Player player && player.getPersistentData().getIntOr(REMAINING, 0) > 0; }
    public static void tick(LivingEntity entity) {
        if (!(entity instanceof Player) || entity.level().isClientSide()) return;
        var data = entity.getPersistentData();
        if (!data.contains(REMAINING)) return;
        int remaining = Math.clamp(data.getIntOr(REMAINING, 0), 0, 100);
        if (remaining <= 1) data.remove(REMAINING); else data.putInt(REMAINING, remaining - 1);
    }
}
