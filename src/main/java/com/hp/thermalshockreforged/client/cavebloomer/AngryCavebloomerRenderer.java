package com.hp.thermalshockreforged.client.cavebloomer;
import com.hp.thermalshockreforged.client.geo.*;
import com.geckolib.renderer.base.GeoRenderState;
import com.geckolib.renderer.base.BoneSnapshots;
import net.minecraft.client.renderer.entity.state.EntityRenderState;

import net.minecraft.client.renderer.rendertype.RenderTypes;

import com.hp.thermalshockreforged.ThermalShockReforged;
import com.hp.thermalshockreforged.entity.cavebloomer.AngryCavebloomerEntity;
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

public final class AngryCavebloomerRenderer extends TsrGeoEntityRenderer<AngryCavebloomerEntity> {
    public AngryCavebloomerRenderer(EntityRendererProvider.Context context) {
        super(context, new Model()); shadowRadius = 1.2F; withScale(1); withRenderLayer(new Glow(this));
    }
    private static Identifier resource(String path) { return Identifier.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, path); }
    @Override protected RenderType visibleRenderType(EntityRenderState state, Identifier texture) { return RenderTypes.entityTranslucent(texture); }
    @Override protected float getDeathMaxRotation(GeoRenderState state) { return 0; }
    private static final class Model extends TsrGeoModel<AngryCavebloomerEntity> {
        @Override public Identifier getModelResource(AngryCavebloomerEntity entity) { return resource("geo/angry_cavebloomer.geo.json"); }
        @Override public Identifier getAnimationResource(AngryCavebloomerEntity entity) { return resource("animations/angry_cavebloomer.animation.json"); }
        @Override public Identifier getTextureResource(AngryCavebloomerEntity entity) { return resource("textures/entities/" + entity.texture() + ".png"); }
        @Override public void adjustBones(GeoRenderState state, BoneSnapshots bones) { lookAt(state, bones, "head"); }
    }
    private static final class Glow extends GeoEyesLayer<AngryCavebloomerEntity, Void, EntityRenderState> {
        private static final Identifier TEXTURE = resource("textures/entities/cavebloomer_glow.png");
        private Glow(AngryCavebloomerRenderer renderer) { super(renderer); }
        @Override protected Identifier texture(AngryCavebloomerEntity entity) { return TEXTURE; }
    }
}
