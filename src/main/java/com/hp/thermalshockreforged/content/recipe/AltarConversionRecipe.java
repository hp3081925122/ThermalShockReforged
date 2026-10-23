package com.hp.thermalshockreforged.content.recipe;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootTable;

public record AltarConversionRecipe(Holder<Block> core, List<Holder<Block>> altars, ResourceKey<LootTable> lootTable) implements DataRecipe {
    public static final MapCodec<AltarConversionRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            BuiltInRegistries.BLOCK.holderByNameCodec().fieldOf("core").forGetter(AltarConversionRecipe::core),
            BuiltInRegistries.BLOCK.holderByNameCodec().listOf(1, 64).fieldOf("altars").forGetter(AltarConversionRecipe::altars),
            ResourceKey.codec(Registries.LOOT_TABLE).fieldOf("loot_table").forGetter(AltarConversionRecipe::lootTable)
    ).apply(instance, AltarConversionRecipe::new));

    public static AltarConversionRecipe find(Level level, Block core, BlockState altar) {
        for (var holder : RecipeContent.ALTAR_CONVERSION.all(level)) {
            var recipe = holder.value();
            if (recipe.core.value() != core) continue;
            for (var block : recipe.altars) if (altar.is(block)) return recipe;
        }
        return null;
    }

    @Override public RecipeSerializer<? extends DataRecipe> getSerializer() { return RecipeContent.ALTAR_CONVERSION.serializer().get(); }
    @Override public RecipeType<? extends DataRecipe> getType() { return RecipeContent.ALTAR_CONVERSION.type().get(); }
}
