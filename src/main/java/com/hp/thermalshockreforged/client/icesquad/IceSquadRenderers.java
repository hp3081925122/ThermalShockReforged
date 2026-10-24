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

public final class IceSquadRenderers {
    private IceSquadRenderers() { }
    public static void register(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(IceSquadContent.GRUNT.get(), context -> new Renderer(context, "icidrosentinel", "head", 0.9F, 0.5F, "gruntglow"));
        event.registerEntityRenderer(IceSquadContent.STRIDER_ATTACK.get(), ThrownItemRenderer::new);
        event.registerEntityRenderer(IceSquadContent.CRYONIC_PROJECTILE.get(), ThrownItemRenderer::new);
        event.registerEntityRenderer(IceSquadContent.ALT_CRYONIC_PROJECTILE.get(), ThrownItemRenderer::new);
        event.registerEntityRenderer(IceSquadContent.GUNNER_BULLET.get(), ShardRenderer::new);
    }
    private static Identifier resource(String path) { return Identifier.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, path); }
    private static final class Model extends TsrGeoModel<IcidrogruntEntity> {
        private final String id;
        private final String bone;
        private Model(String id, String bone) { this.id = id; this.bone = bone; }
        @Override public Identifier getModelResource(IcidrogruntEntity entity) { return resource("geo/" + id + ".geo.json"); }
        @Override public Identifier getAnimationResource(IcidrogruntEntity entity) { return resource("animations/" + id + ".animation.json"); }
        @Override public Identifier getTextureResource(IcidrogruntEntity entity) { return resource("textures/entities/" + entity.texture() + ".png"); }
        @Override public void adjustBones(GeoRenderState state, BoneSnapshots bones) { if (bone == null) return; lookAt(state, bones, bone); }
    }
    private static final class Renderer extends TsrGeoEntityRenderer<IcidrogruntEntity> {
        private Renderer(EntityRendererProvider.Context context, String id, String bone, float scale, float shadow, String glow) {
            super(context, new Model(id, bone)); withScale(scale); shadowRadius = shadow; if (glow != null) withRenderLayer(new Glow(this, glow));
        }
        @Override protected RenderType visibleRenderType(EntityRenderState state, Identifier texture) { return RenderTypes.entityTranslucent(texture); }
    }
    private static final class Glow extends GeoEyesLayer<IcidrogruntEntity, Void, EntityRenderState> {
        private final Identifier texture;
        private Glow(TsrGeoEntityRenderer<IcidrogruntEntity> renderer, String texture) { super(renderer); this.texture = resource("textures/entities/" + texture + ".png"); }
        @Override protected Identifier texture(IcidrogruntEntity entity) { return texture; }
    }
}
