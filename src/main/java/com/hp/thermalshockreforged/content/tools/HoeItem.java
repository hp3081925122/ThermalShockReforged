package com.hp.thermalshockreforged.content.tools;

import net.minecraft.world.item.Item;

public class HoeItem extends Item {
    public HoeItem(Tier tier, Properties properties) { super(tier.toolMaterial().applyToolProperties(properties, tier.getIncorrectBlocksForDrops(), 0, -3.0F, 0)); }
}
