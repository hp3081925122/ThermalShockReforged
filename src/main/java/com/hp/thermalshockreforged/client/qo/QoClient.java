package com.hp.thermalshockreforged.client.qo;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.rendertype.RenderType;

import com.hp.thermalshockreforged.ThermalShockReforged;
import com.hp.thermalshockreforged.entity.qo.*;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.client.event.*;
import com.hp.thermalshockreforged.client.compat.LegacyGeoModel;
import com.hp.thermalshockreforged.client.compat.LegacyGeoEntityRenderer;

public final class QoClient {
    public static void renderers(EntityRenderersEvent.RegisterRenderers event) { event.registerEntityRenderer(QoContent.DEFECTOR_MACHINIST.get(), Renderer::new); event.registerEntityRenderer(QoContent.THERMOID.get(), ThermoidRenderer::new); }
    public static void screens(RegisterMenuScreensEvent event) { for (var page : QoPage.values()) event.register(QoContent.menu(page), QoScreen::new); }
    private static Identifier id(String path) { return Identifier.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, path); }
    private static final class Renderer extends LegacyGeoEntityRenderer<DefectorMachinistEntity> {
        Renderer(EntityRendererProvider.Context context) { super(context, new Model()); shadowRadius = .5F; withScale(1); }
        @Override public RenderType getRenderType(DefectorMachinistEntity entity, Identifier texture, MultiBufferSource buffers, float partialTick) { return RenderTypes.entityTranslucent(texture); }
    }
    private static final class ThermoidRenderer extends LegacyGeoEntityRenderer<ThermoidEntity> {
        ThermoidRenderer(EntityRendererProvider.Context context) { super(context, new ThermoidModel()); shadowRadius = .2F; withScale(1); }
        @Override public RenderType getRenderType(ThermoidEntity entity, Identifier texture, MultiBufferSource buffers, float partialTick) { return RenderTypes.entityTranslucent(texture); }
    }
    private static final class ThermoidModel extends LegacyGeoModel<ThermoidEntity> {
        @Override public Identifier getModelResource(ThermoidEntity entity) { return id("geo/thermoid.geo.json"); }
        @Override public Identifier getAnimationResource(ThermoidEntity entity) { return id("animations/thermoid.animation.json"); }
        @Override public Identifier getTextureResource(ThermoidEntity entity) { return id("textures/entities/" + entity.texture() + ".png"); }
    }
    private static final class Model extends LegacyGeoModel<DefectorMachinistEntity> {
        @Override public Identifier getModelResource(DefectorMachinistEntity entity) { return id("geo/qo.geo.json"); }
        @Override public Identifier getAnimationResource(DefectorMachinistEntity entity) { return id("animations/qo.animation.json"); }
        @Override public Identifier getTextureResource(DefectorMachinistEntity entity) { return id("textures/entities/" + entity.texture() + ".png"); }
    }
}
