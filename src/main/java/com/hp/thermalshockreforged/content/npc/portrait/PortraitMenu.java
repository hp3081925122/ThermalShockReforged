package com.hp.thermalshockreforged.content.npc.portrait;

import java.util.UUID;
import net.minecraft.world.entity.player.*;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public final class PortraitMenu extends AbstractContainerMenu {
    public final boolean ibex;
    private final int npcId;
    private final UUID npcUuid;
    private final Player owner;
    private final Level level;
    public PortraitMenu(boolean ibex, int id, Inventory inventory, int npcId, UUID npcUuid) { super(PortraitContent.menu(ibex), id); this.ibex = ibex; this.npcId = npcId; this.npcUuid = npcUuid; owner = inventory.player; level = owner.level(); }
    @Override public boolean stillValid(Player player) {
        return player == owner && player.level() == level && player.isAlive()
                && level.getEntity(npcId) instanceof PortraitCharacterEntity npc && npc.getUUID().equals(npcUuid)
                && npc.isAlive() && !npc.isRemoved() && player.distanceToSqr(npc) <= 64
                && npc.texture().equals(ibex ? "portrait_ibex" : "portrait_default");
    }
    @Override public ItemStack quickMoveStack(Player player, int slot) { return ItemStack.EMPTY; }
}
