package com.hp.thermalshockreforged.client.fauna;

import com.hp.thermalshockreforged.client.geo.TsrGeoItemRenderer;
import com.hp.thermalshockreforged.client.geo.TsrGeoModel;
import com.hp.thermalshockreforged.entity.fauna.block.CauldronOfTrilobiteItem;
import net.minecraft.resources.Identifier;

final class CauldronItemRenderer extends TsrGeoItemRenderer<CauldronOfTrilobiteItem> {
    CauldronItemRenderer() { super(new Model()); }

    private static final class Model extends TsrGeoModel<CauldronOfTrilobiteItem> {
        @Override public Identifier getModelResource(CauldronOfTrilobiteItem entity) { return Identifier.fromNamespaceAndPath("thermal_shock_reforged", "geo/trilobitcauldron.geo.json"); }
        @Override public Identifier getTextureResource(CauldronOfTrilobiteItem entity) { return Identifier.fromNamespaceAndPath("thermal_shock_reforged", "textures/block/trilobitecauldron.png"); }
        @Override public Identifier getAnimationResource(CauldronOfTrilobiteItem entity) { return Identifier.fromNamespaceAndPath("thermal_shock_reforged", "animations/trilobitcauldron.animation.json"); }
    }
}
