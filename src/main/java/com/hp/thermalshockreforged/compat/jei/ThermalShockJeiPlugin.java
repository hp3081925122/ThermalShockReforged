package com.hp.thermalshockreforged.compat.jei;

import com.hp.thermalshockreforged.ThermalShockReforged;
import com.hp.thermalshockreforged.content.fabrication.FabricationRecipe;
import com.hp.thermalshockreforged.content.fabrication.FabricatorPage;
import com.hp.thermalshockreforged.content.recipe.NpcTradeRecipe;
import com.hp.thermalshockreforged.content.recipe.RecipeContent;
import com.hp.thermalshockreforged.content.recipe.AssemblerRecipe;
import com.hp.thermalshockreforged.entity.labmachinist.LabPage;
import com.hp.thermalshockreforged.entity.whisper.WhisperPage;
import com.mojang.serialization.JsonOps;
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
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
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
    private static final Identifier UID = Identifier.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, "jei_plugin");
    private static final String KEY = "jei." + ThermalShockReforged.MOD_ID + ".";
    private static final List<Machine> MACHINES = List.of(
            new Machine(MachineCategory.FABRICATOR, List.of("fabricator"), new MachineCategory.Layout(new int[][]{{0, 0}, {18, 0}, {36, 0}, {0, 18}, {18, 18}, {36, 18}, {18, 40}}, 62, 20, 92, 20, 1, 110, 58)),
            new Machine(MachineCategory.TRANSMUTATOR, List.of("transmutator"), new MachineCategory.Layout(new int[][]{{0, 20}}, 24, 20, 54, 20, 1, 72, 40)),
            new Machine(MachineCategory.EXTRACTOR, List.of("extractor"), new MachineCategory.Layout(new int[][]{{0, 20}}, 24, 20, 54, 20, 1, 72, 52)),
            new Machine(MachineCategory.CENTRIFUGE, List.of("centrifuge"), new MachineCategory.Layout(new int[][]{{0, 20}, {18, 2}, {36, 20}}, 60, 20, 90, 20, 1, 108, 40)),
            new Machine(MachineCategory.LAPIDARY, List.of("lapidarian_workbench"), new MachineCategory.Layout(new int[][]{{0, 20}}, 24, 20, 54, 2, 6, 162, 56)),
            new Machine(MachineCategory.ASSEMBLER, List.of("assembler"), new MachineCategory.Layout(new int[][]{{0, 0}, {18, 0}, {36, 0}, {54, 0}, {0, 20}, {18, 20}, {36, 20}, {54, 20}}, 72, 20, 102, 20, 1, 132, 58)));
    private static final Map<String, List<String>> TRADE_LOCATIONS = Map.of(
            "florist", List.of("shalespring@fixed_loqendia"),
            "shutterbug", List.of("shalespring@fixed_loqendia"),
            "ly", List.of("kiosk", "lavender_palace@fixed_aqeron"),
            "concierge", List.of("cathedral"),
            "whisper", List.of("large_campsite", "stardust_garden@natural"),
            "lab_machinist", List.of("machinist_lab"));
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
    public Identifier getPluginUid() { return UID; }

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
        var level = Minecraft.getInstance().level;
        if (level == null) {
            ThermalShockReforged.LOGGER.warn("JEI data recipes skipped: client level unavailable");
            return;
        }
        var rows = new LinkedHashMap<String, List<NpcTradeEntry.Row>>();
        for (FloristPage page : FloristPage.values()) if (page.trade != null) trade(rows, level, "florist", page.trade);
        for (LyPage page : LyPage.values()) if (page.trade != null) trade(rows, level, "ly", page.trade);
        for (ShutterbugPage page : ShutterbugPage.values()) if (page.trade != null) trade(rows, level, "shutterbug", page.trade);
        for (ConciergePage page : ConciergePage.values()) if (page.trade != null) trade(rows, level, "concierge", page.trade);
        for (WhisperPage page : WhisperPage.values()) if (page.inventory) trade(rows, level, "whisper", page.id);
        for (LabPage page : LabPage.values()) if (page.trade()) trade(rows, level, "lab_machinist", page.id);
        var pages = new ArrayList<NpcTradeEntry>();
        for (var npc : rows.entrySet()) {
            var info = new ArrayList<Component>();
            for (String location : TRADE_LOCATIONS.getOrDefault(npc.getKey(), List.of())) info.add(location(location));
            if (npc.getKey().equals("lab_machinist")) info.add(requires(RecipeContent.id("uneasy_alliance_1")));
            pages.add(new NpcTradeEntry(npc.getKey(), npc.getValue(), info));
        }
        registration.addRecipes(NpcTradeCategory.TYPE, pages);
        ThermalShockReforged.LOGGER.debug("JEI npc trades registered: pages={}, rows={}", pages.size(), rows.values().stream().mapToInt(List::size).sum());
        var fabrication = new ArrayList<MachineEntry>();
        for (FabricatorPage page : FabricatorPage.values()) {
            if (page.isGuide()) continue;
            var holder = RecipeContent.recipes(level).byKey(net.minecraft.resources.ResourceKey.create(net.minecraft.core.registries.Registries.RECIPE, page.recipe));
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
            fabrication.add(new MachineEntry(inputs, List.of(slot(recipe.result())), null, List.of()));
        }
        registration.addRecipes(MachineCategory.FABRICATOR, fabrication);
        var transmutation = new ArrayList<MachineEntry>();
        for (var holder : RecipeContent.TRANSMUTATION.all(level)) transmutation.add(new MachineEntry(List.of(slot(holder.value().ingredient())), List.of(slot(holder.value().result().copy())), null, List.of()));
        registration.addRecipes(MachineCategory.TRANSMUTATOR, transmutation);
        var extraction = new ArrayList<MachineEntry>();
        for (var holder : RecipeContent.EXTRACTION.all(level)) {
            var recipe = holder.value();
            var inputNotes = new ArrayList<Component>();
            if (recipe.advancement().isPresent()) inputNotes.add(Component.translatable(KEY + "machine.advancement", advancementTitle(recipe.advancement().get())));
            var outputs = new ArrayList<MachineEntry.Slot>();
            for (var output : recipe.outputs()) {
                var notes = new ArrayList<Component>();
                if (!output.guaranteed()) notes.add(Component.translatable(KEY + "sickle_harvest.chance", percent(output.chance())));
                if (output.repeat().minInclusive() != output.repeat().maxInclusive()) notes.add(Component.translatable(KEY + "sickle_harvest.count", range(output.repeat())));
                outputs.add(new MachineEntry.Slot(List.of(output.item().copyWithCount(output.item().getCount() * Math.max(1, output.repeat().maxInclusive()))), notes));
            }
            Component xp = null;
            var xpTooltip = new ArrayList<Component>();
            if (recipe.levels() > 0) {
                xp = Component.translatable(KEY + "machine.xp_levels", recipe.levels());
                xpTooltip.add(Component.translatable(KEY + "machine.levels_gain", recipe.levels()));
            }
            if (recipe.experience().isPresent()) {
                var experience = recipe.experience().get();
                var orbs = experience.orbs();
                if (orbs.minInclusive() == 1 && orbs.maxInclusive() == 1) xpTooltip.add(Component.translatable(KEY + "machine.xp_points", experience.value()));
                else xpTooltip.add(Component.translatable(KEY + "machine.xp_orbs", range(orbs), experience.value()));
                if (xp == null && orbs.minInclusive() == orbs.maxInclusive()) xp = Component.literal(String.valueOf(experience.value() * orbs.minInclusive()));
                if (xp == null) xp = Component.literal(experience.value() + "×" + range(orbs));
            }
            extraction.add(new MachineEntry(List.of(new MachineEntry.Slot(recipe.ingredient().items().map(ItemStack::new).toList(), inputNotes)), outputs, xp, xpTooltip));
        }
        registration.addRecipes(MachineCategory.EXTRACTOR, extraction);
        var centrifuge = new ArrayList<MachineEntry>();
        for (var holder : RecipeContent.CENTRIFUGE.all(level)) {
            var recipe = holder.value();
            centrifuge.add(new MachineEntry(List.of(slot(recipe.energy().get(0)), slot(recipe.energy().get(1)), slot(recipe.ingredient())), List.of(slot(recipe.result().copy())), null, List.of()));
        }
        registration.addRecipes(MachineCategory.CENTRIFUGE, centrifuge);
        var lapidary = new ArrayList<MachineEntry>();
        for (var holder : RecipeContent.LAPIDARY.all(level)) {
            var recipe = holder.value();
            var notes = new ArrayList<Component>();
            if (recipe.extraRollChance() > 0) notes.add(Component.translatable(KEY + "machine.extra_roll", percent(recipe.extraRollChance())));
            if (recipe.springlingChance() > 0) notes.add(Component.translatable(KEY + "machine.springling", percent(recipe.springlingChance())));
            var outputs = new ArrayList<MachineEntry.Slot>();
            if (recipe.lootTable().isPresent()) outputs.addAll(loot(recipe.lootTable().get().identifier()));
            if (recipe.result().isPresent()) {
                var resultNotes = new ArrayList<Component>();
                if (recipe.advancement().isPresent()) resultNotes.add(Component.translatable(KEY + "machine.advancement", advancementTitle(recipe.advancement().get())));
                outputs.add(new MachineEntry.Slot(List.of(recipe.result().get().copy()), resultNotes));
            }
            if (!outputs.isEmpty()) lapidary.add(new MachineEntry(List.of(new MachineEntry.Slot(recipe.ingredient().items().map(ItemStack::new).toList(), notes)), outputs, null, List.of()));
        }
        registration.addRecipes(MachineCategory.LAPIDARY, lapidary);
        var assembly = new ArrayList<MachineEntry>();
        for (var holder : RecipeContent.ASSEMBLER.all(level)) {
            var recipe = holder.value();
            var inputs = new ArrayList<MachineEntry.Slot>();
            for (var cost : recipe.materials()) inputs.add(slot(new ItemStack(cost.item().value(), cost.count())));
            assembly.add(new MachineEntry(inputs, List.of(slot(recipe.result())), null, List.of()));
        }
        registration.addRecipes(MachineCategory.ASSEMBLER, assembly);
        var altars = new ArrayList<MachineEntry>();
        var unlock = Component.translatable(KEY + "altar.unlock");
        for (var holder : RecipeContent.ALTAR_CONVERSION.all(level)) {
            var recipe = holder.value();
            var inputs = new ArrayList<MachineEntry.Slot>();
            var coreNotes = new ArrayList<Component>();
            if (recipe.core().is(RecipeContent.id("pylon_core"))) {
                coreNotes.add(Component.translatable(KEY + "altar.core_source"));
                coreNotes.add(Component.translatable(KEY + "altar.place"));
            }
            inputs.add(new MachineEntry.Slot(List.of(new ItemStack(recipe.core().value())), coreNotes));
            for (var altar : recipe.altars()) {
                var altarNotes = new ArrayList<Component>();
                if (altar.is(RecipeContent.id("frigid_altar"))) {
                    altarNotes.add(location("blizzard_outpost"));
                    altarNotes.add(Component.translatable(KEY + "altar.frigid_unbreakable"));
                }
                if (altar.is(RecipeContent.id("ferrous_altar"))) altarNotes.add(Component.translatable(KEY + "altar.ferrous_craft"));
                altarNotes.add(unlock);
                inputs.add(new MachineEntry.Slot(List.of(new ItemStack(altar.value())), altarNotes));
            }
            var outputs = loot(recipe.lootTable().identifier());
            if (!outputs.isEmpty()) altars.add(new MachineEntry(inputs, outputs, null, List.of()));
        }
        registration.addRecipes(AltarCategory.TYPE, altars);
        var caches = new ArrayList<CacheLootEntry>();
        var consumed = Component.translatable(KEY + "cache.consumed");
        var key = new MachineEntry.Slot(stacks(List.of("cryonic_key")), List.of(Component.translatable(KEY + "cache.key")));
        var trapped = new MachineEntry.Slot(stacks(List.of("trapped_cache_pad")), List.of(Component.translatable(KEY + "cache.trapped", Component.translatable("entity." + ThermalShockReforged.MOD_ID + ".bonerahna"))));
        var pads = new MachineEntry.Slot(stacks(List.of("ferrous_cache_pad", "shale_cache_pad")), List.of(Component.translatable(KEY + "cache.pad")));
        caches.add(cache("cryonic_cache", "blocks/cryonic_cache", List.of(key, trapped), consumed));
        caches.add(cache("super_cryonic_cache", "blocks/super_cryonic_cache", List.of(key), consumed));
        caches.add(cache("enchanted_cache", "enchanted_cache_loot", List.of(pads), consumed));
        registration.addRecipes(CacheLootCategory.TYPE, caches);
        ThermalShockReforged.LOGGER.debug("JEI data recipes registered: fabrication={}, transmutation={}, extraction={}, centrifuge={}, lapidary={}, assembler={}, altar={}, caches={}", fabrication.size(), transmutation.size(), extraction.size(), centrifuge.size(), lapidary.size(), assembly.size(), altars.size(), caches.size());
    }

    private static CacheLootEntry cache(String cache, String table, List<MachineEntry.Slot> requirements, Component consumed) {
        var lines = new ArrayList<CacheLootEntry.Line>();
        var header = new ArrayList<MachineEntry.Slot>();
        header.add(new MachineEntry.Slot(stacks(List.of(cache)), List.of(consumed)));
        header.addAll(requirements);
        lines.add(new CacheLootEntry.Line(header, Component.translatable("block." + ThermalShockReforged.MOD_ID + "." + cache)));
        for (var pool : lootPools(RecipeContent.id(table))) {
            for (int start = 0; start < pool.size(); start += CacheLootWidget.COLUMNS) lines.add(new CacheLootEntry.Line(pool.subList(start, Math.min(pool.size(), start + CacheLootWidget.COLUMNS)), null));
        }
        return new CacheLootEntry(lines);
    }

    private static MachineEntry.Slot slot(Ingredient ingredient) { return new MachineEntry.Slot(ingredient.items().map(ItemStack::new).toList(), List.of()); }

    private static Component advancementTitle(Identifier id) { return Component.translatable("advancements." + id.getPath() + ".title"); }

    private static String percent(float chance) {
        String text = String.format(Locale.ROOT, "%.1f", chance * 100);
        if (text.endsWith(".0")) text = text.substring(0, text.length() - 2);
        return text + "%";
    }

    private static String range(IntProvider provider) {
        if (provider.minInclusive() == provider.maxInclusive()) return String.valueOf(provider.minInclusive());
        return provider.minInclusive() + "~" + provider.maxInclusive();
    }

    private static void trade(Map<String, List<NpcTradeEntry.Row>> rows, Level level, String npc, String page) {
        var recipe = RecipeContent.NPC_TRADE.get(level, NpcTradeRecipe.id(page));
        if (recipe == null) {
            ThermalShockReforged.LOGGER.warn("JEI npc trade recipe missing: {}", NpcTradeRecipe.id(page));
            return;
        }
        var costs = new ArrayList<ItemStack>();
        for (var cost : recipe.costs()) costs.add(new ItemStack(cost.item(), cost.count()));
        Component condition = null;
        if (recipe.requires().isPresent()) condition = requires(recipe.requires().get());
        rows.computeIfAbsent(npc, ignored -> new ArrayList<>()).add(new NpcTradeEntry.Row(costs, recipe.levels(), recipe.result().copy(), condition));
    }

    private static MachineEntry.Slot slot(ItemStack stack) { return new MachineEntry.Slot(List.of(stack), List.of()); }

    private static List<MachineEntry.Slot> loot(Identifier table) {
        var outputs = new ArrayList<MachineEntry.Slot>();
        for (var pool : lootPools(table)) outputs.addAll(pool);
        return outputs;
    }

    private static JsonObject lootJson(Identifier table) {
        var server = Minecraft.getInstance().getSingleplayerServer();
        if (server != null) {
            var loaded = server.reloadableRegistries().getLootTable(ResourceKey.create(Registries.LOOT_TABLE, table));
            if (loaded != LootTable.EMPTY) {
                var encoded = LootTable.DIRECT_CODEC.encodeStart(server.registryAccess().createSerializationContext(JsonOps.INSTANCE), loaded).result();
                if (encoded.isPresent() && encoded.get().isJsonObject()) return encoded.get().getAsJsonObject();
                ThermalShockReforged.LOGGER.warn("JEI loot table encode failed, falling back to bundled file: {}", table);
            }
        }
        String path = "/data/" + table.getNamespace() + "/loot_table/" + table.getPath() + ".json";
        try (var stream = ThermalShockJeiPlugin.class.getResourceAsStream(path)) {
            if (stream == null) {
                ThermalShockReforged.LOGGER.warn("JEI loot table missing: {}", path);
                return null;
            }
            return JsonParser.parseReader(new InputStreamReader(stream, StandardCharsets.UTF_8)).getAsJsonObject();
        } catch (Exception exception) {
            ThermalShockReforged.LOGGER.warn("JEI loot table read failed: {}", path, exception);
            return null;
        }
    }

    private static List<List<MachineEntry.Slot>> lootPools(Identifier table) {
        var pools = new ArrayList<List<MachineEntry.Slot>>();
        var root = lootJson(table);
        if (root == null || !root.has("pools")) return pools;
        try {
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
            ThermalShockReforged.LOGGER.warn("JEI loot table parse failed: {}", table, exception);
        }
        return pools;
    }

    private static int weight(JsonObject entry) {
        if (entry.has("weight")) return entry.get("weight").getAsInt();
        return 1;
    }

    private static String range(JsonElement element) {
        if (element.isJsonPrimitive() || element.getAsJsonObject().has("value")) return String.valueOf(number(element));
        var object = element.getAsJsonObject();
        int min = number(object.get("min"));
        int max = number(object.get("max"));
        if (min == max) return String.valueOf(min);
        return min + "~" + max;
    }

    private static int number(JsonElement element) {
        if (element.isJsonPrimitive()) return (int) element.getAsFloat();
        return (int) element.getAsJsonObject().get("value").getAsFloat();
    }

    private static Component requires(Identifier advancement) {
        return Component.translatable(KEY + "npc_trade.requires", advancementTitle(advancement));
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
        Identifier key;
        if (id.contains(":")) key = Identifier.parse(id);
        else key = Identifier.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, id);
        var item = BuiltInRegistries.ITEM.getValue(key);
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

    private record Crop(List<String> plant, List<String> biomes, List<String[]> base, List<String[]> claw, List<String[]> stratosilver) {
        List<List<String[]>> tiers() { return List.of(base, claw, stratosilver); }
    }
}
