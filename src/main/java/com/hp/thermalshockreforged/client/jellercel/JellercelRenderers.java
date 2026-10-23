package com.hp.thermalshockreforged.client.jellercel;
import net.minecraft.client.renderer.rendertype.RenderTypes;

import com.hp.thermalshockreforged.ThermalShockReforged;
import com.hp.thermalshockreforged.entity.jellercel.JellercelContent;
import com.hp.thermalshockreforged.entity.jellercel.JellercelEntity;
import com.hp.thermalshockreforged.entity.jellercel.JellercelKind;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import com.geckolib.cache.model.BakedGeoModel;
import com.hp.thermalshockreforged.client.compat.LegacyGeoModel;
import com.hp.thermalshockreforged.client.compat.LegacyGeoEntityRenderer;
import com.hp.thermalshockreforged.client.compat.LegacyGeoRenderLayer;

public final class JellercelRenderers {
    private JellercelRenderers() { }
    public static void register(EntityRenderersEvent.RegisterRenderers event) {
        for (JellercelKind kind : JellercelKind.values()) event.registerEntityRenderer(JellercelContent.type(kind), context -> new Renderer(context, kind));
    }
    private static Identifier resource(String path) { return Identifier.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, path); }
    private static final class Model extends LegacyGeoModel<JellercelEntity> {
        private final Identifier model;
        private final Identifier animation;
        private Model(JellercelKind kind) { model = resource("geo/" + kind.id + ".geo.json"); animation = resource("animations/" + kind.id + ".animation.json"); }
        @Override public Identifier getModelResource(JellercelEntity entity) { return model; }
        @Override public Identifier getTextureResource(JellercelEntity entity) { return resource("textures/entities/" + entity.texture() + ".png"); }
        @Override public Identifier getAnimationResource(JellercelEntity entity) { return animation; }
    }
    private static final class Renderer extends LegacyGeoEntityRenderer<JellercelEntity> {
        private Renderer(EntityRendererProvider.Context context, JellercelKind kind) {
            super(context, new Model(kind));
            shadowRadius = kind.shadow;
            addRenderLayer(new Glow(this, kind));
        }
        @Override public RenderType getRenderType(JellercelEntity entity, Identifier texture, MultiBufferSource buffers, float partialTick) { return RenderTypes.entityTranslucent(texture); }
    }
    private static final class Glow extends LegacyGeoRenderLayer<JellercelEntity> {
        private final Identifier texture;
        private Glow(Renderer renderer, JellercelKind kind) { super(renderer); texture = resource("textures/entities/" + kind.glow + ".png"); }
        @Override public void render(PoseStack pose, JellercelEntity entity, BakedGeoModel model, RenderType type, MultiBufferSource buffers, VertexConsumer buffer, float partialTick, int light, int overlay) {
            RenderType glow = RenderTypes.eyes(texture);
            getRenderer().reRender(model, pose, buffers, entity, glow, buffers.getBuffer(glow), partialTick, light, OverlayTexture.NO_OVERLAY, -1);
        }
    }
}
