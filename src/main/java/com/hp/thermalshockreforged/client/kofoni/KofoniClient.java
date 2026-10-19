package com.hp.thermalshockreforged.client.kofoni;

import com.hp.thermalshockreforged.ThermalShockReforged;
import com.hp.thermalshockreforged.entity.kofoni.*;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.client.event.*;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public final class KofoniClient {
    public static void renderers(EntityRenderersEvent.RegisterRenderers event) { event.registerEntityRenderer(KofoniContent.KOFONI.get(), Renderer::new); }
    public static void screens(RegisterMenuScreensEvent event) { for (var page : KofoniPage.values()) event.register(KofoniContent.menu(page), KofoniScreen::new); }
    private static ResourceLocation id(String path) { return ResourceLocation.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, path); }
    private static final class Renderer extends GeoEntityRenderer<KofoniEntity> {
        Renderer(EntityRendererProvider.Context context) { super(context, new Model()); shadowRadius = .5F; withScale(1); }
        @Override public RenderType getRenderType(KofoniEntity entity, ResourceLocation texture, MultiBufferSource buffers, float partialTick) { return RenderType.entityTranslucent(texture); }
    }
    private static final class Model extends GeoModel<KofoniEntity> {
        @Override public ResourceLocation getModelResource(KofoniEntity entity) { return id("geo/kofoni.geo.json"); }
        @Override public ResourceLocation getAnimationResource(KofoniEntity entity) { return id("animations/kofoni.animation.json"); }
        @Override public ResourceLocation getTextureResource(KofoniEntity entity) { return id("textures/entities/" + entity.texture() + ".png"); }
    }
}
