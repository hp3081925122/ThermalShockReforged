package com.hp.thermalshockreforged.client.doors;

import com.geckolib.animatable.GeoAnimatable;
import com.hp.thermalshockreforged.client.geo.TsrGeoBlockRenderer;
import com.hp.thermalshockreforged.client.geo.TsrGeoItemRenderer;
import com.hp.thermalshockreforged.client.geo.TsrGeoModel;
import com.hp.thermalshockreforged.client.geo.TsrRenderProvider;
import com.hp.thermalshockreforged.compat.GeoRenderProviders;
import com.hp.thermalshockreforged.content.doors.*;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;

public final class SpecialDoorRenderers {
    public static void register(EntityRenderersEvent.RegisterRenderers event) {
        for (DoorKind kind : DoorKind.values()) event.registerBlockEntityRenderer(SpecialDoorsContent.entity(kind), BlockRenderer::new);
    }

    public static void extensions(RegisterClientExtensionsEvent event) {
        for (DoorKind kind : DoorKind.values()) GeoRenderProviders.register(TsrRenderProvider.item(() -> new TsrGeoItemRenderer<SpecialDoorItem>(new Model<>())), SpecialDoorsContent.item(kind));
    }

    private static final class BlockRenderer extends TsrGeoBlockRenderer<SpecialDoorBlockEntity> {
        private BlockRenderer(BlockEntityRendererProvider.Context context) { super(context, new Model<>()); }
    }

    private static final class Model<T extends GeoAnimatable> extends TsrGeoModel<T> {
        private DoorKind kind(T entity) { return entity instanceof SpecialDoorBlockEntity block ? block.kind : ((SpecialDoorItem)entity).kind; }
        @Override public Identifier getModelResource(T entity) { return Identifier.fromNamespaceAndPath("thermal_shock_reforged", "geo/" + kind(entity).asset + ".geo.json"); }
        @Override public Identifier getTextureResource(T entity) { return Identifier.fromNamespaceAndPath("thermal_shock_reforged", "textures/block/" + kind(entity).asset + ".png"); }
        @Override public Identifier getAnimationResource(T entity) { return Identifier.fromNamespaceAndPath("thermal_shock_reforged", "animations/" + kind(entity).asset + ".animation.json"); }
    }
}
