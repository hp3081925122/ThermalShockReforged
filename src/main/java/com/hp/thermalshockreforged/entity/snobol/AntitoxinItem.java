package com.hp.thermalshockreforged.entity.snobol;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.component.TooltipDisplay;
import java.util.function.Consumer;

import com.hp.thermalshockreforged.content.MaterialContent;
import com.hp.thermalshockreforged.content.status.StatusContent;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;

final class AntitoxinItem extends Item {
    AntitoxinItem() { super(com.hp.thermalshockreforged.ModRegistration.item().stacksTo(16).food(new FoodProperties.Builder().nutrition(0).saturationModifier(0).alwaysEdible().build(), net.minecraft.world.item.component.Consumables.DEFAULT_DRINK)); }

    @Override
    public ItemUseAnimation getUseAnimation(ItemStack stack) { return ItemUseAnimation.DRINK; }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, TooltipDisplay display, Consumer<Component> tooltipAdder, TooltipFlag flag) {
        super.appendHoverText(stack, context, display, tooltipAdder, flag);
        tooltipAdder.accept(Component.translatable("item.thermal_shock_reforged.antitoxin.description_0"));
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity consumer) {
        var result = super.finishUsingItem(stack, level, consumer);
        if (!level.isClientSide()) consumer.addEffect(new MobEffectInstance(StatusContent.ANTITOXIN_EFFECT, 120000, 1));
        var flask = new ItemStack(MaterialContent.items().get("corrial_flask").get());
        if (result.isEmpty()) return flask;
        if (!level.isClientSide() && consumer instanceof Player player && !player.getAbilities().instabuild) SnobolContent.give(player, flask);
        return result;
    }
}
