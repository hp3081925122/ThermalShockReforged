package com.hp.thermalshock.client.fauna;

import com.hp.thermalshock.entity.fauna.block.CauldronOfTrilobiteItem;
import net.minecraft.client.renderer.*;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoItemRenderer;

final class CauldronItemRenderer extends GeoItemRenderer<CauldronOfTrilobiteItem> {
    CauldronItemRenderer() { super(new Model()); }

    @Override
    public RenderType getRenderType(CauldronOfTrilobiteItem entity, ResourceLocation texture, MultiBufferSource buffers, float partialTick) { return RenderType.entityTranslucent(texture); }

    private static final class Model extends GeoModel<CauldronOfTrilobiteItem> {
        @Override
        public ResourceLocation getModelResource(CauldronOfTrilobiteItem entity) { return ResourceLocation.fromNamespaceAndPath("thermal_shock", "geo/trilobitcauldron.geo.json"); }

        @Override
        public ResourceLocation getTextureResource(CauldronOfTrilobiteItem entity) { return ResourceLocation.fromNamespaceAndPath("thermal_shock", "textures/block/trilobitecauldron.png"); }

        @Override
        public ResourceLocation getAnimationResource(CauldronOfTrilobiteItem entity) { return ResourceLocation.fromNamespaceAndPath("thermal_shock", "animations/trilobitcauldron.animation.json"); }
    }
}
