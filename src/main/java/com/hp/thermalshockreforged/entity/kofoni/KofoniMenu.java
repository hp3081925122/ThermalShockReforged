package com.hp.thermalshockreforged.entity.kofoni;

import com.hp.thermalshockreforged.ThermalShockReforged;
import com.hp.thermalshockreforged.content.decor.nativeblocks.NativeDecorContent;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public final class KofoniMenu extends AbstractContainerMenu {
    private static final TagKey<Item> TABLETS = ItemTags.create(KofoniContent.id("lore_tablet"));
    public final KofoniPage page;
    public final UUID npcUuid;
    public final int intro;
    private final int npcId;
    private final BlockPos anchor;
    private final Player owner;
    private final Level level;
    private final SimpleContainer input = new SimpleContainer(1);
    private boolean changedPage;
    private boolean transaction;
    private boolean closed;
    public KofoniMenu(KofoniPage page, int id, Inventory inventory, int npcId, UUID npcUuid, BlockPos anchor, int intro) {
        super(KofoniContent.menu(page), id); this.page = page; this.npcId = npcId; this.npcUuid = npcUuid; this.anchor = anchor.immutable(); this.intro = intro; owner = inventory.player; level = owner.level();
        if (page == KofoniPage.TABLETS) {
            addSlot(new Slot(input, 0, 95, 39) { @Override public boolean mayPlace(ItemStack stack) { return stack.is(TABLETS); } });
            for (int row = 0; row < 3; row++) for (int column = 0; column < 9; column++) addSlot(new Slot(inventory, column + row * 9 + 9, 23 + column * 18, 99 + row * 18));
            for (int column = 0; column < 9; column++) addSlot(new Slot(inventory, column, 23 + column * 18, 157));
        }
    }
    private KofoniEntity npc() { return level.getEntity(npcId) instanceof KofoniEntity npc && npc.getUUID().equals(npcUuid) ? npc : null; }
    @Override public boolean stillValid(Player player) {
        var npc = npc();
        return !closed && player == owner && player.level() == level && player.isAlive() && npc != null && npc.isAlive() && !npc.isRemoved() && player.distanceToSqr(npc) <= 64;
    }
    @Override public boolean clickMenuButton(Player player, int button) {
        if (changedPage || transaction || !(player instanceof ServerPlayer server) || player.containerMenu != this || !stillValid(player)) return false;
        if (page == KofoniPage.TABLETS) {
            if (button != 0) return false;
            transaction = true;
            try { return exchange(server); } finally { transaction = false; }
        }
        KofoniPage next = switch (page) {
            case MAIN -> switch (button) { case 0 -> KofoniPage.INFO; case 1 -> KofoniPage.TABLETS; default -> null; };
            case INFO -> switch (button) { case 0 -> KofoniPage.SELF; case 1 -> KofoniPage.QO; case 2 -> KofoniPage.BLIZZARD; case 3 -> KofoniPage.MALACHITE; case 4 -> KofoniPage.PYLONS; case 5 -> KofoniPage.BENZOR; case 6 -> KofoniPage.SHALESPRING; case 7 -> KofoniPage.RUBY; default -> null; };
            default -> button == 0 ? KofoniPage.INFO : null;
        };
        if (next == null) return false;
        changedPage = true;
        boolean opened = KofoniContent.open(server, npc(), anchor, next);
        if (!opened) changedPage = false;
        return opened;
    }
    private boolean exchange(ServerPlayer player) {
        ItemStack payment = input.getItem(0);
        if (payment.isEmpty() || !payment.is(TABLETS)) return false;
        var advancement = player.server.getAdvancements().get(KofoniContent.id("whisper_advancement"));
        if (advancement == null) { ThermalShockReforged.LOGGER.warn("Kofoni exchange advancement missing; keeping input: player={}", player.getUUID()); return false; }
        int consumed = payment.getCount();
        var item = payment.getItem();
        input.setItem(0, ItemStack.EMPTY);
        var progress = player.getAdvancements().getOrStartProgress(advancement);
        if (!progress.isDone()) for (String criterion : progress.getRemainingCriteria()) player.getAdvancements().award(advancement, criterion);
        var world = ((net.minecraft.server.level.ServerLevel) player.level());
        world.addFreshEntity(new ExperienceOrb(world, anchor.getX(), anchor.getY(), anchor.getZ(), 20));
        int spawned = 0;
        for (int index = 0; index < (int)Math.ceil(Mth.nextDouble(player.getRandom(), 9, 28)); index++) {
            var reward = new ItemEntity(world, anchor.getX(), anchor.getY(), anchor.getZ(), new ItemStack(NativeDecorContent.SPARKLY_MUCK.get()));
            reward.setPickUpDelay(10);
            if (world.addFreshEntity(reward)) spawned++;
        }
        input.setChanged(); broadcastChanges();
        ThermalShockReforged.LOGGER.debug("Kofoni exchange committed: player={}, item={}, consumed={}, muck={}, xp=20", player.getUUID(), item, consumed, spawned);
        return true;
    }
    @Override public void removed(Player player) {
        super.removed(player);
        if (!closed && !level.isClientSide() && player == owner) clearContainer(player, input);
        closed = true;
    }
    @Override public ItemStack quickMoveStack(Player player, int index) {
        if (page != KofoniPage.TABLETS || player != owner || closed || index < 0 || index >= slots.size()) return ItemStack.EMPTY;
        Slot slot = slots.get(index);
        if (!slot.hasItem()) return ItemStack.EMPTY;
        ItemStack stack = slot.getItem();
        ItemStack original = stack.copy();
        if (index == 0) {
            if (!moveItemStackTo(stack, 1, 37, true)) return ItemStack.EMPTY;
            slot.onQuickCraft(stack, original);
        } else if (!stack.is(TABLETS) || !moveItemStackTo(stack, 0, 1, false)) {
            if (index < 28) { if (!moveItemStackTo(stack, 28, 37, false)) return ItemStack.EMPTY; }
            else if (!moveItemStackTo(stack, 1, 28, false)) return ItemStack.EMPTY;
        }
        if (stack.isEmpty()) slot.setByPlayer(ItemStack.EMPTY); else slot.setChanged();
        if (stack.getCount() == original.getCount()) return ItemStack.EMPTY;
        slot.onTake(player, stack);
        return original;
    }
}
