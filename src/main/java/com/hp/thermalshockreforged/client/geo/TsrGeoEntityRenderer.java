package com.hp.thermalshockreforged.client.geo;

import com.geckolib.animatable.GeoAnimatable;
import com.geckolib.constant.dataticket.DataTicket;
import com.geckolib.renderer.GeoEntityRenderer;
import com.geckolib.renderer.base.BoneSnapshots;
import com.geckolib.renderer.base.RenderPassInfo;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.Entity;
import org.jspecify.annotations.Nullable;

public class TsrGeoEntityRenderer<T extends Entity & GeoAnimatable> extends GeoEntityRenderer<T, EntityRenderState> {
    private static final DataTicket<Float> RENDER_SCALE = DataTicket.create("thermal_shock_reforged:render_scale", Float.class);
    private final TsrGeoModel<T> tsrModel;

    public TsrGeoEntityRenderer(EntityRendererProvider.Context context, TsrGeoModel<T> model) {
        super(context, model);
        tsrModel = model;
    }

    protected float renderScale(T animatable) {
        return 1;
    }

    @Override
    public void addRenderData(T animatable, @Nullable Void relatedObject, EntityRenderState state, float partialTick) {
        state.addGeckolibData(RENDER_SCALE, renderScale(animatable));
    }

    @Override
    public void scaleModelForRender(RenderPassInfo<EntityRenderState> renderPassInfo, float widthScale, float heightScale) {
        float scale = renderPassInfo.getOrDefaultGeckolibData(RENDER_SCALE, 1F);
        super.scaleModelForRender(renderPassInfo, widthScale * scale, heightScale * scale);
    }

    @Override
    public void adjustModelBonesForRender(RenderPassInfo<EntityRenderState> renderPassInfo, BoneSnapshots snapshots) {
        tsrModel.adjustBones(renderPassInfo.renderState(), snapshots);
    }

    @Override
    public @Nullable RenderType getRenderType(EntityRenderState state, Identifier texture) {
        return state.isInvisible ? super.getRenderType(state, texture) : visibleRenderType(state, texture);
    }

    protected RenderType visibleRenderType(EntityRenderState state, Identifier texture) {
        return RenderTypes.entityCutout(texture);
    }
}
