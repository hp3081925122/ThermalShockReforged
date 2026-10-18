package com.hp.thermalshockreforged.content.tools;

import com.hp.thermalshockreforged.ThermalShockReforged;
import com.hp.thermalshockreforged.content.MaterialContent;
import com.hp.thermalshockreforged.content.terrain.TerrainContent;
import java.util.List;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Supplier;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.*;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Block;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ToolsContent {
    private static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(ThermalShockReforged.MOD_ID);
    private static final Map<String, DeferredItem<Item>> ENTRIES = new LinkedHashMap<>();

    static {
        register("bloodstone_axe", new NativeTier(1500, 8.0f, 7.0f, BlockTags.INCORRECT_FOR_DIAMOND_TOOL, 21, () -> Ingredient.of(MaterialContent.items().get("bloodstone").get())), (tier) -> new AxeItem(tier, new Item.Properties().attributes(DiggerItem.createAttributes(tier, 1.0f, -3.0f))));
        register("bloodstone_hoe", new NativeTier(1500, 8.0f, 2.0f, BlockTags.INCORRECT_FOR_DIAMOND_TOOL, 21, () -> Ingredient.of(MaterialContent.items().get("bloodstone").get())), (tier) -> new HoeItem(tier, new Item.Properties().attributes(DiggerItem.createAttributes(tier, 0, -3.0f))));
        register("bloodstone_pickaxe", new NativeTier(1500, 8.0f, 1.0f, BlockTags.INCORRECT_FOR_DIAMOND_TOOL, 21, () -> Ingredient.of(MaterialContent.items().get("bloodstone").get())), (tier) -> new PickaxeItem(tier, new Item.Properties().attributes(DiggerItem.createAttributes(tier, 1, -2.8f))));
        register("bloodstone_shovel", new NativeTier(1500, 8.0f, 1.0f, BlockTags.INCORRECT_FOR_DIAMOND_TOOL, 21, () -> Ingredient.of(MaterialContent.items().get("bloodstone").get())), (tier) -> new ShovelItem(tier, new Item.Properties().attributes(DiggerItem.createAttributes(tier, 1.0f, -2.8f))));
        register("bloodstone_sword", new NativeTier(1500, 10.0f, 2.0f, BlockTags.INCORRECT_FOR_DIAMOND_TOOL, 21, () -> Ingredient.of(MaterialContent.items().get("bloodstone").get())), (tier) -> new SwordItem(tier, new Item.Properties().attributes(SwordItem.createAttributes(tier, 3, -2.4f))));
        register("bone_axe", new NativeTier(100, 5.0f, 4.5f, BlockTags.INCORRECT_FOR_STONE_TOOL, 5, () -> Ingredient.of(MaterialContent.items().get("ashen_bone").get(), Items.BONE)), (tier) -> new DescribedAxe(tier, new Item.Properties().attributes(DiggerItem.createAttributes(tier, 1.0f, -3.0f)), "item.thermal_shock_reforged.bone_axe.description_0"));
        register("bone_pickaxe", new NativeTier(100, 5.0f, -0.5f, BlockTags.INCORRECT_FOR_STONE_TOOL, 5, () -> Ingredient.of(MaterialContent.items().get("ashen_bone").get(), Items.BONE)), (tier) -> new DescribedPickaxe(tier, new Item.Properties().attributes(DiggerItem.createAttributes(tier, 1, -2.8f)), "item.thermal_shock_reforged.bone_pickaxe.description_0"));
        register("bone_shovel", new NativeTier(100, 5.0f, -0.5f, BlockTags.INCORRECT_FOR_STONE_TOOL, 5, () -> Ingredient.of(MaterialContent.items().get("ashen_bone").get(), Items.BONE)), (tier) -> new DescribedShovel(tier, new Item.Properties().attributes(DiggerItem.createAttributes(tier, 1.0f, -2.8f)), "item.thermal_shock_reforged.bone_shovel.description_0"));
        register("bone_sword", new NativeTier(100, 5.0f, 0.5f, BlockTags.INCORRECT_FOR_STONE_TOOL, 5, () -> Ingredient.of(MaterialContent.items().get("ashen_bone").get(), Items.BONE)), (tier) -> new DescribedSword(tier, new Item.Properties().attributes(SwordItem.createAttributes(tier, 3, -2.2f).withModifierAdded(Attributes.ENTITY_INTERACTION_RANGE, new AttributeModifier(ResourceLocation.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, "bone_sword_reach"), 2, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND)), "item.thermal_shock_reforged.bone_sword.description_0", "item.thermal_shock_reforged.bone_sword.description_1"));
        register("carborundum_axe", new NativeTier(900, 15.0f, 9.0f, BlockTags.INCORRECT_FOR_DIAMOND_TOOL, 16, () -> Ingredient.of(MaterialContent.items().get("carborundum").get())), (tier) -> new AxeItem(tier, new Item.Properties().attributes(DiggerItem.createAttributes(tier, 1.0f, -2.8f))));
        register("carborundum_pickaxe", new NativeTier(900, 15.0f, 1.0f, BlockTags.INCORRECT_FOR_DIAMOND_TOOL, 16, () -> Ingredient.of(MaterialContent.items().get("carborundum").get())), (tier) -> new PickaxeItem(tier, new Item.Properties().attributes(DiggerItem.createAttributes(tier, 1, -2.8f))));
        register("carborundum_shovel", new NativeTier(900, 15.0f, 1.0f, BlockTags.INCORRECT_FOR_DIAMOND_TOOL, 16, () -> Ingredient.of(MaterialContent.items().get("carborundum").get())), (tier) -> new ShovelItem(tier, new Item.Properties().attributes(DiggerItem.createAttributes(tier, 1.0f, -2.8f))));
        register("carborundum_sword", new NativeTier(900, 15.0f, 3.5f, BlockTags.INCORRECT_FOR_DIAMOND_TOOL, 16, () -> Ingredient.of(MaterialContent.items().get("carborundum").get())), (tier) -> new SwordItem(tier, new Item.Properties().attributes(SwordItem.createAttributes(tier, 3, -2.4f))));
        register("cobalt_axe", new NativeTier(400, 7.0f, 5.0f, BlockTags.INCORRECT_FOR_DIAMOND_TOOL, 5, () -> Ingredient.of(TerrainContent.COBALT.get())), (tier) -> new CobaltAxe(tier, new Item.Properties().attributes(DiggerItem.createAttributes(tier, 1.0f, -3.0f))));
        register("ruby_axe", new NativeTier(1200, 10.0f, 7.0f, BlockTags.INCORRECT_FOR_DIAMOND_TOOL, 21, () -> Ingredient.of(MaterialContent.items().get("ruby").get())), (tier) -> new AxeItem(tier, new Item.Properties().attributes(DiggerItem.createAttributes(tier, 1.0f, -3.0f))));
        register("ruby_hoe", new NativeTier(1200, 10.0f, 2.0f, BlockTags.INCORRECT_FOR_DIAMOND_TOOL, 21, () -> Ingredient.of(MaterialContent.items().get("ruby").get())), (tier) -> new HoeItem(tier, new Item.Properties().attributes(DiggerItem.createAttributes(tier, 0, -3.0f))));
        register("ruby_pickaxe", new NativeTier(1200, 10.0f, 1.0f, BlockTags.INCORRECT_FOR_DIAMOND_TOOL, 21, () -> Ingredient.of(MaterialContent.items().get("ruby").get())), (tier) -> new PickaxeItem(tier, new Item.Properties().attributes(DiggerItem.createAttributes(tier, 1, -2.8f))));
        register("ruby_shovel", new NativeTier(1200, 10.0f, 1.0f, BlockTags.INCORRECT_FOR_DIAMOND_TOOL, 21, () -> Ingredient.of(MaterialContent.items().get("ruby").get())), (tier) -> new ShovelItem(tier, new Item.Properties().attributes(DiggerItem.createAttributes(tier, 1.0f, -2.8f))));
        register("ruby_sword", new NativeTier(1200, 10.0f, 2.0f, BlockTags.INCORRECT_FOR_DIAMOND_TOOL, 21, () -> Ingredient.of(MaterialContent.items().get("ruby").get())), (tier) -> new SwordItem(tier, new Item.Properties().attributes(SwordItem.createAttributes(tier, 3, -2.4f))));
        register("stratosilver_auger", new NativeTier(5000, 50.0f, 1.0f, BlockTags.INCORRECT_FOR_DIAMOND_TOOL, 8, () -> Ingredient.of(MaterialContent.items().get("stratus_alloy").get())), (tier) -> new DescribedShovel(tier, new Item.Properties().attributes(DiggerItem.createAttributes(tier, 1.0f, -2.5f)), "item.thermal_shock_reforged.stratosilver_auger.description_0"));
        register("stratosilver_chainsaw", new NativeTier(5000, 50.0f, 2.0f, BlockTags.INCORRECT_FOR_DIAMOND_TOOL, 8, () -> Ingredient.of(MaterialContent.items().get("stratus_alloy").get())), (tier) -> new DescribedAxe(tier, new Item.Properties().attributes(DiggerItem.createAttributes(tier, 1.0f, -1.0f)), "item.thermal_shock_reforged.stratosilver_chainsaw.description_0"));
        register("stratosilver_drill", new NativeTier(5800, 50.0f, 1.0f, BlockTags.INCORRECT_FOR_DIAMOND_TOOL, 8, () -> Ingredient.of(MaterialContent.items().get("stratus_alloy").get())), (tier) -> new DescribedPickaxe(tier, new Item.Properties().attributes(DiggerItem.createAttributes(tier, 1, -2.8f)), "item.thermal_shock_reforged.stratosilver_drill.description_0"));
    }

    private ToolsContent() { }

    public static void register(IEventBus bus) { ITEMS.register(bus); }

    public static Map<String, DeferredItem<Item>> items() { return Collections.unmodifiableMap(ENTRIES); }

    private static void register(String id, Tier tier, java.util.function.Function<Tier, Item> factory) {
        ENTRIES.put(id, ITEMS.register(id, () -> factory.apply(tier)));
    }

    private record NativeTier(int uses, float speed, float damage, TagKey<Block> incorrect, int enchantability, Supplier<Ingredient> repair) implements Tier {
        @Override public int getUses() { return uses; }
        @Override public float getSpeed() { return speed; }
        @Override public float getAttackDamageBonus() { return damage; }
        @Override public TagKey<Block> getIncorrectBlocksForDrops() { return incorrect; }
        @Override public int getEnchantmentValue() { return enchantability; }
        @Override public Ingredient getRepairIngredient() { return repair.get(); }
    }

    private static void tooltip(List<String> keys, List<Component> lines) { keys.forEach(key -> lines.add(Component.translatable(key))); }

    private static final class DescribedPickaxe extends PickaxeItem {
        private final List<String> descriptions;
        private DescribedPickaxe(Tier tier, Properties properties, String... descriptions) {
            super(tier, properties);
            this.descriptions = List.of(descriptions);
        }
        @Override
        public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> lines, TooltipFlag flag) {
            super.appendHoverText(stack, context, lines, flag);
            tooltip(descriptions, lines);
        }
    }

    private static final class DescribedAxe extends AxeItem {
        private final List<String> descriptions;
        private DescribedAxe(Tier tier, Properties properties, String... descriptions) {
            super(tier, properties);
            this.descriptions = List.of(descriptions);
        }
        @Override
        public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> lines, TooltipFlag flag) {
            super.appendHoverText(stack, context, lines, flag);
            tooltip(descriptions, lines);
        }
    }

    private static final class DescribedShovel extends ShovelItem {
        private final List<String> descriptions;
        private DescribedShovel(Tier tier, Properties properties, String... descriptions) {
            super(tier, properties);
            this.descriptions = List.of(descriptions);
        }
        @Override
        public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> lines, TooltipFlag flag) {
            super.appendHoverText(stack, context, lines, flag);
            tooltip(descriptions, lines);
        }
    }

    private static final class DescribedSword extends SwordItem {
        private final List<String> descriptions;
        private DescribedSword(Tier tier, Properties properties, String... descriptions) {
            super(tier, properties);
            this.descriptions = List.of(descriptions);
        }
        @Override
        public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> lines, TooltipFlag flag) {
            super.appendHoverText(stack, context, lines, flag);
            tooltip(descriptions, lines);
        }
    }
}
