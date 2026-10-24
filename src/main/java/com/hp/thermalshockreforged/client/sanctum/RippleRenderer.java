package com.hp.thermalshockreforged.client.sanctum;
import com.hp.thermalshockreforged.client.geo.*;
import com.geckolib.renderer.base.GeoRenderState;
import com.geckolib.renderer.base.BoneSnapshots;
import net.minecraft.client.renderer.entity.state.EntityRenderState;

import net.minecraft.client.renderer.rendertype.RenderTypes;

import com.hp.thermalshockreforged.ThermalShockReforged;
import com.hp.thermalshockreforged.entity.sanctum.RippleEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;
import com.geckolib.cache.model.BakedGeoModel;

public final class RippleRenderer extends TsrGeoEntityRenderer<RippleEntity> {
    public RippleRenderer(EntityRendererProvider.Context context) {
        super(context, new Model()); shadowRadius = 0.5F; withScale(1); withRenderLayer(new Glow(this));
    }
    private static Identifier resource(String path) { return Identifier.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, path); }
    @Override protected RenderType visibleRenderType(EntityRenderState state, Identifier texture) { return RenderTypes.entityTranslucent(texture); }
    private static final class Model extends TsrGeoModel<RippleEntity> {
        @Override public Identifier getModelResource(RippleEntity entity) { return resource("geo/trojan.geo.json"); }
        @Override public Identifier getAnimationResource(RippleEntity entity) { return resource("animations/trojan.animation.json"); }
        @Override public Identifier getTextureResource(RippleEntity entity) { return resource("textures/entities/" + entity.texture() + ".png"); }

    }
    private static final class Glow extends GeoEyesLayer<RippleEntity, Void, EntityRenderState> {
        private static final Identifier TEXTURE = resource("textures/entities/ripple_glow.png");
        private Glow(RippleRenderer renderer) { super(renderer); }
        @Override protected Identifier texture(RippleEntity entity) { return TEXTURE; }
    }
}
