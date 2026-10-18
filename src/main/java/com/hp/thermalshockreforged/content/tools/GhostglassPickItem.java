package com.hp.thermalshockreforged.content.tools;

import com.hp.thermalshockreforged.ThermalShockReforged;
import java.util.List;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.*;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.*;
import net.minecraft.world.item.context.UseOnContext;

final class GhostglassPickItem extends PickaxeItem {
    private static final ResourceLocation AQERON = ResourceLocation.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, "aqeron");
    GhostglassPickItem(Tier tier, Properties properties) { super(tier, properties); }
    @Override public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> lines, TooltipFlag flag) {
        super.appendHoverText(stack, context, lines, flag); lines.add(Component.translatable("item.thermal_shock_reforged.ghostglass_pick.description_0"));
    }
    @Override public InteractionResult useOn(UseOnContext context) {
        var player = context.getPlayer();
        var face = context.getClickedFace();
        if (player == null || player.isShiftKeyDown() || face == Direction.UP || face == Direction.DOWN || context.getLevel().dimension().location().equals(AQERON)) return InteractionResult.PASS;
        player.setDeltaMovement(0, 0.4, 0);
        player.getCooldowns().addCooldown(this, 5);
        if (context.getLevel().isClientSide) {
            var pos = context.getClickedPos();
            context.getLevel().playLocalSound(pos.getX(), pos.getY(), pos.getZ(), SoundEvents.CALCITE_BREAK, SoundSource.NEUTRAL, 1, 1, false);
            context.getLevel().playLocalSound(pos.getX(), pos.getY(), pos.getZ(), SoundEvents.RABBIT_JUMP, SoundSource.NEUTRAL, 1, 1, false);
        }
        return InteractionResult.SUCCESS;
    }
}
