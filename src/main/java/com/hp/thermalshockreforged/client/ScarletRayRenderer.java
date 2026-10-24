package com.hp.thermalshockreforged.client;
import com.hp.thermalshockreforged.client.geo.*;
import com.geckolib.renderer.base.GeoRenderState;
import com.geckolib.renderer.base.BoneSnapshots;
import net.minecraft.client.renderer.entity.state.EntityRenderState;

import net.minecraft.client.renderer.rendertype.RenderTypes;

import com.hp.thermalshockreforged.ThermalShockReforged;
import com.hp.thermalshockreforged.entity.ScarletRayEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;
import com.geckolib.cache.model.BakedGeoModel;

public final class ScarletRayRenderer extends TsrGeoEntityRenderer<ScarletRayEntity> {
    public ScarletRayRenderer(EntityRendererProvider.Context context) {
        super(context, new Model());
        shadowRadius = 0.4F;
        withScale(1.25F);
        withRenderLayer(new Glow(this));
    }

    @Override protected RenderType visibleRenderType(EntityRenderState state, Identifier texture) { return RenderTypes.entityTranslucent(texture); }

    private static final class Model extends TsrGeoModel<ScarletRayEntity> {
        @Override
        public Identifier getModelResource(ScarletRayEntity entity) {
            return Identifier.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, "geo/scarlet_ray.geo.json");
        }

        @Override
        public Identifier getTextureResource(ScarletRayEntity entity) {
            return Identifier.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, "textures/entities/" + entity.texture() + ".png");
        }

        @Override
        public Identifier getAnimationResource(ScarletRayEntity entity) {
            return Identifier.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, "animations/scarlet_ray.animation.json");
        }
    }

    private static final class Glow extends GeoEyesLayer<ScarletRayEntity, Void, EntityRenderState> {
        private static final Identifier TEXTURE = Identifier.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, "textures/entities/scarletrayglow.png");

        private Glow(ScarletRayRenderer renderer) {
            super(renderer);
        }

        @Override protected Identifier texture(ScarletRayEntity entity) { return TEXTURE; }
    }
}
