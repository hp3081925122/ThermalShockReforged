package com.hp.thermalshockreforged.client.beetles;
import com.hp.thermalshockreforged.client.geo.*;
import com.geckolib.renderer.base.GeoRenderState;
import com.geckolib.renderer.base.BoneSnapshots;
import net.minecraft.client.renderer.entity.state.EntityRenderState;

import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.rendertype.RenderType;

import com.hp.thermalshockreforged.entity.beetles.*;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import com.geckolib.animation.state.AnimationTest;

public final class BeetleRenderers {
    public static void register(EntityRenderersEvent.RegisterRenderers event) { event.registerEntityRenderer(BeetleContent.CRASHER.get(), CrasherRenderer::new); event.registerEntityRenderer(BeetleContent.THRASHER.get(), ThrasherRenderer::new); event.registerEntityRenderer(BeetleContent.DORMANT.get(), DormantRenderer::new); event.registerEntityRenderer(BeetleContent.ATTACK.get(), net.minecraft.client.renderer.entity.NoopRenderer::new); }
    private static Identifier resource(String path) { return BeetleContent.id(path); }
    private static final class CombatModel<T extends BeetleCombatEntity> extends TsrGeoModel<T> {
        private final String model;
        private final boolean headTracking;
        private CombatModel(String model, boolean headTracking) { this.model = model; this.headTracking = headTracking; }
        @Override public Identifier getModelResource(T entity) { return resource("geo/" + model + ".geo.json"); }
        @Override public Identifier getAnimationResource(T entity) { return resource("animations/" + model + ".animation.json"); }
        @Override public Identifier getTextureResource(T entity) { return resource("textures/entities/" + entity.texture() + ".png"); }
        @Override public void adjustBones(GeoRenderState state, BoneSnapshots bones) { if (headTracking) lookAt(state, bones, "head"); }
    }
    private static final class CrasherRenderer extends TsrGeoEntityRenderer<BeetleCrasherEntity> {
        private CrasherRenderer(EntityRendererProvider.Context context) { super(context, new CombatModel<>("beetle_bastion", false)); shadowRadius = 1.5F; }
        @Override protected RenderType visibleRenderType(EntityRenderState state, Identifier texture) { return RenderTypes.entityTranslucent(texture); }
        @Override protected float getDeathMaxRotation(GeoRenderState state) { return 0; }
    }
    private static final class ThrasherRenderer extends TsrGeoEntityRenderer<BeetleThrasherEntity> {
        private ThrasherRenderer(EntityRendererProvider.Context context) { super(context, new CombatModel<>("blood_beetle", true)); shadowRadius = .5F; }
        @Override protected RenderType visibleRenderType(EntityRenderState state, Identifier texture) { return RenderTypes.entityTranslucent(texture); }
    }
    private static final class DormantModel extends TsrGeoModel<DormantBeetleCrasherEntity> {
        @Override public Identifier getModelResource(DormantBeetleCrasherEntity entity) { return resource("geo/springy.geo.json"); }
        @Override public Identifier getAnimationResource(DormantBeetleCrasherEntity entity) { return resource("animations/springy.animation.json"); }
        @Override public Identifier getTextureResource(DormantBeetleCrasherEntity entity) { return resource("textures/entities/" + entity.texture() + ".png"); }
    }
    private static final class DormantRenderer extends TsrGeoEntityRenderer<DormantBeetleCrasherEntity> {
        private DormantRenderer(EntityRendererProvider.Context context) { super(context, new DormantModel()); shadowRadius = 0; }
        @Override protected RenderType visibleRenderType(EntityRenderState state, Identifier texture) { return RenderTypes.entityTranslucent(texture); }
    }
    private BeetleRenderers() { }
}
