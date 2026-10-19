package com.hp.thermalshockreforged.client.squall;

import com.hp.thermalshockreforged.entity.squall.SquallContent;
import com.hp.thermalshockreforged.entity.squall.SquallEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.resources.ResourceLocation;

public final class SquallRenderer extends HumanoidMobRenderer<SquallEntity, HumanoidModel<SquallEntity>> {
    public SquallRenderer(EntityRendererProvider.Context context) {
        super(context, new HumanoidModel<>(context.bakeLayer(ModelLayers.PLAYER)), .5F);
        addLayer(new HumanoidArmorLayer<>(this, new HumanoidModel<>(context.bakeLayer(ModelLayers.PLAYER_INNER_ARMOR)), new HumanoidModel<>(context.bakeLayer(ModelLayers.PLAYER_OUTER_ARMOR)), context.getModelManager()));
    }
    @Override protected void scale(SquallEntity entity, PoseStack pose, float partialTick) { pose.scale(.01F, .01F, .01F); }
    @Override public ResourceLocation getTextureLocation(SquallEntity entity) { return SquallContent.id("textures/entities/zerkglow.png"); }
    @Override protected boolean isBodyVisible(SquallEntity entity) { return false; }
}
