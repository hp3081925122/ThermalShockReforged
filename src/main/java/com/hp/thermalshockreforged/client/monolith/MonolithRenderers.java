package com.hp.thermalshockreforged.client.monolith;

import com.hp.thermalshockreforged.entity.monolith.*;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
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

public final class MonolithRenderers {
    public static void register(EntityRenderersEvent.RegisterRenderers event) { event.registerEntityRenderer(MonolithContent.ACTIVE.get(), ActiveRenderer::new); event.registerEntityRenderer(MonolithContent.DORMANT.get(), DormantRenderer::new); event.registerEntityRenderer(MonolithContent.TASK.get(), net.minecraft.client.renderer.entity.NoopRenderer::new); }
    private static ResourceLocation resource(String path) { return MonolithContent.id(path); }
    private static final class Model<T extends MonolithBaseEntity> extends GeoModel<T> {
        private final boolean headTracking;
        private Model(boolean headTracking) { this.headTracking = headTracking; }
        @Override public ResourceLocation getModelResource(T entity) { return resource("geo/monolith.geo.json"); }
        @Override public ResourceLocation getAnimationResource(T entity) { return resource("animations/monolith.animation.json"); }
        @Override public ResourceLocation getTextureResource(T entity) { return resource("textures/entities/" + entity.texture() + ".png"); }
        @Override public void setCustomAnimations(T entity, long instance, AnimationState<T> state) { super.setCustomAnimations(entity, instance, state); if (headTracking) { var head = getAnimationProcessor().getBone("head"); var data = state.getData(DataTickets.ENTITY_MODEL_DATA); if (head != null && data != null) { head.setRotX(data.headPitch() * Mth.DEG_TO_RAD); head.setRotY(data.netHeadYaw() * Mth.DEG_TO_RAD); } } }
    }
    private static class Renderer<T extends MonolithBaseEntity> extends GeoEntityRenderer<T> {
        private Renderer(EntityRendererProvider.Context context, boolean headTracking) { super(context, new Model<>(headTracking)); shadowRadius = .5F; }
        @Override public RenderType getRenderType(T entity, ResourceLocation texture, MultiBufferSource buffers, float partialTick) { return RenderType.entityTranslucent(texture); }
        @Override public void preRender(PoseStack pose, T entity, BakedGeoModel model, MultiBufferSource buffers, VertexConsumer buffer, boolean isReRender, float partialTick, int light, int overlay, int color) { scaleWidth = 1.7F; scaleHeight = 1.7F; super.preRender(pose, entity, model, buffers, buffer, isReRender, partialTick, light, overlay, color); }
    }
    private static final class ActiveRenderer extends Renderer<MonolithEntity> {
        private ActiveRenderer(EntityRendererProvider.Context context) { super(context, true); addRenderLayer(new Glow(this)); }
        @Override protected float getDeathMaxRotation(MonolithEntity entity) { return 0; }
    }
    private static final class DormantRenderer extends Renderer<DormantMonolithEntity> { private DormantRenderer(EntityRendererProvider.Context context) { super(context, false); } }
    private static final class Glow extends GeoRenderLayer<MonolithEntity> {
        private Glow(ActiveRenderer renderer) { super(renderer); }
        @Override public void render(PoseStack pose, MonolithEntity entity, BakedGeoModel model, RenderType type, MultiBufferSource buffers, VertexConsumer buffer, float partialTick, int light, int overlay) { var glow = RenderType.eyes(resource("textures/entities/monolith25glow.png")); getRenderer().reRender(model, pose, buffers, entity, glow, buffers.getBuffer(glow), partialTick, light, OverlayTexture.NO_OVERLAY, -1); }
    }
    private MonolithRenderers() { }
}
