package com.hp.thermalshockreforged.client.decor.geo;

import com.hp.thermalshockreforged.content.decor.geo.*;
import net.minecraft.client.renderer.*;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.extensions.common.*;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.*;

public final class GeoDecorRenderers {
    public static void register(EntityRenderersEvent.RegisterRenderers event) {
        for (GeoDecorKind kind : GeoDecorKind.values()) event.registerBlockEntityRenderer(GeoDecorContent.entity(kind), context -> new BlockRenderer());
    }

    public static void extensions(RegisterClientExtensionsEvent event) {
        for (GeoDecorKind kind : GeoDecorKind.values()) event.registerItem(new IClientItemExtensions() {
            private ItemRenderer renderer;
            @Override public BlockEntityWithoutLevelRenderer getCustomRenderer() {
                if (renderer == null) renderer = new ItemRenderer();
                return renderer;
            }
        }, GeoDecorContent.item(kind));
    }

    private static final class BlockRenderer extends GeoBlockRenderer<GeoDecorBlockEntity> {
        private BlockRenderer() { super(new Model<>()); }
        @Override public RenderType getRenderType(GeoDecorBlockEntity entity, ResourceLocation texture, MultiBufferSource buffers, float partialTick) {
            return RenderType.entityTranslucent(texture);
        }
    }

    private static final class ItemRenderer extends GeoItemRenderer<GeoDecorItem> {
        private ItemRenderer() { super(new Model<>()); }
        @Override public RenderType getRenderType(GeoDecorItem item, ResourceLocation texture, MultiBufferSource buffers, float partialTick) {
            return RenderType.entityTranslucent(texture);
        }
    }

    private static final class Model<T extends GeoAnimatable> extends GeoModel<T> {
        private GeoDecorKind kind(T entity) { return entity instanceof GeoDecorBlockEntity block ? block.kind : ((GeoDecorItem)entity).kind; }
        @Override public ResourceLocation getModelResource(T entity) { return ResourceLocation.fromNamespaceAndPath("thermal_shock_reforged", kind(entity).model); }
        @Override public ResourceLocation getTextureResource(T entity) { return ResourceLocation.fromNamespaceAndPath("thermal_shock_reforged", kind(entity).texture); }
        @Override public ResourceLocation getAnimationResource(T entity) { return ResourceLocation.fromNamespaceAndPath("thermal_shock_reforged", kind(entity).animation); }
    }
}
