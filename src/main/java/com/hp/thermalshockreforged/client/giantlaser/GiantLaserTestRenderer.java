package com.hp.thermalshockreforged.client.giantlaser;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.rendertype.RenderType;

import com.hp.thermalshockreforged.entity.giantlaser.*;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import com.hp.thermalshockreforged.client.compat.LegacyGeoModel;
import com.hp.thermalshockreforged.client.compat.LegacyGeoEntityRenderer;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.renderer.texture.OverlayTexture;
import com.geckolib.cache.model.BakedGeoModel;
import com.hp.thermalshockreforged.client.compat.LegacyGeoRenderLayer;

public final class GiantLaserTestRenderer extends LegacyGeoEntityRenderer<GiantLaserTestEntity> {
    public GiantLaserTestRenderer(EntityRendererProvider.Context context) { super(context, new Model()); shadowRadius = 0; withScale(3); addRenderLayer(new Glow(this)); }
    public static void register(EntityRenderersEvent.RegisterRenderers event) { event.registerEntityRenderer(GiantLaserTestContent.GIANT_LASER.get(), GiantLaserTestRenderer::new); }
    @Override public RenderType getRenderType(GiantLaserTestEntity entity, Identifier texture, MultiBufferSource buffers, float partialTick) { return RenderTypes.entityTranslucent(texture); }
    private static final class Model extends LegacyGeoModel<GiantLaserTestEntity> {
        @Override public Identifier getModelResource(GiantLaserTestEntity entity) { return GiantLaserTestContent.id("geo/giantbeacon.geo.json"); }
        @Override public Identifier getAnimationResource(GiantLaserTestEntity entity) { return GiantLaserTestContent.id("animations/giantbeacon.animation.json"); }
        @Override public Identifier getTextureResource(GiantLaserTestEntity entity) { return GiantLaserTestContent.id("textures/entities/" + entity.texture() + ".png"); }
    }

    private static final class Glow extends LegacyGeoRenderLayer<GiantLaserTestEntity> {
        private Glow(GiantLaserTestRenderer renderer) { super(renderer); }
        @Override public void render(PoseStack pose, GiantLaserTestEntity entity, BakedGeoModel model, RenderType type, MultiBufferSource buffers, VertexConsumer vertices, float partialTick, int light, int overlay) { var glow = RenderTypes.eyes(GiantLaserTestContent.id("textures/entities/giantbeacon.png")); getRenderer().reRender(model, pose, buffers, entity, glow, buffers.getBuffer(glow), partialTick, light, OverlayTexture.NO_OVERLAY, -1); }
    }
}
