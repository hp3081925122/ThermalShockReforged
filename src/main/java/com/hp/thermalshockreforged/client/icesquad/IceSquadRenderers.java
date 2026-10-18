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

public final class IceSquadRenderers {
    private IceSquadRenderers() { }
    public static void register(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(IceSquadContent.GRUNT.get(), context -> new Renderer(context, "icidrosentinel", "head", 0.9F, 0.5F, "gruntglow"));
        event.registerEntityRenderer(IceSquadContent.STRIDER_ATTACK.get(), ThrownItemRenderer::new);
        event.registerEntityRenderer(IceSquadContent.ALT_CRYONIC_PROJECTILE.get(), ThrownItemRenderer::new);
        event.registerEntityRenderer(IceSquadContent.GUNNER_BULLET.get(), ThrownItemRenderer::new);
        event.registerEntityRenderer(IceSquadContent.BURNER_FLAME.get(), ThrownItemRenderer::new);
    }
    private static ResourceLocation resource(String path) { return ResourceLocation.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, path); }
    private static final class Model extends GeoModel<IcidrogruntEntity> {
        private final String id;
        private final String bone;
        private Model(String id, String bone) { this.id = id; this.bone = bone; }
        @Override public ResourceLocation getModelResource(IcidrogruntEntity entity) { return resource("geo/" + id + ".geo.json"); }
        @Override public ResourceLocation getAnimationResource(IcidrogruntEntity entity) { return resource("animations/" + id + ".animation.json"); }
        @Override public ResourceLocation getTextureResource(IcidrogruntEntity entity) { return resource("textures/entities/" + entity.texture() + ".png"); }
        @Override public void setCustomAnimations(IcidrogruntEntity entity, long instanceId, AnimationState<IcidrogruntEntity> state) {
            super.setCustomAnimations(entity, instanceId, state);
            if (bone == null) return;
            var current = getAnimationProcessor().getBone(bone); var data = state.getData(DataTickets.ENTITY_MODEL_DATA);
            if (current != null && data != null) { current.setRotX(data.headPitch() * Mth.DEG_TO_RAD); current.setRotY(data.netHeadYaw() * Mth.DEG_TO_RAD); }
        }
    }
    private static final class Renderer extends GeoEntityRenderer<IcidrogruntEntity> {
        private Renderer(EntityRendererProvider.Context context, String id, String bone, float scale, float shadow, String glow) {
            super(context, new Model(id, bone)); withScale(scale); shadowRadius = shadow; if (glow != null) addRenderLayer(new Glow(this, glow));
        }
        @Override public RenderType getRenderType(IcidrogruntEntity entity, ResourceLocation texture, MultiBufferSource buffers, float partialTick) { return RenderType.entityTranslucent(texture); }
    }
    private static final class Glow extends GeoRenderLayer<IcidrogruntEntity> {
        private final ResourceLocation texture;
        private Glow(GeoRenderer<IcidrogruntEntity> renderer, String texture) { super(renderer); this.texture = resource("textures/entities/" + texture + ".png"); }
        @Override public void render(PoseStack pose, IcidrogruntEntity entity, BakedGeoModel model, RenderType type, MultiBufferSource buffers, VertexConsumer vertices, float partialTick, int light, int overlay) {
            var glow = RenderType.eyes(texture);
            getRenderer().reRender(model, pose, buffers, entity, glow, buffers.getBuffer(glow), partialTick, light, OverlayTexture.NO_OVERLAY, -1);
        }
    }
}
