package com.hp.thermalshockreforged.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.Entity;

public class PartProjectileRenderer<T extends Entity> extends EntityRenderer<T, ProjectileRenderState> {
    private final ModelPart part;
    private final Identifier texture;

    public PartProjectileRenderer(EntityRendererProvider.Context context, ModelPart part, Identifier texture) {
        super(context);
        this.part = part;
        this.texture = texture;
    }

    @Override public ProjectileRenderState createRenderState() { return new ProjectileRenderState(); }

    @Override public void extractRenderState(T entity, ProjectileRenderState state, float partialTick) {
        super.extractRenderState(entity, state, partialTick);
        state.rotations(entity, partialTick);
    }

    @Override public void submit(ProjectileRenderState state, PoseStack pose, SubmitNodeCollector collector, CameraRenderState camera) {
        pose.pushPose();
        pose.mulPose(Axis.YP.rotationDegrees(state.yRot - 90));
        pose.mulPose(Axis.ZP.rotationDegrees(90 + state.xRot));
        collector.submitModelPart(part, pose, RenderTypes.entityCutout(texture), state.lightCoords, OverlayTexture.NO_OVERLAY, null, false, false, -1, null, state.outlineColor);
        pose.popPose();
        super.submit(state, pose, collector, camera);
    }
}
