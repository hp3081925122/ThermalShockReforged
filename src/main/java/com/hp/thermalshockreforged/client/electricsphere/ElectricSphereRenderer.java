package com.hp.thermalshockreforged.client.electricsphere;

import com.hp.thermalshockreforged.entity.electricsphere.*;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.renderer.layer.GeoRenderLayer;

public final class ElectricSphereRenderer extends GeoEntityRenderer<ElectricSphereEntity> {
    public ElectricSphereRenderer(EntityRendererProvider.Context context) { super(context, new Model()); shadowRadius = 0; addRenderLayer(new Glow(this)); }
    @Override public RenderType getRenderType(ElectricSphereEntity entity, ResourceLocation texture, MultiBufferSource buffers, float partialTick) { return RenderType.entityTranslucent(texture); }
    private static final class Model extends GeoModel<ElectricSphereEntity> {
        @Override public ResourceLocation getModelResource(ElectricSphereEntity entity) { return ElectricSphereContent.id("geo/electric_sphere.geo.json"); }
        @Override public ResourceLocation getAnimationResource(ElectricSphereEntity entity) { return ElectricSphereContent.id("animations/electric_sphere.animation.json"); }
        @Override public ResourceLocation getTextureResource(ElectricSphereEntity entity) { return ElectricSphereContent.id("textures/entities/" + entity.texture() + ".png"); }
    }
    private static final class Glow extends GeoRenderLayer<ElectricSphereEntity> {
        Glow(ElectricSphereRenderer renderer) { super(renderer); }
        @Override public void render(PoseStack pose, ElectricSphereEntity entity, BakedGeoModel model, RenderType type, MultiBufferSource buffers, VertexConsumer buffer, float partialTick, int light, int overlay) {
            var glow = RenderType.eyes(ElectricSphereContent.id("textures/entities/electric_sphere_glow.png"));
            getRenderer().reRender(model, pose, buffers, entity, glow, buffers.getBuffer(glow), partialTick, light, OverlayTexture.NO_OVERLAY, -1);
        }
    }
}
