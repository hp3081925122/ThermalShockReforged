package com.hp.thermalshockreforged.client.protoi;
import com.hp.thermalshockreforged.client.geo.*;
import com.geckolib.renderer.base.GeoRenderState;
import com.geckolib.renderer.base.BoneSnapshots;
import net.minecraft.client.renderer.entity.state.EntityRenderState;

import net.minecraft.client.renderer.rendertype.RenderTypes;

import com.hp.thermalshockreforged.ThermalShockReforged;
import com.hp.thermalshockreforged.entity.protoi.*;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.minecraft.resources.Identifier;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.util.Mth;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import com.geckolib.cache.model.BakedGeoModel;
import com.geckolib.animation.state.AnimationTest;
import com.hp.thermalshockreforged.entity.magnetite.MagnetiteContent;
import com.hp.thermalshockreforged.client.legacy.LegacyMonsterRenderers;

public final class ProtoiRenderers {
    private ProtoiRenderers() { }
    public static void register(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(ProtoiContent.GAMMA.get(), context -> new Renderer(context, "protoi_gamma", 1, .3F));
        event.registerEntityRenderer(ProtoiContent.DELTA.get(), context -> new Renderer(context, "protoi_delta", 1, .8F));
        event.registerEntityRenderer(ProtoiContent.EPSILON.get(), ProtoiEpsilonRenderer::new);
        event.registerEntityRenderer(ProtoiContent.EPSILON_ATTACK.get(), net.minecraft.client.renderer.entity.NoopRenderer::new);
        event.registerEntityRenderer(ProtoiContent.DELTA_VOLLEY.get(), net.minecraft.client.renderer.entity.NoopRenderer::new);
        event.registerEntityRenderer(ProtoiContent.SUNBEAM_ATTACK.get(), net.minecraft.client.renderer.entity.NoopRenderer::new);
        event.registerEntityRenderer(ProtoiContent.SUNBEAM.get(), context -> new Renderer(context, "protoi_sunbeam", 1, 1));
        event.registerEntityRenderer(ProtoiContent.SUNBEAM_BULLET.get(), ThrownItemRenderer::new);
        event.registerEntityRenderer(MagnetiteContent.BULLET.get(), ThrownItemRenderer::new);
        LegacyMonsterRenderers.register(event);
    }
    private static final class Model extends TsrGeoModel<ProtoiEntityBase> {
        private final String id;
        private Model(String id) { this.id = id; }
        @Override public Identifier getModelResource(ProtoiEntityBase entity) { return Identifier.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, "geo/" + id + ".geo.json"); }
        @Override public Identifier getTextureResource(ProtoiEntityBase entity) { return Identifier.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, "textures/entities/" + entity.texture() + ".png"); }
        @Override public Identifier getAnimationResource(ProtoiEntityBase entity) { return Identifier.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, "animations/" + id + ".animation.json"); }
        @Override public void adjustBones(GeoRenderState state, BoneSnapshots bones) { if (is(state, ProtoiDeltaEntity.class) || is(state, ProtoiSunbeamEntity.class)) lookAt(state, bones, "head"); }
    }
    private static final class Renderer extends TsrGeoEntityRenderer<ProtoiEntityBase> {
        private Renderer(EntityRendererProvider.Context context, String id, float scale, float shadow) { super(context, new Model(id)); withScale(scale); shadowRadius = shadow; if (id.equals("protoi_sunbeam")) withRenderLayer(new SunbeamGlow(this)); }
        @Override protected RenderType visibleRenderType(EntityRenderState state, Identifier texture) { return TsrGeoModel.is(state, ProtoiGammaEntity.class) || TsrGeoModel.is(state, ProtoiDeltaEntity.class) || TsrGeoModel.is(state, ProtoiSunbeamEntity.class) ? RenderTypes.entityTranslucent(texture) : super.visibleRenderType(state, texture); }
        @Override protected float getDeathMaxRotation(GeoRenderState state) { return TsrGeoModel.is(state, ProtoiSunbeamEntity.class) ? 0 : super.getDeathMaxRotation(state); }
    }
    private static final class SunbeamGlow extends GeoEyesLayer<ProtoiEntityBase, Void, EntityRenderState> {
        private static final Identifier TEXTURE = Identifier.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, "textures/entities/sunbeam_glow.png");
        private SunbeamGlow(Renderer renderer) { super(renderer); }
        @Override protected Identifier texture(ProtoiEntityBase entity) { return TEXTURE; }
    }
}
