package com.hp.thermalshockreforged.content.npc.laut;

import com.hp.thermalshockreforged.ThermalShockReforged;
import com.hp.thermalshockreforged.content.decor.geo.*;
import com.hp.thermalshockreforged.content.recipe.NpcTradeRecipe;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.registries.BuiltInRegistries;

public final class LautQuest {
    public enum Result { ACCEPTED, INSUFFICIENT, ALREADY_DONE, UNAVAILABLE }
    public static int available(ServerPlayer player) {
        int total = 0;
        var item = GeoDecorContent.item(GeoDecorKind.PLENKY);
        var inventory = player.getInventory();
        for (int slot = 0; slot < inventory.getContainerSize(); slot++) {
            if (slot >= Inventory.INVENTORY_SIZE && slot != Inventory.SLOT_OFFHAND) continue;
            var stack = inventory.getItem(slot);
            if (stack.is(item)) total += stack.getCount();
        }
        return total;
    }
    public static Result accept(ServerPlayer player, LautEntity npc, BlockPos anchor) {
        if (npc.completed() || !npc.isAlive()) return Result.ALREADY_DONE;
        if (available(player) < 3) return Result.INSUFFICIENT;
        var advancement = player.level().getServer().getAdvancements().get(Identifier.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, "babies_or_pets"));
        var reward = BuiltInRegistries.ITEM.getValue(Identifier.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, "adamantine_spice"));
        if (advancement == null || reward == net.minecraft.world.item.Items.AIR) { ThermalShockReforged.LOGGER.warn("Laut quest data is unavailable; keeping payment: player={}, npc={}", player.getUUID(), npc.getUUID()); return Result.UNAVAILABLE; }
        if (!npc.claim()) return Result.ALREADY_DONE;
        if (!NpcTradeRecipe.pay(player, GeoDecorContent.item(GeoDecorKind.PLENKY), 3)) { npc.release(); ThermalShockReforged.LOGGER.error("Laut quest payment changed unexpectedly: player={}, npc={}", player.getUUID(), npc.getUUID()); return Result.UNAVAILABLE; }
        var progress = player.getAdvancements().getOrStartProgress(advancement);
        if (!progress.isDone()) for (String criterion : progress.getRemainingCriteria()) player.getAdvancements().award(advancement, criterion);
        for (int i = 0; i < 10; i++) { var item = new ItemEntity(((net.minecraft.server.level.ServerLevel) player.level()), anchor.getX(), anchor.getY(), anchor.getZ(), new ItemStack(reward)); item.setPickUpDelay(10); ((net.minecraft.server.level.ServerLevel) player.level()).addFreshEntity(item); }
        player.getInventory().setChanged();
        player.containerMenu.broadcastChanges();
        ThermalShockReforged.LOGGER.debug("Laut quest committed: player={}, npc={}, paid=3, rewards=10", player.getUUID(), npc.getUUID());
        return Result.ACCEPTED;
    }
}
