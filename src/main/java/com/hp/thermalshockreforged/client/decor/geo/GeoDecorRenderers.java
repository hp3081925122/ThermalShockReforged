package com.hp.thermalshockreforged.client.decor.geo;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.rendertype.RenderType;
import com.hp.thermalshockreforged.client.compat.LegacyGeoBlockRenderer;
import com.hp.thermalshockreforged.client.compat.LegacyGeoItemRenderer;

import com.hp.thermalshockreforged.content.decor.geo.*;
import com.hp.thermalshockreforged.client.particle.AaaStationaryParticle;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.renderer.entity.NoopRenderer;
import net.minecraft.client.renderer.*;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;
import net.neoforged.neoforge.client.extensions.common.*;
import com.geckolib.animatable.GeoAnimatable;
import com.hp.thermalshockreforged.client.compat.LegacyGeoModel;
import com.geckolib.renderer.*;

public final class GeoDecorRenderers {
    public static void register(EntityRenderersEvent.RegisterRenderers event) {
        for (GeoDecorKind kind : GeoDecorKind.values()) event.registerBlockEntityRenderer(GeoDecorContent.entity(kind), context -> new BlockRenderer());
        event.registerEntityRenderer(BrimstoneCakeConsumption.TYPE.get(), NoopRenderer::new);
    }

    public static void particles(RegisterParticleProvidersEvent event) {
        for (int index = 0; index < GeoDecorContent.HOLOGRAM_PARTICLES.size(); index++) {
            String effect = "holo" + (index + 1);
            event.registerSpriteSet(GeoDecorContent.HOLOGRAM_PARTICLES.get(index).get(), sprites -> new AaaStationaryParticle.Provider(sprites, effect, 0, net.minecraft.client.particle.SingleQuadParticle.Layer.OPAQUE, true, false, 0));
        }
    }

    public static void extensions(RegisterClientExtensionsEvent event) {
        event.registerBlock(new IClientBlockExtensions() {
            @Override public boolean addHitEffects(net.minecraft.world.level.block.state.BlockState state, net.minecraft.world.level.Level level, net.minecraft.world.phys.HitResult target, net.minecraft.client.particle.ParticleEngine manager) {
                if (target instanceof net.minecraft.world.phys.BlockHitResult hit) {
                    var main = DecorPartBlock.main(level, hit.getBlockPos(), state);
                    if (main != null) { }
                }
                return true;
            }
            @Override public boolean addDestroyEffects(net.minecraft.world.level.block.state.BlockState state, net.minecraft.world.level.Level level, net.minecraft.core.BlockPos pos, net.minecraft.client.particle.ParticleEngine manager) { return true; }
        }, GeoDecorContent.DECOR_PART.get());
        for (GeoDecorKind kind : GeoDecorKind.values()) event.registerItem(new IClientItemExtensions() {
            private ItemRenderer renderer;

        }, GeoDecorContent.item(kind));
    }

    private static final class BlockRenderer extends LegacyGeoBlockRenderer<GeoDecorBlockEntity> {
        private static final int BEAM_COLOR = 0xFF5FD4FF;
        private static final int BEAM_RANGE = 256;
        private BlockRenderer() { super(new Model<>()); }
        private static boolean beacon(GeoDecorBlockEntity entity) { return entity.kind == GeoDecorKind.FRIGID_ALTAR; }
        @Override public void render(GeoDecorBlockEntity entity, float partialTick, com.mojang.blaze3d.vertex.PoseStack poseStack, MultiBufferSource buffers, int packedLight, int packedOverlay) {
            super.render(entity, partialTick, poseStack, buffers, packedLight, packedOverlay);
            if (!beacon(entity) || entity.getLevel() == null) return;
            int height = net.minecraft.client.renderer.blockentity.BeaconRenderer.MAX_RENDER_Y - entity.getBlockPos().getY();
            if (height <= 1) return;
            if (height > 1) { }
        }
        @Override public boolean shouldRenderOffScreen(GeoDecorBlockEntity entity) { return beacon(entity) || super.shouldRenderOffScreen(entity); }
        @Override public boolean shouldRender(GeoDecorBlockEntity entity, net.minecraft.world.phys.Vec3 camera) {
            if (!beacon(entity)) return super.shouldRender(entity, camera);
            return net.minecraft.world.phys.Vec3.atCenterOf(entity.getBlockPos()).multiply(1, 0, 1).closerThan(camera.multiply(1, 0, 1), BEAM_RANGE);
        }
        @Override public net.minecraft.world.phys.AABB getRenderBoundingBox(GeoDecorBlockEntity entity) {
            if (!beacon(entity)) return super.getRenderBoundingBox(entity);
            var pos = entity.getBlockPos();
            return new net.minecraft.world.phys.AABB(pos.getX(), pos.getY(), pos.getZ(), pos.getX() + 1, net.minecraft.client.renderer.blockentity.BeaconRenderer.MAX_RENDER_Y, pos.getZ() + 1);
        }
        @Override public RenderType getRenderType(GeoDecorBlockEntity entity, Identifier texture, MultiBufferSource buffers, float partialTick) {
            return RenderTypes.entityTranslucent(texture);
        }
    }

    private static final class ItemRenderer extends LegacyGeoItemRenderer<GeoDecorItem> {
        private ItemRenderer() { super(new Model<>()); }
        @Override public RenderType getRenderType(GeoDecorItem item, Identifier texture, MultiBufferSource buffers, float partialTick) {
            return RenderTypes.entityTranslucent(texture);
        }
    }

    private static final class Model<T extends GeoAnimatable> extends LegacyGeoModel<T> {
        private GeoDecorKind kind(T entity) { return entity instanceof GeoDecorBlockEntity block ? block.kind : ((GeoDecorItem)entity).kind; }
        private String civilianAsset(T entity) {
            int variant = entity instanceof GeoDecorBlockEntity block ? block.getBlockState().getValue(DialogueBlock.VARIANT) : 0;
            return variant == 6 ? "neon_lime_sootling" : "civil" + (variant + 1);
        }
        @Override public Identifier getModelResource(T entity) { return Identifier.fromNamespaceAndPath("thermal_shock_reforged", kind(entity) == GeoDecorKind.CIVILIAN ? "geo/" + civilianAsset(entity) + ".geo.json" : kind(entity).model); }
        @Override public Identifier getTextureResource(T entity) {
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
            return Identifier.fromNamespaceAndPath("thermal_shock_reforged", texture);
        }
        @Override public Identifier getAnimationResource(T entity) { return Identifier.fromNamespaceAndPath("thermal_shock_reforged", kind(entity) == GeoDecorKind.CIVILIAN ? "animations/" + civilianAsset(entity) + ".animation.json" : kind(entity).animation); }
    }
}
