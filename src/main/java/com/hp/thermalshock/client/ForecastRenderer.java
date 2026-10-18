package com.hp.thermalshock.client;

import com.hp.thermalshock.ThermalShock;
import com.hp.thermalshock.entity.ForecastEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.renderer.layer.GeoRenderLayer;

public final class ForecastRenderer extends GeoEntityRenderer<ForecastEntity> {
    public ForecastRenderer(EntityRendererProvider.Context context) {
        super(context, new Model());
        shadowRadius = 0.2F;
        addRenderLayer(new Glow(this));
    }

    @Override
    public RenderType getRenderType(ForecastEntity entity, ResourceLocation texture, MultiBufferSource buffers, float partialTick) {
        return RenderType.entityTranslucent(texture);
    }

    private static final class Model extends GeoModel<ForecastEntity> {
        @Override
        public ResourceLocation getModelResource(ForecastEntity entity) {
            return ResourceLocation.fromNamespaceAndPath(ThermalShock.MOD_ID, "geo/forecast.geo.json");
        }

        @Override
        public ResourceLocation getTextureResource(ForecastEntity entity) {
            return ResourceLocation.fromNamespaceAndPath(ThermalShock.MOD_ID, "textures/entities/" + entity.texture() + ".png");
        }

        @Override
        public ResourceLocation getAnimationResource(ForecastEntity entity) {
            return ResourceLocation.fromNamespaceAndPath(ThermalShock.MOD_ID, "animations/forecast.animation.json");
        }
    }

    private static final class Glow extends GeoRenderLayer<ForecastEntity> {
        private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(ThermalShock.MOD_ID, "textures/entities/forecast_glow.png");

        private Glow(ForecastRenderer renderer) {
            super(renderer);
        }

        @Override
        public void render(PoseStack pose, ForecastEntity entity, BakedGeoModel model, RenderType type, MultiBufferSource buffers, VertexConsumer vertices, float partialTick, int light, int overlay) {
            RenderType glow = RenderType.eyes(TEXTURE);
            getRenderer().reRender(model, pose, buffers, entity, glow, buffers.getBuffer(glow), partialTick, 15728640, OverlayTexture.NO_OVERLAY, -1);
        }
    }
}
