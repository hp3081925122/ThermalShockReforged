package com.hp.thermalshockreforged.client;
import com.hp.thermalshockreforged.client.compat.LegacyEntityRenderer;

import com.hp.thermalshockreforged.world.thermite.ThermiteDetonationEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.*;
import net.minecraft.resources.Identifier;

final class ThermiteDetonationRenderer extends LegacyEntityRenderer<ThermiteDetonationEntity> {
    ThermiteDetonationRenderer(EntityRendererProvider.Context context) { super(context); }
    @Override public void render(ThermiteDetonationEntity entity, float yaw, float partialTick, PoseStack pose, MultiBufferSource buffers, int light) { }
    @Override public Identifier getTextureLocation(ThermiteDetonationEntity entity) { return Identifier.withDefaultNamespace("textures/misc/white.png"); }
}
