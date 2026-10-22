package com.hp.thermalshockreforged.client.squall;

import com.hp.thermalshockreforged.entity.squall.SquallContent;
import com.hp.thermalshockreforged.entity.squall.SquallEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public final class SquallRenderer extends EntityRenderer<SquallEntity> {
    private static final ResourceLocation TEXTURE = SquallContent.id("textures/particle/squall_visage.png");

    public SquallRenderer(EntityRendererProvider.Context context) {
        super(context);
        shadowRadius = 0;
    }

    @Override public void render(SquallEntity entity, float yaw, float partialTick, PoseStack pose, MultiBufferSource buffers, int light) {
        super.render(entity, yaw, partialTick, pose, buffers, light);
        if (!entity.isAlive()) return;
        float time = entity.tickCount + partialTick;
        float alpha = 1;
        if (entity.lit()) alpha = .45F + .25F * Mth.sin(time * 1.3F);
        float size = 2;
        if (entity.enraged()) size = 2.3F;
        float shake = 0;
        if (entity.cast() != SquallEntity.NONE) {
            size *= 1.08F + .04F * Mth.sin(time * 1.5F);
            shake = Mth.sin(time * 7) * .04F;
        }
        pose.pushPose();
        pose.translate(shake, 1.4 + Mth.sin(time * .08F) * .08F, 0);
        pose.mulPose(entityRenderDispatcher.cameraOrientation());
        pose.scale(size, size, size);
        VertexConsumer consumer = buffers.getBuffer(RenderType.entityTranslucent(TEXTURE));
        PoseStack.Pose last = pose.last();
        vertex(consumer, last, -1, -1, 0, 1, alpha);
        vertex(consumer, last, 1, -1, 1, 1, alpha);
        vertex(consumer, last, 1, 1, 1, 0, alpha);
        vertex(consumer, last, -1, 1, 0, 0, alpha);
        pose.popPose();
    }

    private static void vertex(VertexConsumer consumer, PoseStack.Pose pose, float x, float y, float u, float v, float alpha) {
        consumer.addVertex(pose, x, y, 0).setColor(1, 1, 1, alpha).setUv(u, v).setOverlay(OverlayTexture.NO_OVERLAY).setLight(LightTexture.FULL_BRIGHT).setNormal(pose, 0, 1, 0);
    }

    @Override public ResourceLocation getTextureLocation(SquallEntity entity) { return TEXTURE; }
}
