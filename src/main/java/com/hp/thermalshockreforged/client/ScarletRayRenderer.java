package com.hp.thermalshockreforged.client;
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
import com.hp.thermalshockreforged.client.compat.LegacyGeoModel;
import com.hp.thermalshockreforged.client.compat.LegacyGeoEntityRenderer;
import com.hp.thermalshockreforged.client.compat.LegacyGeoRenderLayer;

public final class ScarletRayRenderer extends LegacyGeoEntityRenderer<ScarletRayEntity> {
    public ScarletRayRenderer(EntityRendererProvider.Context context) {
        super(context, new Model());
        shadowRadius = 0.4F;
        withScale(1.25F);
        addRenderLayer(new Glow(this));
    }

    @Override
    public RenderType getRenderType(ScarletRayEntity entity, Identifier texture, MultiBufferSource buffers, float partialTick) {
        return RenderTypes.entityTranslucent(texture);
    }

    private static final class Model extends LegacyGeoModel<ScarletRayEntity> {
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

    private static final class Glow extends LegacyGeoRenderLayer<ScarletRayEntity> {
        private static final Identifier TEXTURE = Identifier.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, "textures/entities/scarletrayglow.png");

        private Glow(ScarletRayRenderer renderer) {
            super(renderer);
        }

        @Override
        public void render(PoseStack pose, ScarletRayEntity entity, BakedGeoModel model, RenderType type, MultiBufferSource buffers, VertexConsumer vertices, float partialTick, int light, int overlay) {
            RenderType glow = RenderTypes.eyes(TEXTURE);
            getRenderer().reRender(model, pose, buffers, entity, glow, buffers.getBuffer(glow), partialTick, light, OverlayTexture.NO_OVERLAY, -1);
        }
    }
}
