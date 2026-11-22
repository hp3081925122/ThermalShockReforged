package com.hp.thermalshockreforged.content.weapons;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public final class WeaponAmmo {
    private WeaponAmmo() { }

    public static boolean has(Player player, Item ammo) {
        if (player.getAbilities().instabuild) return true;
        return findSlot(player.getInventory(), ammo) >= 0;
    }

    public static boolean consume(Player player, Item ammo) {
        if (player.getAbilities().instabuild) return true;
        Inventory inventory = player.getInventory();
        int slot = findSlot(inventory, ammo);
        if (slot < 0) return false;
        inventory.getItem(slot).shrink(1);
        return true;
    }

    private static int findSlot(Inventory inventory, Item ammo) {
        ItemStack plain = new ItemStack(ammo);
        ItemStack offhand = inventory.getItem(Inventory.SLOT_OFFHAND);
        if (ItemStack.isSameItemSameComponents(offhand, plain)) return Inventory.SLOT_OFFHAND;
        int fallback = offhand.is(ammo) ? Inventory.SLOT_OFFHAND : -1;
        for (int index = 0; index < Inventory.INVENTORY_SIZE; index++) {
            ItemStack candidate = inventory.getItem(index);
            if (!candidate.is(ammo)) continue;
            if (ItemStack.isSameItemSameComponents(candidate, plain)) return index;
            if (fallback < 0) fallback = index;
        }
        return fallback;
    }
}
