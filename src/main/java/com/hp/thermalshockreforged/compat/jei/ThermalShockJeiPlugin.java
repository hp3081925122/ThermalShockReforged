package com.hp.thermalshockreforged.compat.jei;

import com.hp.thermalshockreforged.ThermalShockReforged;
import com.hp.thermalshockreforged.content.fabrication.FabricationRecipe;
import com.hp.thermalshockreforged.content.fabrication.FabricatorPage;
import com.hp.thermalshockreforged.content.npc.NpcTrade;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.hp.thermalshockreforged.content.npc.concierge.ConciergePage;
import com.hp.thermalshockreforged.content.npc.florist.FloristPage;
import com.hp.thermalshockreforged.content.npc.ly.LyPage;
import com.hp.thermalshockreforged.content.npc.shutterbug.ShutterbugPage;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@JeiPlugin
public final class ThermalShockJeiPlugin implements IModPlugin {
    private static final ResourceLocation UID = ResourceLocation.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, "jei_plugin");
    private static final String KEY = "jei." + ThermalShockReforged.MOD_ID + ".";
    private static final List<Machine> MACHINES = List.of(
            new Machine(MachineCategory.FABRICATOR, List.of("fabricator"), new MachineCategory.Layout(new int[][]{{0, 0}, {18, 0}, {36, 0}, {0, 18}, {18, 18}, {36, 18}, {18, 40}}, 62, 20, 92, 20, 1, 110, 58)),
            new Machine(MachineCategory.TRANSMUTATOR, List.of("transmutator"), new MachineCategory.Layout(new int[][]{{0, 20}}, 24, 20, 54, 20, 1, 72, 40)),
            new Machine(MachineCategory.EXTRACTOR, List.of("extractor"), new MachineCategory.Layout(new int[][]{{0, 20}}, 24, 20, 54, 20, 1, 72, 52)),
            new Machine(MachineCategory.CENTRIFUGE, List.of("centrifuge"), new MachineCategory.Layout(new int[][]{{0, 20}, {18, 2}, {36, 20}}, 60, 20, 90, 20, 1, 108, 40)),
            new Machine(MachineCategory.LAPIDARY, List.of("lapidarian_workbench"), new MachineCategory.Layout(new int[][]{{0, 20}}, 24, 20, 54, 2, 6, 162, 56)));
    private static final List<String[]> TRANSMUTATIONS = List.of(
            new String[]{"rough_ruby", "rough_bloodstone"}, new String[]{"rough_bloodstone", "rough_ruby"},
            new String[]{"ruby", "bloodstone"}, new String[]{"bloodstone", "ruby"},
            new String[]{"malachite", "bismuth"}, new String[]{"bismuth", "malachite"});
    private static final List<String[]> LAPIDARY_TABLES = List.of(
            new String[]{"muck", "blocks/muck_loot"},
            new String[]{"sparkly_muck", "sparkly_muck_loot", "springling"},
            new String[]{"minecraft:gravel", "gravel_lapidary"},
            new String[]{"pathiolite_scrap", "gameplay/pathiolite_lapidary"},
            new String[]{"thermal_geyser", "thermal_vent_lapidary"},
            new String[]{"glittery_shale", "gameplay/glittery_shale_lapidary"},
            new String[]{"metallic_debris", "gameplay/metallic_debris_lapidary"},
            new String[]{"modest_archive_cabinet", "gameplay/modest_archive_lapidary"},
            new String[]{"crammed_archive_cabinet", "gameplay/modest_archive_lapidary", "extra"});
    private static final Map<String, List<String>> TRADE_LOCATIONS = Map.of(
            "florist", List.of("shalespring@fixed_loqendia"),
            "shutterbug", List.of("shalespring@fixed_loqendia"),
            "ly", List.of("kiosk", "lavender_palace@fixed_aqeron"),
            "concierge", List.of("cathedral"),
            "whisper", List.of("large_campsite", "stardust_garden@natural"),
            "lab_machinist", List.of("machinist_lab"));
    private static final List<FixedTrade> FIXED_TRADES = List.of(
            new FixedTrade("whisper", "malachite", 10, 20, List.<String[]>of(d("adamantine_spice", "10"))),
            new FixedTrade("whisper", "bismuth", 10, 20, List.<String[]>of(d("adamantine_spice", "10"))),
            new FixedTrade("whisper", "starfruit_seed", 1, 6, List.<String[]>of(d("adamantine_spice", "3"))),
            new FixedTrade("whisper", "lunalemon_seed", 1, 6, List.<String[]>of(d("adamantine_spice", "3"))),
            new FixedTrade("whisper", "cocoa", 1, 5, List.of()),
            new FixedTrade("whisper", "bottled_pixie", 1, 10, List.<String[]>of(d("adamantine_spice", "7"))),
            new FixedTrade("whisper", "zenith_smithing_template", 1, 30, List.of(d("adamantine_spice", "10"), d("tourmaline_spice", "1"))),
            new FixedTrade("whisper", "floor_33", 1, 33, List.of()),
            new FixedTrade("lab_machinist", "ender_tentacle", 1, 12, List.of(d("adamantine_spice", "40"), d("mithral_spice", "1"))),
            new FixedTrade("lab_machinist", "pylon_core", 1, 0, List.<String[]>of(d("mithral_spice", "1"))),
            new FixedTrade("lab_machinist", "shock_stick", 1, 7, List.of(d("adamantine_spice", "12"), d("mithral_spice", "3"))),
            new FixedTrade("lab_machinist", "sylvia_rifle", 1, 42, List.of(d("adamantine_spice", "48"), d("mithral_spice", "5"), d("tourmaline_spice", "1"))),
            new FixedTrade("lab_machinist", "wallbore", 1, 8, List.of(d("adamantine_spice", "10"), d("mithral_spice", "1"))),
            new FixedTrade("lab_machinist", "zap_gauntlet", 1, 16, List.of(d("adamantine_spice", "28"), d("mithral_spice", "5"))),
            new FixedTrade("lab_machinist", "zenith_smithing_template", 1, 55, List.<String[]>of(d("adamantine_spice", "5"))));
    private static final List<List<String>> SICKLE_TIERS = List.of(
            List.of("ruby_sickle", "bloodstone_sickle", "carborundum_sickle"),
            List.of("claw_sickle"),
            List.of("stratosilver_sickle"));
    private static final List<String> ANCIENT_SEEDS = List.of("ancient_skorio_seed", "ancient_barbubble_seed", "dewleaf_seed", "hearthfly_branch_seed", "frost_lotus_seed", "neoflorm_seed", "volatine_seed");
    private static final List<String> SPARKLER_PLANT = List.of("vermillion_sparkler_stem", "vermillion_sparkler_top_1", "vermillion_sparkler_top_2", "vermillion_sparkler_top_3");
    private static final List<String> COMMON_BIOMES = List.of("borean_forest", "vermillion_woods", "stardust_garden");
    private static final List<Crop> CROPS = List.of(
            new Crop(List.of("skorio"), COMMON_BIOMES,
                    List.of(d("skorio_petal", "2"), d("skorio_seed", "1-2")),
                    List.of(d("skorio_petal", "2"), d("skorio_seed", "1-2")),
                    List.of(d("skorio_petal", "3"), d("skorio_seed", "1-3"), d("ancient_skorio_seed", "60%"))),
            new Crop(List.of("barbubble"), COMMON_BIOMES,
                    List.of(d("barbubble_melon", "2"), d("barbubble_seed", "1-2")),
                    List.of(d("barbubble_melon", "2"), d("barbubble_seed", "1-2")),
                    List.of(d("barbubble_melon", "3"), d("barbubble_seed", "1-3"), d("ancient_barbubble_seed", "60%"))),
            new Crop(SPARKLER_PLANT, List.of("vermillion_woods"),
                    List.of(d("steelstem", "2"), d("sparkler_seed", "1-2")),
                    List.of(d("steelstem", "2"), d("sparkler_seed", "1-2")),
                    List.of(d("steelstem", "3"), d("sparkler_seed", "1-3"))),
            new Crop(List.of("visi_bulb"), List.of("glaciation"),
                    List.<String[]>of(d("visi_bulb_seed", "1-2")),
                    List.<String[]>of(d("visi_bulb_seed", "1-3")),
                    List.<String[]>of(d("visi_bulb_seed", "2-3"))),
            new Crop(List.of("ancient_skorio"), List.of(),
                    List.of(d("skorio_petal", "5"), d("ancient_skorio_seed", "1-2")),
                    List.of(d("skorio_petal", "5"), d("ancient_skorio_seed", "1-2")),
                    List.of(d("skorio_petal", "7"), d("ancient_skorio_seed", "1-3"))),
            new Crop(List.of("ancient_barbubble"), List.of(),
                    List.of(d("barbubble_melon_slice", "5"), d("ancient_barbubble_seed", "1-2")),
                    List.of(d("barbubble_melon_slice", "5"), d("ancient_barbubble_seed", "1-2")),
                    List.of(d("barbubble_melon_slice", "8"), d("ancient_barbubble_seed", "1-3"))),
            new Crop(List.of("dewleaf"), List.of(),
                    List.of(d("dew_bottle", "1-2"), d("dewleaf_seed", "1-2")),
                    List.of(d("dew_bottle", "1-2"), d("dewleaf_seed", "1-2")),
                    List.of(d("dew_bottle", "2-4"), d("dewleaf_seed", "1-3"))),
            new Crop(List.of("neoflorm"), List.of(),
                    List.of(d("neoflorm_crystal", "2"), d("neoflorm_seed", "1-2")),
                    List.of(d("neoflorm_crystal", "2-3"), d("neoflorm_seed", "1-2")),
                    List.of(d("neoflorm_crystal", "2-6"), d("neoflorm_seed", "1-2"), d("stellar_scrap", "50%"))),
            new Crop(List.of("frost_lotus"), List.of(),
                    List.of(d("frost_pearl", "2"), d("frost_lotus_seed", "1-2")),
                    List.of(d("frost_pearl", "2"), d("frost_lotus_seed", "1-2")),
                    List.of(d("frost_pearl", "3"), d("frost_lotus_seed", "1-3"))),
            new Crop(List.of("hearthfly_branch"), List.of(),
                    List.of(d("hearth_honey", "2"), d("hearthfly_branch_seed", "1-2")),
                    List.of(d("hearth_honey", "2"), d("hearthfly_branch_seed", "1-2")),
                    List.of(d("hearth_honey", "2"), d("hearthfly_branch_seed", "1-3"))),
            new Crop(List.of("volatine"), List.of(),
                    List.of(d("minecraft:gunpowder", "2"), d("volatine_seed", "1-2")),
                    List.of(d("minecraft:gunpowder", "2"), d("volatine_seed", "1-2")),
                    List.of(d("minecraft:gunpowder", "2"), d("volatine_seed", "1-2"))),
            new Crop(List.of("gel_bulb"), List.of(),
                    List.of(d("springelatin_ball", "1-2"), d("gel_bulb_seed", "1-2")),
                    List.of(d("springelatin_ball", "1-2"), d("gel_bulb_seed", "1-2")),
                    List.of(d("springelatin_ball", "2-5"), d("gel_bulb_seed", "1-3"))));

    @Override
    public ResourceLocation getPluginUid() { return UID; }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        var helper = registration.getJeiHelpers().getGuiHelper();
        registration.addRecipeCategories(new SickleHarvestCategory(helper, SickleHarvestCategory.WILD, stack("ruby_sickle", "1"), List.of()));
        registration.addRecipeCategories(new SickleHarvestCategory(helper, SickleHarvestCategory.POT, stack("planter_pot", "1"), stacks(List.of("planter_pot", "planter_box"))));
        registration.addRecipeCategories(new NpcTradeCategory(helper, stack("adamantine_spice", "1")));
        registration.addRecipeCategories(new AltarCategory(helper, stack("frigid_altar", "1")));
        registration.addRecipeCategories(new CacheLootCategory(helper, stack("cryonic_cache", "1")));
        for (Machine machine : MACHINES) registration.addRecipeCategories(new MachineCategory(helper, machine.type(), "block." + ThermalShockReforged.MOD_ID + "." + machine.blocks().getFirst(), stacks(machine.blocks()), machine.layout()));
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        for (List<String> tier : SICKLE_TIERS) {
            for (ItemStack sickle : stacks(tier)) {
                registration.addRecipeCatalyst(sickle, SickleHarvestCategory.WILD);
                registration.addRecipeCatalyst(sickle, SickleHarvestCategory.POT);
            }
        }
        for (ItemStack planter : stacks(List.of("planter_pot", "planter_box"))) registration.addRecipeCatalyst(planter, SickleHarvestCategory.POT);
        for (String npc : TRADE_LOCATIONS.keySet()) {
            var egg = stack(npc + "_spawn_egg", "1");
            if (!egg.isEmpty()) registration.addRecipeCatalyst(egg, NpcTradeCategory.TYPE);
        }
        for (Machine machine : MACHINES) for (ItemStack block : stacks(machine.blocks())) registration.addRecipeCatalyst(block, machine.type());
        for (ItemStack altar : stacks(List.of("frigid_altar", "ferrous_altar"))) registration.addRecipeCatalyst(altar, AltarCategory.TYPE);
        for (ItemStack cache : stacks(List.of("cryonic_cache", "super_cryonic_cache", "enchanted_cache"))) registration.addRecipeCatalyst(cache, CacheLootCategory.TYPE);
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        var wild = new ArrayList<SickleHarvestEntry>();
        var pot = new ArrayList<SickleHarvestEntry>();
        for (Crop crop : CROPS) {
            var plant = stacks(crop.plant());
            if (plant.isEmpty()) continue;
            var biomes = new ArrayList<Component>();
            for (String biome : crop.biomes()) biomes.add(Component.translatable("biome." + ThermalShockReforged.MOD_ID + "." + biome));
            for (int tier = 0; tier < SICKLE_TIERS.size(); tier++) {
                var sickles = stacks(SICKLE_TIERS.get(tier));
                var drops = new ArrayList<SickleHarvestEntry.Drop>();
                for (String[] drop : crop.tiers().get(tier)) {
                    var stack = stack(drop[0], drop[1]);
                    if (!stack.isEmpty()) drops.add(new SickleHarvestEntry.Drop(List.of(stack), note(drop[1])));
                }
                if (tier > 0) drops.add(new SickleHarvestEntry.Drop(stacks(ANCIENT_SEEDS), Component.translatable("jei." + ThermalShockReforged.MOD_ID + ".sickle_harvest.ancient_seeds")));
                pot.add(new SickleHarvestEntry(sickles, plant, drops, List.of()));
                if (!biomes.isEmpty()) wild.add(new SickleHarvestEntry(sickles, plant, drops, biomes));
            }
        }
        registration.addRecipes(SickleHarvestCategory.WILD, wild);
        registration.addRecipes(SickleHarvestCategory.POT, pot);
        ThermalShockReforged.LOGGER.debug("JEI sickle harvest registered: wild={}, pot={}", wild.size(), pot.size());
        var rows = new LinkedHashMap<String, List<NpcTradeEntry.Row>>();
        for (FloristPage page : FloristPage.values()) if (page.trade != null) trade(rows, "florist", page.trade);
        for (LyPage page : LyPage.values()) if (page.trade != null) trade(rows, "ly", page.trade);
        for (ShutterbugPage page : ShutterbugPage.values()) if (page.trade != null) trade(rows, "shutterbug", page.trade);
        for (ConciergePage page : ConciergePage.values()) {
            if (page.trade == null) continue;
            Component condition = null;
            if (page.trade.item().equals("panacea")) condition = requires("blasphemy_4");
            trade(rows, "concierge", page.trade.item(), page.trade.count(), page.trade.levels(), List.<String[]>of(d("adamantine_spice", String.valueOf(page.trade.spiceCount()))), condition);
        }
        for (FixedTrade row : FIXED_TRADES) trade(rows, row.npc(), row.item(), row.count(), row.levels(), row.costs(), null);
        var pages = new ArrayList<NpcTradeEntry>();
        for (var npc : rows.entrySet()) {
            var info = new ArrayList<Component>();
            for (String location : TRADE_LOCATIONS.getOrDefault(npc.getKey(), List.of())) info.add(location(location));
            if (npc.getKey().equals("lab_machinist")) info.add(requires("uneasy_alliance_1"));
            pages.add(new NpcTradeEntry(npc.getKey(), npc.getValue(), info));
        }
        registration.addRecipes(NpcTradeCategory.TYPE, pages);
        ThermalShockReforged.LOGGER.debug("JEI npc trades registered: pages={}, rows={}", pages.size(), rows.values().stream().mapToInt(List::size).sum());
        var fabrication = new ArrayList<MachineEntry>();
        var level = Minecraft.getInstance().level;
        if (level == null) ThermalShockReforged.LOGGER.warn("JEI fabrication skipped: client level unavailable");
        else {
            for (FabricatorPage page : FabricatorPage.values()) {
                if (page.isGuide()) continue;
                var holder = level.getRecipeManager().byKey(page.recipe).orElse(null);
                if (holder == null || !(holder.value() instanceof FabricationRecipe recipe)) {
                    ThermalShockReforged.LOGGER.warn("JEI fabrication recipe missing: {}", page.recipe);
                    continue;
                }
                var inputs = new ArrayList<MachineEntry.Slot>();
                for (int index = 0; index < 6; index++) {
                    if (index < recipe.materials().size()) inputs.add(slot(new ItemStack(recipe.materials().get(index).item(), recipe.materials().get(index).count())));
                    else inputs.add(new MachineEntry.Slot(List.of(), List.of()));
                }
                inputs.add(new MachineEntry.Slot(List.of(new ItemStack(recipe.energy().item(), recipe.energy().count())), List.of(Component.translatable(KEY + "machine.energy"))));
                fabrication.add(new MachineEntry(inputs, List.of(slot(recipe.getResultItem(level.registryAccess()))), null, List.of()));
            }
        }
        registration.addRecipes(MachineCategory.FABRICATOR, fabrication);
        var transmutation = new ArrayList<MachineEntry>();
        for (String[] pair : TRANSMUTATIONS) {
            var input = stack(pair[0], "1");
            var output = stack(pair[1], "1");
            if (!input.isEmpty() && !output.isEmpty()) transmutation.add(new MachineEntry(List.of(slot(input)), List.of(slot(output)), null, List.of()));
        }
        registration.addRecipes(MachineCategory.TRANSMUTATOR, transmutation);
        var extraction = new ArrayList<MachineEntry>();
        var core = List.<Component>of(Component.translatable(KEY + "machine.advancement", Component.translatable("advancements.blasphemy_3.title")));
        extraction.add(new MachineEntry(List.of(slot("infected_core_sample")), List.of(new MachineEntry.Slot(stacks(List.of("primordial_blood")), core)), null, List.of()));
        var heraldXp = List.<Component>of(Component.translatable(KEY + "machine.herald_xp", 10, "3~8", 5));
        extraction.add(new MachineEntry(List.of(slot("herald_key")), List.of(drop("helion_slag", "1-3", "30%")), Component.translatable(KEY + "machine.xp_levels", 10), heraldXp));
        extraction.add(xpEntry(List.of("infected_seeds", "blood_vessel", "ashen_bone"), 2, List.of()));
        extraction.add(xpEntry(List.of("beetle_claw", "infected_flesh", "tainted_energy"), 8, List.of()));
        extraction.add(xpEntry(List.of("pesticide"), 3, List.of(drop("ferrous_nugget", "1", "10%"))));
        extraction.add(xpEntry(List.of("neural_chip"), 20, List.of(drop("phobotite", "1-6", "30%"))));
        registration.addRecipes(MachineCategory.EXTRACTOR, extraction);
        var centrifuge = new ArrayList<MachineEntry>();
        centrifuge.add(new MachineEntry(List.of(slot("cryonic_energy"), slot("cryonic_energy"), slot("hearth_honey")), List.of(slot("pyrogenic_energy")), null, List.of()));
        centrifuge.add(new MachineEntry(List.of(slot("pyrogenic_energy"), slot("pyrogenic_energy"), slot("frost_pearl")), List.of(slot("cryonic_energy")), null, List.of()));
        registration.addRecipes(MachineCategory.CENTRIFUGE, centrifuge);
        var lapidary = new ArrayList<MachineEntry>();
        var gemNote = List.<Component>of(Component.translatable(KEY + "machine.advancement", Component.translatable("advancements.lapidary_get.title")));
        lapidary.add(new MachineEntry(List.of(slot("rough_ruby")), List.of(new MachineEntry.Slot(stacks(List.of("ruby")), gemNote)), null, List.of()));
        lapidary.add(new MachineEntry(List.of(slot("rough_bloodstone")), List.of(new MachineEntry.Slot(stacks(List.of("bloodstone")), gemNote)), null, List.of()));
        for (String[] row : LAPIDARY_TABLES) {
            var input = stack(row[0], "1");
            if (input.isEmpty()) continue;
            var notes = new ArrayList<Component>();
            if (row.length > 2 && row[2].equals("extra")) notes.add(Component.translatable(KEY + "machine.extra_roll"));
            if (row.length > 2 && row[2].equals("springling")) notes.add(Component.translatable(KEY + "machine.springling"));
            var outputs = loot(row[1]);
            if (!outputs.isEmpty()) lapidary.add(new MachineEntry(List.of(new MachineEntry.Slot(List.of(input), notes)), outputs, null, List.of()));
        }
        registration.addRecipes(MachineCategory.LAPIDARY, lapidary);
        var unlock = Component.translatable(KEY + "altar.unlock");
        var frigid = new MachineEntry.Slot(stacks(List.of("frigid_altar")), List.of(location("blizzard_outpost"), Component.translatable(KEY + "altar.frigid_unbreakable"), unlock));
        var ferrous = new MachineEntry.Slot(stacks(List.of("ferrous_altar")), List.of(Component.translatable(KEY + "altar.ferrous_craft"), unlock));
        var pylonCore = new MachineEntry.Slot(stacks(List.of("pylon_core")), List.of(Component.translatable(KEY + "altar.core_source"), Component.translatable(KEY + "altar.place")));
        var energy = new MachineEntry.Slot(List.of(stack("cryonic_energy", "3")), List.of(Component.translatable(KEY + "sickle_harvest.count", "1~3")));
        registration.addRecipes(AltarCategory.TYPE, List.of(new MachineEntry(List.of(pylonCore, frigid, ferrous), List.of(energy), null, List.of())));
        var cacheLines = new ArrayList<CacheLootEntry.Line>();
        var consumed = Component.translatable(KEY + "cache.consumed");
        var key = new MachineEntry.Slot(stacks(List.of("cryonic_key")), List.of(Component.translatable(KEY + "cache.key")));
        var trapped = new MachineEntry.Slot(stacks(List.of("trapped_cache_pad")), List.of(Component.translatable(KEY + "cache.trapped", Component.translatable("entity." + ThermalShockReforged.MOD_ID + ".bonerahna"))));
        var pads = new MachineEntry.Slot(stacks(List.of("ferrous_cache_pad", "shale_cache_pad")), List.of(Component.translatable(KEY + "cache.pad")));
        cache(cacheLines, "cryonic_cache", "blocks/cryonic_cache", List.of(key, trapped), consumed);
        cache(cacheLines, "super_cryonic_cache", "blocks/super_cryonic_cache", List.of(key), consumed);
        cache(cacheLines, "enchanted_cache", "enchanted_cache_loot", List.of(pads), consumed);
        registration.addRecipes(CacheLootCategory.TYPE, List.of(new CacheLootEntry(cacheLines)));
        ThermalShockReforged.LOGGER.debug("JEI cache loot registered: lines={}", cacheLines.size());
        ThermalShockReforged.LOGGER.debug("JEI machines registered: fabrication={}, transmutation={}, extraction={}, centrifuge={}, lapidary={}", fabrication.size(), transmutation.size(), extraction.size(), centrifuge.size(), lapidary.size());
    }

    private static void cache(List<CacheLootEntry.Line> lines, String cache, String table, List<MachineEntry.Slot> requirements, Component consumed) {
        var header = new ArrayList<MachineEntry.Slot>();
        header.add(new MachineEntry.Slot(stacks(List.of(cache)), List.of(consumed)));
        header.addAll(requirements);
        lines.add(new CacheLootEntry.Line(header, Component.translatable("block." + ThermalShockReforged.MOD_ID + "." + cache)));
        for (var pool : lootPools(table)) {
            for (int start = 0; start < pool.size(); start += CacheLootWidget.COLUMNS) lines.add(new CacheLootEntry.Line(pool.subList(start, Math.min(pool.size(), start + CacheLootWidget.COLUMNS)), null));
        }
    }

    private static MachineEntry.Slot slot(ItemStack stack) { return new MachineEntry.Slot(List.of(stack), List.of()); }
    private static MachineEntry.Slot slot(String id) { return new MachineEntry.Slot(stacks(List.of(id)), List.of()); }

    private static MachineEntry.Slot drop(String id, String count, String chance) {
        var notes = new ArrayList<Component>();
        notes.add(Component.translatable(KEY + "sickle_harvest.chance", chance));
        if (count.contains("-")) notes.add(Component.translatable(KEY + "sickle_harvest.count", count.replace("-", "~")));
        return new MachineEntry.Slot(List.of(stack(id, count)), notes);
    }

    private static MachineEntry xpEntry(List<String> inputs, int xp, List<MachineEntry.Slot> outputs) {
        return new MachineEntry(List.of(new MachineEntry.Slot(stacks(inputs), List.of())), outputs, Component.literal(String.valueOf(xp)), List.of(Component.translatable(KEY + "machine.xp_points", xp)));
    }

    private static List<MachineEntry.Slot> loot(String table) {
        var outputs = new ArrayList<MachineEntry.Slot>();
        for (var pool : lootPools(table)) outputs.addAll(pool);
        return outputs;
    }

    private static List<List<MachineEntry.Slot>> lootPools(String table) {
        var pools = new ArrayList<List<MachineEntry.Slot>>();
        String path = "/data/" + ThermalShockReforged.MOD_ID + "/loot_table/" + table + ".json";
        try (var stream = ThermalShockJeiPlugin.class.getResourceAsStream(path)) {
            if (stream == null) {
                ThermalShockReforged.LOGGER.warn("JEI loot table missing: {}", path);
                return pools;
            }
            var root = JsonParser.parseReader(new InputStreamReader(stream, StandardCharsets.UTF_8)).getAsJsonObject();
            for (JsonElement poolElement : root.getAsJsonArray("pools")) {
                var pool = poolElement.getAsJsonObject();
                String rolls = range(pool.get("rolls"));
                int total = 0;
                for (JsonElement entry : pool.getAsJsonArray("entries")) total += weight(entry.getAsJsonObject());
                var outputs = new ArrayList<MachineEntry.Slot>();
                pools.add(outputs);
                for (JsonElement entryElement : pool.getAsJsonArray("entries")) {
                    var entry = entryElement.getAsJsonObject();
                    if (!entry.get("type").getAsString().endsWith("item")) continue;
                    String count = "1";
                    boolean enchanted = false;
                    if (entry.has("functions")) {
                        for (JsonElement functionElement : entry.getAsJsonArray("functions")) {
                            var function = functionElement.getAsJsonObject();
                            String name = function.get("function").getAsString();
                            if (name.endsWith("set_count")) count = range(function.get("count"));
                            if (name.endsWith("enchant_with_levels")) enchanted = true;
                        }
                    }
                    String max = count.substring(count.lastIndexOf('~') + 1);
                    var stack = stack(entry.get("name").getAsString(), String.valueOf(Math.max(1, Integer.parseInt(max))));
                    if (stack.isEmpty()) continue;
                    var notes = new ArrayList<Component>();
                    notes.add(Component.translatable(KEY + "machine.rolls", rolls));
                    notes.add(Component.translatable(KEY + "machine.roll_chance", String.format(Locale.ROOT, "%.1f%%", weight(entry) * 100.0 / total)));
                    if (count.contains("~")) notes.add(Component.translatable(KEY + "sickle_harvest.count", count));
                    if (enchanted) notes.add(Component.translatable(KEY + "machine.enchanted"));
                    outputs.add(new MachineEntry.Slot(List.of(stack), notes));
                }
            }
        } catch (Exception exception) {
            ThermalShockReforged.LOGGER.warn("JEI loot table parse failed: {}", path, exception);
        }
        return pools;
    }

    private static int weight(JsonObject entry) {
        if (entry.has("weight")) return entry.get("weight").getAsInt();
        return 1;
    }

    private static String range(JsonElement element) {
        if (element.isJsonPrimitive()) return String.valueOf((int) element.getAsFloat());
        var object = element.getAsJsonObject();
        if (object.has("value")) return String.valueOf((int) object.get("value").getAsFloat());
        int min = (int) object.get("min").getAsFloat();
        int max = (int) object.get("max").getAsFloat();
        if (min == max) return String.valueOf(min);
        return min + "~" + max;
    }

    private static Component requires(String advancement) {
        return Component.translatable(KEY + "npc_trade.requires", Component.translatable("advancements." + advancement + ".title"));
    }

    private static void trade(Map<String, List<NpcTradeEntry.Row>> rows, String npc, NpcTrade trade) {
        var costs = new ArrayList<String[]>();
        if (trade.spiceCount() > 0) costs.add(new String[]{trade.spice(), String.valueOf(trade.spiceCount())});
        trade(rows, npc, trade.item(), trade.count(), trade.levels(), costs, null);
    }

    private static void trade(Map<String, List<NpcTradeEntry.Row>> rows, String npc, String item, int count, int levels, List<String[]> costs, Component condition) {
        var result = stack(item, String.valueOf(count));
        if (result.isEmpty()) return;
        var costStacks = new ArrayList<ItemStack>();
        for (String[] cost : costs) {
            if (Integer.parseInt(cost[1]) <= 0) continue;
            var stack = stack(cost[0], cost[1]);
            if (!stack.isEmpty()) costStacks.add(stack);
        }
        rows.computeIfAbsent(npc, key -> new ArrayList<>()).add(new NpcTradeEntry.Row(costStacks, levels, result, condition));
    }

    private static Component location(String location) {
        String[] parts = location.split("@");
        var name = Component.translatable("structure." + ThermalShockReforged.MOD_ID + "." + parts[0]);
        if (parts.length == 1) return Component.translatable(KEY + "npc_trade.location", name);
        if (parts[1].equals("natural")) return Component.translatable(KEY + "npc_trade.natural", Component.translatable("biome." + ThermalShockReforged.MOD_ID + "." + parts[0]));
        return Component.translatable(KEY + "npc_trade.location", Component.translatable(KEY + "npc_trade." + parts[1], name));
    }

    private static String[] d(String item, String amount) { return new String[]{item, amount}; }

    private static Component note(String amount) {
        if (amount.endsWith("%")) return Component.translatable("jei." + ThermalShockReforged.MOD_ID + ".sickle_harvest.chance", amount);
        if (amount.contains("-")) return Component.translatable("jei." + ThermalShockReforged.MOD_ID + ".sickle_harvest.count", amount.replace("-", "~"));
        return null;
    }

    private static ItemStack stack(String id, String amount) {
        ResourceLocation key;
        if (id.contains(":")) key = ResourceLocation.parse(id);
        else key = ResourceLocation.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, id);
        var item = BuiltInRegistries.ITEM.get(key);
        if (item == Items.AIR) {
            ThermalShockReforged.LOGGER.warn("JEI sickle harvest item missing: {}", key);
            return ItemStack.EMPTY;
        }
        int count = 1;
        if (!amount.endsWith("%")) {
            String[] parts = amount.split("-");
            count = Integer.parseInt(parts[parts.length - 1]);
        }
        return new ItemStack(item, count);
    }

    private static List<ItemStack> stacks(List<String> ids) {
        var result = new ArrayList<ItemStack>();
        for (String id : ids) {
            var stack = stack(id, "1");
            if (!stack.isEmpty()) result.add(stack);
        }
        return result;
    }

    private record Machine(mezz.jei.api.recipe.RecipeType<MachineEntry> type, List<String> blocks, MachineCategory.Layout layout) { }

    private record FixedTrade(String npc, String item, int count, int levels, List<String[]> costs) { }

    private record Crop(List<String> plant, List<String> biomes, List<String[]> base, List<String[]> claw, List<String[]> stratosilver) {
        List<List<String[]>> tiers() { return List.of(base, claw, stratosilver); }
    }
}
