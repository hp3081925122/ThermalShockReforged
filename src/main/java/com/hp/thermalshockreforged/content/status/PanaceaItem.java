package com.hp.thermalshockreforged.content.status;

import net.minecraft.sounds.*;
import net.minecraft.world.*;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;

final class PanaceaItem extends Item {
    PanaceaItem() { super(com.hp.thermalshockreforged.ModRegistration.item().stacksTo(1).fireResistant().rarity(Rarity.EPIC)); }

    @Override
    public ItemUseAnimation getUseAnimation(ItemStack stack) { return ItemUseAnimation.BOW; }
    @Override
    public int getUseDuration(ItemStack stack, LivingEntity entity) { return 60; }
    @Override
    public boolean isFoil(ItemStack stack) { return true; }
    @Override
    public boolean hasCraftingRemainingItem(ItemStack stack) { return true; }
    @Override
    public ItemStack getCraftingRemainingItem(ItemStack stack) { return new ItemStack(this); }
    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        player.startUsingItem(hand);
        return InteractionResult.SUCCESS;
    }
    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity entity) {
        ItemStack result = super.finishUsingItem(stack, level, entity);
        if (!level.isClientSide()) {
            level.playSound(null, entity.blockPosition(), SoundEvents.GLASS_BREAK, SoundSource.NEUTRAL, 1.0F, 1.0F);
            level.playSound(null, entity.blockPosition(), SoundEvents.PLAYER_LEVELUP, SoundSource.NEUTRAL, 1.0F, 5.0F);
            entity.addEffect(new MobEffectInstance(StatusContent.PURIFIED, 200, 0));
        }
        return result;
    }
}
