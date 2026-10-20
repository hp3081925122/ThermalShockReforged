package com.hp.thermalshockreforged.content.npc.concierge;

import com.hp.thermalshockreforged.ThermalShockReforged;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.items.ItemHandlerHelper;

public record ConciergeTrade(String item, int count, int levels, int spiceCount) {
    public ConciergeTrade {
        if (count <= 0 || levels < 0 || spiceCount < 0) throw new IllegalArgumentException("Invalid Concierge trade amount");
    }
    public boolean buy(ServerPlayer player) {
        if (item.equals("panacea") && !ConciergeQuest.completed(player)) return false;
        Item reward = BuiltInRegistries.ITEM.get(ConciergeContent.id(item));
        Item currency = BuiltInRegistries.ITEM.get(ConciergeContent.id("adamantine_spice"));
        if (reward == Items.AIR || currency == Items.AIR || player.experienceLevel < levels) return false;
        int inventoryNeeded = spiceCount;
        for (int slot = 0; slot < player.getInventory().getContainerSize() && inventoryNeeded > 0; slot++) {
            ItemStack stack = player.getInventory().getItem(slot);
            if (stack.is(currency)) inventoryNeeded -= Math.min(inventoryNeeded, stack.getCount());
        }
        if (inventoryNeeded != 0) {
            ThermalShockReforged.LOGGER.debug("Concierge trade inventory qualification rejected: player={}, item={}, requiredSpice={}, missingSpice={}", player.getUUID(), item, spiceCount, inventoryNeeded);
            return false;
        }
        if (!pay(player, currency, spiceCount)) return false;
        player.giveExperienceLevels(-levels);
        ItemHandlerHelper.giveItemToPlayer(player, new ItemStack(reward, count));
        player.getInventory().setChanged(); player.containerMenu.broadcastChanges();
        ThermalShockReforged.LOGGER.debug("Concierge trade committed: player={}, item={}, count={}, spice={}, levels={}", player.getUUID(), item, count, spiceCount, levels);
        return true;
    }
    static boolean pay(ServerPlayer player, Item currency, int amount) {
        if (amount <= 0) return amount == 0;
        List<Debit> plan = new ArrayList<>();
        int needed = amount;
        for (Container container : List.of(player.getInventory(), player.inventoryMenu.getCraftSlots())) {
            for (int slot = 0; slot < container.getContainerSize() && needed > 0; slot++) {
                ItemStack stack = container.getItem(slot);
                if (!stack.is(currency)) continue;
                int take = Math.min(needed, stack.getCount());
                plan.add(new Debit(container, stack, take)); needed -= take;
            }
        }
        if (needed != 0) return false;
        for (Debit debit : plan) debit.stack.shrink(debit.count);
        for (Debit debit : plan) debit.container.setChanged();
        return true;
    }
    private record Debit(Container container, ItemStack stack, int count) { }
}
