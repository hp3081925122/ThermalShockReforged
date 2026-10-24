package com.hp.thermalshockreforged.content.utility;
import net.minecraft.world.item.Item;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.component.TooltipDisplay;
import java.util.function.Consumer;

import com.hp.thermalshockreforged.ThermalShockReforged;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class UtilityContent {
    private static final DeferredRegister.Items ITEMS = com.hp.thermalshockreforged.ModRegistration.items(ThermalShockReforged.MOD_ID);
    public static final DeferredItem<Item> LOQUE_MILK = ITEMS.register("loque_milk", () -> new LoqueMilkItem(com.hp.thermalshockreforged.ModRegistration.item().stacksTo(1).craftRemainder(Items.BUCKET)
            .food(new FoodProperties.Builder().nutrition(5).saturationModifier(1).build(), net.minecraft.world.item.component.Consumables.defaultDrink().consumeSeconds(2.25F).build()).usingConvertsTo(Items.BUCKET)));
    public static final DeferredItem<Item> ADAMANTINE_SPICE = ITEMS.register("adamantine_spice", () -> new AdamantineSpiceItem(com.hp.thermalshockreforged.ModRegistration.item().rarity(Rarity.UNCOMMON)));
    public static final DeferredItem<Item> MUCK_PIE = ITEMS.register("muck_pie", () -> new NourishingFoodItem(ItemUseAnimation.EAT));

    public static final DeferredItem<Item> SNOWDAE = ITEMS.register("snowdae", () -> new NourishingFoodItem(ItemUseAnimation.EAT));
    public static final DeferredItem<Item> LOQUESICLE = ITEMS.register("loquesicle", () -> new NourishingFoodItem(ItemUseAnimation.EAT));
    public static final DeferredItem<Item> BLOOD_POP = ITEMS.register("blood_pop", () -> new NourishingFoodItem(ItemUseAnimation.DRINK));
    public static final DeferredItem<Item> CHALK_CANDY = ITEMS.register("chalk_candy", () -> new NourishingFoodItem(ItemUseAnimation.EAT));
    public static final DeferredItem<Item> LUCK_TICKET = ITEMS.register("luck_ticket", () -> new NourishingFoodItem(ItemUseAnimation.EAT));
    public static final DeferredItem<Item> LOQUE_MILK_BOTTLE = ITEMS.register("loque_milk_bottle", BottledMilkItem::new);

    public static final DeferredItem<Item> FROZEN_CHILLOO = ITEMS.register("frozen_chilloo", () -> new SlowFoodItem(0.1F));
    public static final DeferredItem<Item> MARROW_MELD = ITEMS.register("marrow_meld", () -> new SlowFoodItem(-0.1F));

    public static final DeferredItem<PesticideItem> PESTICIDE = ITEMS.register("pesticide", PesticideItem::new);
    public static final DeferredItem<Item> CHISEL = ITEMS.register("chisel", ChiselItem::new);
    private UtilityContent() { }

    public static void register(IEventBus bus) {
        ITEMS.register(bus);
    }

    private static final class NourishingFoodItem extends Item {
        private final ItemUseAnimation animation;
        private NourishingFoodItem(ItemUseAnimation animation) {
            super(com.hp.thermalshockreforged.ModRegistration.item().stacksTo(1).food(new FoodProperties.Builder().nutrition(10).saturationModifier(2).build(), (animation == ItemUseAnimation.DRINK ? net.minecraft.world.item.component.Consumables.defaultDrink() : net.minecraft.world.item.component.Consumables.defaultFood()).consumeSeconds(3.0F).build()));
            this.animation = animation;
        }
        @Override public ItemUseAnimation getUseAnimation(ItemStack stack) { return animation; }
        @Override public int getUseDuration(ItemStack stack, LivingEntity consumer) { return 60; }
        @Override
        public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity consumer) {
            ItemStack result = super.finishUsingItem(stack, level, consumer);
            if (!level.isClientSide()) BuiltInRegistries.MOB_EFFECT.get(Identifier.fromNamespaceAndPath("farmersdelight", "nourishment"))
                    .ifPresent(effect -> consumer.addEffect(new MobEffectInstance(effect, 4000, 0)));
            return result;
        }
    }

    private static final class SlowFoodItem extends Item {
        private SlowFoodItem(float saturation) {
            super(com.hp.thermalshockreforged.ModRegistration.item().food(new FoodProperties.Builder().nutrition(1).saturationModifier(saturation).build()));
        }
        @Override public int getUseDuration(ItemStack stack, LivingEntity consumer) { return 48; }
    }

    private static final class BottledMilkItem extends Item {
        private BottledMilkItem() {
            super(com.hp.thermalshockreforged.ModRegistration.item().stacksTo(1).food(new FoodProperties.Builder().nutrition(1).saturationModifier(0.5F)
                    .build(), net.minecraft.world.item.component.Consumables.defaultDrink().consumeSeconds(1.0F).build()).usingConvertsTo(Items.GLASS_BOTTLE));
        }
        @Override public ItemUseAnimation getUseAnimation(ItemStack stack) { return ItemUseAnimation.DRINK; }
        @Override public int getUseDuration(ItemStack stack, LivingEntity consumer) { return 20; }
        @Override
        public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity consumer) {
            ItemStack result = super.finishUsingItem(stack, level, consumer);
            return !(consumer instanceof Player) && result.isEmpty() ? new ItemStack(Items.GLASS_BOTTLE) : result;
        }
    }

    private static final class LoqueMilkItem extends Item {
        private LoqueMilkItem(Properties properties) { super(properties); }
        @Override public ItemUseAnimation getUseAnimation(ItemStack stack) { return ItemUseAnimation.DRINK; }
        @Override public int getUseDuration(ItemStack stack, LivingEntity consumer) { return 45; }
        @Override
        public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity consumer) {
            ItemStack result = super.finishUsingItem(stack, level, consumer);
            return !(consumer instanceof Player) && result.isEmpty() ? new ItemStack(Items.BUCKET) : result;
        }
    }

    private static final class AdamantineSpiceItem extends Item {
        private AdamantineSpiceItem(Properties properties) { super(properties); }
        @Override public int getUseDuration(ItemStack stack, LivingEntity consumer) { return 10; }
        @Override
        public void appendHoverText(ItemStack stack, Item.TooltipContext context, TooltipDisplay display, Consumer<Component> tooltipAdder, TooltipFlag flag) {
            super.appendHoverText(stack, context, display, tooltipAdder, flag);
            tooltipAdder.accept(net.minecraft.network.chat.Component.translatable("item.thermal_shock_reforged.adamantine_spice.description_0"));
        }
        @Override
        public InteractionResult use(Level level, Player player, InteractionHand hand) {
            player.startUsingItem(hand);
            return InteractionResult.CONSUME;
        }
    }
}
