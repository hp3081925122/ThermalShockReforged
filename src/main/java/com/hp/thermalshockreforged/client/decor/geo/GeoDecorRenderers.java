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
        event.registerBlock(new IClientBlockExtensions() {
            @Override public boolean addHitEffects(net.minecraft.world.level.block.state.BlockState state, net.minecraft.world.level.Level level, net.minecraft.world.phys.HitResult target, net.minecraft.client.particle.ParticleEngine manager) {
                if (target instanceof net.minecraft.world.phys.BlockHitResult hit) {
                    var main = DecorPartBlock.main(level, hit.getBlockPos(), state);
                    if (main != null) manager.crack(main, hit.getDirection());
                }
                return true;
            }
            @Override public boolean addDestroyEffects(net.minecraft.world.level.block.state.BlockState state, net.minecraft.world.level.Level level, net.minecraft.core.BlockPos pos, net.minecraft.client.particle.ParticleEngine manager) { return true; }
        }, GeoDecorContent.DECOR_PART.get());
        for (GeoDecorKind kind : GeoDecorKind.values()) event.registerItem(new IClientItemExtensions() {
            private ItemRenderer renderer;
            @Override public BlockEntityWithoutLevelRenderer getCustomRenderer() {
                if (renderer == null) renderer = new ItemRenderer();
                return renderer;
            }
        }, GeoDecorContent.item(kind));
    }

    private static final class BlockRenderer extends GeoBlockRenderer<GeoDecorBlockEntity> {
        private static final int BEAM_COLOR = 0xFF5FD4FF;
        private static final int BEAM_RANGE = 256;
        private BlockRenderer() { super(new Model<>()); }
        private static boolean beacon(GeoDecorBlockEntity entity) { return entity.kind == GeoDecorKind.FRIGID_ALTAR; }
        @Override public void render(GeoDecorBlockEntity entity, float partialTick, com.mojang.blaze3d.vertex.PoseStack poseStack, MultiBufferSource buffers, int packedLight, int packedOverlay) {
            super.render(entity, partialTick, poseStack, buffers, packedLight, packedOverlay);
            if (!beacon(entity) || entity.getLevel() == null) return;
            int height = net.minecraft.client.renderer.blockentity.BeaconRenderer.MAX_RENDER_Y - entity.getBlockPos().getY();
            if (height <= 1) return;
            net.minecraft.client.renderer.blockentity.BeaconRenderer.renderBeaconBeam(poseStack, buffers, net.minecraft.client.renderer.blockentity.BeaconRenderer.BEAM_LOCATION, partialTick, 1, entity.getLevel().getGameTime(), 1, height, BEAM_COLOR, .2F, .25F);
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
