package com.hp.thermalshockreforged.client.growth;

import com.hp.thermalshockreforged.content.growth.*;
import com.hp.thermalshockreforged.entity.growth.*;
import net.minecraft.client.renderer.*;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.extensions.common.*;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.*;

public final class GrowthRenderers {
    public static void register(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(GrowthContent.GEL_ENTITY.get(), context -> new BlockRenderer());
        event.registerEntityRenderer(GrowthEntities.GELSHOOTER.get(), GelshooterRenderer::new);
        event.registerEntityRenderer(GrowthEntities.GELSHOOTER_BULLET.get(), net.minecraft.client.renderer.entity.ThrownItemRenderer::new);
    }

    public static void extensions(RegisterClientExtensionsEvent event) {
        event.registerItem(new IClientItemExtensions() {
            private ItemRenderer renderer;
            @Override public BlockEntityWithoutLevelRenderer getCustomRenderer() {
                if (renderer == null) renderer = new ItemRenderer();
                return renderer;
            }
        }, GrowthContent.GEL_BULB_ITEM.get());
    }

    private static final class BlockRenderer extends GeoBlockRenderer<GelBulbBlockEntity> {
        private BlockRenderer() { super(new Model<>()); }
        @Override public RenderType getRenderType(GelBulbBlockEntity entity, ResourceLocation texture, MultiBufferSource buffers, float partialTick) {
            return RenderType.entityTranslucent(texture);
        }
    }

    private static final class ItemRenderer extends GeoItemRenderer<GelBulbItem> {
        private ItemRenderer() { super(new Model<>()); }
        @Override public RenderType getRenderType(GelBulbItem item, ResourceLocation texture, MultiBufferSource buffers, float partialTick) {
            return RenderType.entityTranslucent(texture);
        }
    }

    private static final class Model<T extends GeoAnimatable> extends GeoModel<T> {
        @Override public ResourceLocation getModelResource(T entity) { return ResourceLocation.fromNamespaceAndPath("thermal_shock_reforged", "geo/gelbulb.geo.json"); }
        @Override public ResourceLocation getTextureResource(T entity) { return ResourceLocation.fromNamespaceAndPath("thermal_shock_reforged", "textures/block/geldrop.png"); }
        @Override public ResourceLocation getAnimationResource(T entity) { return ResourceLocation.fromNamespaceAndPath("thermal_shock_reforged", "animations/gelbulb.animation.json"); }
    }

    private static final class GelshooterRenderer extends GeoEntityRenderer<GelshooterEntity> {
        private GelshooterRenderer(net.minecraft.client.renderer.entity.EntityRendererProvider.Context context) { super(context,new GelshooterModel()); shadowRadius=.2f; }
        @Override public RenderType getRenderType(GelshooterEntity entity,ResourceLocation texture,MultiBufferSource buffers,float partialTick) { return RenderType.entityTranslucent(texture); }
    }
    private static final class GelshooterModel extends GeoModel<GelshooterEntity> {
        @Override public ResourceLocation getModelResource(GelshooterEntity entity) { return ResourceLocation.fromNamespaceAndPath("thermal_shock_reforged","geo/gelshooter.geo.json"); }
        @Override public ResourceLocation getTextureResource(GelshooterEntity entity) { return ResourceLocation.fromNamespaceAndPath("thermal_shock_reforged","textures/entities/"+entity.texture()+".png"); }
        @Override public ResourceLocation getAnimationResource(GelshooterEntity entity) { return ResourceLocation.fromNamespaceAndPath("thermal_shock_reforged","animations/gelshooter.animation.json"); }
        @Override public void setCustomAnimations(GelshooterEntity entity,long id,software.bernie.geckolib.animation.AnimationState<GelshooterEntity> state) {
            var head=getAnimationProcessor().getBone("head");
            if(head!=null) {
                var data=state.getData(software.bernie.geckolib.constant.DataTickets.ENTITY_MODEL_DATA);
                head.setRotX(data.headPitch()*(float)(Math.PI/180));
                head.setRotY(data.netHeadYaw()*(float)(Math.PI/180));
            }
        }
    }
}
