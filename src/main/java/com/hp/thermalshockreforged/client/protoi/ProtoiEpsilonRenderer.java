package com.hp.thermalshockreforged.client.protoi;
import com.hp.thermalshockreforged.client.geo.*;
import com.geckolib.renderer.base.GeoRenderState;
import com.geckolib.renderer.base.BoneSnapshots;
import net.minecraft.client.renderer.entity.state.EntityRenderState;

import net.minecraft.client.renderer.rendertype.RenderTypes;

import com.hp.thermalshockreforged.ThermalShockReforged;
import com.hp.thermalshockreforged.entity.protoi.ProtoiEpsilonEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import com.geckolib.animation.state.AnimationTest;
import com.geckolib.cache.model.BakedGeoModel;

public final class ProtoiEpsilonRenderer extends TsrGeoEntityRenderer<ProtoiEpsilonEntity> {
    public ProtoiEpsilonRenderer(EntityRendererProvider.Context context) { super(context, new Model()); shadowRadius = 1; withRenderLayer(new Glow(this)); }
    private static Identifier resource(String path) { return Identifier.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, path); }
    @Override protected RenderType visibleRenderType(EntityRenderState state, Identifier texture) { return RenderTypes.entityTranslucent(texture); }
    private static final class Model extends TsrGeoModel<ProtoiEpsilonEntity> {
        @Override public Identifier getModelResource(ProtoiEpsilonEntity entity) { return resource("geo/protoi_epsilon.geo.json"); }
        @Override public Identifier getTextureResource(ProtoiEpsilonEntity entity) { return resource("textures/entities/" + entity.texture() + ".png"); }
        @Override public Identifier getAnimationResource(ProtoiEpsilonEntity entity) { return resource("animations/protoi_epsilon.animation.json"); }
        @Override public void adjustBones(GeoRenderState state, BoneSnapshots bones) { lookAt(state, bones, "body"); }
    }
    private static final class Glow extends GeoEyesLayer<ProtoiEpsilonEntity, Void, EntityRenderState> {
        private Glow(ProtoiEpsilonRenderer renderer) { super(renderer); }
        @Override protected Identifier texture(ProtoiEpsilonEntity entity) { return resource("textures/entities/epsilon_glow.png"); }
    }
}
