package com.hp.thermalshockreforged.client.labmachinist;

import com.hp.thermalshockreforged.client.dialogue.DialogueText;
import com.hp.thermalshockreforged.entity.labmachinist.*;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;

public final class LabScreen extends AbstractContainerScreen<LabMenu> implements LabDialogueClock.Owner {
    private final LabDialogueClock clock;
    private boolean soundPlayed;
    public LabScreen(LabMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
        clock = Minecraft.getInstance().screen instanceof LabDialogueClock.Owner previous && previous.dialogueKey().equals(dialogueKey()) ? previous.dialogueClock() : new LabDialogueClock();
    }
    @Override public LabDialogueClock dialogueClock() { return clock; }
    @Override public String dialogueKey() { return menu.npcId >= 0 ? menu.npcUuid.toString() : "block:" + menu.anchor; }
    private static Identifier texture(String name) { return LabContent.id("textures/screens/" + name + ".png"); }
    @Override protected void init() {
        super.init();
        if (!soundPlayed && menu.page.voice && minecraft != null && minecraft.level != null) { minecraft.level.playLocalSound(menu.anchor.getX(), menu.anchor.getY(), menu.anchor.getZ(), net.minecraft.core.registries.BuiltInRegistries.SOUND_EVENT.getValue(LabContent.id(menu.openingSound)), net.minecraft.sounds.SoundSource.NEUTRAL, 1, 1, false); soundPlayed = true; }
        for (var button : menu.page.buttons) addRenderableWidget(new ImageButton(button));
    }
    @Override protected void containerTick() { if (menu.page.clock) clock.frame = clock.frame > 12 ? 0 : clock.frame + 1; }
    @Override public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) { super.extractRenderState(graphics, mouseX, mouseY, partialTick);  if (menu.getCarried().isEmpty() && hoveredSlot == null) for (var image : menu.page.images) if (com.hp.thermalshockreforged.client.npc.NpcIcons.tooltip(graphics, font, image.texture(), leftPos + image.x(), topPos + image.y(), image.width(), image.height(), mouseX, mouseY)) break; if (menu.getCarried().isEmpty()) for (var line : menu.page.lines) if (com.hp.thermalshockreforged.client.npc.NpcTradeLabels.tooltip(graphics, font, line.key(), leftPos + line.x(), topPos + line.y(), mouseX, mouseY)) break; }
    @Override public void extractContents(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) { super.extractContents(graphics, mouseX, mouseY, partialTick);
        
        for (var image : menu.page.images) if (!com.hp.thermalshockreforged.client.npc.NpcIcons.draw(graphics, image.texture(), leftPos + image.x(), topPos + image.y(), image.width(), image.height())) graphics.blit(net.minecraft.client.renderer.RenderPipelines.GUI_TEXTURED, texture(image.texture()), leftPos + image.x(), topPos + image.y(), 0, image.frameMax() < 0 ? 0 : Mth.clamp(clock.frame * 128, 0, image.frameMax()), image.width(), image.height(), image.textureWidth(), image.textureHeight());
        
    }
    @Override protected void extractLabels(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
        for (var line : menu.page.lines) DialogueText.draw(graphics, font, com.hp.thermalshockreforged.client.npc.NpcTradeLabels.text(line.key()), line.x(), line.y(), line.color(), imageWidth, line.spacing());
        if (menu.page == LabPage.MAIN) DialogueText.draw(graphics, font, Component.translatable("gui.thermal_shock_reforged.qo_main.greeting_" + (menu.intro == 1 || menu.intro == 2 ? menu.intro : 0)), 9, 112, -1, imageWidth, 0);
    }
    private final class ImageButton extends AbstractButton {
        private final int action;
        private final Identifier texture;
        ImageButton(LabPage.Button button) { super(leftPos + button.x(), topPos + button.y(), button.width(), button.height(), Component.translatable("gui.thermal_shock_reforged.qo.button_" + button.texture())); action = button.action(); texture = texture("atlas/imagebutton_" + button.texture()); }
        @Override public void onPress(net.minecraft.client.input.InputWithModifiers input) { if (minecraft != null && minecraft.gameMode != null) minecraft.gameMode.handleInventoryButtonClick(menu.containerId, action); }
        @Override protected void extractContents(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) { graphics.blit(net.minecraft.client.renderer.RenderPipelines.GUI_TEXTURED, texture, getX(), getY(), 0, isHoveredOrFocused() ? height : 0, width, height, width, height * 2); com.hp.thermalshockreforged.client.ButtonLabels.draw(graphics, texture, getX(), getY(), width, height, isHoveredOrFocused()); }
        @Override protected void updateWidgetNarration(NarrationElementOutput output) { defaultButtonNarrationText(output); }
    }
}
