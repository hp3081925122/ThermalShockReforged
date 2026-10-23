package com.hp.thermalshockreforged.content.tools;

import net.minecraft.world.item.Item;

public class AxeItem extends Item {
    public AxeItem(Tier tier, Properties properties) { super(tier.toolMaterial().applyToolProperties(properties, tier.getIncorrectBlocksForDrops(), 5, -3.0F, 0)); }
}
