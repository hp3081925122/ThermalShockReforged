package com.hp.thermalshockreforged.client.protoi;

import com.hp.thermalshockreforged.ThermalShockReforged;
import com.hp.thermalshockreforged.entity.protoi.*;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import com.hp.thermalshockreforged.entity.magnetite.MagnetiteContent;

public final class ProtoiRenderers {
    private ProtoiRenderers() { }
    public static void register(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(ProtoiContent.GAMMA.get(), context -> new Renderer(context, "protoi_gamma", .9F, .7F));
        event.registerEntityRenderer(ProtoiContent.DELTA.get(), context -> new Renderer(context, "protoi_delta", 1, 1));
        event.registerEntityRenderer(ProtoiContent.EPSILON.get(), context -> new Renderer(context, "protoi_epsilon", 1, 1));
        event.registerEntityRenderer(ProtoiContent.SUNBEAM.get(), context -> new Renderer(context, "protoi_sunbeam", 1, 1));
        event.registerEntityRenderer(ProtoiContent.SUNBEAM_BULLET.get(), ThrownItemRenderer::new);
        event.registerEntityRenderer(MagnetiteContent.MAGNETITE.get(), context -> new Renderer(context, "magnetite", 1, .5F));
        event.registerEntityRenderer(MagnetiteContent.BULLET.get(), ThrownItemRenderer::new);
    }
    private static final class Model extends GeoModel<ProtoiEntityBase> {
        private final String id;
        private Model(String id) { this.id = id; }
        @Override public ResourceLocation getModelResource(ProtoiEntityBase entity) { return ResourceLocation.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, "geo/" + id + ".geo.json"); }
        @Override public ResourceLocation getTextureResource(ProtoiEntityBase entity) { return ResourceLocation.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, "textures/entities/" + entity.texture() + ".png"); }
        @Override public ResourceLocation getAnimationResource(ProtoiEntityBase entity) { return ResourceLocation.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, "animations/" + id + ".animation.json"); }
    }
    private static final class Renderer extends GeoEntityRenderer<ProtoiEntityBase> {
        private Renderer(EntityRendererProvider.Context context, String id, float scale, float shadow) { super(context, new Model(id)); withScale(scale); shadowRadius = shadow; }
    }
}
