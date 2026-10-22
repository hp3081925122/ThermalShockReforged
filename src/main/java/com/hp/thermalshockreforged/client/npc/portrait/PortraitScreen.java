package com.hp.thermalshockreforged.client.npc.portrait;

import com.hp.thermalshockreforged.ThermalShockReforged;
import com.hp.thermalshockreforged.client.dialogue.DialogueText;
import com.hp.thermalshockreforged.content.npc.portrait.PortraitMenu;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public final class PortraitScreen extends AbstractContainerScreen<PortraitMenu> {
    public PortraitScreen(PortraitMenu menu, Inventory inventory, Component title) { super(menu, inventory, title); imageWidth = 276; imageHeight = 182; }
    @Override public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) { super.render(graphics, mouseX, mouseY, partialTick); renderTooltip(graphics, mouseX, mouseY); }
    @Override protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        RenderSystem.setShaderColor(1, 1, 1, 1); RenderSystem.enableBlend(); RenderSystem.defaultBlendFunc();
        graphics.blit(ResourceLocation.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, "textures/screens/mainspeechbox.png"), leftPos, topPos + 103, 0, 0, 276, 276, 276, 276);
        graphics.blit(ResourceLocation.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, "textures/screens/mainlabelbox.png"), leftPos, topPos - 30, 0, 0, 134, 134, 134, 134);
        RenderSystem.disableBlend();
    }
    private Component text(String key) { return Component.translatable("gui." + ThermalShockReforged.MOD_ID + ".laven_civil_" + (menu.ibex ? "2" : "1") + "_tlk.label_" + key); }
    private Component dialogue() {
        var result = Component.empty().append(text("kutty")).append(Component.literal("\n"));
        result.append(text(menu.ibex ? "shocked_dialogue" : "supporter_dialogue"));
        if (!menu.ibex) result.append(Component.literal("\n")).append(text("spot_dialogue"));
        return result;
    }
    @Override protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
        graphics.drawString(font, text("concierge"), 5, 93, -1, false);
        DialogueText.draw(graphics, font, dialogue(), 9, 112, -1, imageWidth, 0);
    }
}
