package com.hp.thermalshockreforged.client.geo;

import com.geckolib.animatable.GeoAnimatable;
import com.geckolib.renderer.GeoItemRenderer;
import com.geckolib.renderer.base.BoneSnapshots;
import com.geckolib.renderer.base.GeoRenderState;
import com.geckolib.renderer.base.RenderPassInfo;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import org.jspecify.annotations.Nullable;

public class TsrGeoItemRenderer<T extends Item & GeoAnimatable> extends GeoItemRenderer<T> {
    private final TsrGeoModel<T> tsrModel;

    public TsrGeoItemRenderer(TsrGeoModel<T> model) {
        super(model);
        tsrModel = model;
    }

    @Override
    public void adjustModelBonesForRender(RenderPassInfo<GeoRenderState> renderPassInfo, BoneSnapshots snapshots) {
        tsrModel.adjustBones(renderPassInfo.renderState(), snapshots);
    }

    @Override
    public @Nullable RenderType getRenderType(GeoRenderState state, Identifier texture) {
        return RenderTypes.entityTranslucent(texture);
    }
}
