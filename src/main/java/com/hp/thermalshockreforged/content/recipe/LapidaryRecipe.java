package com.hp.thermalshockreforged.content.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.loot.LootTable;

public record LapidaryRecipe(Ingredient ingredient, Optional<ResourceKey<LootTable>> lootTable, Optional<ItemStack> result, RecipeSound sound, float extraRollChance, float springlingChance, Optional<ResourceLocation> advancement) implements DataRecipe {
    public static final MapCodec<LapidaryRecipe> CODEC = RecordCodecBuilder.<LapidaryRecipe>mapCodec(instance -> instance.group(
            Ingredient.CODEC_NONEMPTY.fieldOf("ingredient").forGetter(LapidaryRecipe::ingredient),
            ResourceKey.codec(Registries.LOOT_TABLE).optionalFieldOf("loot_table").forGetter(LapidaryRecipe::lootTable),
            ItemStack.STRICT_CODEC.optionalFieldOf("result").forGetter(LapidaryRecipe::result),
            RecipeSound.CODEC.fieldOf("sound").forGetter(LapidaryRecipe::sound),
            Codec.floatRange(0, 1).optionalFieldOf("extra_roll_chance", 0F).forGetter(LapidaryRecipe::extraRollChance),
            Codec.floatRange(0, 1).optionalFieldOf("springling_chance", 0F).forGetter(LapidaryRecipe::springlingChance),
            ResourceLocation.CODEC.optionalFieldOf("advancement").forGetter(LapidaryRecipe::advancement)
    ).apply(instance, LapidaryRecipe::new)).validate(recipe -> {
        if (recipe.lootTable.isPresent() != recipe.result.isPresent()) return DataResult.success(recipe);
        return DataResult.error(() -> "Lapidary recipe needs exactly one of loot_table or result");
    });

    public static LapidaryRecipe find(Level level, ItemStack stack) {
        if (stack.isEmpty()) return null;
        for (var holder : RecipeContent.LAPIDARY.all(level)) if (holder.value().ingredient.test(stack)) return holder.value();
        return null;
    }

    @Override public ItemStack getResultItem(HolderLookup.Provider registries) { return result.orElse(ItemStack.EMPTY); }
    @Override public RecipeSerializer<?> getSerializer() { return RecipeContent.LAPIDARY.serializer().get(); }
    @Override public RecipeType<?> getType() { return RecipeContent.LAPIDARY.type().get(); }
}
