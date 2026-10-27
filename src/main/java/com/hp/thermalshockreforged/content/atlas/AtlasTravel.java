package com.hp.thermalshockreforged.content.atlas;

import com.hp.thermalshockreforged.ThermalShockReforged;
import com.hp.thermalshockreforged.world.LoqendiaContent;
import com.hp.thermalshockreforged.world.PalaceGeneration;
import com.hp.thermalshockreforged.content.decor.geo.GeoDecorContent;
import com.hp.thermalshockreforged.content.decor.geo.GeoDecorKind;
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
            player.displayClientMessage(Component.translatable("message.thermal_shock_reforged.atlas.already_here"), true);
            return true;
        }
        var target = player.server.getLevel(destination);
        if (target == null) {
            ThermalShockReforged.LOGGER.warn("Atlas destination is unavailable: player={}, destination={}", player.getUUID(), destination.location());
            player.displayClientMessage(Component.translatable("message.thermal_shock_reforged.atlas.destination_unavailable"), true);
            return false;
        }
        var point = AtlasCoordinates.get(player, destination);
        if (destination.equals(AtlasContent.PALACE) && !target.getBlockState(BlockPos.containing(point)).is(GeoDecorContent.block(GeoDecorKind.ATLAS))) {
            boolean returnReady = target.getBlockState(PalaceGeneration.RETURN_ATLAS).is(GeoDecorContent.block(GeoDecorKind.ATLAS));
            if (!returnReady) returnReady = PalaceGeneration.ensureGenerated(target) && target.getBlockState(PalaceGeneration.RETURN_ATLAS).is(GeoDecorContent.block(GeoDecorKind.ATLAS));
            if (!returnReady) {
                ThermalShockReforged.LOGGER.warn("Atlas entry blocked without a return Atlas: player={}, destination={}, structurePlaced={}", player.getUUID(), destination.location(), com.hp.thermalshockreforged.world.WorldProgressData.get(target).palaceExists());
                player.displayClientMessage(Component.translatable("message.thermal_shock_reforged.atlas.arrival_unavailable"), true);
                return false;
            }
            point = new net.minecraft.world.phys.Vec3(0, 194, 0);
        }
        player.closeContainer();
        player.teleportTo(target, point.x, point.y, point.z, player.getYRot(), player.getXRot());
        if (player.serverLevel() != target) {
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
