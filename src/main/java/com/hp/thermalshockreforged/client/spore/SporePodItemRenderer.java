package com.hp.thermalshockreforged.client.spore;
import com.hp.thermalshockreforged.client.geo.*;
import com.geckolib.renderer.base.GeoRenderState;
import com.geckolib.renderer.base.BoneSnapshots;
import net.minecraft.client.renderer.entity.state.EntityRenderState;

import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.rendertype.RenderType;

import com.hp.thermalshockreforged.entity.spore.block.SporePodItem;
import net.minecraft.client.renderer.*;
import net.minecraft.resources.Identifier;
import com.hp.thermalshockreforged.client.compat.LegacyGeoItemRenderer;

final class SporePodItemRenderer extends LegacyGeoItemRenderer<SporePodItem> {
    SporePodItemRenderer() { super(new Model()); }

    @Override
    public RenderType getRenderType(SporePodItem entity, Identifier texture, MultiBufferSource buffers, float partialTick) { return RenderTypes.entityTranslucent(texture); }

    private static final class Model extends TsrGeoModel<SporePodItem> {
        @Override
        public Identifier getModelResource(SporePodItem entity) { return Identifier.fromNamespaceAndPath("thermal_shock_reforged", "geo/spore_pod.geo.json"); }

        @Override
        public Identifier getTextureResource(SporePodItem entity) { return Identifier.fromNamespaceAndPath("thermal_shock_reforged", "textures/block/spore_pod.png"); }

        @Override
        public Identifier getAnimationResource(SporePodItem entity) { return Identifier.fromNamespaceAndPath("thermal_shock_reforged", "animations/spore_pod.animation.json"); }
    }
}
