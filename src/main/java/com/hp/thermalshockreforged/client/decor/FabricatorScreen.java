package com.hp.thermalshockreforged.client.decor;

import com.hp.thermalshockreforged.ThermalShockReforged;
import com.hp.thermalshockreforged.content.decor.geo.FabricatorMenu;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public final class FabricatorScreen extends AbstractContainerScreen<FabricatorMenu> {
    private final FabricatorLayout.Layout layout;

    public FabricatorScreen(FabricatorMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
        layout = FabricatorLayout.forPage(menu.page);
        imageWidth = layout.width();
        imageHeight = layout.height();
    }

    @Override protected void init() {
        super.init();
        for (var button : layout.buttons()) addRenderableWidget(new ImageButton(button));
    }

    private static ResourceLocation texture(String path) { return ResourceLocation.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, path); }

    @Override public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        super.render(graphics, mouseX, mouseY, partialTick);
        renderTooltip(graphics, mouseX, mouseY);
    }

    @Override protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        RenderSystem.setShaderColor(1, 1, 1, 1);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        int backgroundWidth = menu.page.isGuide() ? 176 : imageWidth;
        graphics.blit(texture(layout.background()), leftPos + layout.backgroundX(), topPos, 0, 0,
                backgroundWidth, imageHeight, backgroundWidth, imageHeight);
        RenderSystem.disableBlend();
    }

    @Override protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
        for (var label : layout.labels()) graphics.drawString(font, Component.translatable(label.key()), label.x(), label.y(), label.color(), false);
    }

    private final class ImageButton extends AbstractButton {
        private final FabricatorLayout.SpriteButton sprite;
        private final ResourceLocation texture;
        private boolean sent;

        private ImageButton(FabricatorLayout.SpriteButton sprite) {
            super(leftPos + sprite.x(), topPos + sprite.y(), sprite.width(), sprite.height(), Component.translatable(sprite.label()));
            this.sprite = sprite;
            texture = texture(sprite.texture());
        }
        @Override public void onPress() {
            if (sent || minecraft == null || minecraft.gameMode == null) return;
            if (menu.page.isGuide()) sent = true;
            minecraft.gameMode.handleInventoryButtonClick(menu.containerId, sprite.action());
        }
        @Override protected void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            graphics.blit(texture, getX(), getY(), sprite.u(), sprite.v() + (isHoveredOrFocused() ? sprite.hoverV() : 0),
                    width, height, sprite.sheetWidth(), sprite.sheetHeight());
            RenderSystem.disableBlend();
        }
        @Override protected void updateWidgetNarration(NarrationElementOutput output) { defaultButtonNarrationText(output); }
    }
}
