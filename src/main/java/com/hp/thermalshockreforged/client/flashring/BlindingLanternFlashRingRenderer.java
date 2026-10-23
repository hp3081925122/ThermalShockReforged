package com.hp.thermalshockreforged.client.flashring;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.rendertype.RenderType;

import com.hp.thermalshockreforged.entity.flashring.*;
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

public final class BlindingLanternFlashRingRenderer extends LegacyGeoEntityRenderer<BlindingLanternFlashRingEntity> {
    public BlindingLanternFlashRingRenderer(EntityRendererProvider.Context context) { super(context, new Model()); shadowRadius = 0;  addRenderLayer(new Glow(this)); }
    public static void register(EntityRenderersEvent.RegisterRenderers event) { event.registerEntityRenderer(BlindingLanternFlashRingContent.FLASH_RING.get(), BlindingLanternFlashRingRenderer::new); }
    @Override public RenderType getRenderType(BlindingLanternFlashRingEntity entity, Identifier texture, MultiBufferSource buffers, float partialTick) { return RenderTypes.entityTranslucent(texture); }
    private static final class Model extends LegacyGeoModel<BlindingLanternFlashRingEntity> {
        @Override public Identifier getModelResource(BlindingLanternFlashRingEntity entity) { return BlindingLanternFlashRingContent.id("geo/lantern_flash_ring.geo.json"); }
        @Override public Identifier getAnimationResource(BlindingLanternFlashRingEntity entity) { return BlindingLanternFlashRingContent.id("animations/lantern_flash_ring.animation.json"); }
        @Override public Identifier getTextureResource(BlindingLanternFlashRingEntity entity) { return BlindingLanternFlashRingContent.id("textures/entities/" + entity.texture() + ".png"); }
    }

    private static final class Glow extends LegacyGeoRenderLayer<BlindingLanternFlashRingEntity> {
        private Glow(BlindingLanternFlashRingRenderer renderer) { super(renderer); }
        @Override public void render(PoseStack pose, BlindingLanternFlashRingEntity entity, BakedGeoModel model, RenderType type, MultiBufferSource buffers, VertexConsumer vertices, float partialTick, int light, int overlay) { var glow = RenderTypes.eyes(BlindingLanternFlashRingContent.id("textures/entities/yellow_light_flash.png")); getRenderer().reRender(model, pose, buffers, entity, glow, buffers.getBuffer(glow), partialTick, light, OverlayTexture.NO_OVERLAY, -1); }
    }
}
