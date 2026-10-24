package com.hp.thermalshockreforged.client.lights;

import com.geckolib.animatable.GeoAnimatable;
import com.hp.thermalshockreforged.client.geo.TsrGeoBlockRenderer;
import com.hp.thermalshockreforged.client.geo.TsrGeoItemRenderer;
import com.hp.thermalshockreforged.client.geo.TsrGeoModel;
import com.hp.thermalshockreforged.client.geo.TsrRenderProvider;
import com.hp.thermalshockreforged.compat.GeoRenderProviders;
import com.hp.thermalshockreforged.content.lights.*;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;

public final class LightsRenderers {
    public static void register(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(LightsContent.GLOWSTICK_ENTITY.get(), BlockRenderer::new);
    }

    public static void extensions(RegisterClientExtensionsEvent event) {
        GeoRenderProviders.register(TsrRenderProvider.item(() -> new TsrGeoItemRenderer<GlowstickItem>(new Model<>())), LightsContent.GLOWSTICK_ITEM.get());
    }

    private static final class BlockRenderer extends TsrGeoBlockRenderer<GlowstickBlockEntity> {
        private BlockRenderer(BlockEntityRendererProvider.Context context) { super(context, new Model<>()); }
    }

    private static final class Model<T extends GeoAnimatable> extends TsrGeoModel<T> {
        @Override public Identifier getModelResource(T entity) { return Identifier.fromNamespaceAndPath("thermal_shock_reforged", "geo/glowstick.geo.json"); }
        @Override public Identifier getTextureResource(T entity) { return Identifier.fromNamespaceAndPath("thermal_shock_reforged", "textures/block/glowstick.png"); }
        @Override public Identifier getAnimationResource(T entity) { return Identifier.fromNamespaceAndPath("thermal_shock_reforged", "animations/glowstick.animation.json"); }
    }
}
