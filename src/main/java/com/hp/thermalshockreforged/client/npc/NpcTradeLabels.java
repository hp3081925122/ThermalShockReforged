package com.hp.thermalshockreforged.client.npc;

import com.hp.thermalshockreforged.ThermalShockReforged;
import com.hp.thermalshockreforged.content.recipe.NpcTradeRecipe;
import com.hp.thermalshockreforged.content.recipe.RecipeContent;
import java.util.Locale;
import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public final class NpcTradeLabels {
    private static final String PREFIX = "gui." + ThermalShockReforged.MOD_ID + ".";
    private static final int ICON_OFFSET_X = 23, ICON_OFFSET_Y = 5, ICON_SIZE = 20;
    private static final int SPICE_OFFSET_X = 4, SPICE_OFFSET_Y = 8, SPICE_SIZE = 20, LEVEL_WIDTH = 40;

    private NpcTradeLabels() { }

    public static Component text(String key) {
        var fallback = Component.translatable(key);
        var recipe = recipe(key);
        if (recipe == null) return fallback;
        String suffix = key.substring(key.lastIndexOf('.') + 1);
        if (suffix.equals("label_xxx")) {
            if (recipe.levels() <= 0) return fallback;
            return Component.literal(String.format(Locale.ROOT, "%03d", recipe.levels()));
        }
        var spice = spice(suffix);
        if (spice == null) return fallback;
        int count = recipe.cost(spice);
        if (count <= 0) return fallback;
        return Component.literal(String.format(Locale.ROOT, "%02d", count));
    }

    public static boolean tooltip(GuiGraphicsExtractor graphics, Font font, String key, int x, int y, int mouseX, int mouseY) {
        var recipe = recipe(key);
        if (recipe == null) return false;
        String suffix = key.substring(key.lastIndexOf('.') + 1);
        if (suffix.equals("label_lorem_ipsum")) {
            if (!inside(mouseX, mouseY, x - ICON_OFFSET_X, y - ICON_OFFSET_Y, ICON_SIZE, ICON_SIZE) || recipe.result().isEmpty()) return false;
            graphics.renderTooltip(font, recipe.result(), mouseX, mouseY);
            return true;
        }
        if (suffix.equals("label_xxx")) {
            if (!inside(mouseX, mouseY, x - SPICE_OFFSET_X, y - SPICE_OFFSET_Y, LEVEL_WIDTH, SPICE_SIZE)) return false;
            graphics.renderTooltip(font, Component.translatable(PREFIX + "trade.experience"), mouseX, mouseY);
            return true;
        }
        var spice = spice(suffix);
        if (spice == null || !inside(mouseX, mouseY, x - SPICE_OFFSET_X, y - SPICE_OFFSET_Y, SPICE_SIZE, SPICE_SIZE)) return false;
        graphics.renderTooltip(font, new ItemStack(spice).getHoverName(), mouseX, mouseY);
        return true;
    }

    private static NpcTradeRecipe recipe(String key) {
        int dot = key.lastIndexOf('.');
        var level = Minecraft.getInstance().level;
        if (!key.startsWith(PREFIX) || dot <= PREFIX.length() || level == null) return null;
        return RecipeContent.NPC_TRADE.get(level, NpcTradeRecipe.id(key.substring(PREFIX.length(), dot)));
    }

    private static Item spice(String suffix) {
        String name;
        switch (suffix) {
            case "label_xx" -> name = "adamantine_spice";
            case "label_xx1" -> name = "mithral_spice";
            case "label_xx2" -> name = "tourmaline_spice";
            default -> { return null; }
        }
        return BuiltInRegistries.ITEM.get(RecipeContent.id(name));
    }

    private static boolean inside(int mouseX, int mouseY, int x, int y, int width, int height) {
        return mouseX >= x && mouseX < x + width && mouseY >= y && mouseY < y + height;
    }
}
