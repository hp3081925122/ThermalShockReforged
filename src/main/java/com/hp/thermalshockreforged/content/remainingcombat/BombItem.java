package com.hp.thermalshockreforged.content.remainingcombat;

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
    @Override public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (player.getCooldowns().isOnCooldown(this)) return InteractionResultHolder.fail(stack);
        if (level instanceof ServerLevel server) {
            if (!CombatItemTaskEntity.submit(server, player.position(), ice ? CombatItemTaskEntity.ICE : CombatItemTaskEntity.FLOWER)) return InteractionResultHolder.fail(stack);
            if (ice && !player.getAbilities().instabuild) stack.shrink(1);
            player.getCooldowns().addCooldown(this, ice ? 60 : 600);
            CombatItemTaskEntity.sound(server, player.position(), "minecraft:entity.tnt.primed", 1, 2);
            CombatItemTaskEntity.sound(server, player.position(), "minecraft:entity.item.pickup", 1, 1);
            server.sendParticles(RemainingCombatContent.ICE_BOMB_EFFECT.get(), player.getX(), player.getY(), player.getZ(), 1, .001, .001, .001, 0);
        }
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
    }
    @Override public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> lines, TooltipFlag flag) {
        super.appendHoverText(stack, context, lines, flag);
        lines.add(Component.translatable("item.thermal_shock_reforged." + (ice ? "ice_bomb" : "flower_bomb") + ".description_0"));
    }
}
