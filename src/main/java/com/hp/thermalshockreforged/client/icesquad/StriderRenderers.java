package com.hp.thermalshockreforged.client.icesquad;

import com.hp.thermalshockreforged.ThermalShockReforged;
import com.hp.thermalshockreforged.entity.icesquad.*;
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

public final class StriderRenderers {
    private StriderRenderers() { }
    public static void register(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(IceSquadContent.STRIDER.get(), context -> new Renderer(context, "icidrovehicle", null, 1, 1, "striderglow"));
    }
    private static ResourceLocation resource(String path) { return ResourceLocation.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, path); }
    private static final class Model extends GeoModel<IcidrostriderEntity> {
        private final String id;
        private final String bone;
        private Model(String id, String bone) { this.id = id; this.bone = bone; }
        @Override public ResourceLocation getModelResource(IcidrostriderEntity entity) { return resource("geo/" + id + ".geo.json"); }
        @Override public ResourceLocation getAnimationResource(IcidrostriderEntity entity) { return resource("animations/" + id + ".animation.json"); }
        @Override public ResourceLocation getTextureResource(IcidrostriderEntity entity) { return resource("textures/entities/" + entity.texture() + ".png"); }
        @Override public void setCustomAnimations(IcidrostriderEntity entity, long instanceId, AnimationState<IcidrostriderEntity> state) {
            super.setCustomAnimations(entity, instanceId, state);
            if (bone == null) return;
            var current = getAnimationProcessor().getBone(bone); var data = state.getData(DataTickets.ENTITY_MODEL_DATA);
            if (current != null && data != null) { current.setRotX(data.headPitch() * Mth.DEG_TO_RAD); current.setRotY(data.netHeadYaw() * Mth.DEG_TO_RAD); }
        }
    }
    private static final class Renderer extends GeoEntityRenderer<IcidrostriderEntity> {
        private Renderer(EntityRendererProvider.Context context, String id, String bone, float scale, float shadow, String glow) {
            super(context, new Model(id, bone)); withScale(scale); shadowRadius = shadow; if (glow != null) addRenderLayer(new Glow(this, glow));
        }
        @Override protected float getDeathMaxRotation(IcidrostriderEntity entity) { return 0; }
        @Override public RenderType getRenderType(IcidrostriderEntity entity, ResourceLocation texture, MultiBufferSource buffers, float partialTick) { return RenderType.entityTranslucent(texture); }
    }
    private static final class Glow extends GeoRenderLayer<IcidrostriderEntity> {
        private final ResourceLocation texture;
        private Glow(GeoRenderer<IcidrostriderEntity> renderer, String texture) { super(renderer); this.texture = resource("textures/entities/" + texture + ".png"); }
        @Override public void render(PoseStack pose, IcidrostriderEntity entity, BakedGeoModel model, RenderType type, MultiBufferSource buffers, VertexConsumer vertices, float partialTick, int light, int overlay) {
            var glow = RenderType.eyes(texture);
            getRenderer().reRender(model, pose, buffers, entity, glow, buffers.getBuffer(glow), partialTick, light, OverlayTexture.NO_OVERLAY, -1);
        }
    }
}
