package com.hp.thermalshockreforged.content.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.Container;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

public record AssemblerRecipe(List<Cost> materials, ItemStack result) implements DataRecipe {
    public record Cost(Holder<Item> item, int count) {
        public static final Codec<Cost> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Item.CODEC.fieldOf("item").forGetter(Cost::item),
                Codec.intRange(1, 4096).fieldOf("count").forGetter(Cost::count)
        ).apply(instance, Cost::new));
        public static final StreamCodec<RegistryFriendlyByteBuf, Cost> STREAM_CODEC = StreamCodec.composite(
                ByteBufCodecs.<Item>holderRegistry(Registries.ITEM), Cost::item,
                ByteBufCodecs.VAR_INT, Cost::count, Cost::new);

        public Cost {
            if (new ItemStack(item).isEmpty() || count < 1 || count > 4096) throw new IllegalArgumentException("Invalid assembler cost");
        }
    }

    private static final Codec<List<Cost>> MATERIALS_CODEC = Cost.CODEC.listOf().validate(materials ->
            materials.isEmpty() || materials.size() > 8 ? DataResult.error(() -> "Assembler needs 1 to 8 material entries") : DataResult.success(materials));
    public static final MapCodec<AssemblerRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            MATERIALS_CODEC.fieldOf("materials").forGetter(AssemblerRecipe::materials),
            ItemStack.CODEC.fieldOf("result").forGetter(AssemblerRecipe::result)
    ).apply(instance, AssemblerRecipe::new));
    private static final StreamCodec<RegistryFriendlyByteBuf, AssemblerRecipe> STREAM_CODEC = StreamCodec.composite(
            Cost.STREAM_CODEC.apply(ByteBufCodecs.list(8)), AssemblerRecipe::materials,
            ItemStack.STREAM_CODEC, AssemblerRecipe::result,
            AssemblerRecipe::new);
    public AssemblerRecipe {
        if (materials.isEmpty() || materials.size() > 8 || result.isEmpty()) throw new IllegalArgumentException("Invalid assembler recipe");
        materials = List.copyOf(materials);
        result = result.copy();
    }

    private Map<Item, Integer> totals() {
        var merged = new LinkedHashMap<Item, Integer>();
        for (var cost : materials) merged.merge(cost.item().value(), cost.count(), Integer::sum);
        return merged;
    }

    public static AssemblerRecipe find(Level level, Container inventory) {
        for (var holder : RecipeContent.ASSEMBLER.all(level)) if (holder.value().matches(inventory)) return holder.value();
        return null;
    }

    public int[] removalPlan(Container inventory) {
        int[] plan = new int[inventory.getContainerSize()];
        for (var cost : totals().entrySet()) {
            int remaining = cost.getValue();
            for (int slot = 0; slot < plan.length && remaining > 0; slot++) {
                var stack = inventory.getItem(slot);
                if (!stack.is(cost.getKey())) continue;
                int take = Math.min(remaining, stack.getCount() - plan[slot]);
                if (take > 0) { plan[slot] += take; remaining -= take; }
            }
            if (remaining > 0) return null;
        }
        return plan;
    }

    public boolean matches(Container inventory) { return removalPlan(inventory) != null; }
    public void consume(Container inventory, int[] plan) { for (int slot = 0; slot < plan.length; slot++) if (plan[slot] > 0) inventory.getItem(slot).shrink(plan[slot]); }
    public ItemStack getResultItem(HolderLookup.Provider registries) { return result.copy(); }
    public RecipeSerializer<? extends DataRecipe> getSerializer() { return RecipeContent.ASSEMBLER.serializer().get(); }
    public RecipeType<? extends DataRecipe> getType() { return RecipeContent.ASSEMBLER.type().get(); }
    public NonNullList<Ingredient> getIngredients() {
        var ingredients = NonNullList.<Ingredient>create();
        for (var cost : materials) ingredients.add(Ingredient.of(cost.item().value()));
        return ingredients;
    }
}
