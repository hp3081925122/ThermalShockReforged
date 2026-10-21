package com.hp.thermalshockreforged.content.fabrication;

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
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

public final class FabricationRecipe implements Recipe<FabricationRecipe.Input> {
    public record Cost(Holder<Item> item, int count) {
        public static final Codec<Cost> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                ItemStack.ITEM_NON_AIR_CODEC.fieldOf("item").forGetter(Cost::item),
                Codec.intRange(1, 4096).fieldOf("count").forGetter(Cost::count)
        ).apply(instance, Cost::new));
        public static final StreamCodec<RegistryFriendlyByteBuf, Cost> STREAM_CODEC = StreamCodec.composite(
                ByteBufCodecs.<Item>holderRegistry(Registries.ITEM), Cost::item,
                ByteBufCodecs.VAR_INT, Cost::count, Cost::new);

        public Cost {
            if (new ItemStack(item).isEmpty() || count < 1 || count > 4096) throw new IllegalArgumentException("Invalid fabrication cost");
        }
        public boolean matches(ItemStack stack) { return stack.is(item.value()) && stack.getCount() >= count; }
    }

    public record Input(List<ItemStack> materials, ItemStack energy) implements RecipeInput {
        public Input { materials = List.copyOf(materials); }
        @Override public ItemStack getItem(int index) { return index == materials.size() ? energy : materials.get(index); }
        @Override public int size() { return materials.size() + 1; }
    }

    private final List<Cost> materials;
    private final Cost energy;
    private final ItemStack result;
    private final Map<Item, Integer> totals;

    public FabricationRecipe(List<Cost> materials, Cost energy, ItemStack result) {
        if (materials.isEmpty() || materials.size() > 64 || result.isEmpty()) throw new IllegalArgumentException("Invalid fabrication recipe");
        this.materials = List.copyOf(materials);
        this.energy = energy;
        this.result = result.copy();
        var counts = new LinkedHashMap<Item, Integer>();
        for (var cost : materials) counts.merge(cost.item().value(), cost.count(), Integer::sum);
        totals = Map.copyOf(counts);
    }

    public int[] removalPlan(Input input) {
        if (!energy.matches(input.energy())) return null;
        int[] plan = new int[input.materials().size()];
        for (var cost : totals.entrySet()) {
            int remaining = cost.getValue();
            for (int slot = 0; slot < plan.length && remaining > 0; slot++) {
                var stack = input.materials().get(slot);
                if (stack.is(cost.getKey())) {
                    int take = Math.min(remaining, stack.getCount());
                    plan[slot] = take;
                    remaining -= take;
                }
            }
            if (remaining > 0) return null;
        }
        return plan;
    }

    public int energyCount() { return energy.count(); }
    public List<Cost> materials() { return materials; }
    public Cost energy() { return energy; }
    @Override public boolean matches(Input input, Level level) { return removalPlan(input) != null; }
    @Override public ItemStack assemble(Input input, HolderLookup.Provider registries) { return result.copy(); }
    @Override public ItemStack getResultItem(HolderLookup.Provider registries) { return result.copy(); }
    @Override public boolean canCraftInDimensions(int width, int height) { return false; }
    @Override public boolean isSpecial() { return true; }
    @Override public boolean showNotification() { return false; }
    @Override public boolean isIncomplete() { return false; }
    @Override public RecipeSerializer<?> getSerializer() { return FabricationContent.SERIALIZER.get(); }
    @Override public RecipeType<?> getType() { return FabricationContent.TYPE.get(); }
    @Override public NonNullList<ItemStack> getRemainingItems(Input input) { return NonNullList.withSize(input.size(), ItemStack.EMPTY); }
    @Override public NonNullList<Ingredient> getIngredients() {
        var ingredients = NonNullList.<Ingredient>create();
        for (var cost : materials) ingredients.add(Ingredient.of(cost.item().value()));
        return ingredients;
    }

    public static final class Serializer implements RecipeSerializer<FabricationRecipe> {
        private static final Codec<List<Cost>> MATERIALS_CODEC = Cost.CODEC.listOf().validate(costs ->
                costs.isEmpty() || costs.size() > 64 ? DataResult.error(() -> "Fabrication needs 1 to 64 material entries") : DataResult.success(costs));
        private static final MapCodec<FabricationRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                MATERIALS_CODEC.fieldOf("materials").forGetter(recipe -> recipe.materials),
                Cost.CODEC.fieldOf("energy").forGetter(recipe -> recipe.energy),
                ItemStack.STRICT_CODEC.fieldOf("result").forGetter(recipe -> recipe.result)
        ).apply(instance, FabricationRecipe::new));
        private static final StreamCodec<RegistryFriendlyByteBuf, FabricationRecipe> STREAM_CODEC = StreamCodec.composite(
                Cost.STREAM_CODEC.apply(ByteBufCodecs.list(64)), recipe -> recipe.materials,
                Cost.STREAM_CODEC, recipe -> recipe.energy,
                ItemStack.STREAM_CODEC, recipe -> recipe.result,
                FabricationRecipe::new);
        @Override public MapCodec<FabricationRecipe> codec() { return CODEC; }
        @Override public StreamCodec<RegistryFriendlyByteBuf, FabricationRecipe> streamCodec() { return STREAM_CODEC; }
    }
}
