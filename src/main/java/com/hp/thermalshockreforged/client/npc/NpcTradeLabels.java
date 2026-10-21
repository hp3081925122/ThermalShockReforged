package com.hp.thermalshockreforged.client.npc;

import com.hp.thermalshockreforged.ThermalShockReforged;
import com.hp.thermalshockreforged.content.recipe.NpcTradeRecipe;
import com.hp.thermalshockreforged.content.recipe.RecipeContent;
import java.util.Locale;
import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;

public final class NpcTradeLabels {
    private static final String PREFIX = "gui." + ThermalShockReforged.MOD_ID + ".";

    private NpcTradeLabels() { }

    public static Component text(String key) {
        var fallback = Component.translatable(key);
        int dot = key.lastIndexOf('.');
        var level = Minecraft.getInstance().level;
        if (!key.startsWith(PREFIX) || dot <= PREFIX.length() || level == null) return fallback;
        String suffix = key.substring(dot + 1);
        String spice;
        switch (suffix) {
            case "label_xx" -> spice = "adamantine_spice";
            case "label_xx1" -> spice = "mithral_spice";
            case "label_xx2" -> spice = "tourmaline_spice";
            case "label_xxx" -> spice = null;
            default -> { return fallback; }
        }
        var recipe = RecipeContent.NPC_TRADE.get(level, NpcTradeRecipe.id(key.substring(PREFIX.length(), dot)));
        if (recipe == null) return fallback;
        if (spice == null) {
            if (recipe.levels() <= 0) return fallback;
            return Component.literal(String.format(Locale.ROOT, "%03d", recipe.levels()));
        }
        int count = recipe.cost(BuiltInRegistries.ITEM.get(RecipeContent.id(spice)));
        if (count <= 0) return fallback;
        return Component.literal(String.format(Locale.ROOT, "%02d", count));
    }
}
