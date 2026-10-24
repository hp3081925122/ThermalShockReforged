package com.hp.thermalshockreforged.client.remaininggui;

import com.hp.thermalshockreforged.content.remaininggui.RemainingGuiContent;
import com.hp.thermalshockreforged.content.remaininggui.RemainingGuiMenu;
import com.hp.thermalshockreforged.content.remaininggui.RemainingGuiPage;
import com.hp.thermalshockreforged.content.remaininggui.VaultCodePayload;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;

public final class RemainingGuiScreen extends AbstractContainerScreen<RemainingGuiMenu> {
    private EditBox vaultCode;
    private int speechFrame;

    public RemainingGuiScreen(RemainingGuiMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
    }

    public static void register(RegisterMenuScreensEvent event) {
        for (var page : RemainingGuiPage.values()) event.register(RemainingGuiContent.type(page), RemainingGuiScreen::new);
    }

    private static Identifier texture(String name) { return RemainingGuiContent.id("textures/screens/" + name + ".png"); }

    @Override protected void init() {
        super.init();
        if (menu.page == RemainingGuiPage.LIMBO_VAULT_GUI) {
            var hint = Component.translatable(menu.page.translation("vaultcodeslot"));
            vaultCode = new EditBox(font, leftPos + 81, topPos + 31, 118, 18, hint);
            vaultCode.setMaxLength(Short.MAX_VALUE);
            vaultCode.setSuggestion(hint.getString());
            vaultCode.setResponder(value -> vaultCode.setSuggestion(value.isEmpty() ? hint.getString() : null));
            addWidget(vaultCode);
        }
        for (var button : menu.page.buttons) addRenderableWidget(new ImageButton(button));
    }

    @Override protected void containerTick() {
        super.containerTick();
        if (menu.page == RemainingGuiPage.CIVIL_8_TLK_2) speechFrame = speechFrame > 12 ? 0 : speechFrame + 1;
    }

    @Override public void resize(int width, int height) {
        String value = vaultCode == null ? null : vaultCode.getValue();
        super.resize(width, height);
        if (value != null && vaultCode != null) vaultCode.setValue(value);
    }

    @Override public boolean keyPressed(net.minecraft.client.input.KeyEvent event) {
        if (event.key() == 256 && minecraft != null && minecraft.player != null) { minecraft.player.closeContainer(); return true; }
        if (vaultCode != null && vaultCode.isFocused()) return vaultCode.keyPressed(event);
        return super.keyPressed(event);
    }

    @Override public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        super.extractRenderState(graphics, mouseX, mouseY, partialTick);
        if (vaultCode != null) vaultCode.extractRenderState(graphics, mouseX, mouseY, partialTick);
         if (menu.getCarried().isEmpty() && hoveredSlot == null) for (var image : menu.page.images) if (com.hp.thermalshockreforged.client.npc.NpcIcons.tooltip(graphics, font, image.texture(), leftPos + image.x(), topPos + image.y(), image.width(), image.height(), mouseX, mouseY)) break;
    }

    @Override public void extractContents(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) { super.extractContents(graphics, mouseX, mouseY, partialTick);
        
        
        for (var image : menu.page.images) if (!com.hp.thermalshockreforged.client.npc.NpcIcons.draw(graphics, image.texture(), leftPos + image.x(), topPos + image.y(), image.width(), image.height())) graphics.blit(net.minecraft.client.renderer.RenderPipelines.GUI_TEXTURED, texture(image.texture()), leftPos + image.x(), topPos + image.y(), 0, 0, image.width(), image.height(), image.width(), image.height());
        
    }

    @Override protected void extractLabels(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
        for (var line : menu.page.lines) graphics.text(font, Component.translatable(menu.page.translation(line.suffix())), line.x(), line.y(), line.color(), false);
    }

    private final class ImageButton extends AbstractButton {
        private final RemainingGuiPage.Button button;
        private final Identifier texture;

        ImageButton(RemainingGuiPage.Button button) {
            super(leftPos + button.x(), topPos + button.y(), button.width(), button.height(), Component.translatable("gui.thermal_shock_reforged.remaininggui.button_" + button.texture()));
            this.button = button;
            texture = texture("atlas/imagebutton_" + button.texture());
        }

        @Override public void onPress(net.minecraft.client.input.InputWithModifiers input) {
            if (button.action() < 0 || minecraft == null || minecraft.gameMode == null) return;
            if (menu.page == RemainingGuiPage.LIMBO_VAULT_GUI) net.neoforged.neoforge.client.network.ClientPacketDistributor.sendToServer(new VaultCodePayload(menu.containerId, vaultCode.getValue()));
            else minecraft.gameMode.handleInventoryButtonClick(menu.containerId, button.action());
        }

        @Override protected void extractContents(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
            
            
            graphics.blit(net.minecraft.client.renderer.RenderPipelines.GUI_TEXTURED, texture, getX(), getY(), 0, isHoveredOrFocused() ? height : 0, width, height, width, height * 2); com.hp.thermalshockreforged.client.ButtonLabels.draw(graphics, texture, getX(), getY(), width, height, isHoveredOrFocused());
            
        }

        @Override protected void updateWidgetNarration(NarrationElementOutput output) { defaultButtonNarrationText(output); }
    }
}
