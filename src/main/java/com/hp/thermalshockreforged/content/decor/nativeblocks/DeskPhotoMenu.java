package com.hp.thermalshockreforged.content.decor.nativeblocks;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.item.ItemStack;

public final class DeskPhotoMenu extends AbstractContainerMenu {
    private final ContainerLevelAccess access;

    public DeskPhotoMenu(int id, Inventory inventory) {
        this(id, inventory, ContainerLevelAccess.NULL);
    }

    public DeskPhotoMenu(int id, Inventory inventory, ContainerLevelAccess access) {
        super(NativeDecorContent.DESK_PHOTO_SCREEN.get(), id);
        this.access = access;
    }

    @Override
    public boolean stillValid(Player player) {
        return player.isAlive() && stillValid(access, player, NativeDecorContent.DESK_PHOTO.get());
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        return ItemStack.EMPTY;
    }
}
