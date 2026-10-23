package com.hp.thermalshockreforged.client.growth;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.rendertype.RenderType;
import com.geckolib.animation.state.AnimationTest;
import com.hp.thermalshockreforged.client.compat.LegacyGeoEntityRenderer;
import com.hp.thermalshockreforged.client.compat.LegacyGeoBlockRenderer;
import com.hp.thermalshockreforged.client.compat.LegacyGeoItemRenderer;

import com.hp.thermalshockreforged.content.growth.*;
import com.hp.thermalshockreforged.entity.growth.*;
import net.minecraft.client.renderer.*;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.extensions.common.*;
import com.geckolib.animatable.GeoAnimatable;
import com.hp.thermalshockreforged.client.compat.LegacyGeoModel;
import com.geckolib.renderer.*;

public final class GrowthRenderers {
    public static void register(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(GrowthContent.GEL_ENTITY.get(), context -> new BlockRenderer());
        event.registerEntityRenderer(GrowthEntities.GELSHOOTER.get(), GelshooterRenderer::new);
        event.registerEntityRenderer(GrowthEntities.GELSHOOTER_BULLET.get(), net.minecraft.client.renderer.entity.ThrownItemRenderer::new);
    }

    public static void extensions(RegisterClientExtensionsEvent event) {
        event.registerItem(new IClientItemExtensions() {
            private ItemRenderer renderer;

        }, GrowthContent.GEL_BULB_ITEM.get());
    }

    private static final class BlockRenderer extends LegacyGeoBlockRenderer<GelBulbBlockEntity> {
        private BlockRenderer() { super(new Model<>()); }
        @Override public RenderType getRenderType(GelBulbBlockEntity entity, Identifier texture, MultiBufferSource buffers, float partialTick) {
            return RenderTypes.entityTranslucent(texture);
        }
    }

    private static final class ItemRenderer extends LegacyGeoItemRenderer<GelBulbItem> {
        private ItemRenderer() { super(new Model<>()); }
        @Override public RenderType getRenderType(GelBulbItem item, Identifier texture, MultiBufferSource buffers, float partialTick) {
            return RenderTypes.entityTranslucent(texture);
        }
    }

    private static final class Model<T extends GeoAnimatable> extends LegacyGeoModel<T> {
        @Override public Identifier getModelResource(T entity) { return Identifier.fromNamespaceAndPath("thermal_shock_reforged", "geo/gelbulb.geo.json"); }
        @Override public Identifier getTextureResource(T entity) { return Identifier.fromNamespaceAndPath("thermal_shock_reforged", "textures/block/geldrop.png"); }
        @Override public Identifier getAnimationResource(T entity) { return Identifier.fromNamespaceAndPath("thermal_shock_reforged", "animations/gelbulb.animation.json"); }
    }

    private static final class GelshooterRenderer extends LegacyGeoEntityRenderer<GelshooterEntity> {
        private GelshooterRenderer(net.minecraft.client.renderer.entity.EntityRendererProvider.Context context) { super(context,new GelshooterModel()); shadowRadius=.2f; }
        @Override public RenderType getRenderType(GelshooterEntity entity,Identifier texture,MultiBufferSource buffers,float partialTick) { return RenderTypes.entityTranslucent(texture); }
    }
    private static final class GelshooterModel extends LegacyGeoModel<GelshooterEntity> {
        @Override public Identifier getModelResource(GelshooterEntity entity) { return Identifier.fromNamespaceAndPath("thermal_shock_reforged","geo/gelshooter.geo.json"); }
        @Override public Identifier getTextureResource(GelshooterEntity entity) { return Identifier.fromNamespaceAndPath("thermal_shock_reforged","textures/entities/"+entity.texture()+".png"); }
        @Override public Identifier getAnimationResource(GelshooterEntity entity) { return Identifier.fromNamespaceAndPath("thermal_shock_reforged","animations/gelshooter.animation.json"); }
        @Override public void setCustomAnimations(GelshooterEntity entity,long id,com.geckolib.animation.state.AnimationTest<GelshooterEntity> state) {
            var head=getAnimationProcessor().getBone("head");
            if(head!=null) {
                var data=state.getData(com.geckolib.constant.DataTickets.ENTITY_MODEL_DATA);
                head.setRotX(data.headPitch()*(float)(Math.PI/180));
                head.setRotY(data.netHeadYaw()*(float)(Math.PI/180));
            }
        }
    }
}
