package com.hp.thermalshock.client.farming;

import com.hp.thermalshock.content.farming.*;
import net.minecraft.client.renderer.*;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.extensions.common.*;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.*;

public final class FarmingRenderers {
    public static void register(EntityRenderersEvent.RegisterRenderers event) {
        for (CropKind kind : CropKind.values()) if (kind.geo) event.registerBlockEntityRenderer(FarmingContent.cropEntity(kind), context -> new BlockRenderer());
    }

    public static void extensions(RegisterClientExtensionsEvent event) {
        for (CropKind kind : CropKind.values()) if (kind.geo) event.registerItem(new IClientItemExtensions() {
            private ItemRenderer renderer;
            @Override public BlockEntityWithoutLevelRenderer getCustomRenderer() {
                if (renderer == null) renderer = new ItemRenderer();
                return renderer;
            }
        }, FarmingContent.geoItem(kind));
    }

    public static void layers() {
        net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(FarmingContent.crop(CropKind.SKORIO), RenderType.cutout());
        net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(FarmingContent.crop(CropKind.BARBUBBLE), RenderType.cutout());
        for (CropKind kind : CropKind.values()) net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(FarmingContent.seed(kind), RenderType.cutout());
    }

    public static void particles(net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent event) {
        event.registerSpriteSet(FarmingContent.HEARTHFLY.get(), HearthflyParticle.Provider::new);
    }

    private static final class BlockRenderer extends GeoBlockRenderer<GeoCropBlockEntity> {
        private BlockRenderer() { super(new Model<>()); }
        @Override public RenderType getRenderType(GeoCropBlockEntity entity, ResourceLocation texture, MultiBufferSource buffers, float partialTick) {
            return RenderType.entityTranslucent(texture);
        }
    }

    private static final class ItemRenderer extends GeoItemRenderer<GeoCropItem> {
        private ItemRenderer() { super(new Model<>()); }
        @Override public RenderType getRenderType(GeoCropItem item, ResourceLocation texture, MultiBufferSource buffers, float partialTick) {
            return RenderType.entityTranslucent(texture);
        }
    }

    private static final class Model<T extends GeoAnimatable> extends GeoModel<T> {
        private CropKind kind(T entity) { return entity instanceof GeoCropBlockEntity block ? block.kind : ((GeoCropItem)entity).kind; }
        @Override public ResourceLocation getModelResource(T entity) { return ResourceLocation.fromNamespaceAndPath("thermal_shock", "geo/" + kind(entity).model() + ".geo.json"); }
        @Override public ResourceLocation getTextureResource(T entity) { return ResourceLocation.fromNamespaceAndPath("thermal_shock", "textures/block/" + kind(entity).id + ".png"); }
        @Override public ResourceLocation getAnimationResource(T entity) { return ResourceLocation.fromNamespaceAndPath("thermal_shock", "animations/" + kind(entity).model() + ".animation.json"); }
    }
}
