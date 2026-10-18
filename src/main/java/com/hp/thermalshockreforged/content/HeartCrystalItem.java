package com.hp.thermalshockreforged.content;

import java.util.List;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

public final class HeartCrystalItem extends Item {
    public HeartCrystalItem(Properties properties) { super(properties); }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, context, tooltip, flag);
        tooltip.add(Component.translatable("item.thermal_shock_reforged.heart_crystal.description_0"));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (!level.isClientSide) {
            MobEffectInstance previous = player.getEffect(ModEffects.HEART_BOOST);
            int amplifier = previous == null ? 1 : previous.getAmplifier() + 1;
            stack.shrink(1);
            player.addEffect(new MobEffectInstance(ModEffects.HEART_BOOST, 5000, amplifier));
            player.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 20, 9));
            level.playSound(null, player.blockPosition(), SoundEvents.ENCHANTMENT_TABLE_USE, SoundSource.NEUTRAL, 1, 4);
        }
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
    }
}
