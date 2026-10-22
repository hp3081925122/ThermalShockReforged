package com.hp.thermalshockreforged.client.outcrops;

import com.hp.thermalshockreforged.ThermalShockReforged;
import com.hp.thermalshockreforged.content.outcrops.*;
import net.minecraft.client.renderer.*;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.extensions.common.*;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.*;

public final class OutcropRenderers {
    public static void register(EntityRenderersEvent.RegisterRenderers event) {
        for (var kind : OutcropKind.values()) event.registerBlockEntityRenderer(OutcropContent.entity(kind), context -> new BlockRenderer());
    }
    public static void extensions(RegisterClientExtensionsEvent event) {
        for (var kind : OutcropKind.values()) event.registerItem(new IClientItemExtensions() {
            private ItemRenderer renderer;
            @Override public BlockEntityWithoutLevelRenderer getCustomRenderer() {
                if (renderer == null) renderer = new ItemRenderer();
                return renderer;
            }
        }, OutcropContent.item(kind));
    }
    private static final class BlockRenderer extends GeoBlockRenderer<OutcropBlockEntity> {
        private BlockRenderer() { super(new Model<>()); }
        @Override public RenderType getRenderType(OutcropBlockEntity entity, ResourceLocation texture, MultiBufferSource buffers, float partialTick) { return RenderType.entityTranslucentCull(texture); }
    }
    private static final class ItemRenderer extends GeoItemRenderer<OutcropItem> {
        private ItemRenderer() { super(new Model<>()); }
        @Override public RenderType getRenderType(OutcropItem item, ResourceLocation texture, MultiBufferSource buffers, float partialTick) { return RenderType.entityTranslucent(texture); }
    }
    private static final class Model<T extends GeoAnimatable> extends GeoModel<T> {
        @Override public ResourceLocation getModelResource(T entity) { return ResourceLocation.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, "geo/fluorite_ore.geo.json"); }
        @Override public ResourceLocation getAnimationResource(T entity) { return ResourceLocation.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, "animations/fluorite_ore.animation.json"); }
        @Override public ResourceLocation getTextureResource(T entity) {
            var kind = entity instanceof OutcropBlockEntity block ? block.kind : ((OutcropItem)entity).kind;
            return ResourceLocation.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, "textures/block/" + kind.id + ".png");
        }
    }
}
