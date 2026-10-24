package com.hp.thermalshockreforged.client;
import com.hp.thermalshockreforged.client.geo.*;
import com.geckolib.renderer.base.GeoRenderState;
import com.geckolib.renderer.base.BoneSnapshots;
import net.minecraft.client.renderer.entity.state.EntityRenderState;

import net.minecraft.client.renderer.rendertype.RenderTypes;

import com.hp.thermalshockreforged.ThermalShockReforged;
import com.hp.thermalshockreforged.entity.ProboskerEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.Identifier;

public final class ProboskerRenderer extends TsrGeoEntityRenderer<ProboskerEntity> {
    public ProboskerRenderer(EntityRendererProvider.Context context) {
        super(context, new Model());
        shadowRadius = 0.7F;
    }

    @Override protected RenderType visibleRenderType(EntityRenderState state, Identifier texture) { return RenderTypes.entityTranslucent(texture); }

    private static final class Model extends TsrGeoModel<ProboskerEntity> {
        @Override
        public Identifier getModelResource(ProboskerEntity entity) {
            return Identifier.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, "geo/probosker.geo.json");
        }

        @Override
        public Identifier getTextureResource(ProboskerEntity entity) {
            return Identifier.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, "textures/entities/" + entity.texture() + ".png");
        }

        @Override
        public Identifier getAnimationResource(ProboskerEntity entity) {
            return Identifier.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, "animations/probosker.animation.json");
        }

        @Override
        public void adjustBones(GeoRenderState state, BoneSnapshots bones) {
            lookAt(state, bones, "head");
        }
    }
}
