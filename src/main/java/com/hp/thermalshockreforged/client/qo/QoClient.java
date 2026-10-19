package com.hp.thermalshockreforged.client.qo;

import com.hp.thermalshockreforged.ThermalShockReforged;
import com.hp.thermalshockreforged.entity.qo.*;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.client.event.*;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public final class QoClient {
    public static void renderers(EntityRenderersEvent.RegisterRenderers event) { event.registerEntityRenderer(QoContent.DEFECTOR_MACHINIST.get(), Renderer::new); event.registerEntityRenderer(QoContent.THERMOID.get(), ThermoidRenderer::new); }
    public static void screens(RegisterMenuScreensEvent event) { for (var page : QoPage.values()) event.register(QoContent.menu(page), QoScreen::new); }
    private static ResourceLocation id(String path) { return ResourceLocation.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, path); }
    private static final class Renderer extends GeoEntityRenderer<DefectorMachinistEntity> {
        Renderer(EntityRendererProvider.Context context) { super(context, new Model()); shadowRadius = .5F; withScale(1); }
        @Override public RenderType getRenderType(DefectorMachinistEntity entity, ResourceLocation texture, MultiBufferSource buffers, float partialTick) { return RenderType.entityTranslucent(texture); }
    }
    private static final class ThermoidRenderer extends GeoEntityRenderer<ThermoidEntity> {
        ThermoidRenderer(EntityRendererProvider.Context context) { super(context, new ThermoidModel()); shadowRadius = .2F; withScale(1); }
        @Override public RenderType getRenderType(ThermoidEntity entity, ResourceLocation texture, MultiBufferSource buffers, float partialTick) { return RenderType.entityTranslucent(texture); }
    }
    private static final class ThermoidModel extends GeoModel<ThermoidEntity> {
        @Override public ResourceLocation getModelResource(ThermoidEntity entity) { return id("geo/thermoid.geo.json"); }
        @Override public ResourceLocation getAnimationResource(ThermoidEntity entity) { return id("animations/thermoid.animation.json"); }
        @Override public ResourceLocation getTextureResource(ThermoidEntity entity) { return id("textures/entities/" + entity.texture() + ".png"); }
    }
    private static final class Model extends GeoModel<DefectorMachinistEntity> {
        @Override public ResourceLocation getModelResource(DefectorMachinistEntity entity) { return id("geo/qo.geo.json"); }
        @Override public ResourceLocation getAnimationResource(DefectorMachinistEntity entity) { return id("animations/qo.animation.json"); }
        @Override public ResourceLocation getTextureResource(DefectorMachinistEntity entity) { return id("textures/entities/" + entity.texture() + ".png"); }
    }
}
