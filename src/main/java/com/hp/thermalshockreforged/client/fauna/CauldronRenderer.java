package com.hp.thermalshockreforged.client.fauna;

import com.hp.thermalshockreforged.client.geo.TsrGeoBlockRenderer;
import com.hp.thermalshockreforged.client.geo.TsrGeoModel;
import com.hp.thermalshockreforged.entity.fauna.block.CauldronOfTrilobiteBlockEntity;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.resources.Identifier;

final class CauldronRenderer extends TsrGeoBlockRenderer<CauldronOfTrilobiteBlockEntity> {
    CauldronRenderer(BlockEntityRendererProvider.Context context) { super(context, new Model()); }

    private static final class Model extends TsrGeoModel<CauldronOfTrilobiteBlockEntity> {
        @Override public Identifier getModelResource(CauldronOfTrilobiteBlockEntity entity) { return Identifier.fromNamespaceAndPath("thermal_shock_reforged", "geo/trilobitcauldron.geo.json"); }
        @Override public Identifier getTextureResource(CauldronOfTrilobiteBlockEntity entity) { return Identifier.fromNamespaceAndPath("thermal_shock_reforged", "textures/block/trilobitecauldron.png"); }
        @Override public Identifier getAnimationResource(CauldronOfTrilobiteBlockEntity entity) { return Identifier.fromNamespaceAndPath("thermal_shock_reforged", "animations/trilobitcauldron.animation.json"); }
    }
}
