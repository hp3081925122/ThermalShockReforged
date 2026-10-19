package com.hp.thermalshockreforged.client.npc.concierge;

import com.hp.thermalshockreforged.content.npc.concierge.*;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;

public final class ConciergeScreen extends AbstractContainerScreen<ConciergeMenu> {
    private final Clock clock;
    private ImageButton vilebath;
    public ConciergeScreen(ConciergeMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title); imageWidth = menu.page.width; imageHeight = menu.page.height;
        clock = Minecraft.getInstance().screen instanceof ConciergeScreen previous && previous.menu.npcUuid.equals(menu.npcUuid) ? previous.clock : new Clock();
        var level = Minecraft.getInstance().level;
        var sound = BuiltInRegistries.SOUND_EVENT.get(ConciergeContent.id("thermalshockjingle"));
        if (menu.page == ConciergePage.FINISH && level != null && sound != null) level.playLocalSound(menu.anchor.getX(), menu.anchor.getY(), menu.anchor.getZ(), sound, SoundSource.NEUTRAL, 1, 1, false);
    }
    private static ResourceLocation texture(String name) { return ConciergeContent.id("textures/screens/" + name + ".png"); }
    @Override protected void init() {
        super.init();
        for (var button : menu.page.buttons) {
            var widget = addRenderableWidget(new ImageButton(button));
            if (menu.page == ConciergePage.INFO && button.action() == 3) { vilebath = widget; widget.visible = menu.vilebathAllowed(); }
        }
    }
    @Override protected void containerTick() {
        if (menu.page.trade == null) clock.frame = clock.frame > 12 ? 0 : clock.frame + 1;
        if (vilebath != null) vilebath.visible = menu.vilebathAllowed();
    }
    @Override public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        super.render(graphics, mouseX, mouseY, partialTick); renderTooltip(graphics, mouseX, mouseY);
        if (menu.page == ConciergePage.ERRAND_3 && mouseX >= leftPos + 229 && mouseX <= leftPos + 253 && mouseY >= topPos + 25 && mouseY <= topPos + 49) graphics.renderTooltip(font, Component.translatable("gui.thermal_shock_reforged.conc_errand_1_epl_3.tooltip_primordial_blood"), mouseX, mouseY);
    }
    @Override protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        RenderSystem.setShaderColor(1, 1, 1, 1); RenderSystem.enableBlend(); RenderSystem.defaultBlendFunc();
        for (var image : menu.page.images) graphics.blit(texture(image.texture()), leftPos + image.x(), topPos + image.y(), 0, image.animated() ? Mth.clamp(clock.frame * 128, 0, 1408) : 0, image.width(), image.height(), image.textureWidth(), image.textureHeight());
        RenderSystem.disableBlend();
    }
    @Override protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
        if (menu.page == ConciergePage.MAIN) graphics.drawString(font, Component.translatable("gui.thermal_shock_reforged.concierge.intro_" + (menu.intro == 1 ? "1" : menu.intro == 2 ? "2" : "fallback")), 9, 112, -1, false);
        for (var line : menu.page.lines) graphics.drawString(font, Component.translatable(line.key()), line.x(), line.y(), line.color(), false);
    }
    private static final class Clock { int frame; }
    private final class ImageButton extends AbstractButton {
        private final int action;
        private final ResourceLocation texture;
        ImageButton(ConciergePage.Button button) {
            super(leftPos + button.x(), topPos + button.y(), button.width(), button.height(), Component.translatable("gui.thermal_shock_reforged.concierge.button_" + button.texture()));
            action = button.action(); texture = texture("atlas/imagebutton_" + button.texture());
        }
        @Override public void onPress() { if (action >= 0 && minecraft != null && minecraft.gameMode != null) minecraft.gameMode.handleInventoryButtonClick(menu.containerId, action); }
        @Override protected void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
            RenderSystem.enableBlend(); RenderSystem.defaultBlendFunc(); graphics.blit(texture, getX(), getY(), 0, isHoveredOrFocused() ? height : 0, width, height, width, height * 2); RenderSystem.disableBlend();
        }
        @Override protected void updateWidgetNarration(NarrationElementOutput output) { defaultButtonNarrationText(output); }
    }
}
