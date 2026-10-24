package com.hp.thermalshockreforged.client.npc.shutterbug;
import com.hp.thermalshockreforged.client.geo.*;
import com.geckolib.renderer.base.GeoRenderState;
import com.geckolib.renderer.base.BoneSnapshots;
import net.minecraft.client.renderer.entity.state.EntityRenderState;

import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.rendertype.RenderType;

import com.hp.thermalshockreforged.ThermalShockReforged;
import com.hp.thermalshockreforged.content.npc.shutterbug.*;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.client.event.*;

public final class ShutterbugClient {
    public static void renderers(EntityRenderersEvent.RegisterRenderers event) { event.registerEntityRenderer(ShutterbugContent.SHUTTERBUG.get(), Renderer::new); }
    public static void screens(RegisterMenuScreensEvent event) { for (var page : ShutterbugPage.values()) event.register(ShutterbugContent.menu(page), ShutterbugScreen::new); }
    private static Identifier id(String path) { return Identifier.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, path); }
    private static final class Renderer extends TsrGeoEntityRenderer<ShutterbugEntity> {
        Renderer(EntityRendererProvider.Context context) { super(context, new Model()); shadowRadius = .5F; }
        @Override protected RenderType visibleRenderType(EntityRenderState state, Identifier texture) { return RenderTypes.entityTranslucent(texture); }
    }
    private static final class Model extends TsrGeoModel<ShutterbugEntity> {
        @Override public Identifier getModelResource(ShutterbugEntity entity) { return id("geo/unette.geo.json"); }
        @Override public Identifier getAnimationResource(ShutterbugEntity entity) { return id("animations/unette.animation.json"); }
        @Override public Identifier getTextureResource(ShutterbugEntity entity) { return id("textures/entities/" + entity.texture() + ".png"); }
    }
}
