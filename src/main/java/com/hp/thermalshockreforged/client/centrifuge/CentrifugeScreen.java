package com.hp.thermalshockreforged.client.centrifuge;

import com.hp.thermalshockreforged.ThermalShockReforged;
import com.hp.thermalshockreforged.content.centrifuge.CentrifugeMenu;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;

public final class CentrifugeScreen extends AbstractContainerScreen<CentrifugeMenu> {
    private static final Identifier BACKGROUND = texture("textures/screens/centrifuger_pager.png");
    private static final Identifier QUESTION = texture("textures/screens/qmark.png");
    private static final Identifier BUTTON = texture("textures/screens/atlas/imagebutton_fabricator_craft_button.png");
    public CentrifugeScreen(CentrifugeMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title, 176, 166);
    }
    private static Identifier texture(String path) { return Identifier.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, path); }
    @Override protected void init() { super.init(); addRenderableWidget(new ConvertButton()); }
    @Override public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        super.extractRenderState(graphics, mouseX, mouseY, partialTick);
        
        if (mouseX > leftPos + 11 && mouseX < leftPos + 35 && mouseY > topPos + 32 && mouseY < topPos + 56)
            graphics.setTooltipForNextFrame(font, Component.translatable("gui.thermal_shock_reforged.centrifuge_page.tooltip_place_two_same_energies_in_the_l"), mouseX, mouseY);
        if (mouseX > leftPos + 138 && mouseX < leftPos + 162 && mouseY > topPos + 32 && mouseY < topPos + 56)
            graphics.setTooltipForNextFrame(font, Component.translatable("gui.thermal_shock_reforged.centrifuge_page.tooltip_place_frost_pearl_or_hearth_hone"), mouseX, mouseY);
    }
    @Override public void extractContents(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) { super.extractContents(graphics, mouseX, mouseY, partialTick);
        com.mojang.blaze3d.opengl.GlStateManager._enableBlend();
        
        graphics.blit(BACKGROUND, leftPos, topPos, 0, 0, 176, 166, 176, 166);
        graphics.blit(QUESTION, leftPos + 15, topPos + 34, 0, 0, 18, 18, 18, 18);
        graphics.blit(QUESTION, leftPos + 141, topPos + 34, 0, 0, 18, 18, 18, 18);
        com.mojang.blaze3d.opengl.GlStateManager._disableBlend();
    }
    @Override protected void extractLabels(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
        graphics.text(font, Component.translatable("gui.thermal_shock_reforged.centrifuge_page.label_centrifuge"), 5, -10, -1, false);
    }
    private final class ConvertButton extends AbstractButton {
        private ConvertButton() { super(leftPos + 68, topPos + 60, 40, 18, Component.translatable("gui.thermal_shock_reforged.centrifuge_page.button_convert")); }
        @Override public void onPress(net.minecraft.client.input.InputWithModifiers input) {
            if (minecraft != null && minecraft.gameMode != null) minecraft.gameMode.handleInventoryButtonClick(menu.containerId, 0);
        }
        @Override protected void extractContents(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
            com.mojang.blaze3d.opengl.GlStateManager._enableBlend();
            
            graphics.blit(BUTTON, getX(), getY(), 0, isHoveredOrFocused() ? 18 : 0, width, height, 40, 36); com.hp.thermalshockreforged.client.ButtonLabels.draw(graphics, BUTTON, getX(), getY(), 40, 18, isHoveredOrFocused());
            com.mojang.blaze3d.opengl.GlStateManager._disableBlend();
        }
        @Override protected void updateWidgetNarration(NarrationElementOutput output) { defaultButtonNarrationText(output); }
    }
}
