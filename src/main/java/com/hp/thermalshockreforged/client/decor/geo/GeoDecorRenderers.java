package com.hp.thermalshockreforged.client.decor.geo;

import com.hp.thermalshockreforged.content.decor.geo.*;
import com.hp.thermalshockreforged.client.particle.AaaStationaryParticle;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.renderer.entity.NoopRenderer;
import net.minecraft.client.renderer.*;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;
import net.neoforged.neoforge.client.extensions.common.*;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.*;

public final class GeoDecorRenderers {
    public static void register(EntityRenderersEvent.RegisterRenderers event) {
        for (GeoDecorKind kind : GeoDecorKind.values()) event.registerBlockEntityRenderer(GeoDecorContent.entity(kind), context -> new BlockRenderer());
        event.registerEntityRenderer(BrimstoneCakeConsumption.TYPE.get(), NoopRenderer::new);
    }

    public static void particles(RegisterParticleProvidersEvent event) {
        for (int index = 0; index < GeoDecorContent.HOLOGRAM_PARTICLES.size(); index++) {
            String effect = "holo" + (index + 1);
            event.registerSpriteSet(GeoDecorContent.HOLOGRAM_PARTICLES.get(index).get(), sprites -> new AaaStationaryParticle.Provider(sprites, effect, 0, ParticleRenderType.PARTICLE_SHEET_LIT, true, false, 0));
        }
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
        private String civilianAsset(T entity) {
            int variant = entity instanceof GeoDecorBlockEntity block ? block.getBlockState().getValue(DialogueBlock.VARIANT) : 0;
            return variant == 6 ? "neon_lime_sootling" : "civil" + (variant + 1);
        }
        @Override public ResourceLocation getModelResource(T entity) { return ResourceLocation.fromNamespaceAndPath("thermal_shock_reforged", kind(entity) == GeoDecorKind.CIVILIAN ? "geo/" + civilianAsset(entity) + ".geo.json" : kind(entity).model); }
        @Override public ResourceLocation getTextureResource(T entity) {
            String texture = kind(entity).texture;
            if (entity instanceof GeoDecorBlockEntity block && block.kind == GeoDecorKind.CAPSULE) texture = "textures/block/" + switch (block.getBlockState().getValue(CapsuleBlock.VARIANT)) {
                case 1 -> "capsule_errant";
                case 2 -> "capsule_frostilyte";
                case 3 -> "capsule_kilolyte";
                case 4 -> "capsule_ripple";
                default -> "capsule";
            } + ".png";
            if (entity instanceof GeoDecorBlockEntity block && block.kind == GeoDecorKind.SHALE_POT) texture = "textures/block/pot" + (block.getBlockState().getValue(ShalePotBlock.VARIANT) + 1) + ".png";
            if (kind(entity) == GeoDecorKind.CIVILIAN) texture = "textures/block/" + (civilianAsset(entity).equals("neon_lime_sootling") ? "neon_sootling" : civilianAsset(entity)) + ".png";
            return ResourceLocation.fromNamespaceAndPath("thermal_shock_reforged", texture);
        }
        @Override public ResourceLocation getAnimationResource(T entity) { return ResourceLocation.fromNamespaceAndPath("thermal_shock_reforged", kind(entity) == GeoDecorKind.CIVILIAN ? "animations/" + civilianAsset(entity) + ".animation.json" : kind(entity).animation); }
    }
}
