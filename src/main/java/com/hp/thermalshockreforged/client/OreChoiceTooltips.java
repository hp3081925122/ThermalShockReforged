package com.hp.thermalshockreforged.client;

import com.hp.thermalshockreforged.ThermalShockReforged;
import com.hp.thermalshockreforged.content.recipe.RecipeContent;
import com.hp.thermalshockreforged.world.LoqendiaContent;
import com.hp.thermalshockreforged.world.OreChoicePayload;
import java.util.Map;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;

@EventBusSubscriber(modid = ThermalShockReforged.MOD_ID, value = Dist.CLIENT)
public final class OreChoiceTooltips {
    private static final String KEY = "tooltip." + ThermalShockReforged.MOD_ID + ".ore_choice.";
    private static final Map<String, int[]> ORES = Map.ofEntries(
            Map.entry("ruby", new int[] { 1, 1 }), Map.entry("rough_ruby", new int[] { 1, 1 }), Map.entry("ruby_ore", new int[] { 1, 1 }), Map.entry("mantle_ruby_ore", new int[] { 1, 1 }),
            Map.entry("bloodstone", new int[] { 1, 2 }), Map.entry("rough_bloodstone", new int[] { 1, 2 }), Map.entry("bloodstone_ore", new int[] { 1, 2 }), Map.entry("mantle_bloodstone_ore", new int[] { 1, 2 }),
            Map.entry("malachite", new int[] { 2, 1 }), Map.entry("malachite_fragment", new int[] { 2, 1 }), Map.entry("malachite_ore", new int[] { 2, 1 }), Map.entry("malachite_outcrop", new int[] { 2, 1 }), Map.entry("malachite_crystal", new int[] { 2, 1 }),
            Map.entry("bismuth", new int[] { 2, 2 }), Map.entry("bismuth_fragment", new int[] { 2, 2 }), Map.entry("bismuth_ore", new int[] { 2, 2 }), Map.entry("bismuth_outcrop", new int[] { 2, 2 }), Map.entry("bismuth_crystal", new int[] { 2, 2 }));

    private OreChoiceTooltips() { }

    @SubscribeEvent
    public static void tooltip(ItemTooltipEvent event) {
        var id = BuiltInRegistries.ITEM.getKey(event.getItemStack().getItem());
        if (!id.getNamespace().equals(ThermalShockReforged.MOD_ID)) return;
        var added = new java.util.ArrayList<Component>();
        int[] ore = ORES.get(id.getPath());
        var level = Minecraft.getInstance().level;
        if (ore != null && level != null) {
            int choice;
            if (ore[0] == 1) choice = OreChoicePayload.tier1(level);
            else choice = OreChoicePayload.tier2(level);
            boolean generated = false;
            if (!level.dimension().identifier().equals(LoqendiaContent.LOQENDIA_ID)) added.add(Component.translatable(KEY + "absent").withStyle(ChatFormatting.RED));
            else if (choice == 0) added.add(Component.translatable(KEY + "pending").withStyle(ChatFormatting.GRAY));
            else if (choice == ore[1]) {
                generated = true;
                added.add(Component.translatable(KEY + "generated").withStyle(ChatFormatting.GREEN));
            } else added.add(Component.translatable(KEY + "absent").withStyle(ChatFormatting.RED));
            var stack = event.getItemStack();
            for (var holder : RecipeContent.recipes(level).byType(RecipeContent.TRANSMUTATION.type().get())) {
                var recipe = holder.value();
                if (generated && recipe.ingredient().test(stack)) {
                    added.add(Component.translatable(KEY + "transmute_to", recipe.result().create().getHoverName()).withStyle(ChatFormatting.AQUA));
                    break;
                }
                if (!generated && recipe.result().is(stack.getItem()) && recipe.ingredient().items().findFirst().isPresent()) {
                    added.add(Component.translatable(KEY + "transmute_from", new net.minecraft.world.item.ItemStack(recipe.ingredient().items().findFirst().orElseThrow()).getHoverName(), stack.getHoverName()).withStyle(ChatFormatting.AQUA));
                    break;
                }
            }
        }
        var info = GENERATION.get(id.getPath());
        if (info != null) {
            added.add(Component.translatable(INFO_KEY + "biomes", biomes(info.biomes())).withStyle(ChatFormatting.GRAY));
            if (info.peak()) added.add(Component.translatable(INFO_KEY + "peak", Math.floorDiv(info.min() + info.max(), 2), info.min(), info.max()).withStyle(ChatFormatting.GRAY));
            else added.add(Component.translatable(INFO_KEY + "uniform", info.min(), info.max()).withStyle(ChatFormatting.GRAY));
        }
        var sources = SOURCES.get(id.getPath());
        if (sources != null) for (var source : sources) added.add(Component.translatable(INFO_KEY + source.key(), biomes(source.biomes())).withStyle(ChatFormatting.GRAY));
        if (added.isEmpty()) return;
        var lines = event.getToolTip();
        lines.addAll(Math.min(1, lines.size()), added);
    }

