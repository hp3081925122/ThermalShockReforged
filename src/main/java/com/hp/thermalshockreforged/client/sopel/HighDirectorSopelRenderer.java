package com.hp.thermalshockreforged.client.sopel;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.rendertype.RenderType;

import com.hp.thermalshockreforged.entity.sopel.*;
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

public final class HighDirectorSopelRenderer extends LegacyGeoEntityRenderer<HighDirectorSopelEntity> {
    public HighDirectorSopelRenderer(EntityRendererProvider.Context context) { super(context, new Model()); shadowRadius = .5F;  addRenderLayer(new Glow(this)); }
    public static void register(EntityRenderersEvent.RegisterRenderers event) { event.registerEntityRenderer(SopelEntityContent.SOPEL.get(), HighDirectorSopelRenderer::new); }
    @Override public RenderType getRenderType(HighDirectorSopelEntity entity, Identifier texture, MultiBufferSource buffers, float partialTick) { return RenderTypes.entityTranslucent(texture); }
    protected float getDeathMaxRotation(HighDirectorSopelEntity entity) { return 0; }
    private static final class Model extends LegacyGeoModel<HighDirectorSopelEntity> {
        @Override public Identifier getModelResource(HighDirectorSopelEntity entity) { return SopelEntityContent.id("geo/sopel.geo.json"); }
        @Override public Identifier getAnimationResource(HighDirectorSopelEntity entity) { return SopelEntityContent.id("animations/sopel.animation.json"); }
        @Override public Identifier getTextureResource(HighDirectorSopelEntity entity) { return SopelEntityContent.id("textures/entities/" + entity.texture() + ".png"); }
    }

    private static final class Glow extends LegacyGeoRenderLayer<HighDirectorSopelEntity> {
        private Glow(HighDirectorSopelRenderer renderer) { super(renderer); }
        @Override public void render(PoseStack pose, HighDirectorSopelEntity entity, BakedGeoModel model, RenderType type, MultiBufferSource buffers, VertexConsumer vertices, float partialTick, int light, int overlay) { var glow = RenderTypes.eyes(SopelEntityContent.id("textures/entities/sopelglow.png")); getRenderer().reRender(model, pose, buffers, entity, glow, buffers.getBuffer(glow), partialTick, light, OverlayTexture.NO_OVERLAY, -1); }
    }
}
