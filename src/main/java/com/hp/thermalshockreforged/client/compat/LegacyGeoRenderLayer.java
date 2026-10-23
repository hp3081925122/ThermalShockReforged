package com.hp.thermalshockreforged.client.compat;

import com.geckolib.animatable.GeoAnimatable;
import com.geckolib.cache.model.BakedGeoModel;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.rendertype.RenderType;

public abstract class LegacyGeoRenderLayer<T extends net.minecraft.world.entity.Entity & GeoAnimatable> {
    private final LegacyGeoEntityRenderer<T> renderer;
    protected LegacyGeoRenderLayer(LegacyGeoEntityRenderer<T> renderer) { this.renderer = renderer; }
    protected LegacyGeoEntityRenderer<T> getRenderer() { return renderer; }
    public abstract void render(PoseStack pose, T entity, BakedGeoModel model, RenderType type, MultiBufferSource buffers, VertexConsumer vertices, float partialTick, int light, int overlay);
}
