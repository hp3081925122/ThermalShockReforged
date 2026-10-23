package com.hp.thermalshockreforged.content.remaininggui;

import com.hp.thermalshockreforged.ThermalShockReforged;
import com.hp.thermalshockreforged.content.remainingcombat.RemainingCombatContent;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.items.ItemHandlerHelper;

public final class RemainingGuiMenu extends AbstractContainerMenu implements Supplier<Map<Integer, Slot>> {
    public final RemainingGuiPage page;
    public final BlockPos anchor;
    private final Player owner;
    private final Level level;
    private final Map<Integer, Slot> customSlots = new HashMap<>();
    private Supplier<Boolean> boundItemMatcher;
    private Entity boundEntity;
    private BlockEntity boundBlockEntity;
    private boolean bound;
    private boolean closed;

    public RemainingGuiMenu(RemainingGuiPage page, int id, Inventory inventory, FriendlyByteBuf data) {
        super(RemainingGuiContent.type(page), id);
        this.page = page;
        owner = inventory.player;
        level = owner.level();
        anchor = data == null ? BlockPos.ZERO : data.readBlockPos().immutable();
        if (page == RemainingGuiPage.CHAR_TRADE_TEMPLATE) {
            if (data != null) bind(data);
            for (int row = 0; row < 3; row++) for (int col = 0; col < 9; col++) addSlot(new Slot(inventory, col + (row + 1) * 9, 23 + col * 18, 99 + row * 18));
            for (int col = 0; col < 9; col++) addSlot(new Slot(inventory, col, 23 + col * 18, 157));
        }
    }

    private void bind(FriendlyByteBuf data) {
        if (data.readableBytes() == 1) {
            byte hand = data.readByte();
            var stack = hand == 0 ? owner.getMainHandItem() : owner.getOffhandItem();
            boundItemMatcher = () -> stack == (hand == 0 ? owner.getMainHandItem() : owner.getOffhandItem());
            bound = stack.getCapability(Capabilities.ItemHandler.ITEM) != null;
        } else if (data.readableBytes() > 1) {
            data.readByte();
            boundEntity = level.getEntity(data.readVarInt());
            bound = boundEntity != null && boundEntity.getCapability(Capabilities.ItemHandler.ENTITY) != null;
        } else {
            boundBlockEntity = level.getBlockEntity(anchor);
            bound = boundBlockEntity != null && level.getCapability(Capabilities.ItemHandler.BLOCK, anchor, null) != null;
        }
    }

    @Override public boolean stillValid(Player player) {
        if (closed || player != owner || player.level() != level || !player.isAlive()) return false;
        if (bound) {
            if (boundItemMatcher != null) return boundItemMatcher.get();
            if (boundBlockEntity != null) return AbstractContainerMenu.stillValid(ContainerLevelAccess.create(level, anchor), player, boundBlockEntity.getBlockState().getBlock());
            if (boundEntity != null) return boundEntity.isAlive();
        }
        return true;
    }

    @Override public Map<Integer, Slot> get() { return customSlots; }

    @Override public boolean clickMenuButton(Player player, int button) {
        if (page != RemainingGuiPage.CHAR_TRADE_TEMPLATE || button != 0 || !(player instanceof ServerPlayer server)
                || player.containerMenu != this || player.isSpectator() || !stillValid(player) || !level.hasChunkAt(anchor)) return false;
        for (int slot = 0; slot < 3; slot++) {
            if (customSlots.get(slot) == null) {
                ThermalShockReforged.LOGGER.debug("Legacy trade template rejected missing custom slots: player={}, position={}", player.getUUID(), anchor);
                return false;
            }
        }
        boolean any = false;
        for (int slot = 0; slot < 3; slot++) any |= !customSlots.get(slot).getItem().isEmpty();
        if (any) {
            var advancement = server.getServer().getAdvancements().get(RemainingGuiContent.id("whisper_advancement"));
            if (advancement != null) {
                var progress = server.getAdvancements().getOrStartProgress(advancement);
                if (!progress.isDone()) for (String criterion : progress.getRemainingCriteria()) server.getAdvancements().award(advancement, criterion);
            }
        }
        for (int slot = 0; slot < 3; slot++) {
            server.giveExperiencePoints(customSlots.get(slot).getItem().getCount() * (2 << slot));
            customSlots.get(slot).set(ItemStack.EMPTY);
            broadcastChanges();
        }
        return true;
    }

    public boolean submitVaultCode(Player player, String code) {
        if (page != RemainingGuiPage.LIMBO_VAULT_GUI || !(player instanceof ServerPlayer server)
                || player.containerMenu != this || player.isSpectator() || !stillValid(player) || !level.hasChunkAt(anchor)
                || code.length() > Short.MAX_VALUE || !code.equals("DumbBug")) return false;
        ItemHandlerHelper.giveItemToPlayer(server, new ItemStack(RemainingCombatContent.ANTENNAE.get(), 1));
        level.playSound(null, anchor, SoundEvents.EXPERIENCE_ORB_PICKUP, SoundSource.NEUTRAL, 1, 1);
        ThermalShockReforged.LOGGER.debug("Limbo vault reward committed: player={}, position={}", player.getUUID(), anchor);
        return true;
    }

    @Override public ItemStack quickMoveStack(Player player, int index) {
        if (page != RemainingGuiPage.CHAR_TRADE_TEMPLATE || index < 0 || index >= slots.size() || !stillValid(player)) return ItemStack.EMPTY;
        var slot = slots.get(index);
        if (!slot.hasItem()) return ItemStack.EMPTY;
        var stack = slot.getItem();
        if (index < 27) moveItemStackTo(stack, 27, 36, true);
        else moveItemStackTo(stack, 0, 27, false);
        return ItemStack.EMPTY;
    }

    @Override public void removed(Player player) { super.removed(player); closed = true; }
}
