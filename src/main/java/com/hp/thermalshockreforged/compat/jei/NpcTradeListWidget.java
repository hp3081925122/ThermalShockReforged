package com.hp.thermalshockreforged.compat.jei;

import com.hp.thermalshockreforged.ThermalShockReforged;
import com.mojang.blaze3d.platform.InputConstants;
import mezz.jei.api.gui.builder.ITooltipBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotDrawable;
import mezz.jei.api.gui.inputs.IJeiInputHandler;
import mezz.jei.api.gui.inputs.IJeiUserInput;
import mezz.jei.api.gui.inputs.RecipeSlotUnderMouse;
import mezz.jei.api.gui.widgets.ISlottedRecipeWidget;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.navigation.ScreenPosition;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;

import java.util.List;
import java.util.Optional;

public final class NpcTradeListWidget implements ISlottedRecipeWidget, IJeiInputHandler {
    public static final int ROW = 18;
    public static final int VISIBLE_ROWS = 4;
    public static final int COST_SLOTS = 3;
    private static final int XP_X = COST_SLOTS * ROW + 2;
    private static final int ARROW_X = XP_X + 24;
    private static final int RESULT_X = ARROW_X + 28;
    private static final int LOCK_X = RESULT_X + 22;
    private static final int SCROLLBAR_WIDTH = 6;
    private static final Identifier ORB = Identifier.withDefaultNamespace("textures/entity/experience_orb.png");
    private static final Identifier LOCK = Identifier.withDefaultNamespace("widget/locked_button");
    private final ScreenPosition position;
    private final int width;
    private final NpcTradeEntry entry;
    private final List<List<IRecipeSlotDrawable>> costSlots;
    private final List<IRecipeSlotDrawable> resultSlots;
    private final IDrawable slotBackground;
    private final IDrawable arrow;
    private int firstRow;
    private boolean dragging;

    public NpcTradeListWidget(int x, int y, int width, NpcTradeEntry entry, List<List<IRecipeSlotDrawable>> costSlots, List<IRecipeSlotDrawable> resultSlots, IDrawable slotBackground, IDrawable arrow) {
        position = new ScreenPosition(x, y);
        this.width = width;
        this.entry = entry;
        this.costSlots = costSlots;
        this.resultSlots = resultSlots;
        this.slotBackground = slotBackground;
        this.arrow = arrow;
    }

    private int hiddenRows() { return Math.max(0, entry.rows().size() - VISIBLE_ROWS); }
    private int height() { return VISIBLE_ROWS * ROW; }
    private int scrollbarX() { return width - SCROLLBAR_WIDTH; }

    @Override public ScreenPosition getPosition() { return position; }
    @Override public ScreenRectangle getArea() { return new ScreenRectangle(position.x(), position.y(), width, height()); }

    @Override
    public void drawWidget(GuiGraphicsExtractor graphics, double mouseX, double mouseY) {
        for (int visible = 0; visible < VISIBLE_ROWS; visible++) {
            int index = firstRow + visible;
            if (index >= entry.rows().size()) break;
            var row = entry.rows().get(index);
            int y = visible * ROW;
            for (int column = 0; column < COST_SLOTS; column++) {
                if (column < costSlots.get(index).size()) {
                    var slot = costSlots.get(index).get(column);
                    slot.setPosition(column * ROW + 1, y + 1);
                    slot.draw(graphics);
                    if (slot.isMouseOver(mouseX, mouseY)) slot.drawHoverOverlays(graphics);
                } else {
                    slotBackground.draw(graphics, column * ROW, y);
                }
            }
            if (row.levels() > 0) drawXp(graphics, XP_X, y + 4, Component.literal(String.valueOf(row.levels())));
            arrow.draw(graphics, ARROW_X, y + 1);
            var result = resultSlots.get(index);
            result.setPosition(RESULT_X + 1, y + 1);
            result.draw(graphics);
            if (result.isMouseOver(mouseX, mouseY)) result.drawHoverOverlays(graphics);
            if (row.condition() != null) graphics.blitSprite(LOCK, LOCK_X, y + 3, 12, 12);
        }
        hideRows();
        if (hiddenRows() > 0) {
            int track = height();
            int thumb = Math.max(10, track * VISIBLE_ROWS / entry.rows().size());
            int thumbY = (track - thumb) * firstRow / hiddenRows();
            graphics.fill(scrollbarX(), 0, width, track, 0xFF373737);
            graphics.fill(scrollbarX(), thumbY, width, thumbY + thumb, 0xFFC6C6C6);
            graphics.fill(scrollbarX() + 1, thumbY + 1, width - 1, thumbY + thumb - 1, 0xFF8B8B8B);
        }
    }

    public static void drawXp(GuiGraphicsExtractor graphics, int x, int y, Component text) {
        graphics.setColor(0.6F, 1F, 0.3F, 1F);
        graphics.blit(ORB, x, y, 10, 10, 0, 0, 16, 16, 64, 64);
        graphics.setColor(1F, 1F, 1F, 1F);
        graphics.drawString(Minecraft.getInstance().font, text, x + 11, y + 1, 0xFF80FF20, true);
    }

    private void hideRows() {
        for (int index = 0; index < entry.rows().size(); index++) {
            if (index >= firstRow && index < firstRow + VISIBLE_ROWS) continue;
            for (var slot : costSlots.get(index)) slot.setPosition(-10000, -10000);
            resultSlots.get(index).setPosition(-10000, -10000);
        }
    }

    @Override
    public void getTooltip(ITooltipBuilder tooltip, double mouseX, double mouseY) {
        int visible = (int) Math.floor(mouseY / ROW);
        int index = firstRow + visible;
        if (mouseY < 0 || visible >= VISIBLE_ROWS || index >= entry.rows().size()) return;
        var row = entry.rows().get(index);
        if (row.levels() > 0 && mouseX >= XP_X && mouseX < ARROW_X) tooltip.add(Component.translatable("jei." + ThermalShockReforged.MOD_ID + ".npc_trade.levels", row.levels()));
        if (row.condition() != null && mouseX >= LOCK_X && mouseX < LOCK_X + 12) tooltip.add(row.condition());
    }

    @Override
    public Optional<RecipeSlotUnderMouse> getSlotUnderMouse(double mouseX, double mouseY) {
        for (int index = firstRow; index < Math.min(entry.rows().size(), firstRow + VISIBLE_ROWS); index++) {
            for (var slot : costSlots.get(index)) if (slot.isMouseOver(mouseX, mouseY)) return Optional.of(new RecipeSlotUnderMouse(slot, position));
            if (resultSlots.get(index).isMouseOver(mouseX, mouseY)) return Optional.of(new RecipeSlotUnderMouse(resultSlots.get(index), position));
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
        double localX = mouseX - position.x();
        double localY = mouseY - position.y();
        if (localX < scrollbarX() || localX >= width || localY < 0 || localY >= height()) {
            if (!input.isSimulate()) dragging = false;
            return false;
        }
        if (!input.isSimulate()) {
            dragging = true;
            scrollTo(localY);
        }
        return true;
    }

    @Override
    public boolean handleMouseDragged(double mouseX, double mouseY, InputConstants.Key key, double dragX, double dragY) {
        if (!dragging || key.getValue() != InputConstants.MOUSE_BUTTON_LEFT) return false;
        scrollTo(mouseY - position.y());
        return true;
    }

    private void scrollTo(double localY) {
        firstRow = Mth.clamp((int) Math.round(localY / height() * hiddenRows()), 0, hiddenRows());
    }
}
