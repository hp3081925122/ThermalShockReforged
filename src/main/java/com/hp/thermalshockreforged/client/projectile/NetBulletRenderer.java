package com.hp.thermalshockreforged.client.projectile;

import com.hp.thermalshockreforged.ThermalShockReforged;
import com.hp.thermalshockreforged.entity.projectile.NetBulletEntity;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import com.hp.thermalshockreforged.client.PartProjectileRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;

public final class NetBulletRenderer extends PartProjectileRenderer<NetBulletEntity> {
    public static final ModelLayerLocation LAYER = new ModelLayerLocation(Identifier.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, "model_net_proj"), "main");
    private static final Identifier TEXTURE = Identifier.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, "textures/entities/net_proj.png");

    public NetBulletRenderer(EntityRendererProvider.Context context) { super(context, context.bakeLayer(LAYER).getChild("bone"), TEXTURE); }
    public static void layers(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(LAYER, () -> {
            var mesh = new MeshDefinition();
            var bone = mesh.getRoot().addOrReplaceChild("bone", CubeListBuilder.create(), PartPose.offsetAndRotation(0, 23.5F, 0, 0, 0, -1.5708F));
            bone.addOrReplaceChild("cube_r1", CubeListBuilder.create().texOffs(0, 0).addBox(0, -17, -9, 0, 32, 32, new CubeDeformation(0)), PartPose.offsetAndRotation(0, .5F, 7, 0, 3.1416F, 0));
            return LayerDefinition.create(mesh, 64, 64);
        });
    }
}
