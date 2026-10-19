package com.hp.thermalshockreforged.client.jellercel;

import com.hp.thermalshockreforged.ThermalShockReforged;
import com.hp.thermalshockreforged.entity.jellercel.JellercelContent;
import com.hp.thermalshockreforged.entity.jellercel.JellercelEntity;
import com.hp.thermalshockreforged.entity.jellercel.JellercelKind;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.renderer.layer.GeoRenderLayer;

public final class JellercelRenderers {
    private JellercelRenderers() { }
    public static void register(EntityRenderersEvent.RegisterRenderers event) {
        for (JellercelKind kind : JellercelKind.values()) event.registerEntityRenderer(JellercelContent.type(kind), context -> new Renderer(context, kind));
    }
    private static ResourceLocation resource(String path) { return ResourceLocation.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, path); }
    private static final class Model extends GeoModel<JellercelEntity> {
        private final ResourceLocation model;
        private final ResourceLocation animation;
        private Model(JellercelKind kind) { model = resource("geo/" + kind.id + ".geo.json"); animation = resource("animations/" + kind.id + ".animation.json"); }
        @Override public ResourceLocation getModelResource(JellercelEntity entity) { return model; }
        @Override public ResourceLocation getTextureResource(JellercelEntity entity) { return resource("textures/entities/" + entity.texture() + ".png"); }
        @Override public ResourceLocation getAnimationResource(JellercelEntity entity) { return animation; }
    }
    private static final class Renderer extends GeoEntityRenderer<JellercelEntity> {
        private Renderer(EntityRendererProvider.Context context, JellercelKind kind) {
            super(context, new Model(kind));
            shadowRadius = kind.shadow;
            addRenderLayer(new Glow(this, kind));
        }
        @Override public RenderType getRenderType(JellercelEntity entity, ResourceLocation texture, MultiBufferSource buffers, float partialTick) { return RenderType.entityTranslucent(texture); }
    }
    private static final class Glow extends GeoRenderLayer<JellercelEntity> {
        private final ResourceLocation texture;
        private Glow(Renderer renderer, JellercelKind kind) { super(renderer); texture = resource("textures/entities/" + kind.glow + ".png"); }
        @Override public void render(PoseStack pose, JellercelEntity entity, BakedGeoModel model, RenderType type, MultiBufferSource buffers, VertexConsumer buffer, float partialTick, int light, int overlay) {
            RenderType glow = RenderType.eyes(texture);
            getRenderer().reRender(model, pose, buffers, entity, glow, buffers.getBuffer(glow), partialTick, light, OverlayTexture.NO_OVERLAY, -1);
        }
    }
}
