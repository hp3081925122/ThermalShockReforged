package com.hp.thermalshockreforged.client.huntress;
import com.hp.thermalshockreforged.client.geo.*;
import com.geckolib.renderer.base.GeoRenderState;
import com.geckolib.renderer.base.BoneSnapshots;
import net.minecraft.client.renderer.entity.state.EntityRenderState;

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

public final class HuntressRenderers {
    public static void register(EntityRenderersEvent.RegisterRenderers event) { event.registerEntityRenderer(HuntressContent.NPC.get(), NpcRenderer::new); event.registerEntityRenderer(HuntressContent.BOSS.get(), BossRenderer::new); event.registerEntityRenderer(HuntressContent.TASK.get(), net.minecraft.client.renderer.entity.NoopRenderer::new); }
    private static final class Model<T extends LivingEntity & GeoEntity> extends TsrGeoModel<T> {
        @Override public Identifier getModelResource(T entity) { return HuntressContent.id("geo/huntress.geo.json"); }
        @Override public Identifier getAnimationResource(T entity) { return HuntressContent.id("animations/huntress.animation.json"); }
        @Override public Identifier getTextureResource(T entity) { return HuntressContent.id("textures/entities/" + (entity instanceof HuntressBossEntity boss ? boss.texture() : ((HuntressEntity)entity).texture()) + ".png"); }
        @Override public void adjustBones(GeoRenderState state, BoneSnapshots bones) { lookAt(state, bones, "head"); }
    }
    private static final class NpcRenderer extends TsrGeoEntityRenderer<HuntressEntity> {
        private NpcRenderer(EntityRendererProvider.Context context) { super(context, new Model<>()); shadowRadius = 1; }
        @Override protected RenderType visibleRenderType(EntityRenderState state, Identifier texture) { return RenderTypes.entityTranslucent(texture); }
    }
    private static final class BossRenderer extends TsrGeoEntityRenderer<HuntressBossEntity> {
        private BossRenderer(EntityRendererProvider.Context context) { super(context, new Model<>()); shadowRadius = 1; }
        @Override protected RenderType visibleRenderType(EntityRenderState state, Identifier texture) { return RenderTypes.entityTranslucent(texture); }
    }
    private HuntressRenderers() { }
}
