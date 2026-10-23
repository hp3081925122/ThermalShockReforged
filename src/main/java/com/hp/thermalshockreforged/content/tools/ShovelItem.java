package com.hp.thermalshockreforged.content.tools;

import net.minecraft.world.item.Item;

public class ShovelItem extends Item {
    public ShovelItem(Tier tier, Properties properties) { super(tier.toolMaterial().applyToolProperties(properties, tier.getIncorrectBlocksForDrops(), 1, -2.8F, 0)); }
}
