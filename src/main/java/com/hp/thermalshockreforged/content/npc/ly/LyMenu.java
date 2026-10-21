package com.hp.thermalshockreforged.content.npc.ly;

import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.*;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public final class LyMenu extends AbstractContainerMenu {
    public final LyPage page;
    public final UUID npcUuid;
    public final int intro;
    private final int npcId;
    private final BlockPos anchor;
    private final Player owner;
    private final Level level;
    private boolean changedPage;
    public LyMenu(LyPage page, int id, Inventory inventory, int npcId, UUID npcUuid, BlockPos anchor, int intro) {
        super(LyContent.menu(page), id); this.page = page; this.npcId = npcId; this.npcUuid = npcUuid; this.anchor = anchor.immutable(); this.intro = intro; owner = inventory.player; level = owner.level();
        if (page.trade != null) {
            for (int row = 0; row < 3; row++) for (int column = 0; column < 9; column++) addSlot(new Slot(inventory, column + row * 9 + 9, 23 + column * 18, 99 + row * 18));
            for (int column = 0; column < 9; column++) addSlot(new Slot(inventory, column, 23 + column * 18, 157));
        }
    }
    private LyEntity npc() { return level.getEntity(npcId) instanceof LyEntity npc && npc.getUUID().equals(npcUuid) ? npc : null; }
    @Override public boolean stillValid(Player player) {
        var npc = npc();
        return player == owner && player.level() == level && player.isAlive() && npc != null && npc.isAlive() && !npc.isRemoved() && player.distanceToSqr(npc) <= 64;
    }
    @Override public boolean clickMenuButton(Player player, int button) {
        if (changedPage || !(player instanceof ServerPlayer server) || player.containerMenu != this || !stillValid(player)) return false;
        if (page.trade != null && button == 0) { com.hp.thermalshockreforged.content.npc.NpcTrade.buy(server, anchor, page.trade); return true; }
        LyPage next = page.target(button);
        if (next == null) return false;
        changedPage = true;
        boolean opened = LyContent.open(server, npc(), anchor, next);
        if (!opened) changedPage = false;
        return opened;
    }
    @Override public ItemStack quickMoveStack(Player player, int index) {
        if (page.trade == null || player != owner || index < 0 || index >= slots.size()) return ItemStack.EMPTY;
        Slot slot = slots.get(index);
        if (slot.hasItem()) {
            ItemStack stack = slot.getItem();
            if (index < 27) moveItemStackTo(stack, 27, 36, true);
            else moveItemStackTo(stack, 0, 27, false);
            if (stack.isEmpty()) slot.setByPlayer(ItemStack.EMPTY);
            else slot.setChanged();
        }
        return ItemStack.EMPTY;
    }
}
