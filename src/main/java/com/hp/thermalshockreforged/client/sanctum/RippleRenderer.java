package com.hp.thermalshockreforged.client.sanctum;

import com.hp.thermalshockreforged.ThermalShockReforged;
import com.hp.thermalshockreforged.entity.sanctum.RippleEntity;
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

public final class RippleRenderer extends GeoEntityRenderer<RippleEntity> {
    public RippleRenderer(EntityRendererProvider.Context context) {
        super(context, new Model()); shadowRadius = 0.5F; withScale(1); addRenderLayer(new Glow(this));
    }
    private static ResourceLocation resource(String path) { return ResourceLocation.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, path); }
    @Override public RenderType getRenderType(RippleEntity entity, ResourceLocation texture, MultiBufferSource buffers, float partialTick) { return RenderType.entityTranslucent(texture); }
    private static final class Model extends GeoModel<RippleEntity> {
        @Override public ResourceLocation getModelResource(RippleEntity entity) { return resource("geo/trojan.geo.json"); }
        @Override public ResourceLocation getAnimationResource(RippleEntity entity) { return resource("animations/trojan.animation.json"); }
        @Override public ResourceLocation getTextureResource(RippleEntity entity) { return resource("textures/entities/" + entity.texture() + ".png"); }

    }
    private static final class Glow extends GeoRenderLayer<RippleEntity> {
        private static final ResourceLocation TEXTURE = resource("textures/entities/ripple_glow.png");
        private Glow(RippleRenderer renderer) { super(renderer); }
        @Override public void render(PoseStack pose, RippleEntity entity, BakedGeoModel model, RenderType type, MultiBufferSource buffers, VertexConsumer vertices, float partialTick, int light, int overlay) {
            RenderType glow = RenderType.eyes(TEXTURE);
            getRenderer().reRender(model, pose, buffers, entity, glow, buffers.getBuffer(glow), partialTick, light, OverlayTexture.NO_OVERLAY, -1);
        }
    }
}
