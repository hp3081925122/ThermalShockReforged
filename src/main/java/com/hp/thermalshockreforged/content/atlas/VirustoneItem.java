package com.hp.thermalshockreforged.content.atlas;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.component.TooltipDisplay;
import java.util.function.Consumer;

import java.util.List;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.*;

final class VirustoneItem extends KeystoneItem {
    @Override public void appendHoverText(ItemStack stack, Item.TooltipContext context, TooltipDisplay display, Consumer<Component> tooltipAdder, TooltipFlag flag) {
        tooltipAdder.accept(Component.translatable("item.thermal_shock_reforged.virustone.description_0"));
    }
}
