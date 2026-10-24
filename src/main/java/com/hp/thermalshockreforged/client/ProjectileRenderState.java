package com.hp.thermalshockreforged.client;

import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.world.entity.Entity;

public final class ProjectileRenderState extends EntityRenderState {
    public float xRot;
    public float yRot;

    public void rotations(Entity entity, float partialTick) {
        xRot = entity.getXRot(partialTick);
        yRot = entity.getYRot(partialTick);
    }
}
