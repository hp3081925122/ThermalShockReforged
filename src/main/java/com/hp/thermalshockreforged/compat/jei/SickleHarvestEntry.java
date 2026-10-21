package com.hp.thermalshockreforged.compat.jei;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public record SickleHarvestEntry(List<ItemStack> sickles, List<ItemStack> plant, List<Drop> drops, List<Component> biomes) {
    public record Drop(List<ItemStack> stacks, Component note) { }
}
