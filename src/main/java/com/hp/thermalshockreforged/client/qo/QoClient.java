package com.hp.thermalshockreforged.client.qo;
import com.hp.thermalshockreforged.client.geo.*;
import com.geckolib.renderer.base.GeoRenderState;
import com.geckolib.renderer.base.BoneSnapshots;
import net.minecraft.client.renderer.entity.state.EntityRenderState;

import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.rendertype.RenderType;

import com.hp.thermalshockreforged.ThermalShockReforged;
import com.hp.thermalshockreforged.entity.qo.*;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.client.event.*;

public final class QoClient {
    public static void renderers(EntityRenderersEvent.RegisterRenderers event) { event.registerEntityRenderer(QoContent.DEFECTOR_MACHINIST.get(), Renderer::new); event.registerEntityRenderer(QoContent.THERMOID.get(), ThermoidRenderer::new); }
    public static void screens(RegisterMenuScreensEvent event) { for (var page : QoPage.values()) event.register(QoContent.menu(page), QoScreen::new); }
    private static Identifier id(String path) { return Identifier.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, path); }
    private static final class Renderer extends TsrGeoEntityRenderer<DefectorMachinistEntity> {
        Renderer(EntityRendererProvider.Context context) { super(context, new Model()); shadowRadius = .5F; withScale(1); }
        @Override protected RenderType visibleRenderType(EntityRenderState state, Identifier texture) { return RenderTypes.entityTranslucent(texture); }
    }
    private static final class ThermoidRenderer extends TsrGeoEntityRenderer<ThermoidEntity> {
        ThermoidRenderer(EntityRendererProvider.Context context) { super(context, new ThermoidModel()); shadowRadius = .2F; withScale(1); }
        @Override protected RenderType visibleRenderType(EntityRenderState state, Identifier texture) { return RenderTypes.entityTranslucent(texture); }
    }
    private static final class ThermoidModel extends TsrGeoModel<ThermoidEntity> {
        @Override public Identifier getModelResource(ThermoidEntity entity) { return id("geo/thermoid.geo.json"); }
        @Override public Identifier getAnimationResource(ThermoidEntity entity) { return id("animations/thermoid.animation.json"); }
        @Override public Identifier getTextureResource(ThermoidEntity entity) { return id("textures/entities/" + entity.texture() + ".png"); }
    }
    private static final class Model extends TsrGeoModel<DefectorMachinistEntity> {
        @Override public Identifier getModelResource(DefectorMachinistEntity entity) { return id("geo/qo.geo.json"); }
        @Override public Identifier getAnimationResource(DefectorMachinistEntity entity) { return id("animations/qo.animation.json"); }
        @Override public Identifier getTextureResource(DefectorMachinistEntity entity) { return id("textures/entities/" + entity.texture() + ".png"); }
    }
}
