package com.hp.thermalshockreforged.content.camera;

import com.hp.thermalshockreforged.ThermalShockReforged;
import java.util.*;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;

final class CameraCatalog {
    private static final List<Entry> ENTRIES = List.of(
        new Entry(Set.of(id("maiden")), "camera.thermal_shock_reforged.catalog.maiden", false),
        new Entry(Set.of(id("probosker")), "camera.thermal_shock_reforged.catalog.probosker", false),
        new Entry(Set.of(id("snobol")), "camera.thermal_shock_reforged.catalog.snobol", false),
        new Entry(Set.of(id("explosive_snobol")), "camera.thermal_shock_reforged.catalog.explosive_snobol", false),
        new Entry(Set.of(id("razorwing"), id("ruby_armoured_razorwing"), id("blood_armoured_razorwing")), "camera.thermal_shock_reforged.catalog.razorwing", false),
        new Entry(Set.of(id("trilobite")), "camera.thermal_shock_reforged.catalog.trilobite", false),
        new Entry(Set.of(id("sentrarius")), "camera.thermal_shock_reforged.catalog.sentrarius", false),
        new Entry(Set.of(id("willograde")), "camera.thermal_shock_reforged.catalog.willograde", false),
        new Entry(Set.of(id("cerulean_jellercel"), id("fuschia_jellercel"), id("veridian_jellercel")), "camera.thermal_shock_reforged.catalog.cerulean_jellercel", false),
        new Entry(Set.of(id("aerocel")), "camera.thermal_shock_reforged.catalog.aerocel", false),
        new Entry(Set.of(id("chilloo")), "camera.thermal_shock_reforged.catalog.chilloo", false),
        new Entry(Set.of(id("louse")), "camera.thermal_shock_reforged.catalog.louse", false),
        new Entry(Set.of(id("glastrekker"), id("armoured_glastrekker")), "camera.thermal_shock_reforged.catalog.glastrekker", false),
        new Entry(Set.of(id("pixie")), "camera.thermal_shock_reforged.catalog.pixie", false),
        new Entry(Set.of(id("pillbud")), "camera.thermal_shock_reforged.catalog.pillbud", false),
        new Entry(Set.of(id("gelshooter")), "camera.thermal_shock_reforged.catalog.gelshooter", false),
        new Entry(Set.of(id("tesla_reed")), "camera.thermal_shock_reforged.catalog.tesla_reed", false),
        new Entry(Set.of(id("gardenling")), "camera.thermal_shock_reforged.catalog.gardenling", false),
        new Entry(Set.of(id("crystal_koi")), "camera.thermal_shock_reforged.catalog.crystal_koi", false),
        new Entry(Set.of(id("chromefish")), "camera.thermal_shock_reforged.catalog.chromefish", false),
        new Entry(Set.of(id("siibol")), "camera.thermal_shock_reforged.catalog.siibol", false),
        new Entry(Set.of(id("bonerahna")), "camera.thermal_shock_reforged.catalog.bonerahna", false),
        new Entry(Set.of(id("scarlet_ray")), "camera.thermal_shock_reforged.catalog.scarlet_ray", false),
        new Entry(Set.of(id("pondler")), "camera.thermal_shock_reforged.catalog.pondler", false),
        new Entry(Set.of(id("torpedo")), "camera.thermal_shock_reforged.catalog.torpedo", false),
        new Entry(Set.of(id("springling")), "camera.thermal_shock_reforged.catalog.springling", false),
        new Entry(Set.of(id("sola")), "camera.thermal_shock_reforged.catalog.sola", false),
        new Entry(Set.of(id("magnetite")), "camera.thermal_shock_reforged.catalog.magnetite", false),
        new Entry(Set.of(id("lens")), "camera.thermal_shock_reforged.catalog.lens", false),
        new Entry(Set.of(id("frostilyte")), "camera.thermal_shock_reforged.catalog.frostilyte", false),
        new Entry(Set.of(id("frostilyte_soldier")), "camera.thermal_shock_reforged.catalog.frostilyte_soldier", false),
        new Entry(Set.of(id("frostilyte_gunner")), "camera.thermal_shock_reforged.catalog.frostilyte_gunner", false),
        new Entry(Set.of(id("frostilyte_cryomancer")), "camera.thermal_shock_reforged.catalog.frostilyte_cryomancer", false),
        new Entry(Set.of(id("kilolyte")), "camera.thermal_shock_reforged.catalog.kilolyte", false),
        new Entry(Set.of(id("icidrone")), "camera.thermal_shock_reforged.catalog.icidrone", false),
        new Entry(Set.of(id("icidrogrunt")), "camera.thermal_shock_reforged.catalog.icidrogrunt", false),
        new Entry(Set.of(id("icidrozerk")), "camera.thermal_shock_reforged.catalog.icidrozerk", false),
        new Entry(Set.of(id("protoi_gamma")), "camera.thermal_shock_reforged.catalog.protoi_gamma", false),
        new Entry(Set.of(id("protoi_delta")), "camera.thermal_shock_reforged.catalog.protoi_delta", false),
        new Entry(Set.of(id("protoi_sunbeam")), "camera.thermal_shock_reforged.catalog.protoi_sunbeam", false),
        new Entry(Set.of(id("sootle")), "camera.thermal_shock_reforged.catalog.sootle", false),
        new Entry(Set.of(id("sootling")), "camera.thermal_shock_reforged.catalog.sootling", false),
        new Entry(Set.of(id("beetle_thrasher")), "camera.thermal_shock_reforged.catalog.beetle_thrasher", false),
        new Entry(Set.of(id("beetle_crasher")), "camera.thermal_shock_reforged.catalog.beetle_crasher", false),
        new Entry(Set.of(id("dormant_beetle_crasher")), "camera.thermal_shock_reforged.catalog.dormant_beetle_crasher", false),
        new Entry(Set.of(id("galvanite")), "camera.thermal_shock_reforged.catalog.galvanite", false),
        new Entry(Set.of(id("forecast")), "camera.thermal_shock_reforged.catalog.forecast", false),
        new Entry(Set.of(id("ripple"), id("sanctum_ripple")), "camera.thermal_shock_reforged.catalog.ripple", false),
        new Entry(Set.of(id("ribbonblade_galvanite")), "camera.thermal_shock_reforged.catalog.ribbonblade_galvanite", false),
        new Entry(Set.of(id("graver")), "camera.thermal_shock_reforged.catalog.graver", false),
        new Entry(Set.of(id("neon_lime_graver")), "camera.thermal_shock_reforged.catalog.neon_lime_graver", false),
        new Entry(Set.of(id("lanternbearer")), "camera.thermal_shock_reforged.catalog.lanternbearer", false),
        new Entry(Set.of(id("raincatcher")), "camera.thermal_shock_reforged.catalog.raincatcher", false),
        new Entry(Set.of(id("skeletitan"), id("frost_titan"), id("mantletitan")), "camera.thermal_shock_reforged.catalog.skeletitan", false),
        new Entry(Set.of(id("errant"), id("sanctum_errant")), "camera.thermal_shock_reforged.catalog.errant", false),
        new Entry(Set.of(id("skullscor")), "camera.thermal_shock_reforged.catalog.skullscor", false),
        new Entry(Set.of(id("spectre")), "camera.thermal_shock_reforged.catalog.spectre", false),
        new Entry(Set.of(id("spirit")), "camera.thermal_shock_reforged.catalog.spirit", false),
        new Entry(Set.of(id("effigy")), "camera.thermal_shock_reforged.catalog.effigy", false),
        new Entry(Set.of(id("assimilator")), "camera.thermal_shock_reforged.catalog.assimilator", false),
        new Entry(Set.of(id("assimilated_graver")), "camera.thermal_shock_reforged.catalog.assimilated_graver", false),
        new Entry(Set.of(id("razoraptor")), "camera.thermal_shock_reforged.catalog.razoraptor", false),
        new Entry(Set.of(id("monolith")), "camera.thermal_shock_reforged.catalog.monolith", false),
        new Entry(Set.of(id("dormant_monolith")), "camera.thermal_shock_reforged.catalog.dormant_monolith", false),
        new Entry(Set.of(id("herald")), "camera.thermal_shock_reforged.catalog.herald", false),
        new Entry(Set.of(id("squall")), "camera.thermal_shock_reforged.catalog.squall", false),
        new Entry(Set.of(id("flare")), "camera.thermal_shock_reforged.catalog.flare", false),
        new Entry(Set.of(id("infestophage_seed")), "camera.thermal_shock_reforged.catalog.infestophage_seed", false),
        new Entry(Set.of(id("infestophage")), "camera.thermal_shock_reforged.catalog.infestophage", false),
        new Entry(Set.of(id("mangler")), "camera.thermal_shock_reforged.catalog.mangler", false),
        new Entry(Set.of(id("blighted_spore")), "camera.thermal_shock_reforged.catalog.blighted_spore", false),
        new Entry(Set.of(id("angry_cavebloomer")), "camera.thermal_shock_reforged.catalog.angry_cavebloomer", true),
        new Entry(Set.of(id("budding_bloomer")), "camera.thermal_shock_reforged.catalog.budding_bloomer", false),
        new Entry(Set.of(id("dandybloomer")), "camera.thermal_shock_reforged.catalog.dandybloomer", false),
        new Entry(Set.of(id("icidrogunner")), "camera.thermal_shock_reforged.catalog.icidrogunner", false),
        new Entry(Set.of(id("icidroburner")), "camera.thermal_shock_reforged.catalog.icidroburner", false),
        new Entry(Set.of(id("whisper")), "camera.thermal_shock_reforged.catalog.whisper", false),
        new Entry(Set.of(id("shutterbug")), "camera.thermal_shock_reforged.catalog.shutterbug", false),
        new Entry(Set.of(id("florist")), "camera.thermal_shock_reforged.catalog.florist", false),
        new Entry(Set.of(id("kofoni")), "camera.thermal_shock_reforged.catalog.kofoni", false),
        new Entry(Set.of(id("benzor")), "camera.thermal_shock_reforged.catalog.benzor", false),
        new Entry(Set.of(id("concierge")), "camera.thermal_shock_reforged.catalog.concierge", false),
        new Entry(Set.of(id("defector_machinist"), id("lab_machinist")), "camera.thermal_shock_reforged.catalog.defector_machinist", false),
        new Entry(Set.of(id("huntress"), id("huntress_boss")), "camera.thermal_shock_reforged.catalog.huntress", false),
        new Entry(Set.of(id("ly")), "camera.thermal_shock_reforged.catalog.ly", false),
        new Entry(Set.of(id("laut")), "camera.thermal_shock_reforged.catalog.laut", false));
    private static Identifier id(String path) { return Identifier.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, path); }
    static void describe(ServerPlayer player, Set<Identifier> subjects) {
        for (Entry entry : ENTRIES) {
            if (Collections.disjoint(entry.types, subjects)) continue;
            player.sendSystemMessage(Component.translatable(entry.translationKey));
            if (entry.photoQuest) {
                var advancement = player.level().getServer().getAdvancements().get(id("leaf_it_to_me_2"));
                if (advancement == null) { ThermalShockReforged.LOGGER.warn("Camera photo advancement unavailable: leaf_it_to_me_2"); continue; }
                var progress = player.getAdvancements().getOrStartProgress(advancement);
                if (!progress.isDone()) for (String criterion : progress.getRemainingCriteria()) player.getAdvancements().award(advancement, criterion);
            }
        }
    }
    private record Entry(Set<Identifier> types, String translationKey, boolean photoQuest) { }
}
