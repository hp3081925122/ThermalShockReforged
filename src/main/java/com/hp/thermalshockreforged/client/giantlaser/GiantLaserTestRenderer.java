package com.hp.thermalshockreforged.client.giantlaser;

import com.hp.thermalshockreforged.entity.giantlaser.*;
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

public final class GiantLaserTestRenderer extends GeoEntityRenderer<GiantLaserTestEntity> {
    public GiantLaserTestRenderer(EntityRendererProvider.Context context) { super(context, new Model()); shadowRadius = 0; withScale(3); addRenderLayer(new Glow(this)); }
    public static void register(EntityRenderersEvent.RegisterRenderers event) { event.registerEntityRenderer(GiantLaserTestContent.GIANT_LASER.get(), GiantLaserTestRenderer::new); }
    @Override public RenderType getRenderType(GiantLaserTestEntity entity, ResourceLocation texture, MultiBufferSource buffers, float partialTick) { return RenderType.entityTranslucent(texture); }
    private static final class Model extends GeoModel<GiantLaserTestEntity> {
        @Override public ResourceLocation getModelResource(GiantLaserTestEntity entity) { return GiantLaserTestContent.id("geo/giantbeacon.geo.json"); }
        @Override public ResourceLocation getAnimationResource(GiantLaserTestEntity entity) { return GiantLaserTestContent.id("animations/giantbeacon.animation.json"); }
        @Override public ResourceLocation getTextureResource(GiantLaserTestEntity entity) { return GiantLaserTestContent.id("textures/entities/" + entity.texture() + ".png"); }
    }

    private static final class Glow extends GeoRenderLayer<GiantLaserTestEntity> {
        private Glow(GiantLaserTestRenderer renderer) { super(renderer); }
        @Override public void render(PoseStack pose, GiantLaserTestEntity entity, BakedGeoModel model, RenderType type, MultiBufferSource buffers, VertexConsumer vertices, float partialTick, int light, int overlay) { var glow = RenderType.eyes(GiantLaserTestContent.id("textures/entities/giantbeacon.png")); getRenderer().reRender(model, pose, buffers, entity, glow, buffers.getBuffer(glow), partialTick, light, OverlayTexture.NO_OVERLAY, -1); }
    }
}
