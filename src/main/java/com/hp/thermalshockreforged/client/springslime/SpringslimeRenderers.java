package com.hp.thermalshockreforged.client.springslime;

import com.hp.thermalshockreforged.ThermalShockReforged;
import com.hp.thermalshockreforged.entity.springslime.*;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.entity.*;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public final class SpringslimeRenderers {
    public static void register(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(SpringslimeContent.SPRING.get(), Renderer::new);
        event.registerEntityRenderer(SpringslimeContent.PROJECTILE.get(), ThrownItemRenderer::new);
        event.registerEntityRenderer(SpringslimeContent.FADE.get(), NoopRenderer::new);
    }
    private static ResourceLocation id(String path) { return ResourceLocation.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, path); }
    private static final class Renderer extends GeoEntityRenderer<SpringslimeEntity> {
        Renderer(EntityRendererProvider.Context context) { super(context, new Model()); shadowRadius = 1.2F; }
        @Override public RenderType getRenderType(SpringslimeEntity entity, ResourceLocation texture, MultiBufferSource buffers, float partialTick) { return RenderType.entityTranslucent(texture); }
        @Override protected float getDeathMaxRotation(SpringslimeEntity entity) { return 0; }
    }
    private static final class Model extends GeoModel<SpringslimeEntity> {
        @Override public ResourceLocation getModelResource(SpringslimeEntity entity) { return id("geo/violetsplat.geo.json"); }
        @Override public ResourceLocation getAnimationResource(SpringslimeEntity entity) { return id("animations/violetsplat.animation.json"); }
        @Override public ResourceLocation getTextureResource(SpringslimeEntity entity) { return id("textures/entities/" + entity.texture() + ".png"); }
    }
}
