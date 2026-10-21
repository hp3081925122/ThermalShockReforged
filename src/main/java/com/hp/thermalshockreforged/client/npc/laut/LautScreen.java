package com.hp.thermalshockreforged.client.npc.laut;

import com.hp.thermalshockreforged.client.dialogue.DialogueText;
import com.hp.thermalshockreforged.ThermalShockReforged;
import com.hp.thermalshockreforged.content.npc.laut.*;
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

public final class LautScreen extends AbstractContainerScreen<LautMenu> {
    private static final ResourceLocation SPEECH = id("textures/screens/mainspeechbox.png");
    private static final ResourceLocation LABEL = id("textures/screens/mainlabelbox.png");
    private static final ResourceLocation PORTRAIT = id("textures/screens/lautemote1portrait.png");
    private final Clock clock;
    public LautScreen(LautMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title); imageWidth = 276; imageHeight = 182;
        clock = Minecraft.getInstance().screen instanceof LautScreen previous && previous.menu.npcUuid.equals(menu.npcUuid) ? previous.clock : new Clock();
    }
    private static ResourceLocation id(String path) { return ResourceLocation.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, path); }
    @Override protected void init() {
        super.init();
        if (menu.page == LautPage.HAVE) addRenderableWidget(new Button(0, 237, 162, 38, "nextbuttona", Component.translatable("gui.proceed")));
        if (menu.page == LautPage.CONFIRM) {
            addRenderableWidget(new Button(0, 107, 87, 20, "yesbuttonb", Component.translatable("gui.yes")));
            addRenderableWidget(new Button(1, 147, 87, 20, "nobuttonb", Component.translatable("gui.no")));
        }
    }
    @Override protected void containerTick() { if (menu.page != LautPage.CONFIRM) clock.frame = clock.frame > 12 ? 0 : clock.frame + 1; }
    @Override public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) { super.render(graphics, mouseX, mouseY, partialTick); renderTooltip(graphics, mouseX, mouseY); }
    @Override protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        RenderSystem.setShaderColor(1, 1, 1, 1); RenderSystem.enableBlend(); RenderSystem.defaultBlendFunc();
        graphics.blit(SPEECH, leftPos, topPos + (menu.page == LautPage.CONFIRM ? 50 : 104), 0, 0, 276, 276, 276, 276);
        if (menu.page != LautPage.CONFIRM) graphics.blit(LABEL, leftPos + 62, topPos - 29, 0, 0, 134, 134, 134, 134);
        if (menu.page.portrait()) graphics.blit(PORTRAIT, leftPos - 32, topPos - 23, 0, Mth.clamp(clock.frame * 128, 0, 1408), 128, 128, 128, 1536);
        RenderSystem.disableBlend();
    }
    @Override protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) { for (var line : menu.page.lines) DialogueText.draw(graphics, font, Component.translatable(line.key()), line.x(), line.y(), line.color(), imageWidth, line.spacing()); }
    private static final class Clock { int frame; }
    private final class Button extends AbstractButton {
        private final int action;
        private final ResourceLocation texture;
        Button(int action, int x, int y, int width, String name, Component label) { super(leftPos + x, topPos + y, width, 20, label); this.action = action; texture = id("textures/screens/atlas/imagebutton_" + name + ".png"); }
        @Override public void onPress() { if (minecraft != null && minecraft.gameMode != null) minecraft.gameMode.handleInventoryButtonClick(menu.containerId, action); }
        @Override protected void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) { RenderSystem.enableBlend(); RenderSystem.defaultBlendFunc(); graphics.blit(texture, getX(), getY(), 0, isHoveredOrFocused() ? 20 : 0, width, 20, width, 40); RenderSystem.disableBlend(); }
        @Override protected void updateWidgetNarration(NarrationElementOutput output) { defaultButtonNarrationText(output); }
    }
}
