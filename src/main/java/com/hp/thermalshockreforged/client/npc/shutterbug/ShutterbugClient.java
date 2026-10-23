package com.hp.thermalshockreforged.client.npc.shutterbug;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.rendertype.RenderType;

import com.hp.thermalshockreforged.ThermalShockReforged;
import com.hp.thermalshockreforged.content.npc.shutterbug.*;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.client.event.*;
import com.hp.thermalshockreforged.client.compat.LegacyGeoModel;
import com.hp.thermalshockreforged.client.compat.LegacyGeoEntityRenderer;

public final class ShutterbugClient {
    public static void renderers(EntityRenderersEvent.RegisterRenderers event) { event.registerEntityRenderer(ShutterbugContent.SHUTTERBUG.get(), Renderer::new); }
    public static void screens(RegisterMenuScreensEvent event) { for (var page : ShutterbugPage.values()) event.register(ShutterbugContent.menu(page), ShutterbugScreen::new); }
    private static Identifier id(String path) { return Identifier.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, path); }
    private static final class Renderer extends LegacyGeoEntityRenderer<ShutterbugEntity> {
        Renderer(EntityRendererProvider.Context context) { super(context, new Model()); shadowRadius = .5F; }
        @Override public RenderType getRenderType(ShutterbugEntity entity, Identifier texture, MultiBufferSource buffers, float partialTick) { return RenderTypes.entityTranslucent(texture); }
    }
    private static final class Model extends LegacyGeoModel<ShutterbugEntity> {
        @Override public Identifier getModelResource(ShutterbugEntity entity) { return id("geo/unette.geo.json"); }
        @Override public Identifier getAnimationResource(ShutterbugEntity entity) { return id("animations/unette.animation.json"); }
        @Override public Identifier getTextureResource(ShutterbugEntity entity) { return id("textures/entities/" + entity.texture() + ".png"); }
    }
}
