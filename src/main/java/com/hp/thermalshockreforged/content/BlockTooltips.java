package com.hp.thermalshockreforged.content;

import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.block.Block;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.tooltip.TooltipLocation;
import net.neoforged.neoforge.event.RegisterTooltipAppendersEvent;

public final class BlockTooltips {
    private static final Map<Block, List<String>> KEYS = new IdentityHashMap<>();

    private BlockTooltips() {
    }

    public interface Provider {
        void appendHoverText(ItemStack stack, Item.TooltipContext context, TooltipDisplay display, Consumer<Component> tooltipAdder, TooltipFlag flag);
    }

    public static synchronized <B extends Block> B keys(B block, List<String> keys) {
        if (!keys.isEmpty()) KEYS.put(block, List.copyOf(keys));
        return block;
    }

    public static void register(IEventBus bus) {
        bus.addListener(RegisterTooltipAppendersEvent.class, event -> event.registerAppender(TooltipLocation.POST_CUSTOM, (stack, context, display, player, flag, builder) -> {
            if (!(stack.getItem() instanceof BlockItem item)) return;
            Block block = item.getBlock();
            if (block instanceof Provider provider) provider.appendHoverText(stack, context, display, builder, flag);
            List<String> keys = KEYS.get(block);
            if (keys != null) keys.forEach(key -> builder.accept(Component.translatable(key)));
        }));
    }
}
