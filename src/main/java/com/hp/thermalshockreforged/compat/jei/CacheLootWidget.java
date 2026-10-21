package com.hp.thermalshockreforged.compat.jei;

import com.mojang.blaze3d.platform.InputConstants;
import mezz.jei.api.gui.builder.ITooltipBuilder;
import mezz.jei.api.gui.ingredient.IRecipeSlotDrawable;
import mezz.jei.api.gui.inputs.IJeiInputHandler;
import mezz.jei.api.gui.inputs.IJeiUserInput;
import mezz.jei.api.gui.inputs.RecipeSlotUnderMouse;
import mezz.jei.api.gui.widgets.ISlottedRecipeWidget;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.navigation.ScreenPosition;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.util.Mth;

import java.util.List;
import java.util.Optional;

public final class CacheLootWidget implements ISlottedRecipeWidget, IJeiInputHandler {
    public static final int ROW = 18;
    public static final int COLUMNS = 9;
    public static final int VISIBLE_ROWS = 7;
    private static final int SCROLLBAR_WIDTH = 6;
    private static final int GRID_WIDTH = COLUMNS * ROW;
    public static final int WIDTH = GRID_WIDTH + 2 + SCROLLBAR_WIDTH;
    private static final ScreenPosition POSITION = new ScreenPosition(0, 0);
    private final CacheLootEntry entry;
    private final List<List<IRecipeSlotDrawable>> lines;
    private int firstRow;
    private boolean dragging;

    public CacheLootWidget(CacheLootEntry entry, List<List<IRecipeSlotDrawable>> lines) {
        this.entry = entry;
        this.lines = lines;
    }

    private int hiddenRows() { return Math.max(0, entry.lines().size() - VISIBLE_ROWS); }
    private int height() { return VISIBLE_ROWS * ROW; }

    @Override public ScreenPosition getPosition() { return POSITION; }
    @Override public ScreenRectangle getArea() { return new ScreenRectangle(0, 0, WIDTH, height()); }

    @Override
    public void drawWidget(GuiGraphics graphics, double mouseX, double mouseY) {
        var font = Minecraft.getInstance().font;
        for (int visible = 0; visible < VISIBLE_ROWS; visible++) {
            int index = firstRow + visible;
            if (index >= entry.lines().size()) break;
            var line = entry.lines().get(index);
            int y = visible * ROW;
            if (line.title() != null) {
                graphics.fill(0, y, GRID_WIDTH, y + ROW, 0xFF9E9E9E);
                graphics.drawString(font, line.title(), line.slots().size() * ROW + 4, y + 5, 0xFFFFFFFF, true);
            }
            var slots = lines.get(index);
            for (int column = 0; column < slots.size(); column++) {
                var slot = slots.get(column);
                slot.setPosition(column * ROW + 1, y + 1);
                slot.draw(graphics);
                if (slot.isMouseOver(mouseX, mouseY)) slot.drawHoverOverlays(graphics);
            }
        }
        for (int index = 0; index < lines.size(); index++) {
            if (index >= firstRow && index < firstRow + VISIBLE_ROWS) continue;
            for (var slot : lines.get(index)) slot.setPosition(-10000, -10000);
        }
        if (hiddenRows() > 0) {
            int track = height();
            int thumb = Math.max(10, track * VISIBLE_ROWS / entry.lines().size());
            int thumbY = (track - thumb) * firstRow / hiddenRows();
            int x = WIDTH - SCROLLBAR_WIDTH;
            graphics.fill(x, 0, WIDTH, track, 0xFF373737);
            graphics.fill(x, thumbY, WIDTH, thumbY + thumb, 0xFFC6C6C6);
            graphics.fill(x + 1, thumbY + 1, WIDTH - 1, thumbY + thumb - 1, 0xFF8B8B8B);
        }
    }

    @Override
    public void getTooltip(ITooltipBuilder tooltip, double mouseX, double mouseY) { }

    @Override
    public Optional<RecipeSlotUnderMouse> getSlotUnderMouse(double mouseX, double mouseY) {
        for (int index = firstRow; index < Math.min(lines.size(), firstRow + VISIBLE_ROWS); index++) {
            for (var slot : lines.get(index)) if (slot.isMouseOver(mouseX, mouseY)) return Optional.of(new RecipeSlotUnderMouse(slot, POSITION));
        }
        return Optional.empty();
    }

    @Override
    public boolean handleMouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        if (hiddenRows() == 0) return false;
        if (scrollY > 0) firstRow = Math.max(0, firstRow - 1);
        if (scrollY < 0) firstRow = Math.min(hiddenRows(), firstRow + 1);
        return true;
    }

    @Override
    public boolean handleInput(double mouseX, double mouseY, IJeiUserInput input) {
        if (hiddenRows() == 0 || input.getKey().getType() != InputConstants.Type.MOUSE || input.getKey().getValue() != InputConstants.MOUSE_BUTTON_LEFT) return false;
        if (mouseX < WIDTH - SCROLLBAR_WIDTH || mouseX >= WIDTH || mouseY < 0 || mouseY >= height()) {
            if (!input.isSimulate()) dragging = false;
            return false;
        }
        if (!input.isSimulate()) {
            dragging = true;
            scrollTo(mouseY);
        }
        return true;
    }

    @Override
    public boolean handleMouseDragged(double mouseX, double mouseY, InputConstants.Key key, double dragX, double dragY) {
        if (!dragging || key.getValue() != InputConstants.MOUSE_BUTTON_LEFT) return false;
        scrollTo(mouseY);
        return true;
    }

    private void scrollTo(double localY) {
        firstRow = Mth.clamp((int) Math.round(localY / height() * hiddenRows()), 0, hiddenRows());
    }
}
