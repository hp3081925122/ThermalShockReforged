package com.hp.thermalshockreforged.client.npc.shutterbug;

import com.hp.thermalshockreforged.client.dialogue.DialogueText;
import com.hp.thermalshockreforged.ThermalShockReforged;
import com.hp.thermalshockreforged.content.npc.shutterbug.*;
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

public final class ShutterbugScreen extends AbstractContainerScreen<ShutterbugMenu> {
    private final Clock clock;
    public ShutterbugScreen(ShutterbugMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
        clock = Minecraft.getInstance().screen instanceof ShutterbugScreen previous && previous.menu.npcUuid.equals(menu.npcUuid) ? previous.clock : new Clock();
    }
    private static Identifier id(String path) { return Identifier.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, "textures/screens/" + path + ".png"); }
    @Override protected void init() {
        super.init();
        if (menu.page == ShutterbugPage.TRADE) button(0, 84, 73, 38, 20, "tradebuttona", "gui.thermal_shock_reforged.button_label.buy");
        else if (menu.page == ShutterbugPage.MAIN) {
            button(0, 203, -6, 65, 22, "maininfobuttona", "gui.thermal_shock_reforged.button_label.maininfobuttona");
            button(1, 203, 18, 65, 22, "maintradebuttona", "gui.thermal_shock_reforged.button_label.maintradebuttona");
            button(2, 203, 42, 65, 22, "mainerrandsbuttona", "gui.thermal_shock_reforged.button_label.mainerrandsbuttona");
            button(3, 203, 65, 65, 22, "maintipbuttona", "gui.thermal_shock_reforged.button_label.maintipbuttona");
        } else if (menu.page == ShutterbugPage.INFO) {
            button(0, 76, 32, 59, 20, "infoshalespring", "gui.thermal_shock_reforged.button_label.infoshalespring");
            button(1, 14, 55, 59, 20, "infoblizzard", "gui.thermal_shock_reforged.button_label.infoblizzard");
            button(2, 14, 32, 59, 20, "infounette", "gui.thermal_shock_reforged.button_label.infounette");
            button(3, 138, 32, 59, 20, "infoinfection", "gui.thermal_shock_reforged.button_label.infoinfection");
            button(4, 76, 55, 59, 20, "inforuby", "gui.thermal_shock_reforged.button_label.inforuby");
            button(5, 138, 55, 59, 20, "infotrain", "gui.thermal_shock_reforged.button_label.infotrain");
        } else button(0, 237, menu.page == ShutterbugPage.INFO_TRAIN ? 162 : 161, 38, 20, "nextbuttona", "gui.thermal_shock_reforged.button_label.nextbuttona");
    }
    private void button(int action, int x, int y, int width, int height, String texture, String labelKey) { addRenderableWidget(new ImageButton(action, x, y, width, height, texture, Component.translatable(labelKey))); }
    @Override protected void containerTick() { if (menu.page != ShutterbugPage.TRADE) clock.frame = clock.frame > 12 ? 0 : clock.frame + 1; }
    @Override public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) { super.extractRenderState(graphics, mouseX, mouseY, partialTick);  if (menu.getCarried().isEmpty()) for (var line : menu.page.lines) if (com.hp.thermalshockreforged.client.npc.NpcTradeLabels.tooltip(graphics, font, line.key(), leftPos + line.x(), topPos + line.y(), mouseX, mouseY)) break; }
    private void blit(GuiGraphicsExtractor graphics, String texture, int x, int y, int width, int height) { graphics.blit(id(texture), leftPos + x, topPos + y, 0, 0, width, height, width, height); }
    @Override public void extractContents(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) { super.extractContents(graphics, mouseX, mouseY, partialTick);
        com.mojang.blaze3d.opengl.GlStateManager._enableBlend(); 
        if (menu.page == ShutterbugPage.TRADE) {
            blit(graphics, "unettetrademenu", -20, 0, 246, 180); blit(graphics, "camera", 47, 26, 16, 16);
        } else if (menu.page == ShutterbugPage.INFO) blit(graphics, "uneinfobox", 0, 0, 212, 116);
        else {
            if (menu.page == ShutterbugPage.MAIN) {
                blit(graphics, "mainspeechbox", 0, 103, 276, 276); blit(graphics, "mainflavourboxes", 62, -110, 214, 214);
            } else {
                blit(graphics, "mainspeechbox", 0, 104, 276, 276); blit(graphics, "mainlabelbox", 62, -29, 134, 134);
            }
            graphics.blit(id("unette" + menu.page.portrait + "portrait"), leftPos - 32, topPos + (menu.page == ShutterbugPage.MAIN ? -18 : -17), 0, Mth.clamp(clock.frame * 128, 0, 1408), 128, 128, 128, 1536);
            if (menu.page == ShutterbugPage.INTRO_5 || menu.page == ShutterbugPage.INTRO_6) blit(graphics, "camera", 233, 33, 16, 16);
        }
        com.mojang.blaze3d.opengl.GlStateManager._disableBlend();
    }
    @Override protected void extractLabels(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
        for (var line : menu.page.lines) DialogueText.draw(graphics, font, com.hp.thermalshockreforged.client.npc.NpcTradeLabels.text(line.key()), line.x(), line.y(), line.color(), imageWidth, line.spacing());
        if (menu.page == ShutterbugPage.MAIN) DialogueText.draw(graphics, font, Component.translatable("gui.thermal_shock_reforged.une_main.greeting_" + (menu.intro == 1 || menu.intro == 2 ? menu.intro : 0)), 9, 112, -1, imageWidth, 0);
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
