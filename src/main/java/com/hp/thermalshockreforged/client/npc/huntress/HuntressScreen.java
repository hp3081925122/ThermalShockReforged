package com.hp.thermalshockreforged.client.npc.huntress;

import com.hp.thermalshockreforged.client.dialogue.DialogueText;
import com.hp.thermalshockreforged.content.npc.huntress.*;
import com.hp.thermalshockreforged.entity.huntress.HuntressContent;
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

public final class HuntressScreen extends AbstractContainerScreen<HuntressMenu> {
    private final Clock clock;
    public HuntressScreen(HuntressMenu menu, Inventory inventory, Component title) { super(menu, inventory, title); imageWidth = 276; imageHeight = 182; clock = Minecraft.getInstance().screen instanceof HuntressScreen previous && previous.menu.npcUuid.equals(menu.npcUuid) ? previous.clock : new Clock(); }
    public static void register(RegisterMenuScreensEvent event) { for (var page : HuntressPage.values()) event.register(HuntressMenus.menu(page), HuntressScreen::new); }
    private static ResourceLocation texture(String name) { return HuntressContent.id("textures/screens/" + name + ".png"); }
    @Override protected void init() { super.init(); for (var button : menu.page.buttons) addRenderableWidget(new ImageButton(button)); }
    @Override protected void containerTick() { clock.frame = clock.frame > 12 ? 0 : clock.frame + 1; }
    @Override public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) { super.render(graphics, mouseX, mouseY, partialTick); renderTooltip(graphics, mouseX, mouseY); }
    @Override protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        RenderSystem.setShaderColor(1, 1, 1, 1); RenderSystem.enableBlend(); RenderSystem.defaultBlendFunc();
        graphics.blit(texture("mainspeechbox"), leftPos, topPos + 103, 0, 0, 276, 276, 276, 276);
        if (menu.page == HuntressPage.MAIN) graphics.blit(texture("mainflavourboxes"), leftPos + 62, topPos - 110, 0, 0, 214, 214, 214, 214);
        else graphics.blit(texture("mainlabelbox"), leftPos + 62, topPos - 30, 0, 0, 134, 134, 134, 134);
        graphics.blit(texture("huntressidleportrait"), leftPos - 29, topPos - 23, 0, Mth.clamp(clock.frame * 128, 0, 1408), 128, 128, 128, 1536);
        RenderSystem.disableBlend();
    }
    @Override protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) { if (menu.page == HuntressPage.MAIN) DialogueText.draw(graphics, font, Component.translatable("gui.thermal_shock_reforged.huntress.intro_" + (menu.intro == 1 ? "1" : menu.intro == 2 ? "2" : "fallback")), 9, 112, -1, imageWidth, 0); for (var line : menu.page.lines) DialogueText.draw(graphics, font, Component.translatable(line.key()), line.x(), line.y(), line.color(), imageWidth, line.spacing()); }
    private static final class Clock { int frame; }
    private final class ImageButton extends AbstractButton {
        private final int action;
        private final ResourceLocation texture;
        ImageButton(HuntressPage.Button button) { super(leftPos + button.x(), topPos + button.y(), button.width(), button.height(), Component.translatable("gui.thermal_shock_reforged.huntress.button_" + button.texture())); action = button.action(); texture = texture("atlas/imagebutton_" + button.texture()); }
        @Override public void onPress() { if (action >= 0 && minecraft != null && minecraft.gameMode != null) minecraft.gameMode.handleInventoryButtonClick(menu.containerId, action); }
        @Override protected void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) { RenderSystem.enableBlend(); RenderSystem.defaultBlendFunc(); graphics.blit(texture, getX(), getY(), 0, isHoveredOrFocused() ? height : 0, width, height, width, height * 2); com.hp.thermalshockreforged.client.ButtonLabels.draw(graphics, texture, getX(), getY(), width, height, isHoveredOrFocused()); RenderSystem.disableBlend(); }
        @Override protected void updateWidgetNarration(NarrationElementOutput output) { defaultButtonNarrationText(output); }
    }
}
