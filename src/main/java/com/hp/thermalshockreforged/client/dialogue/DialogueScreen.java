package com.hp.thermalshockreforged.client.dialogue;

import com.hp.thermalshockreforged.ThermalShockReforged;
import com.hp.thermalshockreforged.content.dialogue.*;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;

public final class DialogueScreen extends AbstractContainerScreen<DialogueMenu> {
    private static final ResourceLocation SPEECH = ResourceLocation.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, "textures/screens/mainspeechbox.png");
    private static final ResourceLocation LABEL = ResourceLocation.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, "textures/screens/mainlabelbox.png");
    public DialogueScreen(DialogueMenu menu, Inventory inventory, Component title) { super(menu, inventory, title); imageWidth = 276; imageHeight = 182; }
    public static void register(RegisterMenuScreensEvent event) { for (var page : DialoguePage.values()) event.register(DialogueContent.type(page), DialogueScreen::new); }
    @Override public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) { super.render(graphics, mouseX, mouseY, partialTick); renderTooltip(graphics, mouseX, mouseY); }
    @Override protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        RenderSystem.setShaderColor(1, 1, 1, 1); RenderSystem.enableBlend(); RenderSystem.defaultBlendFunc();
        graphics.blit(SPEECH, leftPos, topPos + 103, 0, 0, 276, 276, 276, 276);
        graphics.blit(LABEL, leftPos, topPos - 30, 0, 0, 134, 134, 134, 134);
        RenderSystem.disableBlend();
    }
    @Override protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) { for (var line : menu.page.lines) DialogueText.draw(graphics, font, Component.translatable(line.key()), line.x(), line.y(), -1, imageWidth, line.spacing()); }
}
