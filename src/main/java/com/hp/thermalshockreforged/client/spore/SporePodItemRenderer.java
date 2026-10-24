package com.hp.thermalshockreforged.client.spore;

import com.hp.thermalshockreforged.client.geo.TsrGeoItemRenderer;
import com.hp.thermalshockreforged.client.geo.TsrGeoModel;
import com.hp.thermalshockreforged.entity.spore.block.SporePodItem;
import net.minecraft.resources.Identifier;

final class SporePodItemRenderer extends TsrGeoItemRenderer<SporePodItem> {
    SporePodItemRenderer() { super(new Model()); }

    private static final class Model extends TsrGeoModel<SporePodItem> {
        @Override public Identifier getModelResource(SporePodItem entity) { return Identifier.fromNamespaceAndPath("thermal_shock_reforged", "geo/spore_pod.geo.json"); }
        @Override public Identifier getTextureResource(SporePodItem entity) { return Identifier.fromNamespaceAndPath("thermal_shock_reforged", "textures/block/spore_pod.png"); }
        @Override public Identifier getAnimationResource(SporePodItem entity) { return Identifier.fromNamespaceAndPath("thermal_shock_reforged", "animations/spore_pod.animation.json"); }
    }
}
