package com.hp.thermalshockreforged.entity.whisper;

import com.hp.thermalshockreforged.ThermalShockReforged;
import com.hp.thermalshockreforged.content.recipe.NpcTradeRecipe;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.*;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.*;

public final class WhisperMenu extends AbstractContainerMenu {
    public final WhisperPage page;
    public final int npcId, intro;
    public final UUID npcUuid;
    public final BlockPos anchor;
    private final Player owner;
    private final net.minecraft.world.level.Level level;
    private boolean changedPage, closed;
    public WhisperMenu(WhisperPage page, int id, Inventory inventory, int npcId, UUID npcUuid, BlockPos anchor, int intro) {
        super(WhisperContent.menu(page), id);
        this.page = page; this.npcId = npcId; this.npcUuid = npcUuid; this.anchor = anchor.immutable(); this.intro = intro; owner = inventory.player; level = owner.level();
        if (page.inventory) {
            for (int row = 0; row < 3; row++) for (int col = 0; col < 9; col++) addSlot(new Slot(inventory, col + (row + 1) * 9, 23 + col * 18, 99 + row * 18));
            for (int col = 0; col < 9; col++) addSlot(new Slot(inventory, col, 23 + col * 18, 157));
        }
    }
    private WhisperEntity npc() { return level.getEntity(npcId) instanceof WhisperEntity npc && npc.getUUID().equals(npcUuid) ? npc : null; }
    @Override public boolean stillValid(Player player) {
        var npc = npc();
        return !closed && player == owner && player.level() == level && player.isAlive() && npc != null && npc.isAlive() && !npc.isRemoved() && player.distanceToSqr(npc) <= 64;
    }
    @Override public boolean clickMenuButton(Player player, int action) {
        if (changedPage || !(player instanceof ServerPlayer server) || server.containerMenu != this || !stillValid(player)) return false;
        var button = page.buttons.stream().filter(b -> b.action() == action).findFirst();
        if (button.isEmpty()) return false;
        if (button.get().route().isEmpty()) return page.inventory && action == 0 && buy(server);
        changedPage = true;
        if (WhisperContent.open(server, npc(), WhisperPage.valueOf(button.get().route()))) return true;
        changedPage = false;
        return false;
    }
    private boolean buy(ServerPlayer player) {
        var recipe = NpcTradeRecipe.get(player.level(), page.id);
        if (recipe == null || !recipe.allowed(player) || !recipe.trade(player)) return false;
        broadcastChanges();
        ThermalShockReforged.LOGGER.debug("Whisper trade committed: player={}, page={}", player.getUUID(), page.id);
        return true;
    }
    @Override public ItemStack quickMoveStack(Player player, int index) {
        if (!page.inventory || index < 0 || index >= slots.size() || !stillValid(player)) return ItemStack.EMPTY;
        var slot = slots.get(index);
        if (!slot.hasItem()) return ItemStack.EMPTY;
        var stack = slot.getItem(); var original = stack.copy();
        if (index < 27 ? !moveItemStackTo(stack, 27, 36, true) : !moveItemStackTo(stack, 0, 27, false)) return ItemStack.EMPTY;
        if (stack.isEmpty()) slot.setByPlayer(ItemStack.EMPTY); else slot.setChanged();
        if (stack.getCount() == original.getCount()) return ItemStack.EMPTY;
        slot.onTake(player, stack);
        ThermalShockReforged.LOGGER.debug("Whisper quick move committed: player={}, page={}, sourceSlot={}, reverseDestination={}", player.getUUID(), page.id, index, index < 27);
        return original;
    }
    @Override public void removed(Player player) { super.removed(player); closed = true; }
}
