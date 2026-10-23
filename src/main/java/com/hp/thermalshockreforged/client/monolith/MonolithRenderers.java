package com.hp.thermalshockreforged.client.monolith;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.rendertype.RenderType;

import com.hp.thermalshockreforged.entity.monolith.*;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import com.geckolib.animation.state.AnimationTest;
import com.geckolib.cache.model.BakedGeoModel;
import com.hp.thermalshockreforged.client.compat.LegacyGeoModel;
import com.hp.thermalshockreforged.client.compat.LegacyGeoEntityRenderer;
import com.hp.thermalshockreforged.client.compat.LegacyGeoRenderLayer;

public final class MonolithRenderers {
    public static void register(EntityRenderersEvent.RegisterRenderers event) { event.registerEntityRenderer(MonolithContent.ACTIVE.get(), ActiveRenderer::new); event.registerEntityRenderer(MonolithContent.DORMANT.get(), DormantRenderer::new); event.registerEntityRenderer(MonolithContent.TASK.get(), net.minecraft.client.renderer.entity.NoopRenderer::new); }
    private static Identifier resource(String path) { return MonolithContent.id(path); }
    private static final class Model<T extends MonolithBaseEntity> extends LegacyGeoModel<T> {
        private final boolean headTracking;
        private Model(boolean headTracking) { this.headTracking = headTracking; }
        @Override public Identifier getModelResource(T entity) { return resource("geo/monolith.geo.json"); }
        @Override public Identifier getAnimationResource(T entity) { return resource("animations/monolith.animation.json"); }
        @Override public Identifier getTextureResource(T entity) { return resource("textures/entities/" + entity.texture() + ".png"); }
        @Override public void setCustomAnimations(T entity, long instance, AnimationTest<T> state) { super.setCustomAnimations(entity, instance, state); if (headTracking) { var head = getAnimationProcessor().getBone("head"); var data = com.hp.thermalshockreforged.compat.LegacyAnimationData.from(state); if (head != null && data != null) { head.setRotX(data.headPitch() * Mth.DEG_TO_RAD); head.setRotY(data.netHeadYaw() * Mth.DEG_TO_RAD); } } }
    }
    private static class Renderer<T extends MonolithBaseEntity> extends LegacyGeoEntityRenderer<T> {
        private Renderer(EntityRendererProvider.Context context, boolean headTracking) { super(context, new Model<>(headTracking)); shadowRadius = .5F; }
        @Override public RenderType getRenderType(T entity, Identifier texture, MultiBufferSource buffers, float partialTick) { return RenderTypes.entityTranslucent(texture); }
        @Override public void preRender(PoseStack pose, T entity, BakedGeoModel model, MultiBufferSource buffers, VertexConsumer buffer, boolean isReRender, float partialTick, int light, int overlay, int color) { scaleWidth = 1.7F; scaleHeight = 1.7F; super.preRender(pose, entity, model, buffers, buffer, isReRender, partialTick, light, overlay, color); }
    }
    private static final class ActiveRenderer extends Renderer<MonolithEntity> {
        private ActiveRenderer(EntityRendererProvider.Context context) { super(context, true); addRenderLayer(new Glow(this)); }
        protected float getDeathMaxRotation(com.geckolib.renderer.base.GeoRenderState state) { return 0; }
    }
    private static final class DormantRenderer extends Renderer<DormantMonolithEntity> { private DormantRenderer(EntityRendererProvider.Context context) { super(context, false); } }
    private static final class Glow extends LegacyGeoRenderLayer<MonolithEntity> {
        private Glow(ActiveRenderer renderer) { super(renderer); }
        @Override public void render(PoseStack pose, MonolithEntity entity, BakedGeoModel model, RenderType type, MultiBufferSource buffers, VertexConsumer buffer, float partialTick, int light, int overlay) { var glow = RenderTypes.eyes(resource("textures/entities/monolith25glow.png")); getRenderer().reRender(model, pose, buffers, entity, glow, buffers.getBuffer(glow), partialTick, light, OverlayTexture.NO_OVERLAY, -1); }
    }
    private MonolithRenderers() { }
}
