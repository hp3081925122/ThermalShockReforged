package com.hp.thermalshockreforged.content.remainingcombat;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.component.TooltipDisplay;
import java.util.function.Consumer;

import java.util.List;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;

public final class BombItem extends Item {
    private final boolean ice;
    public BombItem(boolean ice) { super(new Properties().stacksTo(ice ? 16 : 1).rarity(Rarity.COMMON)); this.ice = ice; }
    @Override public InteractionResult use(Level level, Player player, InteractionHand hand) {
        var result = super.use(level, player, hand);
        if (!(level instanceof ServerLevel server) || player.getCooldowns().isOnCooldown(this)) return result;
        if (!CombatItemTaskEntity.submit(server, player.position(), ice ? CombatItemTaskEntity.ICE : CombatItemTaskEntity.FLOWER)) return result;
        if (ice) player.getItemInHand(hand).shrink(1);
        player.getCooldowns().addCooldown(this, ice ? 60 : 600);
        CombatItemTaskEntity.sound(server, player.position(), "minecraft:entity.tnt.primed", 1, 2);
        CombatItemTaskEntity.sound(server, player.position(), "minecraft:entity.item.pickup", 1, 1);
        server.sendParticles(RemainingCombatContent.ICE_BOMB_EFFECT.get(), player.getX(), player.getY(), player.getZ(), 1, .001, .001, .001, 0);
        return result;
    }
    @Override public void appendHoverText(ItemStack stack, Item.TooltipContext context, TooltipDisplay display, Consumer<Component> tooltipAdder, TooltipFlag flag) {
        super.appendHoverText(stack, context, display, tooltipAdder, flag);
        tooltipAdder.accept(Component.translatable("item.thermal_shock_reforged." + (ice ? "ice_bomb" : "flower_bomb") + ".description_0"));
    }
}
