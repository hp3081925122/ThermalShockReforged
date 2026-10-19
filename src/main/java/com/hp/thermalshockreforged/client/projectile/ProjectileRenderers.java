package com.hp.thermalshockreforged.client.projectile;

import com.hp.thermalshockreforged.entity.projectile.ProjectileContent;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;

public final class ProjectileRenderers {
    private ProjectileRenderers() { }
    public static void register(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(ProjectileContent.MAGNETIC_BOMB.get(), MagneticBombRenderer::new);
        event.registerEntityRenderer(ProjectileContent.EPSILON_BOMB.get(), MagneticBombRenderer::new);
        event.registerEntityRenderer(ProjectileContent.MAGNETIC_DETONATION.get(), net.minecraft.client.renderer.entity.NoopRenderer::new);
        event.registerEntityRenderer(ProjectileContent.VOLCANO_SPARK_TASK.get(), net.minecraft.client.renderer.entity.NoopRenderer::new);
        event.registerEntityRenderer(ProjectileContent.BLOOD_BULLET.get(), ThrownItemRenderer::new);
        event.registerEntityRenderer(ProjectileContent.FLARE_BULLET.get(), ThrownItemRenderer::new);
        event.registerEntityRenderer(ProjectileContent.GRENADE_BULLET.get(), ThrownItemRenderer::new);
        event.registerEntityRenderer(ProjectileContent.VOLCANO_BULLET.get(), SmallBeamRenderer::new);
        event.registerEntityRenderer(ProjectileContent.GHOSTGLASS_SWORD_BULLET.get(), ThrownItemRenderer::new);
        event.registerEntityRenderer(ProjectileContent.MAGNET_CROSSBOW_BULLET.get(), ThrownItemRenderer::new);
        event.registerEntityRenderer(ProjectileContent.THERMOBLASTER_BULLET.get(), ThrownItemRenderer::new);
        event.registerEntityRenderer(ProjectileContent.THERMOBLASTER_SHOTGUN_BULLET.get(), ThrownItemRenderer::new);
        event.registerEntityRenderer(ProjectileContent.NET_BULLET.get(), NetBulletRenderer::new);
        event.registerEntityRenderer(ProjectileContent.LITEPRISM_BEAM.get(), SmallBeamRenderer::new);
        event.registerEntityRenderer(ProjectileContent.THISTLE_BEAM.get(), SmallBeamRenderer::new);
    }
}
