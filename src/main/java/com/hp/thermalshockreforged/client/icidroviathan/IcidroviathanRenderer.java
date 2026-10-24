package com.hp.thermalshockreforged.client.icidroviathan;
import com.hp.thermalshockreforged.client.geo.*;
import com.geckolib.renderer.base.GeoRenderState;
import com.geckolib.renderer.base.BoneSnapshots;
import net.minecraft.client.renderer.entity.state.EntityRenderState;

import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.rendertype.RenderType;

import com.hp.thermalshockreforged.entity.icidroviathan.*;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;

public final class IcidroviathanRenderer extends TsrGeoEntityRenderer<IcidroviathanEntity> {
    public IcidroviathanRenderer(EntityRendererProvider.Context context) { super(context, new Model()); shadowRadius = 2; }
    public static void register(EntityRenderersEvent.RegisterRenderers event) { event.registerEntityRenderer(IcidroviathanContent.ICIDROVIATHAN.get(), IcidroviathanRenderer::new); }
    @Override protected RenderType visibleRenderType(EntityRenderState state, Identifier texture) { return RenderTypes.entityTranslucent(texture); }
    private static final class Model extends TsrGeoModel<IcidroviathanEntity> {
        private Identifier id(String path) { return Identifier.fromNamespaceAndPath("thermal_shock_reforged", path); }
        @Override public Identifier getModelResource(IcidroviathanEntity entity) { return id("geo/icidroviathan.geo.json"); }
        @Override public Identifier getAnimationResource(IcidroviathanEntity entity) { return id("animations/icidroviathan.animation.json"); }
        @Override public Identifier getTextureResource(IcidroviathanEntity entity) { return id("textures/entities/" + entity.texture() + ".png"); }
    }
}
