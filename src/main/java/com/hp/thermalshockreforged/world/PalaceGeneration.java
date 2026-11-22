package com.hp.thermalshockreforged.world;

import com.hp.thermalshockreforged.ThermalShockReforged;
import com.hp.thermalshockreforged.content.ModGameRules;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.player.AdvancementEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import com.hp.thermalshockreforged.content.atlas.AtlasContent;

public final class PalaceGeneration {
    private static final Identifier ARRIVAL = Identifier.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, "palace_get");
    private static final Identifier TEMPLATE = Identifier.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, "lavender_palace");
    private static final BlockPos ORIGIN = new BlockPos(-74, 178, -132);
    public static final BlockPos RETURN_ATLAS = ORIGIN.offset(73, 7, 131);

    private PalaceGeneration() { }
    public static void register() {
        NeoForge.EVENT_BUS.addListener(PalaceGeneration::advancement);
        NeoForge.EVENT_BUS.addListener((PlayerEvent.PlayerChangedDimensionEvent event) -> arrived(event));
        NeoForge.EVENT_BUS.addListener((PlayerEvent.PlayerLoggedInEvent event) -> arrived(event));
        ThermalShockReforged.LOGGER.debug("Registered Palace generation and arrival recovery listeners: advancement={}", ARRIVAL);
    }
    private static void advancement(AdvancementEvent.AdvancementProgressEvent event) {
        if (event.getProgressType() != AdvancementEvent.AdvancementProgressEvent.ProgressType.GRANT
                || !event.getAdvancementProgress().isDone() || !ARRIVAL.equals(event.getAdvancement().id())
                || !(event.getEntity().level() instanceof ServerLevel level)) return;
        ensureGenerated(level);
    }
    private static void arrived(PlayerEvent event) {
        if (event.getEntity().level() instanceof ServerLevel level && level.dimension().equals(AtlasContent.PALACE)) ensureGenerated(level);
    }
    public static boolean ensureGenerated(ServerLevel level) {
        if (!level.dimension().equals(AtlasContent.PALACE)) return false;
        WorldProgressData progress = WorldProgressData.get(level);
        if (progress.palaceExists()) return true;
        if (!level.getGameRules().get(ModGameRules.LOQENDIA_GENERATE_SPECIAL_STRUCTURES.get()) || !progress.beginPalacePlacement()) return false;
        boolean placed = false;
        try {
            var template = level.getStructureManager().get(TEMPLATE);
            if (template.isEmpty()) {
                ThermalShockReforged.LOGGER.error("Palace template is missing: template={}, dimension={}", TEMPLATE, level.dimension().identifier());
                return false;
            }
            ThermalShockReforged.LOGGER.debug("Placing full Palace template: position={}, dimension={}", ORIGIN, level.dimension().identifier());
            placed = template.get().placeInWorld(level, ORIGIN, ORIGIN,
                    new StructurePlaceSettings().setRotation(Rotation.NONE).setMirror(Mirror.NONE).setIgnoreEntities(false), level.getRandom(), 3);
            if (placed) ThermalShockReforged.LOGGER.debug("Palace placement completed: position={}, dimension={}", ORIGIN, level.dimension().identifier());
            else ThermalShockReforged.LOGGER.error("Palace placement failed: position={}, dimension={}", ORIGIN, level.dimension().identifier());
        } finally {
            progress.finishPalacePlacement(placed);
        }
        return placed;
    }
}
