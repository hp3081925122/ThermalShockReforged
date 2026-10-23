package com.hp.thermalshockreforged.content.tools;

import net.minecraft.world.item.Item;

public class PickaxeItem extends Item {
    public PickaxeItem(Tier tier, float damage, float speed, Properties properties) { super(properties.pickaxe(tier.toolMaterial(), damage, speed)); }
}
