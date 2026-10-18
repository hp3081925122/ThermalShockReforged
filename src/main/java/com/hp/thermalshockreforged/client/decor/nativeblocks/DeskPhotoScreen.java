package com.hp.thermalshockreforged.client.decor.nativeblocks;

import com.hp.thermalshockreforged.ThermalShockReforged;
import com.hp.thermalshockreforged.content.decor.nativeblocks.DeskPhotoMenu;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public final class DeskPhotoScreen extends AbstractContainerScreen<DeskPhotoMenu> {
    private static final ResourceLocation PHOTO = ResourceLocation.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, "textures/screens/lotograph.png");

    public DeskPhotoScreen(DeskPhotoMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
        imageWidth = 176;
        imageHeight = 166;
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        super.render(graphics, mouseX, mouseY, partialTick);
        renderTooltip(graphics, mouseX, mouseY);
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        RenderSystem.setShaderColor(1, 1, 1, 1);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        graphics.blit(PHOTO, leftPos + 6, topPos + 1, 0, 0, 164, 164, 164, 164);
        RenderSystem.disableBlend();
    }

    @Override
    protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
    }
}
