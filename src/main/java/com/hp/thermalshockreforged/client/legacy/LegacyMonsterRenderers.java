package com.hp.thermalshockreforged.client.legacy;

import com.hp.thermalshockreforged.ThermalShockReforged;
import com.hp.thermalshockreforged.entity.base.AnimatedMonsterEntity;
import com.hp.thermalshockreforged.entity.legacy.LegacyContent;
import com.hp.thermalshockreforged.entity.legacy.SolaEntity;
import com.hp.thermalshockreforged.entity.legacy.LilJellyEntity;
import com.hp.thermalshockreforged.entity.magnetite.MagnetiteContent;
import com.hp.thermalshockreforged.entity.magnetite.MagnetiteEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
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

public final class LegacyMonsterRenderers {
    private LegacyMonsterRenderers() { }
    public static void register(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(LegacyContent.SOLA.get(), context -> new Renderer(context, "sola", .3F, "sola"));
        event.registerEntityRenderer(MagnetiteContent.MAGNETITE.get(), context -> new Renderer(context, "magnetite", .5F, "magnetite_glow"));
        event.registerEntityRenderer(LegacyContent.SPECTRE.get(), context -> new Renderer(context, "specter", .5F, "specter"));
        event.registerEntityRenderer(LegacyContent.LIL_JELLY.get(), context -> new Renderer(context, "liljelly", .2F, null));
    }

    private static final class Model extends GeoModel<AnimatedMonsterEntity> {
        private final ResourceLocation model;
        private final ResourceLocation animation;
        private Model(String id) {
            model = ResourceLocation.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, "geo/" + id + ".geo.json");
            animation = ResourceLocation.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, "animations/" + id + ".animation.json");
        }
        @Override public ResourceLocation getModelResource(AnimatedMonsterEntity entity) { return model; }
        @Override public ResourceLocation getTextureResource(AnimatedMonsterEntity entity) { return ResourceLocation.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, "textures/entities/" + entity.texture() + ".png"); }
        @Override public ResourceLocation getAnimationResource(AnimatedMonsterEntity entity) { return animation; }
        @Override public void setCustomAnimations(AnimatedMonsterEntity entity, long instanceId, AnimationState<AnimatedMonsterEntity> state) {
            super.setCustomAnimations(entity, instanceId, state);
            if (entity instanceof MagnetiteEntity || entity instanceof LilJellyEntity) {
                var head = getAnimationProcessor().getBone("head");
                var data = state.getData(DataTickets.ENTITY_MODEL_DATA);
                if (head != null && data != null) { head.setRotX(data.headPitch() * Mth.DEG_TO_RAD); head.setRotY(data.netHeadYaw() * Mth.DEG_TO_RAD); }
            }
        }
    }

    private static final class Renderer extends GeoEntityRenderer<AnimatedMonsterEntity> {
        private Renderer(EntityRendererProvider.Context context, String id, float shadow, String glow) {
            super(context, new Model(id));
            shadowRadius = shadow;
            if (glow != null) addRenderLayer(new Glow(this, glow));
        }
        @Override public RenderType getRenderType(AnimatedMonsterEntity entity, ResourceLocation texture, MultiBufferSource buffers, float partialTick) { return RenderType.entityTranslucent(texture); }
        @Override public void preRender(PoseStack pose, AnimatedMonsterEntity entity, BakedGeoModel model, MultiBufferSource buffers, VertexConsumer buffer, boolean isReRender, float partialTick, int light, int overlay, int color) {
            float scale = entity instanceof SolaEntity sola ? sola.visualScale() : 1;
            scaleWidth = scale; scaleHeight = scale;
            super.preRender(pose, entity, model, buffers, buffer, isReRender, partialTick, light, overlay, color);
        }
    }

    private static final class Glow extends GeoRenderLayer<AnimatedMonsterEntity> {
        private final ResourceLocation texture;
        private Glow(Renderer renderer, String id) { super(renderer); texture = ResourceLocation.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, "textures/entities/" + id + ".png"); }
        @Override public void render(PoseStack pose, AnimatedMonsterEntity entity, BakedGeoModel model, RenderType type, MultiBufferSource buffers, VertexConsumer buffer, float partialTick, int light, int overlay) {
            RenderType glow = RenderType.eyes(texture);
            getRenderer().reRender(model, pose, buffers, entity, glow, buffers.getBuffer(glow), partialTick, light, OverlayTexture.NO_OVERLAY, -1);
        }
    }
}
