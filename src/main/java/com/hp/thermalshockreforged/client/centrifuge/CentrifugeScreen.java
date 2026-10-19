package com.hp.thermalshockreforged.client.centrifuge;

import com.hp.thermalshockreforged.ThermalShockReforged;
import com.hp.thermalshockreforged.content.centrifuge.CentrifugeMenu;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public final class CentrifugeScreen extends AbstractContainerScreen<CentrifugeMenu> {
    private static final ResourceLocation BACKGROUND = texture("textures/screens/centrifuger_pager.png");
    private static final ResourceLocation QUESTION = texture("textures/screens/qmark.png");
    private static final ResourceLocation BUTTON = texture("textures/screens/atlas/imagebutton_fabricator_craft_button.png");
    public CentrifugeScreen(CentrifugeMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
        imageWidth = 176;
        imageHeight = 166;
    }
    private static ResourceLocation texture(String path) { return ResourceLocation.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, path); }
    @Override protected void init() { super.init(); addRenderableWidget(new ConvertButton()); }
    @Override public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        super.render(graphics, mouseX, mouseY, partialTick);
        renderTooltip(graphics, mouseX, mouseY);
        if (mouseX > leftPos + 11 && mouseX < leftPos + 35 && mouseY > topPos + 32 && mouseY < topPos + 56)
            graphics.renderTooltip(font, Component.translatable("gui.thermal_shock_reforged.centrifuge_page.tooltip_place_two_same_energies_in_the_l"), mouseX, mouseY);
        if (mouseX > leftPos + 138 && mouseX < leftPos + 162 && mouseY > topPos + 32 && mouseY < topPos + 56)
            graphics.renderTooltip(font, Component.translatable("gui.thermal_shock_reforged.centrifuge_page.tooltip_place_frost_pearl_or_hearth_hone"), mouseX, mouseY);
    }
    @Override protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        RenderSystem.setShaderColor(1, 1, 1, 1);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        graphics.blit(BACKGROUND, leftPos, topPos, 0, 0, 176, 166, 176, 166);
        graphics.blit(QUESTION, leftPos + 15, topPos + 34, 0, 0, 18, 18, 18, 18);
        graphics.blit(QUESTION, leftPos + 141, topPos + 34, 0, 0, 18, 18, 18, 18);
        RenderSystem.disableBlend();
    }
    @Override protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
        graphics.drawString(font, Component.translatable("gui.thermal_shock_reforged.centrifuge_page.label_centrifuge"), 5, -10, -1, false);
    }
    private final class ConvertButton extends AbstractButton {
        private ConvertButton() { super(leftPos + 68, topPos + 60, 40, 18, Component.translatable("gui.thermal_shock_reforged.centrifuge_page.button_convert")); }
        @Override public void onPress() {
            if (minecraft != null && minecraft.gameMode != null) minecraft.gameMode.handleInventoryButtonClick(menu.containerId, 0);
        }
        @Override protected void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            graphics.blit(BUTTON, getX(), getY(), 0, isHoveredOrFocused() ? 18 : 0, width, height, 40, 36);
            RenderSystem.disableBlend();
        }
        @Override protected void updateWidgetNarration(NarrationElementOutput output) { defaultButtonNarrationText(output); }
    }
}
