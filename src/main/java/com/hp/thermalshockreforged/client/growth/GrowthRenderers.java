package com.hp.thermalshockreforged.client.growth;

import com.geckolib.animatable.GeoAnimatable;
import com.geckolib.renderer.base.BoneSnapshots;
import com.geckolib.renderer.base.GeoRenderState;
import com.hp.thermalshockreforged.client.geo.TsrGeoBlockRenderer;
import com.hp.thermalshockreforged.client.geo.TsrGeoEntityRenderer;
import com.hp.thermalshockreforged.client.geo.TsrGeoItemRenderer;
import com.hp.thermalshockreforged.client.geo.TsrGeoModel;
import com.hp.thermalshockreforged.client.geo.TsrRenderProvider;
import com.hp.thermalshockreforged.compat.GeoRenderProviders;
import com.hp.thermalshockreforged.content.growth.*;
import com.hp.thermalshockreforged.entity.growth.*;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;

public final class GrowthRenderers {
    public static void register(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(GrowthContent.GEL_ENTITY.get(), BlockRenderer::new);
        event.registerEntityRenderer(GrowthEntities.GELSHOOTER.get(), GelshooterRenderer::new);
        event.registerEntityRenderer(GrowthEntities.GELSHOOTER_BULLET.get(), net.minecraft.client.renderer.entity.ThrownItemRenderer::new);
    }

    public static void extensions(RegisterClientExtensionsEvent event) {
        GeoRenderProviders.register(TsrRenderProvider.item(() -> new TsrGeoItemRenderer<GelBulbItem>(new Model<>())), GrowthContent.GEL_BULB_ITEM.get());
    }

    private static final class BlockRenderer extends TsrGeoBlockRenderer<GelBulbBlockEntity> {
        private BlockRenderer(BlockEntityRendererProvider.Context context) { super(context, new Model<>()); }
    }

    private static final class Model<T extends GeoAnimatable> extends TsrGeoModel<T> {
        @Override public Identifier getModelResource(T entity) { return Identifier.fromNamespaceAndPath("thermal_shock_reforged", "geo/gelbulb.geo.json"); }
        @Override public Identifier getTextureResource(T entity) { return Identifier.fromNamespaceAndPath("thermal_shock_reforged", "textures/block/geldrop.png"); }
        @Override public Identifier getAnimationResource(T entity) { return Identifier.fromNamespaceAndPath("thermal_shock_reforged", "animations/gelbulb.animation.json"); }
    }

    private static final class GelshooterRenderer extends TsrGeoEntityRenderer<GelshooterEntity> {
        private GelshooterRenderer(net.minecraft.client.renderer.entity.EntityRendererProvider.Context context) { super(context,new GelshooterModel()); shadowRadius=.2f; }
        @Override protected RenderType visibleRenderType(EntityRenderState state, Identifier texture) { return RenderTypes.entityTranslucent(texture); }
    }
    private static final class GelshooterModel extends TsrGeoModel<GelshooterEntity> {
        @Override public Identifier getModelResource(GelshooterEntity entity) { return Identifier.fromNamespaceAndPath("thermal_shock_reforged","geo/gelshooter.geo.json"); }
        @Override public Identifier getTextureResource(GelshooterEntity entity) { return Identifier.fromNamespaceAndPath("thermal_shock_reforged","textures/entities/"+entity.texture()+".png"); }
        @Override public Identifier getAnimationResource(GelshooterEntity entity) { return Identifier.fromNamespaceAndPath("thermal_shock_reforged","animations/gelshooter.animation.json"); }
        @Override public void adjustBones(GeoRenderState state, BoneSnapshots bones) { lookAt(state, bones, "head"); }
    }
}
