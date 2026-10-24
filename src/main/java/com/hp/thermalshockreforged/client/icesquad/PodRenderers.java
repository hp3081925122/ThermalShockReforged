package com.hp.thermalshockreforged.client.icesquad;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.rendertype.RenderType;

import com.hp.thermalshockreforged.ThermalShockReforged;
import com.hp.thermalshockreforged.entity.icesquad.*;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.entity.*;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;
import com.hp.thermalshockreforged.client.compat.LegacyGeoModel;
import com.hp.thermalshockreforged.client.compat.LegacyGeoEntityRenderer;

public final class PodRenderers {
    private PodRenderers() { }
    public static void register(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(IceSquadContent.DROP_POD.get(), PodRenderer::new);
        event.registerEntityRenderer(IceSquadContent.DEPLOYMENT_TASK.get(), com.hp.thermalshockreforged.client.InvisibleRenderer::new);
    }
    public static void particles(RegisterParticleProvidersEvent event) { event.registerSpriteSet(IceSquadContent.FOG_CLOUD.get(), FogCloudParticle.Provider::new); }
    private static Identifier resource(String path) { return Identifier.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, path); }
    private static final class Model extends LegacyGeoModel<BlizzardDropPodEntity> {
        @Override public Identifier getModelResource(BlizzardDropPodEntity pod) { return resource("geo/drop_pod.geo.json"); }
        @Override public Identifier getAnimationResource(BlizzardDropPodEntity pod) { return resource("animations/drop_pod.animation.json"); }
        @Override public Identifier getTextureResource(BlizzardDropPodEntity pod) { return resource("textures/entities/" + pod.texture() + ".png"); }
    }
    private static final class PodRenderer extends LegacyGeoEntityRenderer<BlizzardDropPodEntity> {
        private PodRenderer(EntityRendererProvider.Context context) { super(context, new Model()); shadowRadius = 0.5F; withScale(1); }
        @Override public RenderType getRenderType(BlizzardDropPodEntity pod, Identifier texture, MultiBufferSource buffers, float partialTick) { return RenderTypes.entityTranslucent(texture); }
    }
}
