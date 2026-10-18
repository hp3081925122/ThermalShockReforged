package com.hp.thermalshockreforged.client.icesquad;

import com.hp.thermalshockreforged.ThermalShockReforged;
import com.hp.thermalshockreforged.entity.icesquad.*;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.entity.*;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public final class PodRenderers {
    private PodRenderers() { }
    public static void register(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(IceSquadContent.DROP_POD.get(), PodRenderer::new);
        event.registerEntityRenderer(IceSquadContent.DEPLOYMENT_TASK.get(), InvisibleRenderer::new);
    }
    public static void particles(RegisterParticleProvidersEvent event) { event.registerSpriteSet(IceSquadContent.FOG_CLOUD.get(), FogCloudParticle.Provider::new); }
    private static ResourceLocation resource(String path) { return ResourceLocation.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, path); }
    private static final class Model extends GeoModel<BlizzardDropPodEntity> {
        @Override public ResourceLocation getModelResource(BlizzardDropPodEntity pod) { return resource("geo/drop_pod.geo.json"); }
        @Override public ResourceLocation getAnimationResource(BlizzardDropPodEntity pod) { return resource("animations/drop_pod.animation.json"); }
        @Override public ResourceLocation getTextureResource(BlizzardDropPodEntity pod) { return resource("textures/entities/" + pod.texture() + ".png"); }
    }
    private static final class PodRenderer extends GeoEntityRenderer<BlizzardDropPodEntity> {
        private PodRenderer(EntityRendererProvider.Context context) { super(context, new Model()); shadowRadius = 0.5F; withScale(1); }
        @Override public RenderType getRenderType(BlizzardDropPodEntity pod, ResourceLocation texture, MultiBufferSource buffers, float partialTick) { return RenderType.entityTranslucent(texture); }
    }
    private static final class InvisibleRenderer extends EntityRenderer<DeploymentTaskEntity> {
        private InvisibleRenderer(EntityRendererProvider.Context context) { super(context); }
        @Override public void render(DeploymentTaskEntity task, float yaw, float partialTick, PoseStack pose, MultiBufferSource buffers, int light) { }
        @Override public ResourceLocation getTextureLocation(DeploymentTaskEntity task) { return ResourceLocation.withDefaultNamespace("textures/misc/white.png"); }
    }
}
