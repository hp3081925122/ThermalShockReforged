package com.hp.thermalshockreforged.client.spore;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.rendertype.RenderType;

import com.hp.thermalshockreforged.entity.spore.*;
import net.minecraft.client.renderer.*;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.extensions.common.*;
import com.hp.thermalshockreforged.client.compat.LegacyGeoModel;
import com.hp.thermalshockreforged.client.compat.LegacyGeoEntityRenderer;

public final class SporeRenderers {
    private SporeRenderers() { }
    public static void register(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(SporeContent.BLIGHTED_SPORE.get(), context -> new Renderer(context));
        event.registerBlockEntityRenderer(SporeContent.SPORE_POD_ENTITY.get(), context -> new SporePodRenderer());
    }
    public static void extensions(RegisterClientExtensionsEvent event) {
    }
    private static Identifier resource(String path) { return Identifier.fromNamespaceAndPath("thermal_shock_reforged", path); }
    private static final class Renderer extends LegacyGeoEntityRenderer<BlightedSporeEntity> {
        private Renderer(net.minecraft.client.renderer.entity.EntityRendererProvider.Context context) { super(context, new Model()); shadowRadius = 0.3F; withScale(1); }
        @Override
        public RenderType getRenderType(BlightedSporeEntity entity, Identifier texture, MultiBufferSource buffers, float partialTick) { return RenderTypes.entityTranslucent(texture); }
        @Override
        protected float getDeathMaxRotation(BlightedSporeEntity entity) { return 0; }
    }
    private static final class Model extends LegacyGeoModel<BlightedSporeEntity> {
        @Override
        public Identifier getModelResource(BlightedSporeEntity entity) { return resource("geo/blighted_spore.geo.json"); }
        @Override
        public Identifier getTextureResource(BlightedSporeEntity entity) { return resource("textures/entities/" + entity.texture() + ".png"); }
        @Override
        public Identifier getAnimationResource(BlightedSporeEntity entity) { return resource("animations/blighted_spore.animation.json"); }
    }
}
