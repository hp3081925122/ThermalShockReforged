package com.hp.thermalshockreforged.compat.jei;

import com.hp.thermalshockreforged.ThermalShockReforged;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public final class SickleHarvestCategory implements IRecipeCategory<SickleHarvestEntry> {
    public static final RecipeType<SickleHarvestEntry> WILD = RecipeType.create(ThermalShockReforged.MOD_ID, "sickle_harvest_wild", SickleHarvestEntry.class);
    public static final RecipeType<SickleHarvestEntry> POT = RecipeType.create(ThermalShockReforged.MOD_ID, "sickle_harvest_pot", SickleHarvestEntry.class);
    private static final int PLANT_Y = 24;
    private static final int ARROW_X = 24;
    private static final int OUTPUT_X = 54;
    private final RecipeType<SickleHarvestEntry> type;
    private final List<ItemStack> planters;
    private final IDrawable icon;
    private final IDrawable arrow;

    public SickleHarvestCategory(IGuiHelper helper, RecipeType<SickleHarvestEntry> type, ItemStack icon, List<ItemStack> planters) {
        this.type = type;
        this.planters = planters;
        this.icon = helper.createDrawableItemStack(icon);
        arrow = helper.getRecipeArrow();
    }

    @Override public RecipeType<SickleHarvestEntry> getRecipeType() { return type; }
    @Override public Component getTitle() { return Component.translatable("jei." + ThermalShockReforged.MOD_ID + "." + type.getUid().getPath()); }
    @Override public IDrawable getIcon() { return icon; }
    @Override public int getWidth() { return OUTPUT_X + 18 * 5; }
    @Override public int getHeight() { return PLANT_Y + 18; }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, SickleHarvestEntry entry, IFocusGroup focuses) {
        builder.addSlot(RecipeIngredientRole.CATALYST, 1, 1).setStandardSlotBackground().addItemStacks(entry.sickles());
        var plant = builder.addSlot(RecipeIngredientRole.INPUT, 1, PLANT_Y + 1).setStandardSlotBackground().addItemStacks(entry.plant());
        if (!entry.biomes().isEmpty()) {
            plant.addRichTooltipCallback((view, tooltip) -> {
                tooltip.add(Component.translatable("jei." + ThermalShockReforged.MOD_ID + ".sickle_harvest.biomes"));
                for (Component biome : entry.biomes()) tooltip.add(Component.literal(" - ").append(biome));
            });
        }
        if (!planters.isEmpty()) builder.addSlot(RecipeIngredientRole.CATALYST, ARROW_X + 4, 3).setStandardSlotBackground().addItemStacks(planters);
        for (int index = 0; index < entry.drops().size(); index++) {
            var drop = entry.drops().get(index);
            var slot = builder.addSlot(RecipeIngredientRole.OUTPUT, OUTPUT_X + 1 + index * 18, PLANT_Y + 1).setStandardSlotBackground().addItemStacks(drop.stacks());
            if (drop.note() != null) slot.addRichTooltipCallback((view, tooltip) -> tooltip.add(drop.note()));
        }
    }

    @Override
    public void draw(SickleHarvestEntry entry, IRecipeSlotsView slots, GuiGraphics graphics, double mouseX, double mouseY) {
        arrow.draw(graphics, ARROW_X, PLANT_Y + 1);
    }
}
