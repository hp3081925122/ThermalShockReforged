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

public final class ShalespringGeneration {
    private static final ResourceLocation ARRIVAL = ResourceLocation.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, "loqendia_get");
    private static final ResourceLocation TEMPLATE = ResourceLocation.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, "shalespring_august");
    private static final BlockPos ORIGIN = new BlockPos(316, 53, 299);

    private ShalespringGeneration() { }
    public static void register() {
        NeoForge.EVENT_BUS.addListener(ShalespringGeneration::advancement);
        ThermalShockReforged.LOGGER.debug("Registered Shalespring generation listener for AdvancementEarnEvent: advancement={}", ARRIVAL);
    }
    private static void advancement(AdvancementEvent.AdvancementEarnEvent event) {
        if (!ARRIVAL.equals(event.getAdvancement().id()) || !(event.getEntity().level() instanceof ServerLevel level)
                || !level.getGameRules().getBoolean(ModGameRules.LOQENDIA_GENERATE_SPECIAL_STRUCTURES)) return;
        WorldProgressData progress = WorldProgressData.get(level);
        if (!progress.beginShalespringPlacement()) return;
        boolean placed = false;
        try {
            var template = level.getStructureManager().get(TEMPLATE);
            if (template.isEmpty()) {
                ThermalShockReforged.LOGGER.error("Shalespring template is missing: template={}, dimension={}", TEMPLATE, level.dimension().location());
                return;
            }
            ThermalShockReforged.LOGGER.debug("Placing full Shalespring template: position={}, dimension={}", ORIGIN, level.dimension().location());
            placed = template.get().placeInWorld(level, ORIGIN, ORIGIN,
                    new StructurePlaceSettings().setRotation(Rotation.NONE).setMirror(Mirror.NONE).setIgnoreEntities(false), level.random, 3);
            if (placed) ThermalShockReforged.LOGGER.debug("Shalespring placement completed: position={}, dimension={}", ORIGIN, level.dimension().location());
            else ThermalShockReforged.LOGGER.error("Shalespring placement failed: position={}, dimension={}", ORIGIN, level.dimension().location());
        } finally {
            progress.finishShalespringPlacement(placed);
        }
    }
}
