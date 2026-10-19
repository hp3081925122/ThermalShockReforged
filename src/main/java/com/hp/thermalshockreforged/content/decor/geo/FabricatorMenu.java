package com.hp.thermalshockreforged.content.decor.geo;

import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.registries.DeferredHolder;

public final class FabricatorMenu extends AbstractContainerMenu {
    public static final class Provider implements MenuProvider {
        private final Level level; private final BlockPos pos;
        public Provider(Level level, BlockPos pos) { this.level = level; this.pos = pos.immutable(); }
        @Override public Component getDisplayName() { return Component.translatable("block.thermal_shock_reforged.fabricator"); }
        @Override public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) { return new FabricatorMenu(id, inventory, pos); }
    }
    private final Level level; private final BlockPos pos; private final Player owner;
    public FabricatorMenu(int id, Inventory inventory, RegistryFriendlyByteBuf data) { this(id, inventory, data.readBlockPos()); }
    public FabricatorMenu(int id, Inventory inventory, BlockPos pos) { super(com.hp.thermalshockreforged.content.decor.nativeblocks.NativeDecorContent.FABRICATOR_MENU.get(), id); this.level = inventory.player.level(); this.pos = pos.immutable(); this.owner = inventory.player; }
    @Override public boolean stillValid(Player player) { return player == owner && player.isAlive() && player.level() == level && level.hasChunkAt(pos) && player.distanceToSqr(pos.getCenter()) <= 64 && level.getBlockState(pos).is(GeoDecorContent.block(GeoDecorKind.FABRICATOR)); }
    @Override public ItemStack quickMoveStack(Player player, int index) { return ItemStack.EMPTY; }
}
