package com.hp.thermalshockreforged.client.protoi;
import net.minecraft.client.renderer.rendertype.RenderTypes;

import com.hp.thermalshockreforged.ThermalShockReforged;
import com.hp.thermalshockreforged.entity.protoi.ProtoiEpsilonEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import com.geckolib.animation.state.AnimationTest;
import com.geckolib.cache.model.BakedGeoModel;
import com.hp.thermalshockreforged.client.compat.LegacyGeoModel;
import com.hp.thermalshockreforged.client.compat.LegacyGeoEntityRenderer;
import com.hp.thermalshockreforged.client.compat.LegacyGeoRenderLayer;

public final class ProtoiEpsilonRenderer extends LegacyGeoEntityRenderer<ProtoiEpsilonEntity> {
    public ProtoiEpsilonRenderer(EntityRendererProvider.Context context) { super(context, new Model()); shadowRadius = 1; addRenderLayer(new Glow(this)); }
    private static Identifier resource(String path) { return Identifier.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, path); }
    @Override public RenderType getRenderType(ProtoiEpsilonEntity entity, Identifier texture, MultiBufferSource buffers, float partialTick) { return RenderTypes.entityTranslucent(texture); }
    private static final class Model extends LegacyGeoModel<ProtoiEpsilonEntity> {
        @Override public Identifier getModelResource(ProtoiEpsilonEntity entity) { return resource("geo/protoi_epsilon.geo.json"); }
        @Override public Identifier getTextureResource(ProtoiEpsilonEntity entity) { return resource("textures/entities/" + entity.texture() + ".png"); }
        @Override public Identifier getAnimationResource(ProtoiEpsilonEntity entity) { return resource("animations/protoi_epsilon.animation.json"); }
        @Override public void setCustomAnimations(ProtoiEpsilonEntity entity, long instanceId, AnimationTest<ProtoiEpsilonEntity> state) {
            super.setCustomAnimations(entity, instanceId, state);
            var body = getAnimationProcessor().getBone("body");
            var data = com.hp.thermalshockreforged.compat.LegacyAnimationData.from(state);
            if (body != null && data != null) { body.setRotX(data.headPitch() * Mth.DEG_TO_RAD); body.setRotY(data.netHeadYaw() * Mth.DEG_TO_RAD); }
        }
    }
    private static final class Glow extends LegacyGeoRenderLayer<ProtoiEpsilonEntity> {
        private Glow(ProtoiEpsilonRenderer renderer) { super(renderer); }
        @Override public void render(PoseStack pose, ProtoiEpsilonEntity entity, BakedGeoModel model, RenderType type, MultiBufferSource buffers, VertexConsumer buffer, float partialTick, int light, int overlay) {
            RenderType glow = RenderTypes.eyes(resource("textures/entities/epsilon_glow.png"));
            getRenderer().reRender(model, pose, buffers, entity, glow, buffers.getBuffer(glow), partialTick, light, OverlayTexture.NO_OVERLAY, -1);
        }
    }
}
