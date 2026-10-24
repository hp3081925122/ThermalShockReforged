package com.hp.thermalshockreforged.client.whisper;
import com.hp.thermalshockreforged.client.geo.*;
import com.geckolib.renderer.base.GeoRenderState;
import com.geckolib.renderer.base.BoneSnapshots;
import net.minecraft.client.renderer.entity.state.EntityRenderState;

import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.rendertype.RenderType;

import com.hp.thermalshockreforged.entity.base.AnimatedPathfinderEntity;
import com.hp.thermalshockreforged.entity.whisper.WhisperContent;
import com.hp.thermalshockreforged.entity.liftbug.LiftbugContent;
import com.hp.thermalshockreforged.entity.aerocel.AerocelContent;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import com.geckolib.animation.state.AnimationTest;

public final class PassiveTrioRenderers {
    public static void register(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(WhisperContent.WHISPER.get(), context -> new Renderer<>(context, "whisper", .5F, true));
        event.registerEntityRenderer(LiftbugContent.LIFTBUG.get(), context -> new Renderer<>(context, "liftbug", 2.5F, false));
        event.registerEntityRenderer(AerocelContent.AEROCEL.get(), context -> new Renderer<>(context, "cloudercel", 3, false));
    }
    private static final class Renderer<T extends AnimatedPathfinderEntity> extends TsrGeoEntityRenderer<T> {
        Renderer(EntityRendererProvider.Context context, String id, float shadow, boolean head) { super(context, new Model<>(id, head)); shadowRadius = shadow; withScale(1); }
        @Override protected RenderType visibleRenderType(EntityRenderState state, Identifier texture) { return RenderTypes.entityTranslucent(texture); }
    }
    private static final class Model<T extends AnimatedPathfinderEntity> extends TsrGeoModel<T> {
        private final String id;
        private final boolean head;
        Model(String id, boolean head) { this.id = id; this.head = head; }
        @Override public Identifier getModelResource(T entity) { return WhisperContent.id("geo/" + id + ".geo.json"); }
        @Override public Identifier getAnimationResource(T entity) { return WhisperContent.id("animations/" + id + ".animation.json"); }
        @Override public Identifier getTextureResource(T entity) { return WhisperContent.id("textures/entities/" + entity.texture() + ".png"); }
        @Override public void adjustBones(GeoRenderState state, BoneSnapshots bones) { if (!head) return; lookAt(state, bones, "head"); }
    }
    private PassiveTrioRenderers() { }
}
