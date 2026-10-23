package com.hp.thermalshockreforged.world;

import com.hp.thermalshockreforged.ThermalShockReforged;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderSet;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.MapItem;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.saveddata.maps.MapDecorationTypes;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;

public final class ErrandMaps {
    private static final String KEY = "item." + ThermalShockReforged.MOD_ID + ".errand_map.";
    private static final int SEARCH_RADIUS = 100;
    private static final byte ZOOM = 2;

    private ErrandMaps() { }

    public static void give(ServerPlayer player, String structure) {
        ServerLevel level = player.server.getLevel(LoqendiaContent.DIMENSION);
        if (level == null) {
            ThermalShockReforged.LOGGER.warn("Errand map skipped: Loqendia level unavailable, structure={}", structure);
            return;
        }
        var key = ResourceKey.create(Registries.STRUCTURE, Identifier.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, structure));
        var holder = level.registryAccess().lookupOrThrow(Registries.STRUCTURE).getHolder(key);
        if (holder.isEmpty()) {
            ThermalShockReforged.LOGGER.warn("Errand map skipped: structure missing, structure={}", structure);
            return;
        }
        Component name = Component.translatable("structure." + ThermalShockReforged.MOD_ID + "." + structure);
        BlockPos origin;
        if (player.level() == level) origin = player.blockPosition();
        else origin = level.getSharedSpawnPos();
        long start = System.nanoTime();
        var found = level.getChunkSource().getGenerator().findNearestMapStructure(level, HolderSet.<Structure>direct(holder.get()), origin, SEARCH_RADIUS, false);
        ThermalShockReforged.LOGGER.debug("Errand map search: player={}, structure={}, origin={}, result={}, millis={}", player.getUUID(), structure, origin, found, (System.nanoTime() - start) / 1_000_000);
        if (found == null) {
            player.sendSystemMessage(Component.translatable(KEY + "not_found", name).withStyle(ChatFormatting.GRAY));
            return;
        }
        BlockPos target = found.getFirst();
        ItemStack map = MapItem.create(level, target.getX(), target.getZ(), ZOOM, true, true);
        MapItem.renderBiomePreviewMap(level, map);
        MapItemSavedData.addTargetDecoration(map, target, "+", MapDecorationTypes.RED_X);
        map.set(DataComponents.ITEM_NAME, Component.translatable(KEY + "name", name));
        if (!player.getInventory().add(map)) player.drop(map, false);
        player.level().playSound(null, player.blockPosition(), SoundEvents.UI_CARTOGRAPHY_TABLE_TAKE_RESULT, SoundSource.PLAYERS, 1, 1);
        player.sendSystemMessage(Component.translatable(KEY + "received", name).withStyle(ChatFormatting.GOLD));
    }
}
