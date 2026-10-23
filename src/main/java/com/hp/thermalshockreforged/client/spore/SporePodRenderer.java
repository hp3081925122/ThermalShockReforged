package com.hp.thermalshockreforged.client.spore;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.rendertype.RenderType;

import com.hp.thermalshockreforged.entity.spore.block.SporePodBlockEntity;
import net.minecraft.client.renderer.*;
import net.minecraft.resources.Identifier;
import com.hp.thermalshockreforged.client.compat.LegacyGeoModel;
import com.hp.thermalshockreforged.client.compat.LegacyGeoBlockRenderer;

final class SporePodRenderer extends LegacyGeoBlockRenderer<SporePodBlockEntity> {
    SporePodRenderer() { super(new Model()); }

    @Override
    public RenderType getRenderType(SporePodBlockEntity entity, Identifier texture, MultiBufferSource buffers, float partialTick) { return RenderTypes.entityTranslucent(texture); }

    private static final class Model extends LegacyGeoModel<SporePodBlockEntity> {
        @Override
        public Identifier getModelResource(SporePodBlockEntity entity) { return Identifier.fromNamespaceAndPath("thermal_shock_reforged", "geo/spore_pod.geo.json"); }

        @Override
        public Identifier getTextureResource(SporePodBlockEntity entity) { return Identifier.fromNamespaceAndPath("thermal_shock_reforged", "textures/block/spore_pod.png"); }

        @Override
        public Identifier getAnimationResource(SporePodBlockEntity entity) { return Identifier.fromNamespaceAndPath("thermal_shock_reforged", "animations/spore_pod.animation.json"); }
    }
}
