package com.hp.thermalshockreforged.client.compat;

import com.geckolib.animatable.GeoAnimatable;
import com.geckolib.renderer.GeoBlockRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.world.level.block.entity.BlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.resources.Identifier;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class LegacyGeoBlockRenderer<T extends BlockEntity & GeoAnimatable> extends GeoBlockRenderer<T, BlockEntityRenderState> {
    public LegacyGeoBlockRenderer(BlockEntityRendererProvider.Context context, LegacyGeoModel<T> model) { super(context, model); }
    public LegacyGeoBlockRenderer(LegacyGeoModel<T> model) { super(null, model); }
    public void render(T entity, float partialTick, PoseStack pose, MultiBufferSource buffers, int light, int overlay) { }
    public boolean shouldRender(T entity, Vec3 camera) { return true; }
    public boolean shouldRenderOffScreen(T entity) { return true; }
    public AABB getRenderBoundingBox(T entity) { return new AABB(0, 0, 0, 1, 1, 1); }
    public RenderType getRenderType(T entity, Identifier texture, MultiBufferSource buffers, float partialTick) { return null; }
}
