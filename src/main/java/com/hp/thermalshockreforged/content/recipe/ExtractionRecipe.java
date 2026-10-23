package com.hp.thermalshockreforged.content.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Optional;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;

public record ExtractionRecipe(Ingredient ingredient, int levels, Optional<Experience> experience, List<Output> outputs, Optional<Identifier> advancement, List<RecipeSound> sounds, Optional<Holder<Block>> particleBlock) implements DataRecipe {
    public record Experience(int value, IntProvider orbs) {
        public static final Codec<Experience> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Codec.intRange(1, Integer.MAX_VALUE).fieldOf("value").forGetter(Experience::value),
                IntProvider.POSITIVE_CODEC.optionalFieldOf("orbs", ConstantInt.of(1)).forGetter(Experience::orbs)
        ).apply(instance, Experience::new));
    }

    public record Output(ItemStack item, float chance, IntProvider repeat) {
        public static final Codec<Output> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                ItemStack.STRICT_CODEC.fieldOf("item").forGetter(Output::item),
                Codec.floatRange(0, 1).optionalFieldOf("chance", 1F).forGetter(Output::chance),
                IntProvider.POSITIVE_CODEC.optionalFieldOf("repeat", ConstantInt.of(1)).forGetter(Output::repeat)
        ).apply(instance, Output::new));

        public boolean guaranteed() { return chance >= 1; }
    }

    public static final MapCodec<ExtractionRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Ingredient.CODEC_NONEMPTY.fieldOf("ingredient").forGetter(ExtractionRecipe::ingredient),
            Codec.intRange(0, Integer.MAX_VALUE).optionalFieldOf("levels", 0).forGetter(ExtractionRecipe::levels),
            Experience.CODEC.optionalFieldOf("experience").forGetter(ExtractionRecipe::experience),
            Output.CODEC.listOf().optionalFieldOf("outputs", List.of()).forGetter(ExtractionRecipe::outputs),
            Identifier.CODEC.optionalFieldOf("advancement").forGetter(ExtractionRecipe::advancement),
            RecipeSound.CODEC.listOf().optionalFieldOf("sounds", List.of()).forGetter(ExtractionRecipe::sounds),
            BuiltInRegistries.BLOCK.holderByNameCodec().optionalFieldOf("particle_block").forGetter(ExtractionRecipe::particleBlock)
    ).apply(instance, ExtractionRecipe::new));

    public static ExtractionRecipe find(Level level, ItemStack stack) {
        if (stack.isEmpty()) return null;
        for (var holder : RecipeContent.EXTRACTION.all(level)) if (holder.value().ingredient.test(stack)) return holder.value();
        return null;
    }

    @Override public RecipeSerializer<?> getSerializer() { return RecipeContent.EXTRACTION.serializer().get(); }
    @Override public RecipeType<?> getType() { return RecipeContent.EXTRACTION.type().get(); }
}
