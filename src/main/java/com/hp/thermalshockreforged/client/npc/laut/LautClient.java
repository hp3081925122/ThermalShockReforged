package com.hp.thermalshockreforged.client.npc.laut;

import com.hp.thermalshockreforged.ThermalShockReforged;
import com.hp.thermalshockreforged.content.npc.laut.*;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.client.event.*;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public final class LautClient {
    public static void renderers(EntityRenderersEvent.RegisterRenderers event) { event.registerEntityRenderer(LautContent.LAUT.get(), Renderer::new); }
    public static void screens(RegisterMenuScreensEvent event) { for (var page : LautPage.values()) event.register(LautContent.menu(page), LautScreen::new); }
    private static ResourceLocation id(String path) { return ResourceLocation.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, path); }
    private static final class Renderer extends GeoEntityRenderer<LautEntity> {
        Renderer(EntityRendererProvider.Context context) { super(context, new Model()); shadowRadius = .5F; }
        @Override public RenderType getRenderType(LautEntity entity, ResourceLocation texture, MultiBufferSource buffers, float partialTick) { return RenderType.entityTranslucent(texture); }
    }
    private static final class Model extends GeoModel<LautEntity> {
        @Override public ResourceLocation getModelResource(LautEntity entity) { return id("geo/laut.geo.json"); }
        @Override public ResourceLocation getAnimationResource(LautEntity entity) { return id("animations/laut.animation.json"); }
        @Override public ResourceLocation getTextureResource(LautEntity entity) { return id("textures/entities/" + entity.texture() + ".png"); }
    }
}
