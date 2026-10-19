package com.hp.thermalshockreforged.entity.labmachinist;

import com.hp.thermalshockreforged.ThermalShockReforged;
import com.hp.thermalshockreforged.content.MaterialContent;
import com.hp.thermalshockreforged.content.atlas.AtlasContent;
import com.hp.thermalshockreforged.content.weapons.WeaponContent;
import com.hp.thermalshockreforged.entity.qo.*;
import java.util.*;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.*;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.*;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;

public final class LabMenu extends AbstractContainerMenu {
    private static final List<LabPage> TRADE = List.of(LabPage.SHOCK_STICK, LabPage.ZAP_GAUNTLET, LabPage.SYLVIA_RIFLE, LabPage.WALLBORE, LabPage.PYLON_CORE, LabPage.ZENITH, LabPage.KINETIC_HOOK);
    public final LabPage page;
    public final UUID npcUuid;
    public final int npcId;
    public final BlockPos anchor;
    public final String openingSound;
    public final int intro;
    private final String blockId;
    private final Player owner;
    private final Level level;
    private final Container output;
    private boolean changedPage, closed;
    public LabMenu(LabPage page, int id, Inventory inventory, int npcId, UUID npcUuid, BlockPos anchor, String blockId, String openingSound, int intro) {
        super(LabContent.menu(page), id); this.page = page; this.npcId = npcId; this.npcUuid = npcUuid; this.anchor = anchor.immutable(); this.blockId = blockId; this.openingSound = openingSound; this.intro = intro; owner = inventory.player; level = owner.level();
        output = page == LabPage.ASSEMBLY && level.getBlockEntity(anchor) instanceof LabMachineBlockEntity machine && !machine.wallbore ? machine : new SimpleContainer(1);
        if (page == LabPage.ASSEMBLY) addSlot(new Slot(output, 0, 186, 15) { @Override public boolean mayPlace(ItemStack stack) { return false; } });
        if (page.inventory) {
            int px = page == LabPage.ASSEMBLY ? 43 : 23, py = page == LabPage.ASSEMBLY ? 82 : 99;
            for (int row = 0; row < 3; row++) for (int col = 0; col < 9; col++) addSlot(new Slot(inventory, col + (row + 1) * 9, px + col * 18, py + row * 18));
            for (int col = 0; col < 9; col++) addSlot(new Slot(inventory, col, px + col * 18, py + 58));
        }
    }
    private LabMachinistEntity npc() { return level.getEntity(npcId) instanceof LabMachinistEntity npc && npc.getUUID().equals(npcUuid) ? npc : null; }
    @Override public boolean stillValid(Player player) {
        if (closed || player != owner || player.level() != level || !player.isAlive()) return false;
        if (npcId >= 0) { var npc = npc(); return npc != null && npc.isAlive() && !npc.isRemoved() && player.distanceToSqr(npc) <= 64; }
        if (!level.hasChunkAt(anchor) || player.distanceToSqr(anchor.getX() + .5, anchor.getY() + .5, anchor.getZ() + .5) > 64 || !net.minecraft.core.registries.BuiltInRegistries.BLOCK.getKey(level.getBlockState(anchor).getBlock()).toString().equals(blockId)) return false;
        if (page != LabPage.ASSEMBLY) return true;
        return level.isClientSide || output == level.getBlockEntity(anchor) && player instanceof ServerPlayer server && QoMenu.completed(server, "icebreaker_1");
    }
    @Override public boolean clickMenuButton(Player player, int button) {
        if (changedPage || !(player instanceof ServerPlayer server) || player.containerMenu != this || !stillValid(player) || page.buttons.stream().noneMatch(b -> b.action() == button)) return false;
        if (page == LabPage.ASSEMBLY) return craft(server);
        if (page.trade()) {
            if (!QoMenu.completed(server, "uneasy_alliance_1")) return false;
            if (button == 0) return LabPlayerState.buy(server, page, anchor);
            int index = TRADE.indexOf(page); return route(server, TRADE.get(Math.floorMod(index + (button == 1 ? 1 : -1), TRADE.size())));
        }
        if (page == LabPage.MAIN) return route(server, button == 0 ? QoMenu.completed(server, "uneasy_alliance_1") ? LabPage.SHOCK_STICK : LabPage.TRADE_INTRO : QoMenu.completed(server, "icebreaker_2") ? LabPage.ERRAND_NULL : LabPage.ERRAND);
        if (page == LabPage.ERRAND_D && !LabPlayerState.available(server, "errands_get", "icebreaker_1")) return false;
        if (page == LabPage.ERRAND_E || page == LabPage.SAFE_1 || page == LabPage.SAFE_2) { changedPage = true; server.closeContainer(); return true; }
        LabPage next = switch (page) { case ERRAND -> LabPage.ERRAND_B; case ERRAND_B -> LabPage.ERRAND_C; case ERRAND_C -> LabPage.ERRAND_D; case ERRAND_D -> LabPage.ERRAND_E; default -> LabPage.MAIN; };
        boolean opened = route(server, next);
        if (opened && page == LabPage.ERRAND_D) { LabPlayerState.award(server, "errands_get"); LabPlayerState.award(server, "icebreaker_1"); }
        return opened;
    }
    private boolean route(ServerPlayer player, LabPage next) { changedPage = true; boolean opened = LabContent.open(player, npc(), anchor, next); if (!opened) changedPage = false; return opened; }
    private boolean craft(ServerPlayer player) {
        if (npcId >= 0 || !(output instanceof LabMachineBlockEntity machine) || machine.wallbore || !output.getItem(0).isEmpty() || !LabPlayerState.available(player, "icebreaker_2")) return false;
        List<Item> materials = List.of(MaterialContent.items().get("frigid_altar_key").get(), WeaponContent.ZAP_GAUNTLET.get(), LabContent.STORM_BATTERY.get().asItem(), AtlasContent.KEYSTONE.get());
        if (materials.stream().anyMatch(item -> LabPlayerState.count(player, item) < 1)) return false;
        for (var item : materials) player.getInventory().clearOrCountMatchingItems(stack -> stack.is(item), 1, player.inventoryMenu.getCraftSlots());
        output.setItem(0, new ItemStack(AtlasContent.VIRUSTONE.get())); output.setChanged();
        player.level().playSound(null, anchor, SoundEvents.CONDUIT_ACTIVATE, SoundSource.NEUTRAL, 1, 2);
        player.level().playSound(null, anchor, SoundEvents.EXPERIENCE_ORB_PICKUP, SoundSource.NEUTRAL, 1, 1);
        LabPlayerState.award(player, "icebreaker_2"); LabPlayerState.sound(player, LabContent.id("thermalshockjingle"), anchor, 1, 1);
        broadcastChanges(); ThermalShockReforged.LOGGER.debug("Lab assembly committed: player={}, position={}", player.getUUID(), anchor); return true;
    }
    @Override public ItemStack quickMoveStack(Player player, int index) {
        if (!page.inventory || index < 0 || index >= slots.size() || !stillValid(player)) return ItemStack.EMPTY;
        var slot = slots.get(index); if (!slot.hasItem()) return ItemStack.EMPTY;
        var stack = slot.getItem(); var original = stack.copy(); int offset = page == LabPage.ASSEMBLY ? 1 : 0;
        if (offset == 1 && index == 0) { if (!moveItemStackTo(stack, 1, 37, true)) return ItemStack.EMPTY; }
        else if (index < offset + 27) { if (!moveItemStackTo(stack, offset + 27, offset + 36, false)) return ItemStack.EMPTY; }
        else if (!moveItemStackTo(stack, offset, offset + 27, false)) return ItemStack.EMPTY;
        if (stack.isEmpty()) slot.setByPlayer(ItemStack.EMPTY); else slot.setChanged();
        if (stack.getCount() == original.getCount()) return ItemStack.EMPTY; slot.onTake(player, stack); return original;
    }
    @Override public void removed(Player player) { super.removed(player); closed = true; }
}
