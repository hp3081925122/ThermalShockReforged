package com.hp.thermalshockreforged.client.npc.shutterbug;

import com.hp.thermalshockreforged.ThermalShockReforged;
import com.hp.thermalshockreforged.content.npc.shutterbug.*;
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

public final class ShutterbugScreen extends AbstractContainerScreen<ShutterbugMenu> {
    private final Clock clock;
    public ShutterbugScreen(ShutterbugMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title); imageWidth = menu.page.width(); imageHeight = menu.page.height();
        clock = Minecraft.getInstance().screen instanceof ShutterbugScreen previous && previous.menu.npcUuid.equals(menu.npcUuid) ? previous.clock : new Clock();
    }
    private static ResourceLocation id(String path) { return ResourceLocation.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, "textures/screens/" + path + ".png"); }
    @Override protected void init() {
        super.init();
        if (menu.page == ShutterbugPage.TRADE) button(0, 84, 73, 38, 20, "tradebuttona", "Buy");
        else if (menu.page == ShutterbugPage.MAIN) {
            button(0, 203, -6, 65, 22, "maininfobuttona", "Info");
            button(1, 203, 18, 65, 22, "maintradebuttona", "Trade");
            button(2, 203, 42, 65, 22, "mainerrandsbuttona", "Errands");
            button(3, 203, 65, 65, 22, "maintipbuttona", "Tips");
        } else if (menu.page == ShutterbugPage.INFO) {
            button(0, 76, 32, 59, 20, "infoshalespring", "Shalespring");
            button(1, 14, 55, 59, 20, "infoblizzard", "Blizzard");
            button(2, 14, 32, 59, 20, "infounette", "Unette");
            button(3, 138, 32, 59, 20, "infoinfection", "Infection");
            button(4, 76, 55, 59, 20, "inforuby", "Ruby");
            button(5, 138, 55, 59, 20, "infotrain", "Train");
        } else button(0, 237, menu.page == ShutterbugPage.INFO_TRAIN ? 162 : 161, 38, 20, "nextbuttona", "Next");
    }
    private void button(int action, int x, int y, int width, int height, String texture, String label) { addRenderableWidget(new ImageButton(action, x, y, width, height, texture, Component.literal(label))); }
    @Override protected void containerTick() { if (menu.page != ShutterbugPage.TRADE) clock.frame = clock.frame > 12 ? 0 : clock.frame + 1; }
    @Override public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) { super.render(graphics, mouseX, mouseY, partialTick); renderTooltip(graphics, mouseX, mouseY); }
    private void blit(GuiGraphics graphics, String texture, int x, int y, int width, int height) { graphics.blit(id(texture), leftPos + x, topPos + y, 0, 0, width, height, width, height); }
    @Override protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        RenderSystem.setShaderColor(1, 1, 1, 1); RenderSystem.enableBlend(); RenderSystem.defaultBlendFunc();
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
        RenderSystem.disableBlend();
    }
    @Override protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
        for (var line : menu.page.lines) graphics.drawString(font, Component.translatable(line.key()), line.x(), line.y(), line.color(), false);
        if (menu.page == ShutterbugPage.MAIN) graphics.drawString(font, menu.intro == 1 ? "❄ Hello again, Stranger!" : menu.intro == 2 ? "❄ Hey, bro... Bro? Sis?? hm." : "❄ kutty", 9, 112, -1, false);
    }
    private static final class Clock { int frame; }
    private final class ImageButton extends AbstractButton {
        private final int action;
        private final ResourceLocation texture;
        ImageButton(int action, int x, int y, int width, int height, String texture, Component label) { super(leftPos + x, topPos + y, width, height, label); this.action = action; this.texture = id("atlas/imagebutton_" + texture); }
        @Override public void onPress() { if (minecraft != null && minecraft.gameMode != null) minecraft.gameMode.handleInventoryButtonClick(menu.containerId, action); }
        @Override protected void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) { RenderSystem.enableBlend(); RenderSystem.defaultBlendFunc(); graphics.blit(texture, getX(), getY(), 0, isHoveredOrFocused() ? height : 0, width, height, width, height * 2); RenderSystem.disableBlend(); }
        @Override protected void updateWidgetNarration(NarrationElementOutput output) { defaultButtonNarrationText(output); }
    }
}
