package com.hp.thermalshockreforged.compat.jei;

import com.hp.thermalshockreforged.ThermalShockReforged;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.IRecipeSlotBuilder;
import mezz.jei.api.gui.builder.ITooltipBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public final class MachineCategory implements IRecipeCategory<MachineEntry> {
    public static final RecipeType<MachineEntry> FABRICATOR = RecipeType.create(ThermalShockReforged.MOD_ID, "fabricator", MachineEntry.class);
    public static final RecipeType<MachineEntry> TRANSMUTATOR = RecipeType.create(ThermalShockReforged.MOD_ID, "transmutator", MachineEntry.class);
    public static final RecipeType<MachineEntry> EXTRACTOR = RecipeType.create(ThermalShockReforged.MOD_ID, "extractor", MachineEntry.class);
    public static final RecipeType<MachineEntry> CENTRIFUGE = RecipeType.create(ThermalShockReforged.MOD_ID, "centrifuge", MachineEntry.class);
    public static final RecipeType<MachineEntry> LAPIDARY = RecipeType.create(ThermalShockReforged.MOD_ID, "lapidary", MachineEntry.class);
    public static final RecipeType<MachineEntry> ASSEMBLER = RecipeType.create(ThermalShockReforged.MOD_ID, "assembler", MachineEntry.class);

    public record Layout(int[][] inputs, int arrowX, int arrowY, int outputX, int outputY, int outputColumns, int width, int height) { }

    private final RecipeType<MachineEntry> type;
    private final Component title;
    private final List<ItemStack> machines;
    private final Layout layout;
    private final IDrawable icon;
    private final IDrawable arrow;

    public MachineCategory(IGuiHelper helper, RecipeType<MachineEntry> type, String titleKey, List<ItemStack> machines, Layout layout) {
        this.type = type;
        this.title = Component.translatable(titleKey);
        this.machines = machines;
        this.layout = layout;
        if (machines.isEmpty()) icon = helper.createBlankDrawable(16, 16);
        else icon = helper.createDrawableItemStack(machines.getFirst());
        arrow = helper.getRecipeArrow();
    }

    @Override public RecipeType<MachineEntry> getRecipeType() { return type; }
    @Override public Component getTitle() { return title; }
    @Override public IDrawable getIcon() { return icon; }
    @Override public int getWidth() { return layout.width(); }
    @Override public int getHeight() { return layout.height(); }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, MachineEntry entry, IFocusGroup focuses) {
        for (int index = 0; index < entry.inputs().size() && index < layout.inputs().length; index++) {
            int[] position = layout.inputs()[index];
            slot(builder.addSlot(RecipeIngredientRole.INPUT, position[0] + 1, position[1] + 1), entry.inputs().get(index));
        }
        if (!machines.isEmpty() && type != ASSEMBLER) builder.addSlot(RecipeIngredientRole.CATALYST, layout.arrowX() + 4, layout.arrowY() - 19).setStandardSlotBackground().addItemStacks(machines);
        for (int index = 0; index < entry.outputs().size(); index++) {
            int x = layout.outputX() + index % layout.outputColumns() * 18;
            int y = layout.outputY() + index / layout.outputColumns() * 18;
            slot(builder.addSlot(RecipeIngredientRole.OUTPUT, x + 1, y + 1), entry.outputs().get(index));
        }
    }

    private static void slot(IRecipeSlotBuilder slot, MachineEntry.Slot data) {
        slot.setStandardSlotBackground().addItemStacks(data.stacks());
        if (!data.notes().isEmpty()) slot.addRichTooltipCallback((view, tooltip) -> tooltip.addAll(data.notes()));
    }

    @Override
    public void draw(MachineEntry entry, IRecipeSlotsView slots, GuiGraphicsExtractor graphics, double mouseX, double mouseY) {
        arrow.draw(graphics, layout.arrowX(), layout.arrowY() + 1);
        if (entry.xp() != null) NpcTradeListWidget.drawXp(graphics, layout.arrowX(), layout.arrowY() + 20, entry.xp());
    }

    @Override
    public void getTooltip(ITooltipBuilder tooltip, MachineEntry entry, IRecipeSlotsView slots, double mouseX, double mouseY) {
        if (entry.xp() == null) return;
        if (mouseX >= layout.arrowX() && mouseX < layout.arrowX() + 28 && mouseY >= layout.arrowY() + 19 && mouseY < layout.arrowY() + 31) tooltip.addAll(entry.xpTooltip());
    }
}
