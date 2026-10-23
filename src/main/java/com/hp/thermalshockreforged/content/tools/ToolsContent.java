package com.hp.thermalshockreforged.content.tools;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.component.TooltipDisplay;
import java.util.function.Consumer;

import com.hp.thermalshockreforged.ThermalShockReforged;
import com.hp.thermalshockreforged.content.MaterialContent;
import com.hp.thermalshockreforged.content.terrain.TerrainContent;
import com.hp.thermalshockreforged.content.weapons.HeldWeaponState;
import java.util.List;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Supplier;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.*;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Block;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ToolsContent {
    private static final DeferredRegister.Items ITEMS = com.hp.thermalshockreforged.ModRegistration.items(ThermalShockReforged.MOD_ID);
    private static final Map<String, DeferredItem<Item>> ENTRIES = new LinkedHashMap<>();

    static {
        register("ghostglass_sword", new NativeTier(500, 10, -1.5F, BlockTags.INCORRECT_FOR_IRON_TOOL, 19, Tier.repairTag("malachite")), tier -> new GhostglassSwordItem(tier, 3, 2, com.hp.thermalshockreforged.ModRegistration.item()));
        register("bloodstone_axe", new NativeTier(1500, 8.0f, 7.0f, BlockTags.INCORRECT_FOR_DIAMOND_TOOL, 21, Tier.repairTag("bloodstone")), (tier) -> new AxeItem(tier, 1.0f, -3.0f, com.hp.thermalshockreforged.ModRegistration.item()));
        register("bloodstone_hoe", new NativeTier(1500, 8.0f, 2.0f, BlockTags.INCORRECT_FOR_DIAMOND_TOOL, 21, Tier.repairTag("bloodstone")), (tier) -> new HoeItem(tier, 0, -3.0f, com.hp.thermalshockreforged.ModRegistration.item()));
        register("bloodstone_pickaxe", new NativeTier(1500, 8.0f, 1.0f, BlockTags.INCORRECT_FOR_DIAMOND_TOOL, 21, Tier.repairTag("bloodstone")), (tier) -> new PickaxeItem(tier, 1, -2.8f, com.hp.thermalshockreforged.ModRegistration.item()));
        register("bloodstone_shovel", new NativeTier(1500, 8.0f, 1.0f, BlockTags.INCORRECT_FOR_DIAMOND_TOOL, 21, Tier.repairTag("bloodstone")), (tier) -> new ShovelItem(tier, 1.0f, -2.8f, com.hp.thermalshockreforged.ModRegistration.item()));
        register("bloodstone_sword", new NativeTier(1500, 10.0f, 2.0f, BlockTags.INCORRECT_FOR_DIAMOND_TOOL, 21, Tier.repairTag("bloodstone")), (tier) -> new SwordItem(tier, 3, -2.4f, com.hp.thermalshockreforged.ModRegistration.item()));
        register("bone_axe", new NativeTier(100, 5.0f, 4.5f, BlockTags.INCORRECT_FOR_STONE_TOOL, 5, Tier.repairTag("bone")), (tier) -> new DescribedAxe(tier, 1.0f, -3.0f, com.hp.thermalshockreforged.ModRegistration.item(), "item.thermal_shock_reforged.bone_axe.description_0"));
        register("bone_pickaxe", new NativeTier(100, 5.0f, -0.5f, BlockTags.INCORRECT_FOR_STONE_TOOL, 5, Tier.repairTag("bone")), (tier) -> new DescribedPickaxe(tier, 1, -2.8f, com.hp.thermalshockreforged.ModRegistration.item(), "item.thermal_shock_reforged.bone_pickaxe.description_0"));
        register("bone_shovel", new NativeTier(100, 5.0f, -0.5f, BlockTags.INCORRECT_FOR_STONE_TOOL, 5, Tier.repairTag("bone")), (tier) -> new DescribedShovel(tier, 1.0f, -2.8f, com.hp.thermalshockreforged.ModRegistration.item(), "item.thermal_shock_reforged.bone_shovel.description_0"));
        register("bone_sword", new NativeTier(100, 5.0f, 0.5f, BlockTags.INCORRECT_FOR_STONE_TOOL, 5, Tier.repairTag("bone")), (tier) -> new DescribedSword(tier, 3, -2.2f, com.hp.thermalshockreforged.ModRegistration.item(), 2, "item.thermal_shock_reforged.bone_sword.description_0", "item.thermal_shock_reforged.bone_sword.description_1"));
        register("ghostglass_pick", new NativeTier(422, 29, 0, BlockTags.INCORRECT_FOR_IRON_TOOL, 19, Tier.repairTag("malachite")), tier -> new GhostglassPickItem(tier, 1, -2.8F, com.hp.thermalshockreforged.ModRegistration.item()));
        register("carborundum_axe", new NativeTier(900, 15.0f, 9.0f, BlockTags.INCORRECT_FOR_DIAMOND_TOOL, 16, Tier.repairTag("carborundum")), (tier) -> new AxeItem(tier, 1.0f, -2.8f, com.hp.thermalshockreforged.ModRegistration.item()));
        register("carborundum_pickaxe", new NativeTier(900, 15.0f, 1.0f, BlockTags.INCORRECT_FOR_DIAMOND_TOOL, 16, Tier.repairTag("carborundum")), (tier) -> new PickaxeItem(tier, 1, -2.8f, com.hp.thermalshockreforged.ModRegistration.item()));
        register("carborundum_shovel", new NativeTier(900, 15.0f, 1.0f, BlockTags.INCORRECT_FOR_DIAMOND_TOOL, 16, Tier.repairTag("carborundum")), (tier) -> new ShovelItem(tier, 1.0f, -2.8f, com.hp.thermalshockreforged.ModRegistration.item()));
        register("carborundum_sword", new NativeTier(900, 15.0f, 3.5f, BlockTags.INCORRECT_FOR_DIAMOND_TOOL, 16, Tier.repairTag("carborundum")), (tier) -> new SwordItem(tier, 3, -2.4f, com.hp.thermalshockreforged.ModRegistration.item()));
        register("cobalt_axe", new NativeTier(400, 7.0f, 5.0f, BlockTags.INCORRECT_FOR_DIAMOND_TOOL, 5, Tier.repairTag("cobalt")), (tier) -> new CobaltAxe(tier, 1.0f, -3.0f, com.hp.thermalshockreforged.ModRegistration.item()));
        register("ruby_axe", new NativeTier(1200, 10.0f, 7.0f, BlockTags.INCORRECT_FOR_DIAMOND_TOOL, 21, Tier.repairTag("ruby")), (tier) -> new AxeItem(tier, 1.0f, -3.0f, com.hp.thermalshockreforged.ModRegistration.item()));
        register("ruby_hoe", new NativeTier(1200, 10.0f, 2.0f, BlockTags.INCORRECT_FOR_DIAMOND_TOOL, 21, Tier.repairTag("ruby")), (tier) -> new HoeItem(tier, 0, -3.0f, com.hp.thermalshockreforged.ModRegistration.item()));
        register("ruby_pickaxe", new NativeTier(1200, 10.0f, 1.0f, BlockTags.INCORRECT_FOR_DIAMOND_TOOL, 21, Tier.repairTag("ruby")), (tier) -> new PickaxeItem(tier, 1, -2.8f, com.hp.thermalshockreforged.ModRegistration.item()));
        register("ruby_shovel", new NativeTier(1200, 10.0f, 1.0f, BlockTags.INCORRECT_FOR_DIAMOND_TOOL, 21, Tier.repairTag("ruby")), (tier) -> new ShovelItem(tier, 1.0f, -2.8f, com.hp.thermalshockreforged.ModRegistration.item()));
        register("ruby_sword", new NativeTier(1200, 10.0f, 2.0f, BlockTags.INCORRECT_FOR_DIAMOND_TOOL, 21, Tier.repairTag("ruby")), (tier) -> new SwordItem(tier, 3, -2.4f, com.hp.thermalshockreforged.ModRegistration.item()));
        register("stratosilver_auger", new NativeTier(5000, 50.0f, 1.0f, BlockTags.INCORRECT_FOR_DIAMOND_TOOL, 8, Tier.repairTag("stratus_alloy")), (tier) -> new DescribedShovel(tier, 1.0f, -2.5f, com.hp.thermalshockreforged.ModRegistration.item(), "item.thermal_shock_reforged.stratosilver_auger.description_0"));
        register("stratosilver_chainsaw", new NativeTier(5000, 50.0f, 2.0f, BlockTags.INCORRECT_FOR_DIAMOND_TOOL, 8, Tier.repairTag("stratus_alloy")), (tier) -> new DescribedAxe(tier, 1.0f, -1.0f, com.hp.thermalshockreforged.ModRegistration.item(), "item.thermal_shock_reforged.stratosilver_chainsaw.description_0"));
        register("stratosilver_drill", new NativeTier(5800, 50.0f, 1.0f, BlockTags.INCORRECT_FOR_DIAMOND_TOOL, 8, Tier.repairTag("stratus_alloy")), (tier) -> new DescribedPickaxe(tier, 1, -2.8f, com.hp.thermalshockreforged.ModRegistration.item(), "item.thermal_shock_reforged.stratosilver_drill.description_0"));
    }

    private ToolsContent() { }

    public static void register(IEventBus bus) { ITEMS.register(bus); }

    public static Map<String, DeferredItem<Item>> items() { return Collections.unmodifiableMap(ENTRIES); }

    private static void register(String id, Tier tier, java.util.function.Function<Tier, Item> factory) {
        ENTRIES.put(id, ITEMS.register(id, () -> factory.apply(tier)));
    }

    private record NativeTier(int uses, float speed, float damage, TagKey<Block> incorrect, int enchantability, TagKey<Item> repair) implements Tier {
        @Override public int getUses() { return uses; }
        @Override public float getSpeed() { return speed; }
        @Override public float getAttackDamageBonus() { return damage; }
        @Override public TagKey<Block> getIncorrectBlocksForDrops() { return incorrect; }
        @Override public int getEnchantmentValue() { return enchantability; }
        @Override public TagKey<Item> getRepairItems() { return repair; }
    }

    private static void tooltip(List<String> keys, Consumer<Component> tooltipAdder) { keys.forEach(key -> tooltipAdder.accept(Component.translatable(key))); }

    private static final class DescribedPickaxe extends PickaxeItem {
        private final List<String> descriptions;
        private DescribedPickaxe(Tier tier, float damage, float speed, Properties properties, String... descriptions) {
            super(tier, damage, speed, properties);
            this.descriptions = List.of(descriptions);
        }
        @Override
        public void appendHoverText(ItemStack stack, Item.TooltipContext context, TooltipDisplay display, Consumer<Component> tooltipAdder, TooltipFlag flag) {
            super.appendHoverText(stack, context, display, tooltipAdder, flag);
            tooltip(descriptions, tooltipAdder);
        }
    }

    private static final class DescribedAxe extends AxeItem {
        private final List<String> descriptions;
        private DescribedAxe(Tier tier, float damage, float speed, Properties properties, String... descriptions) {
            super(tier, damage, speed, properties);
            this.descriptions = List.of(descriptions);
        }
        @Override
        public void appendHoverText(ItemStack stack, Item.TooltipContext context, TooltipDisplay display, Consumer<Component> tooltipAdder, TooltipFlag flag) {
            super.appendHoverText(stack, context, display, tooltipAdder, flag);
            tooltip(descriptions, tooltipAdder);
        }
    }

    private static final class DescribedShovel extends ShovelItem {
        private final List<String> descriptions;
        private DescribedShovel(Tier tier, float damage, float speed, Properties properties, String... descriptions) {
            super(tier, damage, speed, properties);
            this.descriptions = List.of(descriptions);
        }
        @Override
        public void appendHoverText(ItemStack stack, Item.TooltipContext context, TooltipDisplay display, Consumer<Component> tooltipAdder, TooltipFlag flag) {
            super.appendHoverText(stack, context, display, tooltipAdder, flag);
            tooltip(descriptions, tooltipAdder);
        }
    }

    private static class DescribedSword extends SwordItem {
        private final List<String> descriptions;
        private final int reach;
        private DescribedSword(Tier tier, float damage, float speed, Properties properties, String... descriptions) {
            this(tier, damage, speed, properties, 0, descriptions);
        }
        private DescribedSword(Tier tier, float damage, float speed, Properties properties, int reach, String... descriptions) {
            super(tier, damage, speed, properties);
            this.descriptions = List.of(descriptions);
            this.reach = reach;
        }
        @Override
        public void inventoryTick(ItemStack stack, net.minecraft.server.level.ServerLevel level, Entity entity, net.minecraft.world.entity.EquipmentSlot slot) {
            super.inventoryTick(stack, level, entity, slot);
            if (reach > 0 && slot == net.minecraft.world.entity.EquipmentSlot.MAINHAND && entity instanceof LivingEntity living) HeldWeaponState.refreshReach(living, reach);
        }
        @Override
        public void appendHoverText(ItemStack stack, Item.TooltipContext context, TooltipDisplay display, Consumer<Component> tooltipAdder, TooltipFlag flag) {
            super.appendHoverText(stack, context, display, tooltipAdder, flag);
            tooltip(descriptions, tooltipAdder);
        }
    }
}
