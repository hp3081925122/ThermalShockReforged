package com.hp.thermalshockreforged.client.ice;
import com.hp.thermalshockreforged.client.geo.*;
import com.geckolib.renderer.base.GeoRenderState;
import com.geckolib.renderer.base.BoneSnapshots;
import net.minecraft.client.renderer.entity.state.EntityRenderState;

import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.rendertype.RenderType;

import com.hp.thermalshockreforged.ThermalShockReforged;
import com.hp.thermalshockreforged.entity.ice.*;
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

public final class IceRenderers {
    private IceRenderers() { }
    public static void register(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(IceContent.FROSTILYTE_SOLDIER.get(), context -> new Renderer(context, "frostilyte_soldier_rubicon", "head", 1.1F, 0.5F, null));
        event.registerEntityRenderer(IceContent.FROSTILYTE_CRYOMANCER.get(), context -> new Renderer(context, "frostilyte_staff", "head", 1, 0.5F, "cryomancerglow"));
        event.registerEntityRenderer(IceContent.ICIDRONE.get(), context -> new Renderer(context, "icidrone_rubicon", "body", 1, 0.5F, null));
        event.registerEntityRenderer(IceContent.ICIDROZERK.get(), context -> new Renderer(context, "novazerk", null, 1, 1, "novazerk_glow"));
        event.registerEntityRenderer(IceContent.ICE_BULLET.get(), ThrownItemRenderer::new);
        event.registerEntityRenderer(IceContent.SOLDIER_COMBO.get(), NoopRenderer::new);
    }
    private static Identifier resource(String path) { return Identifier.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, path); }
    private static final class Model extends TsrGeoModel<IceCreatureEntity> {
        private final String id;
        private final String bone;
        private Model(String id, String bone) { this.id = id; this.bone = bone; }
        @Override public Identifier getModelResource(IceCreatureEntity entity) { return resource("geo/" + id + ".geo.json"); }
        @Override public Identifier getAnimationResource(IceCreatureEntity entity) { return resource("animations/" + id + ".animation.json"); }
        @Override public Identifier getTextureResource(IceCreatureEntity entity) { return resource("textures/entities/" + entity.texture() + ".png"); }
        @Override public void adjustBones(GeoRenderState state, BoneSnapshots bones) { if (bone == null) return; lookAt(state, bones, bone); }
    }
    private static final class Renderer extends TsrGeoEntityRenderer<IceCreatureEntity> {
        private Renderer(EntityRendererProvider.Context context, String id, String bone, float scale, float shadow, String glow) {
            super(context, new Model(id, bone)); withScale(scale); shadowRadius = shadow; if (glow != null) withRenderLayer(new Glow(this, glow));
        }
        @Override protected RenderType visibleRenderType(EntityRenderState state, Identifier texture) { return RenderTypes.entityTranslucent(texture); }
    }
    private static final class Glow extends GeoEyesLayer<IceCreatureEntity, Void, EntityRenderState> {
        private final Identifier texture;
        private Glow(TsrGeoEntityRenderer<IceCreatureEntity> renderer, String texture) { super(renderer); this.texture = resource("textures/entities/" + texture + ".png"); }
        @Override protected Identifier texture(IceCreatureEntity entity) { return texture; }
    }
}
