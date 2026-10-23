package com.hp.thermalshockreforged.client.decor.nativeblocks;

import com.hp.thermalshockreforged.ThermalShockReforged;
import com.hp.thermalshockreforged.content.decor.nativeblocks.DeskPhotoMenu;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;

public final class DeskPhotoScreen extends AbstractContainerScreen<DeskPhotoMenu> {
    private static final Identifier PHOTO = Identifier.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, "textures/screens/lotograph.png");

    public DeskPhotoScreen(DeskPhotoMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title, 176, 166);
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        super.extractRenderState(graphics, mouseX, mouseY, partialTick);
        
    }

    @Override
    public void extractContents(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        com.mojang.blaze3d.opengl.GlStateManager._enableBlend();
        
        graphics.blit(PHOTO, leftPos + 6, topPos + 1, 0, 0, 164, 164, 164, 164);
        com.mojang.blaze3d.opengl.GlStateManager._disableBlend();
    }

    @Override
    protected void extractLabels(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
    }
}
