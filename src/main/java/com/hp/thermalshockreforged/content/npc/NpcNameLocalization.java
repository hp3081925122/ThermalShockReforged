package com.hp.thermalshockreforged.content.npc;

import com.hp.thermalshockreforged.ThermalShockReforged;
import java.util.Map;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;

public final class NpcNameLocalization {
    private record NameEntry(String key, String english) { }

    private static final Map<String, NameEntry> NAMES = Map.ofEntries(
            Map.entry("shutterbug", new NameEntry("entity.thermal_shock_reforged.shutterbug", "Shutterbug")),
            Map.entry("florist", new NameEntry("entity.thermal_shock_reforged.florist", "Florist")),
            Map.entry("laut", new NameEntry("entity.thermal_shock_reforged.laut", "Laut")),
            Map.entry("ly", new NameEntry("entity.thermal_shock_reforged.ly", "Ly")),
            Map.entry("concierge", new NameEntry("entity.thermal_shock_reforged.concierge.display_name", "Concierge")),
            Map.entry("kofoni", new NameEntry("entity.thermal_shock_reforged.kofoni.display_name", "Kofoni")),
            Map.entry("defector_machinist", new NameEntry("entity.thermal_shock_reforged.defector_machinist.display_name", "Defector Machinist")),
            Map.entry("thermoid", new NameEntry("entity.thermal_shock_reforged.thermoid.display_name", "Thermoid")),
            Map.entry("huntress", new NameEntry("entity.thermal_shock_reforged.huntress.display_name", "Huntress")),
            Map.entry("benzor", new NameEntry("entity.thermal_shock_reforged.benzor.display_name", "Benzor")),
            Map.entry("lab_machinist", new NameEntry("entity.thermal_shock_reforged.lab_machinist", "Defector Machinist"))
    );

    private NpcNameLocalization() { }

    public static void register() { NeoForge.EVENT_BUS.addListener(NpcNameLocalization::join); }

    private static void join(EntityJoinLevelEvent event) {
        if (event.getLevel().isClientSide()) return;
        Entity entity = event.getEntity();
        ResourceLocation id = entity.getType().builtInRegistryHolder().key().location();
        if (!id.getNamespace().equals(ThermalShockReforged.MOD_ID)) return;
        NameEntry entry = NAMES.get(id.getPath());
        if (entry == null) return;
        Component current = entity.getCustomName();
        if (current != null && !entry.english().equals(ChatFormatting.stripFormatting(current.getString()))) return;
        entity.setCustomName(Component.translatable(entry.key()).withStyle(ChatFormatting.GREEN));
        entity.setCustomNameVisible(true);
        ThermalShockReforged.LOGGER.debug("Localized hardcoded NPC name: entity={}, key={}", id, entry.key());
    }
}
