package com.hp.thermalshockreforged.content.fabrication;

import com.hp.thermalshockreforged.ThermalShockReforged;
import java.util.List;
import net.minecraft.resources.Identifier;

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
    EXTRACTOR("extractor"),
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
    LITEPRISM_SWORD("liteprism_sword"),
    STRATOSILVER_SICKLE("stratosilver_sickle");

    public final Identifier recipe;
    FabricatorPage(String output) {
        if (output == null) recipe = null;
        else recipe = Identifier.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, "fabrication/" + output);
    }
    public boolean isGuide() { return recipe == null; }
    public static final List<FabricatorPage> CATEGORIES = List.of(TOOLS, WEAPONS, ARMOR);
    public List<FabricatorPage> recipes() {
        return switch (this) {
            case TOOLS -> List.of(GHOSTGLASS_PICK, FERROUS_ALTAR, TRANSMUTATOR, THINGAMABOB, STRATOSILVER_DRILL, STRATOSILVER_CHAINSAW, STRATOSILVER_AUGER, STRATOSILVER_SICKLE, EXTRACTOR);
            case WEAPONS -> List.of(FLINTSTEEL_RIFLE, VOLCANO, HAILFALL, MAGNETIC_CROSSBOW, TITANIUM_MACE, COBALT_THERMOBLASTER, AMALJELLY_CANISTER, SHOCK_STICK, KNIGHTS_EDGE, HI_VOLT_LANCE, BUZZSAW, LITEPRISM_DISK, LITEPRISM_LASER, LITEPRISM_SWORD);
            case ARMOR -> List.of(SENTRY_HELMET, SENTRY_CHESTPLATE, DEV_SCARF_CHESTPLATE, CORRIAL_DIVING_HELMET_HELMET, PROPULSION_BOOTS_BOOTS, GLASTREKKER_ARMOR, STRANGESILK_ARMOUR_HELMET, STRANGESILK_ARMOUR_CHESTPLATE, SILENCED_ARMOR_HELMET, SILENCED_ARMOR_CHESTPLATE);
            default -> List.of();
        };
    }
    public FabricatorPage category() {
        if (isGuide()) return this;
        for (var category : CATEGORIES) if (category.recipes().contains(this)) return category;
        return TOOLS;
    }
    public static FabricatorPage byIndex(int index) {
        if (index < 0 || index >= values().length) throw new IllegalArgumentException("Invalid Fabricator page: " + index);
        return values()[index];
    }
}
