package com.hp.thermalshockreforged.client.decor;

import com.hp.thermalshockreforged.ThermalShockReforged;
import com.hp.thermalshockreforged.content.decor.geo.FabricatorMenu;
import com.hp.thermalshockreforged.content.fabrication.FabricationRecipe;
import com.hp.thermalshockreforged.content.fabrication.FabricatorPage;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public final class FabricatorScreen extends AbstractContainerScreen<FabricatorMenu> {
    private static final String KEY = "gui." + ThermalShockReforged.MOD_ID + ".fabricator.";
    private static final Identifier BACKGROUND = Identifier.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, "textures/screens/fabricator_ui.png");
    private static final Identifier CRAFT = Identifier.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, "textures/screens/atlas/imagebutton_fabricator_craft_button.png");
    private static final List<String> TAB_KEYS = List.of("gui.thermal_shock_reforged.fabic_guide_page_one.label_tools", "gui.thermal_shock_reforged.fabic_guide_page_two.label_tools", "gui.thermal_shock_reforged.fabric_guide_page_three.label_tools");
    private static final int TEXT = 0xFFE0E0E0;
    private static final int TAB_X = 6, TAB_Y = 18, TAB_WIDTH = 37, TAB_HEIGHT = 14;
    private static final int GRID_X = 8, GRID_Y = 36, GRID_COLUMNS = 6, GRID_ROWS = 3;
    private static final int TRACK_X = 120, TRACK_Y = 36, TRACK_WIDTH = 4, TRACK_HEIGHT = 55, THUMB_HEIGHT = 15;
    private static final int DETAIL_X = 130, MATERIAL_Y = 34, MATERIAL_COLUMNS = 3;
    private static final int CRAFT_X = 192, CRAFT_Y = 80, CRAFT_WIDTH = 40, CRAFT_HEIGHT = 18;
    private FabricatorPage category;
    private int scroll;
    private boolean dragging;

    public FabricatorScreen(FabricatorMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title, 256, 194);
        inventoryLabelX = FabricatorMenu.INVENTORY_X;
        inventoryLabelY = FabricatorMenu.INVENTORY_Y - 11;
        category = menu.page().category();
        int index = category.recipes().indexOf(menu.page());
        if (index >= 0) scroll = Mth.clamp(index / GRID_COLUMNS - GRID_ROWS + 1, 0, maxScroll());
    }

    private int maxScroll() {
        int rows = (category.recipes().size() + GRID_COLUMNS - 1) / GRID_COLUMNS;
        return Math.max(0, rows - GRID_ROWS);
    }

    private FabricatorPage visible(int cell) {
        var recipes = category.recipes();
        int index = scroll * GRID_COLUMNS + cell;
        if (index < 0 || index >= recipes.size()) return null;
        return recipes.get(index);
    }

    private int cellX(int cell) { return leftPos + GRID_X + cell % GRID_COLUMNS * 18; }

    private int cellY(int cell) { return topPos + GRID_Y + cell / GRID_COLUMNS * 18; }

    private int thumbY() {
        int max = maxScroll();
        if (max == 0) return topPos + TRACK_Y;
        return topPos + TRACK_Y + (TRACK_HEIGHT - THUMB_HEIGHT) * scroll / max;
    }

    private FabricationRecipe recipe(FabricatorPage page) {
        if (page.isGuide() || minecraft == null || minecraft.level == null) return null;
        return null;
    }

    private ItemStack result(FabricatorPage page) {
        var recipe = recipe(page);
        if (recipe == null || minecraft == null || minecraft.level == null) return ItemStack.EMPTY;
        return recipe.result();
    }

    private int owned(Item item) {
        int total = 0;
        for (var stack : menu.inventory().getNonEquipmentItems()) if (stack.is(item)) total += stack.getCount();
        return total;
    }

    private static boolean inside(double mouseX, double mouseY, int x, int y, int width, int height) {
        return mouseX >= x && mouseX < x + width && mouseY >= y && mouseY < y + height;
    }

    private void send(int button) {
        if (minecraft != null && minecraft.gameMode != null) minecraft.gameMode.handleInventoryButtonClick(menu.containerId, button);
    }

    private void playClick() {
        if (minecraft != null) minecraft.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1));
    }

    private void dragTo(double mouseY) {
        double progress = (mouseY - topPos - TRACK_Y - THUMB_HEIGHT / 2.0) / (TRACK_HEIGHT - THUMB_HEIGHT);
        scroll = Mth.clamp((int) Math.round(progress * maxScroll()), 0, maxScroll());
    }

    @Override
    public boolean mouseClicked(net.minecraft.client.input.MouseButtonEvent event, boolean doubleClick) {
        double mouseX = event.x(); double mouseY = event.y(); int button = event.button();
        if (button == 0) {
            for (int index = 0; index < FabricatorPage.CATEGORIES.size(); index++) {
                if (!inside(mouseX, mouseY, leftPos + TAB_X + index * (TAB_WIDTH + 1), topPos + TAB_Y, TAB_WIDTH, TAB_HEIGHT)) continue;
                category = FabricatorPage.CATEGORIES.get(index);
                scroll = 0;
                playClick();
                return true;
            }
            if (maxScroll() > 0 && inside(mouseX, mouseY, leftPos + TRACK_X, topPos + TRACK_Y, TRACK_WIDTH, TRACK_HEIGHT)) {
                dragging = true;
                dragTo(mouseY);
                return true;
            }
            for (int cell = 0; cell < GRID_COLUMNS * GRID_ROWS; cell++) {
                if (!inside(mouseX, mouseY, cellX(cell), cellY(cell), 18, 18)) continue;
                var page = visible(cell);
                if (page == null) return true;
                if (page != menu.page()) {
                    menu.select(page);
                    send(FabricatorMenu.SELECT_BUTTON + page.ordinal());
                    playClick();
                }
                return true;
            }
            if (!menu.page().isGuide() && inside(mouseX, mouseY, leftPos + CRAFT_X, topPos + CRAFT_Y, CRAFT_WIDTH, CRAFT_HEIGHT)) {
                send(0);
                playClick();
                return true;
            }
        }
        return super.mouseClicked(event, doubleClick);
    }

    @Override
    public boolean mouseDragged(net.minecraft.client.input.MouseButtonEvent event, double dragX, double dragY) {
        double mouseX = event.x(); double mouseY = event.y(); int button = event.button();
        if (dragging) {
            dragTo(mouseY);
            return true;
        }
        return super.mouseDragged(event, dragX, dragY);
    }

    @Override
    public boolean mouseReleased(net.minecraft.client.input.MouseButtonEvent event) {
        int button = event.button();
        if (button == 0) dragging = false;
        return super.mouseReleased(event);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        if (maxScroll() > 0 && inside(mouseX, mouseY, leftPos + GRID_X, topPos + GRID_Y, TRACK_X + TRACK_WIDTH - GRID_X, GRID_ROWS * 18)) {
            int step = 0;
            if (scrollY > 0) step = -1;
            else if (scrollY < 0) step = 1;
            scroll = Mth.clamp(scroll + step, 0, maxScroll());
            return true;
        }
        return super.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        super.extractRenderState(graphics, mouseX, mouseY, partialTick);
        
        if (menu.getCarried().isEmpty()) customTooltips(graphics, mouseX, mouseY);
    }

    private void customTooltips(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
        for (int cell = 0; cell < GRID_COLUMNS * GRID_ROWS; cell++) {
            if (!inside(mouseX, mouseY, cellX(cell), cellY(cell), 18, 18)) continue;
            var page = visible(cell);
            if (page == null) return;
            var stack = result(page);
            if (!stack.isEmpty()) graphics.setTooltipForNextFrame(font, stack, mouseX, mouseY);
            return;
        }
        var recipe = recipe(menu.page());
        if (recipe == null) return;
        for (int index = 0; index < recipe.materials().size(); index++) {
            if (!inside(mouseX, mouseY, leftPos + DETAIL_X + index % MATERIAL_COLUMNS * 18, topPos + MATERIAL_Y + index / MATERIAL_COLUMNS * 18, 18, 18)) continue;
            var cost = recipe.materials().get(index);
            tooltip(graphics, new ItemStack(cost.item(), cost.count()), cost.count(), owned(cost.item().value()), mouseX, mouseY);
            return;
        }
        var energySlot = menu.getSlot(1);
        if (!energySlot.hasItem() && inside(mouseX, mouseY, leftPos + energySlot.x - 1, topPos + energySlot.y - 1, 18, 18)) {
            var energy = recipe.energy();
            tooltip(graphics, new ItemStack(energy.item(), energy.count()), energy.count(), 0, mouseX, mouseY);
            return;
        }
        if (inside(mouseX, mouseY, leftPos + CRAFT_X, topPos + CRAFT_Y, CRAFT_WIDTH, CRAFT_HEIGHT)) graphics.setTooltipForNextFrame(font, Component.translatable(KEY + "craft_hint"), mouseX, mouseY);
    }

    private void tooltip(GuiGraphicsExtractor graphics, ItemStack stack, int need, int have, int mouseX, int mouseY) {
        var lines = new ArrayList<>(getTooltipFromContainerItem(stack));
        var style = ChatFormatting.GREEN;
        if (have < need) style = ChatFormatting.RED;
        lines.add(Component.translatable(KEY + "need", need, have).withStyle(style));
        graphics.setTooltipForNextFrame(font, lines, java.util.Optional.empty(), mouseX, mouseY);
    }

    private void clippedItem(GuiGraphicsExtractor graphics, ItemStack stack, int x, int y) {
        graphics.enableScissor(x, y, x + 16, y + 16);
        graphics.item(stack, x, y);
        graphics.disableScissor();
    }

    @Override
    public void extractContents(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        int x = leftPos;
        int y = topPos;
        
        graphics.blit(net.minecraft.client.renderer.RenderPipelines.GUI_TEXTURED, BACKGROUND, x, y, 0, 0, imageWidth, imageHeight, 256, 194);
        for (int index = 0; index < FabricatorPage.CATEGORIES.size(); index++) {
            int tabX = x + TAB_X + index * (TAB_WIDTH + 1);
            boolean active = FabricatorPage.CATEGORIES.get(index) == category;
            int border = 0xFF8477D0;
            int fill = 0xFF131315;
            if (active) {
                border = 0xFFFFFFFF;
                fill = 0xFF3A3560;
            } else if (inside(mouseX, mouseY, tabX, y + TAB_Y, TAB_WIDTH, TAB_HEIGHT)) fill = 0xFF25252A;
            graphics.fill(tabX, y + TAB_Y, tabX + TAB_WIDTH, y + TAB_Y + TAB_HEIGHT, 0xFF000000);
            graphics.fill(tabX + 1, y + TAB_Y + 1, tabX + TAB_WIDTH - 1, y + TAB_Y + TAB_HEIGHT - 1, border);
            graphics.fill(tabX + 2, y + TAB_Y + 2, tabX + TAB_WIDTH - 2, y + TAB_Y + TAB_HEIGHT - 2, fill);
            graphics.centeredText(font, Component.translatable(TAB_KEYS.get(index)), tabX + TAB_WIDTH / 2, y + TAB_Y + 3, TEXT);
        }
        for (int cell = 0; cell < GRID_COLUMNS * GRID_ROWS; cell++) {
            var page = visible(cell);
            if (page == null) continue;
            int slotX = cellX(cell);
            int slotY = cellY(cell);
            if (page == menu.page()) graphics.fill(slotX + 1, slotY + 1, slotX + 17, slotY + 17, 0xFF3A3560);
            else if (inside(mouseX, mouseY, slotX, slotY, 18, 18)) graphics.fill(slotX + 1, slotY + 1, slotX + 17, slotY + 17, 0xFF2C2C33);
            clippedItem(graphics, result(page), slotX + 1, slotY + 1);
        }
        int thumb = 0xFF3A3A40;
        if (maxScroll() > 0) {
            thumb = 0xFF8477D0;
            if (dragging || inside(mouseX, mouseY, x + TRACK_X, thumbY(), TRACK_WIDTH, THUMB_HEIGHT)) thumb = 0xFFC1D4EE;
        }
        graphics.fill(x + TRACK_X, thumbY(), x + TRACK_X + TRACK_WIDTH, thumbY() + THUMB_HEIGHT, thumb);
        var recipe = recipe(menu.page());
        if (recipe != null) {
            for (int index = 0; index < recipe.materials().size() && index < 6; index++) {
                var cost = recipe.materials().get(index);
                int itemX = x + DETAIL_X + index % MATERIAL_COLUMNS * 18 + 1;
                int itemY = y + MATERIAL_Y + index / MATERIAL_COLUMNS * 18 + 1;
                var stack = new ItemStack(cost.item(), cost.count());
                clippedItem(graphics, stack, itemX, itemY);
                String count = String.valueOf(cost.count());
                if (owned(cost.item().value()) < cost.count()) count = ChatFormatting.RED + count;
                graphics.itemDecorations(font, stack, itemX, itemY, count);
            }
            var energySlot = menu.getSlot(1);
            if (!energySlot.hasItem()) {
                var energy = new ItemStack(recipe.energy().item(), recipe.energy().count());
                graphics.fakeItem(energy, x + energySlot.x, y + energySlot.y);
                graphics.itemDecorations(font, energy, x + energySlot.x, y + energySlot.y, ChatFormatting.RED + String.valueOf(recipe.energy().count()));
            }
            int v = 0;
            if (inside(mouseX, mouseY, x + CRAFT_X, y + CRAFT_Y, CRAFT_WIDTH, CRAFT_HEIGHT)) v = CRAFT_HEIGHT;
            graphics.blit(net.minecraft.client.renderer.RenderPipelines.GUI_TEXTURED, CRAFT, x + CRAFT_X, y + CRAFT_Y, 0, v, CRAFT_WIDTH, CRAFT_HEIGHT, CRAFT_WIDTH, CRAFT_HEIGHT * 2); com.hp.thermalshockreforged.client.ButtonLabels.draw(graphics, CRAFT, x + CRAFT_X, y + CRAFT_Y, CRAFT_WIDTH, CRAFT_HEIGHT, v > 0);
        }
        
    }

    @Override
    protected void extractLabels(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
        graphics.text(font, title, 8, 6, TEXT, false);
        graphics.text(font, playerInventoryTitle, inventoryLabelX, inventoryLabelY, TEXT, false);
        var page = menu.page();
        Component name = Component.translatable(KEY + "select");
        if (!page.isGuide()) {
            var result = result(page);
            if (!result.isEmpty()) name = result.getHoverName();
        }
        var text = font.substrByWidth(name, imageWidth - DETAIL_X - 8);
        graphics.text(font, Language.getInstance().getVisualOrder(text), DETAIL_X, 20, TEXT, false);
    }
}
