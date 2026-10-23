package com.hp.thermalshockreforged.client.kofoni;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.rendertype.RenderType;

import com.hp.thermalshockreforged.ThermalShockReforged;
import com.hp.thermalshockreforged.entity.kofoni.*;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.client.event.*;
import com.hp.thermalshockreforged.client.compat.LegacyGeoModel;
import com.hp.thermalshockreforged.client.compat.LegacyGeoEntityRenderer;

public final class KofoniClient {
    public static void renderers(EntityRenderersEvent.RegisterRenderers event) { event.registerEntityRenderer(KofoniContent.KOFONI.get(), Renderer::new); }
    public static void screens(RegisterMenuScreensEvent event) { for (var page : KofoniPage.values()) event.register(KofoniContent.menu(page), KofoniScreen::new); }
    private static Identifier id(String path) { return Identifier.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, path); }
    private static final class Renderer extends LegacyGeoEntityRenderer<KofoniEntity> {
        Renderer(EntityRendererProvider.Context context) { super(context, new Model()); shadowRadius = .5F; withScale(1); }
        @Override public RenderType getRenderType(KofoniEntity entity, Identifier texture, MultiBufferSource buffers, float partialTick) { return RenderTypes.entityTranslucent(texture); }
    }
    private static final class Model extends LegacyGeoModel<KofoniEntity> {
        @Override public Identifier getModelResource(KofoniEntity entity) { return id("geo/kofoni.geo.json"); }
        @Override public Identifier getAnimationResource(KofoniEntity entity) { return id("animations/kofoni.animation.json"); }
        @Override public Identifier getTextureResource(KofoniEntity entity) { return id("textures/entities/" + entity.texture() + ".png"); }
    }
}
