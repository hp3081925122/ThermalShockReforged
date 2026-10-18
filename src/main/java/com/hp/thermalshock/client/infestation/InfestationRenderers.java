package com.hp.thermalshock.client.infestation;

import com.hp.thermalshock.entity.infestation.*;
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

public final class InfestationRenderers {
    private InfestationRenderers() { }

    public static void register(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(InfestationEntities.SEED.get(), factory("springy", InfestophageSeedEntity::texture, 0, 1, false));
        event.registerEntityRenderer(InfestationEntities.INFESTOPHAGE.get(), factory("infestor", InfestophageEntity::texture, 0.8F, 1, true));
        event.registerEntityRenderer(InfestationEntities.LOUSE.get(), factory("louse", LouseEntity::texture, 0.1F, 1, false));
    }

    private static <T extends Mob & GeoEntity> EntityRendererProvider<T> factory(String id, Function<T, String> texture, float shadow, float scale, boolean glow) {
        return context -> new Renderer<>(context, new Model<>(id, texture), shadow, scale, glow);
    }

    private static ResourceLocation resource(String path) { return ResourceLocation.fromNamespaceAndPath("thermal_shock", path); }

    private static final class Model<T extends Mob & GeoEntity> extends GeoModel<T> {
        private final String id;
        private final Function<T, String> texture;

        private Model(String id, Function<T, String> texture) {
            this.id = id;
            this.texture = texture;
        }

        @Override
        public ResourceLocation getModelResource(T entity) { return resource("geo/" + id + ".geo.json"); }

        @Override
        public ResourceLocation getTextureResource(T entity) { return resource("textures/entities/" + texture.apply(entity) + ".png"); }

        @Override
        public ResourceLocation getAnimationResource(T entity) { return resource("animations/" + id + ".animation.json"); }


    }

    private static final class Renderer<T extends Mob & GeoEntity> extends GeoEntityRenderer<T> {
        private Renderer(EntityRendererProvider.Context context, GeoModel<T> model, float shadow, float scale, boolean glow) {
            super(context, model);
            shadowRadius = shadow;
            withScale(scale);
            if (glow) addRenderLayer(new Glow<>(this));
        }

        @Override
        public RenderType getRenderType(T entity, ResourceLocation texture, MultiBufferSource buffers, float partialTick) { return RenderType.entityTranslucent(texture); }
    }

    private static final class Glow<T extends Mob & GeoEntity> extends GeoRenderLayer<T> {
        private static final ResourceLocation TEXTURE = resource("textures/entities/infestorglow.png");

        private Glow(GeoRenderer<T> renderer) { super(renderer); }

        @Override
        public void render(PoseStack pose, T entity, BakedGeoModel model, RenderType type, MultiBufferSource buffers, VertexConsumer vertices, float partialTick, int light, int overlay) {
            var glow = RenderType.eyes(TEXTURE);
            getRenderer().reRender(model, pose, buffers, entity, glow, buffers.getBuffer(glow), partialTick, light, OverlayTexture.NO_OVERLAY, -1);
        }
    }
}
