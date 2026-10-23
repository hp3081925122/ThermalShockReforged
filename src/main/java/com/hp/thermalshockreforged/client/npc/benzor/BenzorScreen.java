package com.hp.thermalshockreforged.client.npc.benzor;

import com.hp.thermalshockreforged.client.dialogue.DialogueText;
import com.hp.thermalshockreforged.content.npc.benzor.*;
import com.hp.thermalshockreforged.entity.benzor.BenzorContent;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;

public final class BenzorScreen extends AbstractContainerScreen<BenzorMenu> {
    public BenzorScreen(BenzorMenu menu, Inventory inventory, Component title) { super(menu, inventory, title); }
    public static void register(RegisterMenuScreensEvent event) { for (var page : BenzorPage.values()) event.register(BenzorMenus.menu(page), BenzorScreen::new); }
    private static Identifier texture(String name) { return BenzorContent.id("textures/screens/" + name + ".png"); }
    @Override protected void init() { super.init(); for (var button : menu.page.buttons) addRenderableWidget(new ImageButton(button)); }
    @Override public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) { for (var child : children()) if (child instanceof ImageButton button) button.visible = menu.page != BenzorPage.INFO || button.action != 4 || menu.blackpowderAllowed(); super.extractRenderState(graphics, mouseX, mouseY, partialTick);  if (menu.getCarried().isEmpty() && hoveredSlot == null) for (var image : menu.page.images) if (com.hp.thermalshockreforged.client.npc.NpcIcons.tooltip(graphics, font, image.texture(), leftPos + image.x(), topPos + image.y(), image.width(), image.height(), mouseX, mouseY)) break; }
    @Override public void extractContents(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) { super.extractContents(graphics, mouseX, mouseY, partialTick); com.mojang.blaze3d.opengl.GlStateManager._enableBlend();  for (var image : menu.page.images) if (!com.hp.thermalshockreforged.client.npc.NpcIcons.draw(graphics, image.texture(), leftPos + image.x(), topPos + image.y(), image.width(), image.height())) graphics.blit(texture(image.texture()), leftPos + image.x(), topPos + image.y(), 0, 0, image.width(), image.height(), image.textureWidth(), image.textureHeight()); com.mojang.blaze3d.opengl.GlStateManager._disableBlend(); }
    @Override protected void extractLabels(GuiGraphicsExtractor graphics, int mouseX, int mouseY) { if (menu.page == BenzorPage.MAIN) DialogueText.draw(graphics, font, Component.translatable("gui.thermal_shock_reforged.benzor.intro_" + (menu.intro == 1 ? "1" : menu.intro == 2 ? "2" : "fallback")), 9, 112, -1, imageWidth, 0); for (var line : menu.page.lines) DialogueText.draw(graphics, font, Component.translatable(line.key()), line.x(), line.y(), line.color(), imageWidth, line.spacing()); }
    private final class ImageButton extends AbstractButton {
        private final int action;
        private final Identifier texture;
        ImageButton(BenzorPage.Button button) { super(leftPos + button.x(), topPos + button.y(), button.width(), button.height(), Component.translatable("gui.thermal_shock_reforged.benzor.button_" + button.texture())); action = button.action(); texture = texture("atlas/imagebutton_" + button.texture()); }
        @Override public void onPress(net.minecraft.client.input.InputWithModifiers input) { if (visible && minecraft != null && minecraft.gameMode != null) minecraft.gameMode.handleInventoryButtonClick(menu.containerId, action); }
        @Override protected void extractContents(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) { com.mojang.blaze3d.opengl.GlStateManager._enableBlend();  graphics.blit(texture, getX(), getY(), 0, isHoveredOrFocused() ? height : 0, width, height, width, height * 2); com.hp.thermalshockreforged.client.ButtonLabels.draw(graphics, texture, getX(), getY(), width, height, isHoveredOrFocused()); com.mojang.blaze3d.opengl.GlStateManager._disableBlend(); }
        @Override protected void updateWidgetNarration(NarrationElementOutput output) { defaultButtonNarrationText(output); }
    }
}
