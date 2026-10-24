package com.hp.thermalshockreforged.client.npc.portrait;

import com.hp.thermalshockreforged.ThermalShockReforged;
import com.hp.thermalshockreforged.client.dialogue.DialogueText;
import com.hp.thermalshockreforged.content.npc.portrait.PortraitMenu;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;

public final class PortraitScreen extends AbstractContainerScreen<PortraitMenu> {
    public PortraitScreen(PortraitMenu menu, Inventory inventory, Component title) { super(menu, inventory, title, 276, 182); }
    @Override public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) { super.extractRenderState(graphics, mouseX, mouseY, partialTick);  }
    @Override public void extractContents(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) { super.extractContents(graphics, mouseX, mouseY, partialTick);
        
        graphics.blit(net.minecraft.client.renderer.RenderPipelines.GUI_TEXTURED, Identifier.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, "textures/screens/mainspeechbox.png"), leftPos, topPos + 103, 0, 0, 276, 276, 276, 276);
        graphics.blit(net.minecraft.client.renderer.RenderPipelines.GUI_TEXTURED, Identifier.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, "textures/screens/mainlabelbox.png"), leftPos, topPos - 30, 0, 0, 134, 134, 134, 134);
        
    }
    private Component text(String key) { return Component.translatable("gui." + ThermalShockReforged.MOD_ID + ".laven_civil_" + (menu.ibex ? "2" : "1") + "_tlk.label_" + key); }
    private Component dialogue() {
        var result = Component.empty().append(text("kutty")).append(Component.literal("\n"));
        result.append(text(menu.ibex ? "shocked_dialogue" : "supporter_dialogue"));
        if (!menu.ibex) result.append(Component.literal("\n")).append(text("spot_dialogue"));
        return result;
    }
    @Override protected void extractLabels(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
        graphics.text(font, text("concierge"), 5, 93, -1, false);
        DialogueText.draw(graphics, font, dialogue(), 9, 112, -1, imageWidth, 0);
    }
}
