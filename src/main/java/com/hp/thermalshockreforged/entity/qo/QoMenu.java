package com.hp.thermalshockreforged.entity.qo;

import com.hp.thermalshockreforged.ThermalShockReforged;
import com.hp.thermalshockreforged.content.MaterialContent;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.protocol.game.ClientboundSoundPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.*;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public final class QoMenu extends AbstractContainerMenu {
    public final QoPage page;
    public final String openingSound;
    public final UUID npcUuid;
    public final BlockPos anchor;
    private final int npcId;
    private final Player owner;
    private final Level level;
    private boolean changedPage;
    private boolean closed;
    public QoMenu(QoPage page, int id, Inventory inventory, int npcId, UUID npcUuid, BlockPos anchor, String openingSound) {
        super(QoContent.menu(page), id); this.page = page; this.openingSound = openingSound; this.npcId = npcId; this.npcUuid = npcUuid; this.anchor = anchor.immutable(); owner = inventory.player; level = owner.level();
    }
    private DefectorMachinistEntity npc() { return level.getEntity(npcId) instanceof DefectorMachinistEntity npc && npc.getUUID().equals(npcUuid) ? npc : null; }
    @Override public boolean stillValid(Player player) { var npc = npc(); return !closed && player == owner && player.level() == level && player.isAlive() && npc != null && npc.isAlive() && !npc.isRemoved() && player.distanceToSqr(npc) <= 64; }
    public static boolean completed(ServerPlayer player, String id) { var advancement = player.server.getAdvancements().get(QoContent.id(id)); return advancement != null && player.getAdvancements().getOrStartProgress(advancement).isDone(); }
    @Override public boolean clickMenuButton(Player player, int button) {
        if (changedPage || !(player instanceof ServerPlayer server) || player.containerMenu != this || !stillValid(player)) return false;
        QoPage next = page == QoPage.STRATUS_1 && button == 0 ? QoPage.STRATUS_2 : page == QoPage.STRATUS_2 && button == 1 ? QoPage.STRATUS_REJECT : null;
        if (page == QoPage.STRATUS_2 && button == 0) return accept(server);
        if (next == null) return false;
        changedPage = true; boolean opened = QoContent.open(server, npc(), anchor, next); if (!opened) changedPage = false; return opened;
    }
    private boolean accept(ServerPlayer player) {
        var errands = player.server.getAdvancements().get(QoContent.id("errands_get"));
        var alliance = player.server.getAdvancements().get(QoContent.id("uneasy_alliance_1"));
        if (errands == null || alliance == null) { ThermalShockReforged.LOGGER.warn("Qo errand advancements unavailable; keeping payment: player={}", player.getUUID()); return false; }
        if (player.getAdvancements().getOrStartProgress(alliance).isDone()) return false;
        var material = MaterialContent.items().get("stratus_alloy").get();
        if (!player.getInventory().contains(stack -> stack.is(material)) && !player.inventoryMenu.getCraftSlots().hasAnyOf(java.util.Set.of(material))) return false;
        changedPage = true;
        if (!QoContent.open(player, npc(), anchor, QoPage.STRATUS_ACCEPT)) { changedPage = false; return false; }
        int removed = player.getInventory().clearOrCountMatchingItems(stack -> stack.is(material), 1, player.inventoryMenu.getCraftSlots());
        if (removed != 1) { ThermalShockReforged.LOGGER.error("Qo payment changed during server transaction: player={}", player.getUUID()); return false; }
        for (var advancement : java.util.List.of(errands, alliance)) for (String criterion : player.getAdvancements().getOrStartProgress(advancement).getRemainingCriteria()) player.getAdvancements().award(advancement, criterion);
        QoContent.give(player, new ItemStack(QoContent.ALLIANCE_BUNDLE.get()));
        var sound = BuiltInRegistries.SOUND_EVENT.get(QoContent.id("thermalshockjingle"));
        player.connection.send(new ClientboundSoundPacket(Holder.direct(sound), SoundSource.NEUTRAL, anchor.getX(), anchor.getY(), anchor.getZ(), 1, 1, player.getRandom().nextLong()));
        ThermalShockReforged.LOGGER.debug("Qo stratus errand committed: player={}, npc={}", player.getUUID(), npcUuid); return true;
    }
    @Override public void removed(Player player) { super.removed(player); closed = true; }
    @Override public ItemStack quickMoveStack(Player player, int index) { return ItemStack.EMPTY; }
}
