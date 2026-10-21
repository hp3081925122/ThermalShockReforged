package com.hp.thermalshockreforged.compat.jei;

import com.hp.thermalshockreforged.ThermalShockReforged;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotDrawable;
import mezz.jei.api.gui.widgets.IRecipeExtrasBuilder;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public final class CacheLootCategory implements IRecipeCategory<CacheLootEntry> {
    public static final RecipeType<CacheLootEntry> TYPE = RecipeType.create(ThermalShockReforged.MOD_ID, "cache_loot", CacheLootEntry.class);
    private final IDrawable icon;

    public CacheLootCategory(IGuiHelper helper, ItemStack icon) {
        this.icon = helper.createDrawableItemStack(icon);
    }

    @Override public RecipeType<CacheLootEntry> getRecipeType() { return TYPE; }
    @Override public Component getTitle() { return Component.translatable("jei." + ThermalShockReforged.MOD_ID + ".cache"); }
    @Override public IDrawable getIcon() { return icon; }
    @Override public int getWidth() { return CacheLootWidget.WIDTH; }
    @Override public int getHeight() { return CacheLootWidget.VISIBLE_ROWS * CacheLootWidget.ROW; }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, CacheLootEntry entry, IFocusGroup focuses) {
        for (var line : entry.lines()) {
            var role = RecipeIngredientRole.OUTPUT;
            if (line.title() != null) role = RecipeIngredientRole.INPUT;
            for (var data : line.slots()) {
                var slot = builder.addSlot(role, 0, 0).setStandardSlotBackground().addItemStacks(data.stacks());
                if (!data.notes().isEmpty()) slot.addRichTooltipCallback((view, tooltip) -> tooltip.addAll(data.notes()));
            }
        }
    }

    @Override
    public void createRecipeExtras(IRecipeExtrasBuilder builder, CacheLootEntry entry, IFocusGroup focuses) {
        List<IRecipeSlotDrawable> slots = builder.getRecipeSlots().getSlots();
        var lines = new ArrayList<List<IRecipeSlotDrawable>>();
        int index = 0;
        for (var line : entry.lines()) {
            lines.add(new ArrayList<>(slots.subList(index, index + line.slots().size())));
            index += line.slots().size();
        }
        var widget = new CacheLootWidget(entry, lines);
        builder.addSlottedWidget(widget, slots);
        builder.addInputHandler(widget);
    }
}
