package com.hp.thermalshockreforged.content.tools;

import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.ToolMaterial;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.item.crafting.Ingredient;

public interface Tier {
    int getUses();
    float getSpeed();
    float getAttackDamageBonus();
    TagKey<Block> getIncorrectBlocksForDrops();
    int getEnchantmentValue();
    Ingredient getRepairIngredient();
    default ToolMaterial toolMaterial() {
        return new ToolMaterial(getIncorrectBlocksForDrops(), getUses(), getSpeed(), getAttackDamageBonus(), getEnchantmentValue(), ItemTags.create(Identifier.withDefaultNamespace("forge/ingots/iron")));
    }
}
