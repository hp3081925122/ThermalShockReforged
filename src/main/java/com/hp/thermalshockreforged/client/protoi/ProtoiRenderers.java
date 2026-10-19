package com.hp.thermalshockreforged.client.protoi;

import com.hp.thermalshockreforged.ThermalShockReforged;
import com.hp.thermalshockreforged.entity.protoi.*;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.util.Mth;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.renderer.layer.GeoRenderLayer;
import com.hp.thermalshockreforged.entity.magnetite.MagnetiteContent;
import com.hp.thermalshockreforged.client.legacy.LegacyMonsterRenderers;

public final class ProtoiRenderers {
    private ProtoiRenderers() { }
    public static void register(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(ProtoiContent.GAMMA.get(), context -> new Renderer(context, "protoi_gamma", 1, .3F));
        event.registerEntityRenderer(ProtoiContent.DELTA.get(), context -> new Renderer(context, "protoi_delta", 1, .8F));
        event.registerEntityRenderer(ProtoiContent.EPSILON.get(), ProtoiEpsilonRenderer::new);
        event.registerEntityRenderer(ProtoiContent.EPSILON_ATTACK.get(), net.minecraft.client.renderer.entity.NoopRenderer::new);
        event.registerEntityRenderer(ProtoiContent.DELTA_VOLLEY.get(), net.minecraft.client.renderer.entity.NoopRenderer::new);
        event.registerEntityRenderer(ProtoiContent.SUNBEAM_ATTACK.get(), net.minecraft.client.renderer.entity.NoopRenderer::new);
        event.registerEntityRenderer(ProtoiContent.SUNBEAM.get(), context -> new Renderer(context, "protoi_sunbeam", 1, 1));
        event.registerEntityRenderer(ProtoiContent.SUNBEAM_BULLET.get(), ThrownItemRenderer::new);
        event.registerEntityRenderer(MagnetiteContent.BULLET.get(), ThrownItemRenderer::new);
        LegacyMonsterRenderers.register(event);
    }
    private static final class Model extends GeoModel<ProtoiEntityBase> {
        private final String id;
        private Model(String id) { this.id = id; }
        @Override public ResourceLocation getModelResource(ProtoiEntityBase entity) { return ResourceLocation.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, "geo/" + id + ".geo.json"); }
        @Override public ResourceLocation getTextureResource(ProtoiEntityBase entity) { return ResourceLocation.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, "textures/entities/" + entity.texture() + ".png"); }
        @Override public ResourceLocation getAnimationResource(ProtoiEntityBase entity) { return ResourceLocation.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, "animations/" + id + ".animation.json"); }
        @Override public void setCustomAnimations(ProtoiEntityBase entity, long instanceId, AnimationState<ProtoiEntityBase> state) {
            super.setCustomAnimations(entity, instanceId, state);
            if (entity instanceof ProtoiDeltaEntity || entity instanceof ProtoiSunbeamEntity) {
                var head = getAnimationProcessor().getBone("head");
                var data = state.getData(DataTickets.ENTITY_MODEL_DATA);
                if (head != null && data != null) { head.setRotX(data.headPitch() * Mth.DEG_TO_RAD); head.setRotY(data.netHeadYaw() * Mth.DEG_TO_RAD); }
            }
        }
    }
    private static final class Renderer extends GeoEntityRenderer<ProtoiEntityBase> {
        private Renderer(EntityRendererProvider.Context context, String id, float scale, float shadow) { super(context, new Model(id)); withScale(scale); shadowRadius = shadow; if (id.equals("protoi_sunbeam")) addRenderLayer(new SunbeamGlow(this)); }
        @Override public RenderType getRenderType(ProtoiEntityBase entity, ResourceLocation texture, MultiBufferSource buffers, float partialTick) { return entity instanceof ProtoiGammaEntity || entity instanceof ProtoiDeltaEntity || entity instanceof ProtoiSunbeamEntity ? RenderType.entityTranslucent(texture) : super.getRenderType(entity, texture, buffers, partialTick); }
        @Override protected float getDeathMaxRotation(ProtoiEntityBase entity) { return entity instanceof ProtoiSunbeamEntity ? 0 : super.getDeathMaxRotation(entity); }
    }
    private static final class SunbeamGlow extends GeoRenderLayer<ProtoiEntityBase> {
        private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, "textures/entities/sunbeam_glow.png");
        private SunbeamGlow(Renderer renderer) { super(renderer); }
        @Override public void render(PoseStack pose, ProtoiEntityBase entity, BakedGeoModel model, RenderType type, MultiBufferSource buffers, VertexConsumer buffer, float partialTick, int light, int overlay) {
            RenderType glow = RenderType.eyes(TEXTURE);
            getRenderer().reRender(model, pose, buffers, entity, glow, buffers.getBuffer(glow), partialTick, light, OverlayTexture.NO_OVERLAY, -1);
        }
    }
}
