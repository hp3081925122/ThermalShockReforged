package com.hp.thermalshockreforged.client.atlas;

import com.hp.thermalshockreforged.ThermalShockReforged;
import com.hp.thermalshockreforged.content.atlas.AtlasMenu;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;

public final class AtlasScreen extends AbstractContainerScreen<AtlasMenu> {
    private static final Identifier BACKGROUND = id("textures/screens/atlas_rubicon.png");
    public AtlasScreen(AtlasMenu menu, Inventory inventory, Component title) { super(menu, inventory, title, 196, 196); }
    private static Identifier id(String path) { return Identifier.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, path); }
    @Override protected void init() {
        super.init();
        addRenderableWidget(new AtlasButton(0, 43, 83, "atlas_button_inac", Component.translatable("gui.thermal_shock_reforged.atlas_menu.button_loqendia")));
        addRenderableWidget(new AtlasButton(1, 114, 130, "atlas_button_inac1", Component.translatable("generator.minecraft.normal")));
        addRenderableWidget(new AtlasButton(2, 124, 53, "atlas_button_inac2", Component.translatable("gui.thermal_shock_reforged.atlas_menu.button_palace")));
    }
    @Override public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) { super.extractRenderState(graphics, mouseX, mouseY, partialTick);  }
    @Override public void extractContents(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) { super.extractContents(graphics, mouseX, mouseY, partialTick);
        
        graphics.blit(net.minecraft.client.renderer.RenderPipelines.GUI_TEXTURED, BACKGROUND, leftPos + 2, topPos + 2, 0, 0, 192, 192, 192, 192);
        
    }
    @Override protected void extractLabels(GuiGraphicsExtractor graphics, int mouseX, int mouseY) { graphics.text(font, Component.translatable("gui.thermal_shock_reforged.atlas_menu.label_fabricator_guide"), 2, -9, -1, false); }
    private final class AtlasButton extends AbstractButton {
        private final int destination;
        private final Identifier texture;
        AtlasButton(int destination, int x, int y, String texture, Component label) { super(leftPos + x, topPos + y, 20, 20, label); this.destination = destination; this.texture = id("textures/screens/atlas/imagebutton_" + texture + ".png"); }
        @Override public void onPress(net.minecraft.client.input.InputWithModifiers input) { if (minecraft != null && minecraft.gameMode != null) minecraft.gameMode.handleInventoryButtonClick(menu.containerId, destination); }
        @Override protected void extractContents(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
            
            graphics.blit(net.minecraft.client.renderer.RenderPipelines.GUI_TEXTURED, texture, getX(), getY(), 0, isHoveredOrFocused() ? 20 : 0, 20, 20, 20, 40);
            
        }
        @Override protected void updateWidgetNarration(NarrationElementOutput output) { defaultButtonNarrationText(output); }
    }
}
