package com.hp.thermalshockreforged.client.protoi;

import com.hp.thermalshockreforged.ThermalShockReforged;
import com.hp.thermalshockreforged.entity.protoi.ProtoiEpsilonEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.renderer.layer.GeoRenderLayer;

public final class ProtoiEpsilonRenderer extends GeoEntityRenderer<ProtoiEpsilonEntity> {
    public ProtoiEpsilonRenderer(EntityRendererProvider.Context context) { super(context, new Model()); shadowRadius = 1; addRenderLayer(new Glow(this)); }
    private static ResourceLocation resource(String path) { return ResourceLocation.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, path); }
    @Override public RenderType getRenderType(ProtoiEpsilonEntity entity, ResourceLocation texture, MultiBufferSource buffers, float partialTick) { return RenderType.entityTranslucent(texture); }
    private static final class Model extends GeoModel<ProtoiEpsilonEntity> {
        @Override public ResourceLocation getModelResource(ProtoiEpsilonEntity entity) { return resource("geo/protoi_epsilon.geo.json"); }
        @Override public ResourceLocation getTextureResource(ProtoiEpsilonEntity entity) { return resource("textures/entities/" + entity.texture() + ".png"); }
        @Override public ResourceLocation getAnimationResource(ProtoiEpsilonEntity entity) { return resource("animations/protoi_epsilon.animation.json"); }
        @Override public void setCustomAnimations(ProtoiEpsilonEntity entity, long instanceId, AnimationState<ProtoiEpsilonEntity> state) {
            super.setCustomAnimations(entity, instanceId, state);
            var body = getAnimationProcessor().getBone("body");
            var data = state.getData(DataTickets.ENTITY_MODEL_DATA);
            if (body != null && data != null) { body.setRotX(data.headPitch() * Mth.DEG_TO_RAD); body.setRotY(data.netHeadYaw() * Mth.DEG_TO_RAD); }
        }
    }
    private static final class Glow extends GeoRenderLayer<ProtoiEpsilonEntity> {
        private Glow(ProtoiEpsilonRenderer renderer) { super(renderer); }
        @Override public void render(PoseStack pose, ProtoiEpsilonEntity entity, BakedGeoModel model, RenderType type, MultiBufferSource buffers, VertexConsumer buffer, float partialTick, int light, int overlay) {
            RenderType glow = RenderType.eyes(resource("textures/entities/epsilon_glow.png"));
            getRenderer().reRender(model, pose, buffers, entity, glow, buffers.getBuffer(glow), partialTick, light, OverlayTexture.NO_OVERLAY, -1);
        }
    }
}
