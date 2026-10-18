package com.hp.thermalshock.client.gardenling;

import com.hp.thermalshock.entity.gardenling.*;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.*;
import software.bernie.geckolib.renderer.layer.GeoRenderLayer;

public final class GardenlingRenderers {
    private GardenlingRenderers() { }
    public static void register(EntityRenderersEvent.RegisterRenderers event) { event.registerEntityRenderer(GardenlingContent.GARDENLING.get(), Renderer::new); }
    private static ResourceLocation resource(String path) { return ResourceLocation.fromNamespaceAndPath("thermal_shock", path); }

    private static final class Model extends GeoModel<GardenlingEntity> {
        @Override public ResourceLocation getModelResource(GardenlingEntity entity) { return resource("geo/gardenling.geo.json"); }
        @Override public ResourceLocation getTextureResource(GardenlingEntity entity) { return resource("textures/entities/" + entity.texture() + ".png"); }
        @Override public ResourceLocation getAnimationResource(GardenlingEntity entity) { return resource("animations/gardenling.animation.json"); }
    }

    private static final class Renderer extends GeoEntityRenderer<GardenlingEntity> {
        private Renderer(EntityRendererProvider.Context context) { super(context, new Model()); shadowRadius = 0.5F; addRenderLayer(new Glow(this)); }
        @Override
        public RenderType getRenderType(GardenlingEntity entity, ResourceLocation texture, MultiBufferSource buffers, float partialTick) { return RenderType.entityTranslucent(texture); }
    }

    private static final class Glow extends GeoRenderLayer<GardenlingEntity> {
        private static final ResourceLocation TEXTURE = resource("textures/entities/gardenling_glow.png");
        private Glow(GeoRenderer<GardenlingEntity> renderer) { super(renderer); }
        @Override
        public void render(PoseStack pose, GardenlingEntity entity, BakedGeoModel model, RenderType type, MultiBufferSource buffers,
                VertexConsumer vertices, float partialTick, int light, int overlay) {
            RenderType glow = RenderType.eyes(TEXTURE);
            getRenderer().reRender(model, pose, buffers, entity, glow, buffers.getBuffer(glow), partialTick, light, OverlayTexture.NO_OVERLAY, -1);
        }
    }
}
