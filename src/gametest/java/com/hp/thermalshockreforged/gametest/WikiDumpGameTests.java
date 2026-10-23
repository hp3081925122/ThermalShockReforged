package com.hp.thermalshockreforged.gametest;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.hp.thermalshockreforged.ThermalShockReforged;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.EmptyBlockGetter;
import net.minecraft.core.BlockPos;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

@GameTestHolder(ThermalShockReforged.MOD_ID)
@PrefixGameTestTemplate(false)
public final class WikiDumpGameTests {
    @GameTest(template = "migration_empty")
    public static void dumpWikiData(GameTestHelper helper) {
        JsonObject root = new JsonObject();
        JsonObject tabs = new JsonObject();
        try {
            CreativeModeTabs.tryRebuildTabContents(FeatureFlags.REGISTRY.allFlags(), true, helper.getLevel().registryAccess());
            for (CreativeModeTab tab : BuiltInRegistries.CREATIVE_MODE_TAB) {
                var key = BuiltInRegistries.CREATIVE_MODE_TAB.getKey(tab);
                if (key == null || !key.getNamespace().equals(ThermalShockReforged.MOD_ID)) continue;
                JsonArray list = new JsonArray();
                for (ItemStack stack : tab.getDisplayItems()) list.add(BuiltInRegistries.ITEM.getKey(stack.getItem()).toString());
                tabs.add(key.toString() + "|" + tab.getDisplayName().getString(), list);
            }
        } catch (Throwable t) {
            root.addProperty("tabError", t.toString());
        }
        root.add("tabs", tabs);
        JsonArray items = new JsonArray();
        for (var item : BuiltInRegistries.ITEM) {
            var id = BuiltInRegistries.ITEM.getKey(item);
            if (!id.getNamespace().equals(ThermalShockReforged.MOD_ID)) continue;
            JsonObject o = new JsonObject();
            ItemStack stack = item.getDefaultInstance();
            o.addProperty("id", id.toString());
            o.addProperty("class", item.getClass().getSimpleName());
            o.addProperty("maxStack", stack.getMaxStackSize());
            o.addProperty("maxDamage", stack.getMaxDamage());
            o.addProperty("rarity", stack.getRarity().name());
            o.addProperty("fireResistant", stack.has(DataComponents.FIRE_RESISTANT));
            var food = stack.get(DataComponents.FOOD);
            if (food != null) {
                JsonObject f = new JsonObject();
                f.addProperty("nutrition", food.nutrition());
                f.addProperty("saturation", food.saturation());
                f.addProperty("alwaysEat", food.canAlwaysEat());
                f.addProperty("eatSeconds", food.eatSeconds());
                JsonArray effects = new JsonArray();
                for (var e : food.effects()) effects.add(e.effect().getEffect().getRegisteredName() + "|" + e.effect().getAmplifier() + "|" + e.effect().getDuration() + "|" + e.probability());
                f.add("effects", effects);
                o.add("food", f);
            }
            JsonArray hover = new JsonArray();
            try {
                java.util.List<net.minecraft.network.chat.Component> lines = new java.util.ArrayList<>();
                item.appendHoverText(stack, net.minecraft.world.item.Item.TooltipContext.of(helper.getLevel()), lines, net.minecraft.world.item.TooltipFlag.NORMAL);
                for (var line : lines) hover.add(net.minecraft.network.chat.ComponentSerialization.CODEC.encodeStart(helper.getLevel().registryAccess().createSerializationContext(com.mojang.serialization.JsonOps.INSTANCE), line).getOrThrow());
            } catch (Throwable t) {
                hover.add("error|" + t);
            }
            o.add("tooltip", hover);
            JsonArray attrs = new JsonArray();
            try {
                var mods = stack.getAttributeModifiers();
                if (mods.modifiers().isEmpty()) mods = item.getDefaultAttributeModifiers(stack);
                for (var entry : mods.modifiers()) {
                    attrs.add(entry.attribute().getRegisteredName() + "|" + entry.modifier().amount() + "|" + entry.modifier().operation().name() + "|" + entry.slot().getSerializedName());
                }
            } catch (Throwable t) {
                attrs.add("error|" + t);
            }
            o.add("attributes", attrs);
            var tool = stack.get(DataComponents.TOOL);
            if (tool != null) {
                float best = tool.defaultMiningSpeed();
                for (var rule : tool.rules()) if (rule.speed().isPresent() && rule.speed().get() > best) best = rule.speed().get();
                o.addProperty("toolSpeed", best);
            }
            if (item instanceof BlockItem blockItem) {
                var block = blockItem.getBlock();
                var state = block.defaultBlockState();
                JsonObject b = new JsonObject();
                b.addProperty("block", BuiltInRegistries.BLOCK.getKey(block).toString());
                b.addProperty("blockClass", block.getClass().getSimpleName());
                b.addProperty("hardness", block.defaultDestroyTime());
                b.addProperty("resistance", block.getExplosionResistance());
                try {
                    b.addProperty("light", state.getLightEmission(EmptyBlockGetter.INSTANCE, BlockPos.ZERO));
                } catch (Throwable t) {
                    b.addProperty("light", -1);
                }
                b.addProperty("requiresTool", state.requiresCorrectToolForDrops());
                JsonArray tags = new JsonArray();
                if (state.is(BlockTags.MINEABLE_WITH_PICKAXE)) tags.add("pickaxe");
                if (state.is(BlockTags.MINEABLE_WITH_AXE)) tags.add("axe");
                if (state.is(BlockTags.MINEABLE_WITH_SHOVEL)) tags.add("shovel");
                if (state.is(BlockTags.MINEABLE_WITH_HOE)) tags.add("hoe");
                if (state.is(BlockTags.NEEDS_STONE_TOOL)) tags.add("needs_stone");
                if (state.is(BlockTags.NEEDS_IRON_TOOL)) tags.add("needs_iron");
                if (state.is(BlockTags.NEEDS_DIAMOND_TOOL)) tags.add("needs_diamond");
                b.add("tags", tags);
                o.add("block", b);
            }
            items.add(o);
        }
        root.add("items", items);
        JsonArray blocks = new JsonArray();
        for (var block : BuiltInRegistries.BLOCK) {
            var id = BuiltInRegistries.BLOCK.getKey(block);
            if (!id.getNamespace().equals(ThermalShockReforged.MOD_ID)) continue;
            if (block.asItem() != net.minecraft.world.item.Items.AIR) continue;
            JsonObject b = new JsonObject();
            b.addProperty("id", id.toString());
            b.addProperty("blockClass", block.getClass().getSimpleName());
            b.addProperty("hardness", block.defaultDestroyTime());
            b.addProperty("resistance", block.getExplosionResistance());
            blocks.add(b);
        }
        root.add("blocksWithoutItem", blocks);
        try {
            Path out = Path.of(System.getProperty("tsr.wikiDump", "D:/AI临时文件/tsr_wiki_dump.json"));
            Files.createDirectories(out.getParent());
            Files.writeString(out, new GsonBuilder().setPrettyPrinting().create().toJson(root), StandardCharsets.UTF_8);
            ThermalShockReforged.LOGGER.info("Wiki dump written to {}", out);
        } catch (Exception e) {
            helper.fail("Wiki dump failed: " + e);
            return;
        }
        helper.succeed();
    }
}
