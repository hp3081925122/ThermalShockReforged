package com.hp.thermalshockreforged.content;
import net.minecraft.world.item.component.TooltipDisplay;
import java.util.function.Consumer;

import java.util.List;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

public final class DescribedItem extends Item {
    private final List<String> descriptions;

    public DescribedItem(Properties properties, String... descriptions) {
        super(properties);
        this.descriptions = List.of(descriptions);
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, TooltipDisplay display, Consumer<Component> tooltipAdder, TooltipFlag flag) {
        super.appendHoverText(stack, context, display, tooltipAdder, flag);
        descriptions.forEach(key -> tooltipAdder.accept(Component.translatable(key)));
    }
}
