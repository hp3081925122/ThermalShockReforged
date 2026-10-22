package com.hp.thermalshockreforged.client.npc.ly;

import com.hp.thermalshockreforged.client.dialogue.DialogueText;
import com.hp.thermalshockreforged.ThermalShockReforged;
import com.hp.thermalshockreforged.content.npc.ly.*;
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

public final class LyScreen extends AbstractContainerScreen<LyMenu> {
    private final Clock clock;
    public LyScreen(LyMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title); imageWidth = menu.page == LyPage.MAIN ? 276 : 206; imageHeight = menu.page == LyPage.MAIN ? 182 : 196;
        clock = Minecraft.getInstance().screen instanceof LyScreen previous && previous.menu.npcUuid.equals(menu.npcUuid) ? previous.clock : new Clock();
    }
    private static ResourceLocation id(String path) { return ResourceLocation.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, "textures/screens/" + path + ".png"); }
    @Override protected void init() {
        super.init();
        if (menu.page == LyPage.MAIN) button(0, 203, -6, 65, 22, "maintradebuttona", "gui.thermal_shock_reforged.button_label.maintradebuttona");
        else {
            button(0, 84, 73, 38, 20, "tradebuttona", "gui.thermal_shock_reforged.button_label.buy");
            button(1, 167, 50, 14, 21, "adamantine_spice", "gui.thermal_shock_reforged.button_label.nextbuttona");
            button(2, 25, 50, 14, 21, "tabcycleleft", "gui.thermal_shock_reforged.button_label.previous");
        }
    }
    private void button(int action, int x, int y, int width, int height, String texture, String labelKey) { addRenderableWidget(new ImageButton(action, x, y, width, height, texture, Component.translatable(labelKey))); }
    @Override protected void containerTick() { if (menu.page == LyPage.MAIN) clock.frame = clock.frame > 12 ? 0 : clock.frame + 1; }
    @Override public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) { super.render(graphics, mouseX, mouseY, partialTick); renderTooltip(graphics, mouseX, mouseY); if (menu.getCarried().isEmpty() && hoveredSlot == null) com.hp.thermalshockreforged.client.npc.NpcIcons.tooltip(graphics, font, menu.page.icon, leftPos + 47, topPos + 26, 16, 16, mouseX, mouseY); if (menu.getCarried().isEmpty()) for (var line : menu.page.lines) if (com.hp.thermalshockreforged.client.npc.NpcTradeLabels.tooltip(graphics, font, line.key(), leftPos + line.x(), topPos + line.y(), mouseX, mouseY)) break; }
    private void blit(GuiGraphics graphics, String texture, int x, int y, int width, int height) { graphics.blit(id(texture), leftPos + x, topPos + y, 0, 0, width, height, width, height); }
    @Override protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        RenderSystem.setShaderColor(1, 1, 1, 1); RenderSystem.enableBlend(); RenderSystem.defaultBlendFunc();
        if (menu.page == LyPage.MAIN) {
            blit(graphics, "mainspeechbox", 0, 103, 276, 276);
            blit(graphics, "mainflavourboxes", 62, -110, 214, 214);
            graphics.blit(id("gubyidleportrait"), leftPos - 24, topPos - 24, 0, Mth.clamp(clock.frame * 128, 0, 1408), 128, 128, 128, 1536);
        } else {
            blit(graphics, "lytrademenu", -20, 0, 246, 180);
            if (!com.hp.thermalshockreforged.client.npc.NpcIcons.draw(graphics, menu.page.icon, leftPos + 47, topPos + 26, 16, 16)) blit(graphics, menu.page.icon, 47, 26, 16, 16);
        }
        RenderSystem.disableBlend();
    }
    @Override protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
        if (menu.page == LyPage.MAIN) graphics.drawString(font, Component.translatable(menu.intro == 1 ? "gui.thermal_shock_reforged.ly_main.intro_1" : menu.intro == 2 ? "gui.thermal_shock_reforged.ly_main.intro_2" : "gui.thermal_shock_reforged.ly_main.intro_fallback"), 9, 112, -1, false);
        for (var line : menu.page.lines) DialogueText.draw(graphics, font, com.hp.thermalshockreforged.client.npc.NpcTradeLabels.text(line.key()), line.x(), line.y(), line.color(), imageWidth, line.spacing());
    }
    private static final class Clock { int frame; }
    private final class ImageButton extends AbstractButton {
        private final int action;
        private final ResourceLocation texture;
        ImageButton(int action, int x, int y, int width, int height, String texture, Component label) { super(leftPos + x, topPos + y, width, height, label); this.action = action; this.texture = id("atlas/imagebutton_" + texture); }
        @Override public void onPress() { if (minecraft != null && minecraft.gameMode != null) minecraft.gameMode.handleInventoryButtonClick(menu.containerId, action); }
        @Override protected void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) { RenderSystem.enableBlend(); RenderSystem.defaultBlendFunc(); graphics.blit(texture, getX(), getY(), 0, isHoveredOrFocused() ? height : 0, width, height, width, height * 2); com.hp.thermalshockreforged.client.ButtonLabels.draw(graphics, texture, getX(), getY(), width, height, isHoveredOrFocused()); RenderSystem.disableBlend(); }
        @Override protected void updateWidgetNarration(NarrationElementOutput output) { defaultButtonNarrationText(output); }
    }
}
