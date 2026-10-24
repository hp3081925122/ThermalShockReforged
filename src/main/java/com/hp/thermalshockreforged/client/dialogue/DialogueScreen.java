package com.hp.thermalshockreforged.client.dialogue;

import com.hp.thermalshockreforged.ThermalShockReforged;
import com.hp.thermalshockreforged.content.dialogue.*;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;

public final class DialogueScreen extends AbstractContainerScreen<DialogueMenu> {
    private static final Identifier SPEECH = Identifier.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, "textures/screens/mainspeechbox.png");
    private static final Identifier LABEL = Identifier.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, "textures/screens/mainlabelbox.png");
    public DialogueScreen(DialogueMenu menu, Inventory inventory, Component title) { super(menu, inventory, title, 276, 182); }
    public static void register(RegisterMenuScreensEvent event) { for (var page : DialoguePage.values()) event.register(DialogueContent.type(page), DialogueScreen::new); }
    @Override public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) { super.extractRenderState(graphics, mouseX, mouseY, partialTick);  }
    @Override public void extractContents(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) { super.extractContents(graphics, mouseX, mouseY, partialTick);
        
        graphics.blit(net.minecraft.client.renderer.RenderPipelines.GUI_TEXTURED, SPEECH, leftPos, topPos + 103, 0, 0, 276, 276, 276, 276);
        graphics.blit(net.minecraft.client.renderer.RenderPipelines.GUI_TEXTURED, LABEL, leftPos, topPos - 30, 0, 0, 134, 134, 134, 134);
        
    }
    @Override protected void extractLabels(GuiGraphicsExtractor graphics, int mouseX, int mouseY) { for (var line : menu.page.lines) DialogueText.draw(graphics, font, Component.translatable(line.key()), line.x(), line.y(), -1, imageWidth, line.spacing()); }
}
