package com.hp.thermalshockreforged.client.fauna;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.rendertype.RenderType;

import com.hp.thermalshockreforged.entity.fauna.block.CauldronOfTrilobiteBlockEntity;
import net.minecraft.client.renderer.*;
import net.minecraft.resources.Identifier;
import com.hp.thermalshockreforged.client.compat.LegacyGeoModel;
import com.hp.thermalshockreforged.client.compat.LegacyGeoBlockRenderer;

final class CauldronRenderer extends LegacyGeoBlockRenderer<CauldronOfTrilobiteBlockEntity> {
    CauldronRenderer() { super(new Model()); }

    @Override
    public RenderType getRenderType(CauldronOfTrilobiteBlockEntity entity, Identifier texture, MultiBufferSource buffers, float partialTick) { return RenderTypes.entityTranslucent(texture); }

    private static final class Model extends LegacyGeoModel<CauldronOfTrilobiteBlockEntity> {
        @Override
        public Identifier getModelResource(CauldronOfTrilobiteBlockEntity entity) { return Identifier.fromNamespaceAndPath("thermal_shock_reforged", "geo/trilobitcauldron.geo.json"); }

        @Override
        public Identifier getTextureResource(CauldronOfTrilobiteBlockEntity entity) { return Identifier.fromNamespaceAndPath("thermal_shock_reforged", "textures/block/trilobitecauldron.png"); }

        @Override
        public Identifier getAnimationResource(CauldronOfTrilobiteBlockEntity entity) { return Identifier.fromNamespaceAndPath("thermal_shock_reforged", "animations/trilobitcauldron.animation.json"); }
    }
}
