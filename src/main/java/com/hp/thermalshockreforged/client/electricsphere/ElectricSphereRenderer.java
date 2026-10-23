package com.hp.thermalshockreforged.client.electricsphere;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.rendertype.RenderType;

import com.hp.thermalshockreforged.entity.electricsphere.*;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;
import com.geckolib.cache.model.BakedGeoModel;
import com.hp.thermalshockreforged.client.compat.LegacyGeoModel;
import com.hp.thermalshockreforged.client.compat.LegacyGeoEntityRenderer;
import com.hp.thermalshockreforged.client.compat.LegacyGeoRenderLayer;

public final class ElectricSphereRenderer extends LegacyGeoEntityRenderer<ElectricSphereEntity> {
    public ElectricSphereRenderer(EntityRendererProvider.Context context) { super(context, new Model()); shadowRadius = 0; addRenderLayer(new Glow(this)); }
    @Override public RenderType getRenderType(ElectricSphereEntity entity, Identifier texture, MultiBufferSource buffers, float partialTick) { return RenderTypes.entityTranslucent(texture); }
    private static final class Model extends LegacyGeoModel<ElectricSphereEntity> {
        @Override public Identifier getModelResource(ElectricSphereEntity entity) { return ElectricSphereContent.id("geo/electric_sphere.geo.json"); }
        @Override public Identifier getAnimationResource(ElectricSphereEntity entity) { return ElectricSphereContent.id("animations/electric_sphere.animation.json"); }
        @Override public Identifier getTextureResource(ElectricSphereEntity entity) { return ElectricSphereContent.id("textures/entities/" + entity.texture() + ".png"); }
    }
    private static final class Glow extends LegacyGeoRenderLayer<ElectricSphereEntity> {
        Glow(ElectricSphereRenderer renderer) { super(renderer); }
        @Override public void render(PoseStack pose, ElectricSphereEntity entity, BakedGeoModel model, RenderType type, MultiBufferSource buffers, VertexConsumer buffer, float partialTick, int light, int overlay) {
            var glow = RenderTypes.eyes(ElectricSphereContent.id("textures/entities/electric_sphere_glow.png"));
            getRenderer().reRender(model, pose, buffers, entity, glow, buffers.getBuffer(glow), partialTick, light, OverlayTexture.NO_OVERLAY, -1);
        }
    }
}
