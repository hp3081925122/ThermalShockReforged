package com.hp.thermalshockreforged.content.npc.huntress;

import java.util.UUID;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.*;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public final class HuntressMenu extends AbstractContainerMenu {
    public final HuntressPage page;
    public final UUID npcUuid;
    public final int intro;
    private final int npcId;
    private final Player owner;
    private final Level level;
    private boolean changedPage;
    public HuntressMenu(HuntressPage page, int id, Inventory inventory, int npcId, UUID npcUuid, int intro) { super(HuntressMenus.menu(page), id); this.page = page; this.npcId = npcId; this.npcUuid = npcUuid; this.intro = intro; owner = inventory.player; level = owner.level(); }
    private HuntressEntity npc() { return level.getEntity(npcId) instanceof HuntressEntity npc && npc.getUUID().equals(npcUuid) ? npc : null; }
    @Override public boolean stillValid(Player player) { var npc = npc(); return player == owner && player.level() == level && player.isAlive() && npc != null && npc.isAlive() && !npc.isRemoved() && player.distanceToSqr(npc) <= 64; }
    @Override public boolean clickMenuButton(Player player, int button) {
        if (changedPage || page == HuntressPage.MAIN || button != 0 || !(player instanceof ServerPlayer server) || player.containerMenu != this || !stillValid(player)) return false;
        changedPage = true;
        boolean opened = HuntressMenus.open(server, npc(), HuntressPage.MAIN);
        if (!opened) changedPage = false;
        return opened;
    }
    @Override public ItemStack quickMoveStack(Player player, int index) { return ItemStack.EMPTY; }
}
