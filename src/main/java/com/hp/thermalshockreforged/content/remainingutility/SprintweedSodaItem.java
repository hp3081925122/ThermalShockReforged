package com.hp.thermalshockreforged.content.remainingutility;

import com.hp.thermalshockreforged.content.MaterialContent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;

public final class SprintweedSodaItem extends Item {
    SprintweedSodaItem() { super(new Properties().stacksTo(1)); }

    @Override public UseAnim getUseAnimation(ItemStack stack) { return UseAnim.DRINK; }
    @Override public int getUseDuration(ItemStack stack, LivingEntity consumer) { return 20; }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        return ItemUtils.startUsingInstantly(level, player, hand);
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity consumer) {
        if (level.isClientSide || stack.isEmpty() || !stack.is(this)) return stack;
        consumer.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 1500, 0));
        stack.shrink(1);
        ItemStack flask = new ItemStack(MaterialContent.items().get("corrial_flask").get());
        if (stack.isEmpty()) return flask;
        if (consumer instanceof Player player && !player.getInventory().add(flask)) player.drop(flask, false);
        return stack;
    }
}
