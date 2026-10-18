package com.hp.thermalshockreforged.client.ice;

import com.hp.thermalshockreforged.ThermalShockReforged;
import com.hp.thermalshockreforged.entity.ice.*;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.entity.*;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.*;
import software.bernie.geckolib.renderer.layer.GeoRenderLayer;

public final class IceRenderers {
    private IceRenderers() { }
    public static void register(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(IceContent.FROSTILYTE_SOLDIER.get(), context -> new Renderer(context, "frostilyte_soldier_rubicon", "head", 1.1F, 0.5F, null));
        event.registerEntityRenderer(IceContent.FROSTILYTE_CRYOMANCER.get(), context -> new Renderer(context, "frostilyte_staff", "head", 1, 0.5F, "cryomancerglow"));
        event.registerEntityRenderer(IceContent.ICIDRONE.get(), context -> new Renderer(context, "icidrone_rubicon", "body", 1, 0.5F, null));
        event.registerEntityRenderer(IceContent.ICIDROZERK.get(), context -> new Renderer(context, "novazerk", null, 1, 1, "novazerk_glow"));
        event.registerEntityRenderer(IceContent.ICE_BULLET.get(), ThrownItemRenderer::new);
        event.registerEntityRenderer(IceContent.SOLDIER_COMBO.get(), NoopRenderer::new);
    }
    private static ResourceLocation resource(String path) { return ResourceLocation.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, path); }
    private static final class Model extends GeoModel<IceCreatureEntity> {
        private final String id;
        private final String bone;
        private Model(String id, String bone) { this.id = id; this.bone = bone; }
        @Override public ResourceLocation getModelResource(IceCreatureEntity entity) { return resource("geo/" + id + ".geo.json"); }
        @Override public ResourceLocation getAnimationResource(IceCreatureEntity entity) { return resource("animations/" + id + ".animation.json"); }
        @Override public ResourceLocation getTextureResource(IceCreatureEntity entity) { return resource("textures/entities/" + entity.texture() + ".png"); }
        @Override public void setCustomAnimations(IceCreatureEntity entity, long instanceId, AnimationState<IceCreatureEntity> state) {
            super.setCustomAnimations(entity, instanceId, state);
            if (bone == null) return;
            var current = getAnimationProcessor().getBone(bone); var data = state.getData(DataTickets.ENTITY_MODEL_DATA);
            if (current != null && data != null) { current.setRotX(data.headPitch() * Mth.DEG_TO_RAD); current.setRotY(data.netHeadYaw() * Mth.DEG_TO_RAD); }
        }
    }
    private static final class Renderer extends GeoEntityRenderer<IceCreatureEntity> {
        private Renderer(EntityRendererProvider.Context context, String id, String bone, float scale, float shadow, String glow) {
            super(context, new Model(id, bone)); withScale(scale); shadowRadius = shadow; if (glow != null) addRenderLayer(new Glow(this, glow));
        }
        @Override public RenderType getRenderType(IceCreatureEntity entity, ResourceLocation texture, MultiBufferSource buffers, float partialTick) { return RenderType.entityTranslucent(texture); }
    }
    private static final class Glow extends GeoRenderLayer<IceCreatureEntity> {
        private final ResourceLocation texture;
        private Glow(GeoRenderer<IceCreatureEntity> renderer, String texture) { super(renderer); this.texture = resource("textures/entities/" + texture + ".png"); }
        @Override public void render(PoseStack pose, IceCreatureEntity entity, BakedGeoModel model, RenderType type, MultiBufferSource buffers, VertexConsumer vertices, float partialTick, int light, int overlay) {
            var glow = RenderType.eyes(texture);
            getRenderer().reRender(model, pose, buffers, entity, glow, buffers.getBuffer(glow), partialTick, light, OverlayTexture.NO_OVERLAY, -1);
        }
    }
}
