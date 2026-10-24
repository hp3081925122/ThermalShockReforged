package com.hp.thermalshockreforged.client.geo;

import com.geckolib.animatable.GeoAnimatable;
import com.geckolib.constant.dataticket.DataTicket;
import com.geckolib.renderer.base.GeoRenderState;
import com.geckolib.renderer.base.GeoRenderer;
import com.geckolib.renderer.base.RenderPassInfo;
import com.geckolib.renderer.layer.GeoRenderLayer;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.Nullable;

public class GeoEyesLayer<T extends GeoAnimatable, O, R extends GeoRenderState> extends GeoRenderLayer<T, O, R> {
    private static final AtomicInteger IDS = new AtomicInteger();
    private final DataTicket<Identifier> ticket = DataTicket.create("thermal_shock_reforged:eyes_layer_" + IDS.getAndIncrement(), Identifier.class);
    private final @Nullable Function<T, Identifier> texture;

    public GeoEyesLayer(GeoRenderer<T, O, R> renderer) {
        this(renderer, (Function<T, Identifier>) null);
    }

    public GeoEyesLayer(GeoRenderer<T, O, R> renderer, Identifier texture) {
        this(renderer, animatable -> texture);
    }

    public GeoEyesLayer(GeoRenderer<T, O, R> renderer, @Nullable Function<T, Identifier> texture) {
        super(renderer);
        this.texture = texture;
    }

    protected Identifier texture(T animatable) {
        return texture.apply(animatable);
    }

    protected int light(int packedLight) {
        return packedLight;
    }

    @Override
    public void addRenderData(T animatable, @Nullable O relatedObject, R renderState, float partialTick) {
        renderState.addGeckolibData(ticket, texture(animatable));
    }

    @Override
    public void submitRenderTask(RenderPassInfo<R> info, SubmitNodeCollector tasks) {
        if (!info.willRender()) return;
        Identifier current = info.getGeckolibData(ticket);
        if (current == null) return;
        GeoReRender.submit(info, tasks, RenderTypes.eyes(current), light(info.packedLight()), OverlayTexture.NO_OVERLAY, -1);
    }
}
