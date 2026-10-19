package com.hp.thermalshockreforged.entity.whisper;

import com.hp.thermalshockreforged.ThermalShockReforged;
import com.hp.thermalshockreforged.content.MaterialContent;
import com.hp.thermalshockreforged.content.decor.nativeblocks.NativeDecorContent;
import com.hp.thermalshockreforged.content.growth.GrowthContent;
import com.hp.thermalshockreforged.content.music.MusicContent;
import com.hp.thermalshockreforged.content.status.StatusContent;
import com.hp.thermalshockreforged.content.utility.UtilityContent;
import com.hp.thermalshockreforged.entity.ambient.AmbientContent;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.*;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.*;
import net.neoforged.neoforge.items.ItemHandlerHelper;

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
        int[] costs = switch (page) {
            case MALACHITE, BISMUTH -> new int[]{10, 0, 20, 10};
            case STARFRUIT, LUNALEMON -> new int[]{3, 0, 6, 1};
            case COCOA -> new int[]{0, 0, 5, 1};
            case PIXIE -> new int[]{7, 0, 10, 1};
            case ZENITH -> new int[]{10, 1, 30, 1};
            case DISC -> new int[]{0, 0, 33, 1};
            default -> throw new IllegalStateException();
        };
        Item red = UtilityContent.ADAMANTINE_SPICE.get(), purple = NativeDecorContent.TOURMALINE_SPICE.get();
        if (player.experienceLevel < costs[2] || count(player, red) < costs[0] || count(player, purple) < costs[1]) return false;
        Item reward = switch (page) {
            case MALACHITE -> MaterialContent.items().get("malachite").get();
            case BISMUTH -> MaterialContent.items().get("bismuth").get();
            case STARFRUIT -> GrowthContent.STARFRUIT_SEED.get().asItem();
            case LUNALEMON -> GrowthContent.LUNALEMON_SEED.get().asItem();
            case COCOA -> StatusContent.COCOA.get();
            case PIXIE -> AmbientContent.BOTTLED_PIXIE.get();
            case ZENITH -> MaterialContent.items().get("zenith_smithing_template").get();
            case DISC -> MusicContent.FLOOR_33.get();
            default -> throw new IllegalStateException();
        };
        if (costs[0] > 0) player.getInventory().clearOrCountMatchingItems(stack -> stack.is(red), costs[0], player.inventoryMenu.getCraftSlots());
        if (costs[1] > 0) player.getInventory().clearOrCountMatchingItems(stack -> stack.is(purple), costs[1], player.inventoryMenu.getCraftSlots());
        player.giveExperienceLevels(-costs[2]);
        ItemHandlerHelper.giveItemToPlayer(player, new ItemStack(reward, costs[3]));
        broadcastChanges();
        ThermalShockReforged.LOGGER.debug("Whisper trade committed: player={}, page={}", player.getUUID(), page.id);
        return true;
    }
    private static int count(Player player, Item item) {
        int total = 0;
        for (int slot = 0; slot < player.getInventory().getContainerSize(); slot++) { var stack = player.getInventory().getItem(slot); if (stack.is(item)) total += stack.getCount(); }
        return total;
    }
    @Override public ItemStack quickMoveStack(Player player, int index) {
        if (!page.inventory || index < 0 || index >= slots.size() || !stillValid(player)) return ItemStack.EMPTY;
        var slot = slots.get(index);
        if (!slot.hasItem()) return ItemStack.EMPTY;
        var stack = slot.getItem(); var original = stack.copy();
        if (index < 27 ? !moveItemStackTo(stack, 27, 36, false) : !moveItemStackTo(stack, 0, 27, false)) return ItemStack.EMPTY;
        if (stack.isEmpty()) slot.setByPlayer(ItemStack.EMPTY); else slot.setChanged();
        if (stack.getCount() == original.getCount()) return ItemStack.EMPTY;
        slot.onTake(player, stack); return original;
    }
    @Override public void removed(Player player) { super.removed(player); closed = true; }
}