    private static Component biomes(String[] biomes) {
        if (biomes.length == 0) return Component.translatable(INFO_KEY + "all_biomes");
        var list = Component.empty();
        for (int index = 0; index < biomes.length; index++) {
            if (index > 0) list.append(Component.translatable(INFO_KEY + "separator"));
            list.append(Component.translatable("biome." + ThermalShockReforged.MOD_ID + "." + biomes[index]));
        }
        return list;
    }

    private record Generation(String[] biomes, int min, int max, boolean peak) { }

    private record Source(String key, String[] biomes) { }

    private static final String INFO_KEY = "tooltip." + ThermalShockReforged.MOD_ID + ".ore_info.";
    private static final String[] ALL = { };
    private static final Map<String, Generation> GENERATION = Map.ofEntries(
            Map.entry("ruby_ore", new Generation(ALL, 0, 140, true)), Map.entry("bloodstone_ore", new Generation(ALL, 0, 140, true)),
            Map.entry("mantle_ruby_ore", new Generation(ALL, -64, 0, true)), Map.entry("mantle_bloodstone_ore", new Generation(ALL, -64, 0, true)),
            Map.entry("malachite_ore", new Generation(ALL, 0, 44, true)), Map.entry("bismuth_ore", new Generation(ALL, 0, 44, true)),
            Map.entry("malachite_outcrop", new Generation(new String[] { "stardust_garden" }, 0, 40, true)), Map.entry("bismuth_outcrop", new Generation(new String[] { "stardust_garden" }, 0, 40, true)),
            Map.entry("malachite_crystal", new Generation(new String[] { "borean_tundra", "luminous_sea", "stardust_garden" }, -58, 30, false)),
            Map.entry("bismuth_crystal", new Generation(new String[] { "borean_tundra", "luminous_sea", "stardust_garden" }, -58, 30, false)),
            Map.entry("pathiolite_outcrop", new Generation(ALL, -64, -48, true)),
            Map.entry("phobotite_outcrop", new Generation(ALL, -40, 75, true)), Map.entry("phobotite_mantle_outcrop", new Generation(ALL, -40, 75, true)),
            Map.entry("thermite", new Generation(ALL, -64, -46, false)),
            Map.entry("ferrous_shale", new Generation(ALL, -64, 180, true)), Map.entry("ferrous_mantlestone", new Generation(ALL, -64, 180, true)),
            Map.entry("corroded_shale", new Generation(new String[] { "luminous_sea", "cobalt_heights" }, -64, 50, true)), Map.entry("corroded_mantlestone", new Generation(new String[] { "luminous_sea", "cobalt_heights" }, -64, 50, true)));
    private static final String[] COBALT_HEIGHTS = { "cobalt_heights" };
    private static final Map<String, Source[]> SOURCES = Map.ofEntries(
            Map.entry("cobalt", new Source[] { new Source("cobalt_pillar", COBALT_HEIGHTS), new Source("cobalt_lake", ALL) }),
            Map.entry("snowy_cobalt", new Source[] { new Source("cobalt_pillar", COBALT_HEIGHTS) }));
}
