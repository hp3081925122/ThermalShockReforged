package com.hp.thermalshockreforged.client.spore;

import com.hp.thermalshockreforged.client.geo.TsrGeoBlockRenderer;
import com.hp.thermalshockreforged.client.geo.TsrGeoModel;
import com.hp.thermalshockreforged.entity.spore.block.SporePodBlockEntity;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.resources.Identifier;

final class SporePodRenderer extends TsrGeoBlockRenderer<SporePodBlockEntity> {
    SporePodRenderer(BlockEntityRendererProvider.Context context) { super(context, new Model()); }

    private static final class Model extends TsrGeoModel<SporePodBlockEntity> {
        @Override public Identifier getModelResource(SporePodBlockEntity entity) { return Identifier.fromNamespaceAndPath("thermal_shock_reforged", "geo/spore_pod.geo.json"); }
        @Override public Identifier getTextureResource(SporePodBlockEntity entity) { return Identifier.fromNamespaceAndPath("thermal_shock_reforged", "textures/block/spore_pod.png"); }
        @Override public Identifier getAnimationResource(SporePodBlockEntity entity) { return Identifier.fromNamespaceAndPath("thermal_shock_reforged", "animations/spore_pod.animation.json"); }
    }
}
