package com.hp.thermalshockreforged.client;

import com.hp.thermalshockreforged.world.thermite.ThermiteDetonationEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.*;
import net.minecraft.resources.ResourceLocation;

final class ThermiteDetonationRenderer extends EntityRenderer<ThermiteDetonationEntity> {
    ThermiteDetonationRenderer(EntityRendererProvider.Context context) { super(context); }
    @Override public void render(ThermiteDetonationEntity entity, float yaw, float partialTick, PoseStack pose, MultiBufferSource buffers, int light) { }
    @Override public ResourceLocation getTextureLocation(ThermiteDetonationEntity entity) { return ResourceLocation.withDefaultNamespace("textures/misc/white.png"); }
}
