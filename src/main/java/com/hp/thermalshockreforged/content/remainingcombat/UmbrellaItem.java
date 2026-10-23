package com.hp.thermalshockreforged.content.remainingcombat;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.component.TooltipDisplay;
import java.util.function.Consumer;

import java.util.List;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.*;
import net.minecraft.world.effect.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public final class UmbrellaItem extends AnimatedCombatItem {
    private final boolean legendary;
    public UmbrellaItem(boolean legendary) { super(new Properties().durability(250).rarity(legendary ? Rarity.UNCOMMON : Rarity.COMMON)); this.legendary = legendary; }
    @Override public String modelName() { return "jellyumbrella"; }
    @Override public String textureName() { return "jellumnbra"; }
    @Override public InteractionResult use(Level level, Player player, InteractionHand hand) {
        var result = super.use(level, player, hand);
        if (player.getCooldowns().isOnCooldown(this)) return result;
        player.setDeltaMovement(new Vec3(0, .5, 0)); player.hasImpulse = true;
        if (level instanceof ServerLevel server) {
            player.hurtMarked = true;
            player.addEffect(new MobEffectInstance(MobEffects.SLOW_FALLING, legendary ? 60 : 120, 1));
            player.getCooldowns().addCooldown(this, legendary ? 80 : 200);
            CombatItemTaskEntity.sound(server, player.position(), "jellyumbrelladeploy", 1, 1);
            animate(player, player.getItemInHand(hand));
            if (legendary) com.hp.thermalshockreforged.ThermalShockReforged.LOGGER.debug("Legendary umbrella activation animation submitted: player={}", player.getUUID());
        }
        return result;
    }
    @Override public void appendHoverText(ItemStack stack, Item.TooltipContext context, TooltipDisplay display, Consumer<Component> tooltipAdder, TooltipFlag flag) {
        super.appendHoverText(stack, context, display, tooltipAdder, flag);
        tooltipAdder.accept(Component.translatable("item.thermal_shock_reforged.jelly_umbrella.description_0"));
        if (legendary) tooltipAdder.accept(Component.translatable("item.thermal_shock_reforged.legendary_jelly_umbrella.description_1"));
    }
}
