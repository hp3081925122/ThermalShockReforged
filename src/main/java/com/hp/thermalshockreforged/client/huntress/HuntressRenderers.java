package com.hp.thermalshockreforged.client.huntress;

import com.hp.thermalshockreforged.entity.huntress.*;
import com.hp.thermalshockreforged.content.npc.huntress.HuntressEntity;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public final class HuntressRenderers {
    public static void register(EntityRenderersEvent.RegisterRenderers event) { event.registerEntityRenderer(HuntressContent.NPC.get(), NpcRenderer::new); event.registerEntityRenderer(HuntressContent.BOSS.get(), BossRenderer::new); event.registerEntityRenderer(HuntressContent.TASK.get(), net.minecraft.client.renderer.entity.NoopRenderer::new); }
    private static final class Model<T extends LivingEntity & GeoEntity> extends GeoModel<T> {
        @Override public ResourceLocation getModelResource(T entity) { return HuntressContent.id("geo/huntress.geo.json"); }
        @Override public ResourceLocation getAnimationResource(T entity) { return HuntressContent.id("animations/huntress.animation.json"); }
        @Override public ResourceLocation getTextureResource(T entity) { return HuntressContent.id("textures/entities/" + (entity instanceof HuntressBossEntity boss ? boss.texture() : ((HuntressEntity)entity).texture()) + ".png"); }
        @Override public void setCustomAnimations(T entity, long instance, AnimationState<T> state) { super.setCustomAnimations(entity, instance, state); var head = getAnimationProcessor().getBone("head"); var data = state.getData(DataTickets.ENTITY_MODEL_DATA); if (head != null && data != null) { head.setRotX(data.headPitch() * Mth.DEG_TO_RAD); head.setRotY(data.netHeadYaw() * Mth.DEG_TO_RAD); } }
    }
    private static final class NpcRenderer extends GeoEntityRenderer<HuntressEntity> {
        private NpcRenderer(EntityRendererProvider.Context context) { super(context, new Model<>()); shadowRadius = 1; }
        @Override public RenderType getRenderType(HuntressEntity entity, ResourceLocation texture, MultiBufferSource buffers, float partialTick) { return RenderType.entityTranslucent(texture); }
    }
    private static final class BossRenderer extends GeoEntityRenderer<HuntressBossEntity> {
        private BossRenderer(EntityRendererProvider.Context context) { super(context, new Model<>()); shadowRadius = 1; }
        @Override public RenderType getRenderType(HuntressBossEntity entity, ResourceLocation texture, MultiBufferSource buffers, float partialTick) { return RenderType.entityTranslucent(texture); }
    }
    private HuntressRenderers() { }
}
