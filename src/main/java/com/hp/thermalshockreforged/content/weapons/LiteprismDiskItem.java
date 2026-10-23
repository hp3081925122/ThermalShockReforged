package com.hp.thermalshockreforged.content.weapons;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.component.TooltipDisplay;
import java.util.function.Consumer;

import com.hp.thermalshockreforged.ThermalShockReforged;
import com.hp.thermalshockreforged.entity.projectile.LiteprismDiskProjectileEntity;
import java.util.List;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;

public final class LiteprismDiskItem extends Item {
    public LiteprismDiskItem() { super(com.hp.thermalshockreforged.ModRegistration.item().durability(790).rarity(Rarity.UNCOMMON)); }
    @Override public ItemUseAnimation getUseAnimation(ItemStack stack) { return ItemUseAnimation.BOW; }
    @Override public int getUseDuration(ItemStack stack, LivingEntity user) { return 72000; }
    @Override public InteractionResult use(Level level, Player player, InteractionHand hand) {
        player.startUsingItem(hand);
        return InteractionResult.SUCCESS;
    }
    @Override public void onUseTick(Level level, LivingEntity user, ItemStack stack, int remaining) {
        if (!(level instanceof ServerLevel server) || !(user instanceof ServerPlayer player)) return;
        if (LiteprismDiskProjectileEntity.fire(server, player)) {
            stack.hurtAndBreak(1, player, player.getUsedItemHand() == InteractionHand.MAIN_HAND ? EquipmentSlot.MAINHAND : EquipmentSlot.OFFHAND);
            ThermalShockReforged.LOGGER.debug("Liteprism disk fired: player={}", player.getUUID());
        }
        player.releaseUsingItem();
    }
    @Override public void appendHoverText(ItemStack stack, Item.TooltipContext context, TooltipDisplay display, Consumer<Component> tooltipAdder, TooltipFlag flag) {
        super.appendHoverText(stack, context, display, tooltipAdder, flag);
        for (int line = 0; line < 3; line++) tooltipAdder.accept(Component.translatable("item." + ThermalShockReforged.MOD_ID + ".liteprism_disk.description_" + line));
    }
}
