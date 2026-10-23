package com.hp.thermalshockreforged.content;

import net.minecraft.core.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public final class PondlerEggBlockEntity extends RandomizableContainerBlockEntity implements WorldlyContainer {
    private static final int[] SLOTS = {0, 1, 2, 3, 4, 5, 6, 7, 8};
    private NonNullList<ItemStack> items = NonNullList.withSize(9, ItemStack.EMPTY);
    private int incubationAge;

    public PondlerEggBlockEntity(BlockPos position, BlockState state) {
        super(ModBlockEntities.PONDLER_EGG.get(), position, state);
    }

    public boolean advanceIncubation() {
        if (incubationAge >= 600) return true;
        incubationAge++;
        setChanged();
        return false;
    }

    @Override
    protected void loadAdditional(net.minecraft.world.level.storage.ValueInput input) {
        super.loadAdditional(input);
        incubationAge = Math.max(0, Math.min(600, tag.contains("IncubationAge") ? input.getIntOr("IncubationAge", 0) : (int) getPersistentData().getDouble("age")));
        items = NonNullList.withSize(9, ItemStack.EMPTY);
        if (!tryLoadLootTable(input)) ContainerHelper.loadAllItems(input, items);
    }

    @Override
    protected void saveAdditional(net.minecraft.world.level.storage.ValueOutput output) {
        super.saveAdditional(output);
        output.putInt("IncubationAge", incubationAge);
        if (!trySaveLootTable(output)) ContainerHelper.saveAllItems(output, items);
    }

    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() { return ClientboundBlockEntityDataPacket.create(this); }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) { return saveWithoutMetadata(registries); }

    @Override
    public int getContainerSize() { return 9; }

    @Override
    protected Component getDefaultName() { return Component.translatable("block.thermal_shock_reforged.pondler_egg"); }

    @Override
    protected AbstractContainerMenu createMenu(int id, Inventory inventory) { return new ChestMenu(MenuType.GENERIC_9x1, id, inventory, this, 1); }

    @Override
    protected NonNullList<ItemStack> getItems() { return items; }

    @Override
    protected void setItems(NonNullList<ItemStack> stacks) { items = stacks; }

    @Override
    public int[] getSlotsForFace(Direction side) { return SLOTS.clone(); }

    @Override
    public boolean canPlaceItemThroughFace(int slot, ItemStack stack, Direction side) { return true; }

    @Override
    public boolean canTakeItemThroughFace(int slot, ItemStack stack, Direction side) { return true; }
}
