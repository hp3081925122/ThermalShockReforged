package com.hp.thermalshockreforged.client.doors;
import com.hp.thermalshockreforged.client.geo.*;
import com.geckolib.renderer.base.GeoRenderState;
import com.geckolib.renderer.base.BoneSnapshots;
import net.minecraft.client.renderer.entity.state.EntityRenderState;

import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.rendertype.RenderType;
import com.hp.thermalshockreforged.client.compat.LegacyGeoBlockRenderer;
import com.hp.thermalshockreforged.client.compat.LegacyGeoItemRenderer;

import com.hp.thermalshockreforged.content.doors.*;
import net.minecraft.client.renderer.*;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.extensions.common.*;
import com.geckolib.animatable.GeoAnimatable;
import com.geckolib.renderer.*;

public final class SpecialDoorRenderers {
    public static void register(EntityRenderersEvent.RegisterRenderers event) {
        for (DoorKind kind : DoorKind.values()) event.registerBlockEntityRenderer(SpecialDoorsContent.entity(kind), context -> new BlockRenderer());
    }

    public static void extensions(RegisterClientExtensionsEvent event) {
        for (DoorKind kind : DoorKind.values()) event.registerItem(new IClientItemExtensions() {
            private ItemRenderer renderer;

        }, SpecialDoorsContent.item(kind));
    }

    private static final class BlockRenderer extends LegacyGeoBlockRenderer<SpecialDoorBlockEntity> {
        private BlockRenderer() { super(new Model<>()); }
        @Override public RenderType getRenderType(SpecialDoorBlockEntity entity, Identifier texture, MultiBufferSource buffers, float partialTick) {
            return RenderTypes.entityTranslucent(texture);
        }
    }

    private static final class ItemRenderer extends LegacyGeoItemRenderer<SpecialDoorItem> {
        private ItemRenderer() { super(new Model<>()); }
        @Override public RenderType getRenderType(SpecialDoorItem item, Identifier texture, MultiBufferSource buffers, float partialTick) {
            return RenderTypes.entityTranslucent(texture);
        }
    }

    private static final class Model<T extends GeoAnimatable> extends TsrGeoModel<T> {
        private DoorKind kind(T entity) { return entity instanceof SpecialDoorBlockEntity block ? block.kind : ((SpecialDoorItem)entity).kind; }
        @Override public Identifier getModelResource(T entity) { return Identifier.fromNamespaceAndPath("thermal_shock_reforged", "geo/" + kind(entity).asset + ".geo.json"); }
        @Override public Identifier getTextureResource(T entity) { return Identifier.fromNamespaceAndPath("thermal_shock_reforged", "textures/block/" + kind(entity).asset + ".png"); }
        @Override public Identifier getAnimationResource(T entity) { return Identifier.fromNamespaceAndPath("thermal_shock_reforged", "animations/" + kind(entity).asset + ".animation.json"); }
    }
}
