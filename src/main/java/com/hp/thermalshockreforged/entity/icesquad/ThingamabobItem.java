package com.hp.thermalshockreforged.entity.icesquad;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.component.TooltipDisplay;
import java.util.function.Consumer;

import com.hp.thermalshockreforged.ThermalShockReforged;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import java.util.List;

public final class ThingamabobItem extends Item {
    public ThingamabobItem() { super(com.hp.thermalshockreforged.ModRegistration.item().stacksTo(1).rarity(Rarity.UNCOMMON)); }
    @Override public void appendHoverText(ItemStack stack, Item.TooltipContext context, TooltipDisplay display, Consumer<Component> tooltipAdder, TooltipFlag flag) {
        super.appendHoverText(stack, context, display, tooltipAdder, flag); tooltipAdder.accept(Component.translatable("item.thermal_shock_reforged.thingamabob.description_0"));
    }
    @Override public InteractionResult use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (stack.isEmpty() || !stack.is(this)) return InteractionResult.FAIL;
        if (level instanceof ServerLevel server) {
            stack.shrink(1);
            var anchor = player.position();
            server.sendParticles(ParticleTypes.REVERSE_PORTAL, anchor.x, anchor.y, anchor.z, 8, 1, 1, 1, 0.2);
            server.playSound(null, player.blockPosition(), SoundEvents.BELL_BLOCK, SoundSource.NEUTRAL, 1, 7);
            if (!DeploymentTaskEntity.submit(server, anchor, player.getUUID(), true)) ThermalShockReforged.LOGGER.debug("Blizzard pod request rejected: player={}, anchor={}", player.getUUID(), anchor);
        }
        return InteractionResult.SUCCESS;
    }
}
