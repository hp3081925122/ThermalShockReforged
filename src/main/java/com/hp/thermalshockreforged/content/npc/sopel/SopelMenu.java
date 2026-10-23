package com.hp.thermalshockreforged.content.npc.sopel;

import com.hp.thermalshockreforged.ThermalShockReforged;
import com.hp.thermalshockreforged.content.decor.geo.*;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.*;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public final class SopelMenu extends AbstractContainerMenu {
    public final SopelPage page;
    public final BlockPos anchor;
    private final Player owner;
    private final Level level;
    private boolean changedPage;
    public SopelMenu(SopelPage page, int id, Inventory inventory, BlockPos pos) {
        super(SopelContent.menu(page), id); this.page = page; anchor = pos.immutable(); owner = inventory.player; level = owner.level();
    }
    @Override public boolean stillValid(Player player) {
        return player == owner && player.level() == level && player.isAlive() && level.hasChunkAt(anchor)
                && AbstractContainerMenu.stillValid(ContainerLevelAccess.create(level, anchor), player, GeoDecorContent.block(GeoDecorKind.DESK_DIRECTOR));
    }
    @Override public boolean clickMenuButton(Player player, int button) {
        if (changedPage || !(player instanceof ServerPlayer server) || player.containerMenu != this || !stillValid(player) || button < 0 || button > (page == SopelPage.CHOICE ? 1 : 0)) return false;
        var advancement = page == SopelPage.CHOICE ? server.level().getServer().getAdvancements().get(Identifier.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, "beta_complete")) : null;
        if (page == SopelPage.CHOICE && advancement == null) {
            ThermalShockReforged.LOGGER.warn("Sopel completion advancement unavailable; keeping choice menu: player={}, position={}", player.getUUID(), anchor);
            return false;
        }
        var next = page.next(button);
        changedPage = true;
        if (next == null) { server.closeContainer(); return true; }
        if (!SopelContent.open(server, anchor, next)) { changedPage = false; return false; }
        if (advancement != null) {
            var progress = server.getAdvancements().getOrStartProgress(advancement);
            if (!progress.isDone()) for (String criterion : progress.getRemainingCriteria()) server.getAdvancements().award(advancement, criterion);
        }
        return true;
    }
    @Override public ItemStack quickMoveStack(Player player, int index) { return ItemStack.EMPTY; }
}
