package com.hp.thermalshockreforged.client.icesquad;

import com.hp.thermalshockreforged.ThermalShockReforged;
import com.hp.thermalshockreforged.client.ProjectileRenderState;
import com.hp.thermalshockreforged.entity.icesquad.IceSquadProjectileEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;
import net.minecraft.util.LightCoordsUtil;

final class ShardRenderer extends EntityRenderer<IceSquadProjectileEntity, ProjectileRenderState> {
    private static final Identifier TEXTURE = Identifier.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, "textures/particle/lod_shard.png");
    private static final float LENGTH = .5F;

    ShardRenderer(EntityRendererProvider.Context context) {
        super(context);
        shadowRadius = 0;
    }

    @Override public ProjectileRenderState createRenderState() { return new ProjectileRenderState(); }

    @Override public void extractRenderState(IceSquadProjectileEntity entity, ProjectileRenderState state, float partialTick) {
        super.extractRenderState(entity, state, partialTick);
        state.rotations(entity, partialTick);
    }

    @Override public void submit(ProjectileRenderState state, PoseStack pose, SubmitNodeCollector collector, CameraRenderState camera) {
        pose.pushPose();
        pose.mulPose(Axis.YP.rotationDegrees(state.yRot - 90));
        pose.mulPose(Axis.ZP.rotationDegrees(state.xRot));
        collector.submitCustomGeometry(pose, RenderTypes.entityTranslucent(TEXTURE), (last, consumer) -> {
            vertex(consumer, last, -LENGTH, -LENGTH, 0, 0, 1);
            vertex(consumer, last, LENGTH, -LENGTH, 0, 0, 0);
            vertex(consumer, last, LENGTH, LENGTH, 0, 1, 0);
            vertex(consumer, last, -LENGTH, LENGTH, 0, 1, 1);
            vertex(consumer, last, -LENGTH, 0, -LENGTH, 0, 1);
            vertex(consumer, last, LENGTH, 0, -LENGTH, 0, 0);
            vertex(consumer, last, LENGTH, 0, LENGTH, 1, 0);
            vertex(consumer, last, -LENGTH, 0, LENGTH, 1, 1);
        });
        pose.popPose();
        super.submit(state, pose, collector, camera);
    }

    private static void vertex(VertexConsumer consumer, PoseStack.Pose pose, float x, float y, float z, float u, float v) {
        consumer.addVertex(pose, x, y, z).setColor(1, 1, 1, 1F).setUv(u, v).setOverlay(OverlayTexture.NO_OVERLAY).setLight(LightCoordsUtil.FULL_BRIGHT).setNormal(pose, 0, 1, 0);
    }
}
