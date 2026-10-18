package com.hp.thermalshockreforged.client.doors;

import com.hp.thermalshockreforged.content.doors.*;
import net.minecraft.client.renderer.*;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.extensions.common.*;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.*;

public final class SpecialDoorRenderers {
    public static void register(EntityRenderersEvent.RegisterRenderers event) {
        for (DoorKind kind : DoorKind.values()) event.registerBlockEntityRenderer(SpecialDoorsContent.entity(kind), context -> new BlockRenderer());
    }

    public static void extensions(RegisterClientExtensionsEvent event) {
        for (DoorKind kind : DoorKind.values()) event.registerItem(new IClientItemExtensions() {
            private ItemRenderer renderer;
            @Override public BlockEntityWithoutLevelRenderer getCustomRenderer() {
                if (renderer == null) renderer = new ItemRenderer();
                return renderer;
            }
        }, SpecialDoorsContent.item(kind));
    }

    private static final class BlockRenderer extends GeoBlockRenderer<SpecialDoorBlockEntity> {
        private BlockRenderer() { super(new Model<>()); }
        @Override public RenderType getRenderType(SpecialDoorBlockEntity entity, ResourceLocation texture, MultiBufferSource buffers, float partialTick) {
            return RenderType.entityTranslucent(texture);
        }
    }

    private static final class ItemRenderer extends GeoItemRenderer<SpecialDoorItem> {
        private ItemRenderer() { super(new Model<>()); }
        @Override public RenderType getRenderType(SpecialDoorItem item, ResourceLocation texture, MultiBufferSource buffers, float partialTick) {
            return RenderType.entityTranslucent(texture);
        }
    }

    private static final class Model<T extends GeoAnimatable> extends GeoModel<T> {
        private DoorKind kind(T entity) { return entity instanceof SpecialDoorBlockEntity block ? block.kind : ((SpecialDoorItem)entity).kind; }
        @Override public ResourceLocation getModelResource(T entity) { return ResourceLocation.fromNamespaceAndPath("thermal_shock_reforged", "geo/" + kind(entity).asset + ".geo.json"); }
        @Override public ResourceLocation getTextureResource(T entity) { return ResourceLocation.fromNamespaceAndPath("thermal_shock_reforged", "textures/block/" + kind(entity).asset + ".png"); }
        @Override public ResourceLocation getAnimationResource(T entity) { return ResourceLocation.fromNamespaceAndPath("thermal_shock_reforged", "animations/" + kind(entity).asset + ".animation.json"); }
    }
}
