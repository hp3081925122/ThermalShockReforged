package com.hp.thermalshockreforged.client.kofoni;
import com.hp.thermalshockreforged.client.geo.*;
import com.geckolib.renderer.base.GeoRenderState;
import com.geckolib.renderer.base.BoneSnapshots;
import net.minecraft.client.renderer.entity.state.EntityRenderState;

import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.rendertype.RenderType;

import com.hp.thermalshockreforged.ThermalShockReforged;
import com.hp.thermalshockreforged.entity.kofoni.*;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.client.event.*;

public final class KofoniClient {
    public static void renderers(EntityRenderersEvent.RegisterRenderers event) { event.registerEntityRenderer(KofoniContent.KOFONI.get(), Renderer::new); }
    public static void screens(RegisterMenuScreensEvent event) { for (var page : KofoniPage.values()) event.register(KofoniContent.menu(page), KofoniScreen::new); }
    private static Identifier id(String path) { return Identifier.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, path); }
    private static final class Renderer extends TsrGeoEntityRenderer<KofoniEntity> {
        Renderer(EntityRendererProvider.Context context) { super(context, new Model()); shadowRadius = .5F; withScale(1); }
        @Override protected RenderType visibleRenderType(EntityRenderState state, Identifier texture) { return RenderTypes.entityTranslucent(texture); }
    }
    private static final class Model extends TsrGeoModel<KofoniEntity> {
        @Override public Identifier getModelResource(KofoniEntity entity) { return id("geo/kofoni.geo.json"); }
        @Override public Identifier getAnimationResource(KofoniEntity entity) { return id("animations/kofoni.animation.json"); }
        @Override public Identifier getTextureResource(KofoniEntity entity) { return id("textures/entities/" + entity.texture() + ".png"); }
    }
}
