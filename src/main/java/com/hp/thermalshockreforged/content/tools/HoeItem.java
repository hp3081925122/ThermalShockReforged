package com.hp.thermalshockreforged.content.tools;

public class HoeItem extends net.minecraft.world.item.HoeItem {
    public HoeItem(Tier tier, float damage, float speed, Properties properties) { super(tier.toolMaterial(), damage, speed, properties); }
}
