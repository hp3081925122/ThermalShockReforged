package com.hp.thermalshockreforged.content.atlas;

import com.hp.thermalshockreforged.ThermalShockReforged;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.level.BlockEvent;

public final class AqeronEvents {
    private AqeronEvents() { }
    public static void register() {
        NeoForge.EVENT_BUS.addListener(AqeronEvents::breakBlock);
        NeoForge.EVENT_BUS.addListener(AqeronEvents::placeBlock);
        NeoForge.EVENT_BUS.addListener(AqeronEvents::death);
    }
    private static void death(LivingDeathEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player) || !player.level().dimension().equals(AtlasContent.PALACE)) return;
        float health = Math.min(player.getMaxHealth(), Math.max(1F, player.getHealth()));
        if (!Float.isFinite(health) || health <= 0) return;
        var point = AtlasCoordinates.get(player, AtlasContent.PALACE);
        ThermalShockReforged.LOGGER.debug("Recovering lethal Aqeron damage: player={}, health={}, destination={}", player.getUUID(), player.getHealth(), point);
        event.setCanceled(true);
        player.setHealth(health);
        player.connection.teleport(point.x, point.y, point.z, player.getYRot(), player.getXRot());
    }
    private static boolean protectedActor(Entity entity) {
        return entity != null && !entity.level().isClientSide && (entity.level().dimension().equals(AtlasContent.PALACE) || com.hp.thermalshockreforged.world.sanctum.PylonBuildingLock.active(entity))
                && !(entity instanceof Player player && player.getAbilities().instabuild);
    }
    private static void breakBlock(BlockEvent.BreakEvent event) {
        if (protectedActor(event.getPlayer())) event.setCanceled(true);
    }
    private static void placeBlock(BlockEvent.EntityPlaceEvent event) {
        if (protectedActor(event.getEntity())) event.setCanceled(true);
    }
}
