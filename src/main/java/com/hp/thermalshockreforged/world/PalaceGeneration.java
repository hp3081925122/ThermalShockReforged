package com.hp.thermalshockreforged.world;

import com.hp.thermalshockreforged.ThermalShockReforged;
import com.hp.thermalshockreforged.content.ModGameRules;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.player.AdvancementEvent;

public final class PalaceGeneration {
    private static final ResourceLocation ARRIVAL = ResourceLocation.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, "palace_get");
    private static final ResourceLocation TEMPLATE = ResourceLocation.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, "lavender_palace");
    private static final BlockPos ORIGIN = new BlockPos(-74, 178, -132);

    private PalaceGeneration() { }
    public static void register() {
        NeoForge.EVENT_BUS.addListener(PalaceGeneration::advancement);
        ThermalShockReforged.LOGGER.debug("Registered Palace generation listener for AdvancementEarnEvent: advancement={}", ARRIVAL);
    }
    private static void advancement(AdvancementEvent.AdvancementEarnEvent event) {
        if (!ARRIVAL.equals(event.getAdvancement().id()) || !(event.getEntity().level() instanceof ServerLevel level)
                || !level.getGameRules().getBoolean(ModGameRules.LOQENDIA_GENERATE_SPECIAL_STRUCTURES)) return;
        WorldProgressData progress = WorldProgressData.get(level);
        if (!progress.beginPalacePlacement()) return;
        boolean placed = false;
        try {
            var template = level.getStructureManager().get(TEMPLATE);
            if (template.isEmpty()) {
                ThermalShockReforged.LOGGER.error("Palace template is missing: template={}, dimension={}", TEMPLATE, level.dimension().location());
                return;
            }
            ThermalShockReforged.LOGGER.debug("Placing full Palace template: position={}, dimension={}", ORIGIN, level.dimension().location());
            placed = template.get().placeInWorld(level, ORIGIN, ORIGIN,
                    new StructurePlaceSettings().setRotation(Rotation.NONE).setMirror(Mirror.NONE).setIgnoreEntities(false), level.random, 3);
            if (placed) ThermalShockReforged.LOGGER.debug("Palace placement completed: position={}, dimension={}", ORIGIN, level.dimension().location());
            else ThermalShockReforged.LOGGER.error("Palace placement failed: position={}, dimension={}", ORIGIN, level.dimension().location());
        } finally {
            progress.finishPalacePlacement(placed);
        }
    }
}
