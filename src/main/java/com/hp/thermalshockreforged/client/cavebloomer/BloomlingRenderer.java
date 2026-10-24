package com.hp.thermalshockreforged.client.cavebloomer;
import com.hp.thermalshockreforged.client.geo.*;
import com.geckolib.renderer.base.GeoRenderState;
import com.geckolib.renderer.base.BoneSnapshots;
import net.minecraft.client.renderer.entity.state.EntityRenderState;

import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.rendertype.RenderType;

import com.hp.thermalshockreforged.ThermalShockReforged;
import com.hp.thermalshockreforged.entity.cavebloomer.BloomlingEntity;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import com.geckolib.animation.state.AnimationTest;
import com.geckolib.cache.model.BakedGeoModel;

public final class BloomlingRenderer extends TsrGeoEntityRenderer<BloomlingEntity> {
    public BloomlingRenderer(EntityRendererProvider.Context context, boolean dandy) {
        super(context, new Model()); shadowRadius = 0.5F; withScale(dandy ? 0.8F : 1);
        if (!dandy) withRenderLayer(new Glow(this));
    }
    private static Identifier resource(String path) { return Identifier.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, path); }
    @Override protected RenderType visibleRenderType(EntityRenderState state, Identifier texture) { return RenderTypes.entityTranslucent(texture); }
    private static final com.geckolib.constant.dataticket.DataTicket<Boolean> DANDY = com.geckolib.constant.dataticket.DataTicket.create("thermal_shock_reforged:bloomling_dandy", Boolean.class);
    @Override public void addRenderData(BloomlingEntity entity, Void relatedObject, EntityRenderState state, float partialTick) { super.addRenderData(entity, relatedObject, state, partialTick); state.addGeckolibData(DANDY, entity.dandy()); }
    @Override protected float getDeathMaxRotation(GeoRenderState state) { return state.getOrDefaultGeckolibData(DANDY, false) ? super.getDeathMaxRotation(state) : 0; }
    private static final class Model extends TsrGeoModel<BloomlingEntity> {
        @Override public Identifier getModelResource(BloomlingEntity entity) { return resource("geo/" + entity.modelStem() + ".geo.json"); }
        @Override public Identifier getAnimationResource(BloomlingEntity entity) { return resource("animations/" + entity.modelStem() + ".animation.json"); }
        @Override public Identifier getTextureResource(BloomlingEntity entity) { return resource("textures/entities/" + entity.texture() + ".png"); }
        @Override public void adjustBones(GeoRenderState state, BoneSnapshots bones) { lookAt(state, bones, "head"); }
    }
    private static final class Glow extends GeoEyesLayer<BloomlingEntity, Void, EntityRenderState> {
        private static final Identifier TEXTURE = resource("textures/entities/budding_glow.png");
        private Glow(BloomlingRenderer renderer) { super(renderer); }
        @Override protected Identifier texture(BloomlingEntity entity) { return TEXTURE; }
    }
}
