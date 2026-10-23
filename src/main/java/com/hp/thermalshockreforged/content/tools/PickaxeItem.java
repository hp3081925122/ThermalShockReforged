package com.hp.thermalshockreforged.content.tools;

import net.minecraft.world.item.Item;

public class PickaxeItem extends Item {
    public PickaxeItem(Tier tier, Properties properties) { super(tier.toolMaterial().applyToolProperties(properties, tier.getIncorrectBlocksForDrops(), 1, -2.8F, 0)); }
}
