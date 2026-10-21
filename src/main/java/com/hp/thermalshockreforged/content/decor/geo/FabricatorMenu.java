package com.hp.thermalshockreforged.content.decor.geo;

import com.hp.thermalshockreforged.ThermalShockReforged;
import com.hp.thermalshockreforged.content.decor.nativeblocks.NativeDecorContent;
import com.hp.thermalshockreforged.content.fabrication.FabricationRecipe;
import com.hp.thermalshockreforged.content.fabrication.FabricatorPage;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public final class FabricatorMenu extends AbstractContainerMenu {
    public static final int SELECT_BUTTON = 100;
    public static final int OUTPUT_X = 220, OUTPUT_Y = 44, ENERGY_X = 131, ENERGY_Y = 84, INVENTORY_X = 48, INVENTORY_Y = 108;
    private final DataSlot selected = DataSlot.standalone();
    private final Level level;
    private final BlockPos pos;
    private final Player owner;
    private final Inventory inventory;
    private final GeoDecorBlockEntity attached;
    private final Container storage;

    public FabricatorMenu(int id, Inventory inventory, RegistryFriendlyByteBuf data) {
        this(id, inventory, data.readBlockPos(), FabricatorPage.byIndex(data.readVarInt()));
    }

    private FabricatorMenu(int id, Inventory inventory, BlockPos pos, FabricatorPage page) {
        super(NativeDecorContent.FABRICATOR_MENU.get(), id);
        this.inventory = inventory;
        this.level = inventory.player.level();
        this.pos = pos.immutable();
        this.owner = inventory.player;
        selected.set(page.ordinal());
        addDataSlot(selected);
        attached = level.hasChunkAt(pos) && level.getBlockEntity(pos) instanceof GeoDecorBlockEntity entity && entity.kind == GeoDecorKind.FABRICATOR ? entity : null;
        storage = !level.isClientSide && attached != null ? attached : new SimpleContainer(9);
        addSlot(new Slot(storage, 0, OUTPUT_X, OUTPUT_Y) {
            @Override public boolean mayPlace(ItemStack stack) { return false; }
        });
        addSlot(new Slot(storage, 1, ENERGY_X, ENERGY_Y));
        for (int row = 0; row < 3; row++) for (int column = 0; column < 9; column++) {
            addSlot(new Slot(inventory, column + (row + 1) * 9, INVENTORY_X + column * 18, INVENTORY_Y + row * 18));
        }
        for (int column = 0; column < 9; column++) addSlot(new Slot(inventory, column, INVENTORY_X + column * 18, INVENTORY_Y + 58));
    }

    public FabricatorPage page() {
        int index = selected.get();
        if (index < 0 || index >= FabricatorPage.values().length) return FabricatorPage.TOOLS;
        return FabricatorPage.values()[index];
    }

    public void select(FabricatorPage page) { selected.set(page.ordinal()); }

    public Inventory inventory() { return inventory; }

    public static boolean open(ServerPlayer player, BlockPos pos, FabricatorPage page) {
        var level = player.serverLevel();
        if (!player.isAlive() || !level.hasChunkAt(pos) || player.distanceToSqr(pos.getCenter()) > 64
                || !level.getBlockState(pos).is(GeoDecorContent.block(GeoDecorKind.FABRICATOR))
                || !(level.getBlockEntity(pos) instanceof GeoDecorBlockEntity entity) || entity.kind != GeoDecorKind.FABRICATOR) return false;
        var fixed = pos.immutable();
        return player.openMenu(new SimpleMenuProvider((id, inventory, owner) -> new FabricatorMenu(id, inventory, fixed, page),
                Component.translatable("block.thermal_shock_reforged.fabricator")), buffer -> {
            buffer.writeBlockPos(fixed);
            buffer.writeVarInt(page.ordinal());
        }).isPresent();
    }

    @Override public boolean stillValid(Player player) {
        return player == owner && player.isAlive() && player.level() == level && level.hasChunkAt(pos)
                && player.distanceToSqr(pos.getCenter()) <= 64 && level.getBlockState(pos).is(GeoDecorContent.block(GeoDecorKind.FABRICATOR))
                && (level.isClientSide || attached != null && !attached.isRemoved() && level.getBlockEntity(pos) == attached);
    }

    @Override public boolean clickMenuButton(Player player, int button) {
        if (!(player instanceof ServerPlayer server) || player.containerMenu != this || !stillValid(player)) return false;
        if (button >= SELECT_BUTTON && button - SELECT_BUTTON < FabricatorPage.values().length) {
            selected.set(button - SELECT_BUTTON);
            return true;
        }
        return button == 0 && !page().isGuide() && fabricate(server);
    }

    private boolean fabricate(ServerPlayer player) {
        var page = page();
        var holder = level.getRecipeManager().byKey(page.recipe).orElse(null);
        if (holder == null || !(holder.value() instanceof FabricationRecipe recipe)) {
            ThermalShockReforged.LOGGER.debug("Fabrication recipe unavailable: recipe={}, player={}, position={}", page.recipe, player.getUUID(), pos);
            return false;
        }
        var input = new FabricationRecipe.Input(inventory.items, storage.getItem(1));
        var plan = recipe.removalPlan(input);
        if (plan == null) return false;
        var result = recipe.assemble(input, level.registryAccess());
        var previous = storage.getItem(0);
        int existing = previous.isEmpty() ? 0 : previous.getCount();
        if (result.isEmpty() || !previous.isEmpty() && !ItemStack.isSameItemSameComponents(previous, result)
                || result.getCount() > storage.getMaxStackSize(result) - existing) return false;
        result.grow(existing);
        for (int slot = 0; slot < plan.length; slot++) if (plan[slot] > 0) inventory.removeItem(slot, plan[slot]);
        storage.removeItem(1, recipe.energyCount());
        storage.setItem(0, result);
        storage.setChanged();
        inventory.setChanged();
        broadcastChanges();
        level.playSound(null, pos, SoundEvents.CONDUIT_ACTIVATE, SoundSource.NEUTRAL, 1, 2);
        level.playSound(null, pos, SoundEvents.EXPERIENCE_ORB_PICKUP, SoundSource.NEUTRAL, 1, 1);
        ThermalShockReforged.LOGGER.debug("Fabrication completed: recipe={}, player={}, position={}", page.recipe, player.getUUID(), pos);
        return true;
    }

    @Override public void clicked(int slot, int button, ClickType type, Player player) {
        if (stillValid(player)) super.clicked(slot, button, type, player);
    }

    @Override public ItemStack quickMoveStack(Player player, int index) {
        if (!stillValid(player) || index < 0 || index >= slots.size()) return ItemStack.EMPTY;
        var slot = slots.get(index);
        if (!slot.hasItem() || !slot.mayPickup(player)) return ItemStack.EMPTY;
        var stack = slot.getItem();
        var original = stack.copy();
        if (index < 2) {
            if (!moveItemStackTo(stack, 2, slots.size(), true)) return ItemStack.EMPTY;
            slot.onQuickCraft(stack, original);
        } else if (!moveItemStackTo(stack, 1, 2, false)) {
            if (index < 29 ? !moveItemStackTo(stack, 29, slots.size(), true) : !moveItemStackTo(stack, 2, 29, false)) return ItemStack.EMPTY;
        }
        if (stack.isEmpty()) slot.set(ItemStack.EMPTY); else slot.setChanged();
        if (stack.getCount() == original.getCount()) return ItemStack.EMPTY;
        slot.onTake(player, stack);
        return original;
    }
}
