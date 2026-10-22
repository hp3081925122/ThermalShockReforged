package com.hp.thermalshockreforged.client.whisper;

import com.hp.thermalshockreforged.client.dialogue.DialogueText;
import com.hp.thermalshockreforged.entity.whisper.*;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;

public final class WhisperScreen extends AbstractContainerScreen<WhisperMenu> {
    private static final class Clock { int frame; }
    private final Clock clock;
    public WhisperScreen(WhisperMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title); imageWidth = menu.page.width; imageHeight = menu.page.height;
        clock = Minecraft.getInstance().screen instanceof WhisperScreen previous && previous.menu.npcUuid.equals(menu.npcUuid) ? previous.clock : new Clock();
    }
    public static void register(RegisterMenuScreensEvent event) { for (var page : WhisperPage.values()) event.register(WhisperContent.menu(page), WhisperScreen::new); }
    private static ResourceLocation texture(String name) { return WhisperContent.id("textures/screens/" + name + ".png"); }
    @Override protected void init() { super.init(); for (var button : menu.page.buttons) addRenderableWidget(new ImageButton(button)); }
    @Override protected void containerTick() { if (!menu.page.inventory) clock.frame = clock.frame > 12 ? 0 : clock.frame + 1; }
    @Override public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) { super.render(graphics, mouseX, mouseY, partialTick); renderTooltip(graphics, mouseX, mouseY); if (menu.getCarried().isEmpty() && hoveredSlot == null) for (var image : menu.page.images) if (com.hp.thermalshockreforged.client.npc.NpcIcons.tooltip(graphics, font, image.texture(), leftPos + image.x(), topPos + image.y(), image.width(), image.height(), mouseX, mouseY)) break; if (menu.getCarried().isEmpty()) for (var line : menu.page.lines) if (com.hp.thermalshockreforged.client.npc.NpcTradeLabels.tooltip(graphics, font, line.key(), leftPos + line.x(), topPos + line.y(), mouseX, mouseY)) break; }
    @Override protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        RenderSystem.setShaderColor(1, 1, 1, 1); RenderSystem.enableBlend(); RenderSystem.defaultBlendFunc();
        for (var image : menu.page.images) if (!com.hp.thermalshockreforged.client.npc.NpcIcons.draw(graphics, image.texture(), leftPos + image.x(), topPos + image.y(), image.width(), image.height())) graphics.blit(texture(image.texture()), leftPos + image.x(), topPos + image.y(), 0, image.frameMax() < 0 ? 0 : Mth.clamp(clock.frame * 128, 0, image.frameMax()), image.width(), image.height(), image.textureWidth(), image.textureHeight());
        RenderSystem.disableBlend();
    }
    @Override protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
        for (var line : menu.page.lines) DialogueText.draw(graphics, font, com.hp.thermalshockreforged.client.npc.NpcTradeLabels.text(line.key()), line.x(), line.y(), line.color(), imageWidth, line.spacing());
        if (menu.page == WhisperPage.MAIN) DialogueText.draw(graphics, font, Component.translatable("gui.thermal_shock_reforged.whisper_main.greeting_" + (menu.intro == 1 || menu.intro == 2 ? menu.intro : 0)), 9, 112, -1, imageWidth, 0);
    }
    private final class ImageButton extends AbstractButton {
        private final int action;
        private final ResourceLocation texture;
        ImageButton(WhisperPage.Button button) {
            super(leftPos + button.x(), topPos + button.y(), button.width(), button.height(), Component.translatable("gui.thermal_shock_reforged.whisper.button_" + button.texture()));
            action = button.action(); texture = texture("atlas/imagebutton_" + button.texture());
        }
        @Override public void onPress() { if (minecraft != null && minecraft.gameMode != null) minecraft.gameMode.handleInventoryButtonClick(menu.containerId, action); }
        @Override protected void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
            RenderSystem.enableBlend(); RenderSystem.defaultBlendFunc(); graphics.blit(texture, getX(), getY(), 0, isHoveredOrFocused() ? height : 0, width, height, width, height * 2); com.hp.thermalshockreforged.client.ButtonLabels.draw(graphics, texture, getX(), getY(), width, height, isHoveredOrFocused()); RenderSystem.disableBlend();
        }
        @Override protected void updateWidgetNarration(NarrationElementOutput output) { defaultButtonNarrationText(output); }
    }
}
