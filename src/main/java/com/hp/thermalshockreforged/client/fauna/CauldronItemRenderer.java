package com.hp.thermalshockreforged.client.fauna;
import com.hp.thermalshockreforged.client.geo.*;
import com.geckolib.renderer.base.GeoRenderState;
import com.geckolib.renderer.base.BoneSnapshots;
import net.minecraft.client.renderer.entity.state.EntityRenderState;

import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.rendertype.RenderType;

import com.hp.thermalshockreforged.entity.fauna.block.CauldronOfTrilobiteItem;
import net.minecraft.client.renderer.*;
import net.minecraft.resources.Identifier;
import com.hp.thermalshockreforged.client.compat.LegacyGeoItemRenderer;

final class CauldronItemRenderer extends LegacyGeoItemRenderer<CauldronOfTrilobiteItem> {
    CauldronItemRenderer() { super(new Model()); }

    @Override
    public RenderType getRenderType(CauldronOfTrilobiteItem entity, Identifier texture, MultiBufferSource buffers, float partialTick) { return RenderTypes.entityTranslucent(texture); }

    private static final class Model extends TsrGeoModel<CauldronOfTrilobiteItem> {
        @Override
        public Identifier getModelResource(CauldronOfTrilobiteItem entity) { return Identifier.fromNamespaceAndPath("thermal_shock_reforged", "geo/trilobitcauldron.geo.json"); }

        @Override
        public Identifier getTextureResource(CauldronOfTrilobiteItem entity) { return Identifier.fromNamespaceAndPath("thermal_shock_reforged", "textures/block/trilobitecauldron.png"); }

        @Override
        public Identifier getAnimationResource(CauldronOfTrilobiteItem entity) { return Identifier.fromNamespaceAndPath("thermal_shock_reforged", "animations/trilobitcauldron.animation.json"); }
    }
}
