package com.hp.thermalshockreforged.client.orb;

import com.hp.thermalshockreforged.ThermalShockReforged;
import com.hp.thermalshockreforged.entity.orb.*;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;

public final class OrbRenderers {
    private static final ModelLayerLocation LAYER = new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, "model_tale_orb"), "main");
    private OrbRenderers() { }
    public static void register(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(OrbContent.STOPPER_ORB.get(), context -> new Renderer<>(context, "stopper_orb_trans"));
        event.registerEntityRenderer(OrbContent.MOVER_ORB.get(), context -> new Renderer<>(context, "mover_orb_trans"));
        event.registerEntityRenderer(OrbContent.WHISTLER_NOTE.get(), net.minecraft.client.renderer.entity.ThrownItemRenderer::new);
    }
    public static void layers(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(LAYER, () -> {
            var mesh = new MeshDefinition();
            mesh.getRoot().addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 0).addBox(-8, -8, -8, 16, 16, 16).texOffs(0, 32).addBox(-6, -6, -6, 12, 12, 12).texOffs(48, 32).addBox(-4, -4, -4, 8, 8, 8), PartPose.offset(0, 16, 0));
            return LayerDefinition.create(mesh, 80, 80);
        });
    }
    private static final class TaleOrbModel<T extends Entity> extends EntityModel<T> {
        private final ModelPart head;
        private TaleOrbModel(ModelPart root) { head = root.getChild("head"); }
        @Override public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) { head.xRot = ageInTicks / 20; }
        @Override public void renderToBuffer(PoseStack pose, VertexConsumer vertices, int light, int overlay, int color) { head.render(pose, vertices, light, overlay, color); }
    }
    private static final class Renderer<T extends OrbEntityBase> extends MobRenderer<T, TaleOrbModel<T>> {
        private final ResourceLocation texture;
        private Renderer(EntityRendererProvider.Context context, String texture) {
            super(context, new TaleOrbModel<>(context.bakeLayer(LAYER)), 0);
            this.texture = ResourceLocation.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, "textures/entities/" + texture + ".png");
            addLayer(new Eyes(this, this.texture));
        }
        @Override public ResourceLocation getTextureLocation(T entity) { return texture; }
        @Override protected boolean isBodyVisible(T entity) { return false; }
    }
    private static final class Eyes<T extends OrbEntityBase> extends RenderLayer<T, TaleOrbModel<T>> {
        private final ResourceLocation texture;
        private Eyes(RenderLayerParent<T, TaleOrbModel<T>> parent, ResourceLocation texture) { super(parent); this.texture = texture; }
        @Override public void render(PoseStack pose, MultiBufferSource buffers, int light, T entity, float limbSwing, float limbSwingAmount, float partialTick, float ageInTicks, float netHeadYaw, float headPitch) {
            VertexConsumer vertices = buffers.getBuffer(RenderType.eyes(texture));
            getParentModel().renderToBuffer(pose, vertices, light, OverlayTexture.NO_OVERLAY, -1);
        }
    }
}
