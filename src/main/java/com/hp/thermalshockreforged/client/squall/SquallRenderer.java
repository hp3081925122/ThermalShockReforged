package com.hp.thermalshockreforged.client.squall;

import com.hp.thermalshockreforged.entity.squall.SquallContent;
import com.hp.thermalshockreforged.entity.squall.SquallEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;
import net.minecraft.util.LightCoordsUtil;
import net.minecraft.util.Mth;

public final class SquallRenderer extends EntityRenderer<SquallEntity, SquallRenderer.State> {
    private static final Identifier TEXTURE = SquallContent.id("textures/particle/squall_visage.png");

    public SquallRenderer(EntityRendererProvider.Context context) {
        super(context);
        shadowRadius = 0;
    }

    @Override public State createRenderState() { return new State(); }

    @Override public void extractRenderState(SquallEntity entity, State state, float partialTick) {
        super.extractRenderState(entity, state, partialTick);
        state.alive = entity.isAlive();
        state.time = entity.tickCount + partialTick;
        state.lit = entity.lit();
        state.enraged = entity.enraged();
        state.casting = entity.cast() != SquallEntity.NONE;
    }

    @Override public void submit(State state, PoseStack pose, SubmitNodeCollector collector, CameraRenderState camera) {
        super.submit(state, pose, collector, camera);
        if (!state.alive) return;
        float time = state.time;
        float alpha = 1;
        if (state.lit) alpha = .45F + .25F * Mth.sin(time * 1.3F);
        float size = 2;
        if (state.enraged) size = 2.3F;
        float shake = 0;
        if (state.casting) {
            size *= 1.08F + .04F * Mth.sin(time * 1.5F);
            shake = Mth.sin(time * 7) * .04F;
        }
        float finalAlpha = alpha;
        pose.pushPose();
        pose.translate(shake, 1.4 + Mth.sin(time * .08F) * .08F, 0);
        pose.mulPose(camera.orientation);
        pose.scale(size, size, size);
        collector.submitCustomGeometry(pose, RenderTypes.entityTranslucent(TEXTURE), (last, consumer) -> {
            vertex(consumer, last, -1, -1, 0, 1, finalAlpha);
            vertex(consumer, last, 1, -1, 1, 1, finalAlpha);
            vertex(consumer, last, 1, 1, 1, 0, finalAlpha);
            vertex(consumer, last, -1, 1, 0, 0, finalAlpha);
        });
        pose.popPose();
    }

    private static void vertex(VertexConsumer consumer, PoseStack.Pose pose, float x, float y, float u, float v, float alpha) {
        consumer.addVertex(pose, x, y, 0).setColor(1, 1, 1, alpha).setUv(u, v).setOverlay(OverlayTexture.NO_OVERLAY).setLight(LightCoordsUtil.FULL_BRIGHT).setNormal(pose, 0, 1, 0);
    }

    public static final class State extends EntityRenderState {
        boolean alive;
        float time;
        boolean lit;
        boolean enraged;
        boolean casting;
    }
}
