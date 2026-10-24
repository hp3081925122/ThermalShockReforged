package com.hp.thermalshockreforged.client.legacy;
import com.hp.thermalshockreforged.client.geo.*;
import com.geckolib.renderer.base.GeoRenderState;
import com.geckolib.renderer.base.BoneSnapshots;
import net.minecraft.client.renderer.entity.state.EntityRenderState;

import net.minecraft.client.renderer.rendertype.RenderTypes;

import com.hp.thermalshockreforged.ThermalShockReforged;
import com.hp.thermalshockreforged.entity.base.AnimatedMonsterEntity;
import com.hp.thermalshockreforged.entity.legacy.LegacyContent;
import com.hp.thermalshockreforged.entity.legacy.SolaEntity;
import com.hp.thermalshockreforged.entity.legacy.LilJellyEntity;
import com.hp.thermalshockreforged.entity.magnetite.MagnetiteContent;
import com.hp.thermalshockreforged.entity.magnetite.MagnetiteEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import com.geckolib.animation.state.AnimationTest;
import com.geckolib.cache.model.BakedGeoModel;

public final class LegacyMonsterRenderers {
    private LegacyMonsterRenderers() { }
    public static void register(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(LegacyContent.SOLA.get(), context -> new Renderer(context, "sola", .3F, "sola"));
        event.registerEntityRenderer(MagnetiteContent.MAGNETITE.get(), context -> new Renderer(context, "magnetite", .5F, "magnetite_glow"));
        event.registerEntityRenderer(LegacyContent.SPECTRE.get(), context -> new Renderer(context, "specter", .5F, "specter"));
        event.registerEntityRenderer(LegacyContent.LIL_JELLY.get(), context -> new Renderer(context, "liljelly", .2F, null));
    }

    private static final class Model extends TsrGeoModel<AnimatedMonsterEntity> {
        private final Identifier model;
        private final Identifier animation;
        private Model(String id) {
            model = Identifier.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, "geo/" + id + ".geo.json");
            animation = Identifier.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, "animations/" + id + ".animation.json");
        }
        @Override public Identifier getModelResource(AnimatedMonsterEntity entity) { return model; }
        @Override public Identifier getTextureResource(AnimatedMonsterEntity entity) { return Identifier.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, "textures/entities/" + entity.texture() + ".png"); }
        @Override public Identifier getAnimationResource(AnimatedMonsterEntity entity) { return animation; }
        @Override public void adjustBones(GeoRenderState state, BoneSnapshots bones) { if (is(state, MagnetiteEntity.class) || is(state, LilJellyEntity.class)) lookAt(state, bones, "head"); }
    }

    private static final class Renderer extends TsrGeoEntityRenderer<AnimatedMonsterEntity> {
        private Renderer(EntityRendererProvider.Context context, String id, float shadow, String glow) {
            super(context, new Model(id));
            shadowRadius = shadow;
            if (glow != null) withRenderLayer(new Glow(this, glow));
        }
        @Override protected RenderType visibleRenderType(EntityRenderState state, Identifier texture) { return RenderTypes.entityTranslucent(texture); }
        @Override protected float renderScale(AnimatedMonsterEntity entity) { return entity instanceof SolaEntity sola ? sola.visualScale() : 1; }
    }

    private static final class Glow extends GeoEyesLayer<AnimatedMonsterEntity, Void, EntityRenderState> {
        private final Identifier texture;
        private Glow(Renderer renderer, String id) { super(renderer); texture = Identifier.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, "textures/entities/" + id + ".png"); }
        @Override protected Identifier texture(AnimatedMonsterEntity entity) { return texture; }
    }
}
