package com.hp.thermalshockreforged.client.benzor;

import com.hp.thermalshockreforged.entity.benzor.*;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.renderer.layer.GeoRenderLayer;

public final class BenzorRenderer extends GeoEntityRenderer<BenzorEntity> {
    public BenzorRenderer(EntityRendererProvider.Context context) { super(context, new Model()); shadowRadius = 2.2F; addRenderLayer(new Glow(this)); }
    public static void register(EntityRenderersEvent.RegisterRenderers event) { event.registerEntityRenderer(BenzorContent.BENZOR.get(), BenzorRenderer::new); }
    @Override public RenderType getRenderType(BenzorEntity entity, ResourceLocation texture, MultiBufferSource buffers, float partialTick) { return RenderType.entityTranslucent(texture); }
    private static final class Model extends GeoModel<BenzorEntity> {
        @Override public ResourceLocation getModelResource(BenzorEntity entity) { return BenzorContent.id("geo/benzor.geo.json"); }
        @Override public ResourceLocation getAnimationResource(BenzorEntity entity) { return BenzorContent.id("animations/benzor.animation.json"); }
        @Override public ResourceLocation getTextureResource(BenzorEntity entity) { return BenzorContent.id("textures/entities/" + entity.texture() + ".png"); }
        @Override public void setCustomAnimations(BenzorEntity entity, long instanceId, AnimationState<BenzorEntity> state) { super.setCustomAnimations(entity, instanceId, state); var head = getAnimationProcessor().getBone("head"); var data = state.getData(DataTickets.ENTITY_MODEL_DATA); if (head != null && data != null) { head.setRotX(data.headPitch() * Mth.DEG_TO_RAD); head.setRotY(data.netHeadYaw() * Mth.DEG_TO_RAD); } }
    }
    private static final class Glow extends GeoRenderLayer<BenzorEntity> {
        private Glow(BenzorRenderer renderer) { super(renderer); }
        @Override public void render(PoseStack pose, BenzorEntity entity, BakedGeoModel model, RenderType type, MultiBufferSource buffers, VertexConsumer vertices, float partialTick, int light, int overlay) { var glow = RenderType.eyes(BenzorContent.id("textures/entities/benzorglow.png")); getRenderer().reRender(model, pose, buffers, entity, glow, buffers.getBuffer(glow), partialTick, light, OverlayTexture.NO_OVERLAY, -1); }
    }
}
