package com.hp.thermalshockreforged.client.sopel;

import com.hp.thermalshockreforged.entity.sopel.*;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.renderer.texture.OverlayTexture;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.layer.GeoRenderLayer;

public final class HighDirectorSopelRenderer extends GeoEntityRenderer<HighDirectorSopelEntity> {
    public HighDirectorSopelRenderer(EntityRendererProvider.Context context) { super(context, new Model()); shadowRadius = .5F;  addRenderLayer(new Glow(this)); }
    public static void register(EntityRenderersEvent.RegisterRenderers event) { event.registerEntityRenderer(SopelEntityContent.SOPEL.get(), HighDirectorSopelRenderer::new); }
    @Override public RenderType getRenderType(HighDirectorSopelEntity entity, ResourceLocation texture, MultiBufferSource buffers, float partialTick) { return RenderType.entityTranslucent(texture); }
    @Override protected float getDeathMaxRotation(HighDirectorSopelEntity entity) { return 0; }
    private static final class Model extends GeoModel<HighDirectorSopelEntity> {
        @Override public ResourceLocation getModelResource(HighDirectorSopelEntity entity) { return SopelEntityContent.id("geo/sopel.geo.json"); }
        @Override public ResourceLocation getAnimationResource(HighDirectorSopelEntity entity) { return SopelEntityContent.id("animations/sopel.animation.json"); }
        @Override public ResourceLocation getTextureResource(HighDirectorSopelEntity entity) { return SopelEntityContent.id("textures/entities/" + entity.texture() + ".png"); }
    }

    private static final class Glow extends GeoRenderLayer<HighDirectorSopelEntity> {
        private Glow(HighDirectorSopelRenderer renderer) { super(renderer); }
        @Override public void render(PoseStack pose, HighDirectorSopelEntity entity, BakedGeoModel model, RenderType type, MultiBufferSource buffers, VertexConsumer vertices, float partialTick, int light, int overlay) { var glow = RenderType.eyes(SopelEntityContent.id("textures/entities/sopelglow.png")); getRenderer().reRender(model, pose, buffers, entity, glow, buffers.getBuffer(glow), partialTick, light, OverlayTexture.NO_OVERLAY, -1); }
    }
}
