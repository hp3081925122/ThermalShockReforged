package com.hp.thermalshockreforged.client.npc.ly;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.rendertype.RenderType;

import com.hp.thermalshockreforged.ThermalShockReforged;
import com.hp.thermalshockreforged.content.npc.ly.*;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.client.event.*;
import com.hp.thermalshockreforged.client.compat.LegacyGeoModel;
import com.hp.thermalshockreforged.client.compat.LegacyGeoEntityRenderer;

public final class LyClient {
    public static void renderers(EntityRenderersEvent.RegisterRenderers event) { event.registerEntityRenderer(LyContent.LY.get(), Renderer::new); }
    public static void screens(RegisterMenuScreensEvent event) { for (var page : LyPage.values()) event.register(LyContent.menu(page), LyScreen::new); }
    private static Identifier id(String path) { return Identifier.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, path); }
    private static final class Renderer extends LegacyGeoEntityRenderer<LyEntity> {
        Renderer(EntityRendererProvider.Context context) { super(context, new Model()); shadowRadius = .5F; withScale(1); }
        @Override public RenderType getRenderType(LyEntity entity, Identifier texture, MultiBufferSource buffers, float partialTick) { return RenderTypes.entityTranslucent(texture); }
    }
    private static final class Model extends LegacyGeoModel<LyEntity> {
        @Override public Identifier getModelResource(LyEntity entity) { return id("geo/guby.geo.json"); }
        @Override public Identifier getAnimationResource(LyEntity entity) { return id("animations/guby.animation.json"); }
        @Override public Identifier getTextureResource(LyEntity entity) { return id("textures/entities/" + entity.texture() + ".png"); }
    }
}
