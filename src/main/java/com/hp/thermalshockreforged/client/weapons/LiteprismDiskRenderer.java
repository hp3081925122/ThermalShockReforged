package com.hp.thermalshockreforged.client.weapons;
import com.hp.thermalshockreforged.client.compat.LegacyEntityRenderer;
import net.minecraft.client.renderer.rendertype.RenderType;

import com.hp.thermalshockreforged.ThermalShockReforged;
import com.hp.thermalshockreforged.entity.projectile.LiteprismDiskProjectileEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.model.geom.*;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.entity.*;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;

public final class LiteprismDiskRenderer extends LegacyEntityRenderer<LiteprismDiskProjectileEntity> {
    private static final ModelLayerLocation LAYER = new ModelLayerLocation(Identifier.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, "modellitediskproj"), "main");
    private static final Identifier TEXTURE = Identifier.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, "textures/entities/liteprism_disk_proj.png");
    private final ModelPart bone;
    public LiteprismDiskRenderer(EntityRendererProvider.Context context) { super(context); bone = context.bakeLayer(LAYER).getChild("bone"); }
    public static void layers(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(LAYER, () -> {
            var mesh = new MeshDefinition();
            var bone = mesh.getRoot().addOrReplaceChild("bone", CubeListBuilder.create().texOffs(0, 24).addBox(-3, -.5F, 6, 6, 1, 2).texOffs(18, 8).addBox(-8, -.5F, -3, 2, 1, 6), PartPose.offsetAndRotation(0, 23.5F, 0, 0, 0, 1.5708F));
            bone.addOrReplaceChild("cube_r1", CubeListBuilder.create().texOffs(0, 8).mirror().addBox(5.75F, -1, -3.5F, 2, 1, 7).mirror(false).texOffs(0, 0).addBox(-7.75F, -1, -3.5F, 2, 1, 7), PartPose.offsetAndRotation(0, .5F, 0, 0, .7854F, 0));
            bone.addOrReplaceChild("cube_r2", CubeListBuilder.create().texOffs(0, 0).mirror().addBox(5.75F, -1, -3.5F, 2, 1, 7).mirror(false).texOffs(0, 8).addBox(-7.75F, -1, -3.5F, 2, 1, 7), PartPose.offsetAndRotation(0, .5F, 0, 0, -.7854F, 0));
            bone.addOrReplaceChild("cube_r3", CubeListBuilder.create().texOffs(18, 8).addBox(-8, -1, -3, 2, 1, 6).texOffs(0, 24).addBox(-3, -1, 6, 6, 1, 2), PartPose.offsetAndRotation(0, .5F, 0, 0, 3.1416F, 0));
            return LayerDefinition.create(mesh, 48, 48);
        });
    }
    @Override public void render(LiteprismDiskProjectileEntity entity, float yaw, float partialTick, PoseStack pose, MultiBufferSource buffers, int light) {
        pose.pushPose();
        pose.mulPose(Axis.YP.rotationDegrees(Mth.lerp(partialTick, entity.yRotO, entity.getYRot()) - 90));
        pose.mulPose(Axis.ZP.rotationDegrees(90 + Mth.lerp(partialTick, entity.xRotO, entity.getXRot())));
        bone.render(pose, buffers.getBuffer(RenderType.entityCutout(TEXTURE)), light, OverlayTexture.NO_OVERLAY, -1);
        pose.popPose();
        super.render(entity, yaw, partialTick, pose, buffers, light);
    }
    @Override public Identifier getTextureLocation(LiteprismDiskProjectileEntity entity) { return TEXTURE; }
}
