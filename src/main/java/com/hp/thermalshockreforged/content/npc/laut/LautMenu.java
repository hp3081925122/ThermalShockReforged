package com.hp.thermalshockreforged.content.npc.laut;

import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.*;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public final class LautMenu extends AbstractContainerMenu {
    public final LautPage page;
    public final UUID npcUuid;
    private final int npcId;
    private final BlockPos anchor;
    private final Player owner;
    private final Level level;
    private boolean committed;
    public LautMenu(LautPage page, int id, Inventory inventory, int npcId, UUID npcUuid, BlockPos anchor) {
        super(LautContent.menu(page), id); this.page = page; this.npcId = npcId; this.npcUuid = npcUuid; this.anchor = anchor.immutable(); owner = inventory.player; level = owner.level();
    }
    private LautEntity npc() { return level.getEntity(npcId) instanceof LautEntity npc && npc.getUUID().equals(npcUuid) ? npc : null; }
    @Override public boolean stillValid(Player player) {
        var npc = npc();
        return player == owner && player.level() == level && player.isAlive() && npc != null && npc.isAlive() && !npc.isRemoved() && player.distanceToSqr(npc) <= 64 && (page.terminal() || !npc.completed());
    }
    @Override public boolean clickMenuButton(Player player, int button) {
        if (committed || !(player instanceof ServerPlayer server) || player.containerMenu != this || !stillValid(player)) return false;
        var npc = npc();
        if (page == LautPage.HAVE && button == 0) {
            committed = true;
            LautContent.open(server, npc, anchor, LautQuest.available(server) >= 3 ? LautPage.CONFIRM : LautPage.NEED);
            return true;
        }
        if (page != LautPage.CONFIRM || button < 0 || button > 1) return false;
        committed = true;
        if (button == 1) { LautContent.open(server, npc, anchor, LautPage.REJECT); return true; }
        switch (LautQuest.accept(server, npc, anchor)) {
            case ACCEPTED -> LautContent.open(server, npc, anchor, LautPage.ACCEPT);
            case INSUFFICIENT -> LautContent.open(server, npc, anchor, LautPage.NEED);
            default -> server.closeContainer();
        }
        return true;
    }
    @Override public ItemStack quickMoveStack(Player player, int slot) { return ItemStack.EMPTY; }
}
