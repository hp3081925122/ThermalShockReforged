package com.hp.thermalshockreforged.client.icesquad;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.rendertype.RenderType;
import com.hp.thermalshockreforged.client.compat.LegacyGeoEntityRenderer;

import com.hp.thermalshockreforged.ThermalShockReforged;
import com.hp.thermalshockreforged.entity.icesquad.*;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.entity.*;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;
import com.geckolib.animation.state.AnimationTest;
import com.geckolib.cache.model.BakedGeoModel;
import com.hp.thermalshockreforged.client.compat.LegacyGeoModel;
import com.geckolib.renderer.*;
import com.hp.thermalshockreforged.client.compat.LegacyGeoRenderLayer;

public final class BastionRenderers {
    private BastionRenderers() { }
    public static void register(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(IceSquadContent.GUNNER.get(), context -> new Renderer(context, "icidrobastion", "head", 1, 0.5F, null));
        event.registerEntityRenderer(IceSquadContent.BURNER.get(), context -> new Renderer(context, "icidrobastion", "head", 1, 0.5F, null));
    }
    public static void particles(RegisterParticleProvidersEvent event) {
        event.registerSpriteSet(IceSquadContent.LOD_TRAIL.get(), sprites -> new BastionParticles.DriftProvider(sprites, 6, 3, .25F, -.5F, 0, .9F));
        event.registerSpriteSet(IceSquadContent.LOD_SHELL.get(), sprites -> new BastionParticles.DriftProvider(sprites, 20, 0, .25F, 0, 1, 1));
        event.registerSpriteSet(IceSquadContent.LOD_PUFF.get(), sprites -> new BastionParticles.DriftProvider(sprites, 12, 6, .25F, 1.2F, -.05F, .88F));
        event.registerSpriteSet(IceSquadContent.LOD_MARK.get(), sprites -> new BastionParticles.FlatProvider(sprites, 0));
        event.registerSpriteSet(IceSquadContent.LOD_BURST.get(), sprites -> new BastionParticles.BurstProvider(sprites, 12));
        event.registerSpriteSet(IceSquadContent.POZAR_FLAME.get(), sprites -> new BastionParticles.DriftProvider(sprites, 13, 3, .25F, 1.6F, -.06F, .97F));
        event.registerSpriteSet(IceSquadContent.POZAR_EMBER.get(), sprites -> new BastionParticles.DriftProvider(sprites, 14, 8, .125F, 0, .15F, .96F));
        event.registerSpriteSet(IceSquadContent.POZAR_MARK.get(), sprites -> new BastionParticles.FlatProvider(sprites, 0));
        event.registerSpriteSet(IceSquadContent.POZAR_BURST.get(), sprites -> new BastionParticles.BurstProvider(sprites, 10));
        event.registerSpriteSet(IceSquadContent.LOD_CRACK.get(), sprites -> new BastionParticles.FlatProvider(sprites, 0));
        event.registerSpriteSet(IceSquadContent.LOD_SPIKE.get(), sprites -> new BastionParticles.PillarProvider(sprites, 14));
        event.registerSpriteSet(IceSquadContent.LOD_MINE.get(), sprites -> new BastionParticles.FlatProvider(sprites, 2));
        event.registerSpriteSet(IceSquadContent.POZAR_SHELL.get(), sprites -> new BastionParticles.DriftProvider(sprites, 20, 0, .25F, 0, 1, 1));
        event.registerSpriteSet(IceSquadContent.POZAR_RING.get(), sprites -> new BastionParticles.FlatProvider(sprites, 0));
        event.registerSpriteSet(IceSquadContent.POZAR_PILLAR.get(), sprites -> new BastionParticles.PillarProvider(sprites, 16));
        event.registerSpriteSet(IceSquadContent.THERMAL_BURST.get(), sprites -> new BastionParticles.BurstProvider(sprites, 12));
    }
    private static Identifier resource(String path) { return Identifier.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, path); }
    private static final class Model extends LegacyGeoModel<BastionEntity> {
        private final String id;
        private final String bone;
        private Model(String id, String bone) { this.id = id; this.bone = bone; }
        @Override public Identifier getModelResource(BastionEntity entity) { return resource("geo/" + id + ".geo.json"); }
        @Override public Identifier getAnimationResource(BastionEntity entity) { return resource("animations/" + id + ".animation.json"); }
        @Override public Identifier getTextureResource(BastionEntity entity) { return resource("textures/entities/" + entity.texture() + ".png"); }
        @Override public void setCustomAnimations(BastionEntity entity, long instanceId, AnimationTest<BastionEntity> state) {
            super.setCustomAnimations(entity, instanceId, state);
            if (bone == null) return;
            var current = getAnimationProcessor().getBone(bone); var data = com.hp.thermalshockreforged.compat.LegacyAnimationData.from(state);
            if (current != null && data != null) { current.setRotX(data.headPitch() * Mth.DEG_TO_RAD); current.setRotY(data.netHeadYaw() * Mth.DEG_TO_RAD); }
        }
    }
    private static final class Renderer extends LegacyGeoEntityRenderer<BastionEntity> {
        private Renderer(EntityRendererProvider.Context context, String id, String bone, float scale, float shadow, String glow) {
            super(context, new Model(id, bone)); withScale(scale); shadowRadius = shadow; if (glow != null) addRenderLayer(new Glow(this, glow));
        }
        @Override public RenderType getRenderType(BastionEntity entity, Identifier texture, MultiBufferSource buffers, float partialTick) { return RenderTypes.entityTranslucent(texture); }
    }
    private static final class Glow extends LegacyGeoRenderLayer<BastionEntity> {
        private final Identifier texture;
        private Glow(LegacyGeoEntityRenderer<BastionEntity> renderer, String texture) { super(renderer); this.texture = resource("textures/entities/" + texture + ".png"); }
        @Override public void render(PoseStack pose, BastionEntity entity, BakedGeoModel model, RenderType type, MultiBufferSource buffers, VertexConsumer vertices, float partialTick, int light, int overlay) {
            var glow = RenderTypes.eyes(texture);
            getRenderer().reRender(model, pose, buffers, entity, glow, buffers.getBuffer(glow), partialTick, light, OverlayTexture.NO_OVERLAY, -1);
        }
    }
}
