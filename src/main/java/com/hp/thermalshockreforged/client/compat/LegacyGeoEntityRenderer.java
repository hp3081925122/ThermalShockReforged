package com.hp.thermalshockreforged.client.compat;
import net.minecraft.client.renderer.rendertype.RenderTypes;

import com.geckolib.animatable.GeoAnimatable;
import com.geckolib.cache.model.BakedGeoModel;
import com.geckolib.renderer.GeoEntityRenderer;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.Entity;

public class LegacyGeoEntityRenderer<T extends Entity & GeoAnimatable> extends GeoEntityRenderer<T, EntityRenderState> {
    public LegacyGeoEntityRenderer(EntityRendererProvider.Context context, LegacyGeoModel<T> model) { super(context, model); }
    public RenderType getRenderType(T entity, Identifier texture, MultiBufferSource buffers, float partialTick) { return RenderTypes.entityTranslucent(texture); }
    public void addRenderLayer(Object layer) { }
    public void reRender(BakedGeoModel model, PoseStack pose, MultiBufferSource buffers, T entity, RenderType type, VertexConsumer vertices, float partialTick, int light, int overlay, int color) { }
    public void preRender(PoseStack pose, T entity, BakedGeoModel model, MultiBufferSource buffers, VertexConsumer buffer, boolean isReRender, float partialTick, int light, int overlay, int color) { }
}
