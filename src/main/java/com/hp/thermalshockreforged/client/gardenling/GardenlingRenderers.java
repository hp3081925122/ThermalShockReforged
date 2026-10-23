package com.hp.thermalshockreforged.client.gardenling;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.rendertype.RenderType;
import com.hp.thermalshockreforged.client.compat.LegacyGeoEntityRenderer;

import com.hp.thermalshockreforged.entity.gardenling.*;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import com.geckolib.cache.model.BakedGeoModel;
import com.hp.thermalshockreforged.client.compat.LegacyGeoModel;
import com.geckolib.renderer.*;
import com.hp.thermalshockreforged.client.compat.LegacyGeoRenderLayer;

public final class GardenlingRenderers {
    private GardenlingRenderers() { }
    public static void register(EntityRenderersEvent.RegisterRenderers event) { event.registerEntityRenderer(GardenlingContent.GARDENLING.get(), Renderer::new); }
    private static Identifier resource(String path) { return Identifier.fromNamespaceAndPath("thermal_shock_reforged", path); }

    private static final class Model extends LegacyGeoModel<GardenlingEntity> {
        @Override public Identifier getModelResource(GardenlingEntity entity) { return resource("geo/gardenling.geo.json"); }
        @Override public Identifier getTextureResource(GardenlingEntity entity) { return resource("textures/entities/" + entity.texture() + ".png"); }
        @Override public Identifier getAnimationResource(GardenlingEntity entity) { return resource("animations/gardenling.animation.json"); }
    }

    private static final class Renderer extends LegacyGeoEntityRenderer<GardenlingEntity> {
        private Renderer(EntityRendererProvider.Context context) { super(context, new Model()); shadowRadius = 0.5F; addRenderLayer(new Glow(this)); }
        @Override
        public RenderType getRenderType(GardenlingEntity entity, Identifier texture, MultiBufferSource buffers, float partialTick) { return RenderTypes.entityTranslucent(texture); }
    }

    private static final class Glow extends LegacyGeoRenderLayer<GardenlingEntity> {
        private static final Identifier TEXTURE = resource("textures/entities/gardenling_glow.png");
        private Glow(LegacyGeoEntityRenderer<GardenlingEntity> renderer) { super(renderer); }
        @Override
        public void render(PoseStack pose, GardenlingEntity entity, BakedGeoModel model, RenderType type, MultiBufferSource buffers,
                VertexConsumer vertices, float partialTick, int light, int overlay) {
            RenderType glow = RenderTypes.eyes(TEXTURE);
            getRenderer().reRender(model, pose, buffers, entity, glow, buffers.getBuffer(glow), partialTick, light, OverlayTexture.NO_OVERLAY, -1);
        }
    }
}
