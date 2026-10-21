package com.hp.thermalshockreforged.client.npc.florist;

import com.hp.thermalshockreforged.client.dialogue.DialogueText;
import com.hp.thermalshockreforged.ThermalShockReforged;
import com.hp.thermalshockreforged.content.npc.florist.*;
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

public final class FloristScreen extends AbstractContainerScreen<FloristMenu> {
    private final Clock clock;
    private ImageButton concierge;
    public FloristScreen(FloristMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title); imageWidth = menu.page.width(); imageHeight = menu.page.height();
        clock = Minecraft.getInstance().screen instanceof FloristScreen previous && previous.menu.npcUuid.equals(menu.npcUuid) ? previous.clock : new Clock();
    }
    private static ResourceLocation id(String path) { return ResourceLocation.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, "textures/screens/" + path + ".png"); }
    @Override protected void init() {
        super.init();
        if (menu.page.trade != null) {
            button(0, 84, 73, 38, 20, "tradebuttona", "Buy");
            button(1, 167, 50, 14, 21, "adamantine_spice", "Next");
            button(2, 25, 50, 14, 21, "tabcycleleft", "Previous");
        } else if (menu.page == FloristPage.MAIN) {
            button(0, 203, -6, 65, 22, "maininfobuttona", "Info");
            button(1, 203, 18, 65, 22, "maintradebuttona", "Trade");
        } else if (menu.page == FloristPage.INFO) {
            button(0, 138, 32, 59, 20, "infoinfection", "Infection");
            button(1, 77, 32, 59, 20, "infounette1", "Unette");
            button(2, 15, 32, 59, 20, "infoumabel", "Umabel");
            button(3, 15, 55, 59, 20, "infogardening", "Gardening");
            concierge = button(4, 77, 55, 59, 20, "infoconcierge", "Concierge");
            concierge.visible = menu.conciergeAllowed();
        } else button(0, 237, 161, 38, 20, "nextbuttona", "Next");
    }
    private ImageButton button(int action, int x, int y, int width, int height, String texture, String label) { return addRenderableWidget(new ImageButton(action, x, y, width, height, texture, Component.literal(label))); }
    @Override protected void containerTick() { if (menu.page.trade == null) clock.frame = clock.frame > 12 ? 0 : clock.frame + 1; }
    @Override public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        if (concierge != null) concierge.visible = menu.conciergeAllowed();
        super.render(graphics, mouseX, mouseY, partialTick); renderTooltip(graphics, mouseX, mouseY);
    }
    private void blit(GuiGraphics graphics, String texture, int x, int y, int width, int height) { graphics.blit(id(texture), leftPos + x, topPos + y, 0, 0, width, height, width, height); }
    @Override protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        RenderSystem.setShaderColor(1, 1, 1, 1); RenderSystem.enableBlend(); RenderSystem.defaultBlendFunc();
        if (menu.page.trade != null) {
            blit(graphics, "umabeltrademenu", -20, 0, 246, 180);
            blit(graphics, menu.page.icon, 47, 26, 16, 16);
        } else if (menu.page == FloristPage.INFO) blit(graphics, "umabelinfobox", 0, 0, 212, 116);
        else {
            if (menu.page == FloristPage.MAIN) blit(graphics, "mainflavourboxes", 62, -110, 214, 214);
            else blit(graphics, "mainlabelbox", 62, -30, 134, 134);
            blit(graphics, "mainspeechbox", 0, 103, 276, 276);
            graphics.blit(id("umarubiconidleportrait"), leftPos - 32, topPos - 19, 0, Mth.clamp(clock.frame * 128, 0, 1408), 128, 128, 128, 1536);
        }
        RenderSystem.disableBlend();
    }
    @Override protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
        for (var line : menu.page.lines) DialogueText.draw(graphics, font, com.hp.thermalshockreforged.client.npc.NpcTradeLabels.text(line.key()), line.x(), line.y(), line.color(), imageWidth, line.spacing());
        if (menu.page == FloristPage.MAIN) DialogueText.draw(graphics, font, Component.translatable("gui.thermal_shock_reforged.uma_main.greeting_" + (menu.intro == 1 || menu.intro == 2 ? menu.intro : 0)), 9, 112, -1, imageWidth, 0);
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
