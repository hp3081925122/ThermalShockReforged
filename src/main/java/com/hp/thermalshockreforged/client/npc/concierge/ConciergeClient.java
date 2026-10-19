package com.hp.thermalshockreforged.client.npc.concierge;

import com.hp.thermalshockreforged.ThermalShockReforged;
import com.hp.thermalshockreforged.content.npc.concierge.*;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.client.event.*;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public final class ConciergeClient {
    public static void renderers(EntityRenderersEvent.RegisterRenderers event) { event.registerEntityRenderer(ConciergeContent.CONCIERGE.get(), Renderer::new); }
    public static void screens(RegisterMenuScreensEvent event) { for (var page : ConciergePage.values()) event.register(ConciergeContent.menu(page), ConciergeScreen::new); }
    private static ResourceLocation id(String path) { return ResourceLocation.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, path); }
    private static final class Renderer extends GeoEntityRenderer<ConciergeEntity> {
        Renderer(EntityRendererProvider.Context context) { super(context, new Model()); shadowRadius = .5F; withScale(1); }
        @Override public RenderType getRenderType(ConciergeEntity entity, ResourceLocation texture, MultiBufferSource buffers, float partialTick) { return RenderType.entityTranslucent(texture); }
    }
    private static final class Model extends GeoModel<ConciergeEntity> {
        @Override public ResourceLocation getModelResource(ConciergeEntity entity) { return id("geo/concierge.geo.json"); }
        @Override public ResourceLocation getAnimationResource(ConciergeEntity entity) { return id("animations/concierge.animation.json"); }
        @Override public ResourceLocation getTextureResource(ConciergeEntity entity) { return id("textures/entities/" + entity.texture() + ".png"); }
    }
}
