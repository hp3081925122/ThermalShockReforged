package com.hp.thermalshockreforged.client.decor.geo;

import com.geckolib.animatable.GeoAnimatable;
import com.geckolib.constant.dataticket.DataTicket;
import com.hp.thermalshockreforged.client.geo.TsrGeoBlockRenderer;
import com.hp.thermalshockreforged.client.geo.TsrGeoItemRenderer;
import com.hp.thermalshockreforged.client.geo.TsrGeoModel;
import com.hp.thermalshockreforged.client.geo.TsrRenderProvider;
import com.hp.thermalshockreforged.client.particle.AaaStationaryParticle;
import com.hp.thermalshockreforged.compat.GeoRenderProviders;
import com.hp.thermalshockreforged.content.decor.geo.*;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BeaconRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.entity.NoopRenderer;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.resources.Identifier;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;
import net.neoforged.neoforge.client.extensions.common.IClientBlockExtensions;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;
import org.jspecify.annotations.Nullable;

public final class GeoDecorRenderers {
    public static void register(EntityRenderersEvent.RegisterRenderers event) {
        for (GeoDecorKind kind : GeoDecorKind.values()) event.registerBlockEntityRenderer(GeoDecorContent.entity(kind), context -> new BlockRenderer(context, kind));
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
                if (target instanceof net.minecraft.world.phys.BlockHitResult hit && level instanceof ClientLevel client) {
                    var main = DecorPartBlock.main(level, hit.getBlockPos(), state);
                    if (main != null) client.addBreakingBlockEffect(main, hit.getDirection(), hit);
                }
                return true;
            }
            @Override public boolean addDestroyEffects(net.minecraft.world.level.block.state.BlockState state, net.minecraft.world.level.Level level, net.minecraft.core.BlockPos pos, net.minecraft.client.particle.ParticleEngine manager) { return true; }
        }, GeoDecorContent.DECOR_PART.get());
        for (GeoDecorKind kind : GeoDecorKind.values()) GeoRenderProviders.register(TsrRenderProvider.item(() -> new TsrGeoItemRenderer<GeoDecorItem>(new Model<>())), GeoDecorContent.item(kind));
    }

    private static final class BlockRenderer extends TsrGeoBlockRenderer<GeoDecorBlockEntity> {
        private static final DataTicket<Float> BEAM_TIME = DataTicket.create("thermal_shock_reforged:decor_beam_time", Float.class);
        private static final int BEAM_COLOR = 0xFF5FD4FF;
        private static final int BEAM_RANGE = 256;
        private final boolean beacon;
        private BlockRenderer(BlockEntityRendererProvider.Context context, GeoDecorKind kind) { super(context, new Model<>()); beacon = kind == GeoDecorKind.FRIGID_ALTAR; }
        @Override public void addRenderData(GeoDecorBlockEntity entity, @Nullable Void relatedObject, BlockEntityRenderState state, float partialTick) {
            if (beacon && entity.getLevel() != null) state.addGeckolibData(BEAM_TIME, Math.floorMod(entity.getLevel().getGameTime(), 40) + partialTick);
        }
        @Override public void submit(BlockEntityRenderState state, PoseStack poseStack, SubmitNodeCollector renderTasks, CameraRenderState cameraRenderState) {
            super.submit(state, poseStack, renderTasks, cameraRenderState);
            Float time = state.getGeckolibData(BEAM_TIME);
            if (time == null) return;
            int height = BeaconRenderer.MAX_RENDER_Y - state.blockPos.getY();
            if (height <= 1) return;
            BeaconRenderer.submitBeaconBeam(poseStack, renderTasks, BeaconRenderer.BEAM_LOCATION, 1, time, 1, height, BEAM_COLOR, .2F, .25F);
        }
        @Override public boolean shouldRenderOffScreen() { return beacon || super.shouldRenderOffScreen(); }
        @Override public boolean shouldRender(GeoDecorBlockEntity entity, Vec3 camera) {
            if (!beacon) return super.shouldRender(entity, camera);
            return Vec3.atCenterOf(entity.getBlockPos()).multiply(1, 0, 1).closerThan(camera.multiply(1, 0, 1), BEAM_RANGE);
        }
        @Override public AABB getRenderBoundingBox(GeoDecorBlockEntity entity) {
            if (!beacon) return super.getRenderBoundingBox(entity);
            var pos = entity.getBlockPos();
            return new AABB(pos.getX(), pos.getY(), pos.getZ(), pos.getX() + 1, BeaconRenderer.MAX_RENDER_Y, pos.getZ() + 1);
        }
    }

    private static final class Model<T extends GeoAnimatable> extends TsrGeoModel<T> {
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
