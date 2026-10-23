package com.hp.thermalshockreforged.entity.labmachinist;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.component.TooltipDisplay;
import java.util.function.Consumer;
import net.minecraft.world.InteractionResult;

import java.util.List;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.*;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.*;
import net.minecraft.world.phys.Vec3;

public final class EnderTentacleItem extends Item {
    public EnderTentacleItem() { super(new Properties().stacksTo(1)); }
    @Override public void appendHoverText(ItemStack stack, Item.TooltipContext context, TooltipDisplay display, Consumer<Component> tooltipAdder, TooltipFlag flag) { super.appendHoverText(stack, context, display, tooltipAdder, flag); tooltipAdder.accept(Component.translatable("item.thermal_shock_reforged.ender_tentacle.description_0")); }
    @Override public InteractionResult use(Level level, Player player, InteractionHand hand) {
        var stack = player.getItemInHand(hand);
        if (level.dimension().identifier().equals(LabContent.id("aqeron"))) { if (!level.isClientSide()) level.playSound(null, player.blockPosition(), BuiltInRegistries.SOUND_EVENT.getValue(LabContent.id("flarealtshoot")), SoundSource.PLAYERS, .5F, -3); return InteractionResult.PASS; }
        var start = player.getEyePosition(); var look = player.getViewVector(1); var hit = level.clip(new ClipContext(start, start.add(look.scale(64)), ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, player));
        if (level.isEmptyBlock(hit.getBlockPos())) return InteractionResult.PASS;
        player.getCooldowns().addCooldown(this, 90);
        var hook = level.clip(new ClipContext(start, start.add(look.scale(32)), ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, player)).getBlockPos();
        player.lookAt(EntityAnchorArgument.Anchor.EYES, new Vec3(hook.getX(), hook.getY(), hook.getZ())); player.setDeltaMovement(player.getLookAngle().scale(5));
        if (level instanceof ServerLevel server) {
            player.hurtMarked = true; server.sendParticles(ParticleTypes.PORTAL, player.getX(), player.getY(), player.getZ(), 20, 1, 1.5, 1, .2);
            var end = level.clip(new ClipContext(player.getEyePosition(), player.getEyePosition().add(player.getViewVector(1).scale(64)), ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, player)).getBlockPos(); server.sendParticles(ParticleTypes.PORTAL, end.getX(), end.getY(), end.getZ(), 20, 1, 1.5, 1, .2);
            server.playSound(null, player.blockPosition(), SoundEvents.ENDERMAN_TELEPORT, SoundSource.PLAYERS, 1, 1); server.playSound(null, player.blockPosition(), BuiltInRegistries.SOUND_EVENT.getValue(LabContent.id("magnet_bow_25")), SoundSource.PLAYERS, 1, 3);
        }
        return InteractionResult.PASS;
    }
}
