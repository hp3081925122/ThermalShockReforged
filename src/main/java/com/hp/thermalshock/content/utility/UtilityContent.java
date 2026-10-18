package com.hp.thermalshock.content.utility;

import com.hp.thermalshock.ThermalShock;
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
    private static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(ThermalShock.MOD_ID);
    public static final DeferredItem<Item> LOQUE_MILK = ITEMS.register("loque_milk", () -> new LoqueMilkItem(new Item.Properties().stacksTo(1).craftRemainder(Items.BUCKET)
            .food(new FoodProperties.Builder().nutrition(5).saturationModifier(1).usingConvertsTo(Items.BUCKET).build())));
    public static final DeferredItem<Item> ADAMANTINE_SPICE = ITEMS.register("adamantine_spice", () -> new AdamantineSpiceItem(new Item.Properties().rarity(Rarity.UNCOMMON)));
    public static final DeferredItem<Item> MUCK_PIE = ITEMS.register("muck_pie", () -> new MuckPieItem(new Item.Properties().stacksTo(1).food(new FoodProperties.Builder().nutrition(10).saturationModifier(2).build())));

    private UtilityContent() { }

    public static void register(IEventBus bus) { ITEMS.register(bus); }

    private static final class MuckPieItem extends Item {
        private MuckPieItem(Properties properties) { super(properties); }
        @Override public int getUseDuration(ItemStack stack, LivingEntity consumer) { return 60; }
        @Override
        public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity consumer) {
            ItemStack result = super.finishUsingItem(stack, level, consumer);
            if (!level.isClientSide) BuiltInRegistries.MOB_EFFECT.getHolder(ResourceLocation.fromNamespaceAndPath("farmersdelight", "nourishment"))
                    .ifPresent(effect -> consumer.addEffect(new MobEffectInstance(effect, 4000, 0)));
            return result;
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
            lines.add(net.minecraft.network.chat.Component.translatable("item.thermal_shock.adamantine_spice.description_0"));
        }
        @Override
        public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
            player.startUsingItem(hand);
            return InteractionResultHolder.consume(player.getItemInHand(hand));
        }
    }
}
