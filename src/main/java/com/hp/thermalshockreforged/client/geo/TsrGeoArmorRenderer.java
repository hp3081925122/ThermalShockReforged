package com.hp.thermalshockreforged.client.geo;

import com.geckolib.animatable.GeoItem;
import com.geckolib.renderer.GeoArmorRenderer;
import com.geckolib.renderer.base.BoneSnapshots;
import com.geckolib.renderer.base.RenderPassInfo;
import com.hp.thermalshockreforged.compat.TsrDataTickets;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import org.jspecify.annotations.Nullable;

public class TsrGeoArmorRenderer<T extends Item & GeoItem> extends GeoArmorRenderer<T, HumanoidRenderState> {
    private final TsrGeoModel<T> tsrModel;

    public TsrGeoArmorRenderer(TsrGeoModel<T> model) {
        super(model);
        tsrModel = model;
    }

    @Override
    public void addRenderData(T animatable, @Nullable RenderData renderData, HumanoidRenderState state, float partialTick) {
        if (renderData == null) return;
        state.addGeckolibData(TsrDataTickets.ENTITY, renderData.entity());
        state.addGeckolibData(TsrDataTickets.ITEMSTACK, renderData.itemStack());
        state.addGeckolibData(TsrDataTickets.EQUIPMENT_SLOT, renderData.slot());
    }

    @Override
    public void adjustModelBonesForRender(RenderPassInfo<HumanoidRenderState> renderPassInfo, BoneSnapshots snapshots) {
        super.adjustModelBonesForRender(renderPassInfo, snapshots);
        tsrModel.adjustBones(renderPassInfo.renderState(), snapshots);
    }

    @Override
    public @Nullable RenderType getRenderType(HumanoidRenderState state, Identifier texture) {
        return RenderTypes.entityTranslucent(texture);
    }
}
