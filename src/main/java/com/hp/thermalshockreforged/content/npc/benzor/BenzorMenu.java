package com.hp.thermalshockreforged.content.npc.benzor;

import com.hp.thermalshockreforged.ThermalShockReforged;
import com.hp.thermalshockreforged.entity.benzor.*;
import java.util.UUID;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.*;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;

public final class BenzorMenu extends AbstractContainerMenu {
    private static final String[] SPICES = {"adamantine_spice", "mithral_spice", "tourmaline_spice"};
    public final BenzorPage page;
    public final UUID npcUuid;
    public final int intro;
    private final int npcId;
    private final Player owner;
    private final Level level;
    private final SimpleContainer input = new SimpleContainer(3);
    private final DataSlot blackpowder = DataSlot.standalone();
    private boolean changedPage;
    private boolean transaction;
    private boolean closed;
    public BenzorMenu(BenzorPage page, int id, Inventory inventory, int npcId, UUID npcUuid, int intro) {
        super(BenzorMenus.menu(page), id); this.page = page; this.npcId = npcId; this.npcUuid = npcUuid; this.intro = intro; owner = inventory.player; level = owner.level(); addDataSlot(blackpowder); if (owner instanceof ServerPlayer server) blackpowder.set(BenzorMenus.blackpowder(server) ? 1 : 0);
        if (page == BenzorPage.SPICE) {
            for (int index = 0; index < 3; index++) { int inputIndex = index; addSlot(new Slot(input, index, 59 + index * 36, 52) { @Override public boolean mayPlace(ItemStack stack) { return accepts(inputIndex, stack); } }); }
            for (int row = 0; row < 3; row++) for (int column = 0; column < 9; column++) addSlot(new Slot(inventory, column + row * 9 + 9, 23 + column * 18, 99 + row * 18));
            for (int column = 0; column < 9; column++) addSlot(new Slot(inventory, column, 23 + column * 18, 157));
        }
    }
    private static boolean accepts(int index, ItemStack stack) { return !stack.isEmpty() && BuiltInRegistries.ITEM.getKey(stack.getItem()).equals(BenzorContent.id(SPICES[index])); }
    public boolean blackpowderAllowed() { return blackpowder.get() != 0; }
    @Override public void broadcastChanges() { if (owner instanceof ServerPlayer server) blackpowder.set(BenzorMenus.blackpowder(server) ? 1 : 0); super.broadcastChanges(); }
    private BenzorEntity npc() { return level.getEntity(npcId) instanceof BenzorEntity npc && npc.getUUID().equals(npcUuid) ? npc : null; }
    @Override public boolean stillValid(Player player) { var npc = npc(); return !closed && player == owner && player.level() == level && player.isAlive() && npc != null && npc.isAlive() && !npc.isRemoved() && player.distanceToSqr(npc) <= 64; }
    @Override public boolean clickMenuButton(Player player, int button) {
        if (changedPage || transaction || !(player instanceof ServerPlayer server) || player.containerMenu != this || !stillValid(player)) return false;
        if (page == BenzorPage.SPICE) { if (button != 0) return false; transaction = true; try { return exchange(server); } finally { transaction = false; } }
        boolean introduce = page == BenzorPage.MAIN && button == 1 && !server.getPersistentData().getBoolean(BenzorMenus.INTRO);
        BenzorPage next = switch (page) {
            case MAIN -> button == 0 ? BenzorPage.INFO : button == 1 ? introduce ? BenzorPage.SPICE_INTRO : BenzorPage.SPICE : null;
            case INFO -> switch (button) { case 0 -> BenzorPage.SELF; case 1 -> BenzorPage.SPICE_INFO; case 2 -> BenzorPage.INFECTION; case 3 -> BenzorPage.BLIZZARD; case 4 -> BenzorMenus.blackpowder(server) ? BenzorPage.BLACKPOWDER : null; default -> null; };
            case SPICE_INTRO -> button == 0 ? BenzorPage.SPICE : null;
            default -> button == 0 ? BenzorPage.INFO : null;
        };
        if (next == null) return false;
        changedPage = true; boolean opened = BenzorMenus.open(server, npc(), next); if (!opened) changedPage = false; else if (introduce) server.getPersistentData().putBoolean(BenzorMenus.INTRO, true); return opened;
    }
    private boolean exchange(ServerPlayer player) {
        int[] counts = new int[3]; int experience = 0;
        for (int index = 0; index < 3; index++) { var stack = input.getItem(index); if (!stack.isEmpty() && !accepts(index, stack)) return false; counts[index] = stack.getCount(); experience += counts[index] * (2 << index); }
        if (experience <= 0) return false;
        var advancement = player.server.getAdvancements().get(BenzorContent.id("whisper_advancement"));
        if (advancement == null) { ThermalShockReforged.LOGGER.warn("Benzor exchange advancement missing; keeping input: player={}", player.getUUID()); return false; }
        for (int index = 0; index < 3; index++) input.setItem(index, ItemStack.EMPTY);
        var progress = player.getAdvancements().getOrStartProgress(advancement); if (!progress.isDone()) for (String criterion : progress.getRemainingCriteria()) player.getAdvancements().award(advancement, criterion);
        player.giveExperiencePoints(experience); input.setChanged(); broadcastChanges();
        ThermalShockReforged.LOGGER.debug("Benzor exchange committed: player={}, adamantine={}, mithral={}, tourmaline={}, xp={}", player.getUUID(), counts[0], counts[1], counts[2], experience); return true;
    }
    @Override public void removed(Player player) { super.removed(player); if (!closed && !level.isClientSide() && player == owner) clearContainer(player, input); closed = true; }
    @Override public ItemStack quickMoveStack(Player player, int index) {
        if (page != BenzorPage.SPICE || player != owner || closed || index < 0 || index >= slots.size()) return ItemStack.EMPTY;
        Slot slot = slots.get(index); if (!slot.hasItem()) return ItemStack.EMPTY; ItemStack stack = slot.getItem(); ItemStack original = stack.copy();
        if (index < 3) { if (!moveItemStackTo(stack, 3, 39, true)) return ItemStack.EMPTY; slot.onQuickCraft(stack, original); }
        else if (!moveItemStackTo(stack, 0, 3, false)) { if (index < 30) { if (!moveItemStackTo(stack, 30, 39, false)) return ItemStack.EMPTY; } else if (!moveItemStackTo(stack, 3, 30, false)) return ItemStack.EMPTY; }
        if (stack.isEmpty()) slot.setByPlayer(ItemStack.EMPTY); else slot.setChanged(); if (stack.getCount() == original.getCount()) return ItemStack.EMPTY; slot.onTake(player, stack); return original;
    }
}
