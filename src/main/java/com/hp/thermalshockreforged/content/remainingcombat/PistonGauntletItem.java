package com.hp.thermalshockreforged.content.remainingcombat;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.component.TooltipDisplay;
import java.util.function.Consumer;

import com.hp.thermalshockreforged.content.status.StatusContent;
import java.util.List;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.*;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;

public final class PistonGauntletItem extends AnimatedCombatItem {
    public PistonGauntletItem() { super(com.hp.thermalshockreforged.ModRegistration.item().durability(50).rarity(Rarity.COMMON)); }
    @Override public String modelName() { return "cobalt_fist"; }
    @Override public String textureName() { return "cobalt_fist"; }
    @Override public InteractionResult use(Level level, Player player, InteractionHand hand) {
        var result = super.use(level, player, hand);
        if (!(level instanceof ServerLevel server) || player.getCooldowns().isOnCooldown(getDefaultInstance()) || !player.getOffhandItem().is(this)) return result;
        var stack = player.getItemInHand(hand);
        animate(player, stack);
        player.getCooldowns().addCooldown(getDefaultInstance(), 60);
        var slot = hand == InteractionHand.MAIN_HAND ? EquipmentSlot.MAINHAND : EquipmentSlot.OFFHAND;
        stack.hurtAndBreak(1, server, (net.minecraft.world.entity.LivingEntity) null, broken -> player.onEquippedItemBroken(broken, slot));
        server.playSound(null, player.blockPosition(), net.minecraft.sounds.SoundEvents.PISTON_EXTEND, net.minecraft.sounds.SoundSource.NEUTRAL, .8F, 4);
        player.addEffect(new MobEffectInstance(StatusContent.PARRYING, 15, 1));
        return result;
    }
    @Override public void appendHoverText(ItemStack stack, Item.TooltipContext context, TooltipDisplay display, Consumer<Component> tooltipAdder, TooltipFlag flag) {
        super.appendHoverText(stack, context, display, tooltipAdder, flag);
        tooltipAdder.accept(Component.translatable("item.thermal_shock_reforged.piston_gauntlet.description_0"));
    }
}
