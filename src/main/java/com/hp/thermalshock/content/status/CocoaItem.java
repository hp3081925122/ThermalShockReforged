package com.hp.thermalshock.content.status;

import java.util.List;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.*;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;

final class CocoaItem extends Item {
    CocoaItem() {
        super(new Properties().stacksTo(1).food(new FoodProperties.Builder().nutrition(6).saturationModifier(2.0F).build()));
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) { return UseAnim.DRINK; }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, context, tooltip, flag);
        tooltip.add(Component.translatable("item.thermal_shock.cocoa.description_0"));
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity entity) {
        super.finishUsingItem(stack, level, entity);
        if (!level.isClientSide) {
            entity.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 120, 1, true, false));
            entity.addEffect(new MobEffectInstance(StatusContent.COCOA_EFFECT, 1000, 1, true, true));
        }
        entity.removeEffect(StatusContent.FROSTBITE);
        ItemStack bottle = new ItemStack(Items.GLASS_BOTTLE);
        if (stack.isEmpty()) return bottle;
        if (entity instanceof Player player && !player.getAbilities().instabuild && !player.getInventory().add(bottle)) player.drop(bottle, false);
        return stack;
    }
}
