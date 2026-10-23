package com.hp.thermalshockreforged.content.tools;

public class AxeItem extends net.minecraft.world.item.AxeItem {
    public AxeItem(Tier tier, float damage, float speed, Properties properties) { super(tier.toolMaterial(), damage, speed, properties); }
}
