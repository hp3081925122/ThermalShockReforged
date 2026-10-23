package com.hp.thermalshockreforged.client.outcrops;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.rendertype.RenderType;
import com.hp.thermalshockreforged.client.compat.LegacyGeoBlockRenderer;
import com.hp.thermalshockreforged.client.compat.LegacyGeoItemRenderer;

import com.hp.thermalshockreforged.ThermalShockReforged;
import com.hp.thermalshockreforged.content.outcrops.*;
import net.minecraft.client.renderer.*;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.extensions.common.*;
import com.geckolib.animatable.GeoAnimatable;
import com.hp.thermalshockreforged.client.compat.LegacyGeoModel;
import com.geckolib.renderer.*;

public final class OutcropRenderers {
    public static void register(EntityRenderersEvent.RegisterRenderers event) {
        for (var kind : OutcropKind.values()) event.registerBlockEntityRenderer(OutcropContent.entity(kind), context -> new BlockRenderer());
    }
    public static void extensions(RegisterClientExtensionsEvent event) {
        for (var kind : OutcropKind.values()) event.registerItem(new IClientItemExtensions() {
            private ItemRenderer renderer;

        }, OutcropContent.item(kind));
    }
    private static final class BlockRenderer extends LegacyGeoBlockRenderer<OutcropBlockEntity> {
        private BlockRenderer() { super(new Model<>()); }
        @Override public RenderType getRenderType(OutcropBlockEntity entity, Identifier texture, MultiBufferSource buffers, float partialTick) { return RenderTypes.entityTranslucentCull(texture); }
    }
    private static final class ItemRenderer extends LegacyGeoItemRenderer<OutcropItem> {
        private ItemRenderer() { super(new Model<>()); }
        @Override public RenderType getRenderType(OutcropItem item, Identifier texture, MultiBufferSource buffers, float partialTick) { return RenderTypes.entityTranslucent(texture); }
    }
    private static final class Model<T extends GeoAnimatable> extends LegacyGeoModel<T> {
        @Override public Identifier getModelResource(T entity) { return Identifier.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, "geo/fluorite_ore.geo.json"); }
        @Override public Identifier getAnimationResource(T entity) { return Identifier.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, "animations/fluorite_ore.animation.json"); }
        @Override public Identifier getTextureResource(T entity) {
            var kind = entity instanceof OutcropBlockEntity block ? block.kind : ((OutcropItem)entity).kind;
            return Identifier.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, "textures/block/" + kind.id + ".png");
        }
    }
}
