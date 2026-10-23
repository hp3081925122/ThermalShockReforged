package com.hp.thermalshockreforged.compat.jei;

import com.hp.thermalshockreforged.ThermalShockReforged;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.gui.widgets.IRecipeExtrasBuilder;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.util.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public final class NpcTradeCategory implements IRecipeCategory<NpcTradeEntry> {
    public static final RecipeType<NpcTradeEntry> TYPE = RecipeType.create(ThermalShockReforged.MOD_ID, "npc_trade", NpcTradeEntry.class);
    private static final int WIDTH = 176;
    private static final int MODEL_WIDTH = 60;
    private static final int MODEL_HEIGHT = 72;
    private static final int MODEL_SIZE = 48;
    private static final int LINE = 10;
    private static final int TEXT_X = MODEL_WIDTH + 2;
    private static final int TEXT_WIDTH = WIDTH - TEXT_X;
    private static final int LIST_TOP = MODEL_HEIGHT + 2;
    private static final int LIST_WIDTH = 150;
    private static final int LIST_X = (WIDTH - LIST_WIDTH) / 2;
    private static final int COLOR = 0xFF404040;
    private static final Map<String, Float> MODEL_SCALES = Map.of("lab_machinist", 0.5F);
    private static final Map<String, LivingEntity> ENTITIES = new HashMap<>();
    private static final Set<String> FAILED = new HashSet<>();
    private final IDrawable icon;
    private final IDrawable arrow;
    private final IDrawable slot;

    public NpcTradeCategory(IGuiHelper helper, ItemStack icon) {
        this.icon = helper.createDrawableItemStack(icon);
        arrow = helper.getRecipeArrow();
        slot = helper.getSlotDrawable();
    }

    @Override public RecipeType<NpcTradeEntry> getRecipeType() { return TYPE; }
    @Override public Component getTitle() { return Component.translatable("jei." + ThermalShockReforged.MOD_ID + ".npc_trade"); }
    @Override public IDrawable getIcon() { return icon; }
    @Override public int getWidth() { return WIDTH; }
    @Override public int getHeight() { return LIST_TOP + NpcTradeListWidget.VISIBLE_ROWS * NpcTradeListWidget.ROW; }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, NpcTradeEntry entry, IFocusGroup focuses) {
        for (var row : entry.rows()) {
            for (ItemStack cost : row.costs()) builder.addSlot(RecipeIngredientRole.INPUT, 0, 0).setStandardSlotBackground().addItemStack(cost);
            builder.addSlot(RecipeIngredientRole.OUTPUT, 0, 0).setStandardSlotBackground().addItemStack(row.result());
        }
    }

    @Override
    public void createRecipeExtras(IRecipeExtrasBuilder builder, NpcTradeEntry entry, IFocusGroup focuses) {
        List<IRecipeSlotDrawable> slots = builder.getRecipeSlots().getSlots();
        var costSlots = new ArrayList<List<IRecipeSlotDrawable>>();
        var resultSlots = new ArrayList<IRecipeSlotDrawable>();
        int index = 0;
        for (var row : entry.rows()) {
            costSlots.add(new ArrayList<>(slots.subList(index, index + row.costs().size())));
            index += row.costs().size();
            resultSlots.add(slots.get(index));
            index++;
        }
        var widget = new NpcTradeListWidget(LIST_X, LIST_TOP, LIST_WIDTH, entry, costSlots, resultSlots, slot, arrow);
        builder.addSlottedWidget(widget, slots);
        builder.addInputHandler(widget);
    }

    @Override
    public void draw(NpcTradeEntry entry, IRecipeSlotsView slots, GuiGraphicsExtractor graphics, double mouseX, double mouseY) {
        var entity = entity(entry.npc());
        if (entity != null) {
            float centerX = MODEL_WIDTH / 2F;
            float centerY = MODEL_HEIGHT / 2F;
            float yaw = (float) Math.atan((centerX - mouseX) / 40F);
            float pitch = (float) Math.atan((centerY - mouseY) / 40F);
            var pose = new Quaternionf().rotateZ((float) Math.PI);
            var camera = new Quaternionf().rotateX(pitch * 20F * ((float) Math.PI / 180F));
            pose.mul(camera);
            float bodyRot = entity.yBodyRot;
            float yRot = entity.getYRot();
            float xRot = entity.getXRot();
            float headRotO = entity.yHeadRotO;
            float headRot = entity.yHeadRot;
            entity.yBodyRot = 180F + yaw * 20F;
            entity.setYRot(180F + yaw * 40F);
            entity.setXRot(-pitch * 20F);
            entity.yHeadRot = entity.getYRot();
            entity.yHeadRotO = entity.getYRot();
            entity.tickCount = (int) (Util.getMillis() / 50L);
            float scale = MODEL_SIZE * MODEL_SCALES.getOrDefault(entry.npc(), 1F) / Math.max(entity.getBbHeight(), entity.getBbWidth()) / entity.getScale();
            try {
                InventoryScreen.renderEntityInInventory(graphics, centerX, centerY, scale, new Vector3f(0F, entity.getBbHeight() / 2F, 0F), pose, camera, entity);
            } catch (Exception exception) {
                FAILED.add(entry.npc());
                ThermalShockReforged.LOGGER.warn("JEI npc trade entity render failed: {}", entry.npc(), exception);
            }
            entity.yBodyRot = bodyRot;
            entity.setYRot(yRot);
            entity.setXRot(xRot);
            entity.yHeadRotO = headRotO;
            entity.yHeadRot = headRot;
        }
        var font = Minecraft.getInstance().font;
        var lines = new ArrayList<Component>();
        lines.add(Component.translatable("entity." + ThermalShockReforged.MOD_ID + "." + entry.npc()));
        lines.addAll(entry.info());
        float scale = 1F;
        var parts = new ArrayList<FormattedCharSequence>();
        for (int attempt = 0; attempt < 4; attempt++) {
            parts.clear();
            for (Component line : lines) parts.addAll(font.split(line, (int) (TEXT_WIDTH / scale)));
            if (parts.size() * LINE * scale <= MODEL_HEIGHT) break;
            scale = Math.max(0.5F, scale * MODEL_HEIGHT / (parts.size() * LINE * scale));
        }
        graphics.pose().pushPose();
        graphics.pose().translate(TEXT_X, (MODEL_HEIGHT - parts.size() * LINE * scale) / 2F, 0);
        graphics.pose().scale(scale, scale, 1F);
        int width = (int) (TEXT_WIDTH / scale);
        int y = 0;
        for (var part : parts) {
            graphics.drawString(font, part, (width - font.width(part)) / 2, y, COLOR, false);
            y += LINE;
        }
        graphics.pose().popPose();
    }

    private static LivingEntity entity(String npc) {
        var level = Minecraft.getInstance().level;
        if (level == null || FAILED.contains(npc)) return null;
        var cached = ENTITIES.get(npc);
        if (cached != null && cached.level() == level) return cached;
        try {
            var created = BuiltInRegistries.ENTITY_TYPE.getValue(Identifier.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, npc)).create(level, net.minecraft.world.entity.EntitySpawnReason.LOAD);
            if (created instanceof LivingEntity living) {
                ENTITIES.put(npc, living);
                ThermalShockReforged.LOGGER.debug("JEI npc trade entity created: npc={}, type={}, height={}, width={}", npc, living.getType(), living.getBbHeight(), living.getBbWidth());
                return living;
            }
            ThermalShockReforged.LOGGER.warn("JEI npc trade entity is not living: {}", npc);
        } catch (Exception exception) {
            ThermalShockReforged.LOGGER.warn("JEI npc trade entity create failed: {}", npc, exception);
        }
        FAILED.add(npc);
        return null;
    }
}
