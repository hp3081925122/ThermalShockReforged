package com.hp.thermalshockreforged.client.spore;

import com.hp.thermalshockreforged.entity.spore.block.SporePodItem;
import net.minecraft.client.renderer.*;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoItemRenderer;

final class SporePodItemRenderer extends GeoItemRenderer<SporePodItem> {
    SporePodItemRenderer() { super(new Model()); }

    @Override
    public RenderType getRenderType(SporePodItem entity, ResourceLocation texture, MultiBufferSource buffers, float partialTick) { return RenderType.entityTranslucent(texture); }

    private static final class Model extends GeoModel<SporePodItem> {
        @Override
        public ResourceLocation getModelResource(SporePodItem entity) { return ResourceLocation.fromNamespaceAndPath("thermal_shock_reforged", "geo/spore_pod.geo.json"); }

        @Override
        public ResourceLocation getTextureResource(SporePodItem entity) { return ResourceLocation.fromNamespaceAndPath("thermal_shock_reforged", "textures/block/spore_pod.png"); }

        @Override
        public ResourceLocation getAnimationResource(SporePodItem entity) { return ResourceLocation.fromNamespaceAndPath("thermal_shock_reforged", "animations/spore_pod.animation.json"); }
    }
}
