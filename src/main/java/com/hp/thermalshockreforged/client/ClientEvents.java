package com.hp.thermalshockreforged.client;

import com.hp.thermalshockreforged.ThermalShockReforged;
import com.hp.thermalshockreforged.client.fluids.FluidClient;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;
import com.hp.thermalshockreforged.client.growth.GrowthRenderers;
import com.hp.thermalshockreforged.client.ice.IceRenderers;
import com.hp.thermalshockreforged.client.icesquad.IceSquadRenderers;
import com.hp.thermalshockreforged.client.icesquad.StriderRenderers;
import com.hp.thermalshockreforged.client.icesquad.BastionRenderers;
import com.hp.thermalshockreforged.client.icesquad.PodRenderers;
import com.hp.thermalshockreforged.client.doors.SpecialDoorRenderers;
import com.hp.thermalshockreforged.client.decor.geo.GeoDecorRenderers;
import com.hp.thermalshockreforged.client.hunters.HuntersRenderers;
import com.hp.thermalshockreforged.client.decor.nativeblocks.DeskPhotoScreen;
import com.hp.thermalshockreforged.content.decor.nativeblocks.NativeDecorContent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import com.hp.thermalshockreforged.entity.ModEntities;
import com.hp.thermalshockreforged.entity.ZapWarningEntity;
import com.hp.thermalshockreforged.client.particle.ZapAttackParticle;
import com.hp.thermalshockreforged.client.particle.TeleportFlashParticle;
import com.hp.thermalshockreforged.client.particle.AaaStationaryParticle;
import com.hp.thermalshockreforged.world.LoqendiaContent;
import net.minecraft.client.particle.ParticleRenderType;
import com.hp.thermalshockreforged.client.cavebloomer.CavebloomerClient;
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
        CavebloomerClient.renderers(event);
        event.registerEntityRenderer(com.hp.thermalshockreforged.content.atlas.AtlasContent.ACTIVATION.get(), net.minecraft.client.renderer.entity.NoopRenderer::new);
        com.hp.thermalshockreforged.client.springslime.SpringslimeRenderers.register(event);
        event.registerEntityRenderer(com.hp.thermalshockreforged.world.thermite.ThermiteContent.DETONATION.get(), ThermiteDetonationRenderer::new);
        event.registerEntityRenderer(com.hp.thermalshockreforged.entity.pots.PotContent.SKULLSCOR.get(), com.hp.thermalshockreforged.client.pots.SkullscorRenderer::new);
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
        IceRenderers.register(event);
        IceSquadRenderers.register(event);
        StriderRenderers.register(event);
        BastionRenderers.register(event);
        PodRenderers.register(event);
        GeoDecorRenderers.register(event);
        SpecialDoorRenderers.register(event);
        FarmingRenderers.register(event);
        GrowthRenderers.register(event);
        event.registerEntityRenderer(ModEntities.PONDLER.get(), PondlerRenderer::new);
        event.registerEntityRenderer(ModEntities.PROBOSKER.get(), ProboskerRenderer::new);
        event.registerEntityRenderer(ModEntities.SCARLET_RAY.get(), ScarletRayRenderer::new);
        event.registerEntityRenderer(ModEntities.CRYSTAL_KOI.get(), CrystalKoiRenderer::new);
        event.registerEntityRenderer(ModEntities.FORECAST.get(), ForecastRenderer::new);
        event.registerEntityRenderer(ModEntities.GALVANITE.get(), GalvaniteRenderer::new);
        event.registerEntityRenderer(ModEntities.ZAP.get(), InvisibleWarningRenderer::new);
    }

    @SubscribeEvent
    public static void dimensionEffects(net.neoforged.neoforge.client.event.RegisterDimensionSpecialEffectsEvent event) {
        event.register(LoqendiaContent.LOQENDIA_ID, new net.minecraft.client.renderer.DimensionSpecialEffects(150, true, net.minecraft.client.renderer.DimensionSpecialEffects.SkyType.NORMAL, false, false) {
            @Override public net.minecraft.world.phys.Vec3 getBrightnessDependentFogColor(net.minecraft.world.phys.Vec3 color, float sunHeight) {
                return color.multiply(sunHeight * 0.94 + 0.06, sunHeight * 0.94 + 0.06, sunHeight * 0.91 + 0.09);
            }
            @Override public boolean isFoggyAt(int x, int y) { return false; }
        });
    }

    @SubscribeEvent
    public static void screens(RegisterMenuScreensEvent event) {
        com.hp.thermalshockreforged.client.dialogue.DialogueScreen.register(event);
        event.register(NativeDecorContent.DESK_PHOTO_SCREEN.get(), DeskPhotoScreen::new);
        event.register(com.hp.thermalshockreforged.content.atlas.AtlasContent.MENU.get(), com.hp.thermalshockreforged.client.atlas.AtlasScreen::new);
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
        GrowthRenderers.extensions(event);
        FluidClient.extensions(event);
        GeoDecorRenderers.extensions(event);
        SpecialDoorRenderers.extensions(event);
    }

    @SubscribeEvent
    public static void particles(RegisterParticleProvidersEvent event) {
        CavebloomerClient.particles(event);
        event.registerSpriteSet(ModEntities.ZAP_PARTICLE.get(), ZapAttackParticle.Provider::new);
        event.registerSpriteSet(ModEntities.TELEPORT_FLASH.get(), TeleportFlashParticle.Provider::new);
        event.registerSpriteSet(LoqendiaContent.GOLD_GLITTER.get(), sprites -> new AaaStationaryParticle.Provider(sprites, "goldglitter", 0, ParticleRenderType.PARTICLE_SHEET_LIT, true, false, 0));
        FaunaRenderers.particles(event);
        StatusClient.registerParticles(event);
        SnobolRenderers.particles(event);
        AmbientRenderers.particles(event);
        AuraClient.particles(event);
        PodRenderers.particles(event);
        FarmingRenderers.particles(event);
    }

    @SubscribeEvent
    public static void itemColors(RegisterColorHandlersEvent.Item event) { FluidClient.colors(event); }

    @SubscribeEvent
    public static void setup(FMLClientSetupEvent event) { event.enqueueWork(() -> { FarmingRenderers.layers(); FluidClient.layers(); }); }

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
