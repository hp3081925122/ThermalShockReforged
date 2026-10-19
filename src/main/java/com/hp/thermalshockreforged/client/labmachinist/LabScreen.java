package com.hp.thermalshockreforged.client.labmachinist;

import com.hp.thermalshockreforged.entity.labmachinist.*;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;

public final class LabScreen extends AbstractContainerScreen<LabMenu> implements LabDialogueClock.Owner {
    private final LabDialogueClock clock;
    private boolean soundPlayed;
    public LabScreen(LabMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title); imageWidth = menu.page.width; imageHeight = menu.page.height;
        clock = Minecraft.getInstance().screen instanceof LabDialogueClock.Owner previous && previous.dialogueKey().equals(dialogueKey()) ? previous.dialogueClock() : new LabDialogueClock();
    }
    @Override public LabDialogueClock dialogueClock() { return clock; }
    @Override public String dialogueKey() { return menu.npcId >= 0 ? menu.npcUuid.toString() : "block:" + menu.anchor; }
    private static ResourceLocation texture(String name) { return LabContent.id("textures/screens/" + name + ".png"); }
    @Override protected void init() {
        super.init();
        if (!soundPlayed && menu.page.voice && minecraft != null && minecraft.level != null) { minecraft.level.playLocalSound(menu.anchor.getX(), menu.anchor.getY(), menu.anchor.getZ(), net.minecraft.core.registries.BuiltInRegistries.SOUND_EVENT.get(LabContent.id(menu.openingSound)), net.minecraft.sounds.SoundSource.NEUTRAL, 1, 1, false); soundPlayed = true; }
        for (var button : menu.page.buttons) addRenderableWidget(new ImageButton(button));
    }
    @Override protected void containerTick() { if (menu.page.clock) clock.frame = clock.frame > 12 ? 0 : clock.frame + 1; }
    @Override public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) { super.render(graphics, mouseX, mouseY, partialTick); renderTooltip(graphics, mouseX, mouseY); }
    @Override protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        RenderSystem.setShaderColor(1, 1, 1, 1); RenderSystem.enableBlend(); RenderSystem.defaultBlendFunc();
        for (var image : menu.page.images) graphics.blit(texture(image.texture()), leftPos + image.x(), topPos + image.y(), 0, image.frameMax() < 0 ? 0 : Mth.clamp(clock.frame * 128, 0, image.frameMax()), image.width(), image.height(), image.textureWidth(), image.textureHeight());
        RenderSystem.disableBlend();
    }
    @Override protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
        for (var line : menu.page.lines) graphics.drawString(font, Component.translatable(line.key()), line.x(), line.y(), line.color(), false);
        if (menu.page == LabPage.MAIN) graphics.drawString(font, Component.translatable("gui.thermal_shock_reforged.qo_main.greeting_" + (menu.intro == 1 || menu.intro == 2 ? menu.intro : 0)), 9, 112, -1, false);
    }
    private final class ImageButton extends AbstractButton {
        private final int action;
        private final ResourceLocation texture;
        ImageButton(LabPage.Button button) { super(leftPos + button.x(), topPos + button.y(), button.width(), button.height(), Component.translatable("gui.thermal_shock_reforged.qo.button_" + button.texture())); action = button.action(); texture = texture("atlas/imagebutton_" + button.texture()); }
        @Override public void onPress() { if (minecraft != null && minecraft.gameMode != null) minecraft.gameMode.handleInventoryButtonClick(menu.containerId, action); }
        @Override protected void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) { RenderSystem.enableBlend(); RenderSystem.defaultBlendFunc(); graphics.blit(texture, getX(), getY(), 0, isHoveredOrFocused() ? height : 0, width, height, width, height * 2); RenderSystem.disableBlend(); }
        @Override protected void updateWidgetNarration(NarrationElementOutput output) { defaultButtonNarrationText(output); }
    }
}
