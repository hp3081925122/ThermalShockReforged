package com.hp.thermalshock.client.ecosystem;

import com.hp.thermalshock.entity.ecosystem.*;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Mob;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.extensions.common.*;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.*;
import software.bernie.geckolib.renderer.layer.GeoRenderLayer;
import java.util.function.Function;

public final class EcosystemRenderers {
    private EcosystemRenderers() { }

    public static void register(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(EcosystemContent.CHILLOO.get(), factory("chilloo", ChillooEntity::texture, true, 1, 1, false));
        event.registerEntityRenderer(EcosystemContent.SPRINGLING.get(), factory("springy", SpringlingEntity::texture, false, 0.3F, 1, false));
        event.registerEntityRenderer(EcosystemContent.RAINCATCHER.get(), factory("raincatcher", RaincatcherEntity::texture, true, 0.5F, 1, false));
    }

    private static <T extends Mob & GeoEntity> EntityRendererProvider<T> factory(String id, Function<T, String> texture, boolean head, float shadow, float scale, boolean glow) {
        return context -> new Renderer<>(context, new Model<>(id, texture, head), shadow, scale, glow);
    }

    private static ResourceLocation resource(String path) { return ResourceLocation.fromNamespaceAndPath("thermal_shock", path); }

    private static final class Model<T extends Mob & GeoEntity> extends GeoModel<T> {
        private final String id;
        private final Function<T, String> texture;
        private final boolean head;

        private Model(String id, Function<T, String> texture, boolean head) {
            this.id = id;
            this.texture = texture;
            this.head = head;
        }

        @Override
        public ResourceLocation getModelResource(T entity) { return resource("geo/" + id + ".geo.json"); }

        @Override
        public ResourceLocation getTextureResource(T entity) { return resource("textures/entities/" + texture.apply(entity) + ".png"); }

        @Override
        public ResourceLocation getAnimationResource(T entity) { return resource("animations/" + id + ".animation.json"); }

        @Override
        public void setCustomAnimations(T entity, long id, AnimationState<T> state) {
            super.setCustomAnimations(entity, id, state);
            if (!head) return;
            var bone = getAnimationProcessor().getBone("head");
            var data = state.getData(DataTickets.ENTITY_MODEL_DATA);
            if (bone != null && data != null) {
                bone.setRotX(data.headPitch() * net.minecraft.util.Mth.DEG_TO_RAD);
                bone.setRotY(data.netHeadYaw() * net.minecraft.util.Mth.DEG_TO_RAD);
            }
        }
    }

    private static final class Renderer<T extends Mob & GeoEntity> extends GeoEntityRenderer<T> {
        private Renderer(EntityRendererProvider.Context context, GeoModel<T> model, float shadow, float scale, boolean glow) {
            super(context, model);
            shadowRadius = shadow;
            withScale(scale);
        }

        @Override
        public RenderType getRenderType(T entity, ResourceLocation texture, MultiBufferSource buffers, float partialTick) { return RenderType.entityTranslucent(texture); }
    }

}
