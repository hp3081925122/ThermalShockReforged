package com.hp.thermalshockreforged.client.weapons;

import com.hp.thermalshockreforged.ThermalShockReforged;
import com.hp.thermalshockreforged.entity.weaponcore.FlareGunCoreEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public final class FlareGunCoreRenderer extends GeoEntityRenderer<FlareGunCoreEntity> {
    public FlareGunCoreRenderer(EntityRendererProvider.Context context) { super(context, new Model()); shadowRadius = .5F; }
    @Override public RenderType getRenderType(FlareGunCoreEntity entity, ResourceLocation texture, MultiBufferSource buffers, float partialTick) { return RenderType.entityTranslucent(texture); }
    private static ResourceLocation resource(String path) { return ResourceLocation.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, path); }
    private static final class Model extends GeoModel<FlareGunCoreEntity> {
        @Override public ResourceLocation getModelResource(FlareGunCoreEntity entity) { return resource("geo/flare_core.geo.json"); }
        @Override public ResourceLocation getTextureResource(FlareGunCoreEntity entity) { return resource("textures/entities/" + entity.texture() + ".png"); }
        @Override public ResourceLocation getAnimationResource(FlareGunCoreEntity entity) { return resource("animations/flare_core.animation.json"); }
    }
}
