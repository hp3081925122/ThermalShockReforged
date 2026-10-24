package com.hp.thermalshockreforged.client.orb;

import com.hp.thermalshockreforged.ThermalShockReforged;
import com.hp.thermalshockreforged.entity.orb.*;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.EyesLayer;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;

public final class OrbRenderers {
    private static final ModelLayerLocation LAYER = new ModelLayerLocation(Identifier.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, "model_tale_orb"), "main");
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
    private static final class TaleOrbModel extends EntityModel<LivingEntityRenderState> {
        private final ModelPart head;
        private TaleOrbModel(ModelPart root) {
            super(root);
            head = root.getChild("head");
        }
        @Override public void setupAnim(LivingEntityRenderState state) {
            super.setupAnim(state);
            head.xRot = state.ageInTicks / 20;
        }
    }
    private static final class Renderer<T extends OrbEntityBase> extends MobRenderer<T, LivingEntityRenderState, TaleOrbModel> {
        private final Identifier texture;
        private Renderer(EntityRendererProvider.Context context, String texture) {
            super(context, new TaleOrbModel(context.bakeLayer(LAYER)), 0);
            this.texture = Identifier.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, "textures/entities/" + texture + ".png");
            addLayer(new Eyes(this, this.texture));
        }
        @Override public LivingEntityRenderState createRenderState() { return new LivingEntityRenderState(); }
        @Override public Identifier getTextureLocation(LivingEntityRenderState state) { return texture; }
        @Override protected boolean isBodyVisible(LivingEntityRenderState state) { return false; }
    }
    private static final class Eyes extends EyesLayer<LivingEntityRenderState, TaleOrbModel> {
        private final RenderType renderType;
        private Eyes(RenderLayerParent<LivingEntityRenderState, TaleOrbModel> parent, Identifier texture) {
            super(parent);
            renderType = RenderTypes.eyes(texture);
        }
        @Override public RenderType renderType() { return renderType; }
    }
}
