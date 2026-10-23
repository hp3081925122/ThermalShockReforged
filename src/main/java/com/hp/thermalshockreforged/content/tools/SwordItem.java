package com.hp.thermalshockreforged.content.tools;

import net.minecraft.world.item.Item;

public class SwordItem extends Item {
    public SwordItem(Tier tier, float damage, float speed, Properties properties) { super(properties.sword(tier.toolMaterial(), damage, speed)); }
}
