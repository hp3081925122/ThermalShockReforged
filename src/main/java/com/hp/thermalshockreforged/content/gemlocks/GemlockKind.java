package com.hp.thermalshockreforged.content.gemlocks;

import com.hp.thermalshockreforged.content.MaterialContent;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.material.MapColor;

public enum GemlockKind {
    RUBY("ruby", MapColor.COLOR_RED),
    MALACHITE("malachite", MapColor.COLOR_GREEN),
    BISMUTH("bismuth", MapColor.METAL),
    PHOBOTITE("phobotite", MapColor.COLOR_PURPLE);
    public final String gem;
    public final String id;
    public final MapColor mapColor;
    GemlockKind(String gem, MapColor mapColor) { this.gem = gem; id = gem + "_gemlock"; this.mapColor = mapColor; }
    public Item item() { return MaterialContent.items().get(gem).get(); }
}
