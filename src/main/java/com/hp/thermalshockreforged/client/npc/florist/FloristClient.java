package com.hp.thermalshockreforged.client.npc.florist;

import com.hp.thermalshockreforged.ThermalShockReforged;
import com.hp.thermalshockreforged.content.npc.florist.*;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.texture.OverlayTexture;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.layer.GeoRenderLayer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.client.event.*;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public final class FloristClient {
    public static void renderers(EntityRenderersEvent.RegisterRenderers event) { event.registerEntityRenderer(FloristContent.FLORIST.get(), Renderer::new); }
    public static void screens(RegisterMenuScreensEvent event) { for (var page : FloristPage.values()) event.register(FloristContent.menu(page), FloristScreen::new); }
    private static ResourceLocation id(String path) { return ResourceLocation.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, path); }
    private static final class Renderer extends GeoEntityRenderer<FloristEntity> {
        Renderer(EntityRendererProvider.Context context) { super(context, new Model()); shadowRadius = .5F; addRenderLayer(new Glow(this)); }
        @Override public RenderType getRenderType(FloristEntity entity, ResourceLocation texture, MultiBufferSource buffers, float partialTick) { return RenderType.entityTranslucent(texture); }
    }
    private static final class Glow extends GeoRenderLayer<FloristEntity> {
        private static final ResourceLocation TEXTURE = id("textures/entities/florist_glow.png");
        Glow(Renderer renderer) { super(renderer); }
        @Override public void render(PoseStack pose, FloristEntity entity, BakedGeoModel model, RenderType type, MultiBufferSource buffers, VertexConsumer vertices, float partialTick, int light, int overlay) {
            RenderType glow = RenderType.eyes(TEXTURE);
            getRenderer().reRender(model, pose, buffers, entity, glow, buffers.getBuffer(glow), partialTick, light, OverlayTexture.NO_OVERLAY, -1);
        }
    }
    private static final class Model extends GeoModel<FloristEntity> {
        @Override public ResourceLocation getModelResource(FloristEntity entity) { return id("geo/umabel.geo.json"); }
        @Override public ResourceLocation getAnimationResource(FloristEntity entity) { return id("animations/umabel.animation.json"); }
        @Override public ResourceLocation getTextureResource(FloristEntity entity) { return id("textures/entities/" + entity.texture() + ".png"); }
    }
}
