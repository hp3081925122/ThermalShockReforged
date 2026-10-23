package com.hp.thermalshockreforged.client.benzor;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.rendertype.RenderType;

import com.hp.thermalshockreforged.entity.benzor.*;
import com.mojang.blaze3d.vertex.*;
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

public final class BenzorRenderer extends LegacyGeoEntityRenderer<BenzorEntity> {
    public BenzorRenderer(EntityRendererProvider.Context context) { super(context, new Model()); shadowRadius = 2.2F; addRenderLayer(new Glow(this)); }
    public static void register(EntityRenderersEvent.RegisterRenderers event) { event.registerEntityRenderer(BenzorContent.BENZOR.get(), BenzorRenderer::new); }
    @Override public RenderType getRenderType(BenzorEntity entity, Identifier texture, MultiBufferSource buffers, float partialTick) { return RenderTypes.entityTranslucent(texture); }
    private static final class Model extends LegacyGeoModel<BenzorEntity> {
        @Override public Identifier getModelResource(BenzorEntity entity) { return BenzorContent.id("geo/benzor.geo.json"); }
        @Override public Identifier getAnimationResource(BenzorEntity entity) { return BenzorContent.id("animations/benzor.animation.json"); }
        @Override public Identifier getTextureResource(BenzorEntity entity) { return BenzorContent.id("textures/entities/" + entity.texture() + ".png"); }
        @Override public void setCustomAnimations(BenzorEntity entity, long instanceId, AnimationTest<BenzorEntity> state) { super.setCustomAnimations(entity, instanceId, state); var head = getAnimationProcessor().getBone("head"); var data = com.hp.thermalshockreforged.compat.LegacyAnimationData.from(state); if (head != null && data != null) { head.setRotX(data.headPitch() * Mth.DEG_TO_RAD); head.setRotY(data.netHeadYaw() * Mth.DEG_TO_RAD); } }
    }
    private static final class Glow extends LegacyGeoRenderLayer<BenzorEntity> {
        private Glow(BenzorRenderer renderer) { super(renderer); }
        @Override public void render(PoseStack pose, BenzorEntity entity, BakedGeoModel model, RenderType type, MultiBufferSource buffers, VertexConsumer vertices, float partialTick, int light, int overlay) { var glow = RenderTypes.eyes(BenzorContent.id("textures/entities/benzorglow.png")); getRenderer().reRender(model, pose, buffers, entity, glow, buffers.getBuffer(glow), partialTick, light, OverlayTexture.NO_OVERLAY, -1); }
    }
}
