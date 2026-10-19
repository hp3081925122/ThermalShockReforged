package com.hp.thermalshockreforged.client.icidroviathan;

import com.hp.thermalshockreforged.entity.icidroviathan.*;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public final class IcidroviathanRenderer extends GeoEntityRenderer<IcidroviathanEntity> {
    public IcidroviathanRenderer(EntityRendererProvider.Context context) { super(context, new Model()); shadowRadius = 2; }
    public static void register(EntityRenderersEvent.RegisterRenderers event) { event.registerEntityRenderer(IcidroviathanContent.ICIDROVIATHAN.get(), IcidroviathanRenderer::new); }
    @Override public RenderType getRenderType(IcidroviathanEntity entity, ResourceLocation texture, MultiBufferSource buffers, float partialTick) { return RenderType.entityTranslucent(texture); }
    private static final class Model extends GeoModel<IcidroviathanEntity> {
        private ResourceLocation id(String path) { return ResourceLocation.fromNamespaceAndPath("thermal_shock_reforged", path); }
        @Override public ResourceLocation getModelResource(IcidroviathanEntity entity) { return id("geo/icidroviathan.geo.json"); }
        @Override public ResourceLocation getAnimationResource(IcidroviathanEntity entity) { return id("animations/icidroviathan.animation.json"); }
        @Override public ResourceLocation getTextureResource(IcidroviathanEntity entity) { return id("textures/entities/" + entity.texture() + ".png"); }
    }
}
