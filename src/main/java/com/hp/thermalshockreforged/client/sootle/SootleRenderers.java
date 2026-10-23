package com.hp.thermalshockreforged.client.sootle;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.rendertype.RenderType;
import com.hp.thermalshockreforged.client.compat.LegacyGeoEntityRenderer;

import com.hp.thermalshockreforged.entity.sootle.*;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.Mob;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.extensions.common.*;
import com.geckolib.animatable.GeoEntity;
import com.geckolib.animation.state.AnimationTest;
import com.geckolib.cache.model.BakedGeoModel;
import com.hp.thermalshockreforged.client.compat.LegacyGeoModel;
import com.geckolib.renderer.*;
import com.hp.thermalshockreforged.client.compat.LegacyGeoRenderLayer;
import java.util.function.Function;

public final class SootleRenderers {
    private SootleRenderers() { }

    public static void register(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(SootleContent.SOOTLE.get(), factory("sootling", SootleEntity::texture, true, 0.5F, 1, true));
        event.registerEntityRenderer(SootleContent.SOOTLE_IDLE.get(), factory("sootling", SootleIdleEntity::texture, false, 0.5F, 1, true));
    }

    private static <T extends Mob & GeoEntity> EntityRendererProvider<T> factory(String id, Function<T, String> texture, boolean head, float shadow, float scale, boolean glow) {
        return context -> new Renderer<>(context, new Model<>(id, texture, head), shadow, scale, glow);
    }

    private static Identifier resource(String path) { return Identifier.fromNamespaceAndPath("thermal_shock_reforged", path); }

    private static final class Model<T extends Mob & GeoEntity> extends LegacyGeoModel<T> {
        private final String id;
        private final Function<T, String> texture;
        private final boolean head;

        private Model(String id, Function<T, String> texture, boolean head) {
            this.id = id;
            this.texture = texture;
            this.head = head;
        }

        @Override
        public Identifier getModelResource(T entity) { return resource("geo/" + id + ".geo.json"); }

        @Override
        public Identifier getTextureResource(T entity) { return resource("textures/entities/" + texture.apply(entity) + ".png"); }

        @Override
        public Identifier getAnimationResource(T entity) { return resource("animations/" + id + ".animation.json"); }

        @Override
        public void setCustomAnimations(T entity, long id, AnimationTest<T> state) {
            super.setCustomAnimations(entity, id, state);
            if (!head) return;
            var bone = getAnimationProcessor().getBone("head");
            var data = com.hp.thermalshockreforged.compat.LegacyAnimationData.from(state);
            if (bone != null && data != null) {
                bone.setRotX(data.headPitch() * net.minecraft.util.Mth.DEG_TO_RAD);
                bone.setRotY(data.netHeadYaw() * net.minecraft.util.Mth.DEG_TO_RAD);
            }
        }
    }

    private static final class Renderer<T extends Mob & GeoEntity> extends LegacyGeoEntityRenderer<T> {
        private Renderer(EntityRendererProvider.Context context, LegacyGeoModel<T> model, float shadow, float scale, boolean glow) {
            super(context, model);
            shadowRadius = shadow;
            withScale(scale);
            if (glow) addRenderLayer(new Glow<>(this));
        }

        @Override
        public RenderType getRenderType(T entity, Identifier texture, MultiBufferSource buffers, float partialTick) { return RenderTypes.entityTranslucent(texture); }
    }

    private static final class Glow<T extends Mob & GeoEntity> extends LegacyGeoRenderLayer<T> {


        private Glow(LegacyGeoEntityRenderer<T> renderer) { super(renderer); }

        @Override
        public void render(PoseStack pose, T entity, BakedGeoModel model, RenderType type, MultiBufferSource buffers, VertexConsumer vertices, float partialTick, int light, int overlay) {
            var glow = RenderTypes.eyes(resource("textures/entities/sootleglow.png"));
            getRenderer().reRender(model, pose, buffers, entity, glow, buffers.getBuffer(glow), partialTick, light, OverlayTexture.NO_OVERLAY, -1);
        }
    }
}
