package com.hp.thermalshockreforged.content.utility;

import com.hp.thermalshockreforged.ThermalShockReforged;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class UtilityContent {
    private static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(ThermalShockReforged.MOD_ID);
    public static final DeferredItem<Item> LOQUE_MILK = ITEMS.register("loque_milk", () -> new LoqueMilkItem(new Item.Properties().stacksTo(1).craftRemainder(Items.BUCKET)
            .food(new FoodProperties.Builder().nutrition(5).saturationModifier(1).usingConvertsTo(Items.BUCKET).build())));
    public static final DeferredItem<Item> ADAMANTINE_SPICE = ITEMS.register("adamantine_spice", () -> new AdamantineSpiceItem(new Item.Properties().rarity(Rarity.UNCOMMON)));
    public static final DeferredItem<Item> MUCK_PIE = ITEMS.register("muck_pie", () -> new NourishingFoodItem(UseAnim.EAT));

    public static final DeferredItem<Item> SNOWDAE = ITEMS.register("snowdae", () -> new NourishingFoodItem(UseAnim.EAT));
    public static final DeferredItem<Item> LOQUESICLE = ITEMS.register("loquesicle", () -> new NourishingFoodItem(UseAnim.EAT));
    public static final DeferredItem<Item> BLOOD_POP = ITEMS.register("blood_pop", () -> new NourishingFoodItem(UseAnim.DRINK));
    public static final DeferredItem<Item> CHALK_CANDY = ITEMS.register("chalk_candy", () -> new NourishingFoodItem(UseAnim.EAT));
    public static final DeferredItem<Item> LUCK_TICKET = ITEMS.register("luck_ticket", () -> new NourishingFoodItem(UseAnim.EAT));
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
        private final UseAnim animation;
        private NourishingFoodItem(UseAnim animation) {
            super(new Item.Properties().stacksTo(1).food(new FoodProperties.Builder().nutrition(10).saturationModifier(2).build()));
            this.animation = animation;
        }
        @Override public UseAnim getUseAnimation(ItemStack stack) { return animation; }
        @Override public int getUseDuration(ItemStack stack, LivingEntity consumer) { return 60; }
        @Override
        public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity consumer) {
            ItemStack result = super.finishUsingItem(stack, level, consumer);
            if (!level.isClientSide) BuiltInRegistries.MOB_EFFECT.getHolder(ResourceLocation.fromNamespaceAndPath("farmersdelight", "nourishment"))
                    .ifPresent(effect -> consumer.addEffect(new MobEffectInstance(effect, 4000, 0)));
            return result;
        }
    }

    private static final class SlowFoodItem extends Item {
        private SlowFoodItem(float saturation) {
            super(new Properties().food(new FoodProperties.Builder().nutrition(1).saturationModifier(saturation).build()));
        }
        @Override public int getUseDuration(ItemStack stack, LivingEntity consumer) { return 48; }
    }

    private static final class BottledMilkItem extends Item {
        private BottledMilkItem() {
            super(new Properties().stacksTo(1).food(new FoodProperties.Builder().nutrition(1).saturationModifier(0.5F)
                    .usingConvertsTo(Items.GLASS_BOTTLE).build()));
        }
        @Override public UseAnim getUseAnimation(ItemStack stack) { return UseAnim.DRINK; }
        @Override public int getUseDuration(ItemStack stack, LivingEntity consumer) { return 20; }
        @Override
        public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity consumer) {
            ItemStack result = super.finishUsingItem(stack, level, consumer);
            return !(consumer instanceof Player) && result.isEmpty() ? new ItemStack(Items.GLASS_BOTTLE) : result;
        }
    }

    private static final class LoqueMilkItem extends Item {
        private LoqueMilkItem(Properties properties) { super(properties); }
        @Override public UseAnim getUseAnimation(ItemStack stack) { return UseAnim.DRINK; }
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
        public void appendHoverText(ItemStack stack, TooltipContext context, java.util.List<net.minecraft.network.chat.Component> lines, TooltipFlag flag) {
            super.appendHoverText(stack, context, lines, flag);
            lines.add(net.minecraft.network.chat.Component.translatable("item.thermal_shock_reforged.adamantine_spice.description_0"));
        }
        @Override
        public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
            player.startUsingItem(hand);
            return InteractionResultHolder.consume(player.getItemInHand(hand));
        }
    }
}
