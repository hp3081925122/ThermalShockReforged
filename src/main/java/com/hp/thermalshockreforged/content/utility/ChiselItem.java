package com.hp.thermalshockreforged.content.utility;

import com.hp.thermalshockreforged.ThermalShockReforged;
import java.util.List;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.*;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.*;
import net.minecraft.world.item.context.UseOnContext;

public final class ChiselItem extends Item {
    public ChiselItem() { super(new Properties().stacksTo(1).rarity(Rarity.COMMON)); }
    @Override public InteractionResult useOn(UseOnContext context) {
        if (!context.getLevel().isClientSide) context.getLevel().playSound(null, context.getClickedPos(), SoundEvents.UI_STONECUTTER_TAKE_RESULT, SoundSource.NEUTRAL, 1, 4);
        return InteractionResult.SUCCESS;
    }
    @Override public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> lines, TooltipFlag flag) {
        super.appendHoverText(stack, context, lines, flag); lines.add(Component.translatable("item." + ThermalShockReforged.MOD_ID + ".chisel.description_0"));
    }
}
