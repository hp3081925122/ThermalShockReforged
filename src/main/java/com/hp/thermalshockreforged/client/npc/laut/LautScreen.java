package com.hp.thermalshockreforged.client.npc.laut;

import com.hp.thermalshockreforged.client.dialogue.DialogueText;
import com.hp.thermalshockreforged.ThermalShockReforged;
import com.hp.thermalshockreforged.content.npc.laut.*;
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

public final class LautScreen extends AbstractContainerScreen<LautMenu> {
    private static final Identifier SPEECH = id("textures/screens/mainspeechbox.png");
    private static final Identifier LABEL = id("textures/screens/mainlabelbox.png");
    private static final Identifier PORTRAIT = id("textures/screens/lautemote1portrait.png");
    private final Clock clock;
    public LautScreen(LautMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title, 276, 182);
        clock = Minecraft.getInstance().screen instanceof LautScreen previous && previous.menu.npcUuid.equals(menu.npcUuid) ? previous.clock : new Clock();
    }
    private static Identifier id(String path) { return Identifier.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, path); }
    @Override protected void init() {
        super.init();
        if (menu.page == LautPage.HAVE) addRenderableWidget(new Button(0, 237, 162, 38, "nextbuttona", Component.translatable("gui.proceed")));
        if (menu.page == LautPage.CONFIRM) {
            addRenderableWidget(new Button(0, 107, 87, 20, "yesbuttonb", Component.translatable("gui.yes")));
            addRenderableWidget(new Button(1, 147, 87, 20, "nobuttonb", Component.translatable("gui.no")));
        }
    }
    @Override protected void containerTick() { if (menu.page != LautPage.CONFIRM) clock.frame = clock.frame > 12 ? 0 : clock.frame + 1; }
    @Override public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) { super.extractRenderState(graphics, mouseX, mouseY, partialTick);  }
    @Override public void extractContents(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) { super.extractContents(graphics, mouseX, mouseY, partialTick);
        
        graphics.blit(net.minecraft.client.renderer.RenderPipelines.GUI_TEXTURED, SPEECH, leftPos, topPos + (menu.page == LautPage.CONFIRM ? 50 : 104), 0, 0, 276, 276, 276, 276);
        if (menu.page != LautPage.CONFIRM) graphics.blit(net.minecraft.client.renderer.RenderPipelines.GUI_TEXTURED, LABEL, leftPos + 62, topPos - 29, 0, 0, 134, 134, 134, 134);
        if (menu.page.portrait()) graphics.blit(net.minecraft.client.renderer.RenderPipelines.GUI_TEXTURED, PORTRAIT, leftPos - 32, topPos - 23, 0, Mth.clamp(clock.frame * 128, 0, 1408), 128, 128, 128, 1536);
        
    }
    @Override protected void extractLabels(GuiGraphicsExtractor graphics, int mouseX, int mouseY) { for (var line : menu.page.lines) DialogueText.draw(graphics, font, Component.translatable(line.key()), line.x(), line.y(), line.color(), imageWidth, line.spacing()); }
    private static final class Clock { int frame; }
    private final class Button extends AbstractButton {
        private final int action;
        private final Identifier texture;
        Button(int action, int x, int y, int width, String name, Component label) { super(leftPos + x, topPos + y, width, 20, label); this.action = action; texture = id("textures/screens/atlas/imagebutton_" + name + ".png"); }
        @Override public void onPress(net.minecraft.client.input.InputWithModifiers input) { if (minecraft != null && minecraft.gameMode != null) minecraft.gameMode.handleInventoryButtonClick(menu.containerId, action); }
        @Override protected void extractContents(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) { graphics.blit(net.minecraft.client.renderer.RenderPipelines.GUI_TEXTURED, texture, getX(), getY(), 0, isHoveredOrFocused() ? 20 : 0, width, 20, width, 40); com.hp.thermalshockreforged.client.ButtonLabels.draw(graphics, texture, getX(), getY(), width, 20, isHoveredOrFocused()); }
        @Override protected void updateWidgetNarration(NarrationElementOutput output) { defaultButtonNarrationText(output); }
    }
}
