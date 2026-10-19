package com.hp.thermalshockreforged.client.decor;

import com.hp.thermalshockreforged.ThermalShockReforged;
import com.hp.thermalshockreforged.content.decor.geo.FabricatorMenu;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public final class FabricatorScreen extends AbstractContainerScreen<FabricatorMenu> {
    private int page;
    public FabricatorScreen(FabricatorMenu menu, Inventory inventory, Component title) { super(menu, inventory, title); imageWidth = 240; imageHeight = 196; }
    @Override protected void init() { super.init(); addRenderableWidget(Button.builder(Component.literal("<"), button -> page = Math.max(0, page - 1)).bounds(leftPos + 2, topPos + 85, 18, 18).build()); addRenderableWidget(Button.builder(Component.literal(">"), button -> page = Math.min(2, page + 1)).bounds(leftPos + 220, topPos + 85, 18, 18).build()); }
    @Override protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) { RenderSystem.setShaderColor(1, 1, 1, 1); ResourceLocation pageTexture = ResourceLocation.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, "textures/screens/guide_page_" + (page + 1) + ".png"); graphics.blit(pageTexture, leftPos + 32, topPos, 0, 0, 176, 196, 176, 196); }
    @Override protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) { graphics.drawString(font, Component.translatable("block.thermal_shock_reforged.fabricator"), 8, 8, -1, false); }
}
