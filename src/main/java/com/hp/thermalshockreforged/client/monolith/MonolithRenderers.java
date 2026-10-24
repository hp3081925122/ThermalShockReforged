package com.hp.thermalshockreforged.client.monolith;
import com.hp.thermalshockreforged.client.geo.*;
import com.geckolib.renderer.base.GeoRenderState;
import com.geckolib.renderer.base.BoneSnapshots;
import net.minecraft.client.renderer.entity.state.EntityRenderState;

import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.rendertype.RenderType;

import com.hp.thermalshockreforged.entity.monolith.*;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import com.geckolib.animation.state.AnimationTest;
import com.geckolib.cache.model.BakedGeoModel;

public final class MonolithRenderers {
    public static void register(EntityRenderersEvent.RegisterRenderers event) { event.registerEntityRenderer(MonolithContent.ACTIVE.get(), ActiveRenderer::new); event.registerEntityRenderer(MonolithContent.DORMANT.get(), DormantRenderer::new); event.registerEntityRenderer(MonolithContent.TASK.get(), net.minecraft.client.renderer.entity.NoopRenderer::new); }
    private static Identifier resource(String path) { return MonolithContent.id(path); }
    private static final class Model<T extends MonolithBaseEntity> extends TsrGeoModel<T> {
        private final boolean headTracking;
        private Model(boolean headTracking) { this.headTracking = headTracking; }
        @Override public Identifier getModelResource(T entity) { return resource("geo/monolith.geo.json"); }
        @Override public Identifier getAnimationResource(T entity) { return resource("animations/monolith.animation.json"); }
        @Override public Identifier getTextureResource(T entity) { return resource("textures/entities/" + entity.texture() + ".png"); }
        @Override public void adjustBones(GeoRenderState state, BoneSnapshots bones) { if (headTracking) lookAt(state, bones, "head"); }
    }
    private static class Renderer<T extends MonolithBaseEntity> extends TsrGeoEntityRenderer<T> {
        private Renderer(EntityRendererProvider.Context context, boolean headTracking) { super(context, new Model<>(headTracking)); shadowRadius = .5F; withScale(1.7F); }
        @Override protected RenderType visibleRenderType(EntityRenderState state, Identifier texture) { return RenderTypes.entityTranslucent(texture); }
    }
    private static final class ActiveRenderer extends Renderer<MonolithEntity> {
        private ActiveRenderer(EntityRendererProvider.Context context) { super(context, true); withRenderLayer(new Glow(this)); }
        protected float getDeathMaxRotation(com.geckolib.renderer.base.GeoRenderState state) { return 0; }
    }
    private static final class DormantRenderer extends Renderer<DormantMonolithEntity> { private DormantRenderer(EntityRendererProvider.Context context) { super(context, false); } }
    private static final class Glow extends GeoEyesLayer<MonolithEntity, Void, EntityRenderState> {
        private Glow(ActiveRenderer renderer) { super(renderer); }
        @Override protected Identifier texture(MonolithEntity entity) { return resource("textures/entities/monolith25glow.png"); }
    }
    private MonolithRenderers() { }
}
