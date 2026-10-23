package com.hp.thermalshockreforged.client.npc.florist;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.rendertype.RenderType;

import com.hp.thermalshockreforged.ThermalShockReforged;
import com.hp.thermalshockreforged.content.npc.florist.*;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.texture.OverlayTexture;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.geckolib.cache.model.BakedGeoModel;
import com.hp.thermalshockreforged.client.compat.LegacyGeoRenderLayer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.client.event.*;
import com.hp.thermalshockreforged.client.compat.LegacyGeoModel;
import com.hp.thermalshockreforged.client.compat.LegacyGeoEntityRenderer;

public final class FloristClient {
    public static void renderers(EntityRenderersEvent.RegisterRenderers event) { event.registerEntityRenderer(FloristContent.FLORIST.get(), Renderer::new); }
    public static void screens(RegisterMenuScreensEvent event) { for (var page : FloristPage.values()) event.register(FloristContent.menu(page), FloristScreen::new); }
    private static Identifier id(String path) { return Identifier.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, path); }
    private static final class Renderer extends LegacyGeoEntityRenderer<FloristEntity> {
        Renderer(EntityRendererProvider.Context context) { super(context, new Model()); shadowRadius = .5F; addRenderLayer(new Glow(this)); }
        @Override public RenderType getRenderType(FloristEntity entity, Identifier texture, MultiBufferSource buffers, float partialTick) { return RenderTypes.entityTranslucent(texture); }
    }
    private static final class Glow extends LegacyGeoRenderLayer<FloristEntity> {
        private static final Identifier TEXTURE = id("textures/entities/florist_glow.png");
        Glow(Renderer renderer) { super(renderer); }
        @Override public void render(PoseStack pose, FloristEntity entity, BakedGeoModel model, RenderType type, MultiBufferSource buffers, VertexConsumer vertices, float partialTick, int light, int overlay) {
            RenderType glow = RenderTypes.eyes(TEXTURE);
            getRenderer().reRender(model, pose, buffers, entity, glow, buffers.getBuffer(glow), partialTick, light, OverlayTexture.NO_OVERLAY, -1);
        }
    }
    private static final class Model extends LegacyGeoModel<FloristEntity> {
        @Override public Identifier getModelResource(FloristEntity entity) { return id("geo/umabel.geo.json"); }
        @Override public Identifier getAnimationResource(FloristEntity entity) { return id("animations/umabel.animation.json"); }
        @Override public Identifier getTextureResource(FloristEntity entity) { return id("textures/entities/" + entity.texture() + ".png"); }
    }
}
