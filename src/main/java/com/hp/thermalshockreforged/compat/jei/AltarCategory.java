package com.hp.thermalshockreforged.compat.jei;

import com.hp.thermalshockreforged.ThermalShockReforged;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.IRecipeSlotBuilder;
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

public final class AltarCategory implements IRecipeCategory<MachineEntry> {
    public static final RecipeType<MachineEntry> TYPE = RecipeType.create(ThermalShockReforged.MOD_ID, "altar", MachineEntry.class);
    private static final int ARROW_X = 26;
    private static final int ROW_Y = 20;
    private final IDrawable icon;
    private final IDrawable arrow;

    public AltarCategory(IGuiHelper helper, ItemStack icon) {
        this.icon = helper.createDrawableItemStack(icon);
        arrow = helper.getRecipeArrow();
    }

    @Override public RecipeType<MachineEntry> getRecipeType() { return TYPE; }
    @Override public Component getTitle() { return Component.translatable("jei." + ThermalShockReforged.MOD_ID + ".altar"); }
    @Override public IDrawable getIcon() { return icon; }
    @Override public int getWidth() { return ARROW_X + 56; }
    @Override public int getHeight() { return ROW_Y + 18; }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, MachineEntry entry, IFocusGroup focuses) {
        slot(builder.addSlot(RecipeIngredientRole.INPUT, 1, ROW_Y + 1), entry.inputs().get(0));
        for (int index = 1; index < entry.inputs().size(); index++) slot(builder.addSlot(RecipeIngredientRole.CATALYST, ARROW_X - 8 + (index - 1) * 20, 1), entry.inputs().get(index));
        for (int index = 0; index < entry.outputs().size(); index++) slot(builder.addSlot(RecipeIngredientRole.OUTPUT, ARROW_X + 31 + index * 18, ROW_Y + 1), entry.outputs().get(index));
    }

    private static void slot(IRecipeSlotBuilder slot, MachineEntry.Slot data) {
        slot.setStandardSlotBackground().addItemStacks(data.stacks());
        if (!data.notes().isEmpty()) slot.addRichTooltipCallback((view, tooltip) -> tooltip.addAll(data.notes()));
    }

    @Override
    public void draw(MachineEntry entry, IRecipeSlotsView slots, GuiGraphicsExtractor graphics, double mouseX, double mouseY) {
        arrow.draw(graphics, ARROW_X, ROW_Y + 1);
    }
}
