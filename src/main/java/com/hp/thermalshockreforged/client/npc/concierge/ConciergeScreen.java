package com.hp.thermalshockreforged.client.npc.concierge;

import com.hp.thermalshockreforged.client.dialogue.DialogueText;
import com.hp.thermalshockreforged.content.npc.concierge.*;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;

public final class ConciergeScreen extends AbstractContainerScreen<ConciergeMenu> {
    private final Clock clock;
    private ImageButton vilebath;
    public ConciergeScreen(ConciergeMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
        clock = Minecraft.getInstance().screen instanceof ConciergeScreen previous && previous.menu.npcUuid.equals(menu.npcUuid) ? previous.clock : new Clock();
        var level = Minecraft.getInstance().level;
        var sound = BuiltInRegistries.SOUND_EVENT.getValue(ConciergeContent.id("thermalshockjingle"));
        if (menu.page == ConciergePage.FINISH && level != null && sound != null) level.playLocalSound(menu.anchor.getX(), menu.anchor.getY(), menu.anchor.getZ(), sound, SoundSource.NEUTRAL, 1, 1, false);
    }
    private static Identifier texture(String name) { return ConciergeContent.id("textures/screens/" + name + ".png"); }
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
    @Override public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        super.extractRenderState(graphics, mouseX, mouseY, partialTick);  if (menu.getCarried().isEmpty() && hoveredSlot == null) for (var image : menu.page.images) if (com.hp.thermalshockreforged.client.npc.NpcIcons.tooltip(graphics, font, image.texture(), leftPos + image.x(), topPos + image.y(), image.width(), image.height(), mouseX, mouseY)) break; if (menu.getCarried().isEmpty()) for (var line : menu.page.lines) if (com.hp.thermalshockreforged.client.npc.NpcTradeLabels.tooltip(graphics, font, line.key(), leftPos + line.x(), topPos + line.y(), mouseX, mouseY)) break;
        if (menu.page == ConciergePage.ERRAND_3 && mouseX >= leftPos + 229 && mouseX <= leftPos + 253 && mouseY >= topPos + 25 && mouseY <= topPos + 49) graphics.setTooltipForNextFrame(font, Component.translatable("gui.thermal_shock_reforged.conc_errand_1_epl_3.tooltip_primordial_blood"), mouseX, mouseY);
    }
    @Override public void extractContents(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) { super.extractContents(graphics, mouseX, mouseY, partialTick);
        
        for (var image : menu.page.images) if (!com.hp.thermalshockreforged.client.npc.NpcIcons.draw(graphics, image.texture(), leftPos + image.x(), topPos + image.y(), image.width(), image.height())) graphics.blit(net.minecraft.client.renderer.RenderPipelines.GUI_TEXTURED, texture(image.texture()), leftPos + image.x(), topPos + image.y(), 0, image.animated() ? Mth.clamp(clock.frame * 128, 0, 1408) : 0, image.width(), image.height(), image.textureWidth(), image.textureHeight());
        
    }
    @Override protected void extractLabels(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
        if (menu.page == ConciergePage.MAIN) DialogueText.draw(graphics, font, Component.translatable("gui.thermal_shock_reforged.concierge.intro_" + (menu.intro == 1 ? "1" : menu.intro == 2 ? "2" : "fallback")), 9, 112, -1, imageWidth, 0);
        for (var line : menu.page.lines) DialogueText.draw(graphics, font, com.hp.thermalshockreforged.client.npc.NpcTradeLabels.text(line.key()), line.x(), line.y(), line.color(), imageWidth, line.spacing());
    }
    private static final class Clock { int frame; }
    private final class ImageButton extends AbstractButton {
        private final int action;
        private final Identifier texture;
        ImageButton(ConciergePage.Button button) {
            super(leftPos + button.x(), topPos + button.y(), button.width(), button.height(), Component.translatable("gui.thermal_shock_reforged.concierge.button_" + button.texture()));
            action = button.action(); texture = texture("atlas/imagebutton_" + button.texture());
        }
        @Override public void onPress(net.minecraft.client.input.InputWithModifiers input) { if (action >= 0 && minecraft != null && minecraft.gameMode != null) minecraft.gameMode.handleInventoryButtonClick(menu.containerId, action); }
        @Override protected void extractContents(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
            graphics.blit(net.minecraft.client.renderer.RenderPipelines.GUI_TEXTURED, texture, getX(), getY(), 0, isHoveredOrFocused() ? height : 0, width, height, width, height * 2); com.hp.thermalshockreforged.client.ButtonLabels.draw(graphics, texture, getX(), getY(), width, height, isHoveredOrFocused()); 
        }
        @Override protected void updateWidgetNarration(NarrationElementOutput output) { defaultButtonNarrationText(output); }
    }
}
