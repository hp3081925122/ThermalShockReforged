package com.hp.thermalshockreforged.client.npc.florist;

import com.hp.thermalshockreforged.client.dialogue.DialogueText;
import com.hp.thermalshockreforged.ThermalShockReforged;
import com.hp.thermalshockreforged.content.npc.florist.*;
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

public final class FloristScreen extends AbstractContainerScreen<FloristMenu> {
    private final Clock clock;
    private ImageButton concierge;
    public FloristScreen(FloristMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
        clock = Minecraft.getInstance().screen instanceof FloristScreen previous && previous.menu.npcUuid.equals(menu.npcUuid) ? previous.clock : new Clock();
    }
    private static Identifier id(String path) { return Identifier.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, "textures/screens/" + path + ".png"); }
    @Override protected void init() {
        super.init();
        if (menu.page.trade != null) {
            button(0, 84, 73, 38, 20, "tradebuttona", "gui.thermal_shock_reforged.button_label.buy");
            button(1, 167, 50, 14, 21, "adamantine_spice", "gui.thermal_shock_reforged.button_label.nextbuttona");
            button(2, 25, 50, 14, 21, "tabcycleleft", "gui.thermal_shock_reforged.button_label.previous");
        } else if (menu.page == FloristPage.MAIN) {
            button(0, 203, -6, 65, 22, "maininfobuttona", "gui.thermal_shock_reforged.button_label.maininfobuttona");
            button(1, 203, 18, 65, 22, "maintradebuttona", "gui.thermal_shock_reforged.button_label.maintradebuttona");
        } else if (menu.page == FloristPage.INFO) {
            button(0, 138, 32, 59, 20, "infoinfection", "gui.thermal_shock_reforged.button_label.infoinfection");
            button(1, 77, 32, 59, 20, "infounette1", "gui.thermal_shock_reforged.button_label.infounette1");
            button(2, 15, 32, 59, 20, "infoumabel", "gui.thermal_shock_reforged.button_label.infoumabel");
            button(3, 15, 55, 59, 20, "infogardening", "gui.thermal_shock_reforged.button_label.infogardening");
            concierge = button(4, 77, 55, 59, 20, "infoconcierge", "gui.thermal_shock_reforged.button_label.infoconcierge");
            concierge.visible = menu.conciergeAllowed();
        } else button(0, 237, 161, 38, 20, "nextbuttona", "gui.thermal_shock_reforged.button_label.nextbuttona");
    }
    private ImageButton button(int action, int x, int y, int width, int height, String texture, String labelKey) { return addRenderableWidget(new ImageButton(action, x, y, width, height, texture, Component.translatable(labelKey))); }
    @Override protected void containerTick() { if (menu.page.trade == null) clock.frame = clock.frame > 12 ? 0 : clock.frame + 1; }
    @Override public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        if (concierge != null) concierge.visible = menu.conciergeAllowed();
        super.extractRenderState(graphics, mouseX, mouseY, partialTick);  if (menu.getCarried().isEmpty() && hoveredSlot == null) com.hp.thermalshockreforged.client.npc.NpcIcons.tooltip(graphics, font, menu.page.icon, leftPos + 47, topPos + 26, 16, 16, mouseX, mouseY); if (menu.getCarried().isEmpty()) for (var line : menu.page.lines) if (com.hp.thermalshockreforged.client.npc.NpcTradeLabels.tooltip(graphics, font, line.key(), leftPos + line.x(), topPos + line.y(), mouseX, mouseY)) break;
    }
    private void blit(GuiGraphicsExtractor graphics, String texture, int x, int y, int width, int height) { graphics.blit(id(texture), leftPos + x, topPos + y, 0, 0, width, height, width, height); }
    @Override public void extractContents(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) { super.extractContents(graphics, mouseX, mouseY, partialTick);
        com.mojang.blaze3d.opengl.GlStateManager._enableBlend(); 
        if (menu.page.trade != null) {
            blit(graphics, "umabeltrademenu", -20, 0, 246, 180);
            if (!com.hp.thermalshockreforged.client.npc.NpcIcons.draw(graphics, menu.page.icon, leftPos + 47, topPos + 26, 16, 16)) blit(graphics, menu.page.icon, 47, 26, 16, 16);
        } else if (menu.page == FloristPage.INFO) blit(graphics, "umabelinfobox", 0, 0, 212, 116);
        else {
            if (menu.page == FloristPage.MAIN) blit(graphics, "mainflavourboxes", 62, -110, 214, 214);
            else blit(graphics, "mainlabelbox", 62, -30, 134, 134);
            blit(graphics, "mainspeechbox", 0, 103, 276, 276);
            graphics.blit(id("umarubiconidleportrait"), leftPos - 32, topPos - 19, 0, Mth.clamp(clock.frame * 128, 0, 1408), 128, 128, 128, 1536);
        }
        com.mojang.blaze3d.opengl.GlStateManager._disableBlend();
    }
    @Override protected void extractLabels(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
        for (var line : menu.page.lines) DialogueText.draw(graphics, font, com.hp.thermalshockreforged.client.npc.NpcTradeLabels.text(line.key()), line.x(), line.y(), line.color(), imageWidth, line.spacing());
        if (menu.page == FloristPage.MAIN) DialogueText.draw(graphics, font, Component.translatable("gui.thermal_shock_reforged.uma_main.greeting_" + (menu.intro == 1 || menu.intro == 2 ? menu.intro : 0)), 9, 112, -1, imageWidth, 0);
    }
    private static final class Clock { int frame; }
    private final class ImageButton extends AbstractButton {
        private final int action;
        private final Identifier texture;
        ImageButton(int action, int x, int y, int width, int height, String texture, Component label) { super(leftPos + x, topPos + y, width, height, label); this.action = action; this.texture = id("atlas/imagebutton_" + texture); }
        @Override public void onPress(net.minecraft.client.input.InputWithModifiers input) { if (minecraft != null && minecraft.gameMode != null) minecraft.gameMode.handleInventoryButtonClick(menu.containerId, action); }
        @Override protected void extractContents(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) { com.mojang.blaze3d.opengl.GlStateManager._enableBlend();  graphics.blit(texture, getX(), getY(), 0, isHoveredOrFocused() ? height : 0, width, height, width, height * 2); com.hp.thermalshockreforged.client.ButtonLabels.draw(graphics, texture, getX(), getY(), width, height, isHoveredOrFocused()); com.mojang.blaze3d.opengl.GlStateManager._disableBlend(); }
        @Override protected void updateWidgetNarration(NarrationElementOutput output) { defaultButtonNarrationText(output); }
    }
}
