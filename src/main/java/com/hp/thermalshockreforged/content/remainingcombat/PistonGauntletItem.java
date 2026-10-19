package com.hp.thermalshockreforged.content.remainingcombat;

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
    public PistonGauntletItem() { super(new Properties().durability(50).rarity(Rarity.COMMON)); }
    @Override public String modelName() { return "cobalt_fist"; }
    @Override public String textureName() { return "cobalt_fist"; }
    @Override public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        var result = super.use(level, player, hand);
        if (!(level instanceof ServerLevel server) || player.getCooldowns().isOnCooldown(this) || !player.getOffhandItem().is(this)) return result;
        var stack = result.getObject();
        animate(player, stack);
        player.getCooldowns().addCooldown(this, 60);
        var slot = hand == InteractionHand.MAIN_HAND ? EquipmentSlot.MAINHAND : EquipmentSlot.OFFHAND;
        stack.hurtAndBreak(1, server, (net.minecraft.world.entity.LivingEntity) null, broken -> player.onEquippedItemBroken(broken, slot));
        server.playSound(null, player.blockPosition(), net.minecraft.sounds.SoundEvents.PISTON_EXTEND, net.minecraft.sounds.SoundSource.NEUTRAL, .8F, 4);
        player.addEffect(new MobEffectInstance(StatusContent.PARRYING, 15, 1));
        return result;
    }
    @Override public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> lines, TooltipFlag flag) {
        super.appendHoverText(stack, context, lines, flag);
        lines.add(Component.translatable("item.thermal_shock_reforged.piston_gauntlet.description_0"));
    }
}
