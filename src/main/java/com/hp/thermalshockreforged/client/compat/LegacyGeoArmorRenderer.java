package com.hp.thermalshockreforged.client.compat;
import net.minecraft.client.renderer.rendertype.RenderTypes;

import com.geckolib.animatable.GeoItem;
import com.geckolib.renderer.GeoArmorRenderer;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class LegacyGeoArmorRenderer<T extends Item & GeoItem> extends GeoArmorRenderer<T, HumanoidRenderState> {
    protected LivingEntity currentEntity;
    protected ItemStack currentStack;
    protected EquipmentSlot currentSlot;
    public LegacyGeoArmorRenderer(LegacyGeoModel<T> model) { super(model); }
    public void prepForRender(LivingEntity entity, ItemStack stack, EquipmentSlot slot, HumanoidModel<?> original) { currentEntity = entity; currentStack = stack; currentSlot = slot; }
    public long getInstanceId(T item) { return 0; }
    public RenderType getRenderType(T item, Identifier texture, MultiBufferSource buffers, float partialTick) { return RenderTypes.entityTranslucent(texture); }
}
