package com.hp.thermalshockreforged.client.icidroviathan;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.rendertype.RenderType;

import com.hp.thermalshockreforged.entity.icidroviathan.*;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import com.hp.thermalshockreforged.client.compat.LegacyGeoModel;
import com.hp.thermalshockreforged.client.compat.LegacyGeoEntityRenderer;

public final class IcidroviathanRenderer extends LegacyGeoEntityRenderer<IcidroviathanEntity> {
    public IcidroviathanRenderer(EntityRendererProvider.Context context) { super(context, new Model()); shadowRadius = 2; }
    public static void register(EntityRenderersEvent.RegisterRenderers event) { event.registerEntityRenderer(IcidroviathanContent.ICIDROVIATHAN.get(), IcidroviathanRenderer::new); }
    @Override public RenderType getRenderType(IcidroviathanEntity entity, Identifier texture, MultiBufferSource buffers, float partialTick) { return RenderTypes.entityTranslucent(texture); }
    private static final class Model extends LegacyGeoModel<IcidroviathanEntity> {
        private Identifier id(String path) { return Identifier.fromNamespaceAndPath("thermal_shock_reforged", path); }
        @Override public Identifier getModelResource(IcidroviathanEntity entity) { return id("geo/icidroviathan.geo.json"); }
        @Override public Identifier getAnimationResource(IcidroviathanEntity entity) { return id("animations/icidroviathan.animation.json"); }
        @Override public Identifier getTextureResource(IcidroviathanEntity entity) { return id("textures/entities/" + entity.texture() + ".png"); }
    }
}
