package com.hp.thermalshockreforged.client.geo;

import com.geckolib.constant.DataTickets;
import com.geckolib.renderer.base.GeoRenderState;
import com.geckolib.renderer.base.GeoRenderer;
import com.geckolib.renderer.base.RenderPassInfo;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.rendertype.RenderType;

public final class GeoReRender {
    private GeoReRender() { }

    @SuppressWarnings("unchecked")
    public static <R extends GeoRenderState> void submit(RenderPassInfo<R> info, SubmitNodeCollector tasks, RenderType renderType, int light, int overlay, int color) {
        R state = info.renderState();
        int oldLight = info.packedLight();
        int oldOverlay = info.packedOverlay();
        int oldColor = info.renderColor();
        state.addGeckolibData(DataTickets.PACKED_LIGHT, light);
        state.addGeckolibData(DataTickets.PACKED_OVERLAY, overlay);
        state.addGeckolibData(DataTickets.RENDER_COLOR, color);
        ((GeoRenderer<?, ?, R>) info.renderer()).submitRenderTasks(info, tasks.order(1), renderType);
        state.addGeckolibData(DataTickets.PACKED_LIGHT, oldLight);
        state.addGeckolibData(DataTickets.PACKED_OVERLAY, oldOverlay);
        state.addGeckolibData(DataTickets.RENDER_COLOR, oldColor);
    }
}
