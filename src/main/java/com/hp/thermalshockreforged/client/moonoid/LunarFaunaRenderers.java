package com.hp.thermalshockreforged.client.moonoid;

import com.hp.thermalshockreforged.entity.base.AnimatedMonsterEntity;
import com.hp.thermalshockreforged.ThermalShockReforged;
import com.hp.thermalshockreforged.entity.moonoid.LunarFaunaContent;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public final class LunarFaunaRenderers {
    public static void register(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(LunarFaunaContent.MOONOID.get(), Renderer::new);
        event.registerEntityRenderer(LunarFaunaContent.STARRITE.get(), Renderer::new);
    }
    private static final class Renderer extends GeoEntityRenderer<AnimatedMonsterEntity> {
        Renderer(EntityRendererProvider.Context context) { super(context, new Model()); shadowRadius = .2F; }
        @Override public RenderType getRenderType(AnimatedMonsterEntity entity, ResourceLocation texture, MultiBufferSource buffers, float partialTick) { return RenderType.entityTranslucent(texture); }
    }
    private static final class Model extends GeoModel<AnimatedMonsterEntity> {
        private ResourceLocation id(String path) { return ResourceLocation.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, path); }
        @Override public ResourceLocation getModelResource(AnimatedMonsterEntity entity) { return id("geo/moonoid.geo.json"); }
        @Override public ResourceLocation getAnimationResource(AnimatedMonsterEntity entity) { return id("animations/moonoid.animation.json"); }
        @Override public ResourceLocation getTextureResource(AnimatedMonsterEntity entity) { return id("textures/entities/" + entity.texture() + ".png"); }
    }
    private LunarFaunaRenderers() { }
}
