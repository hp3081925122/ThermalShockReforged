package com.hp.thermalshockreforged.client.weapons;

import com.hp.thermalshockreforged.ThermalShockReforged;
import com.hp.thermalshockreforged.content.weapons.*;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.model.geom.*;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.entity.*;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;

public final class FirearmBulletRenderer extends EntityRenderer<FirearmBulletEntity> {
    private static final ModelLayerLocation LAYER = new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, "model_small_laser_beam"), "main");
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, "textures/entities/gruntglow.png");
    private final ModelPart group;
    public FirearmBulletRenderer(EntityRendererProvider.Context context) { super(context); group = context.bakeLayer(LAYER).getChild("group"); }
    public static void layers(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(LAYER, () -> {
            var mesh = new MeshDefinition();
            mesh.getRoot().addOrReplaceChild("group", CubeListBuilder.create().texOffs(0, 0).addBox(-3, -3, -16, 4, 4, 48, new CubeDeformation(0)), PartPose.offset(1, 16, -8));
            return LayerDefinition.create(mesh, 128, 128);
        });
    }
    public static void register(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(WeaponContent.FLINTSTEEL_RIFLE_BULLET.get(), FirearmBulletRenderer::new);
        event.registerEntityRenderer(WeaponContent.HAILFALL_BULLET.get(), FirearmBulletRenderer::new);
        event.registerEntityRenderer(WeaponContent.RIFLE_CYCLE_SOUND.get(), NoopRenderer::new);
        event.registerEntityRenderer(WeaponContent.SPELEOTHEM_SOUND.get(), NoopRenderer::new);
    }
    @Override public void render(FirearmBulletEntity entity, float yaw, float partialTick, PoseStack pose, MultiBufferSource buffers, int light) {
        pose.pushPose();
        pose.mulPose(Axis.YP.rotationDegrees(Mth.lerp(partialTick, entity.yRotO, entity.getYRot()) - 90));
        pose.mulPose(Axis.ZP.rotationDegrees(90 + Mth.lerp(partialTick, entity.xRotO, entity.getXRot())));
        group.render(pose, buffers.getBuffer(RenderType.entityCutout(TEXTURE)), light, OverlayTexture.NO_OVERLAY, -1);
        pose.popPose();
        super.render(entity, yaw, partialTick, pose, buffers, light);
    }
    @Override public ResourceLocation getTextureLocation(FirearmBulletEntity entity) { return TEXTURE; }
}
