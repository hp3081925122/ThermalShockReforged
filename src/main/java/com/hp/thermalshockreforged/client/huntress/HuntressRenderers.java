package com.hp.thermalshockreforged.client.huntress;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.rendertype.RenderType;

import com.hp.thermalshockreforged.entity.huntress.*;
import com.hp.thermalshockreforged.content.npc.huntress.HuntressEntity;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import com.geckolib.animatable.GeoEntity;
import com.geckolib.animation.state.AnimationTest;
import com.hp.thermalshockreforged.client.compat.LegacyGeoModel;
import com.hp.thermalshockreforged.client.compat.LegacyGeoEntityRenderer;

public final class HuntressRenderers {
    public static void register(EntityRenderersEvent.RegisterRenderers event) { event.registerEntityRenderer(HuntressContent.NPC.get(), NpcRenderer::new); event.registerEntityRenderer(HuntressContent.BOSS.get(), BossRenderer::new); event.registerEntityRenderer(HuntressContent.TASK.get(), net.minecraft.client.renderer.entity.NoopRenderer::new); }
    private static final class Model<T extends LivingEntity & GeoEntity> extends LegacyGeoModel<T> {
        @Override public Identifier getModelResource(T entity) { return HuntressContent.id("geo/huntress.geo.json"); }
        @Override public Identifier getAnimationResource(T entity) { return HuntressContent.id("animations/huntress.animation.json"); }
        @Override public Identifier getTextureResource(T entity) { return HuntressContent.id("textures/entities/" + (entity instanceof HuntressBossEntity boss ? boss.texture() : ((HuntressEntity)entity).texture()) + ".png"); }
        @Override public void setCustomAnimations(T entity, long instance, AnimationTest<T> state) { super.setCustomAnimations(entity, instance, state); var head = getAnimationProcessor().getBone("head"); var data = com.hp.thermalshockreforged.compat.LegacyAnimationData.from(state); if (head != null && data != null) { head.setRotX(data.headPitch() * Mth.DEG_TO_RAD); head.setRotY(data.netHeadYaw() * Mth.DEG_TO_RAD); } }
    }
    private static final class NpcRenderer extends LegacyGeoEntityRenderer<HuntressEntity> {
        private NpcRenderer(EntityRendererProvider.Context context) { super(context, new Model<>()); shadowRadius = 1; }
        @Override public RenderType getRenderType(HuntressEntity entity, Identifier texture, MultiBufferSource buffers, float partialTick) { return RenderTypes.entityTranslucent(texture); }
    }
    private static final class BossRenderer extends LegacyGeoEntityRenderer<HuntressBossEntity> {
        private BossRenderer(EntityRendererProvider.Context context) { super(context, new Model<>()); shadowRadius = 1; }
        @Override public RenderType getRenderType(HuntressBossEntity entity, Identifier texture, MultiBufferSource buffers, float partialTick) { return RenderTypes.entityTranslucent(texture); }
    }
    private HuntressRenderers() { }
}
