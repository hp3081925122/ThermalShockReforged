package com.hp.thermalshockreforged.client.npc;

import com.hp.thermalshockreforged.ThermalShockReforged;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public final class NpcIcons {
    private static final int MAX_SIZE = 32;
    private static final Map<String, String> ALIASES = Map.ofEntries(
            Map.entry("bismuth_25", "bismuth"),
            Map.entry("cake", "minecraft:cake"),
            Map.entry("cooked_koi", "cooked_crystal_koi"),
            Map.entry("cooked_sentrapede", "cokked_sentrapede"),
            Map.entry("ferro_cake_icon", "ferro_cake"),
            Map.entry("goldbel", "bellflower"),
            Map.entry("kinetic_laserhook", "ender_tentacle"),
            Map.entry("large_spider_lily_item", "large_spider_lily"),
            Map.entry("loqsicle", "loquesicle"),
            Map.entry("magnetic_round", "specialized_round"),
            Map.entry("milk_bottle", "loque_milk_bottle"),
            Map.entry("ochre_sapling_26", "ochre_sapling"),
            Map.entry("pixipetil", "pixietill"),
            Map.entry("polemonium_b", "polemonium"),
            Map.entry("scorian_waffle", "skorio_waffle"),
            Map.entry("seed_packet_barbubble", "barbubble_seed"),
            Map.entry("seed_packet_skorio", "skorio_seed"),
            Map.entry("seed_packet_sparkler", "sparkler_seed"),
            Map.entry("spider_lily_item", "spider_lilies"),
            Map.entry("spring_gel", "springelatin_ball"),
            Map.entry("steelstem_pot_side", "planter_pot"),
            Map.entry("topaz_cap", "tophaz"),
            Map.entry("vermillion_sapling_26", "vermillion_sapling"),
            Map.entry("wildgrass_top", "wildgrass"),
            Map.entry("zap_gauntlet_icon", "zap_gauntlet"));
    private static final Map<String, ItemStack> CACHE = new HashMap<>();

    private NpcIcons() { }

    public static ItemStack stack(String texture, int width, int height) {
        if (texture == null || width > MAX_SIZE || height > MAX_SIZE) return ItemStack.EMPTY;
        return CACHE.computeIfAbsent(texture, NpcIcons::resolve);
    }

    public static boolean draw(GuiGraphicsExtractor graphics, String texture, int x, int y, int width, int height) {
        ItemStack stack = stack(texture, width, height);
        if (stack.isEmpty()) return false;
        graphics.pose().pushPose();
        graphics.pose().translate(x, y, 0);
        graphics.pose().scale(width / 16.0F, height / 16.0F, 1);
        graphics.renderItem(stack, 0, 0);
        graphics.pose().popPose();
        return true;
    }

    public static boolean tooltip(GuiGraphicsExtractor graphics, Font font, String texture, int x, int y, int width, int height, int mouseX, int mouseY) {
        if (mouseX < x || mouseX >= x + width || mouseY < y || mouseY >= y + height) return false;
        ItemStack stack = stack(texture, width, height);
        if (stack.isEmpty()) return false;
        graphics.renderTooltip(font, stack, mouseX, mouseY);
        return true;
    }

    private static ItemStack resolve(String texture) {
        String name = ALIASES.getOrDefault(texture, texture);
        Identifier id;
        if (name.contains(":")) id = Identifier.tryParse(name);
        else id = Identifier.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, name);
        var item = BuiltInRegistries.ITEM.get(id);
        if (item == Items.AIR) {
            ThermalShockReforged.LOGGER.debug("NPC icon {} has no matching item, keeping texture", texture);
            return ItemStack.EMPTY;
        }
        return new ItemStack(item);
    }
}
