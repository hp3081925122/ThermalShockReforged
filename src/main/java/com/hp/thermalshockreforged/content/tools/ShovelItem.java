package com.hp.thermalshockreforged.content.tools;

public class ShovelItem extends net.minecraft.world.item.ShovelItem {
    public ShovelItem(Tier tier, float damage, float speed, Properties properties) { super(tier.toolMaterial(), damage, speed, properties); }
}
