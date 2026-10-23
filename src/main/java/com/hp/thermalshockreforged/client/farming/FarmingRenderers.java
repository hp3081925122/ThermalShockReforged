package com.hp.thermalshockreforged.client.farming;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.rendertype.RenderType;
import com.hp.thermalshockreforged.client.compat.LegacyGeoBlockRenderer;
import com.hp.thermalshockreforged.client.compat.LegacyGeoItemRenderer;

import com.hp.thermalshockreforged.content.farming.*;
import net.minecraft.client.renderer.*;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.extensions.common.*;
import com.geckolib.animatable.GeoAnimatable;
import com.hp.thermalshockreforged.client.compat.LegacyGeoModel;
import com.geckolib.renderer.*;

public final class FarmingRenderers {
    public static void register(EntityRenderersEvent.RegisterRenderers event) {
        for (CropKind kind : CropKind.values()) if (kind.geo) event.registerBlockEntityRenderer(FarmingContent.cropEntity(kind), context -> new BlockRenderer());
    }

    public static void extensions(RegisterClientExtensionsEvent event) {
        for (CropKind kind : CropKind.values()) if (kind.geo) event.registerItem(new IClientItemExtensions() {
            private ItemRenderer renderer;

        }, FarmingContent.geoItem(kind));
    }

    public static void layers() {
        // 26.1：方块 cutout 由模型 JSON 的 render_type 控制，ItemBlockRenderTypes 已移除
    }

    public static void particles(net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent event) {
        event.registerSpriteSet(FarmingContent.HEARTHFLY.get(), HearthflyParticle.Provider::new);
    }

    private static final class BlockRenderer extends LegacyGeoBlockRenderer<GeoCropBlockEntity> {
        private BlockRenderer() { super(new Model<>()); }
        @Override public RenderType getRenderType(GeoCropBlockEntity entity, Identifier texture, MultiBufferSource buffers, float partialTick) {
            return RenderTypes.entityTranslucent(texture);
        }
    }

    private static final class ItemRenderer extends LegacyGeoItemRenderer<GeoCropItem> {
        private ItemRenderer() { super(new Model<>()); }
        @Override public RenderType getRenderType(GeoCropItem item, Identifier texture, MultiBufferSource buffers, float partialTick) {
            return RenderTypes.entityTranslucent(texture);
        }
    }

    private static final class Model<T extends GeoAnimatable> extends LegacyGeoModel<T> {
        private CropKind kind(T entity) { return entity instanceof GeoCropBlockEntity block ? block.kind : ((GeoCropItem)entity).kind; }
        @Override public Identifier getModelResource(T entity) { return Identifier.fromNamespaceAndPath("thermal_shock_reforged", "geo/" + kind(entity).model() + ".geo.json"); }
        @Override public Identifier getTextureResource(T entity) { return Identifier.fromNamespaceAndPath("thermal_shock_reforged", "textures/block/" + kind(entity).id + ".png"); }
        @Override public Identifier getAnimationResource(T entity) { return Identifier.fromNamespaceAndPath("thermal_shock_reforged", "animations/" + kind(entity).model() + ".animation.json"); }
    }
}
