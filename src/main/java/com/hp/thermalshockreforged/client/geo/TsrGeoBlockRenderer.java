package com.hp.thermalshockreforged.client.geo;

import com.geckolib.animatable.GeoAnimatable;
import com.geckolib.renderer.GeoBlockRenderer;
import com.geckolib.renderer.base.BoneSnapshots;
import com.geckolib.renderer.base.RenderPassInfo;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jspecify.annotations.Nullable;

public class TsrGeoBlockRenderer<T extends BlockEntity & GeoAnimatable> extends GeoBlockRenderer<T, BlockEntityRenderState> {
    private final TsrGeoModel<T> tsrModel;

    public TsrGeoBlockRenderer(BlockEntityRendererProvider.Context context, TsrGeoModel<T> model) {
        super(context, model);
        tsrModel = model;
    }

    @Override
    public void adjustModelBonesForRender(RenderPassInfo<BlockEntityRenderState> renderPassInfo, BoneSnapshots snapshots) {
        tsrModel.adjustBones(renderPassInfo.renderState(), snapshots);
    }

    @Override
    public @Nullable RenderType getRenderType(BlockEntityRenderState state, Identifier texture) {
        return RenderTypes.entityTranslucent(texture);
    }
}
