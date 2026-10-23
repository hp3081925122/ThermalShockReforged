package com.hp.thermalshockreforged.client.beetles;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.rendertype.RenderType;

import com.hp.thermalshockreforged.entity.beetles.*;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import com.geckolib.animation.state.AnimationTest;
import com.hp.thermalshockreforged.client.compat.LegacyGeoModel;
import com.hp.thermalshockreforged.client.compat.LegacyGeoEntityRenderer;

public final class BeetleRenderers {
    public static void register(EntityRenderersEvent.RegisterRenderers event) { event.registerEntityRenderer(BeetleContent.CRASHER.get(), CrasherRenderer::new); event.registerEntityRenderer(BeetleContent.THRASHER.get(), ThrasherRenderer::new); event.registerEntityRenderer(BeetleContent.DORMANT.get(), DormantRenderer::new); event.registerEntityRenderer(BeetleContent.ATTACK.get(), net.minecraft.client.renderer.entity.NoopRenderer::new); }
    private static Identifier resource(String path) { return BeetleContent.id(path); }
    private static final class CombatModel<T extends BeetleCombatEntity> extends LegacyGeoModel<T> {
        private final String model;
        private final boolean headTracking;
        private CombatModel(String model, boolean headTracking) { this.model = model; this.headTracking = headTracking; }
        @Override public Identifier getModelResource(T entity) { return resource("geo/" + model + ".geo.json"); }
        @Override public Identifier getAnimationResource(T entity) { return resource("animations/" + model + ".animation.json"); }
        @Override public Identifier getTextureResource(T entity) { return resource("textures/entities/" + entity.texture() + ".png"); }
        @Override public void setCustomAnimations(T entity, long instance, AnimationTest<T> state) { super.setCustomAnimations(entity, instance, state); if (headTracking) { var head = getAnimationProcessor().getBone("head"); var data = com.hp.thermalshockreforged.compat.LegacyAnimationData.from(state); if (head != null && data != null) { head.setRotX(data.headPitch() * Mth.DEG_TO_RAD); head.setRotY(data.netHeadYaw() * Mth.DEG_TO_RAD); } } }
    }
    private static final class CrasherRenderer extends LegacyGeoEntityRenderer<BeetleCrasherEntity> {
        private CrasherRenderer(EntityRendererProvider.Context context) { super(context, new CombatModel<>("beetle_bastion", false)); shadowRadius = 1.5F; }
        @Override public RenderType getRenderType(BeetleCrasherEntity entity, Identifier texture, MultiBufferSource buffers, float partialTick) { return RenderTypes.entityTranslucent(texture); }
        protected float getDeathMaxRotation(BeetleCrasherEntity entity) { return 0; }
    }
    private static final class ThrasherRenderer extends LegacyGeoEntityRenderer<BeetleThrasherEntity> {
        private ThrasherRenderer(EntityRendererProvider.Context context) { super(context, new CombatModel<>("blood_beetle", true)); shadowRadius = .5F; }
        @Override public RenderType getRenderType(BeetleThrasherEntity entity, Identifier texture, MultiBufferSource buffers, float partialTick) { return RenderTypes.entityTranslucent(texture); }
    }
    private static final class DormantModel extends LegacyGeoModel<DormantBeetleCrasherEntity> {
        @Override public Identifier getModelResource(DormantBeetleCrasherEntity entity) { return resource("geo/springy.geo.json"); }
        @Override public Identifier getAnimationResource(DormantBeetleCrasherEntity entity) { return resource("animations/springy.animation.json"); }
        @Override public Identifier getTextureResource(DormantBeetleCrasherEntity entity) { return resource("textures/entities/" + entity.texture() + ".png"); }
    }
    private static final class DormantRenderer extends LegacyGeoEntityRenderer<DormantBeetleCrasherEntity> {
        private DormantRenderer(EntityRendererProvider.Context context) { super(context, new DormantModel()); shadowRadius = 0; }
        @Override public RenderType getRenderType(DormantBeetleCrasherEntity entity, Identifier texture, MultiBufferSource buffers, float partialTick) { return RenderTypes.entityTranslucent(texture); }
    }
    private BeetleRenderers() { }
}
