package com.hp.thermalshockreforged.client;

import com.hp.thermalshockreforged.ThermalShockReforged;
import java.util.Set;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

public final class ButtonLabels {
    private static final String KEY = "gui." + ThermalShockReforged.MOD_ID + ".button_label.";
    private static final String PREFIX = "imagebutton_";
    private static final Set<String> ACCENT = Set.of("mainerrandsbuttona", "maininfobuttona", "maintabletsbuttona", "maintipbuttona", "maintradebuttona", "nextbuttona", "tradebuttona", "tradebuttona1");
    private static final int ACCENT_COLOR = 0xFFA5A9E7;
    private static final int TEXT_COLOR = 0xFFFFFFFF;
    private static final int PADDING = 4;
    private static final float MIN_SCALE = 0.5F;

    private ButtonLabels() { }

    public static void draw(GuiGraphicsExtractor graphics, Identifier texture, int x, int y, int width, int height, boolean hovered) {
        String path = texture.getPath();
        int start = path.lastIndexOf(PREFIX);
        if (start < 0 || !path.endsWith(".png")) return;
        String name = path.substring(start + PREFIX.length(), path.length() - 4);
        String key = KEY + name;
        if (!I18n.exists(key)) return;
        var font = Minecraft.getInstance().font;
        Component text = Component.translatable(key);
        int color = TEXT_COLOR;
        if (ACCENT.contains(name) && !hovered) color = ACCENT_COLOR;
        int textWidth = font.width(text);
        int available = width - PADDING * 2;
        if (textWidth <= available) {
            graphics.text(font, text, x + (width - textWidth + 1) / 2, y + (height - 8) / 2, color, false);
            return;
        }
        float scale = Math.max(MIN_SCALE, (float) available / textWidth);
        var pose = graphics.pose();
        pose.pushMatrix();
        pose.translate(x + width / 2F, y + height / 2F);
        pose.scale(scale, scale);
        graphics.text(font, text, -textWidth / 2, -4, color, false);
        pose.popMatrix();
    }
}
