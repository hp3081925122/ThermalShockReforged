package com.hp.thermalshock.content;

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
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, context, tooltip, flag);
        descriptions.forEach(key -> tooltip.add(Component.translatable(key)));
    }
}
