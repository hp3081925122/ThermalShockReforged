package com.hp.thermalshockreforged.content.centrifuge;

import com.hp.thermalshockreforged.ThermalShockReforged;
import com.hp.thermalshockreforged.content.recipe.CentrifugeRecipe;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.items.ItemHandlerHelper;

public final class CentrifugeMenu extends AbstractContainerMenu {
    private final Level level;
    private final BlockPos pos;
    private final Player owner;
    private final CentrifugeBlockEntity attached;
    private final Container storage;
    public CentrifugeMenu(int id, Inventory inventory, RegistryFriendlyByteBuf data) { this(id, inventory, data.readBlockPos()); }
    public CentrifugeMenu(int id, Inventory inventory, BlockPos pos) {
        super(CentrifugeContent.MENU.get(), id);
        level = inventory.player.level();
        owner = inventory.player;
        this.pos = pos.immutable();
        attached = level.hasChunkAt(pos) && level.getBlockEntity(pos) instanceof CentrifugeBlockEntity entity ? entity : null;
        storage = !level.isClientSide() && attached != null ? attached : new SimpleContainer(3) { @Override public int getMaxStackSize() { return 64; } };
        addSlot(new Slot(storage, 0, 59, 35) { @Override public boolean mayPlace(ItemStack stack) { return CentrifugeRecipe.accepts(level, 0, stack); } });
        addSlot(new Slot(storage, 1, 80, 10) { @Override public boolean mayPlace(ItemStack stack) { return CentrifugeRecipe.accepts(level, 1, stack); } });
        addSlot(new Slot(storage, 2, 101, 35) { @Override public boolean mayPlace(ItemStack stack) { return CentrifugeRecipe.accepts(level, 2, stack); } });
        for (int row = 0; row < 3; row++) for (int column = 0; column < 9; column++) addSlot(new Slot(inventory, column + (row + 1) * 9, 8 + column * 18, 84 + row * 18));
        for (int column = 0; column < 9; column++) addSlot(new Slot(inventory, column, 8 + column * 18, 142));
    }
    public static boolean open(ServerPlayer player, BlockPos pos) {
        var level = ((net.minecraft.server.level.ServerLevel) player.level());
        if (!player.isAlive() || !level.hasChunkAt(pos) || player.distanceToSqr(pos.getCenter()) > 64
                || !level.getBlockState(pos).is(CentrifugeContent.BLOCK.get()) || !(level.getBlockEntity(pos) instanceof CentrifugeBlockEntity)) return false;
        var fixed = pos.immutable();
        return player.openMenu(new SimpleMenuProvider((id, inventory, owner) -> new CentrifugeMenu(id, inventory, fixed),
                Component.translatable("block.thermal_shock_reforged.centrifuge")), buffer -> buffer.writeBlockPos(fixed)).isPresent();
    }
    @Override public boolean stillValid(Player player) {
        return player == owner && player.isAlive() && player.level() == level && level.hasChunkAt(pos)
                && player.distanceToSqr(pos.getCenter()) <= 64 && level.getBlockState(pos).is(CentrifugeContent.BLOCK.get())
                && (level.isClientSide() || attached != null && !attached.isRemoved() && level.getBlockEntity(pos) == attached);
    }
    @Override public boolean clickMenuButton(Player player, int button) {
        if (button != 0 || !(player instanceof ServerPlayer) || player.containerMenu != this || !stillValid(player)) return false;
        var first = storage.getItem(0);
        var second = storage.getItem(1);
        var ingredient = storage.getItem(2);
        var recipe = CentrifugeRecipe.find(level, first, second, ingredient);
        if (recipe == null) return false;
        var result = recipe.result().copy();
        first.shrink(1);
        second.shrink(1);
        ingredient.shrink(1);
        storage.setChanged();
        ItemHandlerHelper.giveItemToPlayer(player, result.copy());
        broadcastChanges();
        ThermalShockReforged.LOGGER.debug("Centrifuge conversion completed: output={}, player={}, position={}", result, player.getUUID(), pos);
        return true;
    }
    @Override public void clicked(int slot, int button, ContainerInput type, Player player) {
        if (stillValid(player)) super.clicked(slot, button, type, player);
    }
    @Override public ItemStack quickMoveStack(Player player, int index) {
        if (!stillValid(player) || index < 0 || index >= slots.size()) return ItemStack.EMPTY;
        var slot = slots.get(index);
        if (!slot.hasItem() || !slot.mayPickup(player)) return ItemStack.EMPTY;
        var stack = slot.getItem();
        var original = stack.copy();
        if (index < 3) {
            if (!moveItemStackTo(stack, 3, slots.size(), true)) return ItemStack.EMPTY;
            slot.onQuickCraft(stack, original);
        } else if (!moveItemStackTo(stack, 0, 3, false)) {
            if (index < 30 ? !moveItemStackTo(stack, 30, slots.size(), true) : !moveItemStackTo(stack, 3, 30, false)) return ItemStack.EMPTY;
        }
        if (stack.isEmpty()) slot.set(ItemStack.EMPTY); else slot.setChanged();
        if (stack.getCount() == original.getCount()) return ItemStack.EMPTY;
        slot.onTake(player, stack);
        return original;
    }
    @Override protected boolean moveItemStackTo(ItemStack stack, int start, int end, boolean reverse) {
        boolean changed = false;
        int index = reverse ? end - 1 : start;
        if (stack.isStackable()) {
            while (!stack.isEmpty() && (reverse ? index >= start : index < end)) {
                var slot = slots.get(index);
                var existing = slot.getItem();
                if (!existing.isEmpty() && slot.mayPlace(existing) && ItemStack.isSameItemSameComponents(stack, existing)) {
                    int capacity = slot.getMaxStackSize(existing) - existing.getCount();
                    if (capacity > 0) {
                        int count = Math.min(capacity, stack.getCount());
                        stack.shrink(count);
                        existing.grow(count);
                        slot.setChanged();
                        changed = true;
                    }
                }
                index += reverse ? -1 : 1;
            }
        }
        if (!stack.isEmpty()) {
            index = reverse ? end - 1 : start;
            while (reverse ? index >= start : index < end) {
                var slot = slots.get(index);
                if (!slot.hasItem() && slot.mayPlace(stack)) {
                    slot.setByPlayer(stack.split(Math.min(stack.getCount(), slot.getMaxStackSize(stack))));
                    slot.setChanged();
                    changed = true;
                    break;
                }
                index += reverse ? -1 : 1;
            }
        }
        return changed;
    }
}
