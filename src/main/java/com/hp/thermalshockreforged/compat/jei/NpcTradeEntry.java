package com.hp.thermalshockreforged.compat.jei;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public record NpcTradeEntry(String npc, List<Row> rows, List<Component> info) {
    public record Row(List<ItemStack> costs, int levels, ItemStack result, Component condition) { }
}
