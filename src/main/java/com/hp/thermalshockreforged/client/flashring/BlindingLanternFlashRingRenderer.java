package com.hp.thermalshockreforged.client.flashring;

import com.hp.thermalshockreforged.entity.flashring.*;
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

public final class BlindingLanternFlashRingRenderer extends GeoEntityRenderer<BlindingLanternFlashRingEntity> {
    public BlindingLanternFlashRingRenderer(EntityRendererProvider.Context context) { super(context, new Model()); shadowRadius = 0;  addRenderLayer(new Glow(this)); }
    public static void register(EntityRenderersEvent.RegisterRenderers event) { event.registerEntityRenderer(BlindingLanternFlashRingContent.FLASH_RING.get(), BlindingLanternFlashRingRenderer::new); }
    @Override public RenderType getRenderType(BlindingLanternFlashRingEntity entity, ResourceLocation texture, MultiBufferSource buffers, float partialTick) { return RenderType.entityTranslucent(texture); }
    private static final class Model extends GeoModel<BlindingLanternFlashRingEntity> {
        @Override public ResourceLocation getModelResource(BlindingLanternFlashRingEntity entity) { return BlindingLanternFlashRingContent.id("geo/lantern_flash_ring.geo.json"); }
        @Override public ResourceLocation getAnimationResource(BlindingLanternFlashRingEntity entity) { return BlindingLanternFlashRingContent.id("animations/lantern_flash_ring.animation.json"); }
        @Override public ResourceLocation getTextureResource(BlindingLanternFlashRingEntity entity) { return BlindingLanternFlashRingContent.id("textures/entities/" + entity.texture() + ".png"); }
    }

    private static final class Glow extends GeoRenderLayer<BlindingLanternFlashRingEntity> {
        private Glow(BlindingLanternFlashRingRenderer renderer) { super(renderer); }
        @Override public void render(PoseStack pose, BlindingLanternFlashRingEntity entity, BakedGeoModel model, RenderType type, MultiBufferSource buffers, VertexConsumer vertices, float partialTick, int light, int overlay) { var glow = RenderType.eyes(BlindingLanternFlashRingContent.id("textures/entities/yellow_light_flash.png")); getRenderer().reRender(model, pose, buffers, entity, glow, buffers.getBuffer(glow), partialTick, light, OverlayTexture.NO_OVERLAY, -1); }
    }
}
