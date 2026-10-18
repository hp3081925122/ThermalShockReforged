package com.hp.thermalshockreforged.client.npc.shutterbug;

import com.hp.thermalshockreforged.ThermalShockReforged;
import com.hp.thermalshockreforged.content.npc.shutterbug.*;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.client.event.*;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public final class ShutterbugClient {
    public static void renderers(EntityRenderersEvent.RegisterRenderers event) { event.registerEntityRenderer(ShutterbugContent.SHUTTERBUG.get(), Renderer::new); }
    public static void screens(RegisterMenuScreensEvent event) { for (var page : ShutterbugPage.values()) event.register(ShutterbugContent.menu(page), ShutterbugScreen::new); }
    private static ResourceLocation id(String path) { return ResourceLocation.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, path); }
    private static final class Renderer extends GeoEntityRenderer<ShutterbugEntity> {
        Renderer(EntityRendererProvider.Context context) { super(context, new Model()); shadowRadius = .5F; }
        @Override public RenderType getRenderType(ShutterbugEntity entity, ResourceLocation texture, MultiBufferSource buffers, float partialTick) { return RenderType.entityTranslucent(texture); }
    }
    private static final class Model extends GeoModel<ShutterbugEntity> {
        @Override public ResourceLocation getModelResource(ShutterbugEntity entity) { return id("geo/unette.geo.json"); }
        @Override public ResourceLocation getAnimationResource(ShutterbugEntity entity) { return id("animations/unette.animation.json"); }
        @Override public ResourceLocation getTextureResource(ShutterbugEntity entity) { return id("textures/entities/" + entity.texture() + ".png"); }
    }
}
