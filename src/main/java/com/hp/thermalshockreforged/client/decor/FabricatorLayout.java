package com.hp.thermalshockreforged.client.decor;

import com.hp.thermalshockreforged.content.fabrication.FabricatorPage;
import java.util.List;

final class FabricatorLayout {
    record Label(String key, int x, int y, int color) { }
    record SpriteButton(int action, int x, int y, int width, int height, int u, int v, int hoverV, String texture, int sheetWidth, int sheetHeight, String label) { }
    record Layout(int width, int height, int backgroundX, String background, List<Label> labels, List<SpriteButton> buttons) { }
    static Layout forPage(FabricatorPage page) {
        return switch (page) {
            case TOOLS -> new Layout(240, 196, 32, "textures/screens/page.png", List.of(
                    new Label("gui.thermal_shock_reforged.fabic_guide_page_one.label_fabricator_guide", 29, -14, -1),
                    new Label("gui.thermal_shock_reforged.fabic_guide_page_one.label_tools", 137, -14, -1)), List.of(
                    new SpriteButton(0, 47, 13, 16, 16, 0, 0, 16, "textures/screens/atlas/imagebutton_ghostglass_pickaxe.png", 16, 32, "gui.thermal_shock_reforged.ghostglass_pickaxe_page.label_tools"),
                    new SpriteButton(1, 2, 85, 18, 18, 0, 0, 18, "textures/screens/atlas/imagebutton_cobaltleftarrow.png", 18, 36, "gui.thermal_shock_reforged.fabric_guide_page_three.label_tools"),
                    new SpriteButton(2, 218, 85, 18, 18, 0, 0, 18, "textures/screens/atlas/imagebutton_cobaltrightarrow.png", 18, 36, "gui.thermal_shock_reforged.fabic_guide_page_two.label_tools"),
                    new SpriteButton(3, 47, 31, 15, 15, 0, 0, 15, "textures/screens/atlas/imagebutton_ferrousaltar.png", 15, 30, "gui.thermal_shock_reforged.ferrous_altar_page.label_tools"),
                    new SpriteButton(4, 65, 31, 16, 16, 0, 0, 16, "textures/screens/atlas/imagebutton_trans.png", 16, 32, "gui.thermal_shock_reforged.transmutator_page.label_tools"),
                    new SpriteButton(5, 47, 49, 16, 16, 0, 0, 16, "textures/screens/atlas/imagebutton_thingamabob.png", 16, 32, "gui.thermal_shock_reforged.thingamabob_page.label_tools"),
                    new SpriteButton(6, 65, 13, 16, 16, 0, 0, 16, "textures/screens/atlas/imagebutton_stratus_drill.png", 16, 32, "gui.thermal_shock_reforged.stratus_drill_page.label_tools"),
                    new SpriteButton(7, 83, 13, 16, 16, 0, 0, 16, "textures/screens/atlas/imagebutton_stratus_saw.png", 16, 32, "gui.thermal_shock_reforged.stratus_chainsaw_page.label_tools"),
                    new SpriteButton(8, 101, 13, 16, 16, 0, 0, 16, "textures/screens/atlas/imagebutton_stratus_auger.png", 16, 32, "gui.thermal_shock_reforged.stratus_auger_page.label_tools"),
                    new SpriteButton(9, 83, 31, 16, 16, 0, 0, 16, "textures/screens/atlas/imagebutton_extractor_icon.png", 16, 32, "gui.thermal_shock_reforged.extractor_page.label_tools")));
            case WEAPONS -> new Layout(240, 196, 32, "textures/screens/page.png", List.of(
                    new Label("gui.thermal_shock_reforged.fabic_guide_page_two.label_fabricator_guide", 29, -14, -1),
                    new Label("gui.thermal_shock_reforged.fabic_guide_page_two.label_tools", 173, -14, -1)), List.of(
                    new SpriteButton(0, 2, 85, 18, 18, 0, 0, 18, "textures/screens/atlas/imagebutton_cobaltleftarrow.png", 18, 36, "gui.thermal_shock_reforged.fabic_guide_page_one.label_tools"),
                    new SpriteButton(1, 218, 85, 18, 18, 0, 0, 18, "textures/screens/atlas/imagebutton_cobaltrightarrow.png", 18, 36, "gui.thermal_shock_reforged.fabric_guide_page_three.label_tools"),
                    new SpriteButton(2, 47, 31, 16, 16, 0, 0, 16, "textures/screens/atlas/imagebutton_flintsteelrifle.png", 16, 32, "gui.thermal_shock_reforged.flintsteel_rifle_page.label_tools"),
                    new SpriteButton(3, 65, 31, 16, 16, 0, 0, 16, "textures/screens/atlas/imagebutton_volcano.png", 16, 32, "gui.thermal_shock_reforged.volcano_page.label_tools"),
                    new SpriteButton(4, 83, 31, 16, 16, 0, 0, 16, "textures/screens/atlas/imagebutton_hailfall.png", 16, 32, "gui.thermal_shock_reforged.hailstorm_page.label_tools"),
                    new SpriteButton(5, 101, 31, 16, 16, 0, 0, 16, "textures/screens/atlas/imagebutton_magnetic_crossbow.png", 16, 32, "gui.thermal_shock_reforged.magnetic_crossbow_page.label_tools"),
                    new SpriteButton(6, 47, 13, 16, 16, 0, 0, 16, "textures/screens/atlas/imagebutton_zerker_gauntlet.png", 16, 32, "gui.thermal_shock_reforged.zerker_gauntlet_page.label_tools"),
                    new SpriteButton(7, 137, 31, 16, 16, 0, 0, 16, "textures/screens/atlas/imagebutton_thermoblastericon.png", 16, 32, "gui.thermal_shock_reforged.thermoblaster_page.label_tools"),
                    new SpriteButton(8, 65, 49, 16, 16, 0, 0, 16, "textures/screens/atlas/imagebutton_gel_tentacle.png", 16, 32, "gui.thermal_shock_reforged.amaljelly_canister_page.label_tools"),
                    new SpriteButton(9, 101, 13, 16, 16, 0, 0, 16, "textures/screens/atlas/imagebutton_shock_stick.png", 16, 32, "gui.thermal_shock_reforged.shock_stick_page.label_tools"),
                    new SpriteButton(10, 119, 13, 16, 16, 0, 0, 16, "textures/screens/atlas/imagebutton_knights_dredge.png", 16, 32, "gui.thermal_shock_reforged.knights_edge_page.label_tools"),
                    new SpriteButton(11, 83, 13, 16, 16, 0, 0, 16, "textures/screens/atlas/imagebutton_hi_volt_spear.png", 16, 32, "gui.thermal_shock_reforged.hi_volt_lance_page.label_tools"),
                    new SpriteButton(12, 65, 13, 16, 16, 0, 0, 16, "textures/screens/atlas/imagebutton_buzzsaw_icon.png", 16, 32, "gui.thermal_shock_reforged.buzzsaw_page.label_tools"),
                    new SpriteButton(13, 47, 49, 16, 16, 0, 0, 16, "textures/screens/atlas/imagebutton_liteprism_disk.png", 16, 32, "gui.thermal_shock_reforged.liteprism_disk_page.label_tools"),
                    new SpriteButton(14, 119, 31, 16, 16, 0, 0, 16, "textures/screens/atlas/imagebutton_liteprism_laser_icon.png", 16, 32, "gui.thermal_shock_reforged.liteprism_laser_page.label_tools"),
                    new SpriteButton(15, 83, 49, 16, 16, 0, 0, 16, "textures/screens/atlas/imagebutton_liteprism_sword.png", 16, 32, "gui.thermal_shock_reforged.liteprism_sword_page.label_tools")));
            case ARMOR -> new Layout(240, 196, 32, "textures/screens/page.png", List.of(
                    new Label("gui.thermal_shock_reforged.fabric_guide_page_three.label_fabricator_guide", 29, -14, -1),
                    new Label("gui.thermal_shock_reforged.fabric_guide_page_three.label_tools", 182, -14, -1)), List.of(
                    new SpriteButton(0, 2, 85, 18, 18, 0, 0, 18, "textures/screens/atlas/imagebutton_cobaltleftarrow.png", 18, 36, "gui.thermal_shock_reforged.fabic_guide_page_two.label_tools"),
                    new SpriteButton(1, 218, 85, 18, 18, 0, 0, 18, "textures/screens/atlas/imagebutton_cobaltrightarrow.png", 18, 36, "gui.thermal_shock_reforged.fabic_guide_page_one.label_tools"),
                    new SpriteButton(2, 47, 13, 16, 16, 0, 0, 16, "textures/screens/atlas/imagebutton_sentry_hood.png", 16, 32, "gui.thermal_shock_reforged.sentry_hood_page.label_tools"),
                    new SpriteButton(3, 65, 13, 16, 16, 0, 0, 16, "textures/screens/atlas/imagebutton_sentry_pauldrons.png", 16, 32, "gui.thermal_shock_reforged.sentry_pauldrons_page.label_tools"),
                    new SpriteButton(4, 83, 13, 16, 16, 0, 0, 16, "textures/screens/atlas/imagebutton_voidflarescarfitem.png", 16, 32, "gui.thermal_shock_reforged.voidflare_scarf_page.label_tools"),
                    new SpriteButton(5, 101, 13, 16, 16, 0, 0, 16, "textures/screens/atlas/imagebutton_corrial_diving_helmet.png", 16, 32, "gui.thermal_shock_reforged.cor_div_helmet_page.label_tools"),
                    new SpriteButton(6, 119, 13, 16, 16, 0, 0, 16, "textures/screens/atlas/imagebutton_spark_striders_icon.png", 16, 32, "gui.thermal_shock_reforged.propulsion_boots_page.label_tools"),
                    new SpriteButton(7, 47, 49, 16, 16, 0, 0, 16, "textures/screens/atlas/imagebutton_corrial_glastrekker_armor.png", 16, 32, "gui.thermal_shock_reforged.glastrekker_armor_page.label_tools"),
                    new SpriteButton(8, 47, 31, 16, 16, 0, 0, 16, "textures/screens/atlas/imagebutton_strange_hat.png", 16, 32, "gui.thermal_shock_reforged.strange_hat_page.label_tools"),
                    new SpriteButton(9, 65, 31, 16, 16, 0, 0, 16, "textures/screens/atlas/imagebutton_strange_cloak.png", 16, 32, "gui.thermal_shock_reforged.strange_cloack_page.label_tools"),
                    new SpriteButton(10, 137, 13, 16, 16, 0, 0, 16, "textures/screens/atlas/imagebutton_silenced_helmet.png", 16, 32, "gui.thermal_shock_reforged.silenced_helmet_page.label_tools"),
                    new SpriteButton(11, 155, 13, 16, 16, 0, 0, 16, "textures/screens/atlas/imagebutton_silenced_cloak.png", 16, 32, "gui.thermal_shock_reforged.silenced_chestplate_page.label_tools")));
            case GHOSTGLASS_PICK -> new Layout(246, 163, 0, "textures/screens/fabricator_panel.png", List.of(
                    new Label("gui.thermal_shock_reforged.ghostglass_pickaxe_page.label_fabricator_guide", 5, -13, -1),
                    new Label("gui.thermal_shock_reforged.ghostglass_pickaxe_page.label_tools", 149, -13, -1),
                    new Label("gui.thermal_shock_reforged.ghostglass_pickaxe_page.label_slot_1_cryonic_alloy_x5", 14, 14, -1),
                    new Label("gui.thermal_shock_reforged.ghostglass_pickaxe_page.label_slot_2_cobalt_block_x2", 14, 23, -1),
                    new Label("gui.thermal_shock_reforged.ghostglass_pickaxe_page.label_slot_3_polished_permafrost_x4", 14, 32, -1),
                    new Label("gui.thermal_shock_reforged.ghostglass_pickaxe_page.label_slot_4_iron_ingot_x10", 14, 41, -1)), List.of(
                    new SpriteButton(0, 174, 38, 40, 18, 0, 0, 18, "textures/screens/atlas/imagebutton_fabricator_craft_button.png", 40, 36, "gui.thermal_shock_reforged.fabricator.craft")));
            case FERROUS_ALTAR -> new Layout(246, 163, 0, "textures/screens/fabricator_panel.png", List.of(
                    new Label("gui.thermal_shock_reforged.ferrous_altar_page.label_fabricator_guide", 5, -13, -1),
                    new Label("gui.thermal_shock_reforged.ferrous_altar_page.label_tools", 176, -13, -1),
                    new Label("gui.thermal_shock_reforged.ferrous_altar_page.label_slot_1_cryonic_alloy_x5", 14, 14, -1),
                    new Label("gui.thermal_shock_reforged.ferrous_altar_page.label_slot_2_cobalt_block_x2", 14, 23, -1),
                    new Label("gui.thermal_shock_reforged.ferrous_altar_page.label_slot_3_polished_permafrost_x4", 14, 32, -1),
                    new Label("gui.thermal_shock_reforged.ferrous_altar_page.label_slot_4_iron_ingot_x10", 14, 41, -1)), List.of(
                    new SpriteButton(0, 174, 38, 40, 18, 0, 0, 18, "textures/screens/atlas/imagebutton_fabricator_craft_button.png", 40, 36, "gui.thermal_shock_reforged.fabricator.craft")));
            case TRANSMUTATOR -> new Layout(246, 163, 0, "textures/screens/fabricator_panel.png", List.of(
                    new Label("gui.thermal_shock_reforged.transmutator_page.label_fabricator_guide", 5, -13, -1),
                    new Label("gui.thermal_shock_reforged.transmutator_page.label_tools", 185, -13, -1),
                    new Label("gui.thermal_shock_reforged.transmutator_page.label_slot_1_cryonic_alloy_x5", 14, 14, -1),
                    new Label("gui.thermal_shock_reforged.transmutator_page.label_slot_2_cobalt_block_x2", 14, 23, -1),
                    new Label("gui.thermal_shock_reforged.transmutator_page.label_slot_3_polished_permafrost_x4", 14, 32, -1),
                    new Label("gui.thermal_shock_reforged.transmutator_page.label_slot_4_iron_ingot_x10", 14, 41, -1)), List.of(
                    new SpriteButton(0, 174, 38, 40, 18, 0, 0, 18, "textures/screens/atlas/imagebutton_fabricator_craft_button.png", 40, 36, "gui.thermal_shock_reforged.fabricator.craft")));
            case THINGAMABOB -> new Layout(246, 163, 0, "textures/screens/fabricator_panel.png", List.of(
                    new Label("gui.thermal_shock_reforged.thingamabob_page.label_fabricator_guide", 5, -13, -1),
                    new Label("gui.thermal_shock_reforged.thingamabob_page.label_tools", 185, -13, -1),
                    new Label("gui.thermal_shock_reforged.thingamabob_page.label_slot_1_cryonic_alloy_x5", 14, 14, -1),
                    new Label("gui.thermal_shock_reforged.thingamabob_page.label_slot_3_polished_permafrost_x4", 14, 23, -1)), List.of(
                    new SpriteButton(0, 174, 38, 40, 18, 0, 0, 18, "textures/screens/atlas/imagebutton_fabricator_craft_button.png", 40, 36, "gui.thermal_shock_reforged.fabricator.craft")));
            case STRATOSILVER_DRILL -> new Layout(246, 163, 0, "textures/screens/fabricator_panel.png", List.of(
                    new Label("gui.thermal_shock_reforged.stratus_drill_page.label_fabricator_guide", 5, -13, -1),
                    new Label("gui.thermal_shock_reforged.stratus_drill_page.label_tools", 176, -13, -1),
                    new Label("gui.thermal_shock_reforged.stratus_drill_page.label_slot_1_cryonic_alloy_x5", 14, 14, -1),
                    new Label("gui.thermal_shock_reforged.stratus_drill_page.label_slot_3_polished_permafrost_x4", 14, 23, -1),
                    new Label("gui.thermal_shock_reforged.stratus_drill_page.label_bloodstone_sword", 14, 32, -1),
                    new Label("gui.thermal_shock_reforged.stratus_drill_page.label_ghostglass_sword", 14, 41, -1)), List.of(
                    new SpriteButton(0, 174, 38, 40, 18, 0, 0, 18, "textures/screens/atlas/imagebutton_fabricator_craft_button.png", 40, 36, "gui.thermal_shock_reforged.fabricator.craft")));
            case STRATOSILVER_CHAINSAW -> new Layout(246, 163, 0, "textures/screens/fabricator_panel.png", List.of(
                    new Label("gui.thermal_shock_reforged.stratus_chainsaw_page.label_fabricator_guide", 5, -13, -1),
                    new Label("gui.thermal_shock_reforged.stratus_chainsaw_page.label_tools", 158, -13, -1),
                    new Label("gui.thermal_shock_reforged.stratus_chainsaw_page.label_slot_1_cryonic_alloy_x5", 14, 14, -1),
                    new Label("gui.thermal_shock_reforged.stratus_chainsaw_page.label_slot_3_polished_permafrost_x4", 14, 23, -1),
                    new Label("gui.thermal_shock_reforged.stratus_chainsaw_page.label_bloodstone_sword", 14, 32, -1),
                    new Label("gui.thermal_shock_reforged.stratus_chainsaw_page.label_ghostglass_sword", 14, 41, -1)), List.of(
                    new SpriteButton(0, 174, 38, 40, 18, 0, 0, 18, "textures/screens/atlas/imagebutton_fabricator_craft_button.png", 40, 36, "gui.thermal_shock_reforged.fabricator.craft")));
            case STRATOSILVER_AUGER -> new Layout(246, 163, 0, "textures/screens/fabricator_panel.png", List.of(
                    new Label("gui.thermal_shock_reforged.stratus_auger_page.label_fabricator_guide", 5, -13, -1),
                    new Label("gui.thermal_shock_reforged.stratus_auger_page.label_tools", 176, -13, -1),
                    new Label("gui.thermal_shock_reforged.stratus_auger_page.label_slot_1_cryonic_alloy_x5", 14, 14, -1),
                    new Label("gui.thermal_shock_reforged.stratus_auger_page.label_slot_3_polished_permafrost_x4", 14, 23, -1),
                    new Label("gui.thermal_shock_reforged.stratus_auger_page.label_bloodstone_sword", 14, 32, -1),
                    new Label("gui.thermal_shock_reforged.stratus_auger_page.label_ghostglass_sword", 14, 41, -1)), List.of(
                    new SpriteButton(0, 174, 38, 40, 18, 0, 0, 18, "textures/screens/atlas/imagebutton_fabricator_craft_button.png", 40, 36, "gui.thermal_shock_reforged.fabricator.craft")));
            case EXTRACTOR_CONSTRUCTOR -> new Layout(246, 163, 0, "textures/screens/fabricator_panel.png", List.of(
                    new Label("gui.thermal_shock_reforged.extractor_page.label_fabricator_guide", 5, -13, -1),
                    new Label("gui.thermal_shock_reforged.extractor_page.label_tools", 199, -13, -1),
                    new Label("gui.thermal_shock_reforged.extractor_page.label_slot_1_cryonic_alloy_x5", 14, 14, -1),
                    new Label("gui.thermal_shock_reforged.extractor_page.label_slot_2_cobalt_block_x2", 14, 23, -1),
                    new Label("gui.thermal_shock_reforged.extractor_page.label_slot_3_polished_permafrost_x4", 14, 32, -1),
                    new Label("gui.thermal_shock_reforged.extractor_page.label_slot_4_iron_ingot_x10", 14, 41, -1)), List.of(
                    new SpriteButton(0, 174, 38, 40, 18, 0, 0, 18, "textures/screens/atlas/imagebutton_fabricator_craft_button.png", 40, 36, "gui.thermal_shock_reforged.fabricator.craft")));
            case FLINTSTEEL_RIFLE -> new Layout(246, 163, 0, "textures/screens/fabricator_panel.png", List.of(
                    new Label("gui.thermal_shock_reforged.flintsteel_rifle_page.label_fabricator_guide", 5, -13, -1),
                    new Label("gui.thermal_shock_reforged.flintsteel_rifle_page.label_tools", 158, -13, -1),
                    new Label("gui.thermal_shock_reforged.flintsteel_rifle_page.label_slot_1_cryonic_alloy_x5", 14, 14, -1),
                    new Label("gui.thermal_shock_reforged.flintsteel_rifle_page.label_slot_2_cobalt_block_x2", 14, 23, -1),
                    new Label("gui.thermal_shock_reforged.flintsteel_rifle_page.label_slot_3_polished_permafrost_x4", 14, 32, -1),
                    new Label("gui.thermal_shock_reforged.flintsteel_rifle_page.label_slot_4_iron_ingot_x10", 14, 41, -1)), List.of(
                    new SpriteButton(0, 174, 38, 40, 18, 0, 0, 18, "textures/screens/atlas/imagebutton_fabricator_craft_button.png", 40, 36, "gui.thermal_shock_reforged.fabricator.craft")));
            case VOLCANO -> new Layout(246, 163, 0, "textures/screens/fabricator_panel.png", List.of(
                    new Label("gui.thermal_shock_reforged.volcano_page.label_fabricator_guide", 5, -13, -1),
                    new Label("gui.thermal_shock_reforged.volcano_page.label_tools", 212, -13, -1),
                    new Label("gui.thermal_shock_reforged.volcano_page.label_slot_1_cryonic_alloy_x5", 14, 14, -1),
                    new Label("gui.thermal_shock_reforged.volcano_page.label_slot_2_cobalt_block_x2", 14, 23, -1),
                    new Label("gui.thermal_shock_reforged.volcano_page.label_slot_3_polished_permafrost_x4", 14, 32, -1),
                    new Label("gui.thermal_shock_reforged.volcano_page.label_slot_4_iron_ingot_x10", 14, 41, -1)), List.of(
                    new SpriteButton(0, 174, 38, 40, 18, 0, 0, 18, "textures/screens/atlas/imagebutton_fabricator_craft_button.png", 40, 36, "gui.thermal_shock_reforged.fabricator.craft")));
            case HAILFALL -> new Layout(246, 163, 0, "textures/screens/fabricator_panel.png", List.of(
                    new Label("gui.thermal_shock_reforged.hailstorm_page.label_fabricator_guide", 5, -13, -1),
                    new Label("gui.thermal_shock_reforged.hailstorm_page.label_tools", 194, -13, -1),
                    new Label("gui.thermal_shock_reforged.hailstorm_page.label_slot_1_cryonic_alloy_x5", 14, 14, -1),
                    new Label("gui.thermal_shock_reforged.hailstorm_page.label_slot_2_cobalt_block_x2", 14, 23, -1),
                    new Label("gui.thermal_shock_reforged.hailstorm_page.label_slot_3_polished_permafrost_x4", 14, 32, -1),
                    new Label("gui.thermal_shock_reforged.hailstorm_page.label_slot_4_iron_ingot_x10", 14, 41, -1)), List.of(
                    new SpriteButton(0, 174, 38, 40, 18, 0, 0, 18, "textures/screens/atlas/imagebutton_fabricator_craft_button.png", 40, 36, "gui.thermal_shock_reforged.fabricator.craft")));
            case MAGNETIC_CROSSBOW -> new Layout(246, 163, 0, "textures/screens/fabricator_panel.png", List.of(
                    new Label("gui.thermal_shock_reforged.magnetic_crossbow_page.label_fabricator_guide", 5, -13, -1),
                    new Label("gui.thermal_shock_reforged.magnetic_crossbow_page.label_tools", 158, -13, -1),
                    new Label("gui.thermal_shock_reforged.magnetic_crossbow_page.label_slot_1_cryonic_alloy_x5", 14, 14, -1),
                    new Label("gui.thermal_shock_reforged.magnetic_crossbow_page.label_slot_2_cobalt_block_x2", 14, 23, -1),
                    new Label("gui.thermal_shock_reforged.magnetic_crossbow_page.label_slot_3_polished_permafrost_x4", 14, 32, -1),
                    new Label("gui.thermal_shock_reforged.magnetic_crossbow_page.label_slot_4_iron_ingot_x10", 14, 41, -1)), List.of(
                    new SpriteButton(0, 174, 38, 40, 18, 0, 0, 18, "textures/screens/atlas/imagebutton_fabricator_craft_button.png", 40, 36, "gui.thermal_shock_reforged.fabricator.craft")));
            case TITANIUM_MACE -> new Layout(246, 163, 0, "textures/screens/fabricator_panel.png", List.of(
                    new Label("gui.thermal_shock_reforged.zerker_gauntlet_page.label_fabricator_guide", 5, -13, -1),
                    new Label("gui.thermal_shock_reforged.zerker_gauntlet_page.label_tools", 176, -13, -1),
                    new Label("gui.thermal_shock_reforged.zerker_gauntlet_page.label_slot_1_cryonic_alloy_x5", 14, 14, -1),
                    new Label("gui.thermal_shock_reforged.zerker_gauntlet_page.label_slot_2_cobalt_block_x2", 14, 23, -1),
                    new Label("gui.thermal_shock_reforged.zerker_gauntlet_page.label_slot_3_polished_permafrost_x4", 14, 32, -1),
                    new Label("gui.thermal_shock_reforged.zerker_gauntlet_page.label_slot_4_iron_ingot_x10", 14, 41, -1)), List.of(
                    new SpriteButton(0, 174, 38, 40, 18, 0, 0, 18, "textures/screens/atlas/imagebutton_fabricator_craft_button.png", 40, 36, "gui.thermal_shock_reforged.fabricator.craft")));
            case COBALT_THERMOBLASTER -> new Layout(246, 163, 0, "textures/screens/fabricator_panel.png", List.of(
                    new Label("gui.thermal_shock_reforged.thermoblaster_page.label_fabricator_guide", 5, -13, -1),
                    new Label("gui.thermal_shock_reforged.thermoblaster_page.label_tools", 176, -13, -1),
                    new Label("gui.thermal_shock_reforged.thermoblaster_page.label_slot_1_cryonic_alloy_x5", 14, 14, -1),
                    new Label("gui.thermal_shock_reforged.thermoblaster_page.label_slot_2_cobalt_block_x2", 14, 32, -1),
                    new Label("gui.thermal_shock_reforged.thermoblaster_page.label_slot_3_polished_permafrost_x4", 14, 23, -1),
                    new Label("gui.thermal_shock_reforged.thermoblaster_page.label_slag_x1", 14, 41, -1)), List.of(
                    new SpriteButton(0, 174, 38, 40, 18, 0, 0, 18, "textures/screens/atlas/imagebutton_fabricator_craft_button.png", 40, 36, "gui.thermal_shock_reforged.fabricator.craft")));
            case AMALJELLY_CANISTER -> new Layout(246, 163, 0, "textures/screens/fabricator_panel.png", List.of(
                    new Label("gui.thermal_shock_reforged.amaljelly_canister_page.label_fabricator_guide", 5, -13, -1),
                    new Label("gui.thermal_shock_reforged.amaljelly_canister_page.label_tools", 149, -13, -1),
                    new Label("gui.thermal_shock_reforged.amaljelly_canister_page.label_slot_1_cryonic_alloy_x5", 14, 23, -1),
                    new Label("gui.thermal_shock_reforged.amaljelly_canister_page.label_slot_3_polished_permafrost_x4", 14, 32, -1),
                    new Label("gui.thermal_shock_reforged.amaljelly_canister_page.label_corrial_ingot_x8", 14, 14, -1)), List.of(
                    new SpriteButton(0, 174, 38, 40, 18, 0, 0, 18, "textures/screens/atlas/imagebutton_fabricator_craft_button.png", 40, 36, "gui.thermal_shock_reforged.fabricator.craft")));
            case SHOCK_STICK -> new Layout(246, 163, 0, "textures/screens/fabricator_panel.png", List.of(
                    new Label("gui.thermal_shock_reforged.shock_stick_page.label_fabricator_guide", 5, -13, -1),
                    new Label("gui.thermal_shock_reforged.shock_stick_page.label_tools", 185, -13, -1),
                    new Label("gui.thermal_shock_reforged.shock_stick_page.label_slot_1_cryonic_alloy_x5", 14, 14, -1),
                    new Label("gui.thermal_shock_reforged.shock_stick_page.label_slot_2_cobalt_block_x2", 14, 32, -1),
                    new Label("gui.thermal_shock_reforged.shock_stick_page.label_slot_3_polished_permafrost_x4", 14, 23, -1)), List.of(
                    new SpriteButton(0, 174, 38, 40, 18, 0, 0, 18, "textures/screens/atlas/imagebutton_fabricator_craft_button.png", 40, 36, "gui.thermal_shock_reforged.fabricator.craft")));
            case KNIGHTS_EDGE -> new Layout(246, 163, 0, "textures/screens/fabricator_panel.png", List.of(
                    new Label("gui.thermal_shock_reforged.knights_edge_page.label_fabricator_guide", 5, -13, -1),
                    new Label("gui.thermal_shock_reforged.knights_edge_page.label_tools", 176, -13, -1),
                    new Label("gui.thermal_shock_reforged.knights_edge_page.label_slot_1_cryonic_alloy_x5", 14, 14, -1),
                    new Label("gui.thermal_shock_reforged.knights_edge_page.label_slot_3_polished_permafrost_x4", 14, 23, -1),
                    new Label("gui.thermal_shock_reforged.knights_edge_page.label_bloodstone_sword", 14, 32, -1),
                    new Label("gui.thermal_shock_reforged.knights_edge_page.label_ghostglass_sword", 14, 41, -1),
                    new Label("gui.thermal_shock_reforged.knights_edge_page.label_stellar_alloy_x1", 14, 50, -1),
                    new Label("gui.thermal_shock_reforged.knights_edge_page.label_zenith_smithing_template", 14, 59, -1)), List.of(
                    new SpriteButton(0, 174, 38, 40, 18, 0, 0, 18, "textures/screens/atlas/imagebutton_fabricator_craft_button.png", 40, 36, "gui.thermal_shock_reforged.fabricator.craft")));
            case HI_VOLT_LANCE -> new Layout(246, 163, 0, "textures/screens/fabricator_panel.png", List.of(
                    new Label("gui.thermal_shock_reforged.hi_volt_lance_page.label_fabricator_guide", 5, -13, -1),
                    new Label("gui.thermal_shock_reforged.hi_volt_lance_page.label_tools", 176, -13, -1),
                    new Label("gui.thermal_shock_reforged.hi_volt_lance_page.label_slot_1_cryonic_alloy_x5", 14, 14, -1),
                    new Label("gui.thermal_shock_reforged.hi_volt_lance_page.label_slot_3_polished_permafrost_x4", 14, 23, -1),
                    new Label("gui.thermal_shock_reforged.hi_volt_lance_page.label_bloodstone_sword", 14, 32, -1),
                    new Label("gui.thermal_shock_reforged.hi_volt_lance_page.label_ghostglass_sword", 14, 41, -1)), List.of(
                    new SpriteButton(0, 174, 38, 40, 18, 0, 0, 18, "textures/screens/atlas/imagebutton_fabricator_craft_button.png", 40, 36, "gui.thermal_shock_reforged.fabricator.craft")));
            case BUZZSAW -> new Layout(246, 163, 0, "textures/screens/fabricator_panel.png", List.of(
                    new Label("gui.thermal_shock_reforged.buzzsaw_page.label_fabricator_guide", 5, -13, -1),
                    new Label("gui.thermal_shock_reforged.buzzsaw_page.label_tools", 211, -13, -1),
                    new Label("gui.thermal_shock_reforged.buzzsaw_page.label_slot_1_cryonic_alloy_x5", 14, 14, -1),
                    new Label("gui.thermal_shock_reforged.buzzsaw_page.label_slot_2_cobalt_block_x2", 14, 23, -1),
                    new Label("gui.thermal_shock_reforged.buzzsaw_page.label_slot_3_polished_permafrost_x4", 14, 32, -1)), List.of(
                    new SpriteButton(0, 174, 38, 40, 18, 0, 0, 18, "textures/screens/atlas/imagebutton_fabricator_craft_button.png", 40, 36, "gui.thermal_shock_reforged.fabricator.craft")));
            case LITEPRISM_SWORD -> new Layout(246, 163, 0, "textures/screens/fabricator_panel.png", List.of(
                    new Label("gui.thermal_shock_reforged.liteprism_sword_page.label_fabricator_guide", 5, -13, -1),
                    new Label("gui.thermal_shock_reforged.liteprism_sword_page.label_tools", 167, -13, -1),
                    new Label("gui.thermal_shock_reforged.liteprism_sword_page.label_slot_1_cryonic_alloy_x5", 14, 14, -1),
                    new Label("gui.thermal_shock_reforged.liteprism_sword_page.label_slot_2_cobalt_block_x2", 14, 23, -1),
                    new Label("gui.thermal_shock_reforged.liteprism_sword_page.label_slot_3_polished_permafrost_x4", 14, 32, -1),
                    new Label("gui.thermal_shock_reforged.liteprism_sword_page.label_slot_4_iron_ingot_x10", 14, 41, -1)), List.of(
                    new SpriteButton(0, 174, 38, 40, 18, 0, 0, 18, "textures/screens/atlas/imagebutton_fabricator_craft_button.png", 40, 36, "gui.thermal_shock_reforged.fabricator.craft")));
            case LITEPRISM_DISK -> new Layout(246, 163, 0, "textures/screens/fabricator_panel.png", List.of(
                    new Label("gui.thermal_shock_reforged.liteprism_disk_page.label_fabricator_guide", 5, -13, -1),
                    new Label("gui.thermal_shock_reforged.liteprism_disk_page.label_tools", 167, -13, -1),
                    new Label("gui.thermal_shock_reforged.liteprism_disk_page.label_slot_1_cryonic_alloy_x5", 14, 14, -1),
                    new Label("gui.thermal_shock_reforged.liteprism_disk_page.label_slot_2_cobalt_block_x2", 14, 23, -1),
                    new Label("gui.thermal_shock_reforged.liteprism_disk_page.label_slot_3_polished_permafrost_x4", 14, 32, -1),
                    new Label("gui.thermal_shock_reforged.liteprism_disk_page.label_slot_4_iron_ingot_x10", 14, 41, -1)), List.of(
                    new SpriteButton(0, 174, 38, 40, 18, 0, 0, 18, "textures/screens/atlas/imagebutton_fabricator_craft_button.png", 40, 36, "gui.thermal_shock_reforged.fabricator.craft")));
            case LITEPRISM_LASER -> new Layout(246, 163, 0, "textures/screens/fabricator_panel.png", List.of(
                    new Label("gui.thermal_shock_reforged.liteprism_laser_page.label_fabricator_guide", 5, -13, -1),
                    new Label("gui.thermal_shock_reforged.liteprism_laser_page.label_tools", 158, -13, -1),
                    new Label("gui.thermal_shock_reforged.liteprism_laser_page.label_slot_1_cryonic_alloy_x5", 14, 14, -1),
                    new Label("gui.thermal_shock_reforged.liteprism_laser_page.label_slot_2_cobalt_block_x2", 14, 23, -1),
                    new Label("gui.thermal_shock_reforged.liteprism_laser_page.label_slot_3_polished_permafrost_x4", 14, 32, -1),
                    new Label("gui.thermal_shock_reforged.liteprism_laser_page.label_slot_4_iron_ingot_x10", 14, 41, -1)), List.of(
                    new SpriteButton(0, 174, 38, 40, 18, 0, 0, 18, "textures/screens/atlas/imagebutton_fabricator_craft_button.png", 40, 36, "gui.thermal_shock_reforged.fabricator.craft")));
            case SENTRY_HELMET -> new Layout(246, 163, 0, "textures/screens/fabricator_panel.png", List.of(
                    new Label("gui.thermal_shock_reforged.sentry_hood_page.label_fabricator_guide", 5, -13, -1),
                    new Label("gui.thermal_shock_reforged.sentry_hood_page.label_tools", 185, -13, -1),
                    new Label("gui.thermal_shock_reforged.sentry_hood_page.label_slot_1_cryonic_alloy_x5", 14, 14, -1),
                    new Label("gui.thermal_shock_reforged.sentry_hood_page.label_slot_2_cobalt_block_x2", 14, 23, -1),
                    new Label("gui.thermal_shock_reforged.sentry_hood_page.label_slot_3_polished_permafrost_x4", 14, 32, -1),
                    new Label("gui.thermal_shock_reforged.sentry_hood_page.label_slot_4_iron_ingot_x10", 14, 41, -1)), List.of(
                    new SpriteButton(0, 174, 38, 40, 18, 0, 0, 18, "textures/screens/atlas/imagebutton_fabricator_craft_button.png", 40, 36, "gui.thermal_shock_reforged.fabricator.craft")));
            case SENTRY_CHESTPLATE -> new Layout(246, 163, 0, "textures/screens/fabricator_panel.png", List.of(
                    new Label("gui.thermal_shock_reforged.sentry_pauldrons_page.label_fabricator_guide", 5, -13, -1),
                    new Label("gui.thermal_shock_reforged.sentry_pauldrons_page.label_tools", 158, -13, -1),
                    new Label("gui.thermal_shock_reforged.sentry_pauldrons_page.label_slot_1_cryonic_alloy_x5", 14, 14, -1),
                    new Label("gui.thermal_shock_reforged.sentry_pauldrons_page.label_slot_2_cobalt_block_x2", 14, 23, -1),
                    new Label("gui.thermal_shock_reforged.sentry_pauldrons_page.label_slot_3_polished_permafrost_x4", 14, 32, -1),
                    new Label("gui.thermal_shock_reforged.sentry_pauldrons_page.label_slot_4_iron_ingot_x10", 14, 41, -1)), List.of(
                    new SpriteButton(0, 174, 38, 40, 18, 0, 0, 18, "textures/screens/atlas/imagebutton_fabricator_craft_button.png", 40, 36, "gui.thermal_shock_reforged.fabricator.craft")));
            case DEV_SCARF_CHESTPLATE -> new Layout(246, 163, 0, "textures/screens/fabricator_panel.png", List.of(
                    new Label("gui.thermal_shock_reforged.voidflare_scarf_page.label_fabricator_guide", 5, -13, -1),
                    new Label("gui.thermal_shock_reforged.voidflare_scarf_page.label_tools", 167, -13, -1),
                    new Label("gui.thermal_shock_reforged.voidflare_scarf_page.label_slot_1_cryonic_alloy_x5", 14, 14, -1),
                    new Label("gui.thermal_shock_reforged.voidflare_scarf_page.label_slot_2_cobalt_block_x2", 14, 23, -1),
                    new Label("gui.thermal_shock_reforged.voidflare_scarf_page.label_slot_3_polished_permafrost_x4", 14, 32, -1),
                    new Label("gui.thermal_shock_reforged.voidflare_scarf_page.label_slot_4_iron_ingot_x10", 14, 41, -1)), List.of(
                    new SpriteButton(0, 174, 38, 40, 18, 0, 0, 18, "textures/screens/atlas/imagebutton_fabricator_craft_button.png", 40, 36, "gui.thermal_shock_reforged.fabricator.craft")));
            case CORRIAL_DIVING_HELMET_HELMET -> new Layout(246, 163, 0, "textures/screens/fabricator_panel.png", List.of(
                    new Label("gui.thermal_shock_reforged.cor_div_helmet_page.label_fabricator_guide", 5, -13, -1),
                    new Label("gui.thermal_shock_reforged.cor_div_helmet_page.label_tools", 140, -13, -1),
                    new Label("gui.thermal_shock_reforged.cor_div_helmet_page.label_slot_1_cryonic_alloy_x5", 14, 14, -1),
                    new Label("gui.thermal_shock_reforged.cor_div_helmet_page.label_slot_2_cobalt_block_x2", 14, 32, -1),
                    new Label("gui.thermal_shock_reforged.cor_div_helmet_page.label_slot_3_polished_permafrost_x4", 14, 23, -1),
                    new Label("gui.thermal_shock_reforged.cor_div_helmet_page.label_slot_4_iron_ingot_x10", 14, 41, -1)), List.of(
                    new SpriteButton(0, 174, 38, 40, 18, 0, 0, 18, "textures/screens/atlas/imagebutton_fabricator_craft_button.png", 40, 36, "gui.thermal_shock_reforged.fabricator.craft")));
            case PROPULSION_BOOTS_BOOTS -> new Layout(246, 163, 0, "textures/screens/fabricator_panel.png", List.of(
                    new Label("gui.thermal_shock_reforged.propulsion_boots_page.label_fabricator_guide", 5, -13, -1),
                    new Label("gui.thermal_shock_reforged.propulsion_boots_page.label_tools", 158, -13, -1),
                    new Label("gui.thermal_shock_reforged.propulsion_boots_page.label_slot_1_cryonic_alloy_x5", 14, 14, -1),
                    new Label("gui.thermal_shock_reforged.propulsion_boots_page.label_slot_2_cobalt_block_x2", 14, 32, -1),
                    new Label("gui.thermal_shock_reforged.propulsion_boots_page.label_slot_3_polished_permafrost_x4", 14, 23, -1)), List.of(
                    new SpriteButton(0, 174, 38, 40, 18, 0, 0, 18, "textures/screens/atlas/imagebutton_fabricator_craft_button.png", 40, 36, "gui.thermal_shock_reforged.fabricator.craft")));
            case GLASTREKKER_ARMOR -> new Layout(246, 163, 0, "textures/screens/fabricator_panel.png", List.of(
                    new Label("gui.thermal_shock_reforged.glastrekker_armor_page.label_fabricator_guide", 5, -13, -1),
                    new Label("gui.thermal_shock_reforged.glastrekker_armor_page.label_tools", 158, -13, -1),
                    new Label("gui.thermal_shock_reforged.glastrekker_armor_page.label_slot_1_cryonic_alloy_x5", 14, 14, -1),
                    new Label("gui.thermal_shock_reforged.glastrekker_armor_page.label_slot_2_cobalt_block_x2", 14, 32, -1),
                    new Label("gui.thermal_shock_reforged.glastrekker_armor_page.label_slot_3_polished_permafrost_x4", 14, 23, -1),
                    new Label("gui.thermal_shock_reforged.glastrekker_armor_page.label_slag_x1", 14, 41, -1)), List.of(
                    new SpriteButton(0, 174, 38, 40, 18, 0, 0, 18, "textures/screens/atlas/imagebutton_fabricator_craft_button.png", 40, 36, "gui.thermal_shock_reforged.fabricator.craft")));
            case STRANGESILK_ARMOUR_HELMET -> new Layout(246, 163, 0, "textures/screens/fabricator_panel.png", List.of(
                    new Label("gui.thermal_shock_reforged.strange_hat_page.label_fabricator_guide", 5, -13, -1),
                    new Label("gui.thermal_shock_reforged.strange_hat_page.label_tools", 167, -13, -1),
                    new Label("gui.thermal_shock_reforged.strange_hat_page.label_slot_1_cryonic_alloy_x5", 14, 14, -1),
                    new Label("gui.thermal_shock_reforged.strange_hat_page.label_slot_2_cobalt_block_x2", 14, 23, -1),
                    new Label("gui.thermal_shock_reforged.strange_hat_page.label_slot_3_polished_permafrost_x4", 14, 32, -1)), List.of(
                    new SpriteButton(0, 174, 38, 40, 18, 0, 0, 18, "textures/screens/atlas/imagebutton_fabricator_craft_button.png", 40, 36, "gui.thermal_shock_reforged.fabricator.craft")));
            case STRANGESILK_ARMOUR_CHESTPLATE -> new Layout(246, 163, 0, "textures/screens/fabricator_panel.png", List.of(
                    new Label("gui.thermal_shock_reforged.strange_cloack_page.label_fabricator_guide", 5, -13, -1),
                    new Label("gui.thermal_shock_reforged.strange_cloack_page.label_tools", 158, -13, -1),
                    new Label("gui.thermal_shock_reforged.strange_cloack_page.label_slot_1_cryonic_alloy_x5", 14, 14, -1),
                    new Label("gui.thermal_shock_reforged.strange_cloack_page.label_slot_2_cobalt_block_x2", 14, 23, -1),
                    new Label("gui.thermal_shock_reforged.strange_cloack_page.label_slot_3_polished_permafrost_x4", 14, 32, -1)), List.of(
                    new SpriteButton(0, 174, 38, 40, 18, 0, 0, 18, "textures/screens/atlas/imagebutton_fabricator_craft_button.png", 40, 36, "gui.thermal_shock_reforged.fabricator.craft")));
            case SILENCED_ARMOR_HELMET -> new Layout(246, 163, 1, "textures/screens/fabricator_panel.png", List.of(
                    new Label("gui.thermal_shock_reforged.silenced_helmet_page.label_fabricator_guide", 5, -13, -1),
                    new Label("gui.thermal_shock_reforged.silenced_helmet_page.label_tools", 170, -13, -1),
                    new Label("gui.thermal_shock_reforged.silenced_helmet_page.label_slot_1_cryonic_alloy_x5", 14, 14, -1),
                    new Label("gui.thermal_shock_reforged.silenced_helmet_page.label_slot_2_cobalt_block_x2", 14, 23, -1),
                    new Label("gui.thermal_shock_reforged.silenced_helmet_page.label_slot_3_polished_permafrost_x4", 14, 32, -1),
                    new Label("gui.thermal_shock_reforged.silenced_helmet_page.label_corrial_diving_helmet", 14, 41, -1)), List.of(
                    new SpriteButton(0, 174, 38, 40, 18, 0, 0, 18, "textures/screens/atlas/imagebutton_fabricator_craft_button.png", 40, 36, "gui.thermal_shock_reforged.fabricator.craft")));
            case SILENCED_ARMOR_CHESTPLATE -> new Layout(246, 163, 1, "textures/screens/fabricator_panel.png", List.of(
                    new Label("gui.thermal_shock_reforged.silenced_chestplate_page.label_fabricator_guide", 5, -13, -1),
                    new Label("gui.thermal_shock_reforged.silenced_chestplate_page.label_tools", 174, -13, -1),
                    new Label("gui.thermal_shock_reforged.silenced_chestplate_page.label_slot_1_cryonic_alloy_x5", 14, 14, -1),
                    new Label("gui.thermal_shock_reforged.silenced_chestplate_page.label_slot_2_cobalt_block_x2", 14, 23, -1),
                    new Label("gui.thermal_shock_reforged.silenced_chestplate_page.label_slot_3_polished_permafrost_x4", 14, 32, -1),
                    new Label("gui.thermal_shock_reforged.silenced_chestplate_page.label_corrial_diving_helmet", 14, 41, -1)), List.of(
                    new SpriteButton(0, 174, 38, 40, 18, 0, 0, 18, "textures/screens/atlas/imagebutton_fabricator_craft_button.png", 40, 36, "gui.thermal_shock_reforged.fabricator.craft")));
        };
    }
}
