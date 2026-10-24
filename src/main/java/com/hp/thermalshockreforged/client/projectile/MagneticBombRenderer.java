package com.hp.thermalshockreforged.client.projectile;
import com.hp.thermalshockreforged.client.geo.*;
import com.geckolib.renderer.base.GeoRenderState;
import com.geckolib.renderer.base.BoneSnapshots;
import net.minecraft.client.renderer.entity.state.EntityRenderState;

import net.minecraft.client.renderer.rendertype.RenderTypes;

import com.hp.thermalshockreforged.ThermalShockReforged;
import com.hp.thermalshockreforged.entity.projectile.MagneticBombEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;
import com.geckolib.cache.model.BakedGeoModel;

public final class MagneticBombRenderer extends TsrGeoEntityRenderer<MagneticBombEntity> {
    public MagneticBombRenderer(EntityRendererProvider.Context context) { super(context, new Model()); shadowRadius = .5F; withRenderLayer(new Glow(this)); }
    private static Identifier resource(String path) { return Identifier.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, path); }
    @Override protected RenderType visibleRenderType(EntityRenderState state, Identifier texture) { return RenderTypes.entityTranslucent(texture); }
    @Override protected float getDeathMaxRotation(GeoRenderState state) { return 0; }
    private static final class Model extends TsrGeoModel<MagneticBombEntity> {
        @Override public Identifier getModelResource(MagneticBombEntity entity) { return resource("geo/magnetic_bomb.geo.json"); }
        @Override public Identifier getTextureResource(MagneticBombEntity entity) { return resource("textures/entities/" + entity.texture() + ".png"); }
        @Override public Identifier getAnimationResource(MagneticBombEntity entity) { return resource("animations/magnetic_bomb.animation.json"); }
    }
    private static final class Glow extends GeoEyesLayer<MagneticBombEntity, Void, EntityRenderState> {
        private Glow(MagneticBombRenderer renderer) { super(renderer); }
        @Override protected Identifier texture(MagneticBombEntity entity) { return resource("textures/entities/" + (entity.epsilon() ? "epsilon_bomb" : "magnetic_bomb_glow") + ".png"); }
    }
}
