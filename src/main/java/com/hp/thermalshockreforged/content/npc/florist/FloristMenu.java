package com.hp.thermalshockreforged.content.npc.florist;

import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.*;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.inventory.Slot;
import net.minecraft.resources.Identifier;
import com.hp.thermalshockreforged.ThermalShockReforged;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public final class FloristMenu extends AbstractContainerMenu {
    public final FloristPage page;
    public final UUID npcUuid;
    public final int intro;
    private final int npcId;
    private final BlockPos anchor;
    private final Player owner;
    private final Level level;
    private boolean changedPage;
    private final DataSlot concierge = DataSlot.standalone();
    public FloristMenu(FloristPage page, int id, Inventory inventory, int npcId, UUID npcUuid, BlockPos anchor, int intro) {
        super(FloristContent.menu(page), id); this.page = page; this.npcId = npcId; this.npcUuid = npcUuid; this.anchor = anchor.immutable(); this.intro = intro; owner = inventory.player; level = owner.level(); addDataSlot(concierge); if (owner instanceof ServerPlayer server && page == FloristPage.INFO) concierge.set(unlocked(server) ? 1 : 0);
        if (page.trade != null) {
            for (int row = 0; row < 3; row++) for (int column = 0; column < 9; column++) addSlot(new Slot(inventory, column + row * 9 + 9, 23 + column * 18, 99 + row * 18));
            for (int column = 0; column < 9; column++) addSlot(new Slot(inventory, column, 23 + column * 18, 157));
        }
    }
    private static boolean unlocked(ServerPlayer player) {
        var advancement = player.level().getServer().getAdvancements().get(Identifier.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, "blasphemy_1"));
        return advancement != null && player.getAdvancements().getOrStartProgress(advancement).isDone();
    }
    public boolean conciergeAllowed() { return concierge.get() != 0; }
    @Override public void broadcastChanges() { if (owner instanceof ServerPlayer server && page == FloristPage.INFO) concierge.set(unlocked(server) ? 1 : 0); super.broadcastChanges(); }
    private FloristEntity npc() { return level.getEntity(npcId) instanceof FloristEntity npc && npc.getUUID().equals(npcUuid) ? npc : null; }
    @Override public boolean stillValid(Player player) {
        var npc = npc();
        return player == owner && player.level() == level && player.isAlive() && npc != null && npc.isAlive() && !npc.isRemoved() && player.distanceToSqr(npc) <= 64;
    }
    @Override public boolean clickMenuButton(Player player, int button) {
        if (changedPage || !(player instanceof ServerPlayer server) || player.containerMenu != this || !stillValid(player)) return false;
        if (page.trade != null && button == 0) { com.hp.thermalshockreforged.content.npc.NpcTrade.buy(server, anchor, page.trade); return true; }
        if (page == FloristPage.INFO && button == 4 && !unlocked(server)) return false;
        FloristPage next = page.target(button);
        if (next == null) return false;
        changedPage = true;
        FloristContent.open(server, npc(), anchor, next);
        return true;
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
