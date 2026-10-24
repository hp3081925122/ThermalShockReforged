package com.hp.thermalshockreforged.client.npc.concierge;
import com.hp.thermalshockreforged.client.geo.*;
import com.geckolib.renderer.base.GeoRenderState;
import com.geckolib.renderer.base.BoneSnapshots;
import net.minecraft.client.renderer.entity.state.EntityRenderState;

import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.rendertype.RenderType;

import com.hp.thermalshockreforged.ThermalShockReforged;
import com.hp.thermalshockreforged.content.npc.concierge.*;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.client.event.*;

public final class ConciergeClient {
    public static void renderers(EntityRenderersEvent.RegisterRenderers event) { event.registerEntityRenderer(ConciergeContent.CONCIERGE.get(), Renderer::new); }
    public static void screens(RegisterMenuScreensEvent event) { for (var page : ConciergePage.values()) event.register(ConciergeContent.menu(page), ConciergeScreen::new); }
    private static Identifier id(String path) { return Identifier.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, path); }
    private static final class Renderer extends TsrGeoEntityRenderer<ConciergeEntity> {
        Renderer(EntityRendererProvider.Context context) { super(context, new Model()); shadowRadius = .5F; withScale(1); }
        @Override protected RenderType visibleRenderType(EntityRenderState state, Identifier texture) { return RenderTypes.entityTranslucent(texture); }
    }
    private static final class Model extends TsrGeoModel<ConciergeEntity> {
        @Override public Identifier getModelResource(ConciergeEntity entity) { return id("geo/concierge.geo.json"); }
        @Override public Identifier getAnimationResource(ConciergeEntity entity) { return id("animations/concierge.animation.json"); }
        @Override public Identifier getTextureResource(ConciergeEntity entity) { return id("textures/entities/" + entity.texture() + ".png"); }
    }
}
