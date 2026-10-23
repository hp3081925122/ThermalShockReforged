package com.hp.thermalshockreforged.client.legacyfabricator;

import com.hp.thermalshockreforged.ThermalShockReforged;
import com.hp.thermalshockreforged.content.legacyfabricator.LegacyFabricatorContent;
import com.hp.thermalshockreforged.content.legacyfabricator.LegacyFabricatorMenu;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;

public final class LegacyFabricatorScreen extends AbstractContainerScreen<LegacyFabricatorMenu> {
    private static final Identifier BACKGROUND = resource("textures/screens/fabricator_panel.png");
    private static final Identifier CRAFT = resource("textures/screens/atlas/imagebutton_fabricator_craft_button.png");

    public LegacyFabricatorScreen(LegacyFabricatorMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title, 246, 163);
    }

    public static void register(RegisterMenuScreensEvent event) {
        event.register(LegacyFabricatorContent.ENDER_TENTACLE.get(), LegacyFabricatorScreen::new);
        event.register(LegacyFabricatorContent.GHOSTGLASS_SWORD.get(), LegacyFabricatorScreen::new);
    }

    private static Identifier resource(String path) { return Identifier.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, path); }

    @Override protected void init() {
        super.init();
        if (menu.page == LegacyFabricatorContent.Page.ENDER_TENTACLE) addRenderableWidget(new CraftButton());
    }

    @Override public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        super.extractRenderState(graphics, mouseX, mouseY, partialTick);
        
    }

    @Override public void extractContents(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) { super.extractContents(graphics, mouseX, mouseY, partialTick);
        com.mojang.blaze3d.opengl.GlStateManager._enableBlend();
        
        graphics.blit(BACKGROUND, leftPos, topPos, 0, 0, 246, 163, 246, 163);
        com.mojang.blaze3d.opengl.GlStateManager._disableBlend();
    }

    private void label(GuiGraphicsExtractor graphics, String suffix, int x, int y) {
        graphics.text(font, Component.translatable("gui.thermal_shock_reforged." + menu.page.id + "." + suffix), x, y, -1, false);
    }

    @Override protected void extractLabels(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
        label(graphics, "label_fabricator_guide", 5, -13);
        label(graphics, "label_slot_1_cryonic_alloy_x5", 14, 14);
        if (menu.page == LegacyFabricatorContent.Page.ENDER_TENTACLE) {
            label(graphics, "label_tools", 167, -13);
            label(graphics, "label_slot_2_cobalt_block_x2", 14, 32);
            label(graphics, "label_slot_3_polished_permafrost_x4", 14, 23);
            label(graphics, "label_cryonic_alloy", 14, 41);
            label(graphics, "label_slag_x1", 14, 50);
        } else {
            label(graphics, "label_to_the_crafting_table", 14, 23);
            label(graphics, "label_check_jei_for_the_new_recipe", 14, 32);
        }
    }

    private final class CraftButton extends AbstractButton {
        private CraftButton() {
            super(leftPos + 174, topPos + 38, 40, 18, Component.translatable("gui.thermal_shock_reforged.fabricator.craft"));
        }
        @Override public void onPress(net.minecraft.client.input.InputWithModifiers input) {
            if (minecraft != null && minecraft.gameMode != null) minecraft.gameMode.handleInventoryButtonClick(menu.containerId, 0);
        }
        @Override protected void extractContents(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
            com.mojang.blaze3d.opengl.GlStateManager._enableBlend();
            
            graphics.blit(CRAFT, getX(), getY(), 0, isHoveredOrFocused() ? 18 : 0, 40, 18, 40, 36); com.hp.thermalshockreforged.client.ButtonLabels.draw(graphics, CRAFT, getX(), getY(), 40, 18, isHoveredOrFocused());
            com.mojang.blaze3d.opengl.GlStateManager._disableBlend();
        }
        @Override protected void updateWidgetNarration(NarrationElementOutput output) { defaultButtonNarrationText(output); }
    }
}
