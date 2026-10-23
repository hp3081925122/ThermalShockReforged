package com.hp.thermalshockreforged.client.lights;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.rendertype.RenderType;
import com.hp.thermalshockreforged.client.compat.LegacyGeoBlockRenderer;
import com.hp.thermalshockreforged.client.compat.LegacyGeoItemRenderer;

import com.hp.thermalshockreforged.content.lights.*;
import net.minecraft.client.renderer.*;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.extensions.common.*;
import com.geckolib.animatable.GeoAnimatable;
import com.hp.thermalshockreforged.client.compat.LegacyGeoModel;
import com.geckolib.renderer.*;

public final class LightsRenderers {
    public static void register(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(LightsContent.GLOWSTICK_ENTITY.get(), context -> new BlockRenderer());
    }

    public static void extensions(RegisterClientExtensionsEvent event) {
        event.registerItem(new IClientItemExtensions() {
            private ItemRenderer renderer;

        }, LightsContent.GLOWSTICK_ITEM.get());
    }

    private static final class BlockRenderer extends LegacyGeoBlockRenderer<GlowstickBlockEntity> {
        private BlockRenderer() { super(new Model<>()); }
        @Override public RenderType getRenderType(GlowstickBlockEntity entity, Identifier texture, MultiBufferSource buffers, float partialTick) {
            return RenderTypes.entityTranslucent(texture);
        }
    }

    private static final class ItemRenderer extends LegacyGeoItemRenderer<GlowstickItem> {
        private ItemRenderer() { super(new Model<>()); }
        @Override public RenderType getRenderType(GlowstickItem item, Identifier texture, MultiBufferSource buffers, float partialTick) {
            return RenderTypes.entityTranslucent(texture);
        }
    }

    private static final class Model<T extends GeoAnimatable> extends LegacyGeoModel<T> {
        @Override public Identifier getModelResource(T entity) { return Identifier.fromNamespaceAndPath("thermal_shock_reforged", "geo/glowstick.geo.json"); }
        @Override public Identifier getTextureResource(T entity) { return Identifier.fromNamespaceAndPath("thermal_shock_reforged", "textures/block/glowstick.png"); }
        @Override public Identifier getAnimationResource(T entity) { return Identifier.fromNamespaceAndPath("thermal_shock_reforged", "animations/glowstick.animation.json"); }
    }
}
