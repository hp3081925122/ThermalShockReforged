package com.hp.thermalshockreforged.client.legacyfabricator;

import com.hp.thermalshockreforged.ThermalShockReforged;
import com.hp.thermalshockreforged.content.legacyfabricator.LegacyFabricatorContent;
import com.hp.thermalshockreforged.content.legacyfabricator.LegacyFabricatorMenu;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;

public final class LegacyFabricatorScreen extends AbstractContainerScreen<LegacyFabricatorMenu> {
    private static final ResourceLocation BACKGROUND = resource("textures/screens/fabricator_panel.png");
    private static final ResourceLocation CRAFT = resource("textures/screens/atlas/imagebutton_fabricator_craft_button.png");

    public LegacyFabricatorScreen(LegacyFabricatorMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
        imageWidth = 246;
        imageHeight = 163;
    }

    public static void register(RegisterMenuScreensEvent event) {
        event.register(LegacyFabricatorContent.ENDER_TENTACLE.get(), LegacyFabricatorScreen::new);
        event.register(LegacyFabricatorContent.GHOSTGLASS_SWORD.get(), LegacyFabricatorScreen::new);
    }

    private static ResourceLocation resource(String path) { return ResourceLocation.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, path); }

    @Override protected void init() {
        super.init();
        if (menu.page == LegacyFabricatorContent.Page.ENDER_TENTACLE) addRenderableWidget(new CraftButton());
    }

    @Override public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        super.render(graphics, mouseX, mouseY, partialTick);
        renderTooltip(graphics, mouseX, mouseY);
    }

    @Override protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        RenderSystem.setShaderColor(1, 1, 1, 1);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        graphics.blit(BACKGROUND, leftPos, topPos, 0, 0, 246, 163, 246, 163);
        RenderSystem.disableBlend();
    }

    private void label(GuiGraphics graphics, String suffix, int x, int y) {
        graphics.drawString(font, Component.translatable("gui.thermal_shock_reforged." + menu.page.id + "." + suffix), x, y, -1, false);
    }

    @Override protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
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
        @Override public void onPress() {
            if (minecraft != null && minecraft.gameMode != null) minecraft.gameMode.handleInventoryButtonClick(menu.containerId, 0);
        }
        @Override protected void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            graphics.blit(CRAFT, getX(), getY(), 0, isHoveredOrFocused() ? 18 : 0, 40, 18, 40, 36); com.hp.thermalshockreforged.client.ButtonLabels.draw(graphics, CRAFT, getX(), getY(), 40, 18, isHoveredOrFocused());
            RenderSystem.disableBlend();
        }
        @Override protected void updateWidgetNarration(NarrationElementOutput output) { defaultButtonNarrationText(output); }
    }
}
