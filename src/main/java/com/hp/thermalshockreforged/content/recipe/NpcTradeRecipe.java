package com.hp.thermalshockreforged.content.recipe;

import com.hp.thermalshockreforged.ThermalShockReforged;
import com.hp.thermalshockreforged.content.fabrication.FabricationRecipe;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.items.ItemHandlerHelper;

public record NpcTradeRecipe(List<FabricationRecipe.Cost> costs, int levels, ItemStack result, Optional<Identifier> requires) implements DataRecipe {
    public static final MapCodec<NpcTradeRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            FabricationRecipe.Cost.CODEC.listOf().optionalFieldOf("costs", List.of()).forGetter(NpcTradeRecipe::costs),
            Codec.intRange(0, Integer.MAX_VALUE).optionalFieldOf("levels", 0).forGetter(NpcTradeRecipe::levels),
            ItemStack.STRICT_CODEC.fieldOf("result").forGetter(NpcTradeRecipe::result),
            Identifier.CODEC.optionalFieldOf("requires").forGetter(NpcTradeRecipe::requires)
    ).apply(instance, NpcTradeRecipe::new));

    public static Identifier id(String page) { return RecipeContent.id("npc_trade/" + page); }

    public static NpcTradeRecipe get(Level level, String page) {
        var recipe = RecipeContent.NPC_TRADE.get(level, id(page));
        if (recipe == null) ThermalShockReforged.LOGGER.warn("NPC trade recipe missing: {}", id(page));
        return recipe;
    }

    public int cost(Item item) {
        int total = 0;
        for (var cost : costs) if (cost.item().value() == item) total += cost.count();
        return total;
    }

    public boolean allowed(ServerPlayer player) {
        if (requires.isEmpty()) return true;
        var advancement = player.server.getAdvancements().get(requires.get());
        return advancement != null && player.getAdvancements().getOrStartProgress(advancement).isDone();
    }

    public boolean trade(ServerPlayer player) {
        if (player.experienceLevel < levels) return false;
        var totals = new LinkedHashMap<Item, Integer>();
        for (var cost : costs) totals.merge(cost.item().value(), cost.count(), Integer::sum);
        for (Map.Entry<Item, Integer> cost : totals.entrySet()) {
            int owned = 0;
            for (int slot = 0; slot < player.getInventory().getContainerSize(); slot++) {
                var stack = player.getInventory().getItem(slot);
                if (stack.is(cost.getKey())) owned += stack.getCount();
            }
            if (owned < cost.getValue()) {
                ThermalShockReforged.LOGGER.debug("NPC trade rejected for missing cost: player={}, item={}, required={}, owned={}", player.getUUID(), cost.getKey(), cost.getValue(), owned);
                return false;
            }
        }
        for (Map.Entry<Item, Integer> cost : totals.entrySet()) {
            if (!pay(player, cost.getKey(), cost.getValue())) {
                ThermalShockReforged.LOGGER.error("NPC trade payment failed after qualification: player={}, item={}, required={}", player.getUUID(), cost.getKey(), cost.getValue());
                return false;
            }
        }
        if (levels > 0) player.giveExperienceLevels(-levels);
        ItemHandlerHelper.giveItemToPlayer(player, result.copy());
        player.getInventory().setChanged();
        player.containerMenu.broadcastChanges();
        ThermalShockReforged.LOGGER.debug("NPC trade committed: player={}, result={}, count={}, levels={}, costs={}", player.getUUID(), result.getItem(), result.getCount(), levels, totals);
        return true;
    }

    public static boolean pay(ServerPlayer player, Item currency, int amount) {
        if (amount <= 0) return amount == 0;
        List<Debit> plan = new ArrayList<>();
        int needed = amount;
        for (Container container : List.of(player.getInventory(), player.inventoryMenu.getCraftSlots())) {
            for (int slot = 0; slot < container.getContainerSize() && needed > 0; slot++) {
                ItemStack stack = container.getItem(slot);
                if (!stack.is(currency)) continue;
                int take = Math.min(needed, stack.getCount());
                plan.add(new Debit(container, stack, take));
                needed -= take;
            }
        }
        if (needed != 0) return false;
        for (Debit debit : plan) debit.stack.shrink(debit.count);
        for (Debit debit : plan) debit.container.setChanged();
        return true;
    }

    @Override public ItemStack getResultItem(HolderLookup.Provider registries) { return result; }
    @Override public RecipeSerializer<?> getSerializer() { return RecipeContent.NPC_TRADE.serializer().get(); }
    @Override public RecipeType<?> getType() { return RecipeContent.NPC_TRADE.type().get(); }

    private record Debit(Container container, ItemStack stack, int count) { }
}
