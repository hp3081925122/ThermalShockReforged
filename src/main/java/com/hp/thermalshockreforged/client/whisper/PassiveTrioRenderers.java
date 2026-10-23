package com.hp.thermalshockreforged.client.whisper;
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
import com.hp.thermalshockreforged.client.compat.LegacyGeoModel;
import com.hp.thermalshockreforged.client.compat.LegacyGeoEntityRenderer;

public final class PassiveTrioRenderers {
    public static void register(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(WhisperContent.WHISPER.get(), context -> new Renderer<>(context, "whisper", .5F, true));
        event.registerEntityRenderer(LiftbugContent.LIFTBUG.get(), context -> new Renderer<>(context, "liftbug", 2.5F, false));
        event.registerEntityRenderer(AerocelContent.AEROCEL.get(), context -> new Renderer<>(context, "cloudercel", 3, false));
    }
    private static final class Renderer<T extends AnimatedPathfinderEntity> extends LegacyGeoEntityRenderer<T> {
        Renderer(EntityRendererProvider.Context context, String id, float shadow, boolean head) { super(context, new Model<>(id, head)); shadowRadius = shadow; withScale(1); }
        @Override public RenderType getRenderType(T entity, Identifier texture, MultiBufferSource buffers, float partialTick) { return RenderTypes.entityTranslucent(texture); }
    }
    private static final class Model<T extends AnimatedPathfinderEntity> extends LegacyGeoModel<T> {
        private final String id;
        private final boolean head;
        Model(String id, boolean head) { this.id = id; this.head = head; }
        @Override public Identifier getModelResource(T entity) { return WhisperContent.id("geo/" + id + ".geo.json"); }
        @Override public Identifier getAnimationResource(T entity) { return WhisperContent.id("animations/" + id + ".animation.json"); }
        @Override public Identifier getTextureResource(T entity) { return WhisperContent.id("textures/entities/" + entity.texture() + ".png"); }
        @Override public void setCustomAnimations(T entity, long instanceId, AnimationTest<T> state) {
            super.setCustomAnimations(entity, instanceId, state);
            if (!head) return;
            var bone = getAnimationProcessor().getBone("head"); var data = com.hp.thermalshockreforged.compat.LegacyAnimationData.from(state);
            if (bone != null && data != null) { bone.setRotX(data.headPitch() * net.minecraft.util.Mth.DEG_TO_RAD); bone.setRotY(data.netHeadYaw() * net.minecraft.util.Mth.DEG_TO_RAD); }
        }
    }
    private PassiveTrioRenderers() { }
}
