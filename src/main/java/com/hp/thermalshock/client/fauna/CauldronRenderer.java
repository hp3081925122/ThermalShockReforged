package com.hp.thermalshock.client.fauna;

import com.hp.thermalshock.entity.fauna.block.CauldronOfTrilobiteBlockEntity;
import net.minecraft.client.renderer.*;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

final class CauldronRenderer extends GeoBlockRenderer<CauldronOfTrilobiteBlockEntity> {
    CauldronRenderer() { super(new Model()); }

    @Override
    public RenderType getRenderType(CauldronOfTrilobiteBlockEntity entity, ResourceLocation texture, MultiBufferSource buffers, float partialTick) { return RenderType.entityTranslucent(texture); }

    private static final class Model extends GeoModel<CauldronOfTrilobiteBlockEntity> {
        @Override
        public ResourceLocation getModelResource(CauldronOfTrilobiteBlockEntity entity) { return ResourceLocation.fromNamespaceAndPath("thermal_shock", "geo/trilobitcauldron.geo.json"); }

        @Override
        public ResourceLocation getTextureResource(CauldronOfTrilobiteBlockEntity entity) { return ResourceLocation.fromNamespaceAndPath("thermal_shock", "textures/block/trilobitecauldron.png"); }

        @Override
        public ResourceLocation getAnimationResource(CauldronOfTrilobiteBlockEntity entity) { return ResourceLocation.fromNamespaceAndPath("thermal_shock", "animations/trilobitcauldron.animation.json"); }
    }
}
