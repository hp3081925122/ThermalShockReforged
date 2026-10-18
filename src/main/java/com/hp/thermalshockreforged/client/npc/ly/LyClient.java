package com.hp.thermalshockreforged.client.npc.ly;

import com.hp.thermalshockreforged.ThermalShockReforged;
import com.hp.thermalshockreforged.content.npc.ly.*;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.client.event.*;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public final class LyClient {
    public static void renderers(EntityRenderersEvent.RegisterRenderers event) { event.registerEntityRenderer(LyContent.LY.get(), Renderer::new); }
    public static void screens(RegisterMenuScreensEvent event) { for (var page : LyPage.values()) event.register(LyContent.menu(page), LyScreen::new); }
    private static ResourceLocation id(String path) { return ResourceLocation.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, path); }
    private static final class Renderer extends GeoEntityRenderer<LyEntity> {
        Renderer(EntityRendererProvider.Context context) { super(context, new Model()); shadowRadius = .5F; withScale(1); }
        @Override public RenderType getRenderType(LyEntity entity, ResourceLocation texture, MultiBufferSource buffers, float partialTick) { return RenderType.entityTranslucent(texture); }
    }
    private static final class Model extends GeoModel<LyEntity> {
        @Override public ResourceLocation getModelResource(LyEntity entity) { return id("geo/guby.geo.json"); }
        @Override public ResourceLocation getAnimationResource(LyEntity entity) { return id("animations/guby.animation.json"); }
        @Override public ResourceLocation getTextureResource(LyEntity entity) { return id("textures/entities/" + entity.texture() + ".png"); }
    }
}
