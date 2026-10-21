package com.hp.thermalshockreforged.client.npc.sopel;

import com.hp.thermalshockreforged.client.dialogue.DialogueText;
import com.hp.thermalshockreforged.ThermalShockReforged;
import com.hp.thermalshockreforged.content.npc.sopel.*;
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
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;

public final class SopelScreen extends AbstractContainerScreen<SopelMenu> {
    private final Clock clock;
    private final Level sceneLevel;
    public SopelScreen(SopelMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title); imageWidth = 276; imageHeight = 182;
        sceneLevel = inventory.player.level();
        clock = Minecraft.getInstance().screen instanceof SopelScreen previous && previous.sceneLevel == sceneLevel && previous.menu.anchor.equals(menu.anchor) ? previous.clock : new Clock();
    }
    public static void register(RegisterMenuScreensEvent event) { for (var page : SopelPage.values()) event.register(SopelContent.menu(page), SopelScreen::new); }
    private static ResourceLocation id(String path) { return ResourceLocation.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, "textures/screens/" + path + ".png"); }
    @Override protected void init() {
        super.init();
        if (menu.page == SopelPage.CHOICE) {
            addRenderableWidget(new ImageButton(0, 107, 87, 20, 20, "yesbuttonb", "gui.thermal_shock_reforged.sopel.button_yes"));
            addRenderableWidget(new ImageButton(1, 147, 87, 20, 20, "nobuttonb", "gui.thermal_shock_reforged.sopel.button_no"));
        } else addRenderableWidget(new ImageButton(0, 237, 162, 38, 20, "nextbuttona", "gui.thermal_shock_reforged.sopel.button_next"));
    }
    @Override protected void containerTick() { if (menu.page != SopelPage.CHOICE) clock.frame = clock.frame > 12 ? 0 : clock.frame + .125; }
    @Override public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) { super.render(graphics, mouseX, mouseY, partialTick); renderTooltip(graphics, mouseX, mouseY); }
    @Override protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        RenderSystem.setShaderColor(1, 1, 1, 1); RenderSystem.enableBlend(); RenderSystem.defaultBlendFunc();
        graphics.blit(id("mainspeechbox"), leftPos, topPos + (menu.page == SopelPage.CHOICE ? 50 : 104), 0, 0, 276, 276, 276, 276);
        if (menu.page != SopelPage.CHOICE) {
            graphics.blit(id("mainlabelbox"), leftPos + 62, topPos - 29, 0, 0, 134, 134, 134, 134);
            graphics.blit(id("sopelidleportrait"), leftPos - 30, topPos - 23, 0, Mth.clamp((int)clock.frame * 128, 0, 1408), 128, 128, 128, 1536);
        }
        RenderSystem.disableBlend();
    }
    @Override protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) { for (var line : menu.page.lines) DialogueText.draw(graphics, font, Component.translatable(line.key()), line.x(), line.y(), line.color(), imageWidth, line.spacing()); }
    private static final class Clock { double frame; }
    private final class ImageButton extends AbstractButton {
        private final int action;
        private final ResourceLocation texture;
        ImageButton(int action, int x, int y, int width, int height, String texture, String labelKey) {
            super(leftPos + x, topPos + y, width, height, Component.translatable(labelKey)); this.action = action; this.texture = id("atlas/imagebutton_" + texture);
        }
        @Override public void onPress() { if (minecraft != null && minecraft.gameMode != null) minecraft.gameMode.handleInventoryButtonClick(menu.containerId, action); }
        @Override protected void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) { RenderSystem.enableBlend(); RenderSystem.defaultBlendFunc(); graphics.blit(texture, getX(), getY(), 0, isHoveredOrFocused() ? height : 0, width, height, width, height * 2); com.hp.thermalshockreforged.client.ButtonLabels.draw(graphics, texture, getX(), getY(), width, height, isHoveredOrFocused()); RenderSystem.disableBlend(); }
        @Override protected void updateWidgetNarration(NarrationElementOutput output) { defaultButtonNarrationText(output); }
    }
}
