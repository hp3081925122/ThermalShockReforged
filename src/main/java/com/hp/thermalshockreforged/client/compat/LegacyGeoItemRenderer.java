package com.hp.thermalshockreforged.client.compat;
import net.minecraft.client.renderer.rendertype.RenderTypes;

import com.geckolib.animatable.GeoItem;
import com.geckolib.renderer.GeoItemRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;

public class LegacyGeoItemRenderer<T extends Item & GeoItem> extends GeoItemRenderer<T> {
    public LegacyGeoItemRenderer(LegacyGeoModel<T> model) { super(model); }
    public RenderType getRenderType(T item, Identifier texture, MultiBufferSource buffers, float partialTick) { return RenderTypes.entityTranslucent(texture); }
}
