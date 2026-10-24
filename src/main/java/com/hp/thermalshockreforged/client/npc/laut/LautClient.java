package com.hp.thermalshockreforged.client.npc.laut;
import com.hp.thermalshockreforged.client.geo.*;
import com.geckolib.renderer.base.GeoRenderState;
import com.geckolib.renderer.base.BoneSnapshots;
import net.minecraft.client.renderer.entity.state.EntityRenderState;

import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.rendertype.RenderType;

import com.hp.thermalshockreforged.ThermalShockReforged;
import com.hp.thermalshockreforged.content.npc.laut.*;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.client.event.*;

public final class LautClient {
    public static void renderers(EntityRenderersEvent.RegisterRenderers event) { event.registerEntityRenderer(LautContent.LAUT.get(), Renderer::new); }
    public static void screens(RegisterMenuScreensEvent event) { for (var page : LautPage.values()) event.register(LautContent.menu(page), LautScreen::new); }
    private static Identifier id(String path) { return Identifier.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, path); }
    private static final class Renderer extends TsrGeoEntityRenderer<LautEntity> {
        Renderer(EntityRendererProvider.Context context) { super(context, new Model()); shadowRadius = .5F; }
        @Override protected RenderType visibleRenderType(EntityRenderState state, Identifier texture) { return RenderTypes.entityTranslucent(texture); }
    }
    private static final class Model extends TsrGeoModel<LautEntity> {
        @Override public Identifier getModelResource(LautEntity entity) { return id("geo/laut.geo.json"); }
        @Override public Identifier getAnimationResource(LautEntity entity) { return id("animations/laut.animation.json"); }
        @Override public Identifier getTextureResource(LautEntity entity) { return id("textures/entities/" + entity.texture() + ".png"); }
    }
}
