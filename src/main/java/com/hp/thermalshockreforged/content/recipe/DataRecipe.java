package com.hp.thermalshockreforged.content.recipe;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.PlacementInfo;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeBookCategories;
import net.minecraft.world.item.crafting.RecipeBookCategory;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.level.Level;

public interface DataRecipe extends Recipe<RecipeInput> {
    @Override default boolean matches(RecipeInput input, Level level) { return false; }
    @Override default ItemStack assemble(RecipeInput input) { return ItemStack.EMPTY; }
    @Override default boolean isSpecial() { return true; }
    @Override default boolean showNotification() { return false; }
    @Override default String group() { return ""; }
    @Override default PlacementInfo placementInfo() { return PlacementInfo.NOT_PLACEABLE; }
    @Override default RecipeBookCategory recipeBookCategory() { return RecipeBookCategories.CRAFTING_MISC; }
}
