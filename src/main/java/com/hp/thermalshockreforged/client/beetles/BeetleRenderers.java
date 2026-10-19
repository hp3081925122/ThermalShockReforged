package com.hp.thermalshockreforged.client.beetles;

import com.hp.thermalshockreforged.entity.beetles.*;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public final class BeetleRenderers {
    public static void register(EntityRenderersEvent.RegisterRenderers event) { event.registerEntityRenderer(BeetleContent.CRASHER.get(), CrasherRenderer::new); event.registerEntityRenderer(BeetleContent.THRASHER.get(), ThrasherRenderer::new); event.registerEntityRenderer(BeetleContent.DORMANT.get(), DormantRenderer::new); event.registerEntityRenderer(BeetleContent.ATTACK.get(), net.minecraft.client.renderer.entity.NoopRenderer::new); }
    private static ResourceLocation resource(String path) { return BeetleContent.id(path); }
    private static final class CombatModel<T extends BeetleCombatEntity> extends GeoModel<T> {
        private final String model;
        private final boolean headTracking;
        private CombatModel(String model, boolean headTracking) { this.model = model; this.headTracking = headTracking; }
        @Override public ResourceLocation getModelResource(T entity) { return resource("geo/" + model + ".geo.json"); }
        @Override public ResourceLocation getAnimationResource(T entity) { return resource("animations/" + model + ".animation.json"); }
        @Override public ResourceLocation getTextureResource(T entity) { return resource("textures/entities/" + entity.texture() + ".png"); }
        @Override public void setCustomAnimations(T entity, long instance, AnimationState<T> state) { super.setCustomAnimations(entity, instance, state); if (headTracking) { var head = getAnimationProcessor().getBone("head"); var data = state.getData(DataTickets.ENTITY_MODEL_DATA); if (head != null && data != null) { head.setRotX(data.headPitch() * Mth.DEG_TO_RAD); head.setRotY(data.netHeadYaw() * Mth.DEG_TO_RAD); } } }
    }
    private static final class CrasherRenderer extends GeoEntityRenderer<BeetleCrasherEntity> {
        private CrasherRenderer(EntityRendererProvider.Context context) { super(context, new CombatModel<>("beetle_bastion", false)); shadowRadius = 1.5F; }
        @Override public RenderType getRenderType(BeetleCrasherEntity entity, ResourceLocation texture, MultiBufferSource buffers, float partialTick) { return RenderType.entityTranslucent(texture); }
        @Override protected float getDeathMaxRotation(BeetleCrasherEntity entity) { return 0; }
    }
    private static final class ThrasherRenderer extends GeoEntityRenderer<BeetleThrasherEntity> {
        private ThrasherRenderer(EntityRendererProvider.Context context) { super(context, new CombatModel<>("blood_beetle", true)); shadowRadius = .5F; }
        @Override public RenderType getRenderType(BeetleThrasherEntity entity, ResourceLocation texture, MultiBufferSource buffers, float partialTick) { return RenderType.entityTranslucent(texture); }
    }
    private static final class DormantModel extends GeoModel<DormantBeetleCrasherEntity> {
        @Override public ResourceLocation getModelResource(DormantBeetleCrasherEntity entity) { return resource("geo/springy.geo.json"); }
        @Override public ResourceLocation getAnimationResource(DormantBeetleCrasherEntity entity) { return resource("animations/springy.animation.json"); }
        @Override public ResourceLocation getTextureResource(DormantBeetleCrasherEntity entity) { return resource("textures/entities/" + entity.texture() + ".png"); }
    }
    private static final class DormantRenderer extends GeoEntityRenderer<DormantBeetleCrasherEntity> {
        private DormantRenderer(EntityRendererProvider.Context context) { super(context, new DormantModel()); shadowRadius = 0; }
        @Override public RenderType getRenderType(DormantBeetleCrasherEntity entity, ResourceLocation texture, MultiBufferSource buffers, float partialTick) { return RenderType.entityTranslucent(texture); }
    }
    private BeetleRenderers() { }
}
