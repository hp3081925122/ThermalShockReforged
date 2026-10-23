package com.hp.thermalshockreforged.client.compat;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.Entity;

public abstract class LegacyEntityRenderer<T extends Entity> extends EntityRenderer<T, EntityRenderState> {
    protected LegacyEntityRenderer(EntityRendererProvider.Context context) { super(context); }
    @Override public EntityRenderState createRenderState() { return new EntityRenderState(); }
    public void render(T entity, float yaw, float partialTick, PoseStack pose, MultiBufferSource buffers, int light) { }
    public Identifier getTextureLocation(T entity) { return Identifier.withDefaultNamespace("textures/misc/white.png"); }
}
