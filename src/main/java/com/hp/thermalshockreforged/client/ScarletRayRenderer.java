package com.hp.thermalshockreforged.client;

import com.hp.thermalshockreforged.ThermalShockReforged;
import com.hp.thermalshockreforged.entity.ScarletRayEntity;
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

public final class ScarletRayRenderer extends GeoEntityRenderer<ScarletRayEntity> {
    public ScarletRayRenderer(EntityRendererProvider.Context context) {
        super(context, new Model());
        shadowRadius = 0.4F;
        withScale(1.25F);
        addRenderLayer(new Glow(this));
    }

    @Override
    public RenderType getRenderType(ScarletRayEntity entity, ResourceLocation texture, MultiBufferSource buffers, float partialTick) {
        return RenderType.entityTranslucent(texture);
    }

    private static final class Model extends GeoModel<ScarletRayEntity> {
        @Override
        public ResourceLocation getModelResource(ScarletRayEntity entity) {
            return ResourceLocation.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, "geo/scarlet_ray.geo.json");
        }

        @Override
        public ResourceLocation getTextureResource(ScarletRayEntity entity) {
            return ResourceLocation.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, "textures/entities/" + entity.texture() + ".png");
        }

        @Override
        public ResourceLocation getAnimationResource(ScarletRayEntity entity) {
            return ResourceLocation.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, "animations/scarlet_ray.animation.json");
        }
    }

    private static final class Glow extends GeoRenderLayer<ScarletRayEntity> {
        private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, "textures/entities/scarletrayglow.png");

        private Glow(ScarletRayRenderer renderer) {
            super(renderer);
        }

        @Override
        public void render(PoseStack pose, ScarletRayEntity entity, BakedGeoModel model, RenderType type, MultiBufferSource buffers, VertexConsumer vertices, float partialTick, int light, int overlay) {
            RenderType glow = RenderType.eyes(TEXTURE);
            getRenderer().reRender(model, pose, buffers, entity, glow, buffers.getBuffer(glow), partialTick, light, OverlayTexture.NO_OVERLAY, -1);
        }
    }
}
