package com.hp.thermalshockreforged.compat.jei;

import net.minecraft.network.chat.Component;

import java.util.List;

public record CacheLootEntry(List<Line> lines) {
    public record Line(List<MachineEntry.Slot> slots, Component title) { }
}
