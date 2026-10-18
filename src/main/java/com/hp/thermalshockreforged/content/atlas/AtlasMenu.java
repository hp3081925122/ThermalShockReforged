package com.hp.thermalshockreforged.content.atlas;

import com.hp.thermalshockreforged.content.decor.geo.*;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.*;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public final class AtlasMenu extends AbstractContainerMenu {
    private final BlockPos pos;
    private final Level level;
    private final Player owner;
    private boolean committed;
    public AtlasMenu(int id, Inventory inventory, RegistryFriendlyByteBuf data) { this(id, inventory, data.readBlockPos()); }
    public AtlasMenu(int id, Inventory inventory, BlockPos pos) {
        super(AtlasContent.MENU.get(), id);
        this.pos = pos.immutable(); this.level = inventory.player.level(); this.owner = inventory.player;
    }
    @Override public boolean stillValid(Player player) {
        return player == owner && player.level() == level && player.isAlive() && level.hasChunkAt(pos) && AbstractContainerMenu.stillValid(ContainerLevelAccess.create(level, pos), player, GeoDecorContent.block(GeoDecorKind.ATLAS));
    }
    @Override public boolean clickMenuButton(Player player, int button) {
        if (committed || button < 0 || button > 2 || !(player instanceof ServerPlayer serverPlayer) || player.containerMenu != this || !stillValid(player)) return false;
        committed = true;
        boolean result = AtlasTravel.travel(serverPlayer, pos, button);
        if (!result) committed = false;
        return result;
    }
    @Override public ItemStack quickMoveStack(Player player, int index) { return ItemStack.EMPTY; }
}
