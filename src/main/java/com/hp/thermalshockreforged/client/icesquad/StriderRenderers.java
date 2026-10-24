package com.hp.thermalshockreforged.client.icesquad;
import com.hp.thermalshockreforged.client.geo.*;
import com.geckolib.renderer.base.GeoRenderState;
import com.geckolib.renderer.base.BoneSnapshots;
import net.minecraft.client.renderer.entity.state.EntityRenderState;

import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.rendertype.RenderType;

import com.hp.thermalshockreforged.ThermalShockReforged;
import com.hp.thermalshockreforged.entity.icesquad.*;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.entity.*;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import com.geckolib.animation.state.AnimationTest;
import com.geckolib.cache.model.BakedGeoModel;
import com.geckolib.renderer.*;

public final class StriderRenderers {
    private StriderRenderers() { }
    public static void register(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(IceSquadContent.STRIDER.get(), context -> new Renderer(context, "icidrovehicle", null, 1, 1, "striderglow"));
    }
    private static Identifier resource(String path) { return Identifier.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, path); }
    private static final class Model extends TsrGeoModel<IcidrostriderEntity> {
        private final String id;
        private final String bone;
        private Model(String id, String bone) { this.id = id; this.bone = bone; }
        @Override public Identifier getModelResource(IcidrostriderEntity entity) { return resource("geo/" + id + ".geo.json"); }
        @Override public Identifier getAnimationResource(IcidrostriderEntity entity) { return resource("animations/" + id + ".animation.json"); }
        @Override public Identifier getTextureResource(IcidrostriderEntity entity) { return resource("textures/entities/" + entity.texture() + ".png"); }
        @Override public void adjustBones(GeoRenderState state, BoneSnapshots bones) { if (bone == null) return; lookAt(state, bones, bone); }
    }
    private static final class Renderer extends TsrGeoEntityRenderer<IcidrostriderEntity> {
        private Renderer(EntityRendererProvider.Context context, String id, String bone, float scale, float shadow, String glow) {
            super(context, new Model(id, bone)); withScale(scale); shadowRadius = shadow; if (glow != null) withRenderLayer(new Glow(this, glow));
        }
        @Override protected float getDeathMaxRotation(GeoRenderState state) { return 0; }
        @Override protected RenderType visibleRenderType(EntityRenderState state, Identifier texture) { return RenderTypes.entityTranslucent(texture); }
    }
    private static final class Glow extends GeoEyesLayer<IcidrostriderEntity, Void, EntityRenderState> {
        private final Identifier texture;
        private Glow(TsrGeoEntityRenderer<IcidrostriderEntity> renderer, String texture) { super(renderer); this.texture = resource("textures/entities/" + texture + ".png"); }
        @Override protected Identifier texture(IcidrostriderEntity entity) { return texture; }
    }
}
