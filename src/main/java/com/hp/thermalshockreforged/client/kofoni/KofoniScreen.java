package com.hp.thermalshockreforged.client.kofoni;

import com.hp.thermalshockreforged.client.dialogue.DialogueText;
import com.hp.thermalshockreforged.entity.kofoni.*;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;

public final class KofoniScreen extends AbstractContainerScreen<KofoniMenu> {
    private final Clock clock;
    public KofoniScreen(KofoniMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
        clock = Minecraft.getInstance().screen instanceof KofoniScreen previous && previous.menu.npcUuid.equals(menu.npcUuid) ? previous.clock : new Clock();
    }
    private static Identifier texture(String name) { return KofoniContent.id("textures/screens/" + name + ".png"); }
    @Override protected void init() {
        super.init();
        for (var button : menu.page.buttons) {
            addRenderableWidget(new ImageButton(button));
        }
    }
    @Override protected void containerTick() {
        if (menu.page != KofoniPage.TABLETS) clock.frame = clock.frame > 12 ? 0 : clock.frame + 1;
    }
    @Override public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        super.extractRenderState(graphics, mouseX, mouseY, partialTick);  if (menu.getCarried().isEmpty() && hoveredSlot == null) for (var image : menu.page.images) if (com.hp.thermalshockreforged.client.npc.NpcIcons.tooltip(graphics, font, image.texture(), leftPos + image.x(), topPos + image.y(), image.width(), image.height(), mouseX, mouseY)) break;
    }
    @Override public void extractContents(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) { super.extractContents(graphics, mouseX, mouseY, partialTick);
        com.mojang.blaze3d.opengl.GlStateManager._enableBlend(); 
        for (var image : menu.page.images) if (!com.hp.thermalshockreforged.client.npc.NpcIcons.draw(graphics, image.texture(), leftPos + image.x(), topPos + image.y(), image.width(), image.height())) graphics.blit(texture(image.texture()), leftPos + image.x(), topPos + image.y(), 0, image.animated() ? Mth.clamp(clock.frame * 128, 0, 1408) : 0, image.width(), image.height(), image.textureWidth(), image.textureHeight());
        com.mojang.blaze3d.opengl.GlStateManager._disableBlend();
    }
    @Override protected void extractLabels(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
        if (menu.page == KofoniPage.MAIN) DialogueText.draw(graphics, font, Component.translatable("gui.thermal_shock_reforged.kofoni.intro_" + (menu.intro == 1 ? "1" : menu.intro == 2 ? "2" : "fallback")), 9, 112, -1, imageWidth, 0);
        for (var line : menu.page.lines) DialogueText.draw(graphics, font, Component.translatable(line.key()), line.x(), line.y(), line.color(), imageWidth, line.spacing());
    }
    private static final class Clock { int frame; }
    private final class ImageButton extends AbstractButton {
        private final int action;
        private final Identifier texture;
        ImageButton(KofoniPage.Button button) {
            super(leftPos + button.x(), topPos + button.y(), button.width(), button.height(), Component.translatable("gui.thermal_shock_reforged.kofoni.button_" + button.texture()));
            action = button.action(); texture = texture("atlas/imagebutton_" + button.texture());
        }
        @Override public void onPress(net.minecraft.client.input.InputWithModifiers input) { if (action >= 0 && minecraft != null && minecraft.gameMode != null) minecraft.gameMode.handleInventoryButtonClick(menu.containerId, action); }
        @Override protected void extractContents(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
            com.mojang.blaze3d.opengl.GlStateManager._enableBlend();  graphics.blit(texture, getX(), getY(), 0, isHoveredOrFocused() ? height : 0, width, height, width, height * 2); com.hp.thermalshockreforged.client.ButtonLabels.draw(graphics, texture, getX(), getY(), width, height, isHoveredOrFocused()); com.mojang.blaze3d.opengl.GlStateManager._disableBlend();
        }
        @Override protected void updateWidgetNarration(NarrationElementOutput output) { defaultButtonNarrationText(output); }
    }
}
