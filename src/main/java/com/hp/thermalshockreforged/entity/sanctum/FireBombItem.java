package com.hp.thermalshockreforged.entity.sanctum;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.component.TooltipDisplay;
import java.util.function.Consumer;

import com.hp.thermalshockreforged.ThermalShockReforged;
import java.util.List;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.*;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;

public final class FireBombItem extends Item {
    public FireBombItem() { super(new Properties().stacksTo(16).rarity(Rarity.COMMON)); }
    @Override public void appendHoverText(ItemStack stack, Item.TooltipContext context, TooltipDisplay display, Consumer<Component> tooltipAdder, TooltipFlag flag) { super.appendHoverText(stack, context, display, tooltipAdder, flag); tooltipAdder.accept(Component.translatable("item.thermal_shock_reforged.fire_bomb.description_0")); }
    @Override public InteractionResult use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (stack.isEmpty() || player.getCooldowns().isOnCooldown(this)) return InteractionResult.FAIL;
        if (level instanceof ServerLevel server) {
            if (!FireBombActivationEntity.submit(server, player.position())) {
                ThermalShockReforged.LOGGER.warn("Fire bomb activation could not be scheduled: player={}", player.getUUID()); return InteractionResult.FAIL;
            }
            stack.shrink(1); player.getCooldowns().addCooldown(this, 60);
            server.playSound(null, player.blockPosition(), SoundEvents.TNT_PRIMED, SoundSource.NEUTRAL, 1, 2);
            server.playSound(null, player.blockPosition(), SoundEvents.ITEM_PICKUP, SoundSource.NEUTRAL, 1, 1);
            server.sendParticles(SanctumContent.FIRE_BOMB_EFFECT.get(), player.getX(), player.getY(), player.getZ(), 1, .001, .001, .001, 0);
        }
        return InteractionResult.SUCCESS;
    }
}
