package com.hp.thermalshockreforged.content.recipe;

import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.level.Level;

public interface DataRecipe extends Recipe<RecipeInput> {
    @Override default boolean matches(RecipeInput input, Level level) { return false; }
    @Override default ItemStack assemble(RecipeInput input, HolderLookup.Provider registries) { return getResultItem(registries).copy(); }
    @Override default boolean canCraftInDimensions(int width, int height) { return false; }
    @Override default ItemStack getResultItem(HolderLookup.Provider registries) { return ItemStack.EMPTY; }
    @Override default boolean isSpecial() { return true; }
    @Override default boolean showNotification() { return false; }
    @Override default boolean isIncomplete() { return false; }
}
