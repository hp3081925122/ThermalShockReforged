package com.hp.thermalshockreforged.client;

import com.hp.thermalshockreforged.ThermalShockReforged;
import com.hp.thermalshockreforged.client.decor.geo.GeoDecorRenderers;
import com.hp.thermalshockreforged.client.hunters.HuntersRenderers;
import com.hp.thermalshockreforged.client.decor.nativeblocks.DeskPhotoScreen;
import com.hp.thermalshockreforged.content.decor.nativeblocks.NativeDecorContent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import com.hp.thermalshockreforged.entity.ModEntities;
import com.hp.thermalshockreforged.entity.ZapWarningEntity;
import com.hp.thermalshockreforged.client.fauna.FaunaRenderers;
import com.hp.thermalshockreforged.client.status.StatusClient;
import com.hp.thermalshockreforged.client.armor.ArmorClient;
import com.hp.thermalshockreforged.client.infestation.InfestationRenderers;
import com.hp.thermalshockreforged.client.snobol.SnobolRenderers;
import com.hp.thermalshockreforged.client.ecosystem.EcosystemRenderers;
import com.hp.thermalshockreforged.client.ambient.AmbientRenderers;
import com.hp.thermalshockreforged.client.lights.LightsRenderers;
import com.hp.thermalshockreforged.client.spore.SporeRenderers;
import com.hp.thermalshockreforged.client.sootle.SootleRenderers;
import com.hp.thermalshockreforged.client.gardenling.GardenlingRenderers;
import com.hp.thermalshockreforged.client.farming.FarmingRenderers;
import com.hp.thermalshockreforged.client.mangler.ManglerRenderers;
import com.hp.thermalshockreforged.client.weapons.WeaponClient;
import com.hp.thermalshockreforged.client.status.aura.AuraClient;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;

@EventBusSubscriber(modid = ThermalShockReforged.MOD_ID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public final class ClientEvents {
    @SubscribeEvent
    public static void renderers(EntityRenderersEvent.RegisterRenderers event) {
        FaunaRenderers.register(event);
        InfestationRenderers.register(event);
        SnobolRenderers.register(event);
        EcosystemRenderers.register(event);
        AmbientRenderers.register(event);
        LightsRenderers.register(event);
        SporeRenderers.register(event);
        SootleRenderers.register(event);
        GardenlingRenderers.register(event);
        ManglerRenderers.register(event);
        HuntersRenderers.register(event);
        GeoDecorRenderers.register(event);
        FarmingRenderers.register(event);
        event.registerEntityRenderer(ModEntities.PONDLER.get(), PondlerRenderer::new);
        event.registerEntityRenderer(ModEntities.PROBOSKER.get(), ProboskerRenderer::new);
        event.registerEntityRenderer(ModEntities.SCARLET_RAY.get(), ScarletRayRenderer::new);
        event.registerEntityRenderer(ModEntities.CRYSTAL_KOI.get(), CrystalKoiRenderer::new);
        event.registerEntityRenderer(ModEntities.FORECAST.get(), ForecastRenderer::new);
        event.registerEntityRenderer(ModEntities.ZAP.get(), InvisibleWarningRenderer::new);
    }

    @SubscribeEvent
    public static void screens(RegisterMenuScreensEvent event) {
        event.register(NativeDecorContent.DESK_PHOTO_SCREEN.get(), DeskPhotoScreen::new);
    }

    @SubscribeEvent
    public static void extensions(RegisterClientExtensionsEvent event) {
        StatusClient.register(event);
        ArmorClient.extensions(event);
        LightsRenderers.extensions(event);
        SporeRenderers.extensions(event);
        WeaponClient.extensions(event);
        AuraClient.extensions(event);
        FaunaRenderers.extensions(event);
        FarmingRenderers.extensions(event);
        GeoDecorRenderers.extensions(event);
    }

    @SubscribeEvent
    public static void particles(RegisterParticleProvidersEvent event) {
        FaunaRenderers.particles(event);
        StatusClient.registerParticles(event);
        SnobolRenderers.particles(event);
        AmbientRenderers.particles(event);
        AuraClient.particles(event);
        FarmingRenderers.particles(event);
    }

    @SubscribeEvent
    public static void setup(FMLClientSetupEvent event) { event.enqueueWork(FarmingRenderers::layers); }

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
