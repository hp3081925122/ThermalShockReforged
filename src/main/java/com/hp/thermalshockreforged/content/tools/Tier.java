package com.hp.thermalshockreforged.content.tools;

import com.hp.thermalshockreforged.ThermalShockReforged;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ToolMaterial;
import net.minecraft.world.level.block.Block;

public interface Tier {
    int getUses();
    float getSpeed();
    float getAttackDamageBonus();
    TagKey<Block> getIncorrectBlocksForDrops();
    int getEnchantmentValue();
    TagKey<Item> getRepairItems();
    default ToolMaterial toolMaterial() {
        return new ToolMaterial(getIncorrectBlocksForDrops(), getUses(), getSpeed(), getAttackDamageBonus(), Math.max(1, getEnchantmentValue()), getRepairItems());
    }
    default Item.Properties enchantability(Item.Properties properties) {
        return getEnchantmentValue() > 0 ? properties : properties.component(net.minecraft.core.component.DataComponents.ENCHANTABLE, null);
    }
    static TagKey<Item> repairTag(String id) {
        return TagKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, "tool_repair/" + id));
    }
}
