package com.hp.thermalshockreforged.client.weapons;
import net.minecraft.client.renderer.rendertype.RenderTypes;

import com.hp.thermalshockreforged.ThermalShockReforged;
import com.hp.thermalshockreforged.entity.weaponcore.FlareGunCoreEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.Identifier;
import com.hp.thermalshockreforged.client.compat.LegacyGeoModel;
import com.hp.thermalshockreforged.client.compat.LegacyGeoEntityRenderer;

public final class FlareGunCoreRenderer extends LegacyGeoEntityRenderer<FlareGunCoreEntity> {
    public FlareGunCoreRenderer(EntityRendererProvider.Context context) { super(context, new Model()); shadowRadius = .5F; }
    @Override public RenderType getRenderType(FlareGunCoreEntity entity, Identifier texture, MultiBufferSource buffers, float partialTick) { return RenderTypes.entityTranslucent(texture); }
    private static Identifier resource(String path) { return Identifier.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, path); }
    private static final class Model extends LegacyGeoModel<FlareGunCoreEntity> {
        @Override public Identifier getModelResource(FlareGunCoreEntity entity) { return resource("geo/flare_core.geo.json"); }
        @Override public Identifier getTextureResource(FlareGunCoreEntity entity) { return resource("textures/entities/" + entity.texture() + ".png"); }
        @Override public Identifier getAnimationResource(FlareGunCoreEntity entity) { return resource("animations/flare_core.animation.json"); }
    }
}
