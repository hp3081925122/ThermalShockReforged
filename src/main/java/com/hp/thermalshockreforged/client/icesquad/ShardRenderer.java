package com.hp.thermalshockreforged.client.icesquad;
import net.minecraft.client.renderer.rendertype.RenderTypes;

import com.hp.thermalshockreforged.ThermalShockReforged;
import com.hp.thermalshockreforged.entity.icesquad.IceSquadProjectileEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.util.LightCoordsUtil;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.rendertype.RenderType;
import com.hp.thermalshockreforged.client.compat.LegacyEntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;

final class ShardRenderer extends LegacyEntityRenderer<IceSquadProjectileEntity> {
    private static final Identifier TEXTURE = Identifier.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, "textures/particle/lod_shard.png");
    private static final float LENGTH = .5F;

    ShardRenderer(EntityRendererProvider.Context context) {
        super(context);
        shadowRadius = 0;
    }

    @Override public void render(IceSquadProjectileEntity entity, float yaw, float partialTick, PoseStack pose, MultiBufferSource buffers, int light) {
        pose.pushPose();
        pose.mulPose(Axis.YP.rotationDegrees(Mth.lerp(partialTick, entity.yRotO, entity.getYRot()) - 90));
        pose.mulPose(Axis.ZP.rotationDegrees(Mth.lerp(partialTick, entity.xRotO, entity.getXRot())));
        VertexConsumer consumer = buffers.getBuffer(RenderTypes.entityTranslucent(TEXTURE));
        PoseStack.Pose last = pose.last();
        vertex(consumer, last, -LENGTH, -LENGTH, 0, 0, 1);
        vertex(consumer, last, LENGTH, -LENGTH, 0, 0, 0);
        vertex(consumer, last, LENGTH, LENGTH, 0, 1, 0);
        vertex(consumer, last, -LENGTH, LENGTH, 0, 1, 1);
        vertex(consumer, last, -LENGTH, 0, -LENGTH, 0, 1);
        vertex(consumer, last, LENGTH, 0, -LENGTH, 0, 0);
        vertex(consumer, last, LENGTH, 0, LENGTH, 1, 0);
        vertex(consumer, last, -LENGTH, 0, LENGTH, 1, 1);
        pose.popPose();
        super.render(entity, yaw, partialTick, pose, buffers, light);
    }

    private static void vertex(VertexConsumer consumer, PoseStack.Pose pose, float x, float y, float z, float u, float v) {
        consumer.addVertex(pose, x, y, z).setColor(1, 1, 1, 1F).setUv(u, v).setOverlay(OverlayTexture.NO_OVERLAY).setLight(LightCoordsUtil.FULL_BRIGHT).setNormal(pose, 0, 1, 0);
    }

    @Override public Identifier getTextureLocation(IceSquadProjectileEntity entity) { return TEXTURE; }
}
