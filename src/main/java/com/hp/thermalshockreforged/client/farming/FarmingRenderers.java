package com.hp.thermalshockreforged.client.farming;

import com.geckolib.animatable.GeoAnimatable;
import com.hp.thermalshockreforged.client.geo.TsrGeoBlockRenderer;
import com.hp.thermalshockreforged.client.geo.TsrGeoItemRenderer;
import com.hp.thermalshockreforged.client.geo.TsrGeoModel;
import com.hp.thermalshockreforged.client.geo.TsrRenderProvider;
import com.hp.thermalshockreforged.compat.GeoRenderProviders;
import com.hp.thermalshockreforged.content.farming.*;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;

public final class FarmingRenderers {
    public static void register(EntityRenderersEvent.RegisterRenderers event) {
        for (CropKind kind : CropKind.values()) if (kind.geo) event.registerBlockEntityRenderer(FarmingContent.cropEntity(kind), BlockRenderer::new);
    }

    public static void extensions(RegisterClientExtensionsEvent event) {
        for (CropKind kind : CropKind.values()) if (kind.geo) GeoRenderProviders.register(TsrRenderProvider.item(() -> new TsrGeoItemRenderer<GeoCropItem>(new Model<>())), FarmingContent.geoItem(kind));
    }

    public static void particles(net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent event) {
        event.registerSpriteSet(FarmingContent.HEARTHFLY.get(), HearthflyParticle.Provider::new);
    }

    private static final class BlockRenderer extends TsrGeoBlockRenderer<GeoCropBlockEntity> {
        private BlockRenderer(BlockEntityRendererProvider.Context context) { super(context, new Model<>()); }
    }

    private static final class Model<T extends GeoAnimatable> extends TsrGeoModel<T> {
        private CropKind kind(T entity) { return entity instanceof GeoCropBlockEntity block ? block.kind : ((GeoCropItem)entity).kind; }
        @Override public Identifier getModelResource(T entity) { return Identifier.fromNamespaceAndPath("thermal_shock_reforged", "geo/" + kind(entity).model() + ".geo.json"); }
        @Override public Identifier getTextureResource(T entity) { return Identifier.fromNamespaceAndPath("thermal_shock_reforged", "textures/block/" + kind(entity).id + ".png"); }
        @Override public Identifier getAnimationResource(T entity) { return Identifier.fromNamespaceAndPath("thermal_shock_reforged", "animations/" + kind(entity).model() + ".animation.json"); }
    }
}
