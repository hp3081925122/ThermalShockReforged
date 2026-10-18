package com.hp.thermalshockreforged.content.farming;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;

public final class BarbubbleMelonItem extends Item {
    public BarbubbleMelonItem() { super(new Properties().stacksTo(64).rarity(Rarity.COMMON)); }
    @Override public int getUseDuration(ItemStack stack, LivingEntity entity) { return 32; }
    @Override public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        var result = super.use(level, player, hand);
        player.startUsingItem(hand);
        return result;
    }
}
