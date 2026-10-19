package com.hp.thermalshockreforged.client.decor;

import com.hp.thermalshockreforged.ThermalShockReforged;
import com.hp.thermalshockreforged.content.decor.geo.FabricatorMenu;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public final class FabricatorScreen extends AbstractContainerScreen<FabricatorMenu> {
    private static final ResourceLocation PANEL = ResourceLocation.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, "textures/screens/fabricator_panel.png");
    public FabricatorScreen(FabricatorMenu menu, Inventory inventory, Component title) { super(menu, inventory, title); imageWidth = 176; imageHeight = 196; }
    @Override protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) { RenderSystem.setShaderColor(1, 1, 1, 1); graphics.blit(PANEL, leftPos, topPos, 0, 0, imageWidth, imageHeight, imageWidth, imageHeight); }
    @Override protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) { graphics.drawString(font, Component.translatable("block.thermal_shock_reforged.fabricator"), 8, 8, -1, false); graphics.drawString(font, Component.translatable("gui.thermal_shock_reforged.fabricator_ui.tooltip_how_to_fabricate"), 8, 30, -1, false); }
}
