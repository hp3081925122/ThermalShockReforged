package com.hp.thermalshockreforged.client.gaze;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.rendertype.RenderType;

import com.hp.thermalshockreforged.ThermalShockReforged;
import com.hp.thermalshockreforged.entity.base.AnimatedMonsterEntity;
import com.hp.thermalshockreforged.entity.gaze.*;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.entity.*;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.neoforged.neoforge.client.event.*;
import com.geckolib.animation.state.AnimationTest;
import com.hp.thermalshockreforged.client.compat.LegacyGeoModel;
import com.hp.thermalshockreforged.client.compat.LegacyGeoEntityRenderer;

public final class GazeRenderers {
    private GazeRenderers() { }
    public static void register(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(GazeContent.LANTERNBEARER.get(), context -> new Renderer(context, "lanternbearer", .5F, true));
        event.registerEntityRenderer(GazeContent.LENS.get(), context -> new Renderer(context, "lens", .5F, false));
        event.registerEntityRenderer(GazeContent.SPIRIT.get(), context -> new Renderer(context, "spirit", .3F, false));
        event.registerEntityRenderer(GazeContent.LANTERN_DEATH.get(), NoopRenderer::new);
    }
    public static void particles(RegisterParticleProvidersEvent event) {
        event.registerSpriteSet(GazeContent.LENS_VISAGE_1.get(), LensVisageParticle.Provider::new);
        event.registerSpriteSet(GazeContent.LENS_VISAGE_2.get(), LensVisageParticle.Provider::new);
    }
    private static final class Model extends LegacyGeoModel<AnimatedMonsterEntity> {
        private final Identifier model;
        private final Identifier animation;
        private Model(String name) {
            model = Identifier.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, "geo/" + name + ".geo.json");
            animation = Identifier.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, "animations/" + name + ".animation.json");
        }
        @Override public Identifier getModelResource(AnimatedMonsterEntity entity) { return model; }
        @Override public Identifier getAnimationResource(AnimatedMonsterEntity entity) { return animation; }
        @Override public Identifier getTextureResource(AnimatedMonsterEntity entity) { return Identifier.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, "textures/entities/" + entity.texture() + ".png"); }
        @Override public void setCustomAnimations(AnimatedMonsterEntity entity, long instance, AnimationTest<AnimatedMonsterEntity> state) {
            super.setCustomAnimations(entity, instance, state);
            if (!(entity instanceof SpiritEntity)) return;
            var head = getAnimationProcessor().getBone("head");
            var data = com.hp.thermalshockreforged.compat.LegacyAnimationData.from(state);
            if (head != null && data != null) { head.setRotX(data.headPitch() * Mth.DEG_TO_RAD); head.setRotY(data.netHeadYaw() * Mth.DEG_TO_RAD); }
        }
    }
    private static final class Renderer extends LegacyGeoEntityRenderer<AnimatedMonsterEntity> {
        private final boolean fixedDeath;
        private Renderer(EntityRendererProvider.Context context, String name, float shadow, boolean fixedDeath) { super(context, new Model(name)); shadowRadius = shadow; this.fixedDeath = fixedDeath; }
        @Override public RenderType getRenderType(AnimatedMonsterEntity entity, Identifier texture, MultiBufferSource buffers, float partialTick) { return RenderTypes.entityTranslucent(texture); }
        protected float getDeathMaxRotation(com.geckolib.renderer.base.GeoRenderState state) { return fixedDeath ? 0 : super.getDeathMaxRotation(state); }
    }
}
