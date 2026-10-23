package com.hp.thermalshockreforged.content.npc.concierge;

import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public final class ConciergeMenu extends AbstractContainerMenu {
    public final ConciergePage page;
    public final UUID npcUuid;
    public final BlockPos anchor;
    public final int intro;
    private final int npcId;
    private final Player owner;
    private final Level level;
    private final DataSlot unlocked = DataSlot.standalone();
    private boolean changedPage;
    private boolean transaction;
    public ConciergeMenu(ConciergePage page, int id, Inventory inventory, int npcId, UUID npcUuid, BlockPos anchor, int intro) {
        super(ConciergeContent.menu(page), id); this.page = page; this.npcId = npcId; this.npcUuid = npcUuid; this.anchor = anchor.immutable(); this.intro = intro; owner = inventory.player; level = owner.level();
        addDataSlot(unlocked); if (owner instanceof ServerPlayer server) unlocked.set(ConciergeQuest.vilebath(server) ? 1 : 0);
        if (page.trade != null) {
            for (int row = 0; row < 3; row++) for (int column = 0; column < 9; column++) addSlot(new Slot(inventory, column + row * 9 + 9, 23 + column * 18, 99 + row * 18));
            for (int column = 0; column < 9; column++) addSlot(new Slot(inventory, column, 23 + column * 18, 157));
        }
    }
    public boolean vilebathAllowed() { return unlocked.get() != 0; }
    @Override public void broadcastChanges() { if (owner instanceof ServerPlayer server) unlocked.set(ConciergeQuest.vilebath(server) ? 1 : 0); super.broadcastChanges(); }
    private ConciergeEntity npc() { return level.getEntity(npcId) instanceof ConciergeEntity npc && npc.getUUID().equals(npcUuid) ? npc : null; }
    @Override public boolean stillValid(Player player) {
        var npc = npc();
        return player == owner && player.level() == level && player.isAlive() && npc != null && npc.isAlive() && !npc.isRemoved() && player.distanceToSqr(npc) <= 64;
    }
    @Override public boolean clickMenuButton(Player player, int button) {
        if (changedPage || transaction || !(player instanceof ServerPlayer server) || player.containerMenu != this || !stillValid(player)) return false;
        if (page.trade != null && button == 0) {
            transaction = true;
            try { return ConciergeTrade.buy(server, page.trade); } finally { transaction = false; }
        }
        ConciergePage next = null;
        switch (page) {
            case MAIN -> {
                if (button == 0) next = ConciergePage.INFO;
                if (button == 1) {
                    next = server.getPersistentData().getBooleanOr(ConciergeQuest.INTRO, false) ? ConciergePage.WIDE : ConciergePage.TRADE_INTRO;
                    server.getPersistentData().putBoolean(ConciergeQuest.INTRO, true);
                }
                if (button == 2) { if (!ConciergeQuest.award(server, "errands_get")) return false; next = ConciergeQuest.completed(server) ? ConciergePage.NONE : ConciergePage.ERRAND_1; }
            }
            case INFO -> {
                next = switch (button) { case 0 -> ConciergePage.INFECTION; case 1 -> ConciergePage.BLIZZARD; case 2 -> ConciergePage.SELF; case 3 -> ConciergeQuest.vilebath(server) ? ConciergePage.VILEBATH : null; case 4 -> ConciergePage.FLORIST; default -> null; };
            }
            case INFECTION, BLIZZARD, SELF, VILEBATH, FLORIST -> { if (button == 0) next = ConciergePage.INFO; }
            case TRADE_INTRO -> { if (button == 0) next = ConciergePage.LILIES; }
            case ERRAND_1 -> { if (button == 0) next = ConciergePage.ERRAND_2; }
            case ERRAND_2 -> { if (button == 0) { boolean fresh = !ConciergeQuest.has(server, "blasphemy_1"); if (!ConciergeQuest.award(server, "blasphemy_1")) return false; if (fresh) com.hp.thermalshockreforged.world.ErrandMaps.give(server, "vilebath_castle"); next = ConciergePage.ERRAND_3; } }
            case ERRAND_3, NONE, FINISH -> {
                if (button == 0) {
                    transaction = true;
                    try { next = ConciergeQuest.finish(server, anchor) ? ConciergePage.FINISH : ConciergePage.MAIN; } finally { transaction = false; }
                }
            }
            case WIDE -> { if (button == 1) next = ConciergePage.LILIES; if (button == 2) next = ConciergeQuest.completed(server) ? ConciergePage.PANACEA : ConciergePage.SPECIAL; }
            case LILIES -> { if (button == 1) next = ConciergePage.LARGE_LILY; if (button == 2) next = ConciergePage.WIDE; }
            case LARGE_LILY -> { if (button == 1) next = ConciergePage.LIGHT; if (button == 2) next = ConciergePage.LILIES; }
            case LIGHT -> { if (button == 1) next = ConciergePage.HEAVY; if (button == 2) next = ConciergePage.LARGE_LILY; }
            case HEAVY -> { if (button == 1) next = ConciergePage.SPECIAL; if (button == 2) next = ConciergePage.LIGHT; }
            case SPECIAL -> { if (button == 1) next = ConciergeQuest.completed(server) ? ConciergePage.PANACEA : ConciergePage.WIDE; if (button == 2) next = ConciergePage.HEAVY; }
            case PANACEA -> { if (button == 1) next = ConciergePage.WIDE; if (button == 2) next = ConciergePage.SPECIAL; }
        }
        if (next == null) return false;
        changedPage = true;
        boolean opened = ConciergeContent.open(server, npc(), anchor, next);
        if (!opened) changedPage = false;
        return opened;
    }
    @Override public ItemStack quickMoveStack(Player player, int index) {
        if (page.trade == null || player != owner || index < 0 || index >= slots.size()) return ItemStack.EMPTY;
        Slot slot = slots.get(index);
        if (slot.hasItem()) {
            ItemStack stack = slot.getItem();
            if (index < 27) moveItemStackTo(stack, 27, 36, true); else moveItemStackTo(stack, 0, 27, false);
            if (stack.isEmpty()) slot.setByPlayer(ItemStack.EMPTY); else slot.setChanged();
        }
        return ItemStack.EMPTY;
    }
}
