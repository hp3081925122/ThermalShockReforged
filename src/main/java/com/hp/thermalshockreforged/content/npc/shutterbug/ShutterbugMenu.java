package com.hp.thermalshockreforged.content.npc.shutterbug;

import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.*;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public final class ShutterbugMenu extends AbstractContainerMenu {
    public final ShutterbugPage page;
    public final UUID npcUuid;
    public final int intro;
    private final int npcId;
    private final BlockPos anchor;
    private final Player owner;
    private final Level level;
    private boolean changedPage;
    public ShutterbugMenu(ShutterbugPage page, int id, Inventory inventory, int npcId, UUID npcUuid, BlockPos anchor, int intro) {
        super(ShutterbugContent.menu(page), id); this.page = page; this.npcId = npcId; this.npcUuid = npcUuid; this.anchor = anchor.immutable(); this.intro = intro; owner = inventory.player; level = owner.level();
        if (page.trade != null) {
            for (int row = 0; row < 3; row++) for (int column = 0; column < 9; column++) addSlot(new Slot(inventory, column + row * 9 + 9, 23 + column * 18, 99 + row * 18));
            for (int column = 0; column < 9; column++) addSlot(new Slot(inventory, column, 23 + column * 18, 157));
        }
    }
    private ShutterbugEntity npc() { return level.getEntity(npcId) instanceof ShutterbugEntity npc && npc.getUUID().equals(npcUuid) ? npc : null; }
    @Override public boolean stillValid(Player player) {
        var npc = npc();
        return player == owner && player.level() == level && player.isAlive() && npc != null && npc.isAlive() && !npc.isRemoved() && player.distanceToSqr(npc) <= 64;
    }
    @Override public boolean clickMenuButton(Player player, int button) {
        if (changedPage || !(player instanceof ServerPlayer server) || player.containerMenu != this || !stillValid(player)) return false;
        if (page == ShutterbugPage.TRADE) { if (button != 0) return false; com.hp.thermalshockreforged.content.npc.NpcTrade.buy(server, anchor, page.trade); return true; }
        if (page == ShutterbugPage.MAIN) {
            if (button < 0 || button > 3) return false;
            changedPage = true;
            if (button == 2) ShutterbugQuest.openErrand(server, npc(), anchor);
            else ShutterbugContent.open(server, npc(), anchor, button == 0 ? ShutterbugPage.INFO : button == 1 ? ShutterbugPage.TRADE : ShutterbugPage.randomTip(server.getRandom()));
            return true;
        }
        if (page == ShutterbugPage.INTRO_6) { if (button != 0) return false; changedPage = true; server.closeContainer(); return true; }
        if (page.tip()) { if (button != 0) return false; changedPage = true; ShutterbugContent.open(server, npc(), anchor, ShutterbugPage.randomTip(server.getRandom())); return true; }
        ShutterbugPage next = page.next(button);
        if (next == null) return false;
        changedPage = true;
        if (page == ShutterbugPage.INTRO_5 && ShutterbugQuest.introduced(server)) next = ShutterbugPage.MAIN;
        ShutterbugContent.open(server, npc(), anchor, next);
        if (page == ShutterbugPage.INTRO_5 && next == ShutterbugPage.INTRO_6) ShutterbugQuest.finishIntroduction(server);
        if (page == ShutterbugPage.ERRAND_B && ShutterbugQuest.grantNew(server, "leaf_it_to_me_1")) com.hp.thermalshockreforged.world.ErrandMaps.give(server, "cavebloomer_field");
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
