package com.hp.thermalshock.client.spore;

import com.hp.thermalshock.entity.spore.block.SporePodItem;
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
        public ResourceLocation getModelResource(SporePodItem entity) { return ResourceLocation.fromNamespaceAndPath("thermal_shock", "geo/spore_pod.geo.json"); }

        @Override
        public ResourceLocation getTextureResource(SporePodItem entity) { return ResourceLocation.fromNamespaceAndPath("thermal_shock", "textures/block/spore_pod.png"); }

        @Override
        public ResourceLocation getAnimationResource(SporePodItem entity) { return ResourceLocation.fromNamespaceAndPath("thermal_shock", "animations/spore_pod.animation.json"); }
    }
}
