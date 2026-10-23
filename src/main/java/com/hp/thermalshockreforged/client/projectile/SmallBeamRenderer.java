package com.hp.thermalshockreforged.client.projectile;
import com.hp.thermalshockreforged.client.compat.LegacyEntityRenderer;
import net.minecraft.client.renderer.rendertype.RenderType;

import com.hp.thermalshockreforged.ThermalShockReforged;
import com.hp.thermalshockreforged.client.weapons.FirearmBulletRenderer;
import com.hp.thermalshockreforged.entity.projectile.LegacyArrowEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.entity.*;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;

public final class SmallBeamRenderer<T extends LegacyArrowEntity> extends LegacyEntityRenderer<T> {
    private static final Identifier TEXTURE = Identifier.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, "textures/entities/zerkglow.png");
    private final ModelPart group;
    public SmallBeamRenderer(EntityRendererProvider.Context context) { super(context); group = context.bakeLayer(FirearmBulletRenderer.LAYER).getChild("group"); }
    @Override public void render(T entity, float yaw, float partialTick, PoseStack pose, MultiBufferSource buffers, int light) {
        pose.pushPose();
        pose.mulPose(Axis.YP.rotationDegrees(Mth.lerp(partialTick, entity.yRotO, entity.getYRot()) - 90));
        pose.mulPose(Axis.ZP.rotationDegrees(90 + Mth.lerp(partialTick, entity.xRotO, entity.getXRot())));
        group.render(pose, buffers.getBuffer(RenderType.entityCutout(TEXTURE)), light, OverlayTexture.NO_OVERLAY, -1);
        pose.popPose();
        super.render(entity, yaw, partialTick, pose, buffers, light);
    }
    @Override public Identifier getTextureLocation(T entity) { return TEXTURE; }
}
