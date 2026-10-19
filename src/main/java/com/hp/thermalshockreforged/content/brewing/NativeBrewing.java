package com.hp.thermalshockreforged.content.brewing;

import com.hp.thermalshockreforged.content.MaterialContent;
import com.hp.thermalshockreforged.content.decor.geo.GeoDecorContent;
import com.hp.thermalshockreforged.content.decor.geo.GeoDecorKind;
import com.hp.thermalshockreforged.content.status.StatusContent;
import com.hp.thermalshockreforged.content.terrain.TerrainContent;
import com.hp.thermalshockreforged.content.utility.UtilityContent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionBrewing;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.brewing.RegisterBrewingRecipesEvent;

public final class NativeBrewing {
    private NativeBrewing() { }

    public static void register() { NeoForge.EVENT_BUS.addListener(NativeBrewing::recipes); }

    private static void recipes(RegisterBrewingRecipesEvent event) {
        PotionBrewing.Builder builder = event.getBuilder();
        builder.addRecipe(Ingredient.of(UtilityContent.LOQUE_MILK_BOTTLE.get()), Ingredient.of(Items.COCOA_BEANS), new ItemStack(StatusContent.COCOA.get()));
        builder.addRecipe(Ingredient.of(Items.GLASS_BOTTLE), Ingredient.of(UtilityContent.LOQUE_MILK.get()), new ItemStack(UtilityContent.LOQUE_MILK_BOTTLE.get()));
        builder.addRecipe(Ingredient.of(Items.GLASS_BOTTLE), Ingredient.of(material("starfruit_item")), new ItemStack(GeoDecorContent.item(GeoDecorKind.BOTTLE_OF_STARLIGHT)));
        builder.addRecipe(Ingredient.of(Items.GLASS_BOTTLE), Ingredient.of(material("lunalemon_item")), new ItemStack(GeoDecorContent.item(GeoDecorKind.BOTTLE_OF_LUNARLIGHT)));
        builder.addRecipe(Ingredient.of(material("icy_snowcone")), Ingredient.of(material("starfruit_item")), new ItemStack(material("starry_snowcone")));
        builder.addRecipe(Ingredient.of(material("icy_snowcone")), Ingredient.of(material("lunalemon_item")), new ItemStack(material("lunar_snowcone")));
        builder.addRecipe(Ingredient.of(material("icy_snowcone")), Ingredient.of(material("barbubble_melon_slice")), new ItemStack(material("fizzy_snowcone")));
        builder.addMix(Potions.AWKWARD, TerrainContent.BREATHER_SPONGE.get().asItem(), Potions.WATER_BREATHING);
        builder.addMix(Potions.AWKWARD, material("bioelectric_cell"), StatusContent.ELECTRIFYING);
        builder.addRecipe(Ingredient.of(material("dew_bottle")), Ingredient.of(material("hearth_honey")), PotionContents.createItemStack(Items.POTION, Potions.REGENERATION));
    }

    private static Item material(String id) { return MaterialContent.items().get(id).get(); }
}
