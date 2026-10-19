package com.hp.thermalshockreforged.content.fabrication;

import com.hp.thermalshockreforged.ThermalShockReforged;
import net.minecraft.resources.ResourceLocation;

public enum FabricatorPage {
    TOOLS(null),
    WEAPONS(null),
    ARMOR(null),
    GHOSTGLASS_PICK("ghostglass_pick"),
    FERROUS_ALTAR("ferrous_altar"),
    TRANSMUTATOR("transmutator"),
    THINGAMABOB("thingamabob"),
    STRATOSILVER_DRILL("stratosilver_drill"),
    STRATOSILVER_CHAINSAW("stratosilver_chainsaw"),
    STRATOSILVER_AUGER("stratosilver_auger"),
    EXTRACTOR_CONSTRUCTOR("extractor_constructor"),
    FLINTSTEEL_RIFLE("flintsteel_rifle"),
    VOLCANO("volcano"),
    HAILFALL("hailfall"),
    MAGNETIC_CROSSBOW("magnetic_crossbow"),
    TITANIUM_MACE("titanium_mace"),
    COBALT_THERMOBLASTER("cobalt_thermoblaster"),
    AMALJELLY_CANISTER("amaljelly_canister"),
    SHOCK_STICK("shock_stick"),
    KNIGHTS_EDGE("knights_edge"),
    HI_VOLT_LANCE("hi_volt_lance"),
    BUZZSAW("buzzsaw"),
    LITEPRISM_DISK("liteprism_disk"),
    LITEPRISM_LASER("liteprism_laser"),
    SENTRY_HELMET("sentry_helmet"),
    SENTRY_CHESTPLATE("sentry_chestplate"),
    DEV_SCARF_CHESTPLATE("dev_scarf_chestplate"),
    CORRIAL_DIVING_HELMET_HELMET("corrial_diving_helmet_helmet"),
    PROPULSION_BOOTS_BOOTS("propulsion_boots_boots"),
    GLASTREKKER_ARMOR("glastrekker_armor"),
    STRANGESILK_ARMOUR_HELMET("strangesilk_armour_helmet"),
    STRANGESILK_ARMOUR_CHESTPLATE("strangesilk_armour_chestplate"),
    SILENCED_ARMOR_HELMET("silenced_armor_helmet"),
    SILENCED_ARMOR_CHESTPLATE("silenced_armor_chestplate"),
    LITEPRISM_SWORD("liteprism_sword");

    public final ResourceLocation recipe;
    FabricatorPage(String output) {
        recipe = output == null ? null : ResourceLocation.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, "fabrication/" + output);
    }
    public boolean isGuide() { return recipe == null; }
    public FabricatorPage next(int button) {
        if (button < 0) return null;
        var destinations = switch (this) {
            case TOOLS -> new FabricatorPage[] {GHOSTGLASS_PICK, ARMOR, WEAPONS, FERROUS_ALTAR, TRANSMUTATOR, THINGAMABOB, STRATOSILVER_DRILL, STRATOSILVER_CHAINSAW, STRATOSILVER_AUGER, EXTRACTOR_CONSTRUCTOR};
            case WEAPONS -> new FabricatorPage[] {TOOLS, ARMOR, FLINTSTEEL_RIFLE, VOLCANO, HAILFALL, MAGNETIC_CROSSBOW, TITANIUM_MACE, COBALT_THERMOBLASTER, AMALJELLY_CANISTER, SHOCK_STICK, KNIGHTS_EDGE, HI_VOLT_LANCE, BUZZSAW, LITEPRISM_DISK, LITEPRISM_LASER, LITEPRISM_SWORD};
            case ARMOR -> new FabricatorPage[] {WEAPONS, TOOLS, SENTRY_HELMET, SENTRY_CHESTPLATE, DEV_SCARF_CHESTPLATE, CORRIAL_DIVING_HELMET_HELMET, PROPULSION_BOOTS_BOOTS, GLASTREKKER_ARMOR, STRANGESILK_ARMOUR_HELMET, STRANGESILK_ARMOUR_CHESTPLATE, SILENCED_ARMOR_HELMET, SILENCED_ARMOR_CHESTPLATE};
            default -> new FabricatorPage[0];
        };
        return button < destinations.length ? destinations[button] : null;
    }
    public static FabricatorPage byIndex(int index) {
        if (index < 0 || index >= values().length) throw new IllegalArgumentException("Invalid Fabricator page: " + index);
        return values()[index];
    }
}
