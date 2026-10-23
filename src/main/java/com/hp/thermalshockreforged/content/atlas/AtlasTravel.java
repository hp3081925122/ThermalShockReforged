package com.hp.thermalshockreforged.content.atlas;

import com.hp.thermalshockreforged.ThermalShockReforged;
import com.hp.thermalshockreforged.world.LoqendiaContent;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundLevelEventPacket;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.*;
import net.minecraft.world.level.Level;

final class AtlasTravel {
    private AtlasTravel() { }
    static boolean travel(ServerPlayer player, BlockPos origin, int button) {
        ResourceKey<Level> destination = switch (button) { case 0 -> LoqendiaContent.DIMENSION; case 1 -> Level.OVERWORLD; case 2 -> AtlasContent.PALACE; default -> null; };
        if (destination == null) return false;
        ResourceKey<Level> source = player.level().dimension();
        if ((!source.equals(Level.OVERWORLD) && !source.equals(LoqendiaContent.DIMENSION) && !source.equals(AtlasContent.PALACE)) || source.equals(destination)) {
            player.closeContainer();
            player.level().playSound(null, origin, SoundEvents.NOTE_BLOCK_BASS.value(), SoundSource.NEUTRAL, 1, 1);
            player.sendOverlayMessage(Component.translatable("message.thermal_shock_reforged.atlas.already_here"));
            return true;
        }
        var target = player.server.getLevel(destination);
        if (target == null) {
            ThermalShockReforged.LOGGER.warn("Atlas destination is unavailable: player={}, destination={}", player.getUUID(), destination.location());
            player.sendOverlayMessage(Component.translatable("message.thermal_shock_reforged.atlas.destination_unavailable"));
            return false;
        }
        var point = AtlasCoordinates.get(player, destination);
        player.closeContainer();
        player.teleportTo(target, point.x, point.y, point.z, player.getYRot(), player.getXRot());
        if (((net.minecraft.server.level.ServerLevel) player.level()) != target) {
            ThermalShockReforged.LOGGER.debug("Atlas travel was cancelled: player={}, source={}, destination={}", player.getUUID(), source.location(), destination.location());
            return false;
        }
        AtlasCoordinates.set(player, source, origin);
        var arrival = (AtlasArrivalState) player;
        if (!destination.equals(Level.OVERWORLD)) arrival.beginArrivalProtection(180);
        else if (arrival.arrivalProtectionTicks() > 0) arrival.beginArrivalProtection(arrival.arrivalProtectionTicks());
        player.connection.send(new ClientboundLevelEventPacket(1032, BlockPos.ZERO, 0, false));
        return true;
    }
}
