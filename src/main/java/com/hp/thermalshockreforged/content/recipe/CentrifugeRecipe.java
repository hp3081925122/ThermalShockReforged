package com.hp.thermalshockreforged.content.recipe;

import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

public record CentrifugeRecipe(List<Ingredient> energy, Ingredient ingredient, ItemStack result) implements DataRecipe {
    public static final MapCodec<CentrifugeRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Ingredient.CODEC.listOf().validate(list -> {
                if (list.size() == 2) return DataResult.success(list);
                return DataResult.error(() -> "Centrifuge needs exactly 2 energy ingredients");
            }).fieldOf("energy").forGetter(CentrifugeRecipe::energy),
            Ingredient.CODEC.fieldOf("ingredient").forGetter(CentrifugeRecipe::ingredient),
            ItemStack.CODEC.fieldOf("result").forGetter(CentrifugeRecipe::result)
    ).apply(instance, CentrifugeRecipe::new));

    public boolean matches(ItemStack first, ItemStack second, ItemStack extra) {
        return energy.get(0).test(first) && energy.get(1).test(second) && ingredient.test(extra);
    }

    public static CentrifugeRecipe find(Level level, ItemStack first, ItemStack second, ItemStack extra) {
        for (var holder : RecipeContent.CENTRIFUGE.all(level)) if (holder.value().matches(first, second, extra)) return holder.value();
        return null;
    }

    public static boolean accepts(Level level, int slot, ItemStack stack) {
        for (var holder : RecipeContent.CENTRIFUGE.all(level)) {
            var recipe = holder.value();
            if (slot < 2 && recipe.energy.get(slot).test(stack)) return true;
            if (slot == 2 && recipe.ingredient.test(stack)) return true;
        }
        return false;
    }

    public ItemStack getResultItem(HolderLookup.Provider registries) { return result; }
    @Override public RecipeSerializer<? extends DataRecipe> getSerializer() { return RecipeContent.CENTRIFUGE.serializer().get(); }
    @Override public RecipeType<? extends DataRecipe> getType() { return RecipeContent.CENTRIFUGE.type().get(); }
}
