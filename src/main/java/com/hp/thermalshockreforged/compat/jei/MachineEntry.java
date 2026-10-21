package com.hp.thermalshockreforged.compat.jei;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public record MachineEntry(List<Slot> inputs, List<Slot> outputs, Component xp, List<Component> xpTooltip) {
    public record Slot(List<ItemStack> stacks, List<Component> notes) { }
}
