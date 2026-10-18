package com.hp.thermalshockreforged.client.npc.portrait;

import com.hp.thermalshockreforged.ThermalShockReforged;
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
    private void label(GuiGraphics graphics, String key, int x, int y, int color) { graphics.drawString(font, Component.translatable("gui." + ThermalShockReforged.MOD_ID + ".laven_civil_" + (menu.ibex ? "2" : "1") + "_tlk.label_" + key), x, y, color, false); }
    @Override protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
        label(graphics, "kutty", 9, 112, -1);
        label(graphics, "concierge", 5, 93, -1);
        label(graphics, "our_snow_is_really_dusty_for_s", 9, 122, -1);
        if (menu.ibex) label(graphics, "shocked", 129, 122, -16724788);
        else {
            label(graphics, "doesnt_really_stick_together", 9, 132, -1);
            label(graphics, "but_if_i_gave_it_water_itd_f", 9, 142, -1);
            label(graphics, "it_would_hurt_really_bad", 9, 152, -1);
        }
    }
}
