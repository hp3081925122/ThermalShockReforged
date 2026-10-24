package com.hp.thermalshockreforged.client.geo;

import java.util.function.Function;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.rendertype.RenderSetup;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Util;

public final class TsrRenderTypes {
    private static final Function<Identifier, RenderType> ENTITY_TRANSLUCENT_CULL = Util.memoize(texture -> RenderType.create("thermal_shock_reforged_entity_translucent_cull",
            RenderSetup.builder(RenderPipelines.ENTITY_TRANSLUCENT_CULL)
                    .withTexture("Sampler0", texture)
                    .useLightmap()
                    .useOverlay()
                    .affectsCrumbling()
                    .sortOnUpload()
                    .setOutline(RenderSetup.OutlineProperty.AFFECTS_OUTLINE)
                    .createRenderSetup()));

    private TsrRenderTypes() { }

    public static RenderType entityTranslucentCull(Identifier texture) {
        return ENTITY_TRANSLUCENT_CULL.apply(texture);
    }
}
