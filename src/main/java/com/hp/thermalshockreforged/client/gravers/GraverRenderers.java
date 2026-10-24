package com.hp.thermalshockreforged.client.gravers;
import com.hp.thermalshockreforged.client.geo.*;
import com.geckolib.renderer.base.GeoRenderState;
import com.geckolib.renderer.base.BoneSnapshots;
import net.minecraft.client.renderer.entity.state.EntityRenderState;

import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.rendertype.RenderType;

import com.hp.thermalshockreforged.entity.gravers.*;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.entity.*;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import com.geckolib.animation.state.AnimationTest;
import com.geckolib.cache.model.BakedGeoModel;

public final class GraverRenderers {
    public static void register(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(GraverContent.GRAVER.get(), context -> new Renderer<>(context, "chorpse", .5F, null));
        event.registerEntityRenderer(GraverContent.NEON.get(), context -> new Renderer<>(context, "lime_graver", .5F, "neon_lime_graver"));
        event.registerEntityRenderer(GraverContent.ASSIMILATOR.get(), context -> new Renderer<>(context, "infected_snobol", .5F, null));
        event.registerEntityRenderer(GraverContent.ASSIMILATED.get(), context -> new Renderer<>(context, "infected_graver", .5F, null));
        event.registerEntityRenderer(GraverContent.RAZORAPTOR.get(), context -> new Renderer<>(context, "infected_razorwing", .7F, "razoraptroglow"));
        event.registerEntityRenderer(GraverContent.STRIKE.get(), NoopRenderer::new);
    }
    private static final class Model<T extends GraverFamilyEntity> extends TsrGeoModel<T> {
        private final String name;
        private Model(String name) { this.name = name; }
        @Override public Identifier getModelResource(T entity) { return GraverContent.id("geo/" + name + ".geo.json"); }
        @Override public Identifier getAnimationResource(T entity) { return GraverContent.id("animations/" + name + ".animation.json"); }
        @Override public Identifier getTextureResource(T entity) { return GraverContent.id("textures/entities/" + entity.texture() + ".png"); }
        @Override public void adjustBones(GeoRenderState state, BoneSnapshots bones) { lookAt(state, bones, "head"); }
    }
    private static final class Renderer<T extends GraverFamilyEntity> extends TsrGeoEntityRenderer<T> {
        private Renderer(EntityRendererProvider.Context context, String model, float shadow, String glow) { super(context, new Model<>(model)); shadowRadius = shadow; if (glow != null) withRenderLayer(new Glow<>(this, glow)); }
        @Override protected RenderType visibleRenderType(EntityRenderState state, Identifier texture) { return RenderTypes.entityTranslucent(texture); }
    }
    private static final class Glow<T extends GraverFamilyEntity> extends GeoEyesLayer<T, Void, EntityRenderState> {
        private final Identifier texture;
        private Glow(Renderer<T> renderer, String texture) { super(renderer); this.texture = GraverContent.id("textures/entities/" + texture + ".png"); }
        @Override protected Identifier texture(T entity) { return texture; }
    }
    private GraverRenderers() { }
}
