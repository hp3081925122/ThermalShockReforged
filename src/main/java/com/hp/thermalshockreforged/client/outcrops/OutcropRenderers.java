package com.hp.thermalshockreforged.client.outcrops;

import com.geckolib.animatable.GeoAnimatable;
import com.hp.thermalshockreforged.ThermalShockReforged;
import com.hp.thermalshockreforged.client.geo.TsrGeoBlockRenderer;
import com.hp.thermalshockreforged.client.geo.TsrGeoItemRenderer;
import com.hp.thermalshockreforged.client.geo.TsrGeoModel;
import com.hp.thermalshockreforged.client.geo.TsrRenderProvider;
import com.hp.thermalshockreforged.client.geo.TsrRenderTypes;
import com.hp.thermalshockreforged.compat.GeoRenderProviders;
import com.hp.thermalshockreforged.content.outcrops.*;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;

public final class OutcropRenderers {
    public static void register(EntityRenderersEvent.RegisterRenderers event) {
        for (var kind : OutcropKind.values()) event.registerBlockEntityRenderer(OutcropContent.entity(kind), BlockRenderer::new);
    }
    public static void extensions(RegisterClientExtensionsEvent event) {
        for (var kind : OutcropKind.values()) GeoRenderProviders.register(TsrRenderProvider.item(() -> new TsrGeoItemRenderer<OutcropItem>(new Model<>())), OutcropContent.item(kind));
    }
    private static final class BlockRenderer extends TsrGeoBlockRenderer<OutcropBlockEntity> {
        private BlockRenderer(BlockEntityRendererProvider.Context context) { super(context, new Model<>()); }
        @Override public RenderType getRenderType(BlockEntityRenderState state, Identifier texture) { return TsrRenderTypes.entityTranslucentCull(texture); }
    }
    private static final class Model<T extends GeoAnimatable> extends TsrGeoModel<T> {
        @Override public Identifier getModelResource(T entity) { return Identifier.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, "geo/fluorite_ore.geo.json"); }
        @Override public Identifier getAnimationResource(T entity) { return Identifier.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, "animations/fluorite_ore.animation.json"); }
        @Override public Identifier getTextureResource(T entity) {
            var kind = entity instanceof OutcropBlockEntity block ? block.kind : ((OutcropItem)entity).kind;
            return Identifier.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, "textures/block/" + kind.id + ".png");
        }
    }
}
