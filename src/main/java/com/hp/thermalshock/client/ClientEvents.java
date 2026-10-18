package com.hp.thermalshock.client;

import com.hp.thermalshock.ThermalShock;
import com.hp.thermalshock.entity.ModEntities;
import com.hp.thermalshock.entity.ZapWarningEntity;
import com.hp.thermalshock.client.fauna.FaunaRenderers;
import com.hp.thermalshock.client.status.StatusClient;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;

@EventBusSubscriber(modid = ThermalShock.MOD_ID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public final class ClientEvents {
    @SubscribeEvent
    public static void renderers(EntityRenderersEvent.RegisterRenderers event) {
        FaunaRenderers.register(event);
        event.registerEntityRenderer(ModEntities.PONDLER.get(), PondlerRenderer::new);
        event.registerEntityRenderer(ModEntities.PROBOSKER.get(), ProboskerRenderer::new);
        event.registerEntityRenderer(ModEntities.SCARLET_RAY.get(), ScarletRayRenderer::new);
        event.registerEntityRenderer(ModEntities.CRYSTAL_KOI.get(), CrystalKoiRenderer::new);
        event.registerEntityRenderer(ModEntities.FORECAST.get(), ForecastRenderer::new);
        event.registerEntityRenderer(ModEntities.ZAP.get(), InvisibleWarningRenderer::new);
    }

    @SubscribeEvent
    public static void extensions(RegisterClientExtensionsEvent event) {
        StatusClient.register(event);
        FaunaRenderers.extensions(event);
    }

    @SubscribeEvent
    public static void particles(RegisterParticleProvidersEvent event) {
        FaunaRenderers.particles(event);
    }

    private static final class InvisibleWarningRenderer extends EntityRenderer<ZapWarningEntity> {
        private InvisibleWarningRenderer(EntityRendererProvider.Context context) {
            super(context);
        }

        @Override
        public void render(ZapWarningEntity entity, float yaw, float partialTick, PoseStack pose, MultiBufferSource buffers, int light) {
        }

        @Override
        public ResourceLocation getTextureLocation(ZapWarningEntity entity) {
            return ResourceLocation.withDefaultNamespace("textures/misc/white.png");
        }
    }
}
