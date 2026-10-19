package com.hp.thermalshockreforged.content.outcrops;

import net.minecraft.world.level.material.MapColor;

public enum OutcropKind {
    MALACHITE("malachite_outcrop", 1, MapColor.COLOR_GREEN),
    BISMUTH("bismuth_outcrop", 2, MapColor.METAL);
    public final String id;
    public final int oreChoice;
    public final MapColor mapColor;
    OutcropKind(String id, int oreChoice, MapColor mapColor) { this.id = id; this.oreChoice = oreChoice; this.mapColor = mapColor; }
}
