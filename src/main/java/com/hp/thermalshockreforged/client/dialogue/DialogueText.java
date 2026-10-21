package com.hp.thermalshockreforged.client.dialogue;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

public final class DialogueText {
    private static final int MARGIN = 9;
    private static final int MIN_WIDTH = 16;

    private DialogueText() { }

    public static void draw(GuiGraphics graphics, Font font, Component text, int x, int y, int color, int imageWidth, int spacing) {
        int step = spacing;
        if (step <= 0) step = font.lineHeight + 2;
        int width = Math.max(MIN_WIDTH, imageWidth - x - MARGIN);
        int lineY = y;
        for (var part : font.split(text, width)) {
            graphics.drawString(font, part, x, lineY, color, false);
            lineY += step;
        }
    }
}
