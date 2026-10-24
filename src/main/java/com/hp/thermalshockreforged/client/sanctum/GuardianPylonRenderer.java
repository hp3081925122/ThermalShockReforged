package com.hp.thermalshockreforged.client.sanctum;
import com.hp.thermalshockreforged.client.geo.*;
import com.geckolib.renderer.base.GeoRenderState;
import com.geckolib.renderer.base.BoneSnapshots;
import net.minecraft.client.renderer.entity.state.EntityRenderState;

import net.minecraft.client.renderer.rendertype.RenderTypes;

import com.hp.thermalshockreforged.ThermalShockReforged;
import com.hp.thermalshockreforged.entity.sanctum.GuardianPylonEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;
import com.geckolib.cache.model.BakedGeoModel;

public final class GuardianPylonRenderer extends TsrGeoEntityRenderer<GuardianPylonEntity> {
    public GuardianPylonRenderer(EntityRendererProvider.Context context) {
        super(context, new Model()); shadowRadius = 0; withScale(1); withRenderLayer(new Glow(this));
    }
    @Override protected float getDeathMaxRotation(GeoRenderState state) { return 0; }
    private static Identifier resource(String path) { return Identifier.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, path); }
    @Override protected RenderType visibleRenderType(EntityRenderState state, Identifier texture) { return RenderTypes.entityTranslucent(texture); }
    private static final class Model extends TsrGeoModel<GuardianPylonEntity> {
        @Override public Identifier getModelResource(GuardianPylonEntity entity) { return resource("geo/primary_pylon.geo.json"); }
        @Override public Identifier getAnimationResource(GuardianPylonEntity entity) { return resource("animations/primary_pylon.animation.json"); }
        @Override public Identifier getTextureResource(GuardianPylonEntity entity) { return resource("textures/entities/" + entity.texture() + ".png"); }

    }
    private static final class Glow extends GeoEyesLayer<GuardianPylonEntity, Void, EntityRenderState> {
        private static final Identifier TEXTURE = resource("textures/entities/surge_pylon.png");
        private Glow(GuardianPylonRenderer renderer) { super(renderer); }
        @Override protected Identifier texture(GuardianPylonEntity entity) { return TEXTURE; }
    }
}
