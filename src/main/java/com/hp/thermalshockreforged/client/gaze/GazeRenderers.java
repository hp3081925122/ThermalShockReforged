package com.hp.thermalshockreforged.client.gaze;
import com.hp.thermalshockreforged.client.geo.*;
import com.geckolib.renderer.base.GeoRenderState;
import com.geckolib.renderer.base.BoneSnapshots;
import net.minecraft.client.renderer.entity.state.EntityRenderState;

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
    private static final class Model extends TsrGeoModel<AnimatedMonsterEntity> {
        private final Identifier model;
        private final Identifier animation;
        private Model(String name) {
            model = Identifier.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, "geo/" + name + ".geo.json");
            animation = Identifier.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, "animations/" + name + ".animation.json");
        }
        @Override public Identifier getModelResource(AnimatedMonsterEntity entity) { return model; }
        @Override public Identifier getAnimationResource(AnimatedMonsterEntity entity) { return animation; }
        @Override public Identifier getTextureResource(AnimatedMonsterEntity entity) { return Identifier.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, "textures/entities/" + entity.texture() + ".png"); }
        @Override public void adjustBones(GeoRenderState state, BoneSnapshots bones) { if (!is(state, SpiritEntity.class)) return; lookAt(state, bones, "head"); }
    }
    private static final class Renderer extends TsrGeoEntityRenderer<AnimatedMonsterEntity> {
        private final boolean fixedDeath;
        private Renderer(EntityRendererProvider.Context context, String name, float shadow, boolean fixedDeath) { super(context, new Model(name)); shadowRadius = shadow; this.fixedDeath = fixedDeath; }
        @Override protected RenderType visibleRenderType(EntityRenderState state, Identifier texture) { return RenderTypes.entityTranslucent(texture); }
        @Override protected float getDeathMaxRotation(GeoRenderState state) { return fixedDeath ? 0 : super.getDeathMaxRotation(state); }
    }
}
