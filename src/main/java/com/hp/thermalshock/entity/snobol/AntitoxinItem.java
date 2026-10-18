package com.hp.thermalshock.entity.snobol;

import com.hp.thermalshock.content.MaterialContent;
import com.hp.thermalshock.content.status.StatusContent;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;

final class AntitoxinItem extends Item {
    AntitoxinItem() { super(new Properties().stacksTo(16).food(new FoodProperties.Builder().nutrition(0).saturationModifier(0).alwaysEdible().build())); }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) { return UseAnim.DRINK; }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, java.util.List<Component> lines, TooltipFlag flag) {
        super.appendHoverText(stack, context, lines, flag);
        lines.add(Component.translatable("item.thermal_shock.antitoxin.description_0"));
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity consumer) {
        var result = super.finishUsingItem(stack, level, consumer);
        if (!level.isClientSide) consumer.addEffect(new MobEffectInstance(StatusContent.ANTITOXIN_EFFECT, 120000, 1));
        var flask = new ItemStack(MaterialContent.items().get("corrial_flask").get());
        if (result.isEmpty()) return flask;
        if (!level.isClientSide && consumer instanceof Player player && !player.getAbilities().instabuild) SnobolContent.give(player, flask);
        return result;
    }
}
