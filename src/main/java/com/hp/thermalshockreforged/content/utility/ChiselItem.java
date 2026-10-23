package com.hp.thermalshockreforged.content.utility;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.component.TooltipDisplay;
import java.util.function.Consumer;

import com.hp.thermalshockreforged.ThermalShockReforged;
import java.util.List;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.*;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.*;
import net.minecraft.world.item.context.UseOnContext;

public final class ChiselItem extends Item {
    public ChiselItem() { super(com.hp.thermalshockreforged.ModRegistration.item().stacksTo(1).rarity(Rarity.COMMON)); }
    @Override public InteractionResult useOn(UseOnContext context) {
        if (!context.getLevel().isClientSide()) context.getLevel().playSound(null, context.getClickedPos(), SoundEvents.UI_STONECUTTER_TAKE_RESULT, SoundSource.NEUTRAL, 1, 4);
        return InteractionResult.SUCCESS;
    }
    @Override public void appendHoverText(ItemStack stack, Item.TooltipContext context, TooltipDisplay display, Consumer<Component> tooltipAdder, TooltipFlag flag) {
        super.appendHoverText(stack, context, display, tooltipAdder, flag); tooltipAdder.accept(Component.translatable("item." + ThermalShockReforged.MOD_ID + ".chisel.description_0"));
    }
}
