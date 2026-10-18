package com.hp.thermalshockreforged.entity.icesquad;

import com.hp.thermalshockreforged.ThermalShockReforged;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import java.util.List;

public final class ThingamabobItem extends Item {
    public ThingamabobItem() { super(new Properties().stacksTo(1).rarity(Rarity.UNCOMMON)); }
    @Override public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> lines, TooltipFlag flag) {
        super.appendHoverText(stack, context, lines, flag); lines.add(Component.translatable("item.thermal_shock_reforged.thingamabob.description_0"));
    }
    @Override public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (stack.isEmpty() || !stack.is(this)) return InteractionResultHolder.fail(stack);
        if (level instanceof ServerLevel server) {
            stack.shrink(1);
            var anchor = player.position();
            server.sendParticles(ParticleTypes.REVERSE_PORTAL, anchor.x, anchor.y, anchor.z, 8, 1, 1, 1, 0.2);
            server.playSound(null, player.blockPosition(), SoundEvents.BELL_BLOCK, SoundSource.NEUTRAL, 1, 7);
            if (!DeploymentTaskEntity.submit(server, anchor, player.getUUID(), true)) ThermalShockReforged.LOGGER.debug("Blizzard pod request rejected: player={}, anchor={}", player.getUUID(), anchor);
        }
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
    }
}
