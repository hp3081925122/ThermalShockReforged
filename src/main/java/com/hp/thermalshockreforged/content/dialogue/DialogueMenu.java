package com.hp.thermalshockreforged.content.dialogue;

import com.hp.thermalshockreforged.content.decor.geo.*;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.*;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public final class DialogueMenu extends AbstractContainerMenu {
    public final DialoguePage page;
    private final Player owner;
    private final Level level;
    private final BlockPos pos;
    public DialogueMenu(DialoguePage page, int id, Inventory inventory, BlockPos pos) {
        super(DialogueContent.type(page), id);
        this.page = page; this.owner = inventory.player; this.level = owner.level(); this.pos = pos.immutable();
    }
    @Override public boolean stillValid(Player player) {
        if (player != owner || player.level() != level || !player.isAlive() || !level.hasChunkAt(pos)) return false;
        var block = GeoDecorContent.block(page == DialoguePage.RADIO ? GeoDecorKind.RUSTY_RADIO : GeoDecorKind.CIVILIAN);
        return AbstractContainerMenu.stillValid(ContainerLevelAccess.create(level, pos), player, block) && (page == DialoguePage.RADIO || level.getBlockState(pos).getValue(DialogueBlock.VARIANT) == page.variant);
    }
    @Override public ItemStack quickMoveStack(Player player, int index) { return ItemStack.EMPTY; }
}
