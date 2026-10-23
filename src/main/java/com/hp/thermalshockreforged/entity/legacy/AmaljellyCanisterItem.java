package com.hp.thermalshockreforged.entity.legacy;
import net.minecraft.world.item.component.TooltipDisplay;
import java.util.function.Consumer;

import com.hp.thermalshockreforged.ThermalShockReforged;
import com.hp.thermalshockreforged.content.MaterialContent;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

public final class AmaljellyCanisterItem extends Item {
    public AmaljellyCanisterItem() { super(com.hp.thermalshockreforged.ModRegistration.item().stacksTo(1).rarity(Rarity.UNCOMMON)); }
    @Override public void appendHoverText(ItemStack stack, Item.TooltipContext context, TooltipDisplay display, Consumer<Component> tooltipAdder, TooltipFlag flag) {
        super.appendHoverText(stack, context, display, tooltipAdder, flag);
        tooltipAdder.accept(Component.translatable("item.thermal_shock_reforged.amaljelly_canister.description_0"));
    }

    @Override public InteractionResult use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (player.getCooldowns().isOnCooldown(this)) return InteractionResult.FAIL;
        if (level instanceof ServerLevel server) {
            BlockPos pos = BlockPos.containing(player.getX(), player.getY() + 1, player.getZ());
            LilJellyEntity jelly = LegacyContent.LIL_JELLY.get().spawn(server, pos, EntitySpawnReason.MOB_SUMMONED);
            if (jelly == null || jelly.isRemoved() || server.getEntity(jelly.getUUID()) != jelly) {
                ThermalShockReforged.LOGGER.debug("Amaljelly deployment rejected: player={}, hand={}, position={}", player.getUUID(), hand, pos);
                if (jelly != null && !jelly.isRemoved()) jelly.discard();
                return InteractionResult.FAIL;
            }
            jelly.setYRot(server.getRandom().nextFloat() * 360);
            server.playSound(null, player.blockPosition(), SoundEvents.EVOKER_CAST_SPELL, SoundSource.NEUTRAL, 1, 3);
            server.sendParticles(ParticleTypes.WITCH, player.getX(), player.getY(), player.getZ(), 15, 1, 1, 1, .1);
            exchangeOne(player, hand, new ItemStack(MaterialContent.items().get("empty_amaljelly_canister").get()));
            player.getCooldowns().addCooldown(this, 60);
        }
        return InteractionResult.SUCCESS;
    }

    public static void exchangeOne(Player player, InteractionHand hand, ItemStack replacement) {
        ItemStack input = player.getItemInHand(hand);
        input.shrink(1);
        if (input.isEmpty()) player.setItemInHand(hand, replacement);
        else if (!player.getInventory().add(replacement)) player.drop(replacement, false);
        player.getInventory().setChanged();
    }
}
