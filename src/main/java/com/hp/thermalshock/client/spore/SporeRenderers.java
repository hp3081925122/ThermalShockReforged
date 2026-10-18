package com.hp.thermalshock.client.spore;

import com.hp.thermalshock.entity.spore.*;
import net.minecraft.client.renderer.*;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.extensions.common.*;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public final class SporeRenderers {
    private SporeRenderers() { }
    public static void register(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(SporeContent.BLIGHTED_SPORE.get(), context -> new Renderer(context));
        event.registerBlockEntityRenderer(SporeContent.SPORE_POD_ENTITY.get(), context -> new SporePodRenderer());
    }
    public static void extensions(RegisterClientExtensionsEvent event) {
        event.registerItem(new IClientItemExtensions() {
            private SporePodItemRenderer renderer;
            @Override
            public net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer getCustomRenderer() { if (renderer == null) renderer = new SporePodItemRenderer(); return renderer; }
        }, SporeContent.SPORE_POD_ITEM.get());
    }
    private static ResourceLocation resource(String path) { return ResourceLocation.fromNamespaceAndPath("thermal_shock", path); }
    private static final class Renderer extends GeoEntityRenderer<BlightedSporeEntity> {
        private Renderer(net.minecraft.client.renderer.entity.EntityRendererProvider.Context context) { super(context, new Model()); shadowRadius = 0.3F; withScale(1); }
        @Override
        public RenderType getRenderType(BlightedSporeEntity entity, ResourceLocation texture, MultiBufferSource buffers, float partialTick) { return RenderType.entityTranslucent(texture); }
        @Override
        protected float getDeathMaxRotation(BlightedSporeEntity entity) { return 0; }
    }
    private static final class Model extends GeoModel<BlightedSporeEntity> {
        @Override
        public ResourceLocation getModelResource(BlightedSporeEntity entity) { return resource("geo/blighted_spore.geo.json"); }
        @Override
        public ResourceLocation getTextureResource(BlightedSporeEntity entity) { return resource("textures/entities/" + entity.texture() + ".png"); }
        @Override
        public ResourceLocation getAnimationResource(BlightedSporeEntity entity) { return resource("animations/blighted_spore.animation.json"); }
    }
}
