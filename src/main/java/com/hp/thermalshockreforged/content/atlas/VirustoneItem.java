package com.hp.thermalshockreforged.content.atlas;

import java.util.List;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.*;

final class VirustoneItem extends KeystoneItem {
    @Override public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> lines, TooltipFlag flag) {
        super.appendHoverText(stack, context, lines, flag);
        lines.add(Component.translatable("item.thermal_shock_reforged.virustone.description_0"));
    }
}
