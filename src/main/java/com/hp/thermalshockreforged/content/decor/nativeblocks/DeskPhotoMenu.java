package com.hp.thermalshockreforged.content.decor.nativeblocks;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;

public final class DeskPhotoMenu extends AbstractContainerMenu {
    public DeskPhotoMenu(int id, Inventory inventory) {
        super(NativeDecorContent.DESK_PHOTO_SCREEN.get(), id);
    }

    @Override
    public boolean stillValid(Player player) {
        return true;
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        return ItemStack.EMPTY;
    }
}
