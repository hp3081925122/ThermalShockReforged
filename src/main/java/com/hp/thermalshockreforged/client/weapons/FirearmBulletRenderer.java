package com.hp.thermalshockreforged.client.weapons;
import com.hp.thermalshockreforged.client.PartProjectileRenderer;

import com.hp.thermalshockreforged.ThermalShockReforged;
import com.hp.thermalshockreforged.content.weapons.*;
import net.minecraft.client.model.geom.*;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.client.renderer.entity.*;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;

public final class FirearmBulletRenderer extends PartProjectileRenderer<FirearmBulletEntity> {
    public static final ModelLayerLocation LAYER = new ModelLayerLocation(Identifier.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, "model_small_laser_beam"), "main");
    private static final Identifier TEXTURE = Identifier.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, "textures/entities/gruntglow.png");
    public FirearmBulletRenderer(EntityRendererProvider.Context context) { super(context, context.bakeLayer(LAYER).getChild("group"), TEXTURE); }
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
}
