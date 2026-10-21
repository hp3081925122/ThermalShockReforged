package com.hp.thermalshockreforged.content.recipe;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

public record TransmutationRecipe(Ingredient ingredient, ItemStack result) implements DataRecipe {
    public static final MapCodec<TransmutationRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Ingredient.CODEC_NONEMPTY.fieldOf("ingredient").forGetter(TransmutationRecipe::ingredient),
            ItemStack.STRICT_CODEC.fieldOf("result").forGetter(TransmutationRecipe::result)
    ).apply(instance, TransmutationRecipe::new));

    public static TransmutationRecipe find(Level level, ItemStack stack) {
        if (stack.isEmpty()) return null;
        for (var holder : RecipeContent.TRANSMUTATION.all(level)) if (holder.value().ingredient.test(stack)) return holder.value();
        return null;
    }

    @Override public ItemStack getResultItem(HolderLookup.Provider registries) { return result; }
    @Override public RecipeSerializer<?> getSerializer() { return RecipeContent.TRANSMUTATION.serializer().get(); }
    @Override public RecipeType<?> getType() { return RecipeContent.TRANSMUTATION.type().get(); }
}
