package com.hp.thermalshockreforged.client.gaze;

import com.hp.thermalshockreforged.ThermalShockReforged;
import com.hp.thermalshockreforged.entity.base.AnimatedMonsterEntity;
import com.hp.thermalshockreforged.entity.gaze.*;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.entity.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.neoforged.neoforge.client.event.*;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

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
    private static final class Model extends GeoModel<AnimatedMonsterEntity> {
        private final ResourceLocation model;
        private final ResourceLocation animation;
        private Model(String name) {
            model = ResourceLocation.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, "geo/" + name + ".geo.json");
            animation = ResourceLocation.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, "animations/" + name + ".animation.json");
        }
        @Override public ResourceLocation getModelResource(AnimatedMonsterEntity entity) { return model; }
        @Override public ResourceLocation getAnimationResource(AnimatedMonsterEntity entity) { return animation; }
        @Override public ResourceLocation getTextureResource(AnimatedMonsterEntity entity) { return ResourceLocation.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, "textures/entities/" + entity.texture() + ".png"); }
        @Override public void setCustomAnimations(AnimatedMonsterEntity entity, long instance, AnimationState<AnimatedMonsterEntity> state) {
            super.setCustomAnimations(entity, instance, state);
            if (!(entity instanceof SpiritEntity)) return;
            var head = getAnimationProcessor().getBone("head");
            var data = state.getData(DataTickets.ENTITY_MODEL_DATA);
            if (head != null && data != null) { head.setRotX(data.headPitch() * Mth.DEG_TO_RAD); head.setRotY(data.netHeadYaw() * Mth.DEG_TO_RAD); }
        }
    }
    private static final class Renderer extends GeoEntityRenderer<AnimatedMonsterEntity> {
        private final boolean fixedDeath;
        private Renderer(EntityRendererProvider.Context context, String name, float shadow, boolean fixedDeath) { super(context, new Model(name)); shadowRadius = shadow; this.fixedDeath = fixedDeath; }
        @Override public RenderType getRenderType(AnimatedMonsterEntity entity, ResourceLocation texture, MultiBufferSource buffers, float partialTick) { return RenderType.entityTranslucent(texture); }
        @Override protected float getDeathMaxRotation(AnimatedMonsterEntity entity) { return fixedDeath ? 0 : super.getDeathMaxRotation(entity); }
    }
}
