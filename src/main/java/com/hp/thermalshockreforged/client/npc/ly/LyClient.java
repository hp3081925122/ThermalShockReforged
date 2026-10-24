package com.hp.thermalshockreforged.client.npc.ly;
import com.hp.thermalshockreforged.client.geo.*;
import com.geckolib.renderer.base.GeoRenderState;
import com.geckolib.renderer.base.BoneSnapshots;
import net.minecraft.client.renderer.entity.state.EntityRenderState;

import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.rendertype.RenderType;

import com.hp.thermalshockreforged.ThermalShockReforged;
import com.hp.thermalshockreforged.content.npc.ly.*;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.client.event.*;

public final class LyClient {
    public static void renderers(EntityRenderersEvent.RegisterRenderers event) { event.registerEntityRenderer(LyContent.LY.get(), Renderer::new); }
    public static void screens(RegisterMenuScreensEvent event) { for (var page : LyPage.values()) event.register(LyContent.menu(page), LyScreen::new); }
    private static Identifier id(String path) { return Identifier.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, path); }
    private static final class Renderer extends TsrGeoEntityRenderer<LyEntity> {
        Renderer(EntityRendererProvider.Context context) { super(context, new Model()); shadowRadius = .5F; withScale(1); }
        @Override protected RenderType visibleRenderType(EntityRenderState state, Identifier texture) { return RenderTypes.entityTranslucent(texture); }
    }
    private static final class Model extends TsrGeoModel<LyEntity> {
        @Override public Identifier getModelResource(LyEntity entity) { return id("geo/guby.geo.json"); }
        @Override public Identifier getAnimationResource(LyEntity entity) { return id("animations/guby.animation.json"); }
        @Override public Identifier getTextureResource(LyEntity entity) { return id("textures/entities/" + entity.texture() + ".png"); }
    }
}
