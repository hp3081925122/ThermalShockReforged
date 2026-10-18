package com.hp.thermalshockreforged.client.atlas;

import com.hp.thermalshockreforged.ThermalShockReforged;
import com.hp.thermalshockreforged.content.atlas.AtlasMenu;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public final class AtlasScreen extends AbstractContainerScreen<AtlasMenu> {
    private static final ResourceLocation BACKGROUND = id("textures/screens/atlas_rubicon.png");
    public AtlasScreen(AtlasMenu menu, Inventory inventory, Component title) { super(menu, inventory, title); imageWidth = 196; imageHeight = 196; }
    private static ResourceLocation id(String path) { return ResourceLocation.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, path); }
    @Override protected void init() {
        super.init();
        addRenderableWidget(new AtlasButton(0, 43, 83, "atlas_button_inac", Component.literal("Loqendia")));
        addRenderableWidget(new AtlasButton(1, 114, 130, "atlas_button_inac1", Component.translatable("generator.minecraft.normal")));
        addRenderableWidget(new AtlasButton(2, 124, 53, "atlas_button_inac2", Component.literal("Palace")));
    }
    @Override public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) { super.render(graphics, mouseX, mouseY, partialTick); renderTooltip(graphics, mouseX, mouseY); }
    @Override protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        RenderSystem.setShaderColor(1, 1, 1, 1); RenderSystem.enableBlend(); RenderSystem.defaultBlendFunc();
        graphics.blit(BACKGROUND, leftPos + 2, topPos + 2, 0, 0, 192, 192, 192, 192);
        RenderSystem.disableBlend();
    }
    @Override protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) { graphics.drawString(font, Component.translatable("gui.thermal_shock_reforged.atlas_menu.label_fabricator_guide"), 2, -9, -1, false); }
    private final class AtlasButton extends AbstractButton {
        private final int destination;
        private final ResourceLocation texture;
        AtlasButton(int destination, int x, int y, String texture, Component label) { super(leftPos + x, topPos + y, 20, 20, label); this.destination = destination; this.texture = id("textures/screens/atlas/imagebutton_" + texture + ".png"); }
        @Override public void onPress() { if (minecraft != null && minecraft.gameMode != null) minecraft.gameMode.handleInventoryButtonClick(menu.containerId, destination); }
        @Override protected void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
            RenderSystem.enableBlend(); RenderSystem.defaultBlendFunc();
            graphics.blit(texture, getX(), getY(), 0, isHoveredOrFocused() ? 20 : 0, 20, 20, 20, 40);
            RenderSystem.disableBlend();
        }
        @Override protected void updateWidgetNarration(NarrationElementOutput output) { defaultButtonNarrationText(output); }
    }
}
