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

public final class BastionRenderers {
    private BastionRenderers() { }
    public static void register(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(IceSquadContent.GUNNER.get(), context -> new Renderer(context, "icidrobastion", "head", 1, 0.5F, null));
        event.registerEntityRenderer(IceSquadContent.BURNER.get(), context -> new Renderer(context, "icidrobastion", "head", 1, 0.5F, null));
    }
    private static ResourceLocation resource(String path) { return ResourceLocation.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, path); }
    private static final class Model extends GeoModel<BastionEntity> {
        private final String id;
        private final String bone;
        private Model(String id, String bone) { this.id = id; this.bone = bone; }
        @Override public ResourceLocation getModelResource(BastionEntity entity) { return resource("geo/" + id + ".geo.json"); }
        @Override public ResourceLocation getAnimationResource(BastionEntity entity) { return resource("animations/" + id + ".animation.json"); }
        @Override public ResourceLocation getTextureResource(BastionEntity entity) { return resource("textures/entities/" + entity.texture() + ".png"); }
        @Override public void setCustomAnimations(BastionEntity entity, long instanceId, AnimationState<BastionEntity> state) {
            super.setCustomAnimations(entity, instanceId, state);
            if (bone == null) return;
            var current = getAnimationProcessor().getBone(bone); var data = state.getData(DataTickets.ENTITY_MODEL_DATA);
            if (current != null && data != null) { current.setRotX(data.headPitch() * Mth.DEG_TO_RAD); current.setRotY(data.netHeadYaw() * Mth.DEG_TO_RAD); }
        }
    }
    private static final class Renderer extends GeoEntityRenderer<BastionEntity> {
        private Renderer(EntityRendererProvider.Context context, String id, String bone, float scale, float shadow, String glow) {
            super(context, new Model(id, bone)); withScale(scale); shadowRadius = shadow; if (glow != null) addRenderLayer(new Glow(this, glow));
        }
        @Override public RenderType getRenderType(BastionEntity entity, ResourceLocation texture, MultiBufferSource buffers, float partialTick) { return RenderType.entityTranslucent(texture); }
    }
    private static final class Glow extends GeoRenderLayer<BastionEntity> {
        private final ResourceLocation texture;
        private Glow(GeoRenderer<BastionEntity> renderer, String texture) { super(renderer); this.texture = resource("textures/entities/" + texture + ".png"); }
        @Override public void render(PoseStack pose, BastionEntity entity, BakedGeoModel model, RenderType type, MultiBufferSource buffers, VertexConsumer vertices, float partialTick, int light, int overlay) {
            var glow = RenderType.eyes(texture);
            getRenderer().reRender(model, pose, buffers, entity, glow, buffers.getBuffer(glow), partialTick, light, OverlayTexture.NO_OVERLAY, -1);
        }
    }
}
